"use client";
import { useState, useEffect } from "react";
import Link from "next/link";
import { apiGet, CourseDto } from "@/lib/api";

// Mirrors SearchResultsScreen.kt — Firestore courses + popularTopics filtered
export default function Search(){
  const [q,setQ]=useState(""); const [courses,setCourses]=useState<CourseDto[]>([]);
  useEffect(()=>{ apiGet<{data:CourseDto[]}>("/api/v1/courses?limit=50").then(r=>setCourses(r.data)).catch(()=> setCourses([{id:"1",title:"Java Basics Course",order:0} as any,{id:"2",title:"Python Fundamentals Course",order:1} as any])); },[]);
  const all=[...courses.map(c=> ({title:c.title, type:"course", id:c.id})), {title:"OOP Concepts Topic", type:"topic", id:"t1"}, {title:"Array Methods Topic", type:"topic", id:"t2"}];
  const filtered=all.filter(x=> x.title.toLowerCase().includes(q.toLowerCase()));
  return (
    <div className="space-y-4">
      <div className="flex gap-2 items-center"><Link href="/" className="text-sm">←</Link><input value={q} onChange={e=>setQ(e.target.value)} placeholder="Search courses, topics..." autoFocus className="flex-1 rounded-2xl border px-3 py-3 bg-white"/></div>
      <div className="space-y-2">
        {filtered.map(it=> (
          <Link key={it.title} href={it.type==="course"?`/courses/${it.id}`:"/learning-path"} className="flex gap-3 items-center neo bg-white p-4">
            <span className="w-10 h-10 rounded-xl bg-brand/10 grid place-items-center">{it.type==="course"?"🏫":"💡"}</span>
            <span className="font-medium">{it.title}</span>
          </Link>
        ))}
        {filtered.length===0 && <p className="text-sm text-zinc-500">No results</p>}
      </div>
      <p className="text-xs text-zinc-400">Searches same as Kotlin: filters courses + popularTopics in-memory; backend coming: GET /api/v1/search?q=</p>
    </div>
  );
}
