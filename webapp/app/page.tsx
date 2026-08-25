// Vastavik Web — Stitch redesign — web only, kotlin-app untouched
"use client";
import { useEffect, useState } from "react";
import Link from "next/link";
import { apiGet, CourseDto, API_BASE } from "@/lib/api";

export default function HomePage() {
  const [courses, setCourses] = useState<CourseDto[]>([]);
  const [err, setErr] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    apiGet<{data: CourseDto[]}>("/api/v1/courses?limit=20").then(r => setCourses(r.data)).catch(e => setErr(String(e).slice(0,200))).finally(()=>setLoading(false));
  }, []);

  const sampleCourses = [
    { title: "Java Programming", color: "from-violet-500 to-indigo-600", icon: "☕", lessons: "42 lessons" },
    { title: "Python Basics", color: "from-emerald-500 to-teal-600", icon: "🐍", lessons: "36 lessons" },
    { title: "Data Structures", color: "from-amber-500 to-orange-600", icon: "◈", lessons: "28 lessons" },
    { title: "Web Development", color: "from-cyan-500 to-blue-600", icon: "</>", lessons: "51 lessons" },
  ];
  const display = courses.length
    ? courses.map((c,i)=>({ title: c.title, color: sampleCourses[i%4].color, icon: sampleCourses[i%4].icon, lessons: `${12+i*3} lessons` }))
    : sampleCourses;

  return (
    <div className="space-y-6">
      {/* Hero */}
      <section className="neo overflow-hidden border-0">
        <div className="gradient-brand p-6 md:p-8 text-white relative overflow-hidden">
          <div className="absolute -right-12 -top-12 w-40 h-40 bg-white/10 rounded-full blur-2xl" />
          <div className="absolute right-8 bottom-0 w-24 h-24 bg-cyan-300/20 rounded-full blur-xl" />
          <div className="relative flex justify-between gap-4">
            <div>
              <p className="text-xs font-semibold tracking-widest opacity-80 uppercase">Welcome back</p>
              <h1 className="font-display text-[28px] md:text-3xl font-extrabold leading-none mt-1">Hello, Student 👋</h1>
              <p className="text-sm opacity-90 mt-1.5 max-w-[28ch]">Ready to write some code? Pick up where you left off.</p>
            </div>
            <div className="hidden sm:flex gap-2 self-start">
              <Link href="/notifications" className="w-10 h-10 rounded-full bg-white/15 backdrop-blur grid place-items-center hover:bg-white/25 transition">🔔</Link>
              <Link href="/profile" className="w-10 h-10 rounded-full bg-white text-ink grid place-items-center font-bold">S</Link>
            </div>
          </div>
          <div className="relative mt-5 flex gap-2">
            <div className="flex-1 flex items-center gap-3 bg-white rounded-full px-4 py-3 shadow-soft">
              <span className="text-muted text-sm">⌕</span>
              <input placeholder="Search courses, topics, lessons…" className="flex-1 outline-none text-sm text-ink placeholder:text-muted bg-transparent" />
            </div>
            <Link href="/search" className="hidden md:inline-flex items-center px-5 rounded-full bg-ink text-white text-sm font-semibold hover:bg-black transition">Search</Link>
          </div>
        </div>
        <div className="bg-white dark:bg-zinc-800 px-6 py-3 flex items-center gap-4 text-xs">
          <span className="inline-flex items-center gap-1.5 font-medium"><span className="w-2 h-2 rounded-full bg-emerald-500"/> 7 day streak</span>
          <span className="text-slate-300">|</span>
          <span className="text-muted">65% avg progress</span>
          <span className="ml-auto hidden sm:inline text-muted">Same backend :3001 → Firestore</span>
        </div>
      </section>

      {/* Continue Learning */}
      <section>
        <div className="flex items-baseline justify-between px-1">
          <h2 className="font-display font-bold text-[17px]">Continue Learning</h2>
          <Link href="/learning-path" className="text-xs font-semibold text-brand hover:underline">View all →</Link>
        </div>
        <Link href="/lessons/1?courseId=1&partId=1&subpartId=1" className="mt-3 block neo overflow-hidden p-0 group">
          <div className="bg-gradient-to-br from-ink to-slate-800 dark:from-zinc-900 dark:to-zinc-800 p-5 md:p-6 text-white relative overflow-hidden">
            <div className="absolute -right-10 -bottom-10 w-40 h-40 bg-brand/20 rounded-full blur-2xl group-hover:bg-brand/30 transition" />
            <div className="flex items-center gap-3">
              <span className="w-10 h-10 rounded-xl bg-white/10 border border-white/10 grid place-items-center text-sm font-bold">{"</>"}</span>
              <span className="text-sm font-semibold tracking-wide opacity-90">Java Programming</span>
              <span className="ml-auto text-[11px] px-2 py-1 rounded-full bg-white/15 border border-white/10">In progress</span>
            </div>
            <p className="mt-4 font-display font-bold text-[18px] leading-tight max-w-[18ch]">Object-Oriented Programming — OOP Concepts</p>
            <p className="text-xs opacity-60 mt-1">Lesson 6 of 12 • 18 min</p>
            <div className="mt-5">
              <div className="h-1.5 bg-white/15 rounded-full overflow-hidden"><div className="h-full w-[65%] bg-white rounded-full" /></div>
              <div className="mt-3 flex justify-between items-center">
                <span className="text-xs opacity-70">Progress 65%</span>
                <span className="bg-white text-ink px-5 py-2 rounded-full text-sm font-bold group-hover:scale-[1.02] transition">Continue →</span>
              </div>
            </div>
          </div>
        </Link>
      </section>

      {/* Catalog */}
      <section>
        <div className="flex items-baseline justify-between px-1">
          <h2 className="font-display font-bold text-[17px]">Course Catalog</h2>
          <Link href="/courses" className="text-xs font-semibold text-brand hover:underline">Browse all →</Link>
        </div>
        {loading && <p className="text-sm text-muted px-1 mt-2">Loading from backend…</p>}
        {err && <p className="text-xs text-amber-600 px-1 mt-2 break-all">Backend offline (samples): {err}</p>}
        <div className="mt-3 grid grid-cols-2 md:grid-cols-4 gap-3">
          {display.map(c => (
            <Link key={c.title} href="/courses" className="neo p-4 flex flex-col gap-3 hover:shadow-card-hover hover:-translate-y-0.5 group">
              <div className={`w-11 h-11 rounded-xl bg-gradient-to-br ${c.color} grid place-items-center text-white shadow-soft group-hover:scale-105 transition`}>{c.icon}</div>
              <div>
                <p className="font-display font-bold text-sm leading-tight line-clamp-1">{c.title}</p>
                <p className="text-xs text-muted mt-1">{c.lessons}</p>
              </div>
              <span className="text-xs font-semibold text-brand mt-auto">Explore →</span>
            </Link>
          ))}
        </div>
      </section>

      {/* Stats + dev card */}
      <section className="grid md:grid-cols-3 gap-3">
        <div className="neo p-4 flex items-center gap-3">
          <div className="w-10 h-10 rounded-xl bg-violet-50 text-violet-600 grid place-items-center border border-violet-100">◐</div>
          <div><p className="font-bold leading-none">12.4k</p><p className="text-xs text-muted">Active learners</p></div>
        </div>
        <div className="neo p-4 flex items-center gap-3">
          <div className="w-10 h-10 rounded-xl bg-emerald-50 text-emerald-600 grid place-items-center border border-emerald-100">✓</div>
          <div><p className="font-bold leading-none">500+</p><p className="text-xs text-muted">Hands-on lessons</p></div>
        </div>
        <div className="neo p-4 flex items-center gap-3">
          <div className="w-10 h-10 rounded-xl bg-amber-50 text-amber-600 grid place-items-center border border-amber-100">✦</div>
          <div><p className="font-bold leading-none">4.8/5</p><p className="text-xs text-muted">Avg rating</p></div>
        </div>
      </section>

      <section className="neo p-5 flex gap-4 items-start">
        <div className="w-9 h-9 rounded-xl bg-ink text-white grid place-items-center shrink-0 font-bold text-sm">≋</div>
        <div className="min-w-0">
          <h3 className="font-display font-bold text-sm">Same backend as Android — live sync</h3>
          <p className="text-sm text-muted mt-1 leading-relaxed">Hits <code className="bg-slate-100 dark:bg-zinc-700 px-1.5 py-0.5 rounded text-xs">GET /api/v1/courses</code> with Firebase ID token + <code className="bg-slate-100 dark:bg-zinc-700 px-1.5 py-0.5 rounded text-xs">x-api-key</code> HMAC. Add a course in admin :3000, it appears here + Kotlin app instantly.</p>
          <p className="text-[11px] text-muted/70 mt-2 break-all font-mono">{API_BASE || "http://localhost:3001"}/api/v1/courses</p>
        </div>
      </section>
    </div>
  );
}
