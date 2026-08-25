"use client";
import { useState } from "react";
import Link from "next/link";

// Mirrors PaymentScreen.kt:25 — gateway toggle, promo, plans, features
export default function Payment(){
  const [gateway,setGateway]=useState<"Razorpay"|"PhonePe">("Razorpay");
  const [plan,setPlan]=useState<"monthly"|"yearly">("monthly");
  return (
    <div className="space-y-4 max-w-lg mx-auto">
      <Link href="/profile" className="text-sm text-brand">← Profile</Link>
      <div className="neo bg-gradient-to-br from-brand to-indigo-600 text-white p-6 text-center">
        <div className="text-3xl">⭐</div><h1 className="mt-2 text-2xl font-bold">Vastavik Premium</h1><p className="text-sm opacity-85">Unlock all lessons + AI chat</p>
      </div>
      <div className="neo bg-white p-4">
        <p className="text-xs font-semibold text-brand">Payment Gateway</p>
        <div className="mt-2 grid grid-cols-2 gap-2">{(["Razorpay","PhonePe"] as const).map(g=> <button key={g} onClick={()=>setGateway(g)} className={`p-4 rounded-2xl border-2 text-sm font-semibold ${gateway===g?"bg-brand/10 border-brand text-brand":"bg-zinc-50 border-zinc-200"}`}>{g}</button>)}</div>
      </div>
      <div className="neo bg-amber-50 border-amber-200 p-3 flex gap-2 items-center text-sm"><span>🏷️</span><span className="font-semibold">50% OFF applied! Diwali Sale</span></div>
      <div className="space-y-3">
        {[
          {id:"monthly", title:"Monthly", price:"₹149", slash:"₹299", badge:"50% OFF"},
          {id:"yearly", title:"Yearly", price:"₹999", slash:"₹1,999", badge:"Save 44%"},
        ].map(p=> (
          <button key={p.id} onClick={()=>setPlan(p.id as any)} className={`w-full text-left neo p-4 flex justify-between items-center ${plan===p.id?"bg-brand/5 border-brand":"bg-white"}`}>
            <div><p className="font-bold">{p.title}</p><p><span className="font-bold text-lg">{p.price}</span> <span className="line-through text-zinc-400 text-sm">{p.slash}</span> <span className="ml-2 text-xs bg-green-100 text-green-700 px-2 py-1 rounded-full">{p.badge}</span></p></div>
            <span className={`w-6 h-6 rounded-full border-2 grid place-items-center ${plan===p.id?"bg-brand border-brand text-white":"border-zinc-300"}`}>{plan===p.id?"✓":""}</span>
          </button>
        ))}
      </div>
      <div className="neo bg-white p-4">
        <p className="font-bold">What you get:</p>
        <ul className="mt-2 space-y-2 text-sm">
          {["Unlimited video lessons","All coding challenges","PYQ access","AI chat","Priority support"].map(f=> <li key={f} className="flex gap-2"><span className="text-green-600">✓</span>{f}</li>)}
        </ul>
      </div>
      <Link href="/payment-history" className="block w-full rounded-2xl bg-brand text-white py-4 font-bold text-center">Pay with {gateway} — UPI AutoPay (50% OFF)</Link>
      <p className="text-xs text-zinc-400 text-center">Monthly UPI AutoPay mandate • 3-day grace • Same as Kotlin PaymentScreen — backend coming: POST /api/v1/payments/create-mandate</p>
    </div>
  );
}
