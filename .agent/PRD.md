# PRD.md — Product Requirement Document — vastavikComputers (v0.2.0)

> **Project:** vastavikComputers — EdTech for Class 5–12 (Java, Python, JavaScript, SQL)
> **Repo:** https://github.com/parthasdey2304/vastavikComputers | **Root:** D:\vastavikComputers | **Kotlin App:** kotlin-app/
> **Version:** 0.2.0 | **Date:** 2026-08-22 | **Status:** Agent Design Phase — 21 features scoped
> **Stitch:** https://stitch.withgoogle.com/projects/17415965411885249153 | **AI:** Gemini 3.7 Flash (`gemini-3.7-flash`, via Google AI Studio, API key in Secret Manager — never commit)

---

## 1. Product Overview

Cross-platform EdTech for India Class 5–12 to learn Java/Python/JavaScript/SQL via structured curriculum. Ships as **Kotlin + Jetpack Compose Android app** (kotlin-app/, replaces deprecated Flutter lib/) and future Next.js web + `/admin`. Backend is **dual: Firebase (primary — Auth, Firestore, Storage, FCM, Functions) + Supabase (secondary — Postgres for analytics / relational reporting / notes search / vector)** — see `BACKEND_SCHEMA.md`. Monetization via **UPI AutoPay (PhonePe / Razorpay, monthly mandate, access revoked if not paid)**. UI follows Stitch project + generative home.

---

## 2. Target Audience

| Segment | Details |
|---|---|
| Students | Class 5–12, 10–18y, CBSE/ICSE/State, beginner→intermediate, India-first (IST, INR, UPI) |
| Parents/Teachers | Progress + payment oversight (secondary) |
| Admins | Content + users + payments + notifications (internal) |
| Languages | Java, Python, JavaScript, SQL |

DPDP Act 2023: parental consent for minors, data minimization, 90-day AI chat retention.

---

## 3. 21 Required Features — Full Scope (DO NOT OMIT)

| # | Feature | Spec | Storage/Tech |
|---|---|---|---|
| 1 | **Start Page (Splash)** | Shows `Vastavik Computers` logo, tagline, loading, auth check → Welcome/Onboarding or Home. Lottie. | Compose |
| 2 | **Login Page** | Email/password + Google Sign-In, validation, error, forgot link, "Don''t have account? Sign Up" | Firebase Auth |
| 3 | **Sign Up Page** | Name, email, class 5–12 picker, password + confirm, Google, T&C checkbox, creates `users/{uid}` | Firebase Auth + Firestore |
| 3ii | **Forgot Password** | Email input → Firebase sendPasswordResetEmail, confirmation, back to login | Firebase Auth |
| 4 | **Home Page (Generative)** | Generative look: streak banner, continue learning, recommended (AI-driven), subject chips (Java/Python/JS/SQL), banners/promotions carousel (50% etc.), search, bottom nav. Pull-to-refresh. | Firestore `courses`/`banners` + Realtime |
| 5 | **Learn Page (Learning Path)** | Duolingo-style path (see `DuolingoPath.kt`), topics as nodes, locked/unlocked, progress rings, lesson nodes → Course/Lesson | Firestore `topics`/`lessons` + `progress` |
| 6 | **Profile Page** | Avatar, name, email, class, subscription badge, streak, lessons completed, edit profile, my notes, payment history, settings, logout, delete account | `users/{uid}` |
| 7 | **Practise Page (MCQ, Question Paper / PYQ)** | Tabs: MCQ Practice (topic-wise, instant feedback), Question Papers / PYQ (subject+class filter, timed/practice), Coding Questions. | `mcqs`, `questionPapers`, `codingQuestions` |
| 8 | **Code Editor + Output Screen** | Full-screen editor, syntax highlight for Java/Python/JS/SQL, line numbers, theme (matches Settings), language picker, Run, auto-save draft, output pane (stdout/stderr, per-test results), takes entire space, good line numbers, copy, clear. | Monaco-like Compose editor, Docker sandbox (Cloud Run, 10s, 256M, no net), `codeSubmissions` |
| 9 | **AI Chat** | Gemini 3.7 Flash (`gemini-3.7-flash`) via Google AI Studio, API key `AQ.Ab8RN...PQ` stored ONLY in `local.properties` / Secret Manager + BuildConfig, never in repo. Scoped to Class 5–12 coding, streaming bubbles, code blocks with copy/highlight, history in `aiChats`, token tracking, rate limit 50/day. | `@google/generative-ai` via proxy CF `geminiChat` |
| 10 | **Settings Page** | Light/Dark toggle, Modern vs NeoBrutalish theme variant (radius/shadow/border toggle), font size, notifications toggle, language, about, app version, clear cache | DataStore `ThemePreferences` |
| 11 | **Light/Dark Theme + Font** | Theme.kt: Light/Dark + optional NeoBrutalish (thick borders, hard shadows). Fonts: Plus Jakarta Sans headings, Inter body, JetBrains Mono code. Persisted. | Compose Material3 |
| 12 | **Notification Page** | List of notifications (new lesson, payment, promo, app update), unread dot, mark read, tap → deep link, empty state | `notifications` + FCM |
| 13 | **App Update Page** | Check version vs Firestore `appConfig/latestVersion`, show changelog, Update button → Play Store, force update if `isForceUpdate` (blocks app) | Firestore `appConfig`, Play Store |
| 14 | **Course Page** | Subject courses list, class filter, search, card with thumbnail, progress, tap → topic/lesson. | `courses` |
| 15 | **Course Detail — Videos (3 formats) + MCQ/Paper** | Lesson detail shows 3 video tabs: VS Code 16:9, Whiteboard 16:9, Shorts 1–2min 9:16 vertical for revision, plus theory, MCQ, question paper section linkage. Video player with format badge, speed, PiP. | Storage `videos/{vscode,whiteboard,shorts}` + ExoPlayer/YouTubePlayer |
| 16 | **In-App Notification Pop (Promo)** | Popup that can show Text (heading + body) or Image or Video, dismissable (X), CTA button, can be triggered from admin/Firestore `banners`/`promotions` or FCM data message. Cuttable. Auto-show on home if `isActive`. | Firestore `promotions` + FCM |
| 17 | **Payment Page (PhonePe / Razorpay, UPI AutoPay monthly)** | Plan card (price, features, 50% promo if active), PhonePe or Razorpay toggle (to be confirmed), UPI AutoPay mandate creation, T&C, Pay button → gateway → UPI app. Shows mandate status, next billing, history, retry. If not paid → subscription `expired` after 3-day grace → access revoked (Pro gated). | Razorpay/Cashfree/PhonePe SDK, Firestore `payments` + `users.subscription`, webhook + `checkExpiry` cron |
| 18 | **Promotions (50% etc.)** | Admin creates promo (`promotions/{id}`: title, discount 50%, valid till, banner image), home banner + payment page shows slashed price, code auto-applied. | Firestore `promotions`, Storage banner |
| 19 | **Notes Page (images, PDF)** | Rich notes: title + content + optional images/PDF (pick from gallery/files, camera), stored in Firebase Storage `notes/{uid}/`, Firestore `users/{uid}/notes/{noteId}` with `imageUrl`, `pdfUrl`, search. OCR future. | Firestore + Storage |
| 20 | **Coding Exercise (chat/image OCR → model)** | Exercise where prompt is text or image (code photo). Image taken via camera/gallery → OCR (ML Kit Text Recognition) → extracted code → sent to Gemini 3.7 Flash for correction/explanation/hint. Also chat format where student types code and model reviews. | ML Kit OCR + Gemini |
| 21 | **Video Lectures (Full Library)** | Browse all video lectures aggregated, filter by subject/class/format, search, continues from Home/Learn/Course, progress tracking. | Same as #15 |

