# HANDOVER — vastavikComputers → Antigravity

> **For:** Google Antigravity (or any AI coding agent) to regenerate the **Kotlin + Jetpack Compose Android app** and build a debug APK.
> **Repo:** `D:\vastavikKotlinFun` → https://github.com/parthasdey2304/vastavikKotlinFun
> **Stitch UI (single source of design):** https://stitch.withgoogle.com/projects/17415965411885249153 — translate ALL 21 screens from this project. If Stitch MCP is available, export Figma tokens directly; else manually map Tailwind → Compose Material3.
> **AI model:** `gemini-3.7-flash` only. API key `AQ.Ab8...PQ` — put ONLY in `kotlin-app/local.properties` as `GEMINI_API_KEY` → `BuildConfig.GEMINI_API_KEY`, never commit. Also add to Secret Manager for CI.
> **Original Kotlin app location (now removed for regen):** `kotlin-app/` — you must recreate it from scratch using this handover + `.agent/` docs.
> **Date:** 2026-08-22

---

## 1. How `.agent` / `.agents` work for Antigravity

This repo keeps **both** `.agent/` (22 docs, source of truth) and `.agents/` (identical mirror) so Antigravity finds whichever it looks for. **Do not delete either.**

| Doc | Purpose — read before coding |
|---|---|
| `PRD.md` | 21 features, subscription, dual backend summary |
| `TRD.md` | Tech stack, Gradle deps, data models, Cloud Functions |
| `UI_UX_DESIGN.md` + `Design.md` + `DESIGN_IDEAS.md` | Tokens, light/dark/NeoBrutalish, typography (Plus Jakarta Sans / Inter / JetBrains Mono) |
| `APP_FLOW.md` | Navigation graph, bottom nav, per-screen flows, deep links |
| `BACKEND_SCHEMA.md` | Firebase (primary) + Supabase (secondary) schemas, 14 Firestore collections, Storage, sync via Functions |
| `ARCHITECTURE.md` | MVVM + Hilt + StateFlow + Repository |
| `IMPLEMENTATION_PLAN.md` | Phased build order |
| `TESTING_PLAN.md` + `TEST_REPORT.md` + `BUGS.md` | QA |
| `SECURITY.md` + `Rules.md` + `memory.md` + `logs.md` | Constraints, DPDP, logging |

**Flow:** Read `PRD.md` → `APP_FLOW.md` → `UI_UX_DESIGN.md` + Stitch → `TRD.md`/`BACKEND_SCHEMA.md` → code `kotlin-app/`.

---

## 2. What to generate

Recreate `kotlin-app/` as a **Kotlin + Jetpack Compose** Android app (`com.vastavik.computer`, `minSdk 24`, `targetSdk 34`, Compose BOM 2024.10, Material3, Hilt, Navigation Compose, Firebase BoM 33.x, `generativeai:0.9.0` for Gemini, ML Kit text-recognition, Coil, ExoPlayer).

**Expect APK after:** `./gradlew assembleDebug` → `kotlin-app/app/build/outputs/apk/debug/app-debug.apk` (≈27 MB).

---

## 3. All 21 Pages — Spec + Stitch mapping

