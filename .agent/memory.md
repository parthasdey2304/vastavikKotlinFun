# memory.md — Persistent State Memory — vastavikComputers

> **Purpose:** Retained context across agent sessions. Backend routes tracker + decisions + bugs + TODOs.
> **Update this file after EVERY task that adds/changes routes, collections, or decisions.**

---

## 1. Project State Summary

| Field | Value |
|---|---|
| **Name** | vastavikComputers |
| **Root** | D:\vastavikComputers |
| **Repo** | https://github.com/parthasdey2304/vastavikComputers |
| **Platforms** | Android (Kotlin + Compose, kotlin-app/) — Flutter (`lib/`) deprecated, Web (Next.js 15 future) |
| **Backend** | **Dual:** Firebase (primary — Auth, Firestore, Storage, FCM, Functions Node 20) + Supabase (secondary — Postgres, pgvector, FTS, Realtime, Storage mirror) — synced via `supabaseSync` CF |
| **AI model** | Gemini 3.7 Flash (`gemini-3.7-flash`, 1M context, released 2026-08-13, via Google AI Studio, key `AQ.Ab8...PQ` in local.properties → BuildConfig, Secret Manager) |
| **Payments** | UPI AutoPay via **PhonePe / Razorpay** (monthly mandate, 50% promotions, access revoked if not paid, 3-day grace) |
| **UI source** | Google Stitch https://stitch.withgoogle.com/projects/17415965411885249153 + 21st.dev + v0.dev + DESIGN_IDEAS.md |
| **Current version** | 0.2.0 (Agent Design — 21 features, dual backend) — see VERSION.md |
| **Status** | Phase 0 DONE (0.1.0) + v0.2.0 Agent Design DONE 2026-08-22 — 24 docs in .agent/.agents, ready for M1 |

---

## 2. Backend Routes Tracker

> **CRITICAL — Continuously updatable.** Mark status `Planned` → `In Progress` → `Live`.

### 2.1 Next.js API Routes (`web/app/api/` — future, but spec kept)

| Route | Method | Status | Description | Input (Zod) | Output |
|---|---|---|---|---|---|
| `/api/gemini` | POST | Planned | Proxy AI chat to `geminiChat` CF | `{ message: string, chatId?: string }` + ID token | `{ reply: string, chatId: string, tokenCount: number }` |
| `/api/code-exec` | POST | Planned | Proxy code execution to `executeCode` | `{ language: enum, code: string, questionId?: string }` | `{ output: string, testResults?: [] }` |
| `/api/payment/create` | POST | Planned | Create UPI mandate (PhonePe/Razorpay) | `{ plan: "pro" }` + ID token | `{ mandateId, checkoutUrl }` |
| `/api/payment/webhook` | POST | Planned | Gateway webhook (signature verified, idempotent) | Gateway payload + signature | `{ received: true }` |
| `/api/pdf/generate` | POST | Planned | Generate question paper PDF (admin) | `{ paperId: string }` + admin token | `{ pdfUrl: string }` |
| `/api/admin/questions/bulk` | POST | Planned | Bulk import MCQs/coding Qs (admin) | `{ type: "mcq"|"coding", data: [] }` | `{ imported: number }` |

### 2.2 Cloud Functions (`functions/src/`)

| Function | Trigger | Status | Description |
|---|---|---|---|
| `onUserCreate` | `auth.user().onCreate` | Planned | Create `users/{uid}` doc with free plan, send welcome notification |
| `onContentPublish` | `firestore.document(''lessons/{id}'').onUpdate` | Planned | When `isPublished` → true, FCM to subscribed class/subject |
| `checkExpiry` | `pubsub.schedule(''0 2 * * *'')` Asia/Kolkata | Planned | Daily 02:00 IST — mark expired, grace 3d, downgrade, FCM 7/3/1d |
| `geminiChat` | `https.onCall` | Planned | Validate, rate-limit 50/day, call `gemini-3.7-flash`, update `aiChats` + Supabase mirror |
| `executeCode` | `https.onCall` | Planned | Validate, forward to Docker sandbox (Cloud Run), return output |
| `setAdmin` | `https.onCall` (admin) | Planned | Set custom claim `admin: true` |
| `sendPush` | `https.onCall` (admin) | Planned | Send FCM (topic/token/segment) |
| `bulkImport` | `https.onCall` (admin) | Planned | Bulk import questions |
| `supabaseSync` | `firestore.document("{col}/{id}").onWrite` (users,payments,aiChats,notes,logs,progress,promotions) | Planned | Upsert/delete to Supabase Postgres via service role, idempotent |

