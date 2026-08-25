# ARCHITECTURE.md — System Architecture — vastavikComputers

> **Repo:** https://github.com/parthasdey2304/vastavikComputers | **Root:** D:\vastavikComputers | **Version:** 0.1.0

---

## 1. System Overview

```
                    +-------------------+     +-------------------+
                    |   Android App     |     |    Web App        |
                    | Kotlin + Compose  |     | Next.js 15 + React|
                    |  (kotlin-app/)    |     |    (web/)         |
                    +---------+---------+     +---------+---------+
                              |                         |
                              |  Firebase SDK / HTTPS   |
                              v                         v
                    +-------------------------------------------+
                    |           Firebase Backend                |
                    |  Auth | Firestore | Storage | FCM | Hosting|
                    |  Cloud Functions (Node 20 TS)             |
                    +-----+----------+----------+---------------+
                          |          |          |
              +-----------+ +--------+ +--------+-----------+
              | Gemini API | | Docker | | Razorpay/Cashfree |
              | gemini-    | |Sandbox | | UPI AutoPay       |
              | 3.7-flash  | |CloudRun| | Webhooks          |
              +------------+ +--------+ +-------------------+
```

- **Monorepo root:** `D:\vastavikComputers`
- Legacy Flutter (`lib/`, `pubspec.yaml`, `android/` Flutter) is **deprecated** — kept for reference until migration complete.
- New dirs: `web/`, `kotlin-app/` (aliased as `android/` in docs), `functions/`, `shared/`, `.agent/`

---

## 2. Monorepo Structure

```
D:\vastavikComputers/
├── .agent/                 # 12 Markdown docs (this folder)
├── web/                    # Next.js 15 web app
├── kotlin-app/             # Android app (Kotlin + Compose) — also android/
├── functions/              # Firebase Cloud Functions (TypeScript)
├── shared/                 # Shared Zod schemas, types, constants
├── firebase.json
├── firestore.rules
├── firestore.indexes.json
├── storage.rules
├── .github/workflows/      # CI/CD
└── docs/                   # Extra docs (optional)
```

---

## 3. Android App Structure (`kotlin-app/`) — MVVM + Clean Architecture

```
kotlin-app/
├── app/
│   ├── src/main/java/com/vastavik/computer/
│   │   ├── di/                         # Hilt modules (AppModule, NetworkModule, DbModule)
│   │   ├── data/
│   │   │   ├── remote/                 # Retrofit APIs, Firebase data sources
│   │   │   │   ├── FirebaseAuthDs.kt
│   │   │   │   ├── FirestoreDs.kt
│   │   │   │   └── GeminiProxyApi.kt
│   │   │   ├── local/                  # Room DB, DataStore
│   │   │   │   ├── VastavikDatabase.kt
│   │   │   │   ├── dao/
│   │   │   │   └── entity/
│   │   │   ├── repository/             # Repo impls
│   │   │   └── model/                  # DTOs
│   │   ├── domain/
│   │   │   ├── model/                  # Domain models
│   │   │   └── usecase/                # UseCases (GetLessons, SubmitCode, ChatWithAi)
│   │   ├── ui/
│   │   │   ├── theme/                  # Color.kt, Type.kt, Theme.kt
│   │   │   ├── navigation/             # NavGraph.kt, Routes.kt
│   │   │   ├── screens/                # per-feature screens + ViewModels
│   │   │   │   ├── auth/
│   │   │   │   ├── home/
│   │   │   │   ├── lessons/
│   │   │   │   ├── editor/
│   │   │   │   ├── chat/
│   │   │   │   ├── practice/            # MCQ + coding
│   │   │   │   └── profile/
│   │   │   └── components/             # Reusable Composables
│   │   └── util/                       # Extensions, Constants, Result.kt
│   └── build.gradle.kts
└── gradle/
```

**State:** ViewModel + StateFlow | **DI:** Hilt | **Async:** Coroutines + Flow

---

## 4. Web App Structure (`web/`) — Next.js 15 App Router

