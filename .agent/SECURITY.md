# SECURITY.md — Security Protocols — vastavikComputers

> **Version:** 0.1.0 | **Date:** 2026-08-22 | **Applies to:** Web (Next.js), Android (Kotlin), Cloud Functions, Firestore, Storage, Sandbox

---

## 1. Security Principles

1. **Defense in depth** — rules + validation + auth + sandbox; no single layer trusted alone.
2. **Least privilege** — every token/claim/role gets minimum required access.
3. **Secure by default** — deny unless explicitly allowed; Pro gating default closed.
4. **Never trust client input** — Zod on every boundary; escape all output.
5. **Student data is sacred** — DPDP Act 2023, parental consent for minors, data minimization, 90-day AI log retention.
6. **Audit everything** — `logs` collection + structured CF logs; admin actions logged.

---

## 2. Authentication

### 2.1 Firebase Auth

| Area | Standard |
|---|---|
| **Sign-in methods** | Email/password + Google (OAuth). No anonymous. |
| **Password policy** | Firebase strong policy: 8+ chars, upper+lower+digit, no common passwords, breached-password check |
| **Email verification** | Required before Pro features; blocked until `emailVerified == true` for payments |
| **Session** | Firebase ID token (1h) + refresh token (auto); Web: `onAuthStateChanged`, Android: `FirebaseAuth` listener |
| **Admin custom claims** | `{ admin: true }` set via `setAdmin` callable (admin-only, allowlist check). Verified in `middleware.ts` + Firestore rules (`isAdmin()`). |
| **Rate limiting** | Auth: 5 attempts / 15 min per IP+email; lockout 15 min; Cloud Functions: 50 AI req/day, 20 code-exec/min per user |
| **Account lockout** | 5 failed passwords -> 15 min lock; logged to `logs` with `level: warning` |

### 2.2 Admin Authentication

- `/admin` guarded by Next.js `middleware.ts`: verifies ID token + `admin` claim + email allowlist (`ADMIN_EMAILS` env).
- Shorter session TTL for admin (30 min idle timeout, re-auth for sensitive actions like `setAdmin`, bulk delete).
- Every admin write logged: `{ adminUid, action, target, timestamp }` in `logs`.

### 2.3 API Key Management

| Secret | Stored In | Accessed By | Rotation |
|---|---|---|---|
| `GEMINI_API_KEY` | Secret Manager (`GEMINI_API_KEY`) + Vercel env | Cloud Functions (`geminiChat`) only | 90 days |
| `RAZORPAY_KEY_SECRET` / `CASHFREE_SECRET` | Secret Manager + Vercel env | `paymentWebhook` + `createMandate` only | On leak / 90 days |
| `FIREBASE_SERVICE_ACCOUNT` | Secret Manager (functions) — never committed | Admin SDK init | On rotation |
| Firebase client config | `web/.env.local` (`NEXT_PUBLIC_*`) | Web client | Public (rules still enforce) |
| `ADMIN_EMAILS` | Vercel env + Secret Manager | `middleware.ts`, `setAdmin` | On team change |

**Never commit:** `google-services.json`, `serviceAccountKey.json`, `.env.local`, any `*_SECRET`.

---

## 3. Data Encryption

### 3.1 At Rest

| Data | Encryption |
|---|---|
| Firestore | Google AES-256 (default, CMEK optional) |
| Cloud Storage | AES-256 |
| Passwords | Firebase Auth `scrypt` — never stored plaintext |
| Payments | Only `mandateId` + gateway IDs stored; no card/UPI VPA stored; gateway handles PCI |
| AI chat logs | Firestore AES-256; PII redacted before storage; auto-delete 90 days via TTL or `checkExpiry` |

### 3.2 In Transit

- TLS 1.3 everywhere; HTTPS only (HSTS header on web, `network_security_config` on Android).
- Certificate pinning on Android (optional, for `*.googleapis.com`).
- No HTTP fallback.

### 3.3 Field-Level

- `users.email` — not encrypted at field level (needed for queries), but access restricted (owner/admin only).
- Future: encrypt `aiChats.messages` content at application layer if DPDP requires (AES-GCM with per-user key in Secret Manager).

---

## 4. Firestore Security Rules (Full — 14 Collections)

> **File:** `firestore.rules` — `rules_version = ''2'';` — Deploy via `firebase deploy --only firestore:rules`.

