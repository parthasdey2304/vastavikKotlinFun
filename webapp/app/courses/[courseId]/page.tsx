"use client";
import { use, useEffect, useState } from "react";
import Link from "next/link";
import { apiGet, PartDto } from "@/lib/api";
export default function CoursePartsPage({ params }: { params: Promise<{ courseId: string }> }) {
  const { courseId } = use(params);
  const [parts, setParts] = useState<PartDto[]>([]);
  const [err, setErr] = useState<string|null>(null);
  useEffect(()=>{ apiGet<{data:PartDto[]}>(`/api/v1/courses/${courseId}/parts`).then(r=>setParts(r.data)).catch(e=>setErr(String(e))); },[courseId]);
  return (
    <div className="space-y-4">
      <Link href="/courses" className="text-sm text-brand">← Catalog</Link>
      <h1 className="text-xl font-bold">Parts — {courseId}</h1>
      {err && <p className="text-sm text-red-600 break-all">{err}</p>}
      {parts.length===0 && !err && <p className="text-sm text-zinc-500">No parts yet. Backend: GET /api/v1/courses/:courseId/parts.</p>}
      {parts.map(p=> (
        <div key={p.id} className="neo bg-white p-4">
          <p className="font-semibold">{p.title}</p><p className="text-xs text-zinc-500">{p.description}</p>
          <Link href={`/lessons/${p.id}?courseId=${courseId}&partId=${p.id}&subpartId=demo`} className="text-xs text-brand underline mt-2 inline-block">Open lessons →</Link>
        </div>
      ))}
    </div>
  );
}