```
web/
├── app/
│   ├── (auth)/                         # Auth group — login, register, verify
│   │   ├── login/page.tsx
│   │   └── register/page.tsx
│   ├── (student)/                      # Student area (protected)
│   │   ├── dashboard/page.tsx
│   │   ├── subjects/[subjectId]/page.tsx
│   │   ├── lessons/[lessonId]/page.tsx
│   │   ├── editor/page.tsx
│   │   ├── chat/page.tsx
│   │   ├── practice/
│   │   │   ├── mcq/page.tsx
│   │   │   ├── coding/[id]/page.tsx
│   │   │   └── papers/page.tsx
│   │   └── profile/page.tsx
│   ├── admin/                          # Admin area (admin claim required)
│   │   ├── page.tsx                    # Dashboard + Recharts
│   │   ├── users/page.tsx
│   │   ├── content/page.tsx
│   │   ├── papers/builder/page.tsx
│   │   ├── payments/page.tsx
│   │   └── notifications/page.tsx
│   ├── api/
│   │   ├── gemini/route.ts             # POST — proxy to geminiChat
│   │   ├── code-exec/route.ts          # POST — proxy to executeCode
│   │   ├── payment/
│   │   │   ├── create/route.ts         # POST — create mandate
│   │   │   └── webhook/route.ts        # POST — gateway webhook
│   │   ├── pdf/generate/route.ts       # POST — generate paper PDF
│   │   └── admin/questions/bulk/route.ts
│   ├── layout.tsx
│   ├── globals.css
│   └── middleware.ts                   # Auth + admin guard
├── components/                         # ui/, video/, editor/, chat/, admin/
├── hooks/                              # useAuth, useGemini, useCodeExec, etc.
├── lib/
│   ├── firebase.ts                     # Client SDK init
│   ├── firebase-admin.ts               # Admin SDK (server)
│   ├── gemini.ts                       # gemini-3.7-flash client
│   └── validators.ts                   # Zod schemas
├── stores/                             # Zustand stores (auth, editor, chat)
├── types/                              # Shared TS types (mirrors Firestore)
└── public/
```

**State (Web):** Zustand | **Validation:** Zod | **Styling:** Tailwind CSS 4

---

## 5. Firebase Services

| Service | Usage | Config File |
|---|---|---|
| **Auth** | Email/password, Google, email verification, custom claims (`admin`) | Firebase Console + `functions/src/auth/` |
| **Firestore** | 14 collections (see §7), offline persistence, indexes in `firestore.indexes.json` | `firestore.rules`, `firestore.indexes.json` |
| **Cloud Storage** | Video files (HLS), PDFs, theory diagrams, user avatars | `storage.rules` |
| **Cloud Functions** | Node 20+ TypeScript — auth triggers, AI proxy, payments, sandbox proxy | `functions/` |
| **FCM** | Push to Android + Web (topic + token) | `functions/src/notifications/` |
| **Hosting** | Web prod (or Vercel — decision in VERSION.md) | `firebase.json` |

---

## 6. Cloud Functions Structure (`functions/src/`)

```
functions/src/
├── index.ts                # Exports all functions
├── auth/
│   ├── onUserCreate.ts     # Auth trigger — create users/{uid} doc, send welcome FCM
│   └── setAdmin.ts         # Callable — sets custom claim admin:true (admin-only)
├── content/
│   └── onContentPublish.ts # Firestore trigger — notify subscribers, invalidate cache
├── ai/
│   └── geminiChat.ts       # HTTPS callable — validates, rate-limits, calls gemini-3.7-flash
├── payments/
│   ├── createMandate.ts    # HTTPS — creates Razorpay/Cashfree mandate
│   ├── paymentWebhook.ts   # HTTPS — verifies signature, updates Firestore
│   └── checkExpiry.ts      # Scheduled (daily 02:00 IST) — expiry + grace + downgrade
├── code/
│   └── executeCode.ts      # HTTPS — validates, forwards to Docker sandbox, returns output
├── admin/
│   └── bulkImport.ts       # HTTPS (admin) — bulk MCQ/coding import
└── notifications/
    └── sendPush.ts         # Callable (admin) — sends FCM
```

**Runtime:** Node 20, TypeScript strict, `firebase-functions` v5, `firebase-admin` v12

---

## 7. Firestore Data Model — 14 Collections

### `users/{uid}`

