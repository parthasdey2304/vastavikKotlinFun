# VERSION.md â€” Version Tracking â€” vastavikComputers

> **Repo:** https://github.com/parthasdey2304/vastavikComputers | **Root:** D:\vastavikComputers

---

## 1. Current Version

| Field | Value |
|---|---|
| **Version** | **0.2.0** |
| **Codename** | Agent Design (21 features) |
| **Release date** | 2026-08-22 |
| **Phase** | Phase 0 â€” Project Initialization â€” DONE |
| **Status** | Pre-development â€” docs only, no app code yet |
| **Build tag** | `v0.1.0-init` |
| **Git tag** | `v0.1.0` (to be created on push) |

---

## 2. Semantic Versioning

We use **SemVer** `MAJOR.MINOR.PATCH` + pre-release suffix:

- **MAJOR** â€” Breaking change (e.g., Firestore schema incompatible, auth overhaul) â€” `1.0.0`, `2.0.0`
- **MINOR** â€” New feature, backward compatible â€” `0.2.0`, `0.8.0`
- **PATCH** â€” Bug fix, backward compatible â€” `0.2.1`, `1.0.1`
- **Pre-release** â€” `0.9.0-alpha`, `0.9.0-beta`, `1.0.0-rc.1` â€” appended with `-alpha`, `-beta`, `-rc.N`

---

## 3. Release Tags & Build Metrics

| Phase | Version | Tag | Target Date | Key Deliverables | Build Status |
|---|---|---|---|---|---|
| 0 | 0.1.0 | `v0.1.0` | 2026-08-22 | 12 .agent docs, stack locked, 14 collections modeled | Done |
| 1 | 0.2.0 | `v0.2.0` | +2w | Monorepo + Firebase projects + Next.js + Kotlin scaffolds, emulators green | Planned |
| 2 | 0.3.0 | `v0.3.0` | +5w | Videos + theory browsable (HLS/ExoPlayer, Markdown) | Planned |
| 3 | 0.5.0 | `v0.5.0` | +9w | Editor+sandbox, Gemini 3.7 Flash chat, MCQs, coding Qs | Planned |
| 4 | 0.6.0 | `v0.6.0` | +11w | Papers + PDF export | Planned |
| 5 | 0.7.0 | `v0.7.0` | +14w | UPI AutoPay, expiry cron, Pro gating | Planned |
| 6 | 0.8.0 | `v0.8.0` | +17w | /admin full (Recharts, users, content, payments, FCM) | Planned |
| 7 | 0.9.0 | `v0.9.0-beta` | +19w | FCM, offline, gamification, a11y + perf pass | Planned |
| 8 | 1.0.0 | `v1.0.0` | +21w | Tests, security audit, beta, Prod + Play Store launch | Planned |

---

## 4. Platform Versions

| Platform | Current | Target (v1.0.0) |
|---|---|---|
| **Web (Next.js)** | â€” (not scaffolded) | 0.9.0-beta -> 1.0.0 |
| **Android (Kotlin)** | â€” (kotlin-app/ stub) | 0.9.0-beta -> 1.0.0 |
| **Cloud Functions** | â€” (functions/ placeholder) | 0.2.0 -> 1.0.0 |
| **Firestore Rules** | `firestore.rules.recommended` (legacy) | Custom rules (SECURITY.md) from 0.2.0 |
| **Stitch Design** | Project #17415965411885249153 (browser) | Exported to `web/components/` from 0.2.0 |

---

## 5. Dependency Versions Locked (v0.1.0)

| Dependency | Locked Version | Notes |
|---|---|---|
| **Gemini model** | `gemini-3.7-flash` | Released 2026-08-13, 1M context, $0.75/1M input intro |
| **Next.js** | 15.x (App Router) | â€” |
| **React** | 19.x | â€” |
| **Tailwind CSS** | 4.x | â€” |
| **TypeScript** | 5.x (`strict: true`) | â€” |
| **Firebase SDK (web)** | 11.x | `firebase` npm |
| **Firebase Admin** | 12.x | `firebase-admin` |
| **Firebase Functions** | 5.x | `firebase-functions` |
| **Kotlin** | 2.0+ | â€” |
| **Compose BOM** | 2024.09+ | â€” |
| **Hilt** | 2.51+ | â€” |
| **Node.js** | 20+ (Functions + Web) | LTS |
| **Zod** | 3.x | Shared schemas |
| **Zustand** | 5.x | Web state |

Update this table when bumping any major dep (with CHANGELOG entry).

---

## 6. Deployment Targets

| Target | Platform | Hosting | Region | Env |
|---|---|---|---|---|
| **Web prod** | Vercel (or Firebase Hosting) | `vastavikcomputers.vercel.app` / Hosting | auto / `asia-south1` | `vastavik-prod` |
| **Web staging** | Vercel preview / Hosting preview | Preview URL | auto | `vastavik-staging` |
| **Android prod** | Play Store | â€” | â€” | `vastavik-prod` |
| **Android beta** | Play internal track | â€” | â€” | `vastavik-staging` |
| **Cloud Functions** | Firebase | â€” | `us-central1` (or `asia-south1`) | per project |
| **Firestore** | Firebase | â€” | `asia-south1` (Mumbai) | per project |
| **Code Sandbox** | Cloud Run (Docker) | â€” | `asia-south1` | per project |
| **Storage** | Firebase | â€” | `asia-south1` | per project |

---

## 6.1 API Key (v0.2.0)

| Key | Value | Store |
|---|---|---|
| GEMINI_API_KEY | AQ.Ab8...PQ (Google AI Studio, gemini-3.7-flash) | kotlin-app/local.properties -> BuildConfig.GEMINI_API_KEY, Functions Secret Manager — never in repo, never in logcat |
| SUPABASE_URL / ANON_KEY | project URL + anon | local.properties (RLS-limited) |
| SUPABASE_SERVICE_ROLE_KEY | service role | Functions Secret Manager only |
| RAZORPAY_KEY_SECRET / PHONEPE_SALT | gateway secrets | Functions Secret Manager |

---

## 7. How to Release

1. Merge `feature/*` -> `develop` via PR (CI green).
2. Cut `release/vX.Y.Z` from `develop`, bump version in `VERSION.md` + `web/package.json` + `kotlin-app/build.gradle.kts` + `functions/package.json`.
3. Update `CHANGELOG.md` (move Unreleased -> `[X.Y.Z]`).
4. PR `release/vX.Y.Z` -> `main`, tag `vX.Y.Z`, deploy (Vercel auto + `firebase deploy` + Play Console).
5. Merge `main` back to `develop`.