### 2.3 Firestore Collections (14 + 2 new) — Read/Write Matrix

| Collection | Read By | Written By | Status |
|---|---|---|---|
| `users` | Owner (own doc), Admin (all) | `onUserCreate` (CF), Owner (limited), Admin | Planned |
| `subjects` | Any signed-in | Admin | Planned |
| `courses` | Any signed-in | Admin | Planned |
| `topics` | Any signed-in | Admin | Planned |
| `lessons` | Any signed-in (if not isPro) / Pro (if isPro) | Admin | Planned |
| `theory` | Any signed-in (if lesson not isPro) / Pro | Admin | Planned |
| `mcqs` | Any signed-in | Admin | Planned |
| `codingQuestions` | Any signed-in | Admin | Planned |
| `questionPapers` | Pro only | Admin | Planned |
| `aiChats` | Owner only | Owner + `geminiChat` CF | Planned |
| `codeSubmissions` | Owner only | Owner + `executeCode` CF | Planned |
| `notifications` | Owner only | System (CF) + Admin | Planned |
| `payments` | Owner (own) + Admin | System (webhook CF) | Planned |
| `logs` | Admin only | System (all CFs) | Planned |
| `promotions` | Any signed-in | Admin | Planned (NEW v0.2.0) |
| `appConfig` | Any signed-in | Admin | Planned (NEW v0.2.0) |
| `users/{uid}/notes` | Owner only | Owner | Planned (NEW v0.2.0) |
| `users/{uid}/progress` | Owner only | Owner + system | Planned (NEW v0.2.0) |

**Supabase mirrors:** `users`, `payments`, `ai_chats` (+ vector), `notes` (FTS), `promotions`, `logs`, `progress` — see BACKEND_SCHEMA.md §5.

---

## 3. New Docs in v0.2.0 (21-feature scope)

| Doc | Purpose |
|---|---|
| `TRD.md` | Technical Requirement Document — stack, 21 screens map, integrations (Gemini, Supabase, PhonePe/Razorpay, ML Kit OCR) |
| `UI_UX_DESIGN.md` | 21-screen inventory, tokens, NeoBrutalish, full-screen editor spec, PromoPopup spec |
| `DESIGN_IDEAS.md` | Backlog for later (generative home evolutions, XP, etc.) |
| `APP_FLOW.md` | Nav graph + 16 detailed per-feature flows, deep links, state management |
| `BACKEND_SCHEMA.md` | Firebase 14+2 collections + Storage + Functions + Supabase Postgres (7 tables) + RLS + sync + secrets |
| `IMPLEMENTATION_PLAN.md` | M0..M8 milestone checklists with dual backend + OCR + PhonePe |
| `TESTING_PLAN.md` | After-each-work checks, smoke per milestone, automated layers |
| `GITHUB_WORKFLOW.md` | Branching, commits, CI, push after each step, tags |
| `BUGS.md` | Open/fixed tracker + tech debt TD-01..TD-07 |
| `TEST_REPORT.md` | Post-build report template + M0 PASS |

**21 Features Tracker (PRD §3):** Start, Login, Signup, Forgot (3ii), Home generative, Learn, Profile, Practise (MCQ+PYQ), Code Editor full (line numbers, 4 langs, full space), AI Chat gemini-3.7-flash, Settings, Theme (Light/Dark/NeoBrutalish+fonts), Notifications, App Update, Course, Course 3-format videos + MCQ/paper, Promo Pop (text/image/video), Payment UPI AutoPay PhonePe/Razorpay, Promotions 50%, Notes images/PDF, OCR exercise (ML Kit), Video lectures — all mapped in APP_FLOW §3 + TRD §3.1

---

## 4. Decisions Log

