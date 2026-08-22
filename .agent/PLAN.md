# PLAN.md — Implementation Roadmap — vastavikComputers

> **Version:** 0.1.0 | **Date:** 2026-08-22 | **Root:** D:\vastavikComputers

---

## Overview

8-phase plan from init to v1.0.0 launch. Each phase has entry criteria, tasks (checkboxes), and exit criteria.

---

## Phase 0: Project Initialization — DONE (2026-08-22)

- [x] Create `.agent/` folder with 12 Markdown docs
- [x] Verify Gemini 3.7 Flash (`gemini-3.7-flash`) and Stitch project link
- [x] Define Firestore data model (14 collections) + security rules spec
- [x] Lock tech stack (Kotlin + Next.js + Firebase + Gemini + UPI AutoPay)
- [x] Sync docs to workspace (`D:\vastavikKotlinFun\.agent`)

**Exit:** All 12 docs reviewed and committed.

---

## Phase 1: Foundation & Scaffolding — Target: v0.2.0 (2 weeks)

### Repo & Monorepo

- [ ] Create `develop` branch from `main`, enable branch protection (no direct push to `main`)
- [ ] Monorepo structure: `web/`, `kotlin-app/`, `functions/`, `shared/`, `.agent/`, `.github/workflows/`
- [ ] Root `package.json` workspaces + `turbo.json` (or npm workspaces)
- [ ] `.gitignore` for `node_modules`, `.next`, `build`, `.env*`, `google-services.json`
- [ ] `shared/` — Zod schemas + TS types for all 14 collections + constants (collection names, class levels)

### Firebase Project

- [ ] Create Firebase projects: `vastavik-prod` + `vastavik-staging`
- [ ] Enable: Auth (email/password + Google), Firestore, Storage, FCM
- [ ] Configure `firebase.json`, `firestore.rules` (from SECURITY.md), `storage.rules`, `firestore.indexes.json`
- [ ] Set up emulators: `firebase emulators:start` (Auth, Firestore, Functions, Storage)

### Web Scaffolding (`web/`)

- [ ] `npx create-next-app@15 web --typescript --tailwind --app --eslint`
- [ ] Install: `zustand`, `zod`, `firebase`, `@google/generative-ai`, `@monaco-editor/react`, `react-markdown`, `rehype-highlight`, `recharts`, `lucide-react`, `clsx`, `date-fns`
- [ ] Dev: `vitest`, `@testing-library/react`, `@playwright/test`, `prettier`, `prettier-plugin-tailwindcss`
- [ ] `lib/firebase.ts` (client) + `lib/firebase-admin.ts` (server) + `lib/validators.ts`
- [ ] `app/layout.tsx`, `globals.css` (Tailwind 4), `middleware.ts` (auth guard)
- [ ] `stores/` + `hooks/useAuth.ts` + placeholder `(auth)/`, `(student)/`, `admin/` routes

### Android Scaffolding (`kotlin-app/`)

- [ ] New Kotlin project (Android Studio, Kotlin 2.0+, Compose BOM 2024.09+)
- [ ] Gradle deps: Compose, Navigation, Hilt, Room, Retrofit, Coil, Media3, Firebase BOM, Coroutines
- [ ] `di/AppModule.kt`, `data/`, `domain/`, `ui/theme/`, `ui/navigation/NavGraph.kt`
- [ ] `google-services.json` (from Firebase Console) — gitignored, documented in README
- [ ] Placeholder screens: Splash, Login, Home

### Cloud Functions (`functions/`)

- [ ] `firebase init functions --typescript` (Node 20)
- [ ] Install: `firebase-admin`, `firebase-functions`, `@google/generative-ai`, `zod`, `razorpay`, `pdfkit`, `dockerode`
- [ ] `src/index.ts` exports + folder structure: `auth/`, `content/`, `ai/`, `payments/`, `code/`, `admin/`
- [ ] Emulator smoke test: `onUserCreate` trigger

