"use client";
import { useState, useEffect } from "react";
import Link from "next/link";

const accents=["#FFE500","#FF2D78","#0066FF","#00FF66","#FF6600","#9933FF"];
export default function Settings(){
  const [dark,setDark]=useState(false); const [neo,setNeo]=useState(false); const [accent,setAccent]=useState(0); const [font,setFont]=useState(1); const [notif,setNotif]=useState(true);
  useEffect(()=>{
    const s=localStorage.getItem("vastavik_settings"); if(s){ try{ const j=JSON.parse(s); setDark(!!j.dark); setNeo(!!j.neo); setAccent(j.accent||0); setFont(j.font||1); setNotif(j.notif!==false);}catch{} }
  },[]);
  useEffect(()=>{
    localStorage.setItem("vastavik_settings", JSON.stringify({dark,neo,accent,font,notif}));
    const html = document.documentElement;
    html.classList.toggle("dark", dark);
    html.classList.toggle("neo-brutalist", neo);
    html.style.setProperty("--accent", accents[accent] || accents[0]);
    html.style.fontSize = `${font*16}px`;
    if (notif && typeof window !== "undefined" && "Notification" in window && Notification.permission === "default") Notification.requestPermission().catch(()=>{});
  },[dark,neo,accent,font,notif]);

  const Toggle = ({on, set}:{on:boolean; set:()=>void}) => (
    <button onClick={set} className={`w-[52px] h-[30px] rounded-full p-1 transition flex items-center ${on?"bg-ink dark:bg-white":"bg-slate-200 dark:bg-zinc-600"}`} aria-pressed={on}>
      <span className={`block w-[22px] h-[22px] bg-white rounded-full shadow-soft transition ${on?"translate-x-[22px] dark:bg-ink":""}`} />
    </button>
  );

  return (
    <div className="space-y-5 max-w-lg mx-auto">
      <Link href="/profile" className="inline-flex items-center gap-1 text-sm font-medium text-brand hover:underline">← Profile</Link>
      <div>
        <h1 className="font-display text-2xl font-extrabold leading-none">Settings</h1>
        <p className="text-sm text-muted mt-1">Personalize your Vastavik web experience</p>
      </div>

      <div className="neo p-1.5">
        <div className="divide-y divide-slate-100 dark:divide-zinc-700">
          <div className="flex justify-between items-center p-4">
            <div><p className="font-medium text-sm">Dark Mode</p><p className="text-xs text-muted">Easier on eyes at night</p></div>
            <Toggle on={dark} set={()=>setDark(v=>!v)} />
          </div>
          <div className="flex justify-between items-center p-4 gap-4">
            <div><p className="font-medium text-sm">Neo-Brutalist</p><p className="text-xs text-muted">Thick borders, brutal shadows</p></div>
            <Toggle on={neo} set={()=>setNeo(v=>!v)} />
          </div>
          {neo && (
            <div className="p-4 bg-amber-50/50 dark:bg-zinc-800/50">
              <p className="text-xs font-bold tracking-widest uppercase text-muted">Accent Color</p>
              <div className="mt-3 flex gap-2.5 flex-wrap">{accents.map((c,i)=> <button key={c} onClick={()=>setAccent(i)} className={`w-12 h-12 rounded-full border-[3px] flex items-center justify-center font-bold shadow-soft transition ${accent===i?"border-ink dark:border-white scale-105":"border-transparent"}`} style={{ background:c }}>{accent===i?"✓":""}</button>)}</div>
            </div>
          )}
          <div className="flex justify-between items-center p-4">
            <div><p className="font-medium text-sm">Font Scale</p><p className="text-xs text-muted">{Math.round(font*100)}% — base size</p></div>
            <input type="range" min={0.8} max={1.4} step={0.1} value={font} onChange={e=>setFont(parseFloat(e.target.value))} className="w-28 accent-[var(--accent)]" />
          </div>
        </div>
      </div>

      <div className="neo p-4 flex justify-between items-center">
        <div><p className="font-medium text-sm">Notifications</p><p className="text-xs text-muted">Push & in-app alerts</p></div>
        <Toggle on={notif} set={()=>setNotif(v=>!v)} />
      </div>

      <div className="neo divide-y overflow-hidden">
        <Link href="/notifications" className="flex justify-between items-center p-4 hover:bg-slate-50 dark:hover:bg-zinc-700/50 transition"><span className="font-medium text-sm">Notification History</span><span className="text-muted">›</span></Link>
        <Link href="/app-update" className="flex justify-between items-center p-4 hover:bg-slate-50 dark:hover:bg-zinc-700/50 transition"><div><p className="font-medium text-sm">App Update</p><p className="text-xs text-muted">Check for updates</p></div><span className="text-muted">›</span></Link>
        <div className="p-4 flex justify-between text-sm"><span className="text-muted">About</span><span className="font-medium">Version 1.0.0</span></div>
      </div>
      <p className="text-xs text-muted text-center">Mirrors Kotlin SettingsScreen — ThemePreferences + DataStore; web uses localStorage + CSS.</p>
    </div>
  );
}
