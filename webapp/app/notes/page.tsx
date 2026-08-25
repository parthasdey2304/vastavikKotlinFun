"use client";
import { useEffect, useState } from "react";
import Link from "next/link";
import { collection, query, orderBy, onSnapshot, addDoc, deleteDoc, doc, serverTimestamp } from "firebase/firestore";
import { auth, db } from "@/lib/firebase";

// Mirrors MyNotesScreen.kt — users/{uid}/notes
export default function Notes(){
  const [notes,setNotes]=useState<any[]>([]); const [show,setShow]=useState(false); const [title,setTitle]=useState("");
  useEffect(()=>{
    const uid=auth?.currentUser?.uid; if(!uid || !db){ setNotes([{id:"1",title:"OOP Notes",content:"Inheritance, polymorphism"},{id:"2",title:"Array Methods",content:"push, pop, slice"}]); return; }
    const q=query(collection(db,"users",uid,"notes"), orderBy("createdAt","desc"));
    const unsub=onSnapshot(q, snap=> setNotes(snap.docs.map(d=>({id:d.id, ...d.data()}))), ()=>{});
    return ()=>unsub();
  },[]);
  async function create(){
    if(!title.trim()) return;
    const uid=auth?.currentUser?.uid;
    if(uid && db){ await addDoc(collection(db,"users",uid,"notes"), { title: title.trim(), content:"", userId: uid, createdAt: serverTimestamp() }); }
    else { setNotes(n=>[{id:Date.now().toString(),title:title.trim(),content:""},...n]); }
    setTitle(""); setShow(false);
  }
  async function del(id:string){
    const uid=auth?.currentUser?.uid;
    if(uid && db) await deleteDoc(doc(db,"users",uid,"notes",id));
    else setNotes(n=>n.filter(x=>x.id!==id));
  }
  return (
    <div className="space-y-4">
      <div className="flex justify-between items-center"><Link href="/profile" className="text-sm text-brand">← Profile</Link><button onClick={()=>setShow(true)} className="rounded-full bg-brand text-white px-4 py-2 text-sm font-bold">+ Add</button></div>
      <h1 className="text-xl font-bold">My Notes</h1>
      {notes.length===0 ? (<div className="text-center py-12"><div className="text-5xl opacity-30">📝</div><p className="text-sm text-zinc-500">No notes yet</p></div>) : (
        <div className="space-y-3">{notes.map(n=> (
          <div key={n.id} className="neo bg-white p-4 flex justify-between gap-3">
            <div className="flex gap-3"><span className="w-10 h-10 rounded-xl bg-brand/10 grid place-items-center">📝</span><div><p className="font-semibold">{n.title}</p><p className="text-xs text-zinc-500 line-clamp-1">{n.content}</p></div></div>
            <button onClick={()=>del(n.id)} className="text-red-600 text-sm">Delete</button>
          </div>
        ))}</div>
      )}
      {show && (
        <div className="fixed inset-0 bg-black/40 grid place-items-center z-50" onClick={()=>setShow(false)}>
          <div onClick={e=>e.stopPropagation()} className="bg-white neo p-6 w-[90%] max-w-sm space-y-3">
            <h3 className="font-bold">Create Note</h3><input value={title} onChange={e=>setTitle(e.target.value)} placeholder="Note title" className="w-full rounded-2xl border px-3 py-3"/><div className="flex gap-2"><button onClick={()=>setShow(false)} className="flex-1 rounded-2xl border py-3">Cancel</button><button onClick={create} className="flex-1 rounded-2xl bg-brand text-white py-3 font-bold">Create</button></div>
          </div>
        </div>
      )}
    </div>
  );
}
