# TESTING_PLAN.md — Checking & Testing Plan — vastavikComputers

> **Version:** 0.2.0 | **Rule:** Test after EACH work done (M1..M8), not only at launch. Blocks merge if fail.

---

## 1. After-Every-Work Rule

| When | Required Checks (must pass) | Gate |
|---|---|---|
| After each milestone task (e.g., after M4 editor) | `build` + `lint` + `unit` for that module + manual smoke (see §2) | PR cannot merge to `develop` if any red |
| After each milestone (M1..M8) | Full `test` suite + `connectedAndroidTest` for touched screens + rules test + sandbox test if editor touched | Tag milestone only if green |
| Before `main` deploy | Full + Play internal track beta + security checklist | — |

Update `BUGS.md` and `logs.md` §5 dev session on every check.

---

## 2. Smoke Checklists Per Milestone

### M1 Dual Backend

- [ ] `./gradlew assembleDebug` success
- [ ] Firestore `promotions`, `appConfig`, `notes` CRUD via emulator
- [ ] Supabase `users` row appears after Firestore user create (supabaseSync)
- [ ] `supabaseSync` failure logs to `logs` with retry

### M2 Auth/Nav/Theme

- [ ] Splash → Welcome if no auth, → Home if auth, blocks if forceUpdate
- [ ] Login (email, Google), Signup (class picker), Forgot (email sent), UserSetup, Logout, Delete
- [ ] Theme toggle Light/Dark/NeoBrutalish persists after kill

### M3 Home/Learn/Videos

- [ ] Home shows banners + PromoPopup dismiss 24h, search works, pull refresh
- [ ] Learn nodes lock/unlock correctly
- [ ] VideoLesson 3 tabs play (VS Code 16:9, Whiteboard 16:9, Shorts 9:16 vertical)

### M4 Editor/AI Chat/OCR (critical)

- [ ] Editor fills entire space, line numbers correct, highlight 4 langs, Run shows output, draft persists
- [ ] AI Chat streams from gemini-3.7-flash (key from BuildConfig), code blocks copy, 50/day limit shows snackbar, no key in logcat
- [ ] OCR: photo -> text preview editable -> send -> Gemini explains

### M5 Practise/Notes/Notifications

- [ ] MCQ instant feedback, Papers timed auto-submit, Coding tests pass
- [ ] Notes: add image + PDF, appears in list, Supabase FTS finds by title substring, image zoom/PDF intent works
- [ ] FCM: new_lesson + promo data message shows PromoPopup, bell count updates

### M6 Payments

- [ ] Payment shows 50% slashed price when promo active, toggle Razorpay/PhonePe, mandate creates, webhook updates subscription, grace/downgrade simulated by setting expiresAt past + running checkExpiry logic locally, lock appears on Pro content when expired

### M7 Profile/Admin/Polish

- [ ] Profile stats correct, Edit persists, Admin dashboard visible only for admin claim, non-admin blocked
- [ ] Offline: airplane mode shows cached lessons

---

## 3. Automated Tests

| Layer | Framework | Min Coverage | Command | What to test (21 features) |
|---|---|---|---|---|
| Unit (kotlin) | JUnit + Turbine | 60% | `./gradlew test` | ViewModels (Auth, Chat, Editor, Payment), Repositories, ThemePreferences, Promo logic, Supabase sync mapper |
| UI (Compose) | compose-ui-test | key screens | `./gradlew connectedAndroidTest` | Splash nav, Home promo carousel, Editor line numbers, Settings toggles, Payment sheet |
| Functions | Vitest + functions-test | 80% | `npm run test --workspace=functions` | geminiChat rate limit, paymentWebhook sig, checkExpiry, supabaseSync |
| Rules | rules-unit-testing | 100% collections (14+2) | `npm run test:rules` | All Firestore collections including promotions/appConfig/notes/progress |
| Sandbox | integration | all 4 langs | `npm run test:sandbox` | Timeout, OOM, no-net, truncation 1MB |
| E2E (if web later) | Playwright | critical flows | `npx playwright test` | Auth, payment |
| OCR | instrumented | — | `connectedAndroidTest` | ML Kit extract + Gemini call mock |

CI: `.github/workflows/ci.yml` runs `lint` + `test` + `assemble` on every PR to `develop`. Branch protection blocks merge on fail + requires `BUGS.md` updated if bug found.

---

## 4. Testing After Building & Implementation (Post-Build Report)

After each M, create a section in `TEST_REPORT.md` (or append to `BUGS.md`):

```
## M4 — 2026-08-30 — PASS (3 warnings)
- Build: OK
- Tests: 42 pass, 2 skipped
- Manual: Editor full-screen OK, OCR photo 1 failed (handwriting) -> filed BUG-07
- Coverage: 62%
- Next: M5
```

Tag `v0.X.Y` only if report is PASS or PASS with warnings (no FAIL). FAIL blocks next milestone.

---

## 5. Security & Compliance Checks (each milestone if touches)

- No secret in repo (`gitleaks` CI)
- Firestore rules test green
- Gemini key not in APK (check BuildConfig obfuscation, not logged)
- DPDP: minors consent, 90-day aiChats TTL tested
- Supabase RLS: query as anon non-owner fails for notes
