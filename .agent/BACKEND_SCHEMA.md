# BACKEND_SCHEMA.md — Backend Schema — vastavikComputers

> **Dual Backend:** Firebase (primary — real-time) + Supabase (secondary — relational / search / analytics) — both required, synced via Cloud Functions.
> **Version:** 0.2.0 | **Date:** 2026-08-22

---

## 1. Overview

| Backend | Role | When to read/write |
|---|---|---|
| **Firebase** | Source of truth for app runtime, offline, real-time listeners, FCM, Storage for binaries | All app reads/writes go here first. Client SDK directly. |
| **Supabase** | Postgres mirror for SQL reporting, full-text Notes search, pgvector for AI semantic search, admin dashboards, analytics | Functions sync from Firestore; app reads via supabase-kt for search/analytics only; RLS enforces owner. |

Sync direction: **Firestore → Supabase** (Functions `supabaseSync` onCreate/onUpdate/onDelete). Never diverge.

---

## 2. Firebase — Firestore Collections (14 + 2 new)

### 2.1 Core (existing 14, see ARCHITECTURE.md for full fields)

```
users/{uid} { email, displayName, photoURL, classLevel 5..12, role student|admin, subscription{plan free|pro, status active|expired|grace|cancelled, mandateId, expiresAt, graceEndsAt}, streakCount, lastActiveDate, totalLessonsCompleted, theme light|dark, fcmTokens[], createdAt, lastActiveAt, consent{grantedAt,parentEmail?} }
subjects/{subjectId} { name Java|Python|JavaScript|SQL, icon, classes[], description, order }
courses/{courseId} { subjectId ref, classLevel, title, description, topicIds[], thumbnailUrl, order, isPublished }
topics/{topicId} { courseId ref, title, order, lessonIds[], isPublished }
lessons/{lessonId} { topicId ref, title, description, videos{vscode:{url,duration}, whiteboard:{url,duration}, short:{url,duration}}, theoryId ref, order, isPublished, isPro }
theory/{theoryId} { lessonId ref, title, content Markdown, references[{title,url}], updatedAt }
mcqs/{mcqId} { subjectId ref, classLevel, topicId ref, question, options[4], correctIndex, explanation, difficulty easy|medium|hard, isPublished }
codingQuestions/{cqId} { subjectId ref, classLevel, title, description, starterCode{java,python,javascript,sql}, testCases[{input,expected,isHidden}], timeLimit, difficulty, isPublished }
questionPapers/{qpId} { subjectId ref, classLevel, title, sections[{name,questionIds[],marks}], totalMarks, duration, pdfUrl?, isPublished, createdAt }
aiChats/{chatId} { userId ref, messages[{role user|model, content, timestamp}], tokenCount, createdAt, updatedAt }
codeSubmissions/{submissionId} { userId ref, questionId ref, language java|python|javascript|sql, code, output, status pass|fail|error, testResults?, createdAt }
notifications/{notifId} { userId ref, title, body, type new_lesson|expiry|reminder|achievement|broadcast|promo, read, createdAt }
payments/{paymentId} { userId ref, amount, currency INR, status pending|success|failed|cancelled, mandateId?, method upi_autopay, gateway razorpay|phonepe|cashfree, gatewayPaymentId?, createdAt }
logs/{logId} { level info|warning|error|critical, source, message, metadata?, timestamp, userId? }
```

### 2.2 New for 21-Feature Scope

```ts
// promotions/{promoId}
{
  title: string // "Diwali 50% OFF"
  description: string
  discountPercent: number // 50
  bannerImageUrl?: string // Storage promotions/{id}.jpg
  videoUrl?: string // optional promo video
  validTill: Timestamp
  isActive: boolean
  ctaText?: string // "Grab Now"
  ctaLink?: string // deep link or /payment
  target: "all" | "free" | "pro_expired"
  createdBy: DocumentRef<users>
  createdAt: Timestamp
}

// appConfig/{configId} — singleton doc "current"
{
  latestVersion: string // "1.1.0"
  minVersion: string // "1.0.0"
  isForceUpdate: boolean
  changelog: string // Markdown
  maintenanceMode: boolean
  updatedAt: Timestamp
}

// users/{uid}/notes/{noteId}  (subcollection)
{
  title: string
  content: string
  imageUrl?: string // Storage notes/{uid}/{noteId}.jpg
  pdfUrl?: string   // Storage notes/{uid}/{noteId}.pdf
  imageUrls?: string[] // multiple
  createdAt: Timestamp
  updatedAt: Timestamp
}

// users/{uid}/progress/{courseId}  (subcollection, already noted)
{
  completedLessons: string[] // lessonIds
  lastUpdated: Timestamp
  watchedVideos: { lessonId: string, format: "vscode"|"whiteboard"|"short", percent: number }[]
}
```

