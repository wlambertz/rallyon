# ADR Draft: Design Tools and Design Token Workflow

## Status

Draft. This document records a proposed direction based on a July 2026 research snapshot. It is not yet an accepted project baseline and does not add dependencies, CI workflows, generated artifacts, or a frontend stack.

The design-tool choice specifically (Penpot vs. Figma + Tokens Studio) was researched and confirmed on 2026-07-09 via issue #139 — see "Design Tool Decision" below. The rest of this ADR remains open for review before overall acceptance.

## Context

RallyOn is expected to grow beyond one user interface runtime. The organizer application is currently a placeholder while the frontend stack is reconsidered, and future interfaces may target different technologies such as web, game engines, or other client runtimes. A reusable visual system therefore needs a source of truth that is independent of any one design tool or implementation stack.

The current RallyOn design brief in `wiki/design/frontend-spaceport-theme.md` already defines initial visual language and token seed values for an operations-oriented organizer UI. What is missing is a durable workflow for managing those design decisions as design tokens and transforming them into platform-specific implementation artifacts.

## Proposed Decision

If this ADR is accepted, RallyOn will treat W3C Design Tokens Community Group (DTCG) JSON files as the source of truth for portable design decisions such as color, spacing, typography, radii, borders, motion, and semantic state values.

RallyOn will use Penpot for visual design exploration and token authoring — confirmed 2026-07-09 via issue #139, see "Design Tool Decision" below. If the maintainer prefers Figma for other reasons, Tokens Studio remains the fallback path for managing W3C DTCG-formatted tokens and syncing token JSON with GitHub.

Style Dictionary should be the transformation layer from DTCG token JSON to target-specific code artifacts. Standard targets such as CSS, SCSS, JavaScript, Swift, Kotlin, Android, or JSON can use built-in Style Dictionary capabilities. Less common targets, such as Godot themes or Unity assets, should use explicit custom transforms instead of hand-maintained duplicate token files.

Pencil.dev can remain an optional screen-prototyping tool for AI-assisted visual exploration, but it should not be the source of truth for design tokens. The repo's Pencil workflow is currently paused because `application/organizer` has no active frontend stack, so Pencil should not be reintroduced as project tooling until a separate frontend/tooling decision is made.

## Design Tool Decision (Resolved 2026-07-09, Issue #139)

Penpot is confirmed as the design-tool choice, re-verified against current sources (not just the original ADR snapshot):

- **W3C DTCG conformance**: the Design Tokens Format Module reached its first stable version (2025.10) in October 2025. Penpot is the first fully open-source design tool to natively implement the stable spec — tokens export/import as DTCG JSON directly, "without conversion." It natively supports 13 token types (color, dimension, sizing, spacing, opacity, rotation, stroke width, typography properties, shadow, etc.) plus token sets and token themes (including multi-dimensional theme groups, e.g. mode + brand + platform).
- **Licensing / self-hosting**: Penpot is MPL-2.0, fully open source, and free to self-host indefinitely; paid hosting is optional, not required. No design-token feature is gated behind a paid tier.
- **Figma + Tokens Studio, re-checked**: the Tokens Studio Figma plugin core is MIT-licensed and free, with free single-file GitHub sync. However, multi-file sync, advanced theme management, and branch switching are Pro-gated, and Tokens Studio has since grown a separate tiered "Studio Platform" product (Starter Plus / Essential / Organization, from €17–499/month) beyond the base plugin. Figma itself is also a paid, closed-source, non-self-hostable product for real team usage. This combination carries real recurring cost and vendor lock-in that Penpot avoids.
- **Known Penpot limitations** (acceptable for RallyOn's first token set): font-size tokens currently require px units (rem support planned), tokens can't be applied to groups, and only one typography/shadow composite token per layer.

**Decision**: Penpot is the design tool for RallyOn's first design-token implementation (issue #140 onward). Figma + Tokens Studio remains documented as a fallback only if the maintainer chooses Figma for reasons unrelated to token tooling.

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

Penpot is the confirmed design tool for the first RallyOn token workflow (see "Design Tool Decision" above). It is open source (MPL-2.0), free to self-host, has native design-token features fully conformant with the stable W3C DTCG spec, supports direct JSON import/export without conversion, and supports token themes for modes such as light and dark. It keeps RallyOn independent from paid design-tool subscriptions while still supporting designer-developer collaboration.

### Figma With Tokens Studio

Figma with Tokens Studio remains a documented fallback if the maintainer prefers Figma for reasons unrelated to token tooling. Tokens Studio supports W3C DTCG token JSON and is listed as a reference implementation of the stable DTCG spec; its Figma plugin core is MIT-licensed with free single-file GitHub sync. However, multi-file sync, theme management, and branch workflows are Pro-gated, and Figma itself requires a paid, closed-source, non-self-hostable subscription for real team use — a recurring cost and lock-in that Penpot avoids.

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
- The design-tool choice was rechecked and confirmed via issue #139 (2026-07-09); remaining tool claims (Style Dictionary transform behavior, CI validation approach) should still be rechecked as those follow-up items are implemented.

## Open Follow-Up Work

- ~~Research and confirm the design-tool choice (Penpot vs. alternatives).~~ Resolved 2026-07-09 via issue #139 — see "Design Tool Decision" above.
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
- Design Tokens specification reaches first stable version (2025.10): <https://www.w3.org/community/design-tokens/2025/10/28/design-tokens-specification-reaches-first-stable-version/>
- Penpot Design Tokens: <https://penpot.app/collaboration/design-tokens>
- Penpot Design Tokens help center: <https://help.penpot.app/user-guide/design-tokens/>
- Tokens Studio token format: <https://docs.tokens.studio/manage-settings/token-format>
- Tokens Studio GitHub sync: <https://docs.tokens.studio/token-storage/remote/sync-git-github>
- Tokens Studio Figma plugin (MIT license): <https://github.com/tokens-studio/figma-plugin>
- Tokens Studio pricing: <https://tokens.studio/pricing>
- Style Dictionary: <https://styledictionary.com/>
- Style Dictionary configuration: <https://styledictionary.com/reference/config/>
