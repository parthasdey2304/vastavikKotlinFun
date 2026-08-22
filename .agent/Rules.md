# Rules.md — Agent Guardrails — vastavikComputers

> **Version:** 0.1.0 | **Date:** 2026-08-22

---

## 1. Tech Stack Lock (Approved Only)

| Layer | Approved | Version |
|---|---|---|
| Android | Kotlin, Jetpack Compose, Coroutines+Flow, Hilt, Room, Retrofit, Coil, Media3 ExoPlayer | Kotlin 2.0+, Compose BOM 2024.09+ |
| Web | Next.js (App Router), React, TypeScript, Tailwind CSS, Zustand, Zod | Next 15, React 19, TS 5, Tailwind 4 |
| Backend | Firebase Auth, Firestore, Cloud Functions (Node 20+ TS), Cloud Storage, FCM | firebase 11, functions 5, admin 12 |
| AI | Gemini 3.7 Flash (`gemini-3.7-flash`) via `@google/generative-ai` | Latest |
| Payments | Razorpay or Cashfree (UPI AutoPay) | Latest SDK |
| Code exec | Docker sandbox on Cloud Run | — |
| Charts | Recharts | — |
| Validation | Zod | v3 |
| Icons | lucide-react (web), Material Symbols (Android) | — |

### Prohibited — NEVER Use

`Express.js`, `Redux` (use Zustand), `MongoDB`, `PostgreSQL` (Firestore only), `SwiftUI`, `Flutter` (deprecated — do not extend `lib/`), `React Native`, `Vue`, `Angular`, `any non-Gemini LLM` (no OpenAI, Claude, Gemini 1.5/2.0/2.5 — only `gemini-3.7-flash`), `Prisma`, `Supabase`.

If tempted to use a prohibited tech, STOP and ask.

---

## 2. Coding Standards

### 2.1 General

- Type-safe everywhere — no `any` (TS) / no unchecked casts (Kotlin).
- Doc comments on every public function/class (`/** ... */` / `/** ... */`).
- No magic numbers — extract to named constants.
- Functions <= 50 lines; files <= 300 lines; cyclomatic complexity <= 10.
- No `console.log` in prod — use `logs` collection or structured logger.
- Absolute imports only (TS: `@/lib/...`, `@/components/...`).
- Every PR must pass lint + typecheck + tests.

### 2.2 Kotlin (Android)

- Google Kotlin style guide. `val` over `var`. Null-safety via `?` / `let`.
- Coroutines + Flow for async; **no** RxJava.
- Compose only — no Views/XML. Material3 theming.
- Hilt for DI — no manual service locators.
- Package by feature: `ui.screens.lessons`, `data.repository`, `domain.usecase`.
- Room for local cache; Retrofit + Moshi for network.
- File naming: `PascalCase.kt` for screens/components, `camelCase` for functions/vars.

### 2.3 TypeScript / Next.js (Web)

- `strict: true` in `tsconfig.json`. No `any`, no `// @ts-ignore` without justification.
- App Router conventions: `layout.tsx`, `page.tsx`, `route.ts`, `loading.tsx`, `error.tsx`.
- Zod validation at every API boundary (`parse` / `safeParse`).
- Tailwind utilities only — no CSS modules / styled-components unless justified.
- Zustand stores typed; repository functions typed (`Promise<Result<T>>` pattern).
- File naming: `kebab-case` for routes, `PascalCase.tsx` for components, `camelCase.ts` for utils/hooks. Firestore collections `camelCase` plural.

### 2.4 Cloud Functions

- TypeScript strict, per-file single responsibility (one trigger per file).
- Zod validation on every input (`https.onCall` data + webhook body).
- Admin SDK only on server; never expose service-account JSON.
- Secrets via Secret Manager / `functions:secrets:set` — never env hardcode.
- Structured logging to `logs` collection.

---

## 3. Prohibited Actions (12)

