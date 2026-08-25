"use client";
import { use, useState } from "react";
import { useRouter } from "next/navigation";

// Mirrors QuizSetupScreen.kt:22 — topic, count 10/20/30, difficulty, Generate via Mistral/QuizManager
export default function QuizSetup({ params }: { params: Promise<{ topic: string }> }){
  const { topic } = use(params); const decoded=decodeURIComponent(topic);
  const [count,setCount]=useState(10); const [diff,setDiff]=useState<"Easy"|"Medium"|"Hard">("Medium"); const [loading,setLoading]=useState(false); const [err,setErr]=useState<string|null>(null);
  const r=useRouter();
  async function gen(){
    setLoading(true); setErr(null);
    try{
      const res=await fetch("/api/quiz/generate",{ method:"POST", headers:{"Content-Type":"application/json"}, body: JSON.stringify({ topic:decoded, count, difficulty:diff })});
      const j=await res.json();
      if(!res.ok) throw new Error(j.error || "Quiz generation failed");
      const questions=j.questions || [];
      if(!questions.length) throw new Error("No questions returned from Mistral");
      sessionStorage.setItem("vastavik_quiz", JSON.stringify(questions));
      r.push(`/quiz/take/generated?topic=${encodeURIComponent(decoded)}`);
    }catch(e:any){ setErr(e.message);} finally{ setLoading(false); }
  }
  return (
    <div className="p-4 space-y-6 max-w-lg mx-auto">
      <h1 className="text-xl font-bold">Quiz Setup — {decoded}</h1>
      <div className="neo bg-white p-4"><p className="font-semibold">Topic: {decoded}</p></div>
      <div><p className="text-sm font-semibold">Number of questions</p><div className="mt-2 flex gap-2">{[10,20,30].map(n=> <button key={n} onClick={()=>setCount(n)} className={`px-4 py-2 rounded-full border ${count===n?"bg-brand text-white":"bg-white"}`}>{n}</button>)}</div></div>
      <div><p className="text-sm font-semibold">Difficulty</p><div className="mt-2 flex gap-2">{(["Easy","Medium","Hard"] as const).map(d=> <button key={d} onClick={()=>setDiff(d)} className={`px-4 py-2 rounded-full border flex gap-1 items-center ${diff===d?"bg-brand text-white":"bg-white"}`}>{d==="Medium"?"●":d==="Easy"?"○":"◆"} {d}</button>)}</div></div>
      {err && <p className="text-sm text-red-600">{err}</p>}
      <button onClick={gen} disabled={loading} className="w-full rounded-2xl bg-brand text-white py-4 font-bold disabled:opacity-50">{loading?`Generating ${count} questions via Mistral…`:"Generate Quiz"}</button>
      <p className="text-xs text-zinc-400">Powered by Mistral AI — generates exactly {count} MCQs via server API route. Same as Kotlin QuizManager.</p>
    </div>
  );
}