Follow Stitch for layout/colors/radius/shadows. Map Stitch Tailwind tokens → Compose `Theme.kt` (`VastavikColors` indigo #6366F1, light `#F8FAFC` / dark `#0F172A`, NeoBrutalish `radius 2dp + 4dp hard shadow + 2dp border`).

| # | Page | Route | Stitch screen | Must-have UI |
|---|---|---|---|---|
| 1 | **Splash (Start)** | `splash` | Stitch Splash | Center `Vastavik Computers` logo (LaptopChromebook icon) + tagline, gradient indigo, scale+alpha anim 2s, auth check → `welcome` or `home`. File: `ui/screens/auth/SplashScreen.kt` |
| 2 | **Login** | `login` | Stitch Login | Email + password fields, validation, Google Sign-In button, Forgot link → `forgot_password`, Sign Up link, error snackbar. `LoginScreen.kt` |
| 3 | **Sign Up** | `signup` | Stitch SignUp | Name, email, **class 5–12 picker**, password+confirm, T&C checkbox, Google, create `users/{uid}`. `SignupScreen.kt` |
| 3ii | **Forgot Password** | `forgot_password` | Stitch Forgot | Email input → `sendPasswordResetEmail`, confirmation, back to login. `ForgotPasswordScreen.kt` |
| 4 | **Home (Generative)** | `home` | Stitch Home | **Generative look:** Streak banner (flame ≥3), Continue Learning card, Recommended AI carousel, subject chips Java/Python/JS/SQL → Course filter, banners carousel (promotions 50%), search bar → `search`, bell → `notifications`, avatar → `profile`, **PromoPopup overlay** (X, text/image/video, CTA → `payment`). BottomNav: Home/Learn/Practice/Chat. Pull-refresh. `HomeScreen.kt` + `components/PromoPopup.kt` |
| 5 | **Learn (Learning Path)** | `learning_path` | Stitch Learn | Duolingo-style zigzag `DuolingoPath.kt` nodes, locked/unlocked, progress rings, tap → `video_lesson`. `LearningPathScreen.kt` |
| 6 | **Profile** | `profile` | Stitch Profile | Avatar, name/email/class, subscription badge, streak, edit → `edit_profile`, My Notes, Payment History, **Notifications**, **App Update**, **Code Editor**, **OCR Exercise**, Course, Settings, Admin (if claim), Logout/Delete. `ProfileScreen.kt` |
| 7 | **Practise (MCQ / PYQ)** | `practice` + `pyq` | Stitch Practice | Tabs MCQ / Papers(PYQ) / Coding; MCQ: topic→ `quiz_setup` → `quiz_taking` instant feedback; Papers: filter → timed quiz; `PracticeScreen.kt` + `PYQScreen.kt` + `quiz/*` |
| 8 | **Code Editor (Full-screen)** | `code_editor` | Stitch Editor | **Takes entire space** `weight(1f)`, top language picker Java/Python/JS/SQL, left **48dp gutter LazyColumn line numbers `${idx+1}`**, JetBrains Mono, dark `#1E1E2E`, `BasicTextField`, output bottom sheet 120–260dp (stdout/stderr/tests), FAB Run (mock + future Docker via CF), copy/clear. `editor/CodeEditorScreen.kt` |
| 9 | **AI Chat** | `chat` | Stitch Chat | Gemini **3.7 Flash** `GenerativeModel(modelName="gemini-3.7-flash", apiKey=BuildConfig.GEMINI_API_KEY)` — system prompt Class 5–12 tutor, streaming bubbles, markdown+code blocks with copy, history `aiChats`, 50/day limit. `chat/ChatScreen.kt` |
| 10 | **Settings** | `settings` | Stitch Settings | Toggles **Light/Dark**, **Modern / NeoBrutalish** (`isNeoBrutalish`), font scale slider, notifications, clear cache, about, version, logout. Persists `ThemePreferences` DataStore. `SettingsScreen.kt` + `SettingsViewModel.kt` |
| 11 | **Theme + Font** | — | Stitch Theme | `Theme.kt: VastavikTheme(darkTheme, neoBrutalish, fontScale)` — radius 12dp → 2dp in neo, `Color.kt`, `Type.kt` (Plus Jakarta/Inter/JetBrains Mono). `MainActivity.kt` collects flows. |
| 12 | **Notifications** | `notifications` | Stitch Notifications | List mock 5 types, unread dot, mark all read, tap → deep link. Bell count on Home. `notifications/NotificationsScreen.kt` |
| 13 | **App Update** | `app_update` | Stitch Update | Check `appConfig/latestVersion` Firestore, current `1.0.0`, changelog, Update → Play Store `LocalUriHandler`, **force blocks** if `isForceUpdate`. `AppUpdateScreen.kt` |
| 14 | **Course** | `course` | Stitch Courses | Subject list, class filter, search, card thumb+progress → topic. Reuses Learn. Route `course` → `LearningPathScreen`. |
| 15 | **Course Detail — 3 formats + MCQ/Paper** | `video_lesson/{id}/{title}/{subject}/{class}` | Stitch Video | **3 tabs:** `TabRow` VS Code 16:9 / Whiteboard 16:9 / **Shorts 9:16 1–2min** `ShortsTab()` vertical 200×360 black, player (ExoPlayer), badges, theory+MCQ/paper linkage. `VideoLessonScreen.kt` |
| 16 | **Promo Pop (Text/Image/Video)** | overlay | Stitch Promo | `PromoPopup` — `PromoData(imageUrl?, videoUrl?, ctaLink)`, `Dialog`, Coil `AsyncImage`, X cuttable, auto-show on Home if `promotions.isActive`, admin via FCM `type:promo`. |
| 17 | **Payment (PhonePe/Razorpay UPI AutoPay)** | `payment` | Stitch Payment | Plan card, **gateway toggle Razorpay/PhonePe** (Card `outlinedCardBorder().copy(width=2dp)` + check), slashed original price if **50% promo** `TextDecoration.LineThrough`, `UPI AutoPay` note, 3-day grace, history → `payment_history`. `PaymentScreen.kt` |
| 18 | **Promotions 50%** | — | Stitch Promos | Firestore `promotions/{id}` {title, discount 50, validTill, bannerImageUrl, isActive} — drives Home banner + Payment slashed + Popup. Admin creates. |
| 19 | **Notes (Images/PDF)** | `my_notes` | Stitch Notes | Rich notes: title/content + image/PDF (gallery/camera/file picker) → Storage `notes/{uid}/` + Firestore `users/{uid}/notes/{noteId}` {imageUrl,pdfUrl}, search, zoom. `MyNotesScreen.kt` |
| 20 | **OCR Exercise** | `ocr_exercise` | Stitch OCR | 2 tabs **Type Code** (chat editor → Ask Gemini) + **Photo OCR** (pick/camera → ML Kit → editable OCR preview → Send to Gemini). `OcrExerciseScreen.kt` |
| 21 | **Video Lectures Library** | via Home/Learn | Stitch Library | Aggregated filterable by subject/class/format chips, search, progress. Same as #15. |

**Navigation:** `ui/navigation/AppNavHost.kt` — 26 routes (see `APP_FLOW.md` §1). Splash startDestination via `AuthViewModel` decides. BottomNav `Home|Learn|Practice|Chat` + profile avatar. All 21 pages must be reachable from Home/Profile.

---

## 4. Build & APK steps (Antigravity must run)

```bash
# 1. Create kotlin-app from template (or stitch export)
# 2. Add local.properties (DO NOT COMMIT)
echo "sdk.dir=C:/Users/parth/AppData/Local/Android/Sdk" > kotlin-app/local.properties
echo "GEMINI_API_KEY=AQ.Ab8...PQ" >> kotlin-app/local.properties

# 3. Build debug APK
cd kotlin-app
./gradlew.bat assembleDebug --stacktrace

# 4. Output
# kotlin-app/app/build/outputs/apk/debug/app-debug.apk  (~27 MB)
# Also copy to root for handover:
# copy kotlin-app\app\build\outputs\apk\debug\app-debug.apk ..\vastavik-computers-debug.apk
```

**Key Gradle:** `app/build.gradle.kts` must include `id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")` + `buildConfig true`, `generativeai:0.9.0`, `firebase-bom`, `hilt`, `navigation-compose`, `coil`, `mlkit-text-recognition`, `exoplayer`. See `TRD.md` § deps.

**Verifies:** `BUILD SUCCESSFUL` — earlier build passed 50s after fixing `ChatScreen` `systemInstruction` (just pass `apiKey` + `modelName`, prepend system prompt in `generateContent`) and `OcrExercise` escaping.

---

## 5. Backend (dual — don’t skip)

* Firebase (primary): Auth, Firestore 14 collections (`users`, `courses`, `banners`, `promotions`, `notifications`, `appConfig`, etc.), Storage `videos/{vscode,whiteboard,shorts}`, `notes/{uid}/`, FCM, Functions Node20 TS — see `BACKEND_SCHEMA.md`.
* Supabase (secondary): Postgres mirror `users/payments/aiChats` + `pgvector` + full-text search, sync via `supabaseSync` Function on Firestore write.
* Payments: Razorpay/PhonePe UPI AutoPay mandate → webhook `paymentWebhook` verifies signature → `users.subscription{active,expiresAt}` → FCM. Cron `checkExpiry` 02:00 IST grace 3d.
* Rules + DPDP Act 2023 — parental consent, 90-day AI retention — see `SECURITY.md`.

---

## 6. Checklist for Antigravity before marking done

- [ ] `kotlin-app/` recreated at `D:\vastavikKotlinFun\kotlin-app`
- [ ] All 21 routes in `AppNavHost`, reachable from Home/Profile
- [ ] Stitch UI replicated (light/dark/neobrutalish + font scale)
- [ ] Code Editor full-screen with line numbers (`${idx+1}`) — no empty string bug
- [ ] AI Chat uses `BuildConfig.GEMINI_API_KEY` + `gemini-3.7-flash`
- [ ] Payment shows PhonePe/Razorpay toggle + 50% slashed
- [ ] Video has 3rd Shorts tab 9:16
- [ ] OCR has Type + Photo tabs
- [ ] `./gradlew assembleDebug` → `BUILD SUCCESSFUL`
- [ ] APK exists at `kotlin-app/app/build/outputs/apk/debug/app-debug.apk`
- [ ] `.agent/` + `.agents/` untouched (22 files each), no full API key committed
- [ ] Commit + push to `vastavikKotlinFun` `main`

---

## 7. Reference — last working commit

`a70fec4` (feat: add kotlin-app — full 21-screen UI) contained the working build — 81 files, `+10600` lines — use as diff reference if needed. Now removed for Antigravity regen; `.agent` docs remain authoritative.

> Handover prepared by Muse Spark (Opencode) — 2026-08-22. For questions, read `.agent/memory.md` + `.agent/logs.md`.
