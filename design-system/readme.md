# RallyOn — Design System

**RallyOn** — *„das smarte System für Badmintonturniere"* — is a modular platform for running badminton tournaments end-to-end, serving three audiences at once:

- **Organizers (Veranstalter)** run the tournament from a live "ops terminal": setup, scheduling, court/hall allocation, result entry (Kaiser-System, Schweizer System, KO-Bracket).
- **Players (Spieler)** get real-time visibility into their matches, results, and history, with an emphasis on fairness and transparency.
- **Spectators (Zuschauer)** watch live results, standings, and brackets on the **Turnier-TV** display, in-venue or online.

This project is the **formalized visual system** for that product. RallyOn's frontend framework is deliberately not yet chosen — this design system encodes the visual language as framework-agnostic tokens + reference components so the look isn't tied to any one stack.

> **UI language is German. Code/dev language is English.** All user-facing copy in this system is written in German; token names, component names, and docs are English.

## Sources

This system was built **from a written product brief**, not from a codebase or Penpot/Figma export. The brief documents an intended aesthetic in `wiki/design/frontend-spaceport-theme.md` (a **"Swiss Brutalist Operations Terminal"**, inspired by FREITAG and Nothing) and design-token work tracked in issue #146 / sub-issues #140–#145 on the `feature/139-design-token-tool-research` branch. None of those repo resources were attached — if you have them, reconcile the exact values here against them.

---

## DESIGN DIRECTION — Swiss Brutalist Operations Terminal

The organizer UI reads as a **live ops board**, not a marketing welcome screen: status first, quick-action task modules, a field/court manifest. Vocabulary is **operational** — `Feld` (field/court), `Desk`, `Checkpoint`, `Anmeldung` (registration), `Status`. It explicitly **avoids space/launch metaphors** despite the "spaceport" filename.

Core rules:
- Monochrome warm-paper palette **plus one operational accent** (industrial orange `#FF5C2B`).
- Grid layout, **visible borders/seams**, minimal shadows, **low border-radius** (0–4px).
- **Restrained motion** — 120–160ms, no bounce, no overshoot.
- Type: **Space Grotesk** (display), **IBM Plex Sans** (body/UI), **IBM Plex Mono** (data), **Doto** (scoreboard dot-matrix numerals).

---

## CONTENT FUNDAMENTALS

**Language.** User-facing copy is **German**. Formal-but-direct register — address the user with the neutral imperative or nouns, not chatty second person. Prefer noun-labels over sentences in the UI (`Nächstes Spiel`, `Feld 3 · frei`, `Ergebnis erfassen`).

**Tone.** Operational, factual, terse. The interface behaves like an instrument panel: it *states* rather than *sells*. No exclamation marks, no encouragement copy, no emoji. Where English dev-facing strings appear (logs, CLI, code) they stay English and technical.

**Casing.**
- **Eyebrows / labels / status keys:** UPPERCASE, mono, wide tracking — `STATUS`, `FELD-MANIFEST`, `LIVE`.
- **Headings & buttons:** Sentence case German (`Turnier einrichten`, `Ergebnis erfassen`).
- **Data / IDs / scores:** mono or dot-matrix, never sentence-cased.

**Numbers & data.** Scores, seeds, court numbers, clocks are **mono/dot-matrix and tabular-aligned**. IDs use a mono prefix pattern (`T-2026-014`, `M-104`). Times are 24h (`14:30`).

**Voice examples.**
- Empty state: `Noch keine Spiele geplant.` + action `Zeitplan generieren`
- Status line: `Feld 5 · läuft · Satz 2` / `Feld 2 · frei`
- Confirmation: `Ergebnis gespeichert` (no punctuation, no praise)
- Section eyebrow: `SCHNELLAKTIONEN` above quick-action modules

**Avoid:** marketing adjectives ("amazing", "seamless"), motivational copy, emoji, ambiguous friendly phrasing, title-case English in the UI.

---

## VISUAL FOUNDATIONS

**Palette.** Warm off-white **paper** stock (`--paper-0/1/2`, ~`#EAE8E0`→`#FCFBF6`) against warm near-black **ink** (`#16150F`). Chroma on neutrals is kept extremely low (warm, not blue). The interface is **overwhelmingly black-and-white** — ink on paper. The **single accent**, industrial orange `#FF5C2B`, is a *rare highlight reserved almost entirely for LIVE / in-progress signals* (live status pills, blink dots, live court/match seams, the running scoreboard) — plus the fixed "On" of the wordmark. It is **never** used for primary buttons, selection/active nav, toggles, progress, focus rings, or winner highlights — those are all **ink/black-and-white**. If you can see more than a couple of orange marks on a screen at once, something is over-using it. Non-critical status is an **ink→grey ramp** (done darkest → scheduled faintest); there is exactly **one red**, a deep brick reserved for conflicts, kept darker and less saturated than the orange so the two never blur. No green, no amber. A **dark "terminal" theme** (`[data-theme="dark"]`, near-black `#0A0A09`) exists for the Turnier-TV big-screen display.

**Accent budget (the one rule that matters).** Monochrome is the default; orange earns its place only on things that are *happening right now*. Primary actions carry weight through the ink fill + offset print-block shadow, not color.

