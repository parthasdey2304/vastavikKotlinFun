import Link from "next/link";
// Mirrors WelcomeScreen.kt:23 — gradient, celebration, Get Started → user-setup
export default function Welcome(){
  return (
    <div className="min-h-screen bg-gradient-to-b from-brand to-indigo-600 flex flex-col items-center justify-center p-6 text-white text-center">
      <div className="text-6xl">🎉</div>
      <h1 className="mt-6 text-3xl font-bold">Welcome to Vastavik!</h1>
      <p className="mt-2 text-white/85">Let&apos;s set up your profile so we can personalize your learning</p>
      <Link href="/user-setup" className="mt-8 w-full max-w-sm rounded-2xl bg-white text-brand py-4 font-bold text-center">Get Started</Link>
      <Link href="/" className="mt-3 text-white/70 text-sm">Skip for now</Link>
    </div>
  );
}
