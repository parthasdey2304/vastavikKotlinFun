"use client";
import { useParams, useRouter } from "next/navigation";
import { useState } from "react";
import type { LiveChatMessage, Participant, WhiteboardElement, ReplyPreview } from "@/lib/meeting";
import { Whiteboard, Tool } from "@/components/meeting/Whiteboard";
import { ControlBar } from "@/components/meeting/ControlBar";
import { ChatPanel } from "@/components/meeting/ChatPanel";
import { ParticipantsPanel } from "@/components/meeting/ParticipantsPanel";
import Link from "next/link";

export default function InClassPage() {
  const params = useParams<{ classId: string }>();
  const classId = params.classId || "demo";
  const router = useRouter();
  const topic = `Live Class: ${decodeURIComponent(classId)}`;

  // mock local state — replace with WebSocketMeetingClient when backend is ready (same event names as Kotlin)
  const [participants, setParticipants] = useState<Participant[]>([
    { userId: "admin_demo", displayName: "Prof. Rao", role: "ADMIN", micState: "OFF", cameraState: "OFF", handRaised: false, isScreenSharing: false, hasScreenSharePermission: true, joinedAt: Date.now() - 60000 },
    { userId: "u_me", displayName: "You", role: "STUDENT", micState: "OFF", cameraState: "OFF", handRaised: false, isScreenSharing: false, hasScreenSharePermission: false, joinedAt: Date.now() },
    { userId: "u_star", displayName: "Aarav", role: "STARCAST", micState: "ON", cameraState: "ON", handRaised: false, isScreenSharing: false, hasScreenSharePermission: false, joinedAt: Date.now() - 30000 },
  ]);
  const [messages, setMessages] = useState<LiveChatMessage[]>([]);
  const [replyTo, setReplyTo] = useState<ReplyPreview | null>(null);
  const [elements, setElements] = useState<WhiteboardElement[]>([]);
  const [tool, setTool] = useState<Tool>("PEN");
  const [micOn, setMicOn] = useState(false);
  const [camOn, setCamOn] = useState(false);
  const [shareOn, setShareOn] = useState(false);
  const [handRaised, setHandRaised] = useState(false);
  const [recording, setRecording] = useState(false);
  const [disabled] = useState<Set<string>>(new Set());
  const [showChat, setShowChat] = useState(true);
  const [showPeople, setShowPeople] = useState(true);

  const me: Participant | undefined = participants.find(p => p.userId === "u_me");
  const isAdmin = me?.role === "ADMIN";
  const hasSharePerm = Boolean(me?.hasScreenSharePermission || isAdmin);

  const handleSend = (text: string, reply: ReplyPreview | null) => {
    const msg: LiveChatMessage = {
      id: `m_${Date.now()}`, senderId: "u_me", senderName: "You", senderRole: (me?.role as any) || "STUDENT", text, timestamp: Date.now(),
      replyTo: reply || undefined,
    };
    setMessages(m => [...m, msg]);
  };

  return (
    <div className="space-y-4">
      {/* header */}
      <div className="neo p-3 flex items-center justify-between gap-3">
        <div>
          <p className="text-xs font-semibold tracking-widest uppercase text-muted">Live now</p>
          <h1 className="font-display font-extrabold text-lg leading-none">{topic}</h1>
        </div>
        <div className="flex gap-2">
          <button onClick={() => setShowPeople(v => !v)} className={`px-3 py-2 rounded-xl border-2 border-black text-xs font-bold ${showPeople ? "bg-black text-white" : "bg-white"}`}>People ({participants.length})</button>
          <button onClick={() => setShowChat(v => !v)} className={`px-3 py-2 rounded-xl border-2 border-black text-xs font-bold ${showChat ? "bg-black text-white" : "bg-white"}`}>Chat</button>
          <button onClick={() => setHandRaised(v => !v)} className={`px-3 py-2 rounded-xl border-2 border-black text-xs font-bold ${handRaised ? "bg-amber-400" : "bg-white"}`}>{handRaised ? "Lower hand" : "Raise hand ✋"}</button>
          {isAdmin && (
            <button onClick={() => setRecording(r => !r)} className={`hidden md:inline-flex px-3 py-2 rounded-xl border-2 border-black text-xs font-bold ${recording ? "bg-red-600 text-white" : "bg-white"}`}>{recording ? "Stop REC" : "Start REC"}</button>
          )}
        </div>
      </div>

      {/* DESKTOP LAYOUT — highly responsive, not a stretched mobile view */}
      <div className="grid lg:grid-cols-[1fr_360px] gap-4">
        {/* main column */}
        <div className="space-y-4 min-w-0">
          <Whiteboard elements={elements} onChange={setElements} tool={tool} onToolChange={setTool} />
          {/* video tiles — behind/adjacent to whiteboard */}
          <div className="neo p-3">
            <p className="text-xs font-bold mb-2">Video tiles (camera OFF by default)</p>
            <div className="grid grid-cols-3 gap-2">
              {participants.filter(p => p.cameraState === "ON").map(p => (
                <div key={p.userId} className="aspect-video rounded-xl bg-black text-white grid place-items-center text-xs border-2 border-black">📷 {p.displayName}</div>
              ))}
              {participants.filter(p => p.cameraState === "ON").length === 0 && (
                <div className="col-span-3 py-6 text-center text-sm text-muted border-2 border-dashed border-black/20 rounded-xl">No camera on — tiles appear here when participants enable video.</div>
              )}
            </div>
          </div>
          <ControlBar
            micOn={micOn} camOn={camOn} shareOn={shareOn} hasSharePerm={hasSharePerm} isAdmin={isAdmin} recording={recording} disabled={disabled}
            onMic={() => setMicOn(v => !v)}
            onCam={() => setCamOn(v => !v)}
            onShare={() => {
              if (shareOn) setShareOn(false);
              else if (hasSharePerm) setShareOn(true);
              else {
                // request — in real backend this sends screenshare-request
                alert("Screenshare request sent to Admin (screenshare-request event).");
              }
            }}
            onLeave={() => router.push("/")}
          />
          <p className="text-xs text-muted text-center">Screenshare: max 2 at a time (Admin + one granted student). When hidden, remaining 3 controls expand — no gap. · Recording captures Admin + granted student's share.</p>
        </div>

        {/* side column — desktop persistent; mobile stacks below */}
        <div className="space-y-4">
          {showPeople && (
            <ParticipantsPanel
              participants={participants}
              currentUserId="u_me"
              currentRole={me?.role || "STUDENT"}
              onKick={id => setParticipants(ps => ps.filter(p => p.userId !== id))}
              onStar={id => setParticipants(ps => ps.map(p => p.userId === id ? { ...p, role: "STARCAST" } as Participant : p.role === "STARCAST" ? { ...p, role: "STUDENT" } as Participant : p))}
              onUnstar={id => setParticipants(ps => ps.map(p => p.userId === id ? { ...p, role: "STUDENT" } as Participant : p))}
              onGrantShare={id => setParticipants(ps => ps.map(p => p.userId === id ? { ...p, hasScreenSharePermission: true } : p))}
              onRevokeShare={id => setParticipants(ps => ps.map(p => p.userId === id ? { ...p, hasScreenSharePermission: false, isScreenSharing: false } : p))}
            />
          )}
          {showChat && (
            <ChatPanel
              messages={messages}
              currentUserId="u_me"
              replyTo={replyTo}
              onSend={handleSend}
              onReply={m => setReplyTo({ messageId: m.id, senderName: m.senderName, senderRole: m.senderRole, text: m.text, truncatedText: m.text.slice(0, 48) })}
              onReplyChange={setReplyTo}
              chatEnabled={!disabled.has("CHAT")}
            />
          )}
          {!showPeople && !showChat && (
            <div className="neo p-6 text-center">
              <p className="text-sm font-bold">Panels hidden</p>
              <p className="text-xs text-muted mt-1">Toggle People / Chat above. On desktop the whiteboard stays full-width; panels slide in without shifting the control bar.</p>
            </div>
          )}
        </div>
      </div>

      <div className="flex justify-between text-xs">
        <Link href={`/meeting/lobby/${encodeURIComponent(classId)}`} className="underline">← Back to lobby</Link>
        <Link href="/" className="underline">Home</Link>
      </div>
    </div>
  );
}
