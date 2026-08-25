"use client";
import { use, useEffect, useState } from "react";
import { apiGet, LessonDto } from "@/lib/api";
import { VastavikPlayer } from "@/components/VastavikPlayer";
export default function LessonPage({ params, searchParams }: { params: Promise<{ lessonId: string }>; searchParams: Promise<Record<string,string>> }) {
  const { lessonId } = use(params);
  const sp = use(searchParams);
  const [lesson, setLesson] = useState<LessonDto|null>(null);
  const [err, setErr] = useState<string|null>(null);
  useEffect(()=>{ apiGet<{data:LessonDto}>(`/api/v1/lessons/${lessonId}`).then(r=>setLesson(r.data)).catch(e=>setErr(String(e))); },[lessonId]);
  if (err) return <p className="text-sm text-red-600 break-all p-4">{err}</p>;
  if (!lesson) return <div className="w-full aspect-video bg-black animate-pulse rounded-b-2xl" />;
  return (
    <div className="space-y-4">
      <VastavikPlayer youtubeUrl={lesson.youtubeUrl} youtubeVideoId={lesson.youtubeVideoId} title={lesson.title} />
      <div className="flex gap-2 text-xs">
        {lesson.duration && <span className="bg-brand/10 text-brand px-2 py-1 rounded-full">{lesson.duration}</span>}
        {lesson.videoFormat && <span className="bg-zinc-100 px-2 py-1 rounded-full">{lesson.videoFormat.toUpperCase()}</span>}
        {lesson.isPremium && <span className="bg-amber-100 text-amber-700 px-2 py-1 rounded-full font-bold">PREMIUM</span>}
      </div>
      <h1 className="text-xl font-bold">{lesson.title}</h1>
      <p className="text-sm text-zinc-600 leading-6">{lesson.description}</p>
      {lesson.codeSample && <pre className="neo bg-[#0f172a] text-emerald-200 p-4 overflow-x-auto text-sm">{lesson.codeSample}</pre>}
      {lesson.notes && <div className="neo bg-white p-4"><h3 className="font-bold">Notes</h3><p className="text-sm text-zinc-600 mt-2">{lesson.notes}</p></div>}
      {lesson.whiteboardImageUrl && <img src={lesson.whiteboardImageUrl} alt="Whiteboard" className="neo bg-white w-full object-contain" />}
      <p className="text-xs text-zinc-400">Params: course {sp.courseId} • part {sp.partId} • subpart {sp.subpartId} • via GET /api/v1/lessons/:lessonId — same as Kotlin VastavikYouTubePlayer.</p>
    </div>
  );
}
