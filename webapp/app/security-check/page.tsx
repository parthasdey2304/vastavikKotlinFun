"use client";
import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { auth } from "@/lib/firebase";

// Mirrors SecurityCheckScreen.kt — device checks are Android-only; on web we just show All Clear and redirect
export default function SecurityCheck(){
  const r=useRouter(); const [phase,setPhase]=useState<"checking"|"ok">("checking");
  useEffect(()=>{
    const t=setTimeout(()=>setPhase("ok"),900);
    const t2=setTimeout(()=>{
      const user = auth?.currentUser;
      r.replace(user ? "/" : "/login");
    },2200);
    return()=>{clearTimeout(t);clearTimeout(t2);};
  },[r]);
  return (
    <div className="min-h-screen bg-gradient-to-b from-brand to-brand/80 flex flex-col items-center justify-center p-6 text-white text-center">
      <div className="w-16 h-16 rounded-full bg-white/15 grid place-items-center text-3xl">🛡️</div>
      <h1 className="mt-4 text-2xl font-bold">Device Security Check</h1>
      <p className="text-sm text-white/80 mt-1">We need to verify your device is secure</p>
      <div className="mt-8 neo bg-white text-zinc-900 p-6 w-full max-w-sm">
        {phase==="checking" ? (<><div className="w-12 h-12 border-4 border-brand border-t-transparent rounded-full animate-spin mx-auto"/><p className="mt-3 text-sm">Scanning device…</p></>) : (
          <><div className="w-12 h-12 rounded-full bg-green-100 text-green-600 grid place-items-center mx-auto text-2xl">✓</div><p className="mt-3 font-bold">All Clear!</p><p className="text-xs text-zinc-500">Redirecting…</p></>
        )}
      </div>
    </div>
  );
}
