"use client";
import { useState } from "react";
import Link from "next/link";

// Mirrors NotificationsScreen.kt — 5 AppNotifications with unread, Mark all read, type routing
type N={id:string;title:string;body:string;time:string;unread:boolean;type:"promo"|"update"|"new_lesson"|"reminder"|"expiry"};
const initial:N[]=[
  {id:"1",title:"New Lesson: OOP Basics",body:"Object-Oriented Programming is now live",time:"2h ago",unread:true,type:"new_lesson"},
  {id:"2",title:"50% OFF Premium",body:"Diwali sale — Rs 149/mo UPI AutoPay",time:"5h ago",unread:true,type:"promo"},
  {id:"3",title:"Practice Reminder",body:"You have 3 pending quizzes",time:"1d ago",unread:false,type:"reminder"},
  {id:"4",title:"App Update v1.1.0",body:"OCR + new promo system",time:"2d ago",unread:false,type:"update"},
  {id:"5",title:"Payment due soon",body:"Your premium expires in 3 days",time:"3d ago",unread:false,type:"expiry"},
];
function hrefFor(n:N){ if(n.type==="promo") return "/payment"; if(n.type==="update") return "/app-update"; if(n.type==="new_lesson") return "/lessons/demo?courseId=1&partId=1&subpartId=1"; return "#"; }
export default function Notifications(){
  const [items,setItems]=useState<N[]>(initial);
  const markAll=()=> setItems(v=> v.map(x=> ({...x, unread:false})));
  const markRead=(id:string)=> setItems(v=> v.map(x=> x.id===id?{...x,unread:false}:x));
  return (
    <div className="space-y-4">
      <div className="flex justify-between items-center"><Link href="/" className="text-sm text-brand">← Home</Link><button onClick={markAll} className="text-sm text-brand">Mark all read</button></div>
      <h1 className="text-xl font-bold">Notifications</h1>
      <div className="space-y-3">
        {items.map(n=> (
          <Link key={n.id} href={hrefFor(n)} onClick={()=>markRead(n.id)} className={`block neo p-4 ${n.unread?"bg-white border-brand/20 shadow-sm":"bg-zinc-50 opacity-70"}`}>
            <div className="flex gap-3">
              <span className={`w-10 h-10 rounded-full grid place-items-center ${n.unread?"bg-brand text-white":"bg-zinc-200"}`}>🔔</span>
              <div className="flex-1"><div className="flex gap-2 items-center"><p className="font-semibold text-sm">{n.title}</p>{n.unread && <span className="w-2 h-2 bg-brand rounded-full"/>}</div><p className="text-sm text-zinc-600">{n.body}</p><p className="text-xs text-zinc-400 mt-1">{n.time}</p></div>
            </div>
          </Link>
        ))}
      </div>
    </div>
  );
}
