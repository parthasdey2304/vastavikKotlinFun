# Design.md — UI/UX Specification — vastavikComputers

> **Stitch Project:** https://stitch.withgoogle.com/projects/17415965411885249153 — **MUST be opened in browser** (requires Google login). Export via Stitch MCP server to React/Tailwind/HTML/Figma.
> **Version:** 0.1.0 | **Date:** 2026-08-22

---

## 1. Primary Design Source

| Source | Link / Access | Export |
|---|---|---|
| **Google Stitch** (primary) | https://stitch.withgoogle.com/projects/17415965411885249153 | React + Tailwind + HTML + Figma via MCP server. MCP: `npx stitch-mcp` or Stitch plugin. Cannot be fetched programmatically without browser auth. |
| **Stitch MCP workflow** | Stitch project -> MCP server -> `stitch export --project 17415965411885249153 --format react-tailwind` -> `web/components/` | Design-to-code pipeline |

**Stitch export capabilities:** Generates responsive React components with Tailwind classes, Figma frames, HTML prototypes. Use as base, then adapt with v0.dev/21st.dev.

---

## 2. Design References

| Reference | Purpose | URL / Note |
|---|---|---|
| **Google Stitch** | Primary UI prototypes, screen flows | https://stitch.withgoogle.com/projects/17415965411885249153 |
| **getdesign.md** | Design tokens reference (colors, spacing, type) | Local `getdesign.md` if present; else Tailwind defaults |
| **stitch.com** | Component prototypes | https://stitch.com |
| **tasteskill.dev** | UX pattern inspiration (onboarding, gamification) | https://tasteskill.dev |
| **21st.dev** | React component library (buttons, cards, sheets) | https://21st.dev |
| **v0.dev/templates** | Page-level layout templates | https://v0.dev/templates |
| **moonsites.ai** | Landing/marketing page design | https://moonsites.ai |
| **UI/UX Pro Max** | Design intelligence skill guidelines | Skill config in repo (`.agent/skills/`) |
| **Andrej Karpathy Skill** | Code quality + AI integration patterns | GitHub skill config |

---

## 3. Design Principles

1. **Student-first clarity** — Large tap targets (48dp min), simple language, visual hierarchy for ages 10–18. No jargon without tooltip.
2. **Gamified progression** — Streaks, badges, progress rings, level-up animations. Celebrate completion.
3. **Three-format video awareness** — Distinct icons/colors for VS Code (blue), Whiteboard (green), Shorts (red) so student knows format at a glance.
4. **Code-first aesthetic** — Dark code blocks (JetBrains Mono), syntax highlighting, Monaco/Compose editor is hero element.
5. **Responsive by default** — Mobile-first; every screen works at 320px, 768px, 1280px+.
6. **Accessibility WCAG 2.1 AA** — 4.5:1 contrast, keyboard nav, screen-reader labels, `prefers-reduced-motion` respect.

---

## 4. Design Tokens

### 4.1 Color Palette

| Token | Hex | Usage |
|---|---|---|
| **Primary Indigo** | `#4F46E5` | CTA, links, active nav, progress |
| **Primary Hover** | `#4338CA` | Button hover |
| **Java Orange** | `#F97316` | Java subject accent, badge |
| **Python Blue** | `#3B82F6` | Python subject accent |
| **JavaScript Yellow** | `#EAB308` | JS subject accent (text `#A16207` on light) |
| **SQL Violet** | `#8B5CF6` | SQL subject accent |
| **VS Code Blue** | `#0EA5E9` | VS Code video indicator |
| **Whiteboard Green** | `#10B981` | Whiteboard video indicator |
| **Shorts Red** | `#EF4444` | Shorts indicator |
| **Neutral 50** | `#F9FAFB` | Light bg |
| **Neutral 900** | `#111827` | Dark bg / text |
| **Neutral 700** | `#374151` | Secondary text |
| **Success** | `#22C55E` | Pass, completed |
| **Warning** | `#F59E0B` | Grace, pending |
| **Error** | `#EF4444` | Fail, error |
| **Dark surface** | `#1F2937` | Card dark mode |
| **Dark bg** | `#0F172A` | App dark bg |

Dark mode: `neutral-900` bg, `neutral-50` text, primary stays `#6366F1` (lighter indigo).

### 4.2 Typography

