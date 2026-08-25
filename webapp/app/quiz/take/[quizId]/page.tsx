"use client";
import { use, useState, useEffect } from "react";
import Link from "next/link";
import { useSearchParams } from "next/navigation";
import { jsPDF } from "jspdf";
export const dynamic = "force-dynamic";

// Mirrors QuizTakingScreen.kt:36 — questions, progress, review, result, share
type Q={ q:string; o:string[]; a:number };
const demo:Q[]=[{q:"What is OOP?",o:["Object Oriented Programming","Operational Output","Option Oriented Process","Object Only Program"],a:0},{q:"Which keyword creates a class in Java?",o:["class","struct","object","define"],a:0},{q:"What is inheritance?",o:["Copying files","Class acquires properties of another","Looping","Compiling faster"],a:1}];
export default function QuizTake({ params }: { params: Promise<{ quizId: string }> }){
  const { quizId } = use(params); const sp=useSearchParams();
  const [qs,setQs]=useState<Q[]>(demo); const [idx,setIdx]=useState(0); const [sel,setSel]=useState<number|null>(null); const [answers,setAnswers]=useState<Record<number,number>>({}); const [showResult,setShowResult]=useState(false); const [generatingPdf,setGeneratingPdf]=useState<"questions"|"answers"|null>(null);
  useEffect(()=>{
    try{ const raw=sessionStorage.getItem("vastavik_quiz"); if(raw){ const arr=JSON.parse(raw); if(Array.isArray(arr) && arr.length) setQs(arr.map((x:any)=> ({ q:x.q, o:x.o, a:x.a }))); } }catch{}
  },[]);
  const score=Object.entries(answers).filter(([k,v])=> qs[Number(k)]?.a===v).length;

  function generateQuestionsPdf(){
    const doc=new jsPDF({ unit:"pt", format:"a4" });
    doc.setFont("helvetica", "bold"); doc.setFontSize(18); doc.text("Vastavik — Quiz Questions", 40, 50);
    doc.setFontSize(10); doc.setTextColor(100); doc.text(`Generated on ${new Date().toLocaleDateString()} • ${qs.length} questions`, 40, 65);
    let y=90;
    qs.forEach((q,i)=>{
      if(y>750){ doc.addPage(); y=40; }
      doc.setFont("helvetica", "bold"); doc.setFontSize(11); doc.setTextColor(0);
      const lines=doc.splitTextToSize(`${i+1}. ${q.q}`, 510);
      doc.text(lines, 40, y); y+=lines.length*14+4;
      doc.setFont("helvetica", "normal"); doc.setFontSize(10);
      q.o.forEach((opt,j)=>{
        if(y>750){ doc.addPage(); y=40; }
        doc.text(`   ${String.fromCharCode(65+j)}) ${opt}`, 50, y); y+=14;
      });
      y+=8;
    });
    doc.save(`vastavik-quiz-questions-${quizId}.pdf`);
  }

  async function generateAnswersPdf(){
    setGeneratingPdf("answers");
    try{
      const res=await fetch("/api/quiz/explanations",{ method:"POST", headers:{"Content-Type":"application/json"}, body: JSON.stringify({ questions:qs })});
      const j=await res.json();
      if(!res.ok) throw new Error(j.error || "Failed to get explanations");
      const explanations: string[] = j.explanations || [];
      const doc=new jsPDF({ unit:"pt", format:"a4" });
      doc.setFont("helvetica", "bold"); doc.setFontSize(18); doc.text("Vastavik — Quiz Questions with Answers & Explanations", 40, 50);
      doc.setFontSize(10); doc.setTextColor(100); doc.text(`Generated on ${new Date().toLocaleDateString()} • ${qs.length} questions`, 40, 65);
      let y=90;
      qs.forEach((q,i)=>{
        if(y>700){ doc.addPage(); y=40; }
        doc.setFont("helvetica", "bold"); doc.setFontSize(11); doc.setTextColor(0);
        const qLines=doc.splitTextToSize(`${i+1}. ${q.q}`, 510);
        doc.text(qLines, 40, y); y+=qLines.length*14+4;
        doc.setFont("helvetica", "normal"); doc.setFontSize(10);
        q.o.forEach((opt,j)=>{
          if(y>700){ doc.addPage(); y=40; }
          const isCorrect=j===q.a; const isUserAnswer=answers[i]===j;
          const prefix=isCorrect?"✓ ": isUserAnswer?"✗ ":"  ";
          const style=isCorrect?"bold":isUserAnswer?"bold":"normal";
          doc.setFont("helvetica", style);
          doc.setTextColor(isCorrect?0: isUserAnswer?200:0);
          doc.text(`   ${prefix}${String.fromCharCode(65+j)}) ${opt}`, 50, y); y+=14;
          doc.setFont("helvetica", "normal"); doc.setTextColor(0);
        });
        if(y>700){ doc.addPage(); y=40; }
        if(explanations[i]){
          doc.setFont("helvetica", "italic"); doc.setFontSize(9); doc.setTextColor(80);
          const expLines=doc.splitTextToSize(`Why: ${explanations[i]}`, 500);
          doc.text(expLines, 50, y); y+=expLines.length*12+8;
        }
        y+=6;
      });
      doc.save(`vastavik-quiz-answers-${quizId}.pdf`);
    }catch(e:any){ alert("Error: "+e.message); } finally{ setGeneratingPdf(null); }
  }

  if(showResult){
    return (
      <div className="p-6 text-center space-y-4 max-w-lg mx-auto">
        <div className="text-6xl">🏆</div><h1 className="text-2xl font-bold">Quiz Complete!</h1><p>You scored {score}/{qs.length}</p>
        <div className="w-full h-2 bg-zinc-200 rounded-full"><div className="h-2 bg-brand rounded-full" style={{ width: `${(score/qs.length)*100}%` }}/></div>
        <div className="grid gap-2">
          <Link href="/" className="rounded-2xl bg-brand text-white py-3 font-bold text-center">Back to Home</Link>
          <button onClick={generateQuestionsPdf} disabled={generatingPdf!=null} className="rounded-2xl border py-3">{generatingPdf==="questions"?"Generating PDF…":"Copy Questions as PDF"}</button>
          <button onClick={generateAnswersPdf} disabled={generatingPdf!=null} className="rounded-2xl border py-3">{generatingPdf==="answers"?"Generating with AI…":"View Answers with Explanations (PDF)"}</button>
          <button onClick={()=>{setShowResult(false); setIdx(0); setAnswers({});}} className="rounded-2xl border py-3">Retry Quiz</button>
        </div>
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
