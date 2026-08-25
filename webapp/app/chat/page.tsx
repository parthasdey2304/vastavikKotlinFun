"use client";
import { useState, useRef } from "react";

// Mirrors ChatScreen.kt — Vastavik AI via Mistral, suggestions, markdown, Open in Editor
const SYSTEM_PROMPT="You are Vastavik AI, a helpful programming tutor for Indian school students Class 5-12. Only answer programming/CS questions, politely refuse others. Be crisp, school-level, use code blocks.";
export default function Chat(){
  const [msgs,setMsgs]=useState<{role:"user"|"ai"; text:string}[]>([{role:"ai",text:"Hello! I am Vastavik AI. Ask me anything about Java/Python/JS/SQL."}]);
  const [input,setInput]=useState(""); const [loading,setLoading]=useState(false); const listRef=useRef<HTMLDivElement>(null);
  const suggestions=["Explain Code","Generate Quiz","Find Bug"];

  async function askMistral(prompt:string){
    setLoading(true);
    try{
      const res=await fetch("/api/chat",{ method:"POST", headers:{ "Content-Type":"application/json" }, body: JSON.stringify({ message: prompt })});
      const j=await res.json();
      if(!res.ok) throw new Error(j.error || "chat failed");
      const txt=j.text || "No reply";
      setMsgs(m=>[...m,{role:"ai",text:txt}]);
    }catch(e:any){ setMsgs(m=>[...m,{role:"ai",text:"Error: "+e.message}]);} finally{ setLoading(false); setTimeout(()=>listRef.current?.scrollTo(0,99999),50); }
  }
  function send(text:string){ const t=text.trim(); if(!t) return; setMsgs(m=>[...m,{role:"user",text:t}]); setInput(""); askMistral(t); }

  return (
    <div className="flex flex-col h-[75vh]">
      <div className="flex justify-between items-center"><h1 className="font-bold flex items-center gap-2">🤖 Vastavik AI <span className="text-xs bg-zinc-100 px-2 py-1 rounded-full">Mistral Small</span></h1><button onClick={()=>setMsgs([{role:"ai",text:"Hello! I am Vastavik AI..."}])} className="text-xs text-brand">+ New</button></div>
      <div className="mt-3 flex gap-2 overflow-x-auto pb-2">{suggestions.map(s=> <button key={s} onClick={()=>send(s)} className="shrink-0 rounded-full border bg-white px-3 py-2 text-sm">{s}</button>)}</div>
      <div ref={listRef} className="flex-1 overflow-y-auto space-y-3 mt-3 pr-1">
        {msgs.map((m,i)=> (
          <div key={i} className={`flex gap-2 ${m.role==="user"?"justify-end":""}`}>
            {m.role==="ai" && <div className="w-8 h-8 rounded-full bg-brand text-white grid place-items-center text-xs">AI</div>}
            <div className={`max-w-[75%] rounded-2xl px-3 py-2 text-sm whitespace-pre-wrap ${m.role==="user"?"bg-brand text-white rounded-br-sm":"bg-white border rounded-bl-sm"}`}>{m.text}</div>
            {m.role==="user" && <div className="w-8 h-8 rounded-full bg-zinc-900 text-white grid place-items-center text-xs">You</div>}
          </div>
        ))}
        {loading && <div className="text-xs text-zinc-500">Thinking…</div>}
      </div>
      <div className="mt-2 flex gap-2 items-end">
        <textarea value={input} onChange={e=>setInput(e.target.value)} placeholder="Ask anything (Java/Python/JS/SQL)…" className="flex-1 rounded-2xl border px-3 py-3 bg-white min-h-[48px] max-h-[120px]" rows={1} onKeyDown={e=>{ if(e.key==="Enter"&&!e.shiftKey){ e.preventDefault(); send(input); } }} />
        <button onClick={()=>send(input)} disabled={!input.trim()||loading} className="w-12 h-12 rounded-full bg-brand text-white grid place-items-center disabled:opacity-40">➤</button>
      </div>
      <p className="text-xs text-zinc-400 mt-2">Calls Mistral directly — same as Kotlin ChatScreen.callMistralApi — or via backend coming: POST /api/v1/chat</p>
    </div>
  );
}
