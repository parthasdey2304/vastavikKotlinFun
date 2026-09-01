"use client";

export function ControlBar({
  micOn, camOn, shareOn, hasSharePerm, isAdmin, recording, disabled, onMic, onCam, onShare, onLeave,
}: {
  micOn: boolean; camOn: boolean; shareOn: boolean; hasSharePerm: boolean; isAdmin: boolean; recording: boolean; disabled: Set<string>; onMic: () => void; onCam: () => void; onShare: () => void; onLeave: () => void;
}) {
  const showShare = isAdmin || hasSharePerm;
  const micDisabled = disabled.has("MIC");
  const camDisabled = disabled.has("CAMERA");
  const shareDisabled = disabled.has("SCREENSHARE");

  const controls: { key: string; label: string; icon: string; active: boolean; hidden: boolean; onClick: () => void }[] = [
    { key: "mic", label: micOn ? "Mute" : "Unmute", icon: micOn ? "🎤" : "🔇", active: micOn, hidden: micDisabled, onClick: onMic },
    { key: "cam", label: camOn ? "Cam on" : "Cam off", icon: camOn ? "📷" : "🚫", active: camOn, hidden: camDisabled, onClick: onCam },
    { key: "share", label: shareOn ? "Stop share" : "Share", icon: "🖥️", active: shareOn, hidden: !showShare || shareDisabled, onClick: onShare },
    { key: "leave", label: "Leave", icon: "✕", active: false, hidden: false, onClick: onLeave },
  ].filter(c => !c.hidden);

  // exactly 4 controls shown when allowed; otherwise 3 expand to fill (no gap)
  return (
    <div className="neo p-0 overflow-hidden">
      <div className="flex items-center justify-between gap-3 px-3 md:px-4 py-3 bg-white">
        {recording ? (
          <span className="inline-flex items-center gap-1.5 bg-red-600 text-white text-xs font-bold px-3 py-1.5 rounded-full border-2 border-black">
            <span className="w-2 h-2 bg-white rounded-full animate-pulse" /> REC
          </span>
        ) : <span className="w-px" />}
        <div className="flex gap-2">
          {controls.map(c => (
            <button
              key={c.key}
              onClick={c.onClick}
              className={`flex-1 md:flex-none px-4 py-2.5 rounded-xl border-2 border-black text-sm font-bold shadow-[3px_3px_0_0_#000] transition ${
                c.key === "leave" ? "bg-red-600 text-white hover:bg-red-700" : c.active ? "bg-emerald-500 text-white" : "bg-white text-black hover:bg-slate-50"
              }`}
            >
              <span className="mr-1.5">{c.icon}</span>{c.label}
            </button>
          ))}
        </div>
      </div>
    </div>
  );
}
