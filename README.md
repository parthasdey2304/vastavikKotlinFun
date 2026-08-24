# vastavikKotlinFun — Kotlin Edition 💻

> **Production-Grade Coding Education Platform for Class 5–12 (CBSE/ICSE)**

![Kotlin](https://img.shields.io/badge/Kotlin-0095D5?style=for-the-badge&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)
![Firebase](https://img.shields.io/badge/Firebase-FFCA28?style=for-the-badge&logo=firebase&logoColor=black)
![Supabase](https://img.shields.io/badge/Supabase-3ECF8E?style=for-the-badge&logo=supabase&logoColor=white)
![Mistral AI](https://img.shields.io/badge/Mistral%20AI-FF7000?style=for-the-badge&logo=mistralai&logoColor=white)

**vastavikKotlinFun** is the Kotlin + Jetpack Compose rebuild of [vastavikComputers](https://github.com/parthasdey2304/vastavikComputers) — a comprehensive coding education platform delivering video lessons, AI chat, code editor, quizzes, and notes. Handover for Antigravity regeneration (`HANDOVER_FOR_ANTIGRAVITY.md`).

---

## ✨ Key Features (21)

- 🎓 **Structured Learning Path** — Java/Python/C++ zigzag path with progress
- 📹 **Video Lessons** — 3 formats: Code (VS Code), Whiteboard (pinch-zoom), Shorts (9:16)
- 🤖 **Mistral AI Chat** — `mistral-small-latest` tutor (CBSE/ICSE, Java/Python/JS/SQL), temperature 0.3
- 💻 **Code Editor** — Full-screen, line numbers, 4 languages, syntax highlighting, Mistral run explanation
- 📝 **MCQ Quizzes** — Mistral-generated, timed, download + review with AI explanations
- 📄 **PYQ & Practice** — Past questions, difficulty filters
- 📓 **My Notes** — Create with images/PDF, local + Supabase FTS
- 🔍 **OCR Exercise** — ML Kit text extraction
- 🔒 **Auth** — Firebase Auth (Email/Google) + Supabase profiles
- 🎨 **Themes** — Light/Dark + Neo-Brutalist (6 accent colors: Yellow/Pink/Blue/Lime/Orange/Purple, 0 border radius, thick borders, hard shadows)
- 💬 **Comments** — Like/Dislike + YouTube Shorts-style bottom sheet + Mistral moderation
- 💸 **Payments** — UPI AutoPay (PhonePe/Razorpay), 50% promotions, `checkExpiry` cron
- 🔔 **Notifications & Updates** — FCM, in-app update
- 👤 **Profile & Settings** — Font scale, dark mode, neo toggle, color picker

---

## 🛠️ Tech Stack

| Layer | Technology | Purpose |
|---|---|---|
| **Frontend** | Kotlin + Jetpack Compose + Material3 | Native Android UI |
| **DI** | Hilt | Dependency injection |
| **Auth** | Firebase Auth | Email/Google sign-in |
| **Database** | Firebase Firestore + Supabase Postgres | Real-time + SQL analytics/FTS/pgvector |
| **AI** | Mistral `mistral-small-latest` via REST | Chat, code exec, quiz gen, moderation |
| **Storage** | Firebase Storage + Supabase Storage | Notes, media mirror |
| **Payments** | PhonePe / Razorpay UPI AutoPay | Mandates + webhooks |
| **Other** | ML Kit OCR, ExoPlayer, DataStore | OCR, video, prefs |

---

## 🏗️ Project Structure

```
vastavikKotlinFun/
├── kotlin-app/                 # Android app (package com.vastavik.computer)
│   ├── app/src/main/java/com/vastavik/computer/
│   │   ├── ui/screens/         # 21 screens (auth, home, learning, chat, editor, quiz, video...)
│   │   ├── ui/theme/           # VastavikColors, Theme, NeoBrutalistColors, NeoShapes
│   │   ├── data/               # models, repositories
│   │   └── utils/              # ThemePreferences, Constants
│   └── local.properties        # MISTRAL_API_KEY, GEMINI_API_KEY, sdk.dir
├── .agent/                     # 22 spec docs (PRD, TRD, APP_FLOW, BACKEND_SCHEMA...)
├── HANDOVER_FOR_ANTIGRAVITY.md # 7-section handover (21 pages, Stitch link, build steps)
└── vastavik-computers-debug.apk
```

---

## 🚀 Getting Started

1. Clone: `git clone https://github.com/parthasdey2304/vastavikKotlinFun.git`
2. Android Studio Ladybug+ / Kotlin 1.9+, JDK 17
3. Create `kotlin-app/local.properties`:
   ```
   sdk.dir=C:/Users/<you>/AppData/Local/Android/Sdk
   MISTRAL_API_KEY=your_mistral_key
   GEMINI_API_KEY=your_gemini_key
   ```
4. Add `google-services.json` to `kotlin-app/app/`
5. Run: `./gradlew assembleDebug` or `Run` in Android Studio
6. Install: `adb install -r kotlin-app/app/build/outputs/apk/debug/app-debug.apk`

**Stitch UI:** https://stitch.withgoogle.com/projects/17415965411885249153

---

## 📚 Docs

| Doc | Purpose |
|---|---|
| `.agent/PRD.md` | 21 features, video formats, AI, payments |
| `.agent/TRD.md` | Stack, screen map, integrations |
| `.agent/APP_FLOW.md` | Nav graph + 16 flows |
| `.agent/BACKEND_SCHEMA.md` | Firebase 14+2 collections + Supabase 7 tables |
| `.agent/PLAN.md` | M0–M8 milestones |
| `HANDOVER_FOR_ANTIGRAVITY.md` | Full handover for regen |

All `.agent/` docs are mirrored from `vastavikComputers/.agent`.

---

## 🔐 Security

- API keys via `local.properties` → `BuildConfig`, never committed. Secret Manager in prod.
- Firestore rules + Supabase RLS in `SECURITY.md`.
- Code sandbox: Docker, no net, RO FS, 256M, 10s timeout.

---

**Built with ❤️ for vastavikComputers — Class 5–12 coding education.**