```ts
{
  email: string
  displayName: string
  photoURL?: string
  classLevel: number        // 5..12
  role: "student" | "admin"
  subscription: {
    plan: "free" | "pro"
    status: "active" | "expired" | "grace" | "cancelled"
    mandateId?: string
    expiresAt?: Timestamp
    graceEndsAt?: Timestamp
  }
  notificationPrefs?: { newLesson: boolean; expiry: boolean; reminders: boolean }
  createdAt: Timestamp
  lastActiveAt: Timestamp
  fcmTokens?: string[]
}
```

### `subjects/{subjectId}`

```ts
{ name: "Java"|"Python"|"JavaScript"|"SQL", icon: string, classes: number[], description: string, order: number }
```

### `courses/{courseId}`

```ts
{ subjectId: DocumentRef<subjects>, classLevel: number, title: string, description: string, topicIds: string[], order: number, isPublished: boolean }
```

### `topics/{topicId}`

```ts
{ courseId: DocumentRef<courses>, title: string, order: number, lessonIds: string[], isPublished: boolean }
```

### `lessons/{lessonId}`

```ts
{
  topicId: DocumentRef<topics>
  title: string
  description: string
  videos: { vscode?: { url: string; duration: number }, whiteboard?: { url: string; duration: number }, short?: { url: string; duration: number } }
  theoryId?: DocumentRef<theory>
  order: number
  isPublished: boolean
  isPro: boolean               // true = Pro tier required
  createdAt: Timestamp
}
```

### `theory/{theoryId}`

```ts
{ lessonId: DocumentRef<lessons>, title: string, content: string /* Markdown */, references: { title: string; url: string }[], updatedAt: Timestamp }
```

### `mcqs/{mcqId}`

```ts
{ subjectId: DocumentRef<subjects>, classLevel: number, topicId: DocumentRef<topics>, question: string, options: string[4], correctIndex: number, explanation: string, difficulty: "easy"|"medium"|"hard", isPublished: boolean }
```

### `codingQuestions/{cqId}`

```ts
{
  subjectId: DocumentRef<subjects>, classLevel: number, title: string, description: string,
  starterCode: { java?: string, python?: string, javascript?: string, sql?: string },
  testCases: { input: string, expected: string, isHidden: boolean }[],
  timeLimit: number, // seconds
  difficulty: "easy"|"medium"|"hard", isPublished: boolean
}
```

### `questionPapers/{qpId}`

```ts
{ subjectId: DocumentRef<subjects>, classLevel: number, title: string, sections: { name: string; questionIds: string[]; marks: number }[], totalMarks: number, duration: number, pdfUrl?: string, isPublished: boolean, createdAt: Timestamp }
```

### `aiChats/{chatId}`

```ts
{ userId: DocumentRef<users>, messages: { role: "user"|"model", content: string, timestamp: Timestamp }[], tokenCount: number, createdAt: Timestamp, updatedAt: Timestamp }
```

### `codeSubmissions/{submissionId}`

```ts
{ userId: DocumentRef<users>, questionId: DocumentRef<codingQuestions>, language: "java"|"python"|"javascript"|"sql", code: string, output: string, status: "pass"|"fail"|"error", testResults?: { input: string; expected: string; actual: string; pass: boolean }[], createdAt: Timestamp }
```

### `notifications/{notifId}`

```ts
{ userId: DocumentRef<users>, title: string, body: string, type: "new_lesson"|"expiry"|"reminder"|"achievement"|"broadcast", read: boolean, createdAt: Timestamp }
```

### `payments/{paymentId}`

```ts
{ userId: DocumentRef<users>, amount: number, currency: "INR", status: "pending"|"success"|"failed"|"cancelled", mandateId?: string, method: "upi_autopay", gateway: "razorpay"|"cashfree", gatewayPaymentId?: string, createdAt: Timestamp }
```

### `logs/{logId}`

```ts
{ level: "info"|"warning"|"error"|"critical", source: string, message: string, metadata?: object, timestamp: Timestamp, userId?: string }
```

**Indexes:** Defined in `firestore.indexes.json` — composite on `(subjectId, classLevel, isPublished)`, `(userId, createdAt)`, etc.

---

## 8. API Routes

### Next.js API Routes (`web/app/api/`)

