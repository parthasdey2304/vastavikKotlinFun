import "./globals.css";
import { DesktopNav, MobileNav } from "@/components/BottomNav";
export const metadata = { title: "Vastavik Web", description: "Vastavik student webapp — same courses as Android, same backend, unlisted YouTube with Vastavik branding." };
export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="en" suppressHydrationWarning>
      <head>
        <script dangerouslySetInnerHTML={{__html:`try{const s=JSON.parse(localStorage.getItem('vastavik_settings')||'{}');if(s.dark)document.documentElement.classList.add('dark');if(s.neo)document.documentElement.classList.add('neo-brutalist');if(s.accent!=null)document.documentElement.style.setProperty('--accent',['#FFE500','#FF2D78','#0066FF','#00FF66','#FF6600','#9933FF'][s.accent]||'#4F46E5');if(s.font)document.documentElement.style.fontSize=(s.font*16)+'px'}catch(e){}`}} />
      </head>
      <body className="min-h-screen flex flex-col gradient-mesh">
        <header className="sticky top-0 z-20 glass">
          <div className="max-w-6xl mx-auto px-4 py-3 flex items-center justify-between gap-4">
            <div className="flex items-center gap-3">
              <div className="w-9 h-9 rounded-xl gradient-brand grid place-items-center text-white font-display font-extrabold text-[16px] shadow-soft">V</div>
              <div className="hidden sm:block">
                <p className="font-display font-bold leading-none tracking-tight">Vastavik Web</p>
                <p className="text-[11px] text-muted">Learn • Code • Build</p>
              </div>
            </div>
            <DesktopNav />
            <div className="flex items-center gap-2">
              <span className="hidden lg:inline-flex items-center gap-1.5 text-xs font-medium px-3 py-1.5 rounded-full bg-emerald-50 text-emerald-700 border border-emerald-200"><span className="w-2 h-2 rounded-full bg-emerald-500 animate-pulse"/> backend :3001</span>
              <a href="/search" className="hidden sm:grid w-9 h-9 rounded-full bg-white border border-slate-200 place-items-center hover:shadow-soft transition dark:bg-zinc-800 dark:border-zinc-700">⌕</a>
              <a href="/profile" className="w-9 h-9 rounded-full gradient-brand grid place-items-center text-white font-semibold shadow-soft">S</a>
            </div>
          </div>
        </header>
        <main className="flex-1 max-w-6xl w-full mx-auto px-4 py-6 pb-20 md:pb-8">{children}</main>
        <div className="h-[84px] md:hidden shrink-0" aria-hidden />
        <MobileNav />
      </body>
    </html>
  );
}
