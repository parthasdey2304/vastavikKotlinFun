# Realtime Events Contract — Live Online Class Module

> **Status:** Client-side contract (Kotlin app `kotlin-app/` + Web app `webapp/`) that the backend must implement.  
> **Transport:** WebSocket (primary) + REST for persistence; designed to be swappable to WebRTC SFU (LiveKit/Agora) without changing client event names.  
> **Auth:** All REST/WebSocket calls require `Authorization: Bearer <FirebaseIdToken>` + `x-api-key` HMAC (see `BACKEND_CONNECTION.md`). WebSocket URL: `wss://<BASE>/ws/class/{classId}?token=<IdToken>`.

---

## 1. Roles & Permissions

| Role | How assigned | Capabilities |
|------|--------------|--------------|
| **Admin** | Fixed — class host/teacher (`admin_{classId}` or backend `isAdmin` flag) | Full control: disable any feature, grant/revoke screenshare, kick anyone, assign/revoke starCast, start/stop recording |
| **StarCast** | Chosen by Admin, **one at a time** per class. Inherits Admin in-meeting controls **except** cannot kick Admin or override Admin | Can kick students, enable/disable features, start/stop recording, grant/revoke screenshare, send chat, etc. Cannot remove Admin. Name shown with `★ starCast` tag in participants list and chat |
| **Student** | Default | Standard controls only, gated by Admin's feature toggles and screenshare grant |

- Only **one** StarCast at a time; assigning a new one revokes the previous. Admin can revoke/reassign at any time.
- StarCast can kick students, not Admin.
- Screenshare grant and StarCast assignment are separate permissions.

---

## 2. State Model (client)

```ts
type Role = "ADMIN" | "STARCAST" | "STUDENT"
type MediaState = "ON" | "OFF"

interface Participant {
  userId: string
  displayName: string
  role: Role
  micState: MediaState      // OFF by default on join
  cameraState: MediaState   // OFF by default on join
  handRaised: boolean
  isScreenSharing: boolean
  hasScreenSharePermission: boolean // true only for Admin and one granted student
  joinedAt: number
  leftAt?: number
}

interface ClassSession {
  classId: string
  topic: string
  adminId: string
  startTime: number
  endTime?: number
  isLive: boolean
  recording: boolean         // true when recording-in-progress
  disabledFeatures: DisabledFeature[]
}

enum DisabledFeature { MIC, CAMERA, SCREENSHARE, CAPTIONS, CHAT, RAISE_HAND, EMOJI, RECORDING }

interface LiveChatMessage {
  id: string
  senderId: string
  senderName: string
  senderRole: Role
  text: string
  timestamp: number
  replyTo?: ReplyPreview
}
interface ReplyPreview { messageId: string; senderName: string; senderRole: Role; text: string; truncatedText: string }

interface WhiteboardState { elements: WhiteboardElement[]; viewport: Viewport }
interface WhiteboardElement { id: string; type: "PEN"|"ERASER"|"RECTANGLE"|"ELLIPSE"|"LINE"|"ARROW"|"TEXT"; points: Point[]; text: string; color: string; strokeWidth: number; fontSize: number; bounds: Rect }

interface AuditLogEntry { id: string; classId: string; eventType: string; actorId: string; actorRole: Role; targetId?: string; details: string; timestamp: number }
```

- **Mic & camera are OFF by default** on join (client sets `MediaState.OFF`, server enforces).
- Whiteboard is the **default view** on entering a class.

---

## 3. REST Endpoints (suggested)

