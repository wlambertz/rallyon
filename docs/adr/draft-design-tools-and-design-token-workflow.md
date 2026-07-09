# ADR Draft: Design Tools and Design Token Workflow

## Status

Draft. This document records a proposed direction based on a July 2026 research snapshot. It is not yet an accepted project baseline and does not add dependencies, CI workflows, generated artifacts, or a frontend stack.

## Context

RallyOn is expected to grow beyond one user interface runtime. The organizer application is currently a placeholder while the frontend stack is reconsidered, and future interfaces may target different technologies such as web, game engines, or other client runtimes. A reusable visual system therefore needs a source of truth that is independent of any one design tool or implementation stack.

The current RallyOn design brief in `wiki/design/frontend-spaceport-theme.md` already defines initial visual language and token seed values for an operations-oriented organizer UI. What is missing is a durable workflow for managing those design decisions as design tokens and transforming them into platform-specific implementation artifacts.

## Proposed Decision

If this ADR is accepted, RallyOn will treat W3C Design Tokens Community Group (DTCG) JSON files as the source of truth for portable design decisions such as color, spacing, typography, radii, borders, motion, and semantic state values.

RallyOn should prefer Penpot for visual design exploration and token authoring because it is open source, web-based, and exposes native design-token workflows. If the maintainer prefers Figma, Tokens Studio is the fallback path for managing W3C DTCG-formatted tokens and syncing token JSON with GitHub.

Style Dictionary should be the transformation layer from DTCG token JSON to target-specific code artifacts. Standard targets such as CSS, SCSS, JavaScript, Swift, Kotlin, Android, or JSON can use built-in Style Dictionary capabilities. Less common targets, such as Godot themes or Unity assets, should use explicit custom transforms instead of hand-maintained duplicate token files.

Pencil.dev can remain an optional screen-prototyping tool for AI-assisted visual exploration, but it should not be the source of truth for design tokens. The repo's Pencil workflow is currently paused because `application/organizer` has no active frontend stack, so Pencil should not be reintroduced as project tooling until a separate frontend/tooling decision is made.

## Token Source Layout

The first implementation should keep the token source small and explicit:

```text
design-tokens/
  tokens.json
  style-dictionary.config.js
  transforms/
    godot-theme.js
    unity-scriptable.js
  build/
    css/tokens.css
    scss/_tokens.scss
    json/tokens.json
    godot/theme.tres
    unity/Tokens.cs
```

`design-tokens/tokens.json` is the single source of truth. Files under `design-tokens/build/` are generated outputs. A future implementation must decide whether generated outputs are committed for target consumers or ignored and regenerated locally or in CI. The decision should be explicit because the repository already ignores generic `build/` directories.

## Workflow

1. Create a Penpot project and define the first token set using the current RallyOn design brief as the seed for colors, spacing, typography, shape, and semantic states.
2. Export tokens in W3C DTCG-compatible JSON and commit them under `design-tokens/tokens.json`.
3. Configure Style Dictionary with the smallest useful set of target platforms.
4. Add custom transforms only for target runtimes that are actually being implemented.
5. Add local and CI validation once generated outputs become part of the delivery workflow.
6. Use Pencil.dev only for exploratory screen prototypes, not for token ownership.

## Options Considered

### Penpot

Penpot is the preferred design tool for the first RallyOn token workflow. It is open source, has native design-token features, supports JSON import/export, and supports token themes for modes such as light and dark. It keeps RallyOn independent from paid design-tool subscriptions while still supporting designer-developer collaboration.

### Figma With Tokens Studio

Figma with Tokens Studio remains a reasonable fallback if the maintainer prefers Figma. Tokens Studio supports W3C DTCG token JSON, can sync token files to GitHub, and can work with Style Dictionary through its transform package. License boundaries should be checked before relying on advanced Tokens Studio features such as theme management or branch workflows.

### Pencil.dev

Pencil.dev is useful for prompt-driven screen prototyping and agent-assisted design-to-code experiments. It is not a design-token source of truth for RallyOn because its main value is visual/code generation rather than standards-based token governance. It also depends on external AI-agent workflows that should not become baseline repo tooling without a separate decision.

### Other AI Canvas Tools

Tools such as Banani or MagicPath may be useful for focused UI generation experiments, but they are not part of the proposed token workflow because the decision target is portable design-token ownership, not one-off design-to-code generation.

## Consequences

- RallyOn gains a portable design-system foundation before choosing a frontend stack.
- The design source of truth remains reviewable in Git as JSON.
- Platform-specific artifacts can be regenerated instead of manually duplicated.
- Custom transforms are required for targets that Style Dictionary does not support out of the box.
- The first token implementation must define naming rules, semantic token levels, and validation expectations.
- Tool claims, licensing, and cloud/self-hosting constraints should be rechecked before this draft is accepted.

## Open Follow-Up Work

- Create a Penpot project and define first RallyOn tokens for color, spacing, typography, radii, borders, motion, and semantic states.
- Add `design-tokens/tokens.json` in W3C DTCG format.
- Add a minimal `style-dictionary.config.js`.
- Implement the first target transform only after a concrete runtime target is selected.
- Decide whether generated token outputs are committed, ignored, or published through CI artifacts.
- Add CI validation for token format and generated output drift once the workflow is active.

## References

- RallyOn organizer design brief: `wiki/design/frontend-spaceport-theme.md`
- W3C Design Tokens Community Group: <https://www.w3.org/community/design-tokens/>
- Design Tokens Format Module: <https://tr.designtokens.org/format/>
- Penpot Design Tokens: <https://penpot.app/collaboration/design-tokens>
- Tokens Studio token format: <https://docs.tokens.studio/manage-settings/token-format>
- Tokens Studio GitHub sync: <https://docs.tokens.studio/token-storage/remote/sync-git-github>
- Style Dictionary: <https://styledictionary.com/>
- Style Dictionary configuration: <https://styledictionary.com/reference/config/>
