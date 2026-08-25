# vastavikComputers — README

> **EdTech platform for Class 5–12 — Learn Java, Python, JavaScript, SQL — 21 features, full-screen editor, OCR, promo pop, 50% promos, PhonePe/Razorpay AutoPay**
> **Platforms:** Android (Kotlin + Compose) + Web (Next.js 15) | **Backend:** Dual Firebase (primary) + Supabase (secondary Postgres/pgvector/FTS) | **AI:** Gemini 3.7 Flash (`gemini-3.7-flash`) | **Payments:** UPI AutoPay

---

## 1. Project Description

vastavikComputers teaches programming to Indian school students (Class 5–12) through 3 video formats (VS Code coding, Whiteboard explanation, Shorts), a sandboxed code editor, Gemini 3.7 Flash AI tutor, MCQs, coding questions, full question papers (PDF), and theory materials. Monthly subscription via UPI AutoPay. Admin panel at `/admin`.

**Repo:** https://github.com/parthasdey2304/vastavikComputers | **Root:** D:\vastavikComputers | **Design:** https://stitch.withgoogle.com/projects/17415965411885249153

> **Note:** Legacy Flutter app (`lib/`, `pubspec.yaml`, `android/` Flutter, `web/` Flutter) is **deprecated**. New apps: `kotlin-app/` (Android) + `web/` (Next.js). `functions/` = Cloud Functions, `shared/` = Zod schemas.

---

## 2. Prerequisites

| Tool | Version | Check |
|---|---|---|
| **Node.js** | 20+ LTS | `node -v` |
| **npm** | 10+ | `npm -v` |
| **JDK** | 21 | `java -version` |
| **Android Studio** | Hedgehog+ | — |
| **Kotlin** | 2.0+ | via Android Studio |
| **Firebase CLI** | latest | `firebase --version` (`npm i -g firebase-tools`) |
| **Git** | latest | `git --version` |
| **Docker** | latest (for sandbox) | `docker --version` |

---

## 3. Quick Start

### 3.1 Clone & Install

```bash
git clone https://github.com/parthasdey2304/vastavikComputers.git
cd vastavikComputers

# Web
cd web && npm install && cd ..

# Functions
cd functions && npm install && cd ..

# Android — open kotlin-app/ in Android Studio, sync Gradle
```

### 3.2 Firebase Setup

```bash
firebase login
firebase use --add   # select vastavikcomputers

# Enable in Firebase Console: Auth (Email/Password + Google), Firestore, Storage, FCM

# Deploy rules & indexes
firebase deploy --only firestore:rules,firestore:indexes,storage
```

### 3.3 Environment Configuration

**Web — `web/.env.local`** (create from template, never commit):

```env
# Firebase client (public — from Console > Project settings > SDK)
NEXT_PUBLIC_FIREBASE_API_KEY=...
NEXT_PUBLIC_FIREBASE_AUTH_DOMAIN=...
NEXT_PUBLIC_FIREBASE_PROJECT_ID=vastavikcomputers
NEXT_PUBLIC_FIREBASE_STORAGE_BUCKET=...
NEXT_PUBLIC_FIREBASE_MESSAGING_SENDER_ID=...
NEXT_PUBLIC_FIREBASE_APP_ID=...
NEXT_PUBLIC_FIREBASE_MEASUREMENT_ID=...

# Server only (Secret Manager / Vercel env — never NEXT_PUBLIC_)
GEMINI_API_KEY=...            # from aistudio.google.com/apikey
RAZORPAY_KEY_ID=...
RAZORPAY_KEY_SECRET=...
# or CASHFREE_APP_ID / CASHFREE_SECRET
ADMIN_EMAILS=admin@vastavikcomputers.com
```

**Functions — `functions/.env`** or Secret Manager:

```bash
firebase functions:secrets:set GEMINI_API_KEY
firebase functions:secrets:set RAZORPAY_KEY_SECRET
```

