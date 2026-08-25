import Link from "next/link";
// Mirrors AppUpdateScreen.kt:22 — current vs latest, force flag, update CTA
const current="1.0.0", latest="1.1.0", isUpdateAvailable=true, isForce=false;
export default function AppUpdate(){
  return (
    <div className="space-y-6 max-w-lg mx-auto text-center">
      <Link href="/" className="inline-block text-sm text-brand">← Home</Link>
      <div className="text-6xl">🔄</div>
      <h1 className="text-xl font-bold">Vastavik Computers</h1>
      <p className="text-sm text-zinc-500">Current: v{current} {isUpdateAvailable? `• Latest: v${latest}`:""}</p>
      {isUpdateAvailable ? (
        <div className="neo bg-white p-6 text-left space-y-3">
          <p className="font-bold text-brand">Update available: v{latest}</p>
          <p className="text-sm font-semibold">What&apos;s new:</p>
          <ul className="text-sm list-disc pl-5 space-y-1"><li>OCR — photo to code</li><li>50% promo system</li><li>Unlisted YouTube with Vastavik branding</li></ul>
          {isForce && <p className="text-sm text-red-600">Update required to continue</p>}
          <a href="https://play.google.com/store/apps/details?id=com.vastavik.computer" target="_blank" className="block w-full rounded-2xl bg-brand text-white py-4 font-bold text-center">Update Now</a>
          <Link href="/" className={`block w-full rounded-2xl border py-4 text-center ${isForce?"opacity-40 pointer-events-none":""}`}>Later</Link>
        </div>
      ) : (
        <div className="neo bg-white p-6"><div className="w-12 h-12 rounded-full bg-green-100 text-green-600 grid place-items-center mx-auto">✓</div><p className="mt-3 font-bold">You&apos;re up to date!</p><Link href="/" className="inline-block mt-4 text-brand">Back to Home</Link></div>
      )}
      <p className="text-xs text-zinc-400">Mirrors Kotlin AppUpdateScreen — adminSettings/current {`{latestVersion,minVersion,isForceUpdate}`}</p>
    </div>
  );
}
