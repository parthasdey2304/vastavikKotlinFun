"use client";
import Link from "next/link";
import { usePathname } from "next/navigation";
const items = [
  { href: "/", label: "Home", icon: "⌂", activeIcon: "⬢" },
  { href: "/learning-path", label: "Learn", icon: "◈", activeIcon: "⬣" },
  { href: "/practice", label: "Practice", icon: "⬔", activeIcon: "⬔" },
  { href: "/chat", label: "AI Chat", icon: "✦", activeIcon: "✶" },
];

/** Desktop: horizontal nav rendered inside the header bar. Hidden on mobile. */
export function DesktopNav() {
  const path = usePathname();
  return (
    <nav className="hidden md:flex items-center gap-1 bg-white/60 backdrop-blur-xl border border-slate-200 rounded-full px-1.5 py-1 shadow-soft">
      {items.map(it => {
        const active = path === it.href;
        return (
          <Link
            key={it.href}
            href={it.href}
            className={`flex items-center gap-1.5 px-4 py-2 rounded-full text-sm font-semibold transition whitespace-nowrap ${
              active
                ? "bg-ink text-white shadow-soft"
                : "text-muted hover:text-ink hover:bg-slate-50"
            }`}
          >
            <span className="text-[11px]">{active ? it.activeIcon : it.icon}</span>
            {it.label}
          </Link>
        );
      })}
    </nav>
  );
}

/** Mobile: floating pill fixed at the bottom. Hidden on desktop. */
export function MobileNav() {
  const path = usePathname();
  return (
    <nav className="fixed bottom-4 left-1/2 -translate-x-1/2 w-[min(420px,calc(100%-24px))] bg-white/90 backdrop-blur-xl border border-slate-200 shadow-[0_8px_32px_rgba(15,23,42,0.12)] rounded-full flex justify-around items-center p-1.5 pb-[max(6px,env(safe-area-inset-bottom))] md:hidden z-30">
      {items.map(it => {
        const active = path === it.href;
        return (
          <Link
            key={it.href}
            href={it.href}
            className={`flex-1 flex items-center justify-center gap-1.5 px-3 py-2.5 rounded-full text-sm font-semibold transition ${
              active
                ? "bg-ink text-white shadow-soft"
                : "text-muted hover:text-ink hover:bg-slate-50"
            }`}
          >
            <span className="text-[11px]">{active ? it.activeIcon : it.icon}</span>
            {it.label}
          </Link>
        );
      })}
    </nav>
  );
}

/** Kept for backward compatibility — renders both, but each is visibility-toggled via Tailwind. */
export function BottomNav() {
  return (
    <>
      <DesktopNav />
      <MobileNav />
    </>
  );
}
