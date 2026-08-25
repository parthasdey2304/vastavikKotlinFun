import Link from "next/link";
// Mirrors AccountDeletedScreen.kt:21
export default function AccountDeleted(){
  return (
    <div className="min-h-[60vh] grid place-items-center p-6 text-center">
      <div>
        <div className="text-6xl opacity-60">🗑️</div>
        <h1 className="mt-4 text-2xl font-bold">Account Deleted</h1>
        <p className="text-sm text-zinc-500 mt-2">Your account has been permanently removed.</p>
        <Link href="/login" className="inline-block mt-6 rounded-2xl bg-brand text-white px-6 py-3 font-bold">Back to Login</Link>
      </div>
    </div>
  );
}
