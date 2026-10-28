---
name: rallyon-design
description: Use this skill to generate well-branded interfaces and assets for RallyOn — "das smarte System für Badmintonturniere" — either for production or throwaway prototypes/mocks. Contains the Swiss-Brutalist-Operations-Terminal design guidelines, colors, type, fonts, tokens, and UI kit components (organizer terminal, Turnier-TV, player view) for prototyping.
user-invocable: true
---

Read the `readme.md` file within this skill, and explore the other available files (`styles.css` + `tokens/`, `guidelines/`, `components/`, `ui_kits/`).

RallyOn's visual language is a **Swiss Brutalist Operations Terminal** (light "paper" primary, dark "terminal" for spectator displays): warm monochrome + one industrial-orange accent, hard seams, low radius, restrained 120–160ms motion, Space Grotesk / IBM Plex Sans / IBM Plex Mono / Doto. **User-facing copy is German; code is English.** Operational vocabulary (Feld, Desk, Checkpoint, Anmeldung, Status). No emoji, no marketing tone.

If creating visual artifacts (slides, mocks, throwaway prototypes), copy assets out and create static HTML files that link `styles.css` for tokens. If working on production code, copy assets and read the rules here to become an expert in designing with this brand. Components live as `components/<group>/<Name>.jsx` with `.d.ts` prop contracts and `.prompt.md` usage notes.

If the user invokes this skill without other guidance, ask what they want to build, ask a few questions, and act as an expert designer who outputs HTML artifacts _or_ production code depending on the need.
