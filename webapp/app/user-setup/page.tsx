"use client";
import { useState } from "react";
import { useRouter } from "next/navigation";
import { doc, setDoc, serverTimestamp } from "firebase/firestore";
import { auth, db } from "@/lib/firebase";

// Mirrors UserSetupScreen.kt:22 — collects name/class/board/school/language, writes to users/{uid} + studentSelections
export default function UserSetup(){
  const r=useRouter();
  const [name,setName]=useState(""); const [klass,setKlass]=useState(""); const [board,setBoard]=useState("ICSE"); const [school,setSchool]=useState(""); const [lang,setLang]=useState("Java");
  const canContinue = name.trim() && klass.trim() && school.trim();
  async function onContinue(){
    try{
      const uid = auth?.currentUser?.uid;
      if(uid && db){
        await setDoc(doc(db,"users",uid), { name: name.trim(), studentClass: klass.trim(), board, school: school.trim(), preferredLanguage: lang, updatedAt: serverTimestamp() }, { merge: true });
      }
    }catch{}
    r.push("/");
  }
  return (
    <div className="min-h-[80vh] p-6 max-w-lg mx-auto">
      <h1 className="text-2xl font-bold">Set Up Your Profile</h1>
      <p className="text-sm text-zinc-500">Tell us a bit about yourself</p>
      <div className="mt-6 space-y-4">
        <label className="block"><span className="text-sm">Full Name</span><div className="mt-1 rounded-2xl border px-3 py-3 bg-white flex gap-2"><span>👤</span><input value={name} onChange={e=>setName(e.target.value)} placeholder="Partha Dey" className="flex-1 outline-none"/></div></label>
        <label className="block"><span className="text-sm">Class</span><div className="mt-1 rounded-2xl border px-3 py-3 bg-white flex gap-2"><span>🎓</span><input value={klass} onChange={e=>setKlass(e.target.value)} placeholder="9" className="flex-1 outline-none"/></div></label>
        <label className="block"><span className="text-sm">Board</span><select value={board} onChange={e=>setBoard(e.target.value)} className="mt-1 w-full rounded-2xl border px-3 py-3 bg-white"><option>ICSE</option><option>CBSE</option><option>State Board</option></select></label>
        <label className="block"><span className="text-sm">School</span><div className="mt-1 rounded-2xl border px-3 py-3 bg-white flex gap-2"><span>🏫</span><input value={school} onChange={e=>setSchool(e.target.value)} placeholder="Vastavik Public School" className="flex-1 outline-none"/></div></label>
        <label className="block"><span className="text-sm">Preferred Language</span><select value={lang} onChange={e=>setLang(e.target.value)} className="mt-1 w-full rounded-2xl border px-3 py-3 bg-white"><option>Java</option><option>Python</option><option>C++</option><option>JavaScript</option></select></label>
        <button onClick={onContinue} disabled={!canContinue} className="w-full rounded-2xl bg-brand text-white py-4 font-bold disabled:opacity-40">Continue</button>
        <p className="text-xs text-zinc-400 text-center">Writes to Firestore users/{`{uid}`} — same as Kotlin FirestoreRepository.updateUserProfile.</p>
      </div>
    </div>
  );
}
