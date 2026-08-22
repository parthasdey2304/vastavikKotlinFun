# UI_UX_DESIGN.md — UI/UX Design System — vastavikComputers

> **Stitch:** https://stitch.withgoogle.com/projects/17415965411885249153 (open in browser, Google login, MCP export to React/Tailwind/Figma) — primary source for all 21 screens.
> **Current Kotlin Theme:** `kotlin-app/app/src/main/java/com/vastavik/computer/ui/theme/` (Color.kt, Type.kt, Theme.kt) + `ThemePreferences.kt` (light/dark/neobrutalish)

---

## 1. Design Principles (6)

1. Student-first clarity (10–18y, 48dp tap, simple copy)
2. Gamified progression (streaks, DuolingoPath nodes, rings)
3. Three-format video awareness: VS Code 🔵 `#0EA5E9`, Whiteboard 🟢 `#10B981`, Shorts 🔴 `#EF4444`
4. Code-first (JetBrains Mono, full-screen editor with line numbers)
5. Responsive: mobile-first, but also tablet/desktop if web built
6. WCAG 2.1 AA (4.5:1, focus rings, reduced-motion, screen reader)

---

## 2. Tokens (Sync with Design.md + Theme.kt)

| Token | Value |
|---|---|
| Primary Indigo | `#4F46E5` / `Indigo600`, hover `#4338CA`, light `#6366F1` (dark mode) |
| Java Orange | `#F97316` |
| Python Blue | `#3B82F6` |
| JS Yellow | `#EAB308` (text `#A16207` on light) |
| SQL Violet | `#8B5CF6` |
| Success/Warning/Error | `#22C55E`/`#F59E0B`/`#EF4444` |
| Neutrals | 50 `#F9FAFB` light bg, 900 `#111827` dark bg, surface `#1F2937` |
| Typography | Plus Jakarta Sans (headings 700/600), Inter (body 400), JetBrains Mono (code 400) — see Type.kt |
| Spacing | xs4 sm8 md16 lg24 xl32 2xl48, radius sm6 md10 lg16 xl20 full9999, shadow sm/md/lg |
| NeoBrutalish variant | Radius 2px, border 3px black, hard shadow `4px 4px 0 #000`, toggled via Settings `isNeoBrutalish` |

---

## 3. Component Library for 21 Features (Compose)

| Component | File | Notes |
|---|---|---|
| Button (primary/ghost/danger) | `ui/components/CommonComponents.kt` | 48dp, loading, indigo |
| Card | same | lg radius, sm shadow, neo variant |
| VideoPlayer | `screens/video/VideoLessonScreen.kt` (youtube player) | Format badge, progress, speed |
| CodeEditor (full-screen, line numbers, highlight) | NEW `screens/editor/CodeEditorScreen.kt` | JetBrains Mono, dark, language picker, run, output pane, full space |
| ChatBubble | `screens/chat/ChatScreen.kt` | user indigo, model neutral, markdown+code |
| DuolingoPath | `ui/components/DuolingoPath.kt` | Zigzag nodes, locked/unlocked, progress |
| ProgressRing/Badge | CommonComponents | Gamified |
| MCQCard | `screens/quiz/*` | Instant feedback |
| PaperCard (PYQ) | `screens/onboarding/PYQScreen.kt` | Marks, duration |
| PromoPopup (text/image/video, cuttable) | NEW `ui/components/PromoPopup.kt` | Dismiss X, CTA, can be FCM or Firestore |
| PaymentSheet | `screens/onboarding/PaymentScreen.kt` | UPI mandate, promo price, PhonePe/Razorpay toggle |
| NotesCard | `screens/onboarding/MyNotesScreen.kt` | Image/PDF preview |
| BottomNav + TopBar | `ui/navigation/AppNavHost.kt`, `SystemBars.kt` | 5 tabs: Home, Learn, Practice, Chat, Profile |
| Skeleton/Shimmer + EmptyState | CommonComponents | Loading |

---

## 4. Screen Inventory — 21 Features → Screens (Stitch mapping)

