# TRD.md — Technical Requirement Document — vastavikComputers

> **Version:** 0.2.0 | **Date:** 2026-08-22 | **Root:** D:\vastavikComputers
> **Stack Lock:** Kotlin + Compose (kotlin-app/), Firebase + Supabase dual, Gemini 3.7 Flash (`gemini-3.7-flash`), UPI AutoPay (PhonePe/Razorpay)

---

## 1. Architecture Overview

```
Android (kotlin-app/)  --Firebase SDK / HTTPS-->  Firebase (Auth/Firestore/Storage/FCM/Functions Node20)
        |                                              |
        |  Supabase-JS (read analytics, search)         +-- Functions: onUserCreate, onContentPublish, geminiChat, executeCode, paymentWebhook, checkExpiry, supabaseSync
        +---> Supabase (Postgres + pgvector + Storage + Auth mirror + Realtime)
        |
        +---> Google AI Studio (gemini-3.7-flash) via BuildConfig.GEMINI_API_KEY (local.properties -> Secret Manager)
        +---> Razorpay / PhonePe SDK (UPI AutoPay)
        +---> ML Kit OCR (coding exercise image -> text -> Gemini)
        +---> ExoPlayer + YouTube Player (3 video formats)
```

Monorepo `D:\vastavikComputers`: `kotlin-app/`, `functions/`, `shared/` (Zod schemas if web added), `.agent/` + `.agents/`, `.github/workflows/`.

---

## 2. Android Tech Stack (kotlin-app/)

| Layer | Tech | Version | Purpose |
|---|---|---|---|
| Language | Kotlin | 2.0+ | |
| UI | Jetpack Compose + Material3 | BOM 2024.12.01 | All 21 screens |
| State | ViewModel + StateFlow + Compose State | lifecycle 2.8.7 | |
| DI | Hilt | 2.52 | AppModule |
| Nav | Navigation Compose | 2.8.5 | AppNavHost |
| Firebase | BOM | 33.7.0 | Auth, Firestore, Storage, Messaging, Analytics, Google SignIn 21.3.0 |
| Image | Coil Compose | 2.7.0 | |
| Video | YouTube Player + ExoPlayer (Media3 future) | youtube 12.1.1 | 3 formats |
| AI | GenerativeAI | 0.9.0 | gemini-3.7-flash |
| OCR | ML Kit Text Recognition | 16.0+ (add) | Coding exercise |
| Lottie | lottie-compose | 6.6.2 | Splash |
| Coroutines | kotlinx-coroutines | 1.9.0 | |
| Serialization | kotlinx-serialization-json | 1.7.3 | |
| Supabase | supabase-kt or supabase-js via Functions | 2.x | Secondary backend |

---

## 3. System Requirements

### 3.1 Functional — 21 Features Map to Screens

| # | Feature | Screen File(s) | ViewModel | Data |
|---|---|---|---|---|
| 1 | Splash | `auth/SplashScreen.kt` | — | checks Auth, appConfig |
| 2 | Login | `auth/LoginScreen.kt` | `AuthViewModel` | `AuthRepository` Firebase |
| 3 | Signup | `auth/SignupScreen.kt` | `AuthViewModel` | Firebase |
| 3ii | Forgot | `auth/ForgotPasswordScreen.kt` | `AuthViewModel` | Firebase |
| 4 | Home generative | `home/HomeScreen.kt` | `HomeViewModel` | Firestore courses/banners, Supabase analytics |
| 5 | Learn/DuolingoPath | `learning/LearningPathScreen.kt` + `ui/components/DuolingoPath.kt` | `LearningViewModel` | `FirestoreRepository` |
| 6 | Profile | `profile/ProfileScreen.kt` | `ProfileViewModel` | `users/{uid}` |
| 7 | Practise | `practice/PracticeScreen.kt` + `quiz/*` | `PracticeViewModel`, `QuizViewModel` | `mcqs`, `questionPapers` |
| 8 | Code Editor | (NEW) `screens/editor/CodeEditorScreen.kt` | `EditorViewModel` | sandbox Cloud Run |
| 9 | AI Chat | `chat/ChatScreen.kt` | `ChatViewModel` | `ChatModel`, `generativeai` gemini-3.7-flash |
| 10-11 | Settings + Theme | `onboarding/SettingsScreen.kt` + `ui/theme/*` + `utils/ThemePreferences.kt` | `SettingsViewModel` | DataStore |
| 12 | Notifications | (NEW) `screens/notifications/NotificationsScreen.kt` | — | `notifications` + FCM |
| 13 | App Update | (NEW) `screens/onboarding/AppUpdateScreen.kt` | — | Firestore `appConfig` |
| 14-15,21 | Course + Videos | `video/VideoLessonScreen.kt` | `VideoLessonViewModel` | `courses`, `lessons`, `videos` |
| 16 | Promo Pop | (NEW) `ui/components/PromoPopup.kt` | — | `promotions` |
| 17 | Payment | `onboarding/PaymentScreen.kt` + `PaymentHistoryScreen.kt` | — | Razorpay/PhonePe + `payments` |
| 18 | Promotions | part of Home + Payment | — | `promotions` |
| 19 | Notes | `onboarding/MyNotesScreen.kt` | — | `users/{uid}/notes` + Storage |
| 20 | OCR Exercise | (NEW) `screens/editor/OcrExerciseScreen.kt` | — | ML Kit + Gemini |
| — | Admin | `onboarding/AdminDashboardScreen.kt` | — | Firestore admin |

