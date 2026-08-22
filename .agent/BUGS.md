# BUGS.md — Bugs & Issues Tracker — vastavikComputers

> **Purpose:** Live tracker for bugs found during M1..M8, whether fixed, and testing notes. Update after every TESTING_PLAN check.
> **Template:** Every bug gets ID, date, milestone, severity, status, fix commit.

---

## 1. Status Legend

| Status | Meaning |
|---|---|
| Open | Found, not fixed |
| In Progress | Being fixed |
| Fixed | Fix merged to `develop`, verified |
| Won''t Fix | Backlogged to DESIGN_IDEAS or wontfix with rationale |
| Verified | Fixed + testing PASS |

Severity: `P0 Blocker` (blocks milestone), `P1 Critical`, `P2 Major`, `P3 Minor`.

---

## 2. Open / In-Progress Bugs

| ID | Date | Milestone | Title | Severity | Status | Repro | Assignee |
|---|---|---|---|---|---|---|---|
| — | — | — | (no open bugs — pre-build) | — | — | — | — |

---

## 3. Fixed / Closed Bugs

| ID | Date Found | Date Fixed | Milestone | Title | Severity | Root Cause | Fix | Commit | Verified |
|---|---|---|---|---|---|---|---|---|---|
| — | — | — | — | — | — | — | — | — | — |

_Add rows as bugs are filed and closed._

Example:

| BUG-01 | 2026-08-30 | 2026-08-31 | M4 | Editor line numbers misaligned on font scale 1.5 | P2 | Gutter width calculated for 2 digits only | Measure `maxLines.toString().length * charWidth` | `fix: editor gutter` a1b2c3d | 2026-08-31 PASS |

---

## 4. Known Issues / Tech Debt (from codebase audit 2026-08-22)

| ID | Area | Issue | Severity | Plan |
|---|---|---|---|---|
| TD-01 | kotlin-app | Missing screens: CodeEditor full, Notifications, AppUpdate, PromoPopup, OcrExercise (PRD #8,12,13,16,20) | P1 | M4/M5 — must create |
| TD-02 | Chat | Currently uses master? Need upgrade to `gemini-3.7-flash` with BuildConfig key `AQ.Ab8...PQ` | P1 | M4 |
| TD-03 | Payments | PaymentScreen exists but needs PhonePe toggle + 50% promo slashing + dual gateway abstraction | P1 | M6 |
| TD-04 | Supabase | Not yet wired — needs migrations + supabaseSync CF + local.properties keys | P1 | M1 |
| TD-05 | Theme | Settings has light/dark but NeoBrutalish variant not fully implemented (Theme.kt) | P2 | M2 |
| TD-06 | Build | Hardcoded keystore passwords empty in build.gradle.kts release signingConfigs | P2 | Before prod — fill via local.properties |
| TD-07 | Storage | Needs Storage rules mirror for new `notes`/`promotions` paths | P2 | M1 |

---

## 5. How to File a Bug

1. Add row to §2 with ID `BUG-NN` (increment), date, milestone, title, severity Open, repro steps ("1. Open Editor 2. Paste 100 lines...Expected...Actual...").
2. Assign, move to In Progress when working.
3. On fix, move row to §3, fill Root Cause/Fix/Commit, mark Fixed.
4. After TESTING_PLAN smoke passes, mark Verified + date.
5. Update `logs.md` §5 with bug reference.

---

## 6. Bug Metrics (updated per milestone)

| Milestone | Open | Fixed | Verified | P0 |
|---|---|---|---|---|
| M0 | 0 | 0 | 0 | 0 |
| M1 | — | — | — | — |
| ... | — | — | — | — |
