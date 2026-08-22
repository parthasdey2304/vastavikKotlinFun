# logs.md â€” Runtime Event Tracking â€” vastavikComputers

> **Version:** 0.1.0 | **Date:** 2026-08-22

---

## 1. Log Format

```
[TIMESTAMP] LEVEL | SOURCE | MESSAGE | metadata={...}
```

- **TIMESTAMP:** ISO 8601 UTC (`2026-08-22T14:32:00.000Z`)
- **LEVEL:** INFO | WARNING | ERROR | CRITICAL
- **SOURCE:** `web/api/gemini`, `functions/geminiChat`, `android/lessons`, `sandbox/execute`, etc.
- **MESSAGE:** Human-readable
- **metadata:** JSON â€” `userId`, `chatId`, `durationMs`, `tokenCount`, etc. (PII redacted)

**Firestore `logs` collection schema:** `{ level, source, message, metadata?, timestamp, userId? }`

---

## 2. Log Levels

| Level | Color | When |
|---|---|---|
| **INFO** | Green | Normal events: signup, lesson view, chat success, payment success |
| **WARNING** | Yellow | Rate limit hit, grace period, PII redacted, retry |
| **ERROR** | Red | Auth failure, sandbox timeout, webhook signature fail, function exception |
| **CRITICAL** | Dark red | Data breach attempt, rules violation spike, sandbox escape attempt, payment fraud |

---

## 3. Event Types

| # | Event | Level | Source | Description |
|---|---|---|---|---|
| 1 | `user.signup` | INFO | `functions/onUserCreate` | New user doc created |
| 2 | `user.login` | INFO | `web/auth`, `android/auth` | Successful sign-in |
| 3 | `auth.failed` | WARNING | `auth/*` | Failed login (wrong password, unverified) |
| 4 | `auth.lockout` | WARNING | `auth/*` | Account locked after 5 fails |
| 5 | `payment.create` | INFO | `web/api/payment/create` | Mandate creation initiated |
| 6 | `payment.webhook.success` | INFO | `functions/paymentWebhook` | Webhook verified + subscription activated |
| 7 | `payment.webhook.failed` | ERROR | `functions/paymentWebhook` | Signature fail / idempotency conflict |
| 8 | `payment.expiry` | WARNING | `functions/checkExpiry` | Subscription expired / grace / downgraded |
| 9 | `content.publish` | INFO | `functions/onContentPublish` | Lesson published + FCM sent |
| 10 | `ai.chat` | INFO | `functions/geminiChat` | Chat success â€” tokens, duration |
| 11 | `ai.chat.blocked` | WARNING | `functions/geminiChat` | Off-topic / safety block / prompt injection attempt |
| 12 | `ai.rate_limited` | WARNING | `functions/geminiChat` | User exceeded 50/day or token quota |
| 13 | `code.exec.success` | INFO | `functions/executeCode` | Sandbox run ok â€” duration, language |
| 14 | `code.exec.error` | ERROR | `functions/executeCode` | Timeout, OOM, validation fail |
| 15 | `sandbox.violation` | CRITICAL | `sandbox` | Network/FS escape attempt, seccomp violation |
| 16 | `admin.action` | INFO | `admin/*`, `functions/setAdmin` | Admin CRUD, claim set, push sent |
| 17 | `admin.unauthorized` | ERROR | `middleware`, `functions/*` | Non-admin attempted admin route |
| 18 | `rate_limited` | WARNING | `web/api/*`, `functions/*` | Generic rate limit 429 |
| 19 | `system.error` | ERROR | `functions/*`, `web/*`, `android/*` | Unhandled exception, 500 |
| 20 | `system.critical` | CRITICAL | `functions/*` | Data breach, fraud, repeated violations |

---

## 4. Runtime Log Stream

> **Pre-development â€” no runtime logs yet.** This section will be appended by the app and Cloud Functions at runtime (and/or mirrored from Firestore `logs` collection).

```
(no entries yet)
```

---

## 5. Development Session Log

| Date | Session | Author | Summary |
|---|---|---|---|
| 2026-08-22 | Init | Architect (AI) | Created `.agent/` with 12 Markdown docs (PRD, ARCHITECTURE, Design, Rules, memory, PLAN, SECURITY, logs, DEPENDENCY_GRAPH, CHANGELOG, VERSION, README). Verified Gemini 3.7 Flash (`gemini-3.7-flash`, 2026-08-13, 1M context) and Stitch project link. Locked tech stack: Kotlin+Compose / Next.js 15 / Firebase / Gemini / UPI AutoPay. Defined 14 Firestore collections + full security rules. Synced docs to `D:\vastavikKotlinFun\.agent` workspace. Next: Phase 1 scaffolding. |

| 2026-08-22 | v0.2.0 Agent Design | Architect (AI) | Expanded to 21-feature scope per rookie brief: updated PRD (21 features incl. editor full-screen line numbers, OCR ML Kit, promo pop text/image/video, 50% promotions, Notes images/PDF, PhonePe/Razorpay AutoPay), created TRD, UI_UX_DESIGN (+ NeoBrutalish), DESIGN_IDEAS, APP_FLOW (16 flows), BACKEND_SCHEMA (Firebase 14+2 + Supabase 7 tables + RLS + sync), IMPLEMENTATION_PLAN (M0..M8), TESTING_PLAN, GITHUB_WORKFLOW, BUGS (TD-01..TD-07), TEST_REPORT (M0 PASS). Dual backend spec locked, key AQ.Ab8...PQ into local.properties only. Synced to .agents + D:\vastavikKotlinFun workspace. |

---

## 6. Log Retention Policy

| Log Type | Retention | Storage | Purge |
|---|---|---|---|
| **Runtime logs** (`logs` collection, INFO/WARNING) | 90 days | Firestore | TTL or `checkExpiry` sweep |
| **Security logs** (ERROR/CRITICAL, auth, sandbox) | 1 year | Firestore + Cloud Logging | After 1y |
| **Payment audit logs** | 7 years | Firestore `payments` + `logs` | After 7y (compliance) |
| **Dev session log** (this file Â§5) | Permanent | Git (`logs.md`) | Never â€” append only |
| **AI chat logs** (`aiChats`) | 90 days | Firestore | TTL |
| **Cloud Functions stdout** | 30 days | Cloud Logging | GCP retention |

---

## 7. How to Query Logs

```javascript
// Firestore â€” recent errors
db.collection(''logs'').where(''level'',''in'',[''error'',''critical'']).orderBy(''timestamp'',''desc'').limit(50)

// Cloud Logging â€” function errors
// GCP Console -> Logging -> Query: resource.type="cloud_function" severity>=ERROR
```