Missing screens must be created (editor, notifications, appUpdate, promoPop, ocr).

### 3.2 Non-Functional

| Category | Target |
|---|---|
| Perf | Cold start <2s, frame <16ms, sandbox p95 <4s |
| Offline | Room cache for lessons/theory (add), Firestore offline persistence |
| Security | Rules + Zod (if web) + no key in repo, BuildConfig only |
| A11y | WCAG 2.1 AA, 48dp tap, contentDescription, dynamic font |
| Size | APK <35MB (R8) |
| MinSdk 24, Target 35, Compile 35, JVM 17 |

---

## 4. Key Technical Decisions

| Decision | Rationale |
|---|---|
| **Firebase primary + Supabase secondary** | Firebase for real-time/offline app runtime; Supabase Postgres for SQL analytics, full-text notes search, pgvector RAG — best of both, see BACKEND_SCHEMA |
| **gemini-3.7-flash via Google AI Studio** | 1M context, $0.75/$3.75 intro through 2026-12-31, agentic coding — key `AQ.Ab8RN6...PQ` via `local.properties` -> BuildConfig, never committed |
| **Compose + Hilt + StateFlow** | Modern, same as current kotlin-app scaffold |
| **YouTube Player + ExoPlayer** | VS Code/Whiteboard/Shorts may be YouTube hosted; ExoPlayer for HLS/Storage videos |
| **ML Kit OCR** | On-device, free, fast for coding exercise image -> text |
| **Razorpay + PhonePe abstraction** | PaymentGateway interface to swap, mandate UPI AutoPay pattern shared |
| **DataStore for theme** | Light/Dark + NeoBrutalish toggle persisted |

---

## 5. API & Integration Specs

| Integration | Spec |
|---|---|
| **Gemini** | `GenerativeModel(modelName="gemini-3.7-flash", apiKey=BuildConfig.GEMINI_API_KEY)` — streaming, system instruction "Class 5–12 tutor Java/Python/JS/SQL, refuse off-topic", safety HIGH, PII redaction, tokenCount track |
| **Supabase** | `supabase-kt` or via Functions `supabase-js`: URL + anon key (limited) + service role (Functions only). Tables mirror Firestore (see BACKEND_SCHEMA). RLS: `auth.uid() = user_id` |
| **Payments** | Interface `PaymentGateway { createMandate(plan), handleResult, verifyWebhook }` impl `RazorpayGateway` + `PhonePeGateway`. Webhook CF verifies signature, idempotent via `gatewayPaymentId`. |
| **OCR** | `com.google.mlkit:text-recognition:16.0.0`, `InputImage.fromBitmap`, `recognizer.process`, result text → Gemini prompt "Fix/explain this code: {ocrText}" |
| **FCM** | `FirebaseMessagingService`, topics `class_{level}`, `subject_{id}`, token stored `users/{uid}.fcmTokens` |

---

## 6. Constraints & Risks

| Risk | Mitigation |
|---|---|
| Stitch MCP may not export clean Compose | Manual token translation (colors, typography from Design.md) |
| Gemini key leak if committed | `local.properties` + `.gitignore` + CI gitleaks + BuildConfig, rotate if leaked |
| Supabase sync drift | Functions `supabaseSync` onWrite trigger + nightly reconciliation job |
| Payment gateway approval delay | Start sandbox early, abstract interface, mock gateway for dev |
| OCR accuracy on handwriting | Fall back to manual edit of OCR text before sending to Gemini |

---

## 7. Deployment

Android: `assembleDebug` for dev, `assembleRelease` + Play internal track for prod, `versionCode` bump, `google-services.json` per flavor (prod/staging). Functions: `firebase deploy --only functions`. Supabase: migrations via `supabase db push`.