**Type.** Space Grotesk for display/headings with tight tracking (`-0.02em`); IBM Plex Sans for body and UI at 15px default; IBM Plex Mono for all data, IDs, labels, and eyebrows (uppercase, `0.12em` tracking); Doto for large scoreboard numerals only. Never mix display and body roles.

**Spacing.** Strict **4px grid** (`--s-1`…`--s-24`). Dense by default — this is an information surface, not a landing page. Panels use 20px padding, cards 16px.

**Borders & seams.** The system is **border-led**. Structural seams are hard 1.5px ink lines (`--line-strong`); internal dividers are 1px hairlines (`--line`). Radius is 0 by default, 2px for inputs/chips, 4px for cards/dialogs, pill only for status chips.

**Elevation.** Near-flat. Shadows are avoided in favor of borders. The *only* expressive elevation move is an **offset "print block" shadow** (`--shadow-block`, `3px 3px 0` solid ink) used sparingly on primary CTAs / raised task modules. Overlays (dialogs) get a soft `--shadow-pop`.

**Backgrounds.** Flat paper fills. No gradients, no photographic hero imagery in chrome, no illustrations. Recessed areas ("wells") use `--paper-0`; content sits on `--paper-1/2`. Optional hairline grid/graph-paper texture is acceptable on empty planning canvases only.

**Motion.** Everything is 120–160ms on `cubic-bezier(0.2,0,0,1)` — firm, mechanical, no bounce. `LIVE` indicators use a 2-step blink (`ro-blink`), not a pulse. Reduced-motion is fully honored.

**Hover / press.**
- Buttons: primary is ink-filled with the print-block shadow; hover lifts the ink slightly, press darkens to black + nudges 2px onto the shadow.
- Ghost/secondary: hover fills with `--paper-0` / `--accent-soft`.
- Rows/tiles: hover reveals a left accent seam (2px) + faint tint.

**Focus.** Visible always — a solid 2px ink (or accent, in dark) outline offset by 2px. Never removed.

**Cards.** Flat `--paper-2` fill, 1.5px ink border, 4px radius, no shadow (or `--shadow-2` hairline when raised). A card is a *module* with an uppercase mono eyebrow header and a hairline divider under it.

**Transparency & blur.** Used only for the dialog scrim (`--scrim`, 55% ink). No frosted glass, no translucent panels.

---

## ICONOGRAPHY

No proprietary icon set exists in the brief, so this system standardizes on **[Lucide](https://lucide.dev)** — thin, geometric, consistent stroke — which matches the Swiss/Nothing line quality. It is linked from CDN in the cards and UI kits (`lucide@latest`), rendered at 1.75px stroke to align with the seam weight.

- Icons are **line, never filled**; single-color (inherit `currentColor`).
- Standard sizes: 16px (inline/controls), 20px (nav), 24px (headers).
- **No emoji.** No decorative icons — every icon carries a function (action, status, nav).
- Unicode is used only for tabular glyphs where appropriate (`·` separators, `↑↓` sort).
- Status is communicated by **color + text label + a small square/dot swatch**, not by icon alone.

> **Substitution flag:** Lucide is a substitute chosen for stroke-quality fit — if the real product adopts a specific icon set, swap the CDN link and re-document here.

---

## FONTS — substitution note

All four families (**Space Grotesk, IBM Plex Sans, IBM Plex Mono, Doto**) are the ones named in the brief and are loaded live from **Google Fonts** (`tokens/fonts.css`). No local binaries were provided; if you need self-hosted/offline builds, drop the `.woff2` files in `assets/fonts/` and replace the `@import` with `@font-face` rules.

---

## MANIFEST / INDEX

- **`styles.css`** — global entry point (import this). `@import`s everything below.
- **`tokens/`** — `colors.css`, `typography.css`, `spacing.css`, `borders.css`, `motion.css`, `fonts.css`.
- **`guidelines/`** — foundation specimen cards (Type, Colors, Spacing, Borders, Motion).
- **`components/`** — reusable React primitives, grouped:
  - `forms/` — Button, IconButton, Input, Select, Checkbox, Switch
  - `data/` — Card, StatusPill, Tag, DataTable, Metric
  - `domain/` — MatchCard, BracketNode, FieldTile, ScoreDisplay
  - `feedback/` — Dialog, Banner, ProgressBar, Tabs
- **`ui_kits/`** — full-screen product recreations:
  - `organizer/` — the ops terminal (dashboard, setup, planning)
  - `audience/` — Turnier-TV spectator display (dark theme)
  - `player/` — player self-service view
- **`SKILL.md`** — Agent-Skill wrapper for use in Claude Code.

### Rendering note

The `@dsCard` preview HTML files and the UI-kit `index.html` files are **self-contained**: each inlines the exact primitive source from `components/**` plus React + Babel, so they render identically offline, in the Design System tab, and when opened directly — no dependency on the generated `_ds_bundle.js`. The compiler still discovers and bundles the `components/**/*.jsx` (+ `.d.ts`) for *consumers* of this design system; that bundle is the production API. Consuming projects import primitives from the compiler namespace (e.g. `const { Button, MatchCard } = window.<Namespace>`).