**Android — `kotlin-app/app/google-services.json`** — download from Firebase Console > Project settings > Android app, place at `kotlin-app/app/google-services.json` (gitignored).

### 3.4 Run Dev Servers

```bash
# Web (http://localhost:3000)
cd web && npm run dev

# Functions emulator (http://localhost:5001, UI http://localhost:4000)
firebase emulators:start --only auth,firestore,functions,storage

# Android — open kotlin-app/ in Android Studio -> Run
```

### 3.5 Access Admin Panel

1. Register a user, then set admin claim:
   ```bash
   firebase functions:shell
   # or call callable setAdmin from web (if already admin)
   ```
   Or via Admin SDK script: `admin.auth().setCustomUserClaims(uid, { admin: true })`.
2. Login on web, go to `http://localhost:3000/admin` (middleware checks `admin` claim + `ADMIN_EMAILS`).

---

## 4. Project Structure

```
D:\vastavikComputers/
├── .agent/                 # 12 docs — PRD, ARCHITECTURE, Design, Rules, memory, PLAN, SECURITY, logs, DEPENDENCY_GRAPH, CHANGELOG, VERSION, README
├── web/                    # Next.js 15 (App Router) — (auth)/, (student)/, admin/, api/{gemini,code-exec,payment,pdf}
├── kotlin-app/             # Android Kotlin + Compose — di/, data/, domain/, ui/
├── functions/              # Cloud Functions (Node 20 TS) — auth/, content/, ai/, payments/, code/, admin/
├── shared/                 # Zod schemas + types (web + functions share)
├── firebase.json
├── firestore.rules
├── firestore.indexes.json
├── storage.rules
├── .github/workflows/
└── README.md
```

See `ARCHITECTURE.md` for full folder trees + Firestore data model (14 collections).

---

## 5. Key Documentation (`.agent/`)

| File | Purpose |
|---|---|
| `PRD.md` | Product spec — features, tiers, NFRs, AI instructions |
| `ARCHITECTURE.md` | System diagram, folder structures, 14 collections, API routes, user flows |
| `Design.md` | Stitch link, tokens (colors/type/spacing), components, 25+ screens, responsive, animations |
| `Rules.md` | Stack lock, coding standards, 12 prohibitions, testing, Git workflow |
| `memory.md` | Project state + **backend routes tracker** (Next.js + CF + Firestore) + decisions + TODOs |
| `PLAN.md` | 8-phase roadmap (0 DONE -> 8 v1.0.0) + milestones |
| `SECURITY.md` | Auth, encryption, FULL Firestore rules (14 collections), sandbox, DPDP Act 2023, checklist |
| `logs.md` | Log format, levels, 20 event types, runtime + dev session logs, retention |
| `DEPENDENCY_GRAPH.md` | Monorepo tree, module diagrams, all deps (web/Android/Functions), external services |
| `CHANGELOG.md` | Keep a Changelog — v0.1.0 + Unreleased |
| `VERSION.md` | Current 0.1.0, SemVer, 8 release tags, locked deps, deploy targets |
| `README.md` | This file |

---

## 6. Tech Stack

| Layer | Tech |
|---|---|
| Android | Kotlin 2.0+, Jetpack Compose, Coroutines+Flow, Hilt, Room, Retrofit, Coil, Media3 ExoPlayer |
| Web | Next.js 15 (App Router), React 19, TypeScript 5, Tailwind CSS 4, Zustand, Zod |
| Backend | Firebase Auth, Firestore, Cloud Functions (Node 20 TS), Cloud Storage, FCM |
| AI | Gemini 3.7 Flash (`gemini-3.7-flash`) via `@google/generative-ai` |
| Payments | UPI AutoPay via Razorpay/Cashfree |
| Code exec | Docker sandbox (Cloud Run) — no net, RO FS, 256M, 10s |
| Charts | Recharts |

---

## 7. Features

