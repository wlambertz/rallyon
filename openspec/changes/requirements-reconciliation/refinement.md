# Refinement: reconcile national tournament requirements

## Problem and evidence

Issue #183 contains an externally generated requirements document with unverified rule citations and a scope broader than RallyOn's club-level personas. The project needs a traceable assessment before relying on those claims. The issue's legacy YAML paths are obsolete: current specifications live in `openspec/specs/`, as recorded in `docs/legacy-spec-migration.md`.

## Primary persona and owning app/context

The maintainer needs a prioritized, evidence-based basis for future Organizer capabilities. Tournament Management is the principal context; possible Player Management, Scoring and Public Information gaps are identified without assuming those placeholders exist. Organizer benefit is a hypothesis, not a measured result.

## Scope and exclusions

Inventory national championships, national and regional team leagues, senior and invitation events and generic tournament mechanics in the issue reference. Classify each applicable item against current specs and concrete code; retain source section, German term, English gloss, priority, persona value, indicative effort and candidate context. Flag overlap with #184–#186. Check rules proposed for adoption against national or regional primary sources; retain unresolved and unverified items separately. Prepare candidate specifications and exact follow-up issue proposals for accepted gaps.

International-only requirements are listed as deferred. No production code, tests, migrations, runtime configuration, dependencies or frontend stack selection are included. Existing public REST, JWT and CLI contracts remain unchanged by this analysis. Creating follow-up issues requires the lifecycle's later explicit approval.

## Success criteria

- Every in-scope feature is classified or explicitly skipped with a reason; international-only items are listed as deferred.
- Covered and conflicting items cite concrete current specifications or types, distinguishing model shape from working behavior.
- Prioritized gaps identify affected personas, approximate effort, target context and unresolved architectural decisions.
- Any rule proposed for adoption cites a national or regional primary source; unverified claims remain separate.
- Proposed spec and issue drafts are traceable to the assessment, and documentation formatting is checked.

## Assumptions and agreed budget

The requested analysis is not proof that national-event features fit RallyOn's intended audience. No numerical budget has been agreed. The destination for the analysis is this change's planning artifacts; no wiki edits are assumed.

## G1 decision

The maintainer approved the scope presented in the conversation with “go”. This refinement records that displayed scope; the helper must record the actual decision and source before proceeding.