| Route | Method | Auth | Description |
|---|---|---|---|
| `/api/gemini` | POST | Firebase ID token | Proxy chat to `geminiChat` function; Zod validates `{message, chatId}` |
| `/api/code-exec` | POST | Firebase ID token (+ Pro for coding Q) | `{language, code, questionId?}` -> sandbox |
| `/api/payment/create` | POST | Firebase ID token | Creates UPI mandate; returns gateway checkout data |
| `/api/payment/webhook` | POST | Gateway signature | Verifies signature, idempotent upsert to `payments` |
| `/api/pdf/generate` | POST | Admin claim | `{paperId}` -> generates PDF via pdfkit, uploads to Storage, returns `pdfUrl` |
| `/api/admin/questions/bulk` | POST | Admin claim | Bulk import MCQs/coding Qs (CSV/JSON) |

### Cloud Functions (HTTPS / Triggers)

| Function | Trigger | Description |
|---|---|---|
| `onUserCreate` | `auth.user().onCreate` | Creates `users/{uid}` doc |
| `onContentPublish` | `firestore.document('lessons/{id}').onUpdate` | FCM to subscribers when `isPublished` flips true |
| `checkExpiry` | `pubsub.schedule('0 2 * * *')` Asia/Kolkata | Daily expiry check + grace + downgrade |
| `geminiChat` | `https.onCall` | Validates, rate-limits, calls `gemini-3.7-flash`, logs `aiChats` |
| `executeCode` | `https.onCall` | Validates, forwards to Docker sandbox (Cloud Run) |
| `setAdmin` | `https.onCall` (admin) | Sets custom claim |
| `sendPush` | `https.onCall` (admin) | Sends FCM |
| `bulkImport` | `https.onCall` (admin) | Bulk question import |

---

## 9. Cross-Platform Consistency

| Concern | Strategy |
|---|---|
| **Data models** | Single source: `shared/schemas/` Zod schemas generate TS types + Kotlin data classes (via manual sync) |
| **Content** | Same Firestore collections serve both platforms; no duplication |
| **AI proxy** | Both apps call same `geminiChat` endpoint; system prompt identical |
| **Auth SSO** | Firebase Auth — same project, same users, ID token shared; Web uses JS SDK, Android uses Firebase Auth SDK |
| **FCM** | Same sender ID; topics per `classLevel` + `subjectId` |
| **Offline** | Room (Android) mirrors key collections; Web uses Firestore offline persistence + Service Worker |

---

## 10. Key User Journey Flows

### 10.1 Student Watches Lesson
`Home -> Subjects -> Course -> Topic -> Lesson -> VideoPlayer (HLS/ExoPlayer) -> progress saved (Firestore) -> related theory + MCQs suggested`

### 10.2 Student Uses AI Chat
`Chat screen -> POST /api/gemini {message} -> geminiChat validates + rate-limit -> Gemini 3.7 Flash (1M context, system prompt: Class 5-12 tutor) -> response streamed -> aiChats updated -> tokenCount tracked`

### 10.3 Student Solves Coding Question
`Coding list -> Question detail (starterCode) -> Monaco/Compose editor -> Run -> POST /api/code-exec -> executeCode -> Docker sandbox (10s, 256M) -> per-test-case results -> codeSubmissions saved -> UI shows pass/fail`

### 10.4 Subscription via UPI AutoPay
`Profile -> Subscribe -> POST /api/payment/create -> Razorpay mandate link -> user approves in UPI app -> gateway webhook POST /api/payment/webhook -> verify signature -> payments doc + users.subscription = pro/active -> FCM confirmation -> checkExpiry daily cron handles renewal/expiry`

---

## 11. Deployment & Environments

| Env | Web | Functions | Firestore |
|---|---|---|---|
| **Local** | `npm run dev` (localhost:3000) + Functions emulator | `firebase emulators:start` | Emulator |
| **Staging** | Vercel preview / Firebase Hosting preview | `vastavikcomputers` (same project, staging build variant) | Staging project |
| **Prod** | Vercel prod (or Hosting) | `vastavikcomputers` us-central1 | Prod project |

**Secrets:** `GEMINI_API_KEY`, `RAZORPAY_KEY_SECRET`, `CASHFREE_SECRET` in Secret Manager / Vercel env — never committed.


---
> **Update v0.2.0:** Dual backend added — see BACKEND_SCHEMA.md for Supabase Postgres (7 tables) + supabaseSync CF. 21-feature flows in APP_FLOW.md.