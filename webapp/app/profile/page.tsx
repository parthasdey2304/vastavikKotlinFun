"use client";
import Link from "next/link";
import { auth } from "@/lib/firebase";
import { signOut } from "firebase/auth";
import { useRouter } from "next/navigation";

const menu=[
  ["Edit Profile","/edit-profile","✎","Manage info & avatar"],
  ["Select Course","/learning-path","◈","Choose your path"],
  ["Code Editor","/code-editor","</>","Practice live"],
  ["OCR Exercise","/ocr","◎","Scan & solve"],
  ["My Notes","/notes","≡","Your saved notes"],
  ["Notifications","/notifications","◐","Alerts & updates"],
  ["App Update","/app-update","↻","Version & changelog"],
  ["Payment History","/payment-history","—","Invoices & plans"],
  ["Settings","/settings","⚙","Theme & prefs"],
];
export default function Profile(){
  const r=useRouter(); const user=auth?.currentUser;
  async function logout(){ if(auth) await signOut(auth); r.push("/login"); }
  return (
    <div className="space-y-5 max-w-lg mx-auto">
      {/* Header */}
      <div className="neo overflow-hidden p-0">
        <div className="h-28 gradient-brand relative">
          <div className="absolute -right-6 -top-6 w-24 h-24 bg-white/10 rounded-full blur-xl" />
          <div className="absolute -left-6 -bottom-6 w-20 h-20 bg-white/10 rounded-full blur-xl" />
        </div>
        <div className="px-6 pb-6">
          <div className="w-20 h-20 rounded-2xl bg-white border-4 border-white shadow-soft grid place-items-center text-2xl mx-auto relative z-10 -mt-10">👤</div>
          <p className="mt-3 font-display font-extrabold text-lg leading-none text-center">{user?.displayName || "Student"}</p>
          <p className="text-sm text-muted text-center">{user?.email || "student@example.com"}</p>
          <div className="mt-4 grid grid-cols-2 gap-3">
            <div className="rounded-2xl bg-amber-50 dark:bg-amber-950/30 border border-amber-100 dark:border-amber-900 p-3 flex items-center gap-2.5"><span className="w-9 h-9 rounded-xl bg-white dark:bg-amber-900/50 border border-amber-100 dark:border-amber-800 grid place-items-center shrink-0">🔥</span><div className="min-w-0"><p className="font-bold leading-none">7 days</p><p className="text-xs text-muted">Day streak</p></div></div>
            <div className="rounded-2xl bg-violet-50 dark:bg-violet-950/30 border border-violet-100 dark:border-violet-900 p-3 flex items-center gap-2.5"><span className="w-9 h-9 rounded-xl bg-white dark:bg-violet-900/50 border border-violet-100 dark:border-violet-800 grid place-items-center shrink-0">📚</span><div className="min-w-0"><p className="font-bold leading-none">24</p><p className="text-xs text-muted">Lessons done</p></div></div>
          </div>
        </div>
      </div>

      <Link href="/payment" className="block neo p-0 overflow-hidden group">
        <div className="bg-gradient-to-br from-ink to-slate-800 p-4 flex gap-3 items-center text-white relative">
          <div className="w-11 h-11 rounded-xl bg-gradient-to-br from-amber-400 to-orange-500 grid place-items-center text-lg">★</div>
          <div className="flex-1 min-w-0"><p className="font-display font-bold leading-none">Upgrade to Premium</p><p className="text-xs opacity-70">Unlock all lessons + AI chat</p></div>
          <span className="w-8 h-8 rounded-full bg-white text-ink grid place-items-center group-hover:translate-x-0.5 transition">→</span>
        </div>
      </Link>

      <div className="neo divide-y overflow-hidden">
        {menu.map(([label,href,icon,desc])=> (
          <Link key={label} href={href} className="flex items-center gap-3 p-4 hover:bg-slate-50 dark:hover:bg-zinc-700/50 transition">
            <span className="w-9 h-9 rounded-xl bg-slate-50 dark:bg-zinc-700 border border-slate-200 dark:border-zinc-600 grid place-items-center text-sm">{icon}</span>
            <span className="flex-1 min-w-0"><span className="font-medium text-sm leading-none">{label}</span><span className="block text-xs text-muted">{desc}</span></span>
            <span className="text-muted">›</span>
          </Link>
        ))}
        <button onClick={logout} className="w-full flex items-center gap-3 p-4 hover:bg-red-50 dark:hover:bg-red-950/30 transition text-red-600 text-left">
          <span className="w-9 h-9 rounded-xl bg-red-50 border border-red-100 grid place-items-center text-sm">↗</span>
          <span className="flex-1 font-medium text-sm">Log Out</span><span>›</span>
        </button>
      </div>
      <p className="text-xs text-muted text-center px-4">Profile from Firestore <code>users/{"{uid}"}</code> — mirrors Kotlin ProfileViewModel.</p>
    </div>
  );
}
