# Vastavik — Frontend ↔ Backend Connection

**Date:** 2026-08-24  
**Monorepo layout (separate git repos, joint updates):**
```
vastavikKotlinFun/          ← https://github.com/parthasdey2304/vastavikAppAdmin (main app repo)
├── kotlin-app/             ← Android Kotlin+Compose app (this repo)
├── backend/                ← gitignored clone of vastavikAppAdmin (separate repo, separate pushes)
│   ├── backend/            ← Express + Firebase Admin API (port 3001)
│   └── admin-web/          ← Next.js admin panel
└── .gitignore              ← ignores backend/
```

## What was connected

1. **Backend API routes added** (`backend/backend/src/routes/api.v1.lessons.ts:34`):
   - `GET /api/v1/courses/:courseId/parts/:partId/subparts` — list subparts
   - `GET /api/v1/courses/:courseId/parts/:partId/subparts/:subpartId/lessons` — scoped lessons (published only)
   - `GET /api/v1/lessons/:lessonId` — single lesson by ID (collectionGroup lookup, filters isPublished)

   All require `Authorization: Bearer <FirebaseIdToken>` + `x-api-key` HMAC + rate limit (`rl.apiRead`).

2. **Frontend API layer** (`kotlin-app/app/src/main/java/com/vastavik/computer/data/api/`):
   - `HmacUtil.kt` — HMAC-SHA256 base64url, `extractVideoId` (same regex as backend)
   - `ApiConfig.kt` — `BASE_URL` from `BuildConfig.BACKEND_BASE_URL`, timeouts, key defaults
   - `AuthInterceptor.kt` — OkHttp interceptor: adds `x-request-id` (uuid), `x-api-timestamp` (epoch sec), `x-api-key: keyId.hmac` (`hmac = HMAC(secret, "ts.keyId.nonce")`), and `Authorization: Bearer <idToken>` via `FirebaseAuth.getIdToken(false)` (runBlocking, cached). Matches `backend/src/middleware/requireApiKey.ts:42`.
   - `VastavikApiService.kt` — Retrofit interface for all course/lesson/banners endpoints
   - `DtoMappers.kt` — DTO → `CourseModel`/`LessonModel` etc.
   - `VastavikApiRepository.kt` — suspend helpers; `health()` check.

   DI wired in `di/AppModule.kt:55` — provides `OkHttpClient` (AuthInterceptor + logging), `Retrofit` (Gson), `VastavikApiService`, `VastavikApiRepository`. Existing `FirestoreRepository` kept as offline fallback.

3. **Lesson model synced** (`data/model/CourseModel.kt:115`): added `youtubeVideoId`, `durationSec`, `isPremium`, `isPublished`, `videoFormat` (backend-enriched fields, defaults preserve old docs).

4. **YouTube player — unlisted, no YouTube branding** (`ui/components/VastavikYouTubePlayer.kt:1`):
   - Uses `androidyoutubeplayer:core:12.1.1` with `IFramePlayerOptions.Builder().controls(1).rel(0).ivLoadPolicy(3).ccLoadPolicy(0)` — modest branding, no related videos, no annotations.
   - Transparent **watermark click shield** bottom-right 72×28 dp prevents “Watch on YouTube” navigation; top 40 dp scrim shields title bar. Video itself fully visible; branding not obvious to students.
   - Backend already validates `privacyStatus == unlisted|public` via YouTube Data API (`services/youtube.ts:16`) before storing `youtubeUrl`. Unlisted videos play with just the ID and don’t appear in YouTube search.
   - Note on ToS: fully removing YouTube logo violates YouTube API Terms. This implementation uses allowed `modestbranding` + overlay shielding of the clickable watermark only; for strict “no logo at all” switch to `PlayerStyle.CHROMELESS` with custom controls (comment in file).

5. **VideoLessonScreen wired** (`ui/screens/video/VideoLessonScreen.kt:1`, `VideoLessonViewModel.kt:1`):
   - ViewModel now injects both `FirestoreRepository` and `VastavikApiRepository`; `loadLesson()` tries `GET /api/v1/lessons/:lessonId` → scoped `GET .../lessons` via API → fallback to Firestore streaming. Exposes `usingBackend` flag.
   - Screen collects `lessonData`/`isLoading`/`error`, plays via `VastavikYouTubePlayer(youtubeUrl, youtubeVideoId, startSeconds=youtubePositionSec)`, shows real `title/duration/videoFormat/isPremium/description/codeSample/notes/whiteboardImageUrl` (Coil for whiteboard, vertical player for `short`).

