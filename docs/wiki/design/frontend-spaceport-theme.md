# Organizer Theme Brief: Swiss Brutalist Operations

## Summary
RallyOn Organizer should feel like a tournament operations terminal: clear, strict, fast, and built for staff who need to scan status and act. The product keeps the mission control + airport operating model, but drops sci-fi styling in favor of a Swiss/brutalist system shaped by FREITAG and Nothing.

This brief is organizer-first and written as an implementation handoff for the current Angular organizer app. It is not a request to redesign the public spectator experience or to build a marketing site.

## Reference Roles
- **FREITAG**: Swiss grid discipline, raw utility, bold editorial hierarchy, hard-edged composition, exposed structure.
- **Nothing**: industrial monochrome restraint, punchy product copy, large quiet negative space, and signal-like dot-matrix moments.
- **RallyOn airport + mission control**: gates become fields, schedules become boards, statuses become operations signals, and dashboards behave like active control surfaces.

The intended result is operational, not nostalgic. It should feel like a live terminal for tournament staff, not like a fashion clone of either reference and not like a retro airport tribute.

## Narrative And Copy
- Replace “spaceport” with “tournament operations terminal.”
- Keep airport and mission-control logic: boards, gates, checkpoints, manifests, alerts, field readiness, live state, and task queues.
- Remove cosmic language, space photography, launch metaphors, orbit metaphors, and decorative futurism.
- Use short, blunt, product-like headings. Good examples: `Sign in.`, `Live board.`, `Open tasks.`, `Field status.`, `Ready now.`
- Use concise operational body copy. Write like software that helps staff move the day forward, not like a campaign headline generator.
- Prefer staff-oriented nouns such as `organizer`, `field`, `desk`, `schedule`, `checkpoint`, `registration`, `status`, and `audit`.
- Do not use terms such as `launchpad`, `crew`, `orbit`, `teleport`, `galaxy`, `starboard`, or similar theme dressing.

## Visual System

### Palette And Token Seed
Use a monochrome base with one operational accent. Most of the UI should remain neutral and let structure do the work.

| Role | Seed value | Usage |
|------|------------|-------|
| `ink` | `#111111` | primary text, strong borders, primary buttons |
| `paper` | `#f3f1eb` | app background |
| `panel` | `#ffffff` | cards, drawers, forms, elevated modules |
| `line` | `#1f1f1f` | default borders and dividers |
| `line-soft` | `#c9c6be` | secondary borders and separators |
| `text-muted` | `#6b6861` | secondary copy and metadata |
| `signal` | `#ff5a36` | live/current/attention states, focus treatment |
| `success` | `#117a37` | positive system outcomes only |
| `warning` | `#b26a00` | caution states only |
| `danger` | `#b42318` | blocking errors only |

Color usage rules:
- `signal` is the primary accent and should carry most visible emphasis.
- `success`, `warning`, and `danger` are reserved for true state communication, not general decoration.
- Purple is not part of the target theme. If migration notes mention existing purple tokens, they are transitional only.
- Avoid gradients, tinted glass effects, and luminous sci-fi color glows.

### Typography
Start with the fonts already available in the organizer app so the first implementation pass does not require font procurement.

| Role | Typeface | Guidance |
|------|----------|----------|
| Display / section heading | `Space Grotesk` | strong hierarchy, tight spacing, sentence case or short uppercase labels |
| Body / form / data text | `IBM Plex Sans` | default UI copy, tables, inputs, helper text |
| Signal / counter / timestamp | `Doto` | limited use for match counts, timestamps, telemetry counters, and compact status widgets |

Typography rules:
- H1 should feel editorial and structural, not promotional.
- Eyebrows and operational labels should be uppercase with generous tracking.
- Dense UI text should stay calm and readable; do not over-style metadata.
- Dot-matrix styling should appear only where a status, count, or time benefits from signal-like treatment.
- Do not turn every heading into a dot-matrix display.

### Grid, Shape, And Surface
- Use a Swiss-style modular grid: `12` columns on desktop, `8` on tablet, `4` on mobile.
- Align panels, headings, labels, and controls to the same rails whenever possible.
- Preserve large quiet margins around major sections so dense information still feels controlled.
- Use visible borders and seams between modules. The UI should look assembled and purposeful, not soft and blended.
- Keep border radius low: `0` to `6px` maximum.
- Keep shadows minimal. Surfaces should read through outline, spacing, and contrast first.
- Backgrounds should be flat or nearly flat. If texture is used, keep it subtle and paper-like rather than atmospheric.

### Motion
- Default motion should be restrained: `120ms` to `160ms`, ease-out, no bounce.
- Use motion to confirm state changes, panel entrances, and live-signal emphasis.
- If a pulse pattern is used, reserve it for real-time or attention-requiring indicators.
- Do not add ambient floating motion or decorative transitions.

## Component Direction

### PrimeNG And Token Contract
The first implementation pass should update `application/organizer/src/styles/settings/_tokens.scss` and `application/organizer/src/app/rallyonpreset.ts`.

Add or remap tokens for:
- neutral surfaces: app background, panel background, border, muted text, strong text
- operational accent: signal color, focus ring, active navigation state
- semantic states: success, warning, danger
- typography roles: display, label, body, dense data, signal counter
- layout primitives: content widths, module gaps, outer page padding
- shape and surface: border radius, border widths, shadow intensity
- motion: fast transition duration and signal pulse duration