- **3 video formats:** VS Code coding (10–25m), Whiteboard (10–20m), Shorts (1–2m) — HLS/ExoPlayer
- **Code editor:** Monaco (web) / Compose (Android) — Java/Python/JS/SQL, Docker sandbox, `codeSubmissions`
- **AI chat:** Gemini 3.7 Flash (`gemini-3.7-flash`) — Class 5–12 scoped, rate-limited, `aiChats`
- **MCQs:** Instant feedback + explanation, topic-wise, `mcqs`
- **Coding questions:** Starter code, hidden/visible tests, auto-grade, `codingQuestions`
- **Question papers:** Builder, timed mode, PDF via `pdfkit`, `questionPapers`
- **Theory:** Markdown + `react-markdown` + `rehype-highlight`, searchable, bookmarkable
- **Subscription:** Free vs Pro, UPI AutoPay, webhook + `checkExpiry` cron
- **Admin:** `/admin` (custom claim) — users, content CRUD, paper builder, analytics (Recharts), payments, FCM sender
- **Notifications:** FCM — new lesson, expiry, reminders, streaks
- **Security:** Firestore rules (14 collections), DPDP Act 2023, sandbox isolation

---

## 8. Available Scripts

### Web (`web/`)

| Script | Command |
|---|---|
| Dev | `npm run dev` |
| Build | `npm run build` |
| Typecheck | `npm run typecheck` (`tsc --noEmit`) |
| Lint | `npm run lint` |
| Tests | `npm run test` (Vitest) |
| E2E | `npx playwright test` |

### Functions (`functions/`)

| Script | Command |
|---|---|
| Build | `npm run build` |
| Emulators | `firebase emulators:start` |
| Tests | `npm run test` |
| Deploy | `firebase deploy --only functions` |

### Android (`kotlin-app/`)

| Action | Command |
|---|---|
| Build | `./gradlew assembleDebug` |
| Tests | `./gradlew test` |
| Instrumented | `./gradlew connectedAndroidTest` |

---

## 9. Gemini 3.7 Flash Configuration

| Field | Value |
|---|---|
| **Model ID** | `gemini-3.7-flash` — **only this model** |
| **Context window** | 1M tokens |
| **Pricing (intro)** | $0.75 / 1M input, $3.75 / 1M output through 2026-12-31 |
| **Release** | August 13, 2026 — coding & agentic workflows |
| **Safety** | `HARM_CATEGORY_*` = `BLOCK_MEDIUM_AND_ABOVE` |
| **System prompt scope** | Class 5–12 programming tutor (Java/Python/JS/SQL) — refuse off-topic |
| **Rate limit** | 50 requests / day / user, 100K tokens / day / user |
| **Max conversation** | 1M tokens (model limit); app truncates at 100K per chat |
| **API key** | `aistudio.google.com/apikey` -> Secret Manager `GEMINI_API_KEY` |
| **Proxy** | `POST /api/gemini` (Next.js) -> `geminiChat` CF -> Generative AI API |

See `SECURITY.md` §7 for AI chat security (injection, PII, off-topic, abuse).

---

## 10. UI Design Source

- **Primary:** Google Stitch — https://stitch.withgoogle.com/projects/17415965411885249153 — open in browser (Google login required), exports to React/Tailwind/HTML/Figma, MCP server for design-to-code. See `Design.md` §8 for workflow.
- **Additional:** `getdesign.md`, `stitch.com`, `tasteskill.dev`, `21st.dev`, `v0.dev/templates`, `moonsites.ai`, **UI/UX Pro Max**, **Andrej Karpathy Skill** — see `Design.md` §2.

---

## 11. Contributing

Branch: `feature/<kebab-case>` from `develop` -> PR to `develop` (no direct push to `main`). See `Rules.md` §7–8 for naming, checks, checklist. Update `memory.md` + `logs.md` in same PR if routes/collections changed.

---

## 12. License

TBD — see `LICENSE` at repo root.
