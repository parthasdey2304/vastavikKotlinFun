# GITHUB_WORKFLOW.md — Add to GitHub After Each Step — vastavikComputers

> **Repo:** https://github.com/parthasdey2304/vastavikComputers | **Branching:** `main` (prod, protected) <- `develop` <- `feature/*`, `fix/*` <- PR

---

## 1. Rule: Commit & Push After EACH Step/Milestone

> Your message: "ADD IT TO GITHUB OVER HERE AFTER EACH STEP IS DONE" — this is the enforced workflow.

| After what | What to push | How |
|---|---|---|
| After each file in .agent/.agents change | that file | same PR or small `chore: docs` commit |
| After each milestone task (e.g., editor screen) | code + updated .agent/memory.md, logs.md | `feat:` commit, PR to `develop` |
| After each milestone PASS (testing report) | tag `v0.X.Y` | `develop` -> `release/vX.Y.Z` -> `main` |

Never batch 4 milestones without push.

---

## 2. Branches & Protection

```
main (protected: no direct push, requires PR + CI green + 1 review)
  <- develop (integration, auto preview)
     <- feature/editor-fullscreen
     <- feature/gemini-chat-37
     <- feature/ocr-exercise
     <- fix/promo-popup-dismiss
```

Protection rules (GitHub Settings -> Branches):

- `main`: require PR from `develop` or `release/*`, status checks `ci` must pass, no force push.
- `develop`: require PR from `feature/*`, CI passes.

---

## 3. Commit Convention (Conventional Commits)

```
feat: add full-screen code editor with line numbers (M4)
fix: promo popup dismiss 24h not persisting
chore: update .agent MEMORY for M3
docs: add BACKEND_SCHEMA supabase sync
security: rotate gemini key, remove from log
test: add editor VM unit tests
ci: add supabase migration check
```

---

## 4. CI (`.github/workflows/ci.yml`)

```yaml
name: ci
on: [pull_request]
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with: { java-version: ''17'' }
      - run: ./gradlew -p kotlin-app lintDebug assembleDebug test --stacktrace
      - run: gitleaks detect --no-git -v  # no secret
  functions:
    runs-on: ubuntu-latest
    steps:
      - run: npm --workspace=functions ci && npm --workspace=functions test
```

Add job for `rules` and `sandbox` when those exist.

---

## 5. Step-by-Step Git After Each Milestone (example M4)

```bash
git checkout develop && git pull
git checkout -b feature/editor-fullscreen
# ... code editor + OCR ...
./gradlew -p kotlin-app test  # must green
git add kotlin-app/app/src/main/java/com/vastavik/computer/ui/screens/editor/ .agent/memory.md .agent/logs.md
git commit -m "feat: add full-screen code editor + OCR (M4)"
git push -u origin feature/editor-fullscreen
# open PR to develop, CI green, merge
git checkout develop && git pull
git tag -a v0.6.0 -m "M4 PASS — editor+chat+ocr"
git push origin v0.6.0
```

For docs-only (like this M0):

```bash
git add .agent/ .agents/
git commit -m "docs: expand to 21 features, add TRD/APP_FLOW/BACKEND_SCHEMA etc (v0.2.0)"
git push origin develop  # or feature/docs-v02 then PR
```

---

## 6. What to Include in Every Push

Checklist before `git push`:

- [ ] Code builds
- [ ] Tests for touched module pass
- [ ] `.agent/memory.md` routes/decisions updated if needed
- [ ] `.agent/logs.md` §5 dev session appended
- [ ] `.agent/BUGS.md` updated if bug found/fixed
- [ ] No secret in diff (`git diff --cached | grep -i "AQ.Ab8"` should be empty unless in .gitignored local.properties)
- [ ] `CHANGELOG.md` updated if milestone

---

## 7. Release to Main (v1.0.0)

```bash
git checkout develop
git checkout -b release/v1.0.0
# bump versionName in kotlin-app/app/build.gradle.kts, update .agent/VERSION.md + CHANGELOG.md
git commit -m "chore: bump to 1.0.0"
git push -u origin release/v1.0.0
# PR release/v1.0.0 -> main, CI green, merge
git checkout main && git pull
git tag v1.0.0 && git push origin v1.0.0
# GitHub Release with notes from CHANGELOG.md, APK artifact
```

---

## 8. Supabase Migrations in Git

`supabase/migrations/*.sql` committed, pushed, and run `supabase db push` in CI or manually; never apply manual SQL without migration file.