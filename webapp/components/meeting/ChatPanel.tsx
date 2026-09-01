"use client";
import { useState } from "react";
import type { LiveChatMessage, ReplyPreview } from "@/lib/meeting";

export function ChatPanel({
  messages, currentUserId, replyTo, onSend, onReply, onReplyChange, chatEnabled,
}: {
  messages: LiveChatMessage[]; currentUserId: string; replyTo: ReplyPreview | null; onSend: (text: string, reply: ReplyPreview | null) => void; onReply: (m: LiveChatMessage) => void; onReplyChange: (r: ReplyPreview | null) => void; chatEnabled: boolean;
}) {
  const [text, setText] = useState("");
  return (
    <div className="neo flex flex-col h-[420px] md:h-full overflow-hidden">
      <div className="bg-black text-white px-4 py-3 flex items-center justify-between">
        <span className="font-bold text-sm">Chat</span>
        {!chatEnabled && <span className="text-xs opacity-70">Disabled by Admin</span>}
      </div>
      {replyTo && (
        <div className="mx-3 mt-3 p-2.5 rounded-xl border-2 border-amber-600 bg-amber-50 flex justify-between gap-2">
          <div className="min-w-0">
            <p className="text-xs font-bold text-amber-700">Replying to {replyTo.senderName}</p>
            <p className="text-xs text-amber-900 truncate">{replyTo.truncatedText || replyTo.text}</p>
          </div>
          <button onClick={() => onReplyChange(null)} className="text-amber-700 font-bold">×</button>
        </div>
      )}
      <div className="flex-1 overflow-auto p-3 space-y-2">
        {messages.slice().reverse().map(m => {
          const isOwn = m.senderId === currentUserId;
          return (
            <div key={m.id} className={`flex gap-2 ${isOwn ? "justify-end" : "justify-start"}`}>
              <div className={`max-w-[80%] rounded-2xl border-2 border-black px-3 py-2 ${isOwn ? "bg-black text-white" : "bg-white"}`}>
                {m.replyTo && (
                  <div className={`mb-1.5 rounded-lg border px-2 py-1 text-xs ${isOwn ? "bg-white/10 border-white/20" : "bg-slate-50 border-black/10"}`}>
                    <span className="font-bold">{m.replyTo.senderName}</span> <span className="opacity-70">{m.replyTo.truncatedText || m.replyTo.text}</span>
                  </div>
                )}
                {!isOwn && <p className="text-xs font-bold">{m.senderName}{m.senderRole === "STARCAST" ? " ★ starCast" : ""}</p>}
                <p className="text-sm leading-snug">{m.text}</p>
                <p className="text-[11px] opacity-60 mt-1">{new Date(m.timestamp).toLocaleTimeString()}</p>
                {!isOwn && <button onClick={() => onReply(m)} className="text-xs font-semibold text-brand hover:underline mt-1">Reply</button>}
              </div>
            </div>
          );
        })}
        {messages.length === 0 && <p className="text-sm text-muted text-center py-8">No messages yet. Say hi 👋</p>}
      </div>
      {chatEnabled ? (
        <div className="p-3 border-t-2 border-black bg-white flex gap-2">
          <input value={text} onChange={e => setText(e.target.value)} onKeyDown={e => { if (e.key === "Enter" && text.trim()) { onSend(text.trim(), replyTo); setText(""); onReplyChange(null); } }} placeholder="Message…" className="flex-1 rounded-xl border-2 border-black px-3 py-2 text-sm outline-none" />
          <button onClick={() => { if (text.trim()) { onSend(text.trim(), replyTo); setText(""); onReplyChange(null); } }} className="px-4 py-2 rounded-xl bg-black text-white text-sm font-bold border-2 border-black shadow-[3px_3px_0_0_#000]">Send</button>
        </div>
      ) : (
        <div className="p-3 text-xs text-muted text-center border-t-2 border-black">Chat is disabled by the host.</div>
      )}
    </div>
  );
}
