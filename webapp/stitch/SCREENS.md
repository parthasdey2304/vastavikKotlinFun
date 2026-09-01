# Stitch Screen Generation Spec — Vastavik Live Class

> Paste these prompts into Stitch (once auth is fixed) against the Design System in DESIGN.md. Each prompt lists the exact layout constraints so Stitch produces **separate, highly-responsive laptop/desktop variants**, not a stretched mobile view.

## 1. Class Live Banner (notification + in-app banner)

**Prompt:**
```
NeoBrutalist banner for a class going live. Shows class topic and a primary "Join" button plus Dismiss. Thick bottom/right border, rounded corners, amber/orange accent, black border. Must exist as a compact top banner and as a system notification preview. Two variants: mobile (full-width, stacked button row) and desktop (horizontal, topic left, buttons right, max-width 720).
```

## 2. Lobby / Join Screen (screen 1 of 2-screen flow)

**Prompt:**
```
Lobby screen for live class. Centered NeoBrutalist card (thick bottom/right border, rounded 20) with video-call icon, "Live Class" headline, topic, "Mic & camera will be OFF" note, Cancel (outlined) and Join Class (solid black) buttons. Mobile: card 92vw, stacked buttons. Desktop: card 440px centered, larger icon, two-column button row, background is muted canvas. Provide animated entry (fade + slight scale).
```

## 3. In-Class Meeting Room (screen 2 — Google-Meet-style)

**Prompt:**
```
In-class meeting room. NeoBrutalist theme, thick bottom/right borders, rounded corners.

Default view is the whiteboard (full-width, grid, toolbar at top-left with PEN/ERASER/HAND/TEXT + Clear). Behind/adjacent: small video tiles (bottom-end, max 3, black tiles with name). Control bar at bottom: exactly 4 controls (Mic, Video, Screenshare, Leave) with Screenshare hidden unless user is Admin or Screenshare-granted — when hidden the remaining 3 expand evenly with no gap. Recording indicator (REC red pill) when active. Provide two layouts in the same screen: MOBILE (stacked: whiteboard → video tiles → control bar → collapsible Participants/Chat drawers) and DESKTOP (two-column: left = whiteboard + tiles + control bar, right = persistent Participants + Chat side panels 360px, no layout shift when toggling). High-contrast, blocky, black borders everywhere.
```

## 4. Chat Panel (with reply-to-message)

**Prompt:**
```
Chat panel NeoBrutalist. Header black with "Chat" title. Each message is a bubble with 1.5px black border, rounded corners. Composer at bottom with input + black Send button. Reply flow: tapping Reply on a message shows a small amber preview bubble above the composer ("Replying to <name>" + truncated text + × to cancel); once sent the reply bubble shows a small quoted preview (sender name + truncated text) at the top of the bubble. Messages from StarCast show "★ starCast" tag. Provide mobile (420px height) and desktop (full-height side panel) variants. Empty state: "No messages yet. Say hi 👋".
```

## 5. Participants Panel

**Prompt:**
```
Participants side panel. NeoBrutalist card with black header ("Participants (n)"). List rows: circular avatar (initial) + name + role tags (Admin blue pill, starCast amber pill with ★, You green pill) + status dots row (mic, cam, hand, share — black when on, white when off, each in 1px black border box). Per-row actions (Kotlin mirrors this): Admin sees Grant/Revoke share, Star/Revoke star, Remove; StarCast sees Remove for students (not Admin). Mobile drawer vs desktop persistent 360px panel. Show live join/leave ordering (Admin first, then StarCast, then students alpha).
```

## 6. Whiteboard Detail (if generating standalone)

**Prompt:**
```
Whiteboard component detail. White canvas, light gray grid (48px), NeoBrutalist outer card (bottom/right thick border, rounded 16). Toolbar: PEN/ERASER/HAND/TEXT + Clear, black border, white cards, selected = black. Live stroke preview. Provide mobile (520px height, full-width, touch drag) and desktop (640px) variants. Caption "Default whiteboard — thick bottom/right border, rounded corners". Based on explaino_structura's Excalidraw model but restyled to NeoBrutalist (do not import its original theme).
```

## Shared constraints for all screens

- Event names / role semantics are fixed in `docs/realtime-events.md` — do not rename. Web + Kotlin must use the same `join`, `toggle-mic`, `screenshare-grant`, etc.
- Reuse existing `.neo` / `BrutalCard` primitives; only add new primitives if nothing exists.
- Provide both `MOBILE` and `DESKTOP` artboards per screen; desktop is not a scaled-up mobile view — use side-by-side columns, larger type, and persistent panels.