```javascript
rules_version = ''2'';
service cloud.firestore {
  match /databases/{database}/documents {

    // Helpers
    function isSignedIn() { return request.auth != null; }
    function isAdmin() { return isSignedIn() && request.auth.token.admin == true; }
    function isOwner(userId) { return isSignedIn() && request.auth.uid == userId; }
    function hasActiveSubscription() {
      return isSignedIn() &&
        get(/databases/$(database)/documents/users/$(request.auth.uid)).data.subscription.status == ''active'';
    }

    // users — owner read, no direct client write except limited self-update; creation via CF only
    match /users/{uid} {
      allow read: if isOwner(uid) || isAdmin();
      allow create: if false; // only via onUserCreate CF (Admin SDK bypasses rules)
      allow update: if isOwner(uid) &&
        // only allow updating displayName, photoURL, classLevel, notificationPrefs, fcmTokens, lastActiveAt
        request.resource.data.diff(resource.data).affectedKeys()
          .hasOnly([''displayName'',''photoURL'',''classLevel'',''notificationPrefs'',''fcmTokens'',''lastActiveAt'',''updatedAt''])
        || isAdmin();
      allow delete: if false;
    }

    // Public content — signed-in read, admin write
    match /subjects/{id} { allow read: if isSignedIn(); allow write: if isAdmin(); }
    match /courses/{id}  { allow read: if isSignedIn(); allow write: if isAdmin(); }
    match /topics/{id}   { allow read: if isSignedIn(); allow write: if isAdmin(); }

    // lessons — Pro gating
    match /lessons/{id} {
      allow read: if isSignedIn() && (resource.data.isPro == false || hasActiveSubscription() || isAdmin());
      allow write: if isAdmin();
    }
    match /theory/{id} {
      allow read: if isSignedIn() && (get(/databases/$(database)/documents/lessons/$(resource.data.lessonId)).data.isPro == false || hasActiveSubscription() || isAdmin());
      allow write: if isAdmin();
    }
    match /mcqs/{id}            { allow read: if isSignedIn(); allow write: if isAdmin(); }
    match /codingQuestions/{id} { allow read: if isSignedIn(); allow write: if isAdmin(); }

    // questionPapers — Pro only
    match /questionPapers/{id} {
      allow read: if isSignedIn() && (hasActiveSubscription() || isAdmin());
      allow write: if isAdmin();
    }

    // User-owned
    match /aiChats/{chatId} {
      allow read, update, delete: if isSignedIn() && resource.data.userId == request.auth.uid;
      allow create: if isSignedIn() && request.resource.data.userId == request.auth.uid;
    }
    match /codeSubmissions/{sid} {
      allow read: if isSignedIn() && resource.data.userId == request.auth.uid || isAdmin();
      allow create: if isSignedIn() && request.resource.data.userId == request.auth.uid;
      allow update, delete: if false;
    }
    match /notifications/{nid} {
      allow read, update: if isSignedIn() && resource.data.userId == request.auth.uid;
      allow create, delete: if isAdmin();
    }

    // Admin-only
    match /payments/{pid} {
      allow read: if isSignedIn() && (resource.data.userId == request.auth.uid || isAdmin());
      allow create, update, delete: if false; // only via webhook CF (Admin SDK)
    }
    match /logs/{lid} {
      allow read: if isAdmin();
      allow write: if false; // only via CF (Admin SDK)
    }
  }
}
```

**Storage rules:** `storage.rules` — authenticated read for `videos/*` (Pro check via Firestore `get()` if needed), admin write; `pdfs/*` Pro read.

---

## 5. Code Execution Sandbox Security

### Isolation Spec (Cloud Run + Docker)

| Control | Value |
|---|---|
| **Network** | `network: none` — no egress/ingress |
| **Filesystem** | Read-only root FS; only `/tmp` writable (`tmpfs` 64M) |
| **CPU** | 1 vCPU |
| **Memory** | 256 MB (OOM kill) |
| **Timeout** | 10s wall-clock (CF enforces, container kills at 11s) |
| **PIDs** | `pids_limit: 1` (or 32) — no fork bomb |
| **User** | Non-root (`nobody`) |
| **Seccomp** | Default Docker seccomp profile (blocks `mount`, `ptrace`, etc.) |
| **Output** | Truncated at 1 MB (stdout+stderr) |

### Supported Languages

| Language | Docker Image | Entry |
|---|---|---|
| Java | `eclipse-temurin:21-jdk` | `javac Main.java && java Main` |
| Python | `python:3.12-slim` | `python -u main.py` |
| JavaScript | `node:20-slim` | `node index.js` |
| SQL | `postgres:16-alpine` or SQLite | In-process SQLite (no container for simple queries) |

### Validation (before container)

- Max code 10,000 chars; no `os`, `subprocess`, `eval(`, `exec(`, `fetch(`, `require(''child_process'')`, `import os` patterns — regex blocklist + AST check (future).
- Seccomp + no-network ensures even bypass attempts cannot exfiltrate.
- Rate limit: 20 exec/min/user; 5 concurrent per instance.

---

## 6. API Security

- **Input validation:** Zod on every API route + CF `onCall` data. Reject on `safeParse` fail with 400.
- **Rate limiting:** In-memory (CF) + gateway (Vercel/Cloud Armor). Table:

