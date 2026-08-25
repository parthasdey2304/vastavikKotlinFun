import Link from "next/link";
// Mirrors AdminDashboardScreen.kt — placeholder, real admin is backend/admin-web :3000
export default function Admin(){
  return (
    <div className="min-h-[60vh] grid place-items-center p-6 text-center">
      <div>
        <div className="text-6xl opacity-30">🛠️</div>
        <h1 className="mt-4 text-2xl font-bold">Admin Dashboard</h1>
        <p className="text-sm text-zinc-500 mt-2">This feature is coming soon in the mobile app.</p>
        <p className="text-sm mt-4">Use the real admin panel at</p>
        <a href="http://localhost:3000" target="_blank" className="inline-block mt-2 rounded-2xl bg-brand text-white px-6 py-3 font-bold">Open admin-web :3000 →</a>
        <p className="text-xs text-zinc-400 mt-2">backend/admin-web — Next.js admin for courses/parts/lessons, users, banners.</p>
        <Link href="/profile" className="inline-block mt-4 text-sm text-brand">← Back to Profile</Link>
      </div>
    </div>
  );
}
