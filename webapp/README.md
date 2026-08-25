# Vastavik Web — mirrors Kotlin app

Student webapp at `D:\vastavikKotlinFun\webapp`. Same UI as `kotlin-app`, same backend `backend/backend` (port 3001), same Firestore collections.

- **Home** `app/page.tsx` mirrors `kotlin-app/.../HomeScreen.kt` — Continue Learning, Course Catalog, Popular Topics.
- **Courses / Parts / Lessons** hit `GET /api/v1/...` with Firebase ID token + HMAC `x-api-key` — `lib/api.ts` mirrors `kotlin-app/.../AuthInterceptor.kt`.
- **Player** `components/VastavikPlayer.tsx` mirrors `VastavikYouTubePlayer.kt` — `youtube-nocookie` embed, `modestbranding=1, rel=0, iv_load_policy=3, fs=0`, transparent watermark shield bottom-right 72×28 (no YouTube logo).
- **Unlisted** — admin adds unlisted `youtubeUrl`, backend validates `privacyStatus == unlisted|public`.

## Run

```powershell
cd D:\vastavikKotlinFun\webapp
Copy-Item .env.example .env
npm install
npm run dev   # http://localhost:3002
```

Needs backend running:

```powershell
cd D:\vastavikKotlinFun\backend\backend
Copy-Item .env.example .env; npm install; npm run dev  # http://localhost:3001
```

Three ports:
- **3001** backend API
- **3000** admin-web (Next.js admin)
- **3002** this webapp (Next.js student)

Change in one place, see it everywhere: add a course via admin-web :3000 or `POST /admin/courses`, it appears in `webapp :3002` and `kotlin-app` (same `GET /api/v1/courses`).
