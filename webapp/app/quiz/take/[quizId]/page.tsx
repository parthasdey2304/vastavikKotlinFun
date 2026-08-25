"use client";
import { use, useState, useEffect, Suspense } from "react";
import Link from "next/link";
import { useSearchParams } from "next/navigation";
export const dynamic = "force-dynamic";

// Mirrors QuizTakingScreen.kt:36 — questions, progress, review, result, share
type Q={ q:string; o:string[]; a:number };
const demo:Q[]=[{q:"What is OOP?",o:["Object Oriented Programming","Operational Output","Option Oriented Process","Object Only Program"],a:0},{q:"Which keyword creates a class in Java?",o:["class","struct","object","define"],a:0},{q:"What is inheritance?",o:["Copying files","Class acquires properties of another","Looping","Compiling faster"],a:1}];
export default function QuizTake({ params }: { params: Promise<{ quizId: string }> }){
  const { quizId } = use(params); const sp=useSearchParams();
  const [qs,setQs]=useState<Q[]>(demo); const [idx,setIdx]=useState(0); const [sel,setSel]=useState<number|null>(null); const [answers,setAnswers]=useState<Record<number,number>>({}); const [showResult,setShowResult]=useState(false);
  useEffect(()=>{
    try{ const raw=sessionStorage.getItem("vastavik_quiz"); if(raw){ const arr=JSON.parse(raw); if(Array.isArray(arr) && arr.length) setQs(arr.map((x:any)=> ({ q:x.q, o:x.o, a:x.a }))); } }catch{}
  },[]);
  const score=Object.entries(answers).filter(([k,v])=> qs[Number(k)]?.a===v).length;
  if(showResult){
    return (
      <div className="p-6 text-center space-y-4 max-w-lg mx-auto">
        <div className="text-6xl">🏆</div><h1 className="text-2xl font-bold">Quiz Complete!</h1><p>You scored {score}/{qs.length}</p>
        <div className="w-full h-2 bg-zinc-200 rounded-full"><div className="h-2 bg-brand rounded-full" style={{ width: `${(score/qs.length)*100}%` }}/></div>
        <div className="grid gap-2"><Link href="/" className="rounded-2xl bg-brand text-white py-3 font-bold text-center">Back to Home</Link><button onClick={()=>{ const txt=qs.map((q,i)=>`${i+1}. ${q.q}\n   ${q.o.map((o,j)=> (j===q.a?"✓ ":"  ")+o).join("\n   ")}`).join("\n\n"); navigator.clipboard.writeText(txt); alert("Copied!"); }} className="rounded-2xl border py-3">Copy Questions</button><button onClick={()=>{setShowResult(false); setIdx(0);}} className="rounded-2xl border py-3">Review Answers</button></div>
      </div>
    );
  }
  const q=qs[idx];
  return (
    <div className="p-4 max-w-lg mx-auto">
      <div className="flex justify-between text-sm"><span>Question {idx+1}/{qs.length}</span><span className="text-brand font-bold">{Math.round(((idx)/qs.length)*100)}%</span></div>
      <div className="mt-2 h-2 bg-zinc-200 rounded-full"><div className="h-2 bg-brand rounded-full" style={{ width: `${((idx)/qs.length)*100}%` }}/></div>
      <div className="mt-6 neo bg-white p-4"><p className="font-bold">{q.q}</p></div>
      <div className="mt-4 space-y-3">
        {q.o.map((opt,i)=> (
          <button key={i} onClick={()=>setSel(i)} className={`w-full text-left neo p-4 flex gap-3 items-center ${sel===i?"bg-brand/10 border-brand":"bg-white"}`}>
            <span className={`w-7 h-7 rounded-full grid place-items-center text-xs border ${sel===i?"bg-brand text-white":"bg-zinc-100"}`}>{String.fromCharCode(65+i)}</span><span className="text-sm">{opt}</span>
          </button>
        ))}
      </div>
      <div className="mt-6 flex gap-3">
        <button onClick={()=>setIdx(i=>Math.max(0,i-1))} disabled={idx===0} className="flex-1 rounded-2xl border py-3 disabled:opacity-40">Previous</button>
        <button onClick={()=>{
          if(sel===null) return;
          const na={...answers,[idx]:sel}; setAnswers(na); setSel(null);
          if(idx===qs.length-1){ setShowResult(true); } else setIdx(idx+1);
        }} disabled={sel===null} className="flex-1 rounded-2xl bg-brand text-white py-3 font-bold disabled:opacity-40">{idx===qs.length-1?"Submit":"Next"}</button>
      </div>
    </div>
  );
}
