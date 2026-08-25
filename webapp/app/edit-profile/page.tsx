"use client";
import { useState, useEffect } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { doc, getDoc, setDoc, serverTimestamp } from "firebase/firestore";
import { auth, db } from "@/lib/firebase";

// Mirrors EditProfileScreen.kt
export default function EditProfile(){
  const r=useRouter(); const [name,setName]=useState("Student"); const [school,setSchool]=useState(""); const [klass,setKlass]=useState(""); const [board,setBoard]=useState("ICSE");
  useEffect(()=>{ (async()=>{
    const uid=auth?.currentUser?.uid; if(!uid || !db) return;
    const snap=await getDoc(doc(db,"users",uid)); if(snap.exists()){ const d=snap.data() as any; setName(d.name||"Student"); setSchool(d.school||""); setKlass(d.studentClass||""); setBoard(d.board||"ICSE"); }
  })(); },[]);
  async function save(){
    const uid=auth?.currentUser?.uid; if(!uid || !db){ r.push("/profile"); return; }
    await setDoc(doc(db,"users",uid), { name, school, studentClass: klass, board, updatedAt: serverTimestamp() }, { merge: true });
    r.push("/profile");
  }
  return (
    <div className="space-y-4 max-w-lg mx-auto">
      <div className="flex justify-between items-center"><Link href="/profile" className="text-sm">← Back</Link><button onClick={save} className="text-sm text-brand font-bold">Save</button></div>
      <h1 className="text-xl font-bold">Edit Profile</h1>
      <div className="space-y-3">
        <label className="block"><span className="text-sm">Full Name</span><input value={name} onChange={e=>setName(e.target.value)} className="mt-1 w-full rounded-2xl border px-3 py-3 bg-white"/></label>
        <label className="block"><span className="text-sm">Email</span><input value={auth?.currentUser?.email||"student@example.com"} disabled className="mt-1 w-full rounded-2xl border px-3 py-3 bg-zinc-100 text-zinc-500"/></label>
        <label className="block"><span className="text-sm">School</span><input value={school} onChange={e=>setSchool(e.target.value)} className="mt-1 w-full rounded-2xl border px-3 py-3 bg-white"/></label>
        <label className="block"><span className="text-sm">Class</span><input value={klass} onChange={e=>setKlass(e.target.value)} className="mt-1 w-full rounded-2xl border px-3 py-3 bg-white"/></label>
        <label className="block"><span className="text-sm">Board</span><select value={board} onChange={e=>setBoard(e.target.value)} className="mt-1 w-full rounded-2xl border px-3 py-3 bg-white"><option>ICSE</option><option>CBSE</option><option>State Board</option></select></label>
        <button onClick={save} className="w-full rounded-2xl bg-brand text-white py-4 font-bold">Save Changes</button>
      </div>
    </div>
  );
}
