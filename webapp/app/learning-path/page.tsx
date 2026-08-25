"use client";
import { useEffect, useState } from "react";
import Link from "next/link";
import { apiGet, CourseDto, PartDto } from "@/lib/api";
import { getIdToken } from "@/lib/firebase";

// Mirrors LearningPathScreen.kt:34 — zigzag nodes, course tabs, unit header, bottom sheet subparts
export default function LearningPath(){
  const [courses,setCourses]=useState<CourseDto[]>([]);
  const [selected,setSelected]=useState<string>("");
  const [parts,setParts]=useState<PartDto[]>([]);
  const [sheetPart,setSheetPart]=useState<PartDto|null>(null);
  const offsets=[0,0.4,0.8,0.4,0,-0.4,-0.8,-0.4];
  const nodes=["Introduction","Variables","Control Flow","Functions","OOP Basics","Collections","File I/O","Project","Final Project"];

  useEffect(()=>{ (async()=>{
    const token=await getIdToken(); try{ const r=await apiGet<{data:CourseDto[]}>("/api/v1/courses?limit=20",token); setCourses(r.data); if(r.data[0]) setSelected(r.data[0].id); }catch{ const stub:CourseDto[]=[{id:"1",title:"Java",order:0,description:"Java Fundamentals"},{id:"2",title:"Python",order:1},{id:"3",title:"C++",order:2},{id:"4",title:"Web Dev",order:3}]; setCourses(stub); setSelected("1");}
  })(); },[]);
  useEffect(()=>{ if(!selected) return; (async()=>{ const t=await getIdToken(); try{ const r=await apiGet<{data:PartDto[]}>(`/api/v1/courses/${selected}/parts`,t); setParts(r.data);}catch{ setParts([{id:"1",title:"Introduction",order:0},{id:"2",title:"Variables",order:1},{id:"3",title:"Control Flow",order:2}]);} })(); },[selected]);

  return (
    <div className="space-y-4">
      <div className="flex justify-between items-center"><h1 className="text-xl font-bold">Learning Path</h1><Link href="/profile" className="w-10 h-10 rounded-full bg-brand text-white grid place-items-center">👤</Link></div>
      <div className="flex gap-2 overflow-x-auto pb-1">{courses.map(c=> <button key={c.id} onClick={()=>setSelected(c.id)} className={`px-4 py-2 rounded-full text-sm font-semibold border ${selected===c.id?"bg-brand text-white border-brand":"bg-white"}`}>{c.title}</button>)}</div>
      <div className="neo bg-brand text-white p-4"><p className="text-xs opacity-80">Unit 1</p><p className="text-lg font-bold">Java Fundamentals</p><p className="text-sm opacity-90">Master the basics of Java programming</p></div>
      <div className="relative">
        {nodes.map((n,i)=>{
          const off=offsets[i%offsets.length];
          const isDone=i<3; const isCurrent=i===3;
          return (
            <div key={n} className="flex flex-col items-center my-6" style={{ marginLeft: `${50+off*20}%`, transform:"translateX(-50%)" }}>
              <div className="h-6 w-1 bg-brand/20" style={{ visibility: i===0?"hidden":undefined }} />
              <button onClick={()=>setSheetPart({id:String(i),title:n,order:i})} className={`w-20 h-20 rounded-full grid place-items-center text-xl border-4 ${isDone?"bg-brand text-white border-brand": isCurrent?"bg-white border-brand scale-110 shadow-lg":"bg-zinc-200 text-zinc-400 border-zinc-200"}`}>{i===nodes.length-1?"🏆":"⭐"}</button>
              <p className="mt-2 text-xs font-semibold text-center max-w-[120px]">{n}</p>
            </div>
          );
        })}
      </div>
      <p className="text-xs text-zinc-400 text-center">Fetching parts via <code className="bg-zinc-100 px-1">GET /api/v1/courses/:courseId/parts</code> with HMAC+IdToken — lib/api.ts — same as Kotlin LearningViewModel.streamParts.</p>

      {sheetPart && (
        <div className="fixed inset-0 bg-black/40 grid place-items-end z-50" onClick={()=>setSheetPart(null)}>
          <div onClick={e=>e.stopPropagation()} className="bg-white w-full rounded-t-3xl p-6 space-y-3 max-w-3xl mx-auto">
            <div className="w-10 h-1 bg-zinc-300 rounded-full mx-auto"/>
            <h3 className="font-bold">{sheetPart.title}</h3>
            {["Video Lesson","Practice Quiz","Coding Exercise","Notes"].map(s=> (
              <Link key={s} href={`/lessons/demo?courseId=${selected}&partId=${sheetPart.id}&subpartId=1`} onClick={()=>setSheetPart(null)} className="block neo bg-zinc-50 p-4 flex justify-between"><span>{s}</span><span>→</span></Link>
            ))}
            <button onClick={()=>setSheetPart(null)} className="w-full py-3 rounded-2xl bg-zinc-100">Close</button>
          </div>
        </div>
      )}
    </div>
  );
}
