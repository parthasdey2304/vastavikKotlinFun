"use client";
import { useState, useEffect } from "react";
import Link from "next/link";
import { apiGet } from "@/lib/api";

// Mirrors PracticeScreen.kt — Tabs MCQs/Coding/PYQs, backend: quizzes/codingChallenges/pyqs
type Tab="mcq"|"coding"|"pyq";
export default function Practice(){
  const [tab,setTab]=useState<Tab>("mcq");
  const [quizzes,setQuizzes]=useState<any[]>([]);
  useEffect(()=>{ apiGet<{data:any[]}>("/api/v1/lessons?limit=5").catch(()=>{}); },[]);
  return (
    <div className="space-y-4">
      <h1 className="text-xl font-bold">Practice</h1>
      <div className="flex gap-2 bg-zinc-100 p-1 rounded-2xl w-fit">
        {(["mcq","coding","pyq"] as Tab[]).map(t=> <button key={t} onClick={()=>setTab(t)} className={`px-4 py-2 rounded-xl text-sm font-semibold ${tab===t?"bg-white shadow":"text-zinc-500"}`}>{t==="mcq"?"MCQs":t==="coding"?"Coding":"PYQs"}</button>)}
      </div>

      {tab==="mcq" && (
        <div className="space-y-3">
          {[["OOP Concepts","10 questions"],["Arrays & Lists","15 questions"],["Sorting","12 questions"],["File Handling","8 questions"]].map(([title,sub])=> (
            <button key={title} onClick={()=>window.location.href=`/quiz/setup/${encodeURIComponent(title)}`} className="w-full text-left neo bg-white p-4 flex justify-between items-center">
              <div className="flex gap-3 items-center"><span className="w-12 h-12 rounded-xl bg-brand/10 grid place-items-center">🧠</span><div><p className="font-semibold">{title}</p><p className="text-xs text-zinc-500">{sub}</p></div></div><span>›</span>
            </button>
          ))}
          <p className="text-xs text-zinc-400">Quizzes via <code className="bg-zinc-100 px-1">quizzes</code> collection — FirestoreRepository.streamQuizzes — backend coming: GET /api/v1/quizzes</p>
        </div>
      )}
      {tab==="coding" && (
        <div className="space-y-3">
          {[
            ["Reverse a String","Easy","Strings"],
            ["Two Sum","Medium","Arrays"],
            ["Merge Intervals","Hard","Intervals"],
          ].map(([t,diff,topic])=> (
            <Link key={t} href="/code-editor" className="block neo bg-white p-4">
              <div className="flex justify-between"><p className="font-semibold">{t}</p><span className={`text-xs px-2 py-1 rounded-full ${diff==="Easy"?"bg-green-50 text-green-700":diff==="Medium"?"bg-amber-50 text-amber-700":"bg-red-50 text-red-700"}`}>{diff}</span></div>
              <p className="text-xs text-zinc-500">{topic}</p>
            </Link>
          ))}
        </div>
      )}
      {tab==="pyq" && (
        <div className="space-y-3">
          {[["ICSE 2023","45 questions"],["CBSE 2022","50 questions"],["ICSE 2022","40 questions"]].map(([t,s])=> (
            <Link key={t} href="/pyq" className="block neo bg-white p-4 flex justify-between"><div><p className="font-semibold">{t}</p><p className="text-xs text-zinc-500">{s}</p></div><span>›</span></Link>
          ))}
        </div>
      )}
    </div>
  );
}
