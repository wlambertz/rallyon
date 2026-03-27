---
name: ui-ux-design-system-assistant
description: define or adapt UI design tokens and review user-facing interface text for clarity, consistency, and usability. Use for design-token work in CSS variables, Tailwind, PrimeNG, theme objects, or design-system JSON, and for UX copy review of labels, buttons, helper text, validation messages, empty states, onboarding text, and settings copy.
---

# UI UX Design System Assistant

Use this skill when the task is about organizer-facing design tokens, visual-system consistency, or UX copy quality.

## Read before editing

1. [SYSTEM.md](../../SYSTEM.md)
2. [AGENTS.md](../../AGENTS.md)
3. [skills/organizer-ui/SKILL.md](../../skills/organizer-ui/SKILL.md) when the task touches `application/organizer`
4. [wiki/design/frontend-spaceport-theme.md](../../wiki/design/frontend-spaceport-theme.md) for organizer visual and copy direction
5. [application/organizer/src/styles/README.md](../../application/organizer/src/styles/README.md) for style-layer conventions
6. [application/organizer/design/pencil/README.md](../../application/organizer/design/pencil/README.md) when tokens or shared UI need Pencil sync

Inspect implementation files as needed:

- [application/organizer/src/styles/settings/_tokens.scss](../../application/organizer/src/styles/settings/_tokens.scss)
- [application/organizer/src/app/rallyonpreset.ts](../../application/organizer/src/app/rallyonpreset.ts)
- [application/organizer/src/styles/elements/_typography.scss](../../application/organizer/src/styles/elements/_typography.scss)
- relevant feature templates, styles, and Storybook stories under `application/organizer/src`

Treat code and config as authoritative for shipped behavior. Use the wiki for intended visual direction and UX framing unless implementation contradicts it.

## Modes

Start by determining which mode the request needs:

- `design-tokens`
- `text-review`
- `both`

Ask only the minimum clarifying question when a missing detail would make the result weak or misleading. Otherwise, make explicit assumptions and continue.

## Working rules

- Ground recommendations in the existing RallyOn organizer system before proposing anything new.
- Optimize for consistency over novelty when the repo already contains tokens, theme mappings, or established copy patterns.
- Do not invent brand rules, accessibility guarantees, or product constraints that are not present in repo context or user input.
- Keep organizer copy aligned with the repo’s “tournament operations terminal” direction:
  - short, direct, staff-oriented language
  - no space, orbit, launch, or other cosmic metaphors
  - avoid soft consumer-app wording when an operational term is clearer
- Prefer semantic tokens over raw primitives when the implementation surface supports them.
- Keep token changes minimal and implementation-ready for the target technology the user requests.

## Design-tokens mode

When the request is about tokens:

1. Identify the target technology exactly.
2. Inspect the current token and theme sources before proposing changes.
3. Preserve existing brand and component-library constraints unless the user explicitly wants a broader redesign.
4. Produce only the requested token categories, such as:
   - color
   - typography
   - spacing
   - radius
   - elevation
   - motion
   - breakpoints
5. Explain briefly what should stay, what should change, and why.
6. Favor semantic roles such as surface, text, border, focus, interactive, and status tokens when appropriate.

Common RallyOn outputs:

- CSS custom properties
- SCSS token maps
- Tailwind theme extensions
- PrimeNG theme mappings in `rallyonpreset.ts`
- design-token JSON for cross-tool sync

## Text-review mode

When the request is about UI copy:

- Review labels, CTAs, helper text, validation messages, empty states, onboarding copy, settings text, and transactional UI text.
- Check for clarity, ambiguity, tone, accessibility, consistency, brevity, and actionability.
- Preserve required product terminology such as existing domain nouns when provided.
- Rewrite copy to help the intended user complete the next action faster and with less uncertainty.
- Flag any language that may still need legal, compliance, support, or localization review.
- Explain the main UX reason behind important rewrites.

## Both mode

- Handle tokens first when the copy depends on the component role or UI pattern.
- Otherwise review the text first, then provide token recommendations that support the same UX direction.
- Make sure the visual language and the rewritten copy reinforce each other.

## Response shape

Return Markdown with these sections when applicable:

- `Observed constraints`
- `Assumptions`
- `Token strategy`
- `Design tokens`
- `Text review findings`
- `Rewritten text`
- `Open risks`

Use only the sections needed for the chosen mode.

## RallyOn-specific reminders

- For organizer work, align with the seeded visual system from [wiki/design/frontend-spaceport-theme.md](../../wiki/design/frontend-spaceport-theme.md): monochrome base, `signal` accent, restrained motion, low radius, and editorial-but-operational copy.
- If the task changes shared organizer UI primitives or tokens, keep these in sync when applicable:
  - `application/organizer/src/styles/settings/_tokens.scss`
  - `application/organizer/src/app/rallyonpreset.ts`
  - `application/organizer/src/styles/elements/_typography.scss`
  - `application/organizer/src/stories`
  - `application/organizer/design/pencil/**`
- If the user does not provide enough context, state assumptions explicitly instead of pretending the brand, accessibility level, or audience is settled.
