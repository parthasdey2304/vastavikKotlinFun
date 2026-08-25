"use client";
import { useSearchParams } from "next/navigation";
import { Suspense, useState } from "react";
export const dynamic = "force-dynamic";

// Mirrors CodeEditorScreen.kt:142 — language dropdown, code with line numbers, Run via Mistral, output
const defaults:Record<string,string>={
  Java:`public class Main {\n  public static void main(String[] args){\n    System.out.println("Hello Vastavik");\n  }\n}`,
  Python:`print("Hello Vastavik")`,
  JavaScript:`console.log("Hello Vastavik");`,
  SQL:`SELECT * FROM users WHERE isPremium = true;`,
};
function CodeEditorInner(){
  const sp=useSearchParams();
  const initLang=sp.get("language")||"Python";
  const [lang,setLang]=useState(initLang);
  const [code,setCode]=useState(sp.get("initialCode") ? decodeURIComponent(sp.get("initialCode")!) : defaults[initLang]||"");
  const [output,setOutput]=useState(""); const [running,setRunning]=useState(false);
  async function run(){
    setRunning(true); setOutput("");
    try{
      const key=process.env.NEXT_PUBLIC_MISTRAL_API_KEY || "";
      if(!key){ setOutput(`(demo) Output for ${lang}:\nHello Vastavik\n— set NEXT_PUBLIC_MISTRAL_API_KEY to run via Mistral`); return; }
      const prompt=`You are a code runner for Class 5-12. Language: ${lang}. Code:\n${code}\n\nSimulate execution, show output and brief explanation.`;
      const res=await fetch("https://api.mistral.ai/v1/chat/completions",{ method:"POST", headers:{"Content-Type":"application/json",Authorization:`Bearer ${key}`}, body: JSON.stringify({ model:"mistral-small-latest", messages:[{role:"user",content:prompt}], max_tokens:512, temperature:0.2 })});
      const j=await res.json(); setOutput(j.choices?.[0]?.message?.content || "No output");
    }catch(e:any){ setOutput("Error: "+e.message);} finally{ setRunning(false); }
  }
  return (
    <div className="flex flex-col h-[80vh]">
      <div className="flex justify-between items-center"><h1 className="font-bold">Code Editor</h1><select value={lang} onChange={e=>{ setLang(e.target.value); if(!code.trim()||Object.values(defaults).includes(code)) setCode(defaults[e.target.value]||""); }} className="rounded-full border px-3 py-2 text-sm bg-white"><option>Java</option><option>Python</option><option>JavaScript</option><option>SQL</option></select></div>
      <div className="mt-3 flex-1 flex bg-[#1E1E2E] rounded-2xl overflow-hidden">
        <div className="w-12 bg-[#252526] text-zinc-500 text-xs font-mono p-2 text-right select-none">{code.split("\n").map((_,i)=> <div key={i}>{i+1}</div>)}</div>
        <textarea value={code} onChange={e=>setCode(e.target.value)} className="flex-1 bg-transparent text-[#CDD6F4] font-mono text-sm p-3 outline-none resize-none" spellCheck={false}/>
      </div>
      <div className="mt-3 flex gap-2"><button onClick={run} disabled={running} className="flex-1 rounded-2xl bg-brand text-white py-3 font-bold disabled:opacity-50">{running?"Running…":"▶ Run"}</button><LinkCopy code={code}/></div>
      {(output||running) && <div className="mt-3 neo bg-white p-4"><p className="font-bold text-sm">Output</p><pre className="text-sm whitespace-pre-wrap mt-2 font-mono">{running?"Running…":output}</pre></div>}
    </div>
  );
}
function LinkCopy({code}:{code:string}){ return <button onClick={()=>{navigator.clipboard.writeText(code);}} className="rounded-2xl border px-4 py-3 text-sm">Copy</button>; }
export default function CodeEditor(){
  return <Suspense fallback={<div className="p-6">Loading editor…</div>}><CodeEditorInner/></Suspense>;
}