| Date | Decision | Rationale | Decided By |
|---|---|---|---|
| 2026-08-22 | **Gemini 3.7 Flash** (`gemini-3.7-flash`) as sole LLM | Released 2026-08-13, 1M context, coding-optimized, $0.75/$3.75 intro through 2026-12-31 | Architect |
| 2026-08-22 | **Firebase** + **Supabase dual** | Firebase for real-time/offline runtime; Supabase Postgres for SQL analytics, FTS notes, pgvector RAG — Functions `supabaseSync` keeps consistent | Architect |
| 2026-08-22 | **Kotlin + Compose** over Flutter/RN for Android | Native, Material3, ExoPlayer, team Kotlin; kotlin-app scaffold exists | Architect |
| 2026-08-22 | **Next.js 15 + React 19** for Web (future) | App Router, server components, Vercel | Architect |
| 2026-08-22 | **UPI AutoPay PhonePe/Razorpay abstracted** | India-first, need both gateways, mandate monthly, gateway interface swappable | Architect |
| 2026-08-22 | **Google Stitch** as primary UI source | Project #17415965411885249153 — MCP export | Architect |
| 2026-08-22 | **Three video formats** (VS Code / Whiteboard / Shorts 1–2m) | Pedagogy + revision | Product |
| 2026-08-22 | **Class 5–12**, 4 subjects |Focused curriculum | Product |
| 2026-08-22 | **Docker sandbox** for code exec | Isolation (no net, RO FS, 256M, 10s) | Architect |
| 2026-08-22 | **Monorepo** `D:\vastavikComputers` | Shared schemas, single repo | Architect |
| 2026-08-22 | **`.agent` + `.agents` 24 docs** | 12 original + 12 new for 21-feature thoroughness | Architect |
| 2026-08-22 | **Gemini key via local.properties** | Key `AQ.Ab8...PQ` never in repo, via BuildConfig + Secret Manager | Architect |
| 2026-08-22 | **21-feature scope locked** | Full rookie brief — editor full-screen, OCR ML Kit, promo pop variants, 50% promos, Notes images/PDF | Product |

---

## 5. Bug History

| Date | Bug | Severity | Status | Fix |
|---|---|---|---|---|
| 2026-08-22 | TD-01..TD-07 tech debt (missing 5 screens, Chat model upgrade, PhonePe toggle, Supabase not wired, NeoBrutalish, keystore, Storage rules) | P1–P2 | Open → moved to BUGS.md §4 | Planned M1..M6 |

See `BUGS.md` for live tracker.

---

## 6. Project Evolution Notes

### 2026-08-22 — v0.2.0 Agent Design (21 features, dual backend)

- Expanded `.agent` + `.agents` to 24 docs: updated PRD for 21 features, new TRD, UI_UX_DESIGN, DESIGN_IDEAS, APP_FLOW, BACKEND_SCHEMA (Firebase 14+2 + Supabase 7 tables + RLS + sync), IMPLEMENTATION_PLAN (M0..M8 with OCR/PhonePe), TESTING_PLAN, GITHUB_WORKFLOW, BUGS, TEST_REPORT. Synced to `.agents` + `D:\vastavikKotlinFun` workspace. Dual backend (Firebase+Supabase) and PhonePe/Razorpay abstract + 50% promos + OCR (ML Kit) + full-screen editor + promo pop variants locked. Key `AQ.Ab8RN...PQ` in local.properties only. Next: M1 scaffold + Supabase migrations → verify with TEST_REPORT M1.

### 2026-08-22 — Kickoff & .agent Creation (v0.1.0)

- Repo had Flutter app + kotlin-app stub + functions placeholder. Decision to deprecate Flutter, rebuild Kotlin+Compose. Created initial 12 docs, verified Gemini 3.7 Flash and Stitch. Next was Phase 1 scaffolding — now expanded to v0.2.0 21-feature plan.

---

## 7. Active TODOs

- [ ] Wire Supabase (migrations, RLS, supabaseSync CF, backfill) — M1
- [ ] Implement 5 missing screens: CodeEditor full (line numbers, 4 langs, full space), Notifications, AppUpdate, PromoPopup (text/image/video), OcrExercise (ML Kit) — M4/M5
- [ ] Upgrade Chat to gemini-3.7-flash with BuildConfig key `AQ.Ab8...PQ` — M4
- [ ] PhonePe+Razorpay abstraction + 50% promo + checkExpiry + Pro gates — M6
- [ ] Verify kotlin-app builds after each milestone, run TESTING_PLAN smoke
- [ ] Push after each milestone per GITHUB_WORKFLOW.md (tag v0.3.0..v1.0.0)
- [ ] Set up CI/CD (GitHub Actions: lint, test, build, gitleaks)
- [ ] Final Stitch export in browser and apply tokens to Theme.kt

---

## 8. How to Update This File

After any task that: adds/changes route/CF/collection/field/index → update §2; makes decision → append §4; fixes bug → update §5 + BUGS.md; completes milestone → update §1 status + §6 notes + §7 TODOs. Commit in same PR.