**Exit:** `npm run dev` (web) + emulator + Android build all green.

---

## Phase 2: Core Student Features — Target: v0.3.0 (3 weeks)

### Content Data

- [ ] Seed Firestore: `subjects` (4), `courses` per class, `topics`, sample `lessons` + `theory`
- [ ] Storage: upload sample videos (VS Code, Whiteboard, Shorts) + theory images
- [ ] Admin CRUD for subjects/courses/topics/lessons/theory (web `/admin/content`)

### Video Player

- [ ] Web: HLS.js player with format badges (VS Code/Whiteboard/Shorts), progress, playback speed
- [ ] Android: ExoPlayer wrapper with same
- [ ] Progress tracking: `users/{uid}.progress` or subcollection — watched %, completed

### Theory Materials

- [ ] Markdown renderer (`react-markdown` + `rehype-highlight`) with code copy, dark blocks
- [ ] Android: Markdown renderer (compose-markdown or WebView)
- [ ] Search + bookmark

**Exit:** Student can browse subjects -> topics -> lessons -> watch video -> read theory.

---

## Phase 3: Interactive Learning — Target: v0.5.0 (4 weeks)

### Code Editor + Sandbox

- [ ] Web: Monaco editor (`@monaco-editor/react`) — language switch, run, output pane
- [ ] Android: Compose editor with syntax highlight
- [ ] Cloud Run Docker sandbox: 4 images (Java/Python/Node/SQLite), no net, RO FS, 256M, 10s, seccomp
- [ ] `POST /api/code-exec` + `executeCode` CF — validation (10K char, no `os/subprocess/eval`), truncate 1MB
- [ ] `codeSubmissions` persistence + resume

### AI Chat (Gemini 3.7 Flash)

- [ ] `geminiChat` CF: Zod input, rate-limit (50/day, 100K tokens), system prompt (Class 5–12 tutor), call `gemini-3.7-flash`
- [ ] `POST /api/gemini` proxy (Next.js) — streams response
- [ ] Web + Android chat UI: thread list, bubbles, markdown+code, token counter
- [ ] `aiChats` collection + 90-day retention job
- [ ] Safety: prompt-injection guard, PII redaction, off-topic refusal, content filter

### MCQ System

- [ ] `mcqs` CRUD (admin), student practice UI (instant feedback + explanation), progress tracking

### Coding Questions

- [ ] `codingQuestions` CRUD, student detail (starter code, run, hidden/visible tests, auto-grade), submission history

**Exit:** Editor runs code in sandbox; AI chat answers scoped queries; MCQs + coding Qs graded.

---

## Phase 4: Question Papers & PDF — Target: v0.6.0 (2 weeks)

- [ ] `questionPapers` model + admin builder: drag sections, pick Qs, set marks/duration, preview
- [ ] Student: list, filter, attempt (practice vs timed with countdown + auto-submit)
- [ ] PDF generation: `pdfkit` in `POST /api/pdf/generate` / `bulkImport` CF, upload to Storage, `pdfUrl`
- [ ] Auto-evaluation for MCQ + coding parts
- [ ] Download + share PDF

**Exit:** Admin can build paper; student can attempt and download PDF.

---

## Phase 5: Subscription & Payments (UPI AutoPay) — Target: v0.7.0 (3 weeks)

- [ ] Razorpay/Cashfree integration: `POST /api/payment/create` -> mandate link
- [ ] Web `PaymentSheet` + Android `PaymentBottomSheet` — plan, price, T&C, UPI app redirect
- [ ] `POST /api/payment/webhook` — signature verify, idempotent, update `payments` + `users.subscription`
- [ ] `checkExpiry` scheduled CF — daily 02:00 IST, grace 3d, downgrade, FCM 7/3/1d warnings
- [ ] Access gating: `isPro` lessons/papers/AI chat require `subscription.status == active` (rules + middleware)
- [ ] Free vs Pro tier enforcement + paywall UX
- [ ] Payment history screen