| # | Prohibition | Enforcement |
|---|---|---|
| 1 | **No API keys/secrets in repo** — `GEMINI_API_KEY`, Razorpay secret, service-account JSON | Pre-commit hook + CI secret scan (gitleaks) |
| 2 | **No deploy without tests passing** | CI gates: `npm test` + `lint` + `typecheck` |
| 3 | **No disabling Firestore/Storage security rules** — never `allow read, write: if true` | PR review + rules unit tests |
| 4 | **No plaintext passwords** — Firebase Auth only | Code review |
| 5 | **No hardcoded collection names** — use constants from `shared/constants.ts` | Lint rule |
| 6 | **No `any` / `@ts-ignore` without tracked TODO** | `tsc --noImplicitAny` + ESLint |
| 7 | **No business logic in UI** — move to usecase/repository/store | Review |
| 8 | **No skipping tests for new features** — min coverage per Testing table | CI coverage gate |
| 9 | **No modifying `SECURITY.md` without explicit approval** | CODEOWNERS |
| 10 | **No non-Gemini model** — only `gemini-3.7-flash` | Grep CI check `gemini-3\.7-flash` |
| 11 | **No direct push to `main`** — PR via `develop` or `feature/*` | Branch protection |
| 12 | **No deleting files without checking references** (grep before delete) | Review checklist |

---

## 4. Agent Execution Parameters

| Parameter | Value |
|---|---|
| **Max files per task** | 10 |
| **Max lines per PR** | 600 (split if larger) |
| **Required checks** | `lint`, `typecheck`, `test`, `build` |
| **Branch naming** | `feature/<kebab-case>`, `fix/<kebab-case>`, `chore/<kebab-case>`, `release/vX.Y.Z` |
| **Commit format** | Conventional Commits: `feat:`, `fix:`, `chore:`, `docs:`, `security:` |
| **Post-task** | Update `memory.md` (routes + decisions) + `logs.md` (dev session) + `CHANGELOG.md` if release |
| **PR template** | Description, testing, screenshots, checklist (security + a11y) |

---

## 5. File Naming Conventions

| Context | Convention | Example |
|---|---|---|
| Kotlin class/file | `PascalCase.kt` | `LessonScreen.kt`, `GeminiRepository.kt` |
| TS component | `PascalCase.tsx` | `VideoPlayer.tsx` |
| TS util/hook/store | `camelCase.ts` | `useGemini.ts`, `authStore.ts` |
| Next.js route | `kebab-case` folder | `app/code-exec/route.ts` |
| Firestore collection | `camelCase` plural | `codeSubmissions`, `questionPapers` |
| Firestore field | `camelCase` | `classLevel`, `correctIndex` |
| Branch | `kebab-case` | `feature/ai-chat-streaming` |
| Env var | `UPPER_SNAKE` | `GEMINI_API_KEY` |

---

## 6. Testing Requirements

| Layer | Framework | Minimum Coverage | Command |
|---|---|---|---|
| Web unit | Vitest + Testing Library | 70% lines | `npm run test --workspace=web` |
| Web e2e | Playwright | Critical flows (auth, pay, editor) | `npx playwright test` |
| Android unit | JUnit + Turbine (Flow) | 60% lines | `./gradlew test` |
| Android UI | Compose UI Test | Key screens | `./gradlew connectedAndroidTest` |
| Functions | Vitest + firebase-functions-test | 80% lines | `npm run test --workspace=functions` |
| Security rules | `@firebase/rules-unit-testing` | All 14 collections | `npm run test:rules` |
| Sandbox | Integration (Docker) | All 4 languages | `npm run test:sandbox` |

No feature is "done" without its tests.

---

## 7. Git Workflow

```
main  (protected, prod)  <── release/vX.Y.Z  <── develop  <── feature/*, fix/*
 |                              |                |
 tags vX.Y.Z              version bump      PRs squash-merge
 CI: build+test+deploy    CI: build+test   CI: lint+test
```

- `main` deploys to prod (Vercel prod + Firebase prod).
- `develop` is integration branch; preview deploys.
- `feature/*` branched from `develop`; PR to `develop`.
- Releases: `develop` -> `release/vX.Y.Z` -> `main` (tag).

---

## 8. Code Review Checklist (every PR)

- [ ] Tech stack lock respected (no prohibited deps)
- [ ] `gemini-3.7-flash` only (if AI touched)
- [ ] Zod validation at boundaries
- [ ] Security rules updated if Firestore touched
- [ ] Tests added + passing
- [ ] No secrets in diff (gitleaks)
- [ ] `memory.md` updated if routes/collections changed
- [ ] A11y checked (contrast, keyboard, labels)
