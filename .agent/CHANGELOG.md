# CHANGELOG.md â€” vastavikComputers

> Format: [Keep a Changelog](https://keepachangelog.com/en/1.1.0/) | Versioning: [Semantic Versioning](https://semver.org/)

---

## [Unreleased]

### Planned

- **Phase 1 â€” Foundation & Scaffolding (v0.2.0):** Monorepo `web/`, `kotlin-app/`, `functions/`, `shared/`; Firebase project `vastavik-prod`+`staging`; Next.js 15 + Kotlin scaffolds (see PLAN.md).
- **Phase 2 â€” Core Student Features (v0.3.0):** Content data, video player (HLS/ExoPlayer), theory Markdown.
- **Phase 3 â€” Interactive Learning (v0.5.0):** Monaco/Compose editor + Docker sandbox, Gemini 3.7 Flash AI chat (`gemini-3.7-flash`), MCQs, coding questions.
- **Phase 4 â€” Papers & PDF (v0.6.0):** Question paper builder, timed attempts, `pdfkit` PDF export.
- **Phase 5 â€” Payments (v0.7.0):** UPI AutoPay (Razorpay/Cashfree), `checkExpiry` cron, Pro gating.
- **Phase 6 â€” Admin (v0.8.0):** `/admin` dashboard, Recharts, user/content/payment management, FCM sender.
- **Phase 7 â€” Notifications & Polish (v0.9.0):** FCM, offline (Room + SW), gamification, WCAG 2.1 AA.
- **Phase 8 â€” Launch (v1.0.0):** Tests, security audit, beta, Play Store + prod deploy.

---

## [0.1.0] â€” 2026-08-22 â€” Init

### Added

- `.agent/PRD.md` â€” Full product spec: 3 video formats, editor+sanbox, Gemini 3.7 Flash AI chat, MCQs, coding Qs, papers, theory, UPI AutoPay tiers, admin, FCM, NFRs, tech stack, AI instructions.
- `.agent/ARCHITECTURE.md` â€” System diagram, Android (MVVM+Clean) + Web (App Router) + Functions folder structures, Firebase services table, 14 Firestore collections with schemas, API routes (Next.js + CF), cross-platform notes, 4 user journey flows.
- `.agent/Design.md` â€” Stitch project link (https://stitch.withgoogle.com/projects/17415965411885249153) + MCP workflow, design references (getdesign.md, stitch.com, tasteskill.dev, 21st.dev, v0.dev/templates, moonsites.ai, UI/UX Pro Max, Karpathy Skill), 6 principles, tokens (colors/typography/spacing), component library (12 components x Web+Android), 25+ screen inventory, responsive table, design-to-code workflow, animations, icons.
- `.agent/Rules.md` â€” Tech stack lock + prohibited list (Express/Redux/Mongo/Postgres/SwiftUI/Flutter/RN/Vue/Angular/non-Gemini), coding standards (General/Kotlin/TS/CF), 12 prohibited actions, execution params, naming conventions, testing table, Git workflow (main<-develop<-feature).
- `.agent/memory.md` â€” Project state table, backend routes tracker (6 Next.js routes + 8 CFs + 14 collections, all `Planned`), decisions log (8 decisions), bug history, evolution notes (2026-08-22 kickoff), active TODOs (10 items).
- `.agent/PLAN.md` â€” 8-phase roadmap (Phase 0 DONE -> Phase 8 v1.0.0) with per-phase checkboxes, milestone summary (21 weeks), risks.
- `.agent/SECURITY.md` â€” 6 principles, auth (Firebase + admin claims + API keys), encryption (at rest/in transit/field), FULL Firestore rules (14 collections, helpers `isSignedIn`/`isAdmin`/`isOwner`/`hasActiveSubscription`), sandbox isolation (Docker specs + 4 languages + validation), API rate limits, AI chat threats (7 rows), DPDP Act 2023 (minimization/consent/retention/rights), vuln management, 10-item checklist.
- `.agent/logs.md` â€” Log format, 4 levels, 20 event types, runtime stream (empty), dev session log (2026-08-22), retention policy (90d/1y/7y/permanent).
- `.agent/DEPENDENCY_GRAPH.md` â€” Monorepo tree, cross-platform diagram, web prod+dev deps (16+7), Android deps (11), Functions deps (7), internal module trees (web + Android), external service map (9 services), update policy.
- `.agent/CHANGELOG.md` â€” This file.
- `.agent/VERSION.md` â€” Version 0.1.0, SemVer, 8 release tags, platform versions, locked dep versions, deployment targets.
- `.agent/README.md` â€” Quick start (prereqs, Firebase setup, env templates, run commands, admin access), project structure, docs table (12 files), tech stack, features, scripts, Gemini config, Stitch source.

### Researched

- **Gemini 3.7 Flash** confirmed: model ID `gemini-3.7-flash`, released 2026-08-13, 1M token context, coding & agentic workflows, intro pricing $0.75/1M input through 2026-12-31.
- **Google Stitch** confirmed: free AI UI tool, exports to React/Tailwind/HTML/Figma, MCP server for design-to-code. Project #17415965411885249153 requires browser login.
- **UPI AutoPay** â€” NPCI recurring mandate via Razorpay/Cashfree, webhook + scheduled expiry pattern validated.

### Configuration

- **Project root:** `D:\vastavikComputers` (monorepo: `web/`, `kotlin-app/`, `functions/`, `shared/`, `.agent/`). Legacy Flutter retained but deprecated.
- **Workspace sync:** Docs also at `D:\vastavikKotlinFun\.agent` for current workspace.
- **UI source:** https://stitch.withgoogle.com/projects/17415965411885249153

---

## Version History

| Version | Date | Codename | Phase | Highlights |
|---|---|---|---|---|
| 0.1.0 | 2026-08-22 | Init | Phase 0 | 12 .agent docs created |
| 0.2.0 | 2026-08-22 | Agent Design | M0 | 21 features + dual backend + 12 new docs (24 total), TRD/APP_FLOW/BACKEND_SCHEMA etc. |

---

## How to Update

- On each release, move `Unreleased` items to a new `[X.Y.Z] â€” YYYY-MM-DD` section with `Added/Changed/Fixed/Security`.
- Update `VERSION.md` in same PR.
- Commit `CHANGELOG.md` + `VERSION.md` + `memory.md` together.
