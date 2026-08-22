# APP_FLOW.md — App Flow — vastavikComputers

> **Version:** 0.2.0 | **Nav:** `kotlin-app/app/src/main/java/com/vastavik/computer/ui/navigation/AppNavHost.kt` + `AuthViewModel` decides start destination
> **Screens:** 21 features (see PRD) — Splash → Welcome/Onboarding → Auth → Home/Learn/Practice/Chat/Profile

---

## 1. Overall Navigation Graph (Compose Navigation)

```
Splash (start) --auth==null--> Welcome --Get Started--> Signup
   |                              |                         |
   |--auth!=null & not setup--> UserSetup                    +--> Home
   |--auth!=null & setup-------> Home <--------------------+
   |
   +--> AppUpdate (force) blocks all if isForceUpdate
```

**BottomNav (once logged in, 5 tabs):**

```
[Home]  [Learn]  [Practice]  [Chat]  [Profile]
  |        |         |          |        |
 Home  Learning   Practice   AI Chat  Profile -> Settings/Notes/Payment/PYQ/Notifications
       Path      MCQ|PYQ|Coding
```

**Other routes (push on top):**

`Course -> VideoLesson (3 tabs) -> Theory, quiz, coding -> CodeEditor full -> OcrExercise`
`Notifications (list)`, `Settings (Light/Dark/NeoBrutalish)`, `Payment (PhonePe/Razorpay) -> PaymentHistory`, `MyNotes (images/PDF)`, `SearchResults`, `EditProfile`, `AdminDashboard (if admin claim)`, `QuizSetup -> QuizTaking`, `AppUpdate`.

PromoPopup is overlay on Home (not a route) — dismissable.

---

## 2. Auth Flow Detail

```
Splash: check FirebaseAuth.currentUser + Firestore users/{uid} exists
  -> if null: Welcome -> Login/Signup
  -> if exists but classLevel null: UserSetup (pick class 5–12)
  -> else Home
Login: email/pass or Google (via play-services-auth 21.3.0)
  -> success -> Home (with welcome promo pop if eligible)
  -> fail -> error snackbar
  -> ForgotPassword -> send email -> back to Login with "check inbox"
Signup: name, email, class, pass/confirm, T&C -> createUser -> Firestore users/{uid} {name,email,classLevel,role:student,subscription:free} -> Home
Logout: Profile -> confirm dialog -> signOut -> Splash
Delete account: Profile -> re-auth -> delete from Auth + Firestore + Supabase (via CF) -> Splash
```

---

## 3. Per-Feature Flows

### 3.1 Home Generative (PRD #4)

```
Home: top Streak banner (streakCount, flame if >=3)
  + Continue Learning (last lesson, progress bar)
  + Recommended (from Supabase analytics or Firestore recommended)
  + Subject chips (Java/Python/JS/SQL) -> Course filter
  + Banners carousel (Firestore promotions where isActive)
  + Search bar -> SearchResults
  + PromoPopup auto-show if promotion valid
Pull refresh re-fetches courses + promotions.
Tap course/lesson -> VideoLesson/LearningPath
```

### 3.2 Learn (PRD #5)

```
Learn (LearningPathScreen): classLevel filtered topics, DuolingoPath zigzag nodes
  node locked until previous completed (progress/{courseId}.completedLessons)
  tap node -> VideoLesson (or toast "Complete previous")
  progress rings per node.
```

### 3.3 Profile (PRD #6)

```
Profile: avatar (photoUrl or initial), name, email, class, subscription badge (Pro/Free), streak, totalLessonsCompleted
  rows: Edit Profile -> EditProfileScreen
        My Notes -> MyNotesScreen
        Payment History -> PaymentHistoryScreen
        PYQ/Papers -> PYQScreen
        Notifications -> NotificationsScreen
        App Update -> AppUpdateScreen (shows if update available)
        Settings -> SettingsScreen
        Admin Dashboard (visible only if custom claim admin)
        Logout / Delete
```

### 3.4 Practise (PRD #7)

```
PracticeScreen tabs: [MCQ] [Papers/PYQ] [Coding]
 MCQ: pick subject/class/topic -> QuizSetup (count, difficulty) -> QuizTaking (instant feedback, explanation) -> result + streak XP
 Papers: list questionPapers filtered -> tap -> QuizTaking timed (duration countdown, auto-submit) -> result -> PDF view if available
 Coding: list codingQuestions -> CodeEditor pre-filled starterCode -> Run -> output + test results
```

### 3.5 Code Editor Full-Screen (PRD #8)

```
CodeEditorScreen: TopBar language picker (Java/Python/JS/SQL)
  Editor fills remaining space (weight 1f) with line numbers gutter, syntax color (JetBrains Mono), dark bg
  FAB Run -> executeCode (if coding question: sends to Docker via CF/executor, else local-friendly run mock) -> Output bottom sheet 35% (stdout/stderr, test passes)
  Save draft auto to Firestore codeSubmissions draft
  If question -> Submit -> graded.
```

### 3.6 AI Chat (PRD #9)

