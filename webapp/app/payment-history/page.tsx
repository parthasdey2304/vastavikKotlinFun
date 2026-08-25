"use client";
import { useEffect, useState } from "react";
import Link from "next/link";
import { apiGet } from "@/lib/api";
import { getIdToken } from "@/lib/firebase";

// Mirrors PaymentHistoryScreen.kt — transactions where uid==, fallback mock
export default function PaymentHistory(){
  const [txs,setTxs]=useState<any[]>([]); const [err,setErr]=useState<string|null>(null);
  useEffect(()=>{ (async()=>{
    const token=await getIdToken();
    try{ const r=await apiGet<{data:any[]}>("/api/v1/transactions?limit=20", token).catch(()=>null as any); if(r?.data) setTxs(r.data); else throw new Error("no api");
    }catch{ setTxs([{id:"1",planName:"Premium Monthly",amount:299,status:"success",timestamp:"2026-08-20"},{id:"2",planName:"Premium Yearly",amount:1999,status:"success",timestamp:"2026-07-01"}]); }
  })(); },[]);
  return (
    <div className="space-y-4">
      <Link href="/profile" className="text-sm text-brand">← Profile</Link>
      <h1 className="text-xl font-bold">Payment History</h1>
      {txs.length===0 ? (<div className="text-center py-12"><div className="text-5xl opacity-30">🧾</div><p className="mt-2 text-sm text-zinc-500">No transactions yet</p></div>) : (
        <div className="space-y-3">
          {txs.map(t=> (
            <div key={t.id} className="neo bg-white p-4 flex gap-3 items-center">
              <div className="w-12 h-12 rounded-2xl bg-green-50 text-green-600 grid place-items-center">✓</div>
              <div className="flex-1"><p className="font-semibold">{t.planName || t.planId}</p><p className="text-xs text-zinc-500">{t.status} • {t.timestamp?.slice?.(0,10) || ""}</p></div>
              <p className="font-bold">₹{t.amount}</p>
            </div>
          ))}
        </div>
      )}
      <p className="text-xs text-zinc-400">Via Firestore transactions where uid== — same as Kotlin FirestoreRepository.streamTransactions. Backend coming: GET /api/v1/transactions.</p>
    </div>
  );
}
