# ADR Draft: Claude Design as RallyOn's Design System Workflow

## Status

Draft. Supersedes the tool decision in `docs/adr/draft-design-tools-and-design-token-workflow.md` (which confirmed Penpot on 2026-07-09 via issue #139). That decision is reversed by maintainer choice on 2026-07-13: RallyOn will use Claude Design instead of Penpot. See "Reason for Reversal" below. This document does not add dependencies, CI workflows, or a frontend stack on its own — it only records the new design-tool and design-system-source-of-truth decision.

The prior ADR's `Context` section (RallyOn needs a visual system independent of any one implementation stack, given `application/organizer` is a placeholder) still applies and is not repeated in full here.

## Reason for Reversal

On 2026-07-13 the maintainer chose Claude Design (<https://claude.com/product/design>) over continuing with Penpot for design-system/token authoring. This ADR records that reversal and what it changes about the workflow the original ADR proposed.

## What Claude Design Actually Is (Verified 2026-07-13)

Verified against the product page and the `DesignSync` tool available in this environment, not assumed from the name:

- Claude Design is a conversational AI design tool: users describe design needs and Claude produces drafts (prototypes, wireframes, decks, marketing collateral, documents) that are refined iteratively.
- It can import a design system "from GitHub, design files, or codebases" so generated work uses real components, and exports to PDF, PowerPoint, HTML, or partner-tool integrations (Adobe, Canva, Gamma, Lovable, Miro, Replit, Vercel, Wix).
- The `/design-sync` workflow and its `DesignSync` tool keep a **local component library in the repo in sync with a claude.ai "design-system" project**, incrementally and component-by-component (explicitly "never as a wholesale replace"). Components are HTML/CSS preview files; a leading `<!-- @dsCard group="..." -->` comment in each file drives how it's categorized in the Design System pane, compiled into a `_ds_manifest.json`.
- It is included with Claude Pro/Max/Team/Enterprise subscriptions, not a separately licensed product.
- **It does not natively author, export, or import W3C DTCG-format design-token JSON.** Nothing in the product page or the `DesignSync` tool schema describes a token data model — the unit of sync is HTML/CSS component preview files, not a token JSON document.

This means Claude Design does not drop into Penpot's role unchanged. Penpot's role in the prior ADR was specifically DTCG-conformant token authoring feeding a Style Dictionary transform pipeline to multiple runtime targets (CSS, SCSS, Godot, Unity, etc.). Claude Design is closer in shape to the prior ADR's Pencil.dev entry (AI-assisted visual/code generation) than to Penpot's — except that, per this reversal, it now **is** the source of truth rather than staying out of that role the way Pencil.dev was deliberately kept out.

## New Proposed Decision

- RallyOn's design source of truth becomes an in-repo HTML/CSS component library (working name: `design-system/`), authored and iterated on through Claude Design, kept in Git via the `/design-sync` push/pull workflow (`DesignSync` tool: `list_files`/`get_file` to read the remote project, `finalize_plan` + `write_files`/`delete_files` to push local changes).
- Design tokens (color, spacing, typography, radii, motion, semantic states) are expressed as **CSS custom properties** inside that component library, seeded from the existing brief in `wiki/design/frontend-spaceport-theme.md`, rather than as portable W3C DTCG JSON.
- The Style Dictionary transform layer proposed in the prior ADR is dropped. There is no DTCG JSON source to transform from, and Claude Design does not produce one. CSS custom properties are the only target for now.
- Because sync is incremental and file-based, the component HTML/CSS files remain plain Git-tracked files with reviewable diffs, even though visual editing happens through Claude Design. The source of truth is the **committed files in this repo**, not an exclusively-remote claude.ai project — `/design-sync` keeps the two in agreement, it does not make the remote project authoritative on its own.
- Non-web runtime targets (Godot, Unity, etc.) have no transform path under this model. That is an explicit, accepted gap until a concrete non-web client is chosen — see "Consequences."

## Consequences

- **Lost** relative to the prior decision: native W3C DTCG conformance; Style Dictionary's built-in multi-target transforms (CSS/SCSS/JS/Swift/Kotlin/Android already worked out of the box); a defined (if deferred) path to custom Godot/Unity transforms; and design-tool independence from a single vendor (Anthropic), which was the explicit reason Figma + Tokens Studio was passed over in the prior ADR. That tension is intentional and accepted here, not overlooked.
- **Gained**: an AI-assisted design workflow integrated directly with Claude Code (`/design-sync`), removing the need to self-host or separately subscribe to a design tool, since Claude Design is bundled with existing Claude plans.
- RallyOn's design workflow now depends on a Claude subscription tier that includes Claude Design, replacing the Penpot free-tier dependency.
- Token portability to a future non-web runtime becomes a fresh, unscoped problem if/when that runtime is chosen — no speculative solution is built now (consistent with the prior ADR's own "don't build for hypothetical targets" stance).
- Git-reviewability of design changes is preserved as long as the component HTML/CSS files are what's committed; if that discipline slips and edits happen only inside the claude.ai project without syncing back, reviewability is lost silently. Follow-up work should make this sync step a habit, not an afterthought.

## Options Considered

Retained from the prior ADR for history; only the entries below changed status.

- **Penpot** — no longer the chosen tool. Native DTCG conformance and MPL-2.0 self-hosting remain accurate as researched (2026-07-09); superseded purely by maintainer preference for Claude Design, not by a flaw found in Penpot.
- **Figma + Tokens Studio** — still not chosen; the paid/closed-source/non-self-hostable concerns from the prior ADR are unaffected by this reversal.
- **Pencil.dev** — still not the token/design-system source of truth; superseded in that specific role by Claude Design under this ADR.
- **Claude Design** — newly chosen; see "New Proposed Decision" above.

## Open Follow-Up Work

- Stand up the initial `design-system/` directory and first component pass, seeded from `wiki/design/frontend-spaceport-theme.md`.
- Decide `@dsCard` grouping/naming conventions for RallyOn's components.
- Decide whether `_ds_manifest.json` (or any other sync-generated file) is committed or regenerated locally.
- Add lightweight validation that committed component files and the synced claude.ai project haven't drifted apart.
- Revisit non-web runtime token distribution only once a concrete runtime target is actually chosen for an active RallyOn client.

## References

- Claude Design product page: <https://claude.com/product/design>
- Prior ADR (Penpot decision, now superseded): `docs/adr/draft-design-tools-and-design-token-workflow.md`
- RallyOn organizer design brief: `wiki/design/frontend-spaceport-theme.md`