UI must follow Stitch link for all 21 screens (export via MCP if available, else manual Tailwind→Compose translation).

---

## 4. Subscription & Access Control

| Tier | Price | Access |
|---|---|---|
| Free | Rs 0 | Limited videos/theory, limited MCQs, no AI chat, no papers, no offline |
| Pro | Rs 199–499/mo (with 50% promo when active) | All videos, full bank, AI chat, papers+PDF, offline, notes |

Flow: `PaymentScreen` → `createMandate` (PhonePe/Razorpay) → UPI mandate → webhook `paymentWebhook` verifies signature → `payments` + `users.subscription{plan:pro,status:active,mandateId,expiresAt}` → FCM. Cron `checkExpiry` daily 02:00 IST: warn 7/3/1d, 3d grace → `expired` → downgrade → gates (`isPro` lessons/papers require `active`).

---

## 5. Dual Backend — Firebase + Supabase (Summary — Details in BACKEND_SCHEMA.md)

- **Firebase (primary, real-time, offline):** Auth, Firestore (14 collections), Storage (videos/notes/pdfs), FCM, Functions (Node 20 TS). Source of truth for app runtime.
- **Supabase (secondary, relational + vector):** Postgres (mirrored `users`, `payments`, `aiChats` for analytics/SQL reporting, `notes` full-text search, `pgvector` for AI chat semantic search/RAG future), Auth mirror (optional), Storage backup, Realtime for admin dashboards.
- Sync: Functions on Firestore write → Supabase via `supabase-js` service role (upsert). No client writes directly to Supabase without RLS.

---

## 6. Non-Functional

Perf: cold start <2s, sandbox p95 <4s, LCP <2.5s (web future). Offline: Room cache for lessons/theory. A11y WCAG 2.1 AA, large tap 48dp, NeoBrutalish alternative still AA. Security: rules + Zod + DPDP. Availability 99.5%.

---

## 7. AI Instructions (for agent)

```
- Implement ALL 21 features — hard requirement, no omission, each screen in kotlin-app/ui/screens/*
- Stitch https://stitch.withgoogle.com/projects/17415965411885249153 is primary design source (MCP export, else translate tokens)
- Gemini ONLY gemini-3.7-flash, key AQ.Ab8...PQ goes ONLY in local.properties -> BuildConfig.GEMINI_API_KEY + Secret Manager, never commit, never log
- Backend: Firebase (primary) + Supabase (secondary) — schemas in BACKEND_SCHEMA.md, sync via Functions
- UPI AutoPay via PhonePe/Razorpay — dual gateway, mandate pattern, webhook idempotent + signature, grace 3d
- Code editor must be full-screen, syntax highlight (Java/Python/JS/SQL), line numbers, full space
- OCR coding exercise via ML Kit + Gemini
- Update .agent/* + .agents/* after every task — memory.md routes, logs.md dev session, CHANGELOG.md
- See TRD.md, APP_FLOW.md, BACKEND_SCHEMA.md for authoritative spec
```