**Exit:** User can subscribe via UPI, webhook updates subscription, expiry handled, gating works.

---

## Phase 6: Admin Dashboard — Target: v0.8.0 (3 weeks)

- [ ] `/admin` — Firebase custom claim `admin: true` + middleware guard + email allowlist
- [ ] Dashboard: DAU/MAU, revenue (Recharts), content completion, AI token usage
- [ ] User management: list/search, view subscription, `setAdmin` callable, suspend
- [ ] Content CRUD polish + bulk import (`POST /api/admin/questions/bulk`)
- [ ] Paper builder polish + analytics
- [ ] Payment monitoring: mandates, webhooks, failures
- [ ] AI chat log viewer (PII redacted, flagged)
- [ ] Admin notification sender (FCM topic/token)

**Exit:** Admin can manage all content, users, papers, payments, notifications.

---

## Phase 7: Notifications & Polish — Target: v0.9.0 (2 weeks)

- [ ] FCM setup: Web (service worker) + Android (channels, permissions)
- [ ] Triggers: new lesson, expiry, reminders (scheduled), streak, broadcast
- [ ] User prefs: `notificationPrefs` + settings toggles
- [ ] Offline: Room cache (Android) + Firestore offline + Service Worker (Web) for theory + videos
- [ ] Gamification: streaks, badges, progress rings, celebrations
- [ ] Accessibility audit (WCAG 2.1 AA), performance audit (LCP < 2.5s), dark mode polish
- [ ] Stitch design pass: export remaining screens, apply tokens, responsive fixes

**Exit:** Notifications reliable, offline works, a11y + perf pass, design matches Stitch.

---

## Phase 8: Testing & Launch — Target: v1.0.0 (2 weeks)

- [ ] Unit tests: web (Vitest 70%), functions (80%), Android (JUnit 60%)
- [ ] E2E: Playwright (auth, pay, editor, paper attempt) + Compose UI tests
- [ ] Security: rules unit tests (all 14 collections), sandbox isolation tests, SAST (npm audit, OWASP)
- [ ] Beta: TestFlight / Play internal track + Vercel preview, dogfood with 20 users
- [ ] Perf + load test (Functions concurrency, Firestore reads)
- [ ] DPDP Act 2023 review: consent, retention, data subject rights
- [ ] Prod deploy: Vercel prod (or Hosting), Firebase prod, Play Store, Cloud Run sandbox
- [ ] Tag `v1.0.0`, update CHANGELOG + VERSION, announce

**Exit:** v1.0.0 live on Play Store + Web prod, monitoring (logs, crashlytics) active.

---

## Milestone Summary

| Phase | Milestone | Target Version | Weeks | Key Deliverable |
|---|---|---|---|---|
| 0 | Init | 0.1.0 | — | .agent docs |
| 1 | Foundation | 0.2.0 | 2 | Monorepo + Firebase + scaffolds |
| 2 | Core content | 0.3.0 | 3 | Videos + theory browsable |
| 3 | Interactive | 0.5.0 | 4 | Editor + AI chat + MCQs/coding |
| 4 | Papers | 0.6.0 | 2 | Papers + PDF |
| 5 | Payments | 0.7.0 | 3 | UPI AutoPay + gating |
| 6 | Admin | 0.8.0 | 3 | /admin full |
| 7 | Polish | 0.9.0 | 2 | FCM + offline + a11y |
| 8 | Launch | 1.0.0 | 2 | Prod + Store |

**Total:** ~21 weeks from Phase 1 start.

---

## Dependencies & Risks

| Risk | Mitigation |
|---|---|
| Stitch MCP not yet stable | Fallback: manual export from Stitch browser + v0.dev/21st.dev |
| Gemini 3.7 Flash pricing/quotas change | Monitor `aistudio.google.com`, rate-limit, token budget |
| UPI AutoPay gateway approval delay | Start sandbox early (Phase 1), use test mandates |
| Docker sandbox cost | Cloud Run min instances 0, cache images, 10s timeout |
