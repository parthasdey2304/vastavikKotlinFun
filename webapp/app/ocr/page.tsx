"use client";
import { useState } from "react";

// Mirrors OcrExerciseScreen.kt:33 — Type Code vs Photo OCR tabs
export default function OCR(){
  const [tab,setTab]=useState<"code"|"photo">("code");
  const [code,setCode]=useState(""); const [ai,setAi]=useState("");
  const [ocrText,setOcrText]=useState("");
  return (
    <div className="space-y-4">
      <h1 className="text-xl font-bold">OCR Exercise</h1>
      <div className="flex gap-2 bg-zinc-100 p-1 rounded-2xl w-fit">
        <button onClick={()=>setTab("code")} className={`px-4 py-2 rounded-xl text-sm ${tab==="code"?"bg-white shadow":""}`}>Type Code</button>
        <button onClick={()=>setTab("photo")} className={`px-4 py-2 rounded-xl text-sm ${tab==="photo"?"bg-white shadow":""}`}>Photo OCR</button>
      </div>
      {tab==="code" && (
        <div className="space-y-3">
          <p className="text-sm text-zinc-500">Write code and let AI review (chat format):</p>
          <textarea value={code} onChange={e=>setCode(e.target.value)} placeholder="public class Main {...}" className="w-full rounded-2xl border p-3 font-mono text-sm min-h-[140px] bg-white"/>
          <button onClick={()=>setAi("Gemini 3.7 Flash review: Good structure! Consider adding null checks and comments. — (demo, wire ML Kit + Gemini via backend POST /api/v1/ai/review)")} className="w-full rounded-2xl bg-brand text-white py-3 font-bold">Ask Gemini to Review</button>
          {ai && <div className="neo bg-white p-4 text-sm">{ai}</div>}
        </div>
      )}
      {tab==="photo" && (
        <div className="space-y-3">
          <div className="flex gap-2"><label className="flex-1 rounded-2xl border bg-white p-3 text-center text-sm cursor-pointer">Pick Image<input type="file" accept="image/*" className="hidden" onChange={()=>setOcrText("public class Main {\n  public static void main(String[] args){\n    System.out.println(\"Hello\");\n  }\n}")}/></label><button onClick={()=>setOcrText("def hello():\n  print('Hello from camera')")} className="flex-1 rounded-2xl border bg-white p-3 text-sm">Camera</button></div>
          {ocrText ? (<><textarea value={ocrText} onChange={e=>setOcrText(e.target.value)} className="w-full rounded-2xl border p-3 font-mono text-sm min-h-[120px] bg-white"/><button onClick={()=>setAi("Gemini says: Extracted code looks good — runs as expected.")} className="w-full rounded-2xl bg-brand text-white py-3 font-bold">Send to Gemini</button></>) : (<p className="text-sm text-zinc-500">Take or pick an image — OCR via ML Kit (demo). Gemini 3.7 Flash explains.</p>)}
          {ai && <div className="neo bg-white p-4 text-sm">{ai}</div>}
        </div>
      )}
    </div>
  );
}
