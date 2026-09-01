// Shared meeting domain — mirrors kotlin-app/data/model/MeetingModels.kt
// Keep event names / role semantics identical across Kotlin + web so both talk to same backend.

export type Role = "ADMIN" | "STARCAST" | "STUDENT";
export type MediaState = "ON" | "OFF";
export type DisabledFeature = "MIC" | "CAMERA" | "SCREENSHARE" | "CAPTIONS" | "CHAT" | "RAISE_HAND" | "EMOJI" | "RECORDING";

export interface Participant {
  userId: string;
  displayName: string;
  role: Role;
  micState: MediaState;
  cameraState: MediaState;
  handRaised: boolean;
  isScreenSharing: boolean;
  hasScreenSharePermission: boolean;
  joinedAt: number;
  leftAt?: number;
}

export interface ReplyPreview { messageId: string; senderName: string; senderRole: Role; text: string; truncatedText: string; }
export interface LiveChatMessage {
  id: string;
  senderId: string;
  senderName: string;
  senderRole: Role;
  text: string;
  timestamp: number;
  replyTo?: ReplyPreview;
}

export interface ClassSession {
  classId: string;
  topic: string;
  adminId: string;
  startTime: number;
  endTime?: number;
  isLive: boolean;
  recording: boolean;
  disabledFeatures: DisabledFeature[];
}

export interface WhiteboardState { elements: WhiteboardElement[]; viewport: { x: number; y: number; zoom: number }; }
export type ElementType = "PEN" | "ERASER" | "RECTANGLE" | "ELLIPSE" | "LINE" | "ARROW" | "TEXT";
export interface WhiteboardElement { id: string; type: ElementType; points: { x: number; y: number }[]; text: string; color: string; strokeWidth: number; fontSize: number; bounds: { x: number; y: number; width: number; height: number }; }

export type MeetEventType =
  | "join" | "leave" | "toggle-mic" | "toggle-camera" | "toggle-hand-raise" | "toggle-screenshare"
  | "screenshare-request" | "screenshare-grant" | "screenshare-revoke"
  | "chat-message" | "chat-reply" | "emoji-reaction" | "kick-participant" | "assign-starcast" | "revoke-starcast"
  | "feature-toggle" | "recording-start" | "recording-stop" | "class-started" | "whiteboard-update";

// Tiny in-memory mock client (replace with WebSocketMeetingClient when backend is ready).
export class MockMeetingClient {
  participants: Participant[] = [];
  messages: LiveChatMessage[] = [];
  whiteboard: WhiteboardState = { elements: [], viewport: { x: 0, y: 0, zoom: 1 } };
  session: ClassSession | null = null;
  // ... for brevity web uses React state instead of flows; see page implementations
}