All under `BASE_URL` (`BuildConfig.BACKEND_BASE_URL` on Android, `NEXT_PUBLIC_BACKEND_URL` on web). Auth headers as above.

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/api/v1/classes` | Create class (Admin). Body `{ topic, scheduledAt?, maxParticipants }` → `{ classId }` |
| `POST` | `/api/v1/classes/{classId}/start` | Start class → triggers `class-started` event + FCM push to students |
| `POST` | `/api/v1/classes/{classId}/end` | End class |
| `GET`  | `/api/v1/classes/{classId}` | Get session info |
| `GET`  | `/api/v1/classes/{classId}/participants` | Roster |
| `GET`  | `/api/v1/classes/{classId}/messages?cursor=&limit=` | Paginated chat |
| `POST` | `/api/v1/classes/{classId}/audit-log` | Server persists audit log (or auto-persisted on each event) |
| `GET`  | `/api/v1/classes/{classId}/whiteboard` | Current whiteboard state |
| `POST` | `/api/v1/classes/{classId}/recording/start` | Start recording |
| `POST` | `/api/v1/classes/{classId}/recording/stop`  | Stop recording |
| `GET`  | `/api/v1/recordings/{recordingId}` | Fetch recording artifact (admin only) |

FCM push on `class-started`:
```json
{ "event": "class-started", "classId": "abc123", "topic": "Java Loops — Class 8", "title": "Live class is starting!", "screen": "meeting_lobby", "screenId": "abc123" }
```
Client shows in-app `NotificationBanner` (topic + Join) and system notification; tapping Join → `meeting_lobby/{classId}` (Lobby) → `meeting_inclass/{classId}` (In-class).

---

## 4. WebSocket Events (bidirectional)

**Client → Server** (request) and **Server → Client** (push) share the same `type` names. Where a request needs a response, server sends `{ id, result|error }` correlated by `id`. Server pushes are `{ event: { type, ... } }` broadcast to all participants in the class.

### 4.1 Connection & presence

| type | Direction | Payload | Notes |
|------|-----------|---------|-------|
| `join` | C→S + S→C | `{ classId, participant: Participant }` | Client sends on entering lobby/in-class; server broadcasts to others, persists roster entry, logs audit `join` |
| `leave` | C→S + S→C | `{ classId, userId }` | On explicit leave or disconnect/timeout; server logs `leave` |
| `class-started` | S→C | `{ classId, topic, adminId }` | Broadcast when Admin starts class; drives notification banner |

### 4.2 Media / controls

| type | Payload |
|------|---------|
| `toggle-mic` | `{ classId, userId, enabled: boolean }` |
| `toggle-camera` | `{ classId, userId, enabled: boolean }` |
| `toggle-hand-raise` | `{ classId, userId, raised: boolean }` |
| `toggle-screenshare` | `{ classId, userId, active: boolean }` | Client only sends if `hasScreenSharePermission` or Admin |
| `screenshare-request` | `{ classId, userId }` | Student asks for permission |
| `screenshare-grant` | `{ classId, targetUserId, grantedBy }` | Admin only; **at most one granted student** at a time |
| `screenshare-revoke` | `{ classId, targetUserId, revokedBy }` | Admin only; immediately hides control on that student's device |

**Screenshare restriction (enforced server + client):**

- At most **2** simultaneous screenshares: Admin + one granted student. Server rejects `screenshare-grant` if another student already has permission; client hides Screenshare button unless `isAdmin || hasScreenSharePermission`. Revoke immediately hides the control.
- Layout rule: control bar shows only `[Mic, Video, Screenshare, Cut]`; Screenshare hidden when not eligible, remaining 3 expand to fill space (no gap). Implemented in `MeetingControlBar` (Kotlin) and `ControlBar` (web).

### 4.3 Chat & social

| type | Payload |
|------|---------|
| `chat-message` | `{ classId, message: LiveChatMessage }` |
| `chat-reply` | same as `chat-message` where `message.replyTo` is set | Client renders quoted preview (sender name + truncated text) WhatsApp-style, above composer preview and at top of bubble |
| `emoji-reaction` | `{ classId, userId, emoji: string }` | Present set is agreed out-of-band (see Open Questions) |
| `raise-hand` / `lower-hand` | aliased to `toggle-hand-raise` |  |
| `captions-toggle` | `{ classId, userId, enabled }` | If captions disabled by Admin, toggle is hidden/greyed |

Messages from StarCast include `senderRole: "STARCAST"` and are rendered with `★ starCast` label in roster and chat.

### 4.4 Moderation & roles

| type | Payload |
|------|---------|
| `kick-participant` | `{ classId, targetUserId, kickedBy }` | Allowed for Admin and StarCast (StarCast cannot kick Admin); kicked client shows “You were removed” and navigates to `home` |
| `assign-starcast` | `{ classId, targetUserId, assignedBy }` | Admin only; revokes any existing StarCast |
| `revoke-starcast` | `{ classId, targetUserId, revokedBy }` | Admin only |
| `feature-toggle` | `{ classId, feature: DisabledFeature, enabled: boolean, toggledBy }` | Admin/StarCast; client hides/greys the control when `enabled == false` (not just non-functional) |

### 4.5 Recording

| type | Payload |
|------|---------|
| `recording-start` | `{ classId, startedBy }` | Only Admin/StarCast; server begins capture |
| `recording-stop`  | `{ classId, stoppedBy }` | Only Admin/StarCast |

- Recording captures **Admin's feed** + the **single granted student's screenshare** if active. Client signals start/stop; server handles storage/stream upload (see §5). All participants show a `REC` indicator while `session.recording == true`.

### 4.6 Whiteboard

| type | Payload |
|------|---------|
| `whiteboard-update` | `{ classId, elements: WhiteboardElement[], updatedBy }` | Debounced, persisted (Firestore or backend store); rendered as NeoBrutalist surface (thick bottom/right border, rounded corners) — ported from `explaino_structura`'s Excalidraw model but restyled |
| `whiteboard-clear` | `{ classId, clearedBy }` |  |

- Whiteboard is the default view alongside video tiles; Excalidraw on web, native `Canvas` on Android with same element model.

### 4.7 Audit log feed

Server should persist an `AuditLogEntry` per state-changing event and expose via REST or a `audit-log` WebSocket stream:

| eventType | When emitted |
|-----------|--------------|
| `join`, `leave` | participant presence |
| `toggle-mic`, `toggle-camera`, `raise-hand` | media |
| `screenshare-grant`, `screenshare-revoke` | permission |
| `kick`, `assign-starcast`, `revoke-starcast` | moderation |
| `recording-start`, `recording-stop` | recording |
| `feature-toggle` | admin feature gate |
| `chat-message` (optional — see Open Questions) | chat |

Each entry: `{ eventType, actorId, actorRole, targetId?, details, timestamp }`. Both Kotlin (`AuditLogEntry`) and web share the same shape.

---

## 5. Recording & Whiteboard Storage Contract

**Recording (client responsibility):** capture/stream Admin's camera/mic track + granted student's `getDisplayMedia` track; signal `recording-start`/`recording-stop`; hand off encoded output (e.g., `MediaRecorder` blob or SFU egress URL) to backend `POST /api/v1/classes/{classId}/recording/start|stop` or WebSocket `recording-start|stop` with server doing S3/Firestore Storage upload. Client never stores recordings locally long-term.

**Whiteboard (client responsibility):** send `WhiteboardElement[]` diffs via `whiteboard-update` (debounced, local-first backup in `localStorage`/Room if offline). Server persists to `drawings/{classId}` (Firestore) or Postgres JSONB.

---

## 6. WebRTC / SFU Note

The current implementation uses a transport-abstracted `MeetingClient` (`LocalMeetingClient` for mocks, `WebSocketMeetingClient` stub for the real backend). The backend `transport` (plain WebSocket vs LiveKit/Agora SFU) is **swappable**: SFU join/leave/track publish/subscribe map to the same `join`, `toggle-mic`, `toggle-camera`, `toggle-screenshare`, `leave` events; no client event names change. See Open Questions §8.

---

## 7. Example Frames

**Client → Server (join):**
```json
{ "type": "join", "classId": "cls_7a9", "participant": { "userId": "u_123", "displayName": "Riya", "role": "STUDENT", "micState": "OFF", "cameraState": "OFF" } }
```

**Server → Client (screenshare-grant):**
```json
{ "type": "screenshare-grant", "classId": "cls_7a9", "targetUserId": "u_123", "grantedBy": "admin_cls_7a9" }
```

**Chat with reply:**
```json
{ "type": "chat-message", "classId": "cls_7a9", "message": { "id": "m_42", "senderId": "u_123", "senderName": "Arjun", "senderRole": "STARCAST", "text": "The loop ends at 5, not 6", "timestamp": 1725180000000, "replyTo": { "messageId": "m_41", "senderName": "Kiran", "senderRole": "STUDENT", "text": "Why does it print 6 times?", "truncatedText": "Why does it print 6 times?" } } }
```

**Feature toggle (disable chat):**
```json
{ "type": "feature-toggle", "classId": "cls_7a9", "feature": "CHAT", "enabled": false, "toggledBy": "admin_cls_7a9" }
```

---

## 8. Open Questions (flagged for project owner)

1. Emoji reaction set/style (exact glyphs, counts vs ephemeral).
2. Captions: live speech-to-text provider vs placeholder toggle.
3. Recording & audit-log retention (who can view, how long).
4. Whether `chat-message` bodies are part of the persisted audit log or only presence/control events.
5. Backend transport: WebSocket vs WebRTC SFU provider (LiveKit vs Agora vs P2P) — client is abstracted to swap.
6. Web framework preference beyond “generate via Stitch MCP” (current web is Next.js 15 / React 19 / Tailwind; Stitch spec included in `webapp/stitch/`).

---

## 9. Related Code

- Kotlin: `kotlin-app/app/src/main/java/com/vastavik/computer/data/model/MeetingModels.kt`, `data/realtime/MeetingClient.kt`, `data/realtime/LocalMeetingClient.kt`, `ui/components/Whiteboard.kt`, `ui/components/MeetingComponents.kt`, `ui/screens/meeting/MeetingScreens.kt`, `utils/MeetingForegroundService.kt`
- Web: `webapp/app/meeting/lobby/[classId]/page.tsx`, `webapp/app/meeting/inclass/[classId]/page.tsx`, `webapp/components/meeting/Whiteboard.tsx`, `webapp/components/meeting/ControlBar.tsx` (shared event names)
- `BACKEND_CONNECTION.md` for auth/HMAC; `HANDOVER_FOR_ANTIGRAVITY.md` for repo layout