PrimeNG mapping expectations:
- Primary buttons map to `ink` surfaces with high contrast text.
- Outlined and text buttons use border and signal emphasis instead of soft fills.
- Tags use compact uppercase styling and square-ish corners.
- Drawer, card, and input surfaces stay monochrome and border-led.
- Focus indication should use `signal` as an outline or inset ring, not a diffuse glow.

### Buttons
- Primary button: solid `ink`, light text, strong border, no pill shape.
- Secondary button: panel background, dark border, `signal` hover/focus accent.
- Text button: minimal chrome, still aligned to the same type and spacing system.

### Panels And Cards
- Treat cards as modules, not as soft marketing tiles.
- Prefer labeled sections with obvious boundaries and clean internal spacing.
- Avoid oversized corner radii and glossy fills.

### Tags, Status, And Indicators
- Use uppercase labels, compact sizing, and restrained status colors.
- A small leading dot, rule, or block is acceptable for signal emphasis.
- Dot-matrix styling is appropriate for time, live count, or field-state widgets only.

### Inputs And Forms
- Labels should be explicit and visible above the field.
- Inputs should feel like checkpoint controls: white or near-white fill, dark border, clear active state.
- Error states should rely on border, message, and status color rather than animation.

### Navigation
- The drawer should read like a control manifest or terminal index.
- Group navigation items as operational destinations, not lifestyle categories.
- Active state should feel decisive, using contrast and border treatment instead of soft tinted pills.

## Screen Guidance

### Login
Replace the current space-photo launchpad treatment in `application/organizer/src/app/features/auth/login.component.html` and `login.component.scss`.

Direction:
- Two-zone composition with a strong editorial left side and a compact checkpoint form on the right.
- Left side carries the product stance and a short operational promise.
- Right side behaves like a sign-in checkpoint with visible structure, not a floating glass card.
- Add room for one or two system-readiness or environment lines such as local status, role, or access note.
- Use direct copy. The experience should say “enter the operations terminal,” not “begin your journey.”

### Shell And Navigation
Restyle `application/organizer/src/app/layout/shell/` and `application/organizer/src/app/layout/navigation/` as the base operating environment.

Direction:
- The shell background should be calm and mostly flat.
- The drawer should feel like an indexed terminal panel with strong branding restraint.
- Brand treatment should be typographic first. Keep icon use minimal and purposeful.
- Small metadata such as `Organizer`, `Preview`, or environment labels can help the drawer feel like a real system surface.

### Dashboard
Restyle `application/organizer/src/app/features/dashboard/` as the core live operations board.

Direction:
- The current hero becomes an operations overview module, not a welcome banner.
- Quick actions become task modules with stronger hierarchy and less card-like softness.
- The upcoming event module should feel like a field manifest or live board entry.
- Use labels, dividers, counters, and status treatments to build rhythm instead of gradients.
- Headings and summaries should be shorter and more decisive.

### Events Placeholder
Convert `application/organizer/src/app/features/events/` from a generic placeholder into an operational workbench stub.

Direction:
- Present it like an empty but structured workspace.
- Include copy that suggests schedule, bracket, and field-management work without sounding speculative.
- Use modules and labels that already match the dashboard system.

### Settings Placeholder
Convert `application/organizer/src/app/features/settings/` into a controls register rather than a note card.

Direction:
- Organize future settings around account, access, integrations, tokens, notifications, and audit surfaces.
- Use list structure and status framing that feel administrative and exact.

## Anti-Patterns
- No space imagery, stars, planets, or nebula backgrounds.
- No launch/orbit/crew metaphors in UI copy.
- No glossy gradients, glassmorphism, or oversized soft shadows.
- No rounded SaaS pill buttons or overly friendly consumer-app softness.
- No literal imitation of Nothing hardware or Glyph lighting as decoration.
- No airport nostalgia styling that weakens legibility or operational clarity.
- No purple-first palette.

## Migration Order
1. Foundations: update `src/styles/settings/_tokens.scss`, typography roles, and `src/app/rallyonpreset.ts`.
2. Shell + navigation: convert the app frame into the new terminal language.
3. Dashboard: reshape hero, quick actions, and status modules into operational surfaces.
4. Login: replace the current space-photo experience with the checkpoint layout.
5. Events + Settings placeholders: bring stub routes into the same system and clean up legacy copy.

## Acceptance Criteria
- The result reads as RallyOn, not as a direct clone of FREITAG or Nothing.
- Another engineer can restyle the current organizer app without making new art-direction decisions.
- The same visual system can later extend to player and spectator surfaces without inventing a second brand language.
- The brief gives enough direction for tokens, PrimeNG semantic mapping, component styling, and screen layout decisions.

## References
- FREITAG reference: <https://freitag.ch/en_UY/mission/product-stories/reflected-bags>
- Nothing reference: <https://de.nothing.tech/>
- Nothing Glyph background:
  - <https://support.nothing.tech/hc/de/articles/22960523274257-Was-ist-das-Glyph-Interface>
  - <https://support.nothing.tech/hc/en-us/articles/36457453507345-What-is-the-Glyph-button>
