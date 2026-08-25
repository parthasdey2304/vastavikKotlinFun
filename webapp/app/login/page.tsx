"use client";
import { useState } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { signInWithEmailAndPassword } from "firebase/auth";
import { auth } from "@/lib/firebase";

// Mirrors kotlin-app/.../auth/LoginScreen.kt:42
export default function LoginPage() {
  const r = useRouter();
  const [email,setEmail]=useState(""); const [pw,setPw]=useState(""); const [show,setShow]=useState(false); const [loading,setLoading]=useState(false); const [err,setErr]=useState<string|null>(null);
  async function onLogin(){
    if(!email.trim()||!pw.trim()){ r.push("/"); return; }
    setLoading(true); setErr(null);
    try{
      if(!auth) throw new Error("Firebase not configured");
      await signInWithEmailAndPassword(auth,email.trim(),pw);
      r.push("/");
    }catch(e:any){ setErr(e.message||"Login failed"); } finally{ setLoading(false); }
  }
  return (
    <div className="min-h-[80vh] flex flex-col items-center justify-center p-6">
      <div className="w-full max-w-sm space-y-6">
        <div className="text-center">
          <div className="w-20 h-20 rounded-full bg-brand/10 grid place-items-center mx-auto text-3xl">💻</div>
          <h1 className="mt-4 text-2xl font-bold">Welcome Back</h1>
          <p className="text-sm text-zinc-500">Sign in to continue your learning</p>
        </div>
        <div className="space-y-4">
          <label className="block"><span className="text-sm">Email</span><div className="mt-1 flex items-center gap-2 rounded-2xl border border-zinc-300 px-3 py-3 bg-white"><span>✉️</span><input value={email} onChange={e=>setEmail(e.target.value)} placeholder="student@example.com" className="flex-1 outline-none" type="email"/></div></label>
          <label className="block"><span className="text-sm">Password</span><div className="mt-1 flex items-center gap-2 rounded-2xl border border-zinc-300 px-3 py-3 bg-white"><span>🔒</span><input value={pw} onChange={e=>setPw(e.target.value)} placeholder="••••••••" className="flex-1 outline-none" type={show?"text":"password"}/><button onClick={()=>setShow(s=>!s)} className="text-xs text-brand">{show?"Hide":"Show"}</button></div></label>
          <div className="text-right"><Link href="/forgot-password" className="text-sm text-brand">Forgot Password?</Link></div>
          {err && <p className="text-sm text-red-600 bg-red-50 p-3 rounded-xl">{err}</p>}
          <button onClick={onLogin} disabled={loading} className="w-full rounded-2xl bg-brand text-white py-4 font-bold disabled:opacity-50">{loading?"Signing in…":"Log In"}</button>
          <p className="text-center text-sm">Don&apos;t have an account? <Link href="/signup" className="text-brand font-semibold">Sign Up</Link></p>
        </div>
      </div>
    </div>
  );
}
