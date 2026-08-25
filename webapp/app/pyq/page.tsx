"use client";
import { useState } from "react";

// Mirrors PYQScreen.kt — board/year filters, pyqs collection
const all=[
  {id:"1", title:"ICSE 2023 — Computer Applications", board:"ICSE", year:"2023", qs:"45 questions", subject:"CS"},
  {id:"2", title:"CBSE 2022 — Informatics Practices", board:"CBSE", year:"2022", qs:"50 questions", subject:"CS"},
  {id:"3", title:"ICSE 2022 — Java Paper", board:"ICSE", year:"2022", qs:"40 questions", subject:"Java"},
  {id:"4", title:"CBSE 2021 — Python Paper", board:"CBSE", year:"2021", qs:"48 questions", subject:"Python"},
];
export default function PYQ(){
  const [board,setBoard]=useState("All"); const [year,setYear]=useState("All");
  const filtered=all.filter(x=> (board==="All"||x.board===board) && (year==="All"||x.year===year));
  return (
    <div className="space-y-4">
      <h1 className="text-xl font-bold">Past Year Questions (PYQ)</h1>
      <div className="flex gap-2">
        <select value={board} onChange={e=>setBoard(e.target.value)} className="flex-1 rounded-2xl border px-3 py-3 bg-white"><option>All</option><option>ICSE</option><option>CBSE</option></select>
        <select value={year} onChange={e=>setYear(e.target.value)} className="flex-1 rounded-2xl border px-3 py-3 bg-white"><option>All</option><option>2023</option><option>2022</option><option>2021</option><option>2020</option></select>
      </div>
      <div className="space-y-3">
        {filtered.map(p=> (
          <div key={p.id} className="neo bg-white p-4 flex gap-3 items-center">
            <span className="w-12 h-12 rounded-xl bg-brand/10 grid place-items-center">📄</span>
            <div className="flex-1"><p className="font-semibold">{p.title}</p><p className="text-xs text-zinc-500">{p.subject} • {p.qs}</p></div><span>›</span>
          </div>
        ))}
      </div>
      <p className="text-xs text-zinc-400">Hardcoded like Kotlin — should be Firestore pyqs order year desc — streaming via backend coming: GET /api/v1/pyqs</p>
    </div>
  );
}
