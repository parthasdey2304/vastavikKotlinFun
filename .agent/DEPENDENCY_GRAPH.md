# DEPENDENCY_GRAPH.md — Dependency Mapping — vastavikComputers

> **Version:** 0.1.0 | **Date:** 2026-08-22

---

## 1. Monorepo Structure

```
D:\vastavikComputers/
├── .agent/                 # 12 docs (PRD, ARCHITECTURE, Design, Rules, memory, PLAN, SECURITY, logs, DEPENDENCY_GRAPH, CHANGELOG, VERSION, README)
├── web/                    # Next.js 15 web app
├── kotlin-app/             # Android Kotlin + Compose app (android/ alias)
├── functions/              # Firebase Cloud Functions (Node 20 TS)
├── shared/                 # Zod schemas, TS types, constants (cross-platform)
├── .github/workflows/      # CI/CD (lint, typecheck, test, build, deploy)
├── docs/                   # Optional extra docs
├── firebase.json
├── firestore.rules
├── firestore.indexes.json
├── storage.rules
├── .gitignore
└── README.md
```

Legacy Flutter (`lib/`, `pubspec.yaml`, `android/` Flutter, `web/` Flutter, `ios/`, `linux/`, `macos/`, `windows/`) is deprecated — retained for reference until migration complete.

---

## 2. Cross-Platform Module Relationship

```
         +----------------+      +----------------+
         |  Android App   |      |   Web App      |
         |  kotlin-app/   |      |    web/        |
         | Kotlin+Compose |      | Next.js+React  |
         +-------+--------+      +--------+-------+
                 |                        |
                 |  Firebase SDK / HTTPS  |
                 +-----------+------------+
                             |
              +--------------+--------------+
              |      Firebase Backend       |
              | Auth | Firestore | Storage  |
              | Functions | FCM | Hosting   |
              +---+------+------+-----+-----+
                  |      |      |     |
        +---------+ +----+ +----+ +---+--------+
        | Gemini  | |Docker| |Razor | | Stitch |
        | gemini- | |Sandbox| |Cash  | | Design |
        | 3.7-fl | |CloudRun| | UPI  | |  MCP   |
        +---------+ +------+ +------+ +--------+
```

---

## 3. Web App Dependencies

### 3.1 Production

| Package | Version | Purpose |
|---|---|---|
| `next` | 15.x | App Router, SSR, API routes |
| `react` | 19.x | UI |
| `react-dom` | 19.x | DOM |
| `typescript` | 5.x | Types |
| `tailwindcss` | 4.x | Styling |
| `firebase` | 11.x | Client SDK (Auth, Firestore, Storage, FCM) |
| `zustand` | 5.x | State (auth, editor, chat) |
| `zod` | 3.x | Validation (all API boundaries) |
| `lucide-react` | latest | Icons |
| `@google/generative-ai` | latest | Gemini 3.7 Flash client (via proxy) |
| `@monaco-editor/react` | latest | Code editor |
| `react-markdown` | latest | Theory rendering |
| `rehype-highlight` | latest | Code syntax highlight in markdown |
| `recharts` | latest | Admin charts |
| `pdfkit` | latest | PDF generation (papers) |
| `date-fns` | latest | Date formatting |
| `clsx` | latest | Conditional classes |

### 3.2 Dev

| Package | Version | Purpose |
|---|---|---|
| `vitest` | latest | Unit tests |
| `@testing-library/react` | latest | Component tests |
| `@playwright/test` | latest | E2E |
| `eslint` | 9.x | Lint |
| `eslint-config-next` | 15.x | Next lint |
| `prettier` | 3.x | Format |
| `prettier-plugin-tailwindcss` | latest | Tailwind class sort |

---

## 4. Android App Dependencies (Gradle)

