"use client";
import { useEffect, useState } from "react";
import Link from "next/link";
import { apiGet, CourseDto } from "@/lib/api";
export default function CoursesPage() {
  const [courses, setCourses] = useState<CourseDto[]>([]);
  const [err, setErr] = useState<string|null>(null);
  useEffect(()=>{ apiGet<{data:CourseDto[]}>("/api/v1/courses?limit=50").then(r=>setCourses(r.data)).catch(e=>setErr(String(e))); },[]);
  return (
    <div className="space-y-4">
      <h1 className="text-xl font-bold">Course Catalog</h1>
      {err && <p className="text-sm text-red-600 break-all">{err}</p>}
      {courses.length===0 && !err && <p className="text-sm text-zinc-500">No published courses yet. Add one via admin-web :3000 or backend POST /admin/courses.</p>}
      <div className="grid gap-3">
        {courses.map(c=> (
          <Link key={c.id} href={`/courses/${c.id}`} className="neo bg-white p-4 flex justify-between items-center">
            <div><p className="font-semibold">{c.title}</p><p className="text-xs text-zinc-500 line-clamp-1">{c.description?.slice(0,120)}</p></div>
            <span className="text-brand">→</span>
          </Link>
        ))}
      </div>
    </div>
  );
}