| # | Feature | Screen | Stitch Section (if exported) | Status in kotlin-app |
|---|---|---|---|---|
| 1 | Splash | `auth/SplashScreen.kt` | Splash | Exists |
| 2 | Login | `auth/LoginScreen.kt` | Auth | Exists |
| 3 | Signup | `auth/SignupScreen.kt` | Auth | Exists |
| 3ii | Forgot | `auth/ForgotPasswordScreen.kt` | Auth | Exists |
| 4 | Home generative | `home/HomeScreen.kt` | Home/Dashboard | Exists — needs promo carousel |
| 5 | Learn/Duolingo | `learning/LearningPathScreen.kt` | Learn | Exists |
| 6 | Profile | `profile/ProfileScreen.kt` | Profile | Exists |
| 7 | Practise MCQ/PYQ | `practice/PracticeScreen.kt`, `quiz/Quiz*`, `onboarding/PYQScreen.kt` | Practice | Exists |
| 8 | Code Editor full | NEW `screens/editor/CodeEditorScreen.kt` | Editor | TODO — must fill space + line numbers |
| 9 | AI Chat | `chat/ChatScreen.kt` | Chat | Exists (needs gemini-3.7-flash upgrade) |
| 10 | Settings | `onboarding/SettingsScreen.kt` | Settings | Exists |
| 11 | Theme/fonts | `ui/theme/Theme.kt` + `ThemePreferences.kt` | Theme | Exists — add NeoBrutalish toggle |
| 12 | Notifications list | NEW `screens/notifications/NotificationsScreen.kt` | Notifications | TODO |
| 13 | App Update | NEW `screens/onboarding/AppUpdateScreen.kt` | Update | TODO |
| 14 | Course list | part of Home/Learn | Courses | Exists via courses |
| 15 | Course detail 3 videos + MCQ/paper | `video/VideoLessonScreen.kt` | Lesson Detail | Exists — verify 3 tabs (VSCode/Whiteboard/Shorts) |
| 16 | Promo pop | NEW `ui/components/PromoPopup.kt` | Promo | TODO |
| 17 | Payment UPI AutoPay | `onboarding/PaymentScreen.kt` | Payment | Exists — add PhonePe toggle |
| 18 | Promotions 50% | Home banner + Payment | Promo | Exists via banners |
| 19 | Notes images/PDF | `onboarding/MyNotesScreen.kt` | Notes | Exists |
| 20 | OCR exercise | NEW `screens/editor/OcrExerciseScreen.kt` | Exercise | TODO |
| 21 | Video lectures library | `video/VideoLessonScreen.kt` + home/learn | Videos | Exists |

---

## 5. Stitch → Compose Workflow

```
Stitch (browser) --MCP export--> React/Tailwind/Figma --manual token translate--> Compose Theme (Color.kt/Type.kt) + screens
  ^ v0.dev / 21st.dev for page layouts also adapted
```

If MCP not available: screenshot Stitch → replicate spacing/color/typography via tokens above.

---

## 6. Settings — Light/Dark/NeoBrutalish Spec

- `ThemePreferences` DataStore keys: `isDark`, `isNeoBrutalish`, `fontScale`, `isModern` (alias).
- `Theme.kt`: `VastavikTheme(darkTheme, neoBrutalish)` switches palettes + shapes (neo: `RoundedCornerShape(2.dp)`, `BorderStroke(3.dp, Black)`, `shadow 4dp offset`).
- Preview both in Android Studio.

---

## 7. Code Editor Full-Space Spec

- Must take entire screen (except TopBar), not a small card. Editor `Modifier.fillMaxSize().weight(1f)` + output pane bottom sheet or split 65/35.
- Line numbers gutter (measure widest line number).
- Syntax highlight via `Spannable` or library (e.g., `code-highlight` or manual token color), 4 langs.
- Run button FAB, output copy/clear.

---

## 8. Promo Pop Spec

Props: `PromoPopup(title, body, imageUrl?, videoUrl?, ctaText, ctaLink, dismissable)`. Variants: TextOnly, Image, Video (ExoPlayer autoplay muted). Trigger: Firestore `promotions` where `isActive && now < validTill` or FCM data message. X cuts it, don’t show again for 24h (DataStore flag).

---

## 9. Accessibility

- 4.5:1 contrast checked, 48dp min, `contentDescription`, `semantics`, focus order, reduce motion respects `prefersReducedMotion`.