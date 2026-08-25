"use client";
import { useState } from "react";
import Link from "next/link";
import { sendPasswordResetEmail } from "firebase/auth";
import { auth } from "@/lib/firebase";

// Mirrors ForgotPasswordScreen.kt:21
export default function ForgotPage(){
  const [email,setEmail]=useState(""); const [loading,setLoading]=useState(false); const [sent,setSent]=useState(false); const [err,setErr]=useState<string|null>(null);
  async function onSend(){
    if(!email.trim()) return;
    setLoading(true); setErr(null);
    try{ if(!auth) throw new Error("Firebase not configured"); await sendPasswordResetEmail(auth,email.trim()); setSent(true);}catch(e:any){ setErr(e.message);} finally{ setLoading(false);}
  }
  return (
    <div className="min-h-[80vh] p-6">
      <Link href="/login" className="text-sm text-brand">← Back</Link>
      <div className="max-w-sm mx-auto mt-8 space-y-6 text-center">
        <div className="w-20 h-20 rounded-full bg-brand/10 grid place-items-center mx-auto text-3xl">🔐</div>
        <h1 className="text-2xl font-bold">Reset Password</h1>
        <p className="text-sm text-zinc-500">Enter your email and we&apos;ll send you a reset link</p>
        <div className="text-left space-y-4">
          <div className="rounded-2xl border border-zinc-300 px-3 py-3 bg-white flex gap-2"><span>✉️</span><input value={email} onChange={e=>setEmail(e.target.value)} placeholder="student@example.com" className="flex-1 outline-none" type="email"/></div>
          {sent && <div className="bg-green-50 text-green-700 p-3 rounded-xl text-sm">Password reset link has been sent to your email.</div>}
          {err && <p className="text-sm text-red-600">{err}</p>}
          <button onClick={onSend} disabled={!email.trim()||loading} className="w-full rounded-2xl bg-brand text-white py-4 font-bold disabled:opacity-50">{loading?"Sending…":"Send Reset Link"}</button>
        </div>
      </div>
    </div>
  );
}