```
ChatScreen: thread list (aiChats where userId) + new chat
  messages LazyColumn (user indigo bubble, model neutral with markdown+code copy)
  input bar (text + image attach for OCR) + Send
  -> gemini-3.7-flash via generativeai (apiKey BuildConfig.GEMINI_API_KEY), streaming, tokenCount update
  Rate limit 50/day -> snackbar if hit.
  Image attach -> OCR preview editable -> send OCR text as prompt.
```

### 3.7 Settings + Theme (PRD #10-11)

```
SettingsScreen: toggles Dark Mode (ThemePreferences.isDark), NeoBrutalish (isNeoBrutalish), Modern (same), fontScale slider, notifications, clear cache, about, version, sign out
  Theme.kt reacts via VastavikTheme(darkTheme, neoBrutalish) — immediate preview.
```

### 3.8 Notifications (PRD #12)

```
NotificationsScreen: list notifications where userId == uid or type broadcast, sorted desc, unread dot, swipe to mark read, tap -> deep link (lesson/payment)
  Bell icon on Home shows unread count, FCM onMessage updates list.
```

### 3.9 App Update (PRD #13)

```
AppUpdateScreen: fetch appConfig/latestVersion from Firestore, compare versionName "1.0.0"
  if updateAvailable: show changelog, Update button -> Play Store intent
  if isForceUpdate: block navigation (popUpTo inclusive, cannot dismiss)
  Also triggered on Splash before Home.
```

### 3.10 Course Detail Videos 3 Formats (PRD #15,21)

```
Course -> Topic -> Lesson detail tabs: [VS Code 16:9] [Whiteboard 16:9] [Shorts 9:16 1–2m]
  VideoLessonScreen: top player (ExoPlayer/YouTube), format badge color, below: title, duration, description, theory link, MCQ/Paper buttons
  Shorts tab vertical swipe.
  Progress auto-saved (watched %)
```

### 3.11 Promo Pop Text/Image/Video (PRD #16)

```
Home overlay PromoPopup: fetched from Firestore promotions where isActive && now < validTill
  Variants: Text (heading+body+CTA), Image (banner Storage url), Video (autoplay muted)
  Dismiss X -> flag in DataStore dontShowPromoId for 24h
  CTA -> deep link (Payment, Course, external)
  Admin can also push via FCM data message to force show.
```

### 3.12 Payment AutoPay (PRD #17)

```
PaymentScreen: plan card (Pro Rs 299/mo slashed from 599 if promo 50% active), features, gateway toggle [Razorpay] [PhonePe] (default Razorpay, PhonePe if configured), T&C checkbox, Pay button
  -> createMandate (Gateway SDK) -> UPI app -> success -> webhook -> users.subscription = active -> show success + FCM
  -> failure -> snackbar + retry
  Bottom: PaymentHistory list (payments where userId)
  If subscription expired/grace: banner "Renew to keep access" + paywall gates (isPro lessons show lock).
```

### 3.13 Promotions 50% (PRD #18)

```
Home banner carousel + Payment slashed price + PromoPopup share same Firestore promotions collection.
  Admin creates via AdminDashboard or Firestore console: {title, discount 50, validTill, bannerImageUrl, isActive}
```

### 3.14 Notes Images/PDF (PRD #19)

```
MyNotesScreen: list notes/{noteId} (title, preview, date)
  FAB Add -> dialog title+content + pick image (gallery/camera) / PDF (file picker) -> upload to Storage notes/{uid}/{id}.jpg|pdf -> Firestore note {title, content, imageUrl?, pdfUrl?, createdAt}
  Tap note -> detail with image zoom, PDF viewer (intent), edit/delete.
```

### 3.15 OCR Exercise (PRD #20)

```
OcrExerciseScreen: [Type Code] tab (chat format, same editor) and [Photo] tab
  Photo: Take/choose image -> ML Kit TextRecognition -> editable OCR text preview -> "Send to AI" -> Gemini prompt "Review this code, fix and explain for Class X: {ocrText}" -> response stream
  Also from AI Chat image attach shortcut.
```

### 3.16 Video Lectures Library (PRD #21)

```
Home "All Lectures" see-all -> or Learn -> aggregated list filterable by subject/class/format (chips), search.
```

---

## 4. Deep Links & FCM Data Messages

| Link | Destination |
|---|---|
| `vastavik-computers.firebaseapp.com/lesson/{id}` | VideoLesson |
| `.../payment` | PaymentScreen |
| FCM `type: promo` with `promoId` | Show PromoPopup |
| FCM `type: new_lesson` | Home refresh + notification |

---

## 5. Error & Empty States

- Offline banner if no network (check ACCESS_NETWORK_STATE).
- Empty: illustration + "No courses yet" etc.
- Error: retry button + log to Firestore logs collection (level error).

---

## 6. State Management per Screen

Each screen has ViewModel with `StateFlow<UiState>` (`Loading/Success/Error`) + `Resource<T>` wrapper. Hilt injects `AuthRepository`, `FirestoreRepository`, `GenerativeModel` (via AppModule).