**Indexes:** `firestore.indexes.json` composites: `(subjectId, classLevel, isPublished)` for mcqs/codingQuestions/questionPapers, `(userId, createdAt)` for aiChats/codeSubmissions/notifications, `(isActive, validTill)` for promotions.

**Rules:** See `SECURITY.md` — add `promotions` (signed-in read, admin write), `appConfig` (signed-in read), `users/{uid}/notes` (owner only), `users/{uid}/progress` (owner).

---

## 3. Firebase — Storage Buckets

| Path | Access | Content |
|---|---|---|
| `videos/vscode/{lessonId}.mp4` | signed-in read (isPro check via Firestore get if needed), admin write | 16:9 VS Code videos + HLS variants |
| `videos/whiteboard/{lessonId}.mp4` | same | Whiteboard 16:9 |
| `videos/shorts/{lessonId}.mp4` | same | Shorts 9:16 1–2m |
| `promotions/{promoId}.jpg|mp4` | signed-in read, admin write | Promo banners/videos |
| `notes/{uid}/{noteId}.*` | owner only | Note images/PDFs |
| `questionPapers/{qpId}.pdf` | Pro only, admin write | Generated PDFs |
| `theory/{theoryId}/*` | signed-in read | Diagrams |

Storage rules mirror Firestore gates.

---

## 4. Firebase — Cloud Functions (Node 20 TS, see ARCHITECTURE.md §6)

Add one:

| Function | Trigger | Desc |
|---|---|---|
| `supabaseSync` | `firestore.document("{col}/{id}").onWrite` where col in [users,payments,aiChats,notes,logs,progress] | Upserts/deletes to Supabase via `supabase-js` service role, idempotent by `firebase_id` |

Others remain: `onUserCreate`, `onContentPublish`, `checkExpiry`, `geminiChat` (now gemini-3.7-flash), `executeCode`, `setAdmin`, `sendPush`, `bulkImport`.

Secrets via Secret Manager: `GEMINI_API_KEY` (`AQ.Ab8...PQ`), `RAZORPAY_KEY_SECRET`, `PHONEPE_SALT`, `SUPABASE_SERVICE_ROLE_KEY`.

---

## 5. Supabase — Postgres Schema (mirror + extended)

**Project:** `vastavik-supabase` (or same as Firebase project via Supabase Cloud). Region `ap-south-1` (Mumbai).

### 5.1 Tables (mirror Firestore)

```sql
-- users (mirror)
create table users (
  firebase_id text primary key, -- Firebase uid
  email text not null,
  display_name text,
  photo_url text,
  class_level int check (class_level between 5 and 12),
  role text check (role in (''student'',''admin'')),
  subscription_plan text, -- free|pro
  subscription_status text, -- active|expired|grace|cancelled
  mandate_id text,
  expires_at timestamptz,
  grace_ends_at timestamptz,
  streak_count int default 0,
  total_lessons_completed int default 0,
  fcm_tokens text[],
  created_at timestamptz default now(),
  last_active_at timestamptz,
  updated_at timestamptz default now()
);

-- payments (mirror)
create table payments (
  firebase_id text primary key,
  user_firebase_id text references users(firebase_id),
  amount int, -- paise
  currency text default ''INR'',
  status text,
  mandate_id text,
  method text,
  gateway text,
  gateway_payment_id text unique,
  created_at timestamptz default now()
);

-- ai_chats (mirror)
create table ai_chats (
  firebase_id text primary key,
  user_firebase_id text references users(firebase_id),
  messages jsonb, -- [{role,content,timestamp}]
  token_count int,
  created_at timestamptz default now(),
  updated_at timestamptz
);
-- pgvector for semantic search (future RAG)
create extension if not exists vector;
alter table ai_chats add column embedding vector(768); -- Gemini embedding

-- notes (enhanced — full-text search)
create table notes (
  firebase_id text primary key,
  user_firebase_id text references users(firebase_id),
  title text,
  content text,
  image_url text,
  pdf_url text,
  created_at timestamptz default now(),
  updated_at timestamptz
);
create index notes_fts on notes using gin(to_tsvector(''english'', coalesce(title,'''') || '' '' || coalesce(content,'''')));

-- promotions (mirror)
create table promotions (
  firebase_id text primary key,
  title text,
  description text,
  discount_percent int,
  banner_image_url text,
  video_url text,
  valid_till timestamptz,
  is_active bool,
  cta_text text,
  cta_link text,
  target text,
  created_by text references users(firebase_id),
  created_at timestamptz
);

-- logs (mirror for analytics)
create table logs (
  firebase_id text primary key,
  level text,
  source text,
  message text,
  metadata jsonb,
  timestamp timestamptz default now(),
  user_firebase_id text references users(firebase_id)
);

-- progress
create table progress (
  firebase_id text primary key, -- uid_courseId
  user_firebase_id text references users(firebase_id),
  course_id text,
  completed_lessons text[],
  watched_videos jsonb,
  last_updated timestamptz
);

-- courses/topics/lessons mirrored similarly as jsonb mirrors for reporting (optional, not per-query)
```

