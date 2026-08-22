# IMPLEMENTATION_PLAN.md — Implementation Plan — vastavikComputers

> **Version:** 0.2.0 | **Date:** 2026-08-22 | **Extends:** PLAN.md (8-phase) — now with 21-feature checklists + dual backend + OCR
> **Stack:** kotlin-app/ (Kotlin+Compose), Firebase+Supabase dual, Gemini 3.7 Flash, UPI AutoPay PhonePe/Razorpay

---

## 1. Milestones (updated)

| Milestone | Version | Target | Deliverables | Status |
|---|---|---|---|---|
| M0 Agent Design | 0.2.0 | 2026-08-22 | All .agent/.agents docs (PRD, TRD, UI_UX_DESIGN, APP_FLOW, BACKEND_SCHEMA, etc.) | DONE (this file) |
| M1 Scaffold + Dual Backend | 0.3.0 | +1w | kotlin-app builds, Firebase + Supabase projects, supabaseSync CF, local.properties keys, appConfig + promotions collections | TODO |
| M2 Auth + Core Nav + Theme | 0.4.0 | +2w | Splash/Welcome/Login/Signup/Forgot/UserSetup, AppNavHost, App Update check, Light/Dark/NeoBrutalish via ThemePreferences, BottomNav | TODO |
| M3 Home Generative + Learn + Course/Videos | 0.5.0 | +3w | Home (banners, promo pop text/image/video, generative), LearningPath/DuolingoPath, Course, VideoLesson 3 formats (VS Code/Whiteboard/Shorts), theory | TODO |
| M4 Editor Full + AI Chat + OCR | 0.6.0 | +4w | CodeEditor full-screen line numbers/highlight 4 langs, sandbox, AI Chat gemini-3.7-flash (key AQ.Ab8...), OcrExercise ML Kit | TODO |
| M5 Practise + Notes + Notifications | 0.7.0 | +5w | MCQ/Papers/PYQ/Coding, MyNotes images/PDF (Firestore+Storage+Supabase FTS), Notifications list + FCM + promo pop triggers | TODO |
| M6 Payments + Promotions | 0.8.0 | +5.5w | Payment PhonePe/Razorpay UPI AutoPay, 50% promo, PaymentHistory, checkExpiry cron, Pro gates, access revoked if not paid | TODO |
| M7 Profile + Polish + Admin | 0.9.0-beta | +6.5w | Profile/Edit/PaymentHistory/AdminDashboard, Search, offline Room, a11y, Stitch polish | TODO |
| M8 Testing + Launch | 1.0.0 | +8w | Unit/UI/E2E/security/sandbox tests pass, beta track, Play Store prod | TODO |

---

## 2. Detailed Task Checklists (per milestone)

### M1 — Scaffold + Dual Backend

- [ ] Verify kotlin-app builds: `./gradlew assembleDebug` (already scaffolded: Auth, Home, Learn, Chat, Theme)
- [ ] Create Supabase project (ap-south-1), run migrations from BACKEND_SCHEMA.md §5.1 (users, payments, ai_chats + vector, notes FTS, promotions, logs, progress)
- [ ] Enable RLS + policies §5.2
- [ ] Add `SUPABASE_URL`, `SUPABASE_ANON_KEY` to `kotlin-app/local.properties` (gitignored) + `SUPABASE_SERVICE_ROLE_KEY` to Functions Secret Manager
- [ ] Functions: `supabaseSync` onWrite for 6 collections, backfill script
- [ ] Firestore: create `promotions`, `appConfig/current` (latestVersion=1.0.0), `users/{uid}/notes` + `progress` subcollections
- [ ] Storage rules already mirror §3 BACKEND_SCHEMA

### M2 — Auth + Nav + Theme

- [ ] Splash: Lottie, auth check, appConfig fetch (force update block), nav to Welcome vs Home vs UserSetup
- [ ] Welcome (already exists if not, reuse), Login/Google, Signup class 5–12, Forgot, UserSetup
- [ ] AppNavHost verified (all 21 routes added: editor, notifications, appUpdate, ocr, promoPop overlay)
- [ ] Theme: Light/Dark + NeoBrutalish toggle in Settings, persists via ThemePreferences DataStore, VastavikTheme reacts

### M3 — Home + Learn + Course/Videos

- [ ] Home: streak, continue, recommended, subject chips, banners carousel from `promotions`, search, pull refresh, PromoPopup auto-show
- [ ] DuolingoPath polish (already exists), Learn lock logic
- [ ] Course list + Course detail VideoLesson 3 tabs verified (youtube player + ExoPlayer), Shorts 9:16
- [ ] Theory Markdown + images

### M4 — Editor + AI Chat + OCR (critical)

- [ ] NEW `screens/editor/CodeEditorScreen.kt` — full-screen, weight 1f, line numbers, highlight Java/Python/JS/SQL, run → Docker Cloud Run, output sheet, draft save
- [ ] AI Chat upgrade: modelName="gemini-3.7-flash", BuildConfig.GEMINI_API_KEY (from `local.properties` key `AQ.Ab8...PQ`), streaming, markdown+code, tokenCount, 50/day limit
- [ ] NEW `screens/editor/OcrExerciseScreen.kt` — ML Kit dependency `com.google.mlkit:text-recognition:16.0.0`, camera/gallery pick, OCR editable preview, send to Gemini
- [ ] Chat image attach → OCR path

### M5 — Practise + Notes + Notifications

- [ ] PracticeScreen tabs MCQ/Papers/Coding polish, PYQScreen, QuizSetup/QuizTaking timed
- [ ] MyNotesScreen: upload image/PDF to Storage `notes/{uid}/`, Firestore note, Supabase FTS searchable (query via supabase-kt)
- [ ] NotificationsScreen + FirebaseMessagingService (FCM topics class_X, subject_X, token refresh), PromoPopup trigger via FCM data + Firestore

### M6 — Payments + Promotions

- [ ] PaymentScreen: abstract `PaymentGateway`, impls Razorpay + PhonePe (local.properties keys), plan card with 50% promo slashed price if promotion active, T&C, mandate creation, result handler
- [ ] Webhook CF `paymentWebhook` signature verify, idempotent, Supabase sync
- [ ] checkExpiry scheduled 02:00 IST (warn 7/3/1d, 3d grace → downgrade)
- [ ] Gates: `isPro` lessons/papers/code QA/ AI chat require `subscription.status==active` (Firestore rules + client check shows lock paywall)

### M7 — Profile + Polish + Admin

- [ ] Profile, EditProfile, PaymentHistory, PYQ, SearchResults polish
- [ ] AdminDashboard (claim admin): users, content, promotions CRUD, appConfig edit, send FCM
- [ ] Offline: Room for lessons/theory (add), Firestore offline, image caching (Coil)
- [ ] Stitch final pass, a11y, perf, dark & neo screenshots

### M8 — Testing + Launch

- See TESTING_PLAN.md — must pass before launch tag `v1.0.0`.

---

## 3. Dependency Order

```
M0 (docs done) -> M1 (dual backend) -> M2 (auth/nav) -> M3 (home/learn/videos) -> M4 (editor/chat/ocr) -> M5 (practise/notes/notif) -> M6 (payments) -> M7 (profile/admin/polish) -> M8 (testing/launch)
```

M4 and M5 can partially parallelize after M3. Payments (M6) needs M2 auth but not M5 strictly.

---

## 4. Risks (same as TRD + PLAN)

Stitch MCP instability -> manual translate; Gemini key leak -> gitleaks; Supabase drift -> supabaseSync + backfill; gateway approval -> mock.
