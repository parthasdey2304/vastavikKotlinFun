# Vastavik Live Class — NeoBrutalist Design System (Stitch)

> Stitch MCP project: vastavik Live Class (port from kotlin-app). Run once Stitch MCP auth is available (see error `Incompatible auth server…`). Until then this file is the source of truth for generating parity screens.

## Design language (must match kotlin-app exactly)

- **Borders:** thick, **bottom + right edges only** via shadow/hard-offset, not uniform stroke. In web this is `shadow-[4px_4px_0_0_#000]` or `shadow-[5px_5px_0_0_#000]` combined with `border-2 border-black`. In Kotlin this is `BrutalCard`/`BrutalBoxCard` with `shadowOffset = 5.dp`, `borderWidth = 2.dp`, rounded corners.
- **Rounded corners:** `rounded-xl` / `rounded-[16px]` on cards/buttons/panels; Kotlin `BrutalDefaults.Radius = 16.dp`, `RadiusLarge = 20.dp`.
- **High-contrast blocky:** black borders, white surfaces, bold fonts. Accent colors: Yellow/Pink/Blue/Lime/Orange/Purple (see `NeoBrutalistColors`).
- **Existing primitives:** reuse `neo` (.neo) card + `gradient-brand` where needed; do not introduce thin gray borders as the default — they are only for non-neo, non-class contexts.

## Tokens

```
colors: brand #4F46E5, ink #0F172A, muted #64748B
radius: neo 12px (stitch) / 16dp (kotlin)
border: 2px solid #000
shadow: 4px 4px 0 0 #000 (web) / 5dp offset (kotlin)
fonts: Plus Jakarta Sans (display), Inter (body)
```

## Components to generate (see SCREENS.md)

- `ClassLiveBanner` — topic + Join, system + in-app, NeoBrutalist
- `LobbyScreen` — class info + Join (2-screen flow)
- `InClassScreen` — whiteboard default + video tiles + ControlBar (Mic, Video, Screenshare, Cut) with hide-when-not-eligible logic + ChatPanel (reply) + ParticipantsPanel + recording indicator
- `Whiteboard` — Excalidraw-style (pen/eraser/text/hand) but restyled to NeoBrutalist; desktop: full canvas with fixed 640px height; mobile: 520px, full-width
- `ControlBar` — 4 controls, Screenshare hidden unless Admin or granted; remaining 3 expand evenly
- `ChatPanel` — reply preview bubble + quoted preview in message, starCast tag
- `ParticipantsPanel` — roster, mic/cam/hand/share status, starCast tag, kick/share/star assignment