| Dependency | Version | Purpose |
|---|---|---|
| `androidx.compose:compose-bom` | 2024.09+ | Compose |
| `androidx.navigation:navigation-compose` | 2.8+ | Nav |
| `androidx.lifecycle:lifecycle-viewmodel-compose` | 2.8+ | ViewModel+Compose |
| `com.google.dagger:hilt-android` + `hilt-compiler` | 2.51+ | DI |
| `com.google.firebase:firebase-bom` | 33+ | Auth, Firestore, Storage, Messaging |
| `com.squareup.retrofit2:retrofit` + `converter-moshi` | 2.11+ | HTTP (if needed beyond Firebase) |
| `com.squareup.moshi:moshi-kotlin` | 1.15+ | JSON |
| `io.coil-kt:coil-compose` | 2.7+ | Images |
| `androidx.media3:media3-exoplayer` | 1.4+ | Video |
| `androidx.room:room-runtime` + `room-compiler` + `room-ktx` | 2.6+ | Local cache |
| `org.jetbrains.kotlinx:kotlinx-coroutines-android` | 1.8+ | Async |
| `androidx.compose.material3:material3` | latest | Material3 |

---

## 5. Cloud Functions Dependencies

| Package | Version | Purpose |
|---|---|---|
| `firebase-admin` | 12.x | Admin SDK (Firestore, Auth, Storage) |
| `firebase-functions` | 5.x | Triggers, callable, scheduled |
| `@google/generative-ai` | latest | Gemini 3.7 Flash |
| `zod` | 3.x | Input validation |
| `razorpay` | latest | UPI AutoPay (or `cashfree-pg`) |
| `pdfkit` | latest | PDF gen |
| `dockerode` | latest | Docker sandbox (if sandbox in same service) |

**Runtime:** Node 20, TypeScript 5 strict.

---

## 6. Internal Module Dependencies

### 6.1 Web App

```
app/layout.tsx
  └─> components/ui/*, lib/firebase.ts, stores/authStore
app/(student)/* , app/admin/*
  └─> components/{video,editor,chat,admin}/*
       └─> hooks/* (useAuth, useGemini, useCodeExec)
            └─> stores/* (Zustand)
                 └─> lib/{firebase, validators, gemini}
                      └─> shared/schemas (Zod)
                           └─> types/*
```

- `shared/schemas` is leaf — no deps.
- `lib/validators.ts` imports `shared/schemas`.
- Components never import `firebase-admin` (server-only in `lib/firebase-admin.ts` + API routes).

### 6.2 Android App

```
ui.navigation.NavGraph
  └─> ui.screens.* (Screen + ViewModel)
       └─> domain.usecase.* (GetLessons, ChatWithAi, ExecuteCode)
            └─> data.repository.*Impl
                 └─> data.remote.* (Firebase DS, Retrofit) + data.local.* (Room DAO)
                      └─> domain.model + data.model
```

- `domain` has no dependency on `data` or `ui`.
- `data` depends on `domain/model`.
- Hilt wires `data` impls to `domain` interfaces.

---

## 7. External Service Dependencies

| Service | Used By | Auth | SLA |
|---|---|---|---|
| **Firebase Auth** | Web + Android + Functions | Firebase SDK, ID token | 99.95% |
| **Firestore** | Web + Android + Functions | Security rules + Admin SDK | 99.99% |
| **Cloud Storage** | Web + Android + Functions | Storage rules + Admin SDK | 99.95% |
| **Cloud Functions** | Web + Android (callable) | Firebase callable auth | 99.95% |
| **FCM** | Functions (`sendPush`, triggers) -> Web+Android | Server key / Admin SDK | 99.95% |
| **Gemini API** (`gemini-3.7-flash`) | `geminiChat` CF | `GEMINI_API_KEY` (Secret Manager) | Google SLA |
| **Payment Gateway** (Razorpay/Cashfree) | `createMandate`, `paymentWebhook` | API key + webhook signature | Gateway SLA |
| **Docker Sandbox** (Cloud Run) | `executeCode` CF | Internal (no public) | 99.95% |
| **Google Stitch** (design) | Design workflow | Browser auth, MCP | — |

---

## 8. Dependency Update Policy

- **Dependabot** weekly PRs for npm/Gradle.
- **Manual review** for major: `next`, `react`, `firebase`, `kotlin`, `compose-bom`.
- **Lockfiles** committed: `package-lock.json`, `gradle.lockfile` (or version catalog).
- **Audit:** `npm audit` + `gradle dependencyCheck` on every PR; no `high` without justification.