| Endpoint | Limit |
|---|---|
| `POST /api/gemini` | 50 / day / user |
| `POST /api/code-exec` | 20 / min / user |
| `POST /api/payment/create` | 5 / min / user |
| `POST /api/payment/webhook` | 100 / min / IP (gateway) |
| `POST /api/pdf/generate` | 10 / min / admin |
| `POST /api/admin/questions/bulk` | 5 / min / admin |

- **CORS:** Web API routes allow same-origin only; CF `onCall` uses Firebase CORS.
- **Webhook:** Razorpay/Cashfree signature verification (`HMAC SHA256` with secret); idempotency via `gatewayPaymentId` unique check before upsert.

---

## 7. AI Chat Security

| Threat | Mitigation |
|---|---|
| **Prompt injection** | System prompt hard-coded server-side (not client); user message wrapped with delimiters; instruction hierarchy (system > developer > user) |
| **PII leakage** | Regex redaction before storing (`email`, `phone`, `address` patterns); never log raw prompt if contains PII |
| **Off-topic queries** | System prompt: "You are a Class 5–12 programming tutor for Java/Python/JS/SQL. Refuse off-topic." + classifier post-filter |
| **Token abuse** | 50 req/day + 100K tokens/day per user; `tokenCount` tracked; 429 on exceed |
| **API key exposure** | Key only in Secret Manager, never in client bundle; proxy via CF |
| **Content safety** | Gemini safety settings `HARM_CATEGORY_*` = `BLOCK_MEDIUM_AND_ABOVE`; CF logs flagged content to `logs` |
| **Chat log privacy** | `aiChats` read = owner only (rules); admin sees only aggregated / flagged (PII redacted); 90-day auto-delete |

---

## 8. Student Data Privacy — DPDP Act 2023 (India) Compliance

### Data Minimization

| Collect | Do NOT Collect |
|---|---|
| email, displayName, classLevel, subscription, progress, submissions, chat (scoped) | Aadhaar, phone (unless user opts), location, biometrics, parent financial data beyond mandateId |

### Consent

- Age gate: if `classLevel <= 8` (~age < 14), require parental consent checkbox + parent email (future: OTP to parent).
- Consent record: `users/{uid}.consent { grantedAt, parentEmail?, version }`.
- Withdrawal: Settings -> Delete account -> erases `users`, `aiChats`, `codeSubmissions` (payments retained 7y per law).

### Data Retention

| Data Type | Retention | Deletion |
|---|---|---|
| `aiChats` | 90 days | TTL field + daily `checkExpiry` sweep |
| `codeSubmissions` | Until account deletion | On delete request |
| `payments` | 7 years (audit) | After 7y |
| `logs` (security) | 1 year | After 1y |
| `users` (active) | Until deletion request | On request, 30-day soft delete then purge |

### Data Subject Rights

- **Access:** User can export own data (profile + submissions + chats + payments) via `/api/user/export` (future).
- **Correction:** User can edit `displayName`, `classLevel` in Profile.
- **Deletion:** `DELETE /api/user` -> marks `deletedAt`, purges after 30d; immediate anonymization of `logs`.
- **Grievance:** Contact in Privacy Policy; DPO email.

---

## 9. Vulnerability Management

### Dependency Scanning

- `npm audit` on every PR; `Dependabot` weekly PRs; `OWASP dependency-check` monthly.

### Security Testing

| Test | Frequency | Tool |
|---|---|---|
| SAST (static) | Every PR | ESLint security + `semgrep` |
| Dependency scan | Every PR + weekly | `npm audit`, Dependabot |
| Rules unit test | Every PR if rules changed | `@firebase/rules-unit-testing` |
| Pen test (manual) | Pre-launch + quarterly | OWASP Top 10 checklist |
| Sandbox isolation test | Every CF deploy | Integration tests (attempt net/FS escape) |

### Incident Response (6 steps)

1. **Detect** — `logs` critical alert -> on-call (FCM/email).
2. **Assess** — severity (P0 data breach / P1 auth bypass / P2 rate-limit).
3. **Contain** — revoke keys, block IP, disable function, set Firestore to read-only if needed.
4. **Eradicate** — patch, rotate secrets, fix rules.
5. **Recover** — redeploy, verify, re-enable.
6. **Post-mortem** — doc in `memory.md` + `logs.md`, add regression test.

---

## 10. Security Checklist (every component must pass before merge)

- [ ] No secrets in code/diff (gitleaks pass)
- [ ] Zod validation at all inputs
- [ ] Firestore rules cover new collection/field
- [ ] Auth check (signed-in / admin / owner) verified
- [ ] Rate limiting added if new endpoint
- [ ] PII not logged; redaction tested
- [ ] Sandbox limits enforced (if code exec touched)
- [ ] DPDP retention respected
- [ ] Dependency audit clean (`npm audit` no high)
- [ ] Security test added (rules / SAST / sandbox)
