"use client";
import Link from "next/link";
import { useParams, useRouter } from "next/navigation";
import { useState } from "react";

export default function LobbyPage() {
  const params = useParams<{ classId: string }>();
  const classId = params.classId || "demo";
  const router = useRouter();
  const [joining, setJoining] = useState(false);
  const topic = `Live Class: ${decodeURIComponent(classId)}`;

  return (
    <div className="min-h-[70vh] grid place-items-center py-8">
      <div className="neo p-0 overflow-hidden max-w-[440px] w-full">
        <div className="p-8 text-center">
          <div className="w-16 h-16 rounded-2xl bg-black text-white grid place-items-center mx-auto text-xl border-2 border-black shadow-[3px_3px_0_0_#000]">🎥</div>
          <h1 className="font-display font-extrabold text-2xl mt-4">Live Class</h1>
          <p className="text-sm text-muted mt-1">{topic}</p>
          <div className="mt-6 flex gap-3">
            <Link href="/" className="flex-1 py-3 rounded-xl border-2 border-black bg-white font-bold text-sm text-center">Cancel</Link>
            <button
              onClick={() => { setJoining(true); setTimeout(() => router.push(`/meeting/inclass/${encodeURIComponent(classId)}`), 600); }}
              disabled={joining}
              className="flex-1 py-3 rounded-xl bg-black text-white font-bold text-sm border-2 border-black shadow-[3px_3px_0_0_#000] disabled:opacity-60"
            >
              {joining ? "Joining…" : "Join Class →"}
            </button>
          </div>
          <p className="text-xs text-muted mt-3">Mic & camera will be OFF by default. Whiteboard is the default view.</p>
          <div className="mt-6 text-left neo p-3 bg-slate-50">
            <p className="text-xs font-bold">What you’ll see inside</p>
            <ul className="text-xs text-muted mt-1 list-disc pl-4 space-y-1">
              <li>Whiteboard (default) — Excalidraw-style, NeoBrutalist border</li>
              <li>Controls: Mic, Video, Screenshare (Admin + one student only), Leave — Screenshare hidden when not eligible</li>
              <li>Chat with reply + starCast tag</li>
              <li>Participants roster with live status</li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  );
}