| Role | Font | Size | Weight | Usage |
|---|---|---|---|---|
| **Heading 1** | Plus Jakarta Sans | 30–36px | 700 | Page titles |
| **Heading 2** | Plus Jakarta Sans | 24px | 600 | Section titles |
| **Heading 3** | Plus Jakarta Sans | 18px | 600 | Card titles |
| **Body** | Inter | 16px | 400 | Paragraphs |
| **Body small** | Inter | 14px | 400 | Captions, meta |
| **Code** | JetBrains Mono | 14px | 400 | Editor, snippets, inline code |
| **Label** | Inter | 12px | 600 | Uppercase labels, badges |

Load via `next/font/google` (web) and downloadable fonts (Android).

### 4.3 Spacing, Radius, Shadow, Max Width

| Token | Value |
|---|---|
| **Spacing** | xs 4px, sm 8px, md 16px, lg 24px, xl 32px, 2xl 48px |
| **Radius** | sm 6px, md 10px, lg 16px, xl 20px, full 9999px |
| **Shadow** | sm `0 1px 2px rgba(0,0,0,0.05)`, md `0 4px 6px rgba(0,0,0,0.07)`, lg `0 10px 15px rgba(0,0,0,0.1)` |
| **Max widths** | content 1280px, prose 720px, admin 1440px |
| **Breakpoints** | sm 640px, md 768px, lg 1024px, xl 1280px |

---

## 5. Component Library

| Component | Web (React) | Android (Compose) | Notes |
|---|---|---|---|
| **Button** | `Button` (primary/ghost/outline, sm/md/lg) | `VastavikButton` | Indigo primary, 48dp min, loading spinner |
| **Card** | `Card` + `CardHeader/Content` | `VastavikCard` | Radius lg, shadow sm, hover lift (web) |
| **VideoPlayer** | `VideoPlayer` (HLS.js) | `ExoPlayer` wrapper | Format badge (VS Code/Whiteboard/Shorts), progress bar, speed, PiP |
| **CodeEditor** | `MonacoEditor` (`@monaco-editor/react`) | `CodeEditor` composable | JetBrains Mono, dark theme, run button, output pane |
| **ChatBubble** | `ChatBubble` (user/model) | `ChatBubble` | User indigo, model neutral, markdown + code highlight |
| **ProgressBar** | `ProgressRing` / `ProgressBar` | `LinearProgress` / `CircularProgress` | Gamified, counts toward streak |
| **MCQCard** | `MCQCard` (options, feedback) | `MCQCard` | Instant feedback, explanation reveal |
| **QuestionPaper** | `PaperPreview` (sections, PDF button) | `PaperCard` | Timer badge, marks |
| **Badge** | `Badge` (subject, difficulty) | `Badge` | Color per subject/difficulty |
| **BottomNav** | — | `BottomNavigation` | 5 tabs: Home, Learn, Editor, Chat, Profile |
| **Sidebar** | `Sidebar` (collapsible) | — | Web nav: Dashboard, Subjects, Practice, etc. |
| **PaymentSheet** | `PaymentSheet` (Razorpay checkout) | `PaymentBottomSheet` | UPI mandate info, price, T&C |
| **Skeleton** | `Skeleton` | `Shimmer` | Loading state for all lists |
| **EmptyState** | `EmptyState` | `EmptyState` | Illustration + CTA |
| **Dialog** | `Dialog` (Radix) | `AlertDialog` | Confirmations |

---

## 6. Screen Inventory (20+)

