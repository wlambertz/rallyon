# Issue #139: Research design-tool and token-authoring options for RallyOn

## Summary

Evaluate Penpot, Figma + Tokens Studio, and other viable open-source/low-cost design-token authoring tools against RallyOn's needs, verify the tool claims already made in the ADR's "Options Considered" section are still accurate, and record a confirmed tool decision by updating the ADR (or superseding it) so issue #140 can proceed. Penpot is not a decided choice — the ADR only proposes it.

## Implementation Changes

- Use the evaluation criteria already named in issue #139: native/well-supported W3C DTCG export+import, GitHub-friendly JSON diffs (reviewable token changes in PRs), self-hosting/licensing terms, and fit for a project that may target multiple future client runtimes (not just one frontend stack).
- Re-verify each claim in `docs/adr/draft-design-tools-and-design-token-workflow.md`'s "Options Considered" section (Penpot, Figma + Tokens Studio, Pencil.dev, other AI canvas tools) against current official documentation, pricing, and licensing pages — the ADR is an explicit July 2026 snapshot and tool capabilities/licensing drift over time.
- Only look for additional open-source/low-cost alternatives beyond the two front-runners if one plausibly beats them on the criteria above — this is a narrow scan, not an exhaustive survey.
- Score Penpot and Figma + Tokens Studio side by side against the criteria and record the comparison.
- Record the decision in the ADR itself:
  - If Penpot is confirmed: change the ADR's "Proposed Decision" wording from proposed to decided, with rationale and the verification date.
  - If a different tool is chosen: update the same section to reflect the new choice and rationale, or add a "Superseded by" pointer to a new ADR file if the change is substantial — do not silently overwrite the Draft's original reasoning without a trace.
- No dependencies, CI, frontend stack, or generated artifacts are added by this issue — it stays a research-and-decision issue, consistent with the ADR's own "Status" note.

## Validation

- `npm run format:check` after the ADR edit (docs-only change).
- Manually confirm issue #140's body ("using whichever tool #139 confirms") still reads correctly once the tool is decided.

## Risks / Compatibility

- Tool capabilities, pricing, and licensing are time-sensitive; record the verification date in the ADR update so a future maintainer knows when the claim was last checked.
- No production code, migrations, or CI are touched — the decision stays documentation-only until issue #140 and later begin implementation.

## Assumptions

- Default to preferring an open-source/self-hostable option when the evaluation criteria are roughly tied, per the ADR's own stated preference for keeping RallyOn independent of paid design-tool subscriptions — override only if a paid tool clearly outperforms on the DTCG/GitHub-sync criteria.
- The search for "other viable options" stays narrow rather than exhaustive, since the ADR already narrowed the field to two front-runners.