### 5.2 RLS (Row Level Security)

```sql
alter table users enable row level security;
create policy "users read own" on users for select using (auth.uid()::text = firebase_id);
create policy "users service role all" on users for all using (auth.jwt()->>''role'' = ''service_role'');

alter table notes enable row level security;
create policy "notes owner" on notes for all using (auth.uid()::text = user_firebase_id);

alter table ai_chats enable row level security;
create policy "chats owner" on ai_chats for all using (auth.uid()::text = user_firebase_id);

-- Admin read via service role bypasses RLS in Functions
```

### 5.3 Supabase Storage (backup / alternative)

Bucket `notes`, `promotions` mirroring Firebase Storage paths, but primary remains Firebase. Supabase Storage used only if Firebase quota hit or for direct Postgres `storage.objects` linkage.

### 5.4 Realtime & Functions

- Realtime: admin dashboards subscribe to `payments`, `ai_chats` changes via Supabase Realtime.
- Edge Functions (Deno) not required initially; Cloud Functions is primary. Supabase Edge Functions reserved for nightly `pgvector` embedding backfill.

---

## 6. Sync Strategy (Firestore ↔ Supabase)

```
Firestore write (client)
  -> Cloud Function supabaseSync (onWrite)
     -> supabase.from(table).upsert({firebase_id: doc.id, ...mapped}, {onConflict: ''firebase_id''})
     -> onDelete -> supabase.from(table).delete().eq(''firebase_id'', id)
  -> if Supabase fails: log to logs (error, retry 3x with backoff)
```

**No client direct Supabase writes** for primary data; only reads for search (`notes_fts` query via anon key + RLS). Admin dashboard may write promotions to both, but Firestore is source of truth — Supabase follows.

**Initial backfill:** one-time script `functions/src/scripts/backfillSupabase.ts` iterates Firestore collections → Supabase.

---

## 7. Gemini & OCR Data

- AI chat messages stored in both: primary `aiChats` Firestore, mirror `ai_chats` Supabase + embedding column for future vector search ("find similar past doubt").
- OCR: not stored as collection; transient: image → ML Kit text → Gemini prompt → response stored as `aiChats` message with `metadata{source:"ocr", ocrText}`.

---

## 8. Config & Secrets Matrix

| Secret | Firebase Secret Manager | Supabase Vault | Client |
|---|---|---|---|
| `GEMINI_API_KEY` `AQ.Ab8RN...PQ` | `geminiChat` + `supabaseSync` (if embedding) | Edge function env | `local.properties` → `BuildConfig.GEMINI_API_KEY` (never in git) |
| `SUPABASE_URL` + `ANON_KEY` | Functions env | — | `local.properties` `SUPABASE_URL/ANON_KEY` (RLS-limited) |
| `SUPABASE_SERVICE_ROLE_KEY` | `supabaseSync` only | Vault | never |
| `RAZORPAY_KEY_SECRET` / `PHONEPE_SALT` | `paymentWebhook` | — | SDK keys via `local.properties` |
| `FIREBASE_SERVICE_ACCOUNT` | Functions | — | never |

---

## 9. Migration & Versioning

- Firestore is versioned via `firestore.rules` + `firestore.indexes.json` in git.
- Supabase migrations in `supabase/migrations/*.sql` committed, pushed via `supabase db push`.
- Keep `.agent/BACKEND_SCHEMA.md` as single source — update both sections together on schema change.