| # | Screen | Android | Web | Description |
|---|---|---|---|---|
| 1 | Splash | Yes | — | Logo + loading, auth check |
| 2 | Onboarding | Yes | — | 3 slides: Learn, Code, Achieve |
| 3 | Login | Yes | Yes | Email + Google, forgot password |
| 4 | Register | Yes | Yes | Name, email, class (5–12), password |
| 5 | Verify Email | Yes | Yes | Resend, check |
| 6 | Home/Dashboard | Yes | Yes | Continue learning, subjects, streak, recent |
| 7 | Subject List | Yes | Yes | 4 subject cards (Java/Python/JS/SQL) |
| 8 | Course / Topic List | Yes | Yes | Class-filtered, progress bars |
| 9 | Lesson Detail | Yes | Yes | 3 video tabs, theory, MCQs, coding link |
| 10 | Video Player | Yes | Yes | Fullscreen, format badge, next/prev |
| 11 | Theory Reader | Yes | Yes | Markdown, code copy, bookmark |
| 12 | Code Editor | Yes | Yes | Language picker, run, output, save |
| 13 | MCQ Practice | Yes | Yes | Question, options, feedback, progress |
| 14 | Coding Question | Yes | Yes | Statement, starter, tests, submit |
| 15 | Question Papers | Yes | Yes | List, filter by subject/class |
| 16 | Paper Attempt | Yes | Yes | Timed, sections, auto-submit |
| 17 | AI Chat | Yes | Yes | Thread list, message stream, code blocks |
| 18 | Profile | Yes | Yes | Avatar, class, subscription badge, settings |
| 19 | Subscription/Pay | Yes | Yes | Plans, UPI AutoPay sheet, history |
| 20 | Notifications | Yes | Yes | List, mark read |
| 21 | Admin Dashboard | — | Yes | Metrics (Recharts), recent activity |
| 22 | Admin Users | — | Yes | Table, search, role toggle |
| 23 | Admin Content CRUD | — | Yes | Subjects/courses/topics/lessons/theory |
| 24 | Admin Paper Builder | — | Yes | Drag sections, PDF preview |
| 25 | Admin Analytics | — | Yes | DAU, revenue, token usage |
| 26 | Admin Payments | — | Yes | Mandates, webhooks, failures |
| 27 | 404 / Error | Yes | Yes | Friendly error, go home |

---

## 7. Responsive Behavior

| Breakpoint | Width | Layout |
|---|---|---|
| **Mobile** | < 640px | Single column, BottomNav (Android) / hamburger (Web), stacked cards, full-width video |
| **Tablet** | 640–1024px | 2-col grid for cards, Sidebar collapsible, editor + output split 60/40 |
| **Desktop** | > 1024px | Sidebar fixed, 3–4 col grid, editor + output side-by-side, video + theory split |

Web uses Tailwind responsive prefixes (`sm:`, `md:`, `lg:`). Android uses `WindowSizeClass` (Compact/Medium/Expanded).

---

## 8. Design-to-Code Workflow

```
Stitch (browser)  --export-->  React/Tailwind components
       |
       +--> v0.dev/templates  -> page layouts (dashboard, lesson, admin)
       +--> 21st.dev          -> polished components (sheets, dialogs, charts)
       |
       v
  web/components/  (adapt: props, Zustand, Firebase hooks)
       |
       +--> Android Compose  (translate tokens: color, type, spacing; rebuild with Material3)
       |
       v
  Document in Design.md + Storybook (future)
```

**MCP command (when available):** `npx @stitch/mcp export --project 17415965411885249153 --out web/components/stitch/`

---

## 9. Animation

| Element | Duration | Easing | Spec |
|---|---|---|---|
| Page transition | 200ms | ease-out | Fade + slight slide (8px) |
| Card tap | 100ms | ease-out | Scale 0.98, shadow reduce |
| Chat message appear | 250ms | ease-out | Fade + slide up 8px, stagger 40ms |
| Progress bar | 600ms | ease-out | Width grow, count-up number |
| Bottom sheet | 300ms | spring (damping 0.8) | Slide up, backdrop fade |
| Skeleton loading | 1.2s loop | linear | Shimmer gradient sweep |
| Badge pop | 300ms | spring | Scale 0.8->1, bounce |
| Reduced motion | — | — | If `prefers-reduced-motion`, durations -> 0, no scale |

---

## 10. Iconography

| Platform | Library | Usage |
|---|---|---|
| **Web** | `lucide-react` | General UI icons (all screens) |
| **Android** | Material Symbols | General UI icons |
| **Subjects** | Language logos (official) | Java (coffee), Python (snake), JS (JS), SQL (database) |
| **Video formats** | Lucide: `Code2` (VS Code), `Presentation` (Whiteboard), `Smartphone` (Shorts) | Badge icons with format colors |
| **Code** | `JetBrains Mono` + syntax tokens | Editor only |

---

## 11. Accessibility Checklist

- [ ] Color contrast >= 4.5:1 (AA) — verified with Stitch tokens
- [ ] Focus rings visible (2px indigo outline, offset 2px)
- [ ] All interactive elements keyboard reachable + `aria-label`
- [ ] Video captions (future) + transcript fallback
- [ ] Code blocks have `aria-label` + copy button with feedback
- [ ] Reduced-motion alternative for all animations

---
> **Update v0.2.0:** See UI_UX_DESIGN.md for 21-feature generative home, NeoBrutalish, full-screen editor line numbers, promo pop (text/image/video), and OCR exercise. Stitch remains primary https://stitch.withgoogle.com/projects/17415965411885249153.

