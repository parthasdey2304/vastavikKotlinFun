"use client";
import { useState } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { createUserWithEmailAndPassword } from "firebase/auth";
import { auth } from "@/lib/firebase";

// Mirrors SignupScreen.kt:41
export default function SignupPage(){
  const r=useRouter(); const [email,setEmail]=useState(""); const [pw,setPw]=useState(""); const [confirm,setConfirm]=useState(""); const [loading,setLoading]=useState(false); const [err,setErr]=useState<string|null>(null);
  const mismatch = confirm.length>0 && pw!==confirm;
  async function onSignup(){
    if(mismatch) return;
    if(!email.trim()||!pw.trim()){ r.push("/"); return; }
    setLoading(true); setErr(null);
    try{ if(!auth) throw new Error("Firebase not configured"); await createUserWithEmailAndPassword(auth,email.trim(),pw); r.push("/welcome"); }catch(e:any){ setErr(e.message);} finally{ setLoading(false); }
  }
  return (
    <div className="min-h-[80vh] flex flex-col items-center justify-center p-6">
      <div className="w-full max-w-sm space-y-6">
        <div className="text-center"><div className="w-20 h-20 rounded-full bg-brand/10 grid place-items-center mx-auto text-3xl">💻</div><h1 className="mt-4 text-2xl font-bold">Create Account</h1><p className="text-sm text-zinc-500">Start your learning journey</p></div>
        <div className="space-y-4">
          <label className="block"><span className="text-sm">Email</span><div className="mt-1 flex items-center gap-2 rounded-2xl border border-zinc-300 px-3 py-3 bg-white"><span>✉️</span><input value={email} onChange={e=>setEmail(e.target.value)} placeholder="student@example.com" className="flex-1 outline-none" type="email"/></div></label>
          <label className="block"><span className="text-sm">Password</span><div className="mt-1 rounded-2xl border border-zinc-300 px-3 py-3 bg-white"><input value={pw} onChange={e=>setPw(e.target.value)} placeholder="••••••••" className="w-full outline-none" type="password"/></div></label>
          <label className="block"><span className="text-sm">Confirm Password</span><div className={`mt-1 rounded-2xl border px-3 py-3 bg-white ${mismatch?"border-red-500":"border-zinc-300"}`}><input value={confirm} onChange={e=>setConfirm(e.target.value)} placeholder="••••••••" className="w-full outline-none" type="password"/></div>{mismatch && <p className="text-xs text-red-600 mt-1">Passwords do not match</p>}</label>
          {err && <p className="text-sm text-red-600 bg-red-50 p-3 rounded-xl">{err}</p>}
          <button onClick={onSignup} disabled={loading} className="w-full rounded-2xl bg-brand text-white py-4 font-bold disabled:opacity-50">{loading?"Creating…":"Sign Up"}</button>
          <p className="text-center text-sm">Already have an account? <Link href="/login" className="text-brand font-semibold">Log In</Link></p>
        </div>
      </div>
    </div>
  );
}