6. **Build config** (`kotlin-app/app/build.gradle.kts:28`):
   - Safe `local.properties` load, added `BuildConfig.BACKEND_BASE_URL` (default `http://10.0.2.2:3001` for emulator), `API_KEY_ID`, `API_KEY_SECRET` (dev defaults match `backend/backend/.env.example:11`).
   - Dependencies: `retrofit:2.11.0`, `converter-gson:2.11.0`, `okhttp:4.12.0`, `logging-interceptor:4.12.0`, `gson:2.11.0`.

## How to run together

```powershell
# 1) Backend
cd D:\vastavikKotlinFun\backend\backend
Copy-Item .env.example .env   # set FIREBASE_SERVICE_ACCOUNT_JSON, YOUTUBE_API_KEY, API_KEYS_JSON
npm install
npm run dev   # http://localhost:3001  GET /health → 200

# 2) Frontend — point to backend
# kotlin-app/local.properties:
# BACKEND_BASE_URL=http://10.0.2.2:3001   # emulator
# # For physical device on same Wi-Fi: BACKEND_BASE_URL=http://192.168.1.15:3001
# API_KEY_ID=android-prod
# API_KEY_SECRET=dev-secret-android-32bytes-hex-0000   # must match backend API_KEYS_JSON
# GEMINI_API_KEY=... (existing)
# MISTRAL_API_KEY=... (existing)

cd D:\vastavikKotlinFun\kotlin-app
.\gradlew.bat :app:assembleDebug   # BUILD SUCCESSFUL (tested 2026-08-24)
# Install on emulator: adb install app\build\outputs\apk\debug\app-debug.apk
```

Test the hardening:

```powershell
curl http://localhost:3001/health
# → {"status":"ok"}
# Student API without hmac → 401
curl -H "Authorization: Bearer <ID_TOKEN>" http://localhost:3001/api/v1/courses
# With hmac (generated by app) → 200
```

## Updating both repos separately

- Main app repo: `git -C D:\vastavikKotlinFun status/push` (ignores `backend/`)
- Admin/backend repo: `git -C D:\vastavikKotlinFun\backend status/push` (`origin` → vastavikAppAdmin)

When asked to do a task, apply changes in `kotlin-app/` AND `backend/backend/` (or `backend/admin-web/`) and verify each with its own build (`./gradlew :app:assembleDebug` and `npm run typecheck`).

## YouTube — unlisted + hidden branding summary

- Keep videos **Unlisted** in YouTube Studio (not Public, not Private). Add URL via admin panel; backend `POST /admin/.../lessons` validates and enriches `youtubeVideoId/durationSec/duration` via `YOUTUBE_API_KEY`.
- Frontend never shows “youtube.com” — player is Vastavik-branded, watermark shield + modestbranding, Firestore `youtubeUrl` never exposed as clickable link.
- For prod, consider proxying thumbnails via backend or enabling YouTube “privacy-enhanced mode” (`youtube-nocookie.com`) if needed.

## Files changed (this session)

- `kotlin-app/app/build.gradle.kts` — BuildConfig + Retrofit deps
- `kotlin-app/.../data/api/HmacUtil.kt` (new), `ApiConfig.kt` (new), `AuthInterceptor.kt` (new), `VastavikApiService.kt` (new), `DtoMappers.kt` (new), `data/repository/VastavikApiRepository.kt` (new)
- `kotlin-app/.../data/model/CourseModel.kt` — LessonModel extended
- `kotlin-app/.../di/AppModule.kt` — Retrofit/OkHttp providers
- `kotlin-app/.../ui/components/VastavikYouTubePlayer.kt` (new)
- `kotlin-app/.../ui/screens/video/VideoLessonViewModel.kt` — API-first load with Firestore fallback
- `kotlin-app/.../ui/screens/video/VideoLessonScreen.kt` — real player + data binding
- `backend/backend/src/routes/api.v1.lessons.ts` — added subparts/lessons/lessonId endpoints
