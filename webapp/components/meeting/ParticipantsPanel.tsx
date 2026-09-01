"use client";
import type { Participant } from "@/lib/meeting";

export function ParticipantsPanel({
  participants, currentUserId, currentRole, onKick, onStar, onUnstar, onGrantShare, onRevokeShare,
}: {
  participants: Participant[]; currentUserId: string; currentRole: string; onKick: (id: string) => void; onStar: (id: string) => void; onUnstar: (id: string) => void; onGrantShare: (id: string) => void; onRevokeShare: (id: string) => void;
}) {
  return (
    <div className="neo overflow-hidden flex flex-col h-[420px] md:h-full">
      <div className="bg-black text-white px-4 py-3 font-bold text-sm">Participants ({participants.filter(p => !p.leftAt).length})</div>
      <div className="flex-1 overflow-auto p-3 space-y-2">
        {participants.filter(p => !p.leftAt).sort((a,b) => (b.role === "ADMIN" ? 1 : 0) - (a.role === "ADMIN" ? 1 : 0)).map(p => {
          const isMe = p.userId === currentUserId;
          const canKick = currentRole === "ADMIN" || (currentRole === "STARCAST" && p.role !== "ADMIN");
          const canStar = currentRole === "ADMIN" && p.role !== "ADMIN";
          const canShare = currentRole === "ADMIN" && p.role !== "ADMIN";
          return (
            <div key={p.userId} className={`rounded-xl border-2 border-black p-3 flex items-center justify-between gap-2 ${isMe ? "bg-sky-50" : "bg-white"}`}>
              <div className="flex items-center gap-3 min-w-0">
                <span className={`w-8 h-8 rounded-full grid place-items-center text-xs font-bold border-2 border-black shrink-0 ${p.role === "STARCAST" ? "bg-amber-300 text-black" : p.role === "ADMIN" ? "bg-blue-600 text-white" : "bg-slate-200"}`}>{p.displayName[0]?.toUpperCase()}</span>
                <div className="min-w-0">
                  <p className="text-sm font-bold truncate">{p.displayName}{p.role === "STARCAST" && <span className="ml-1 text-xs bg-amber-300 border border-black px-1.5 py-0.5 rounded">★ starCast</span>}{p.role === "ADMIN" && <span className="ml-1 text-xs bg-blue-600 text-white px-1.5 py-0.5 rounded">Admin</span>}{isMe && <span className="ml-1 text-xs bg-emerald-500 text-white px-1.5 py-0.5 rounded">You</span>}</p>
                  <p className="text-xs text-muted flex gap-1.5">{p.micState === "ON" ? "🎤" : "🔇"} {p.cameraState === "ON" ? "📷" : "🚫"} {p.handRaised ? "✋" : ""} {p.isScreenSharing ? "🖥️" : ""}</p>
                </div>
              </div>
              {!isMe && (
                <div className="flex gap-1">
                  {canShare && (p.hasScreenSharePermission ? <button onClick={() => onRevokeShare(p.userId)} className="text-xs border-2 border-black rounded-lg px-2 py-1 bg-red-50">Revoke share</button> : <button onClick={() => onGrantShare(p.userId)} className="text-xs border-2 border-black rounded-lg px-2 py-1 bg-emerald-50">Grant share</button>)}
                  {canStar && (p.role === "STARCAST" ? <button onClick={() => onUnstar(p.userId)} className="text-xs border-2 border-black rounded-lg px-2 py-1">Unstar</button> : <button onClick={() => onStar(p.userId)} className="text-xs border-2 border-black rounded-lg px-2 py-1">Star</button>)}
                  {canKick && p.role !== "ADMIN" && <button onClick={() => onKick(p.userId)} className="text-xs border-2 border-black rounded-lg px-2 py-1 bg-red-600 text-white">Remove</button>}
                </div>
              )}
            </div>
          );
        })}
      </div>
    </div>
  );
}
