# Research charter: rule-aware group standings

## Question and decision it informs

For one identifiable badminton competition, which versioned rules govern group standings and their exceptional cases, and what results and ownership boundaries would RallyOn need to support them? The findings inform a later decision on a competition-specific feature; they do not select a universal rule hierarchy.

## Primary persona and owning app/context

The organizer needs trustworthy group rankings during a tournament, including when results are incomplete or corrected. Hall connectivity and interruptions are persona hypotheses, not observed evidence. Tournament Management currently contains rule configuration; the proposed Scoring context is a placeholder. Research their respective responsibilities rather than assuming an ownership decision. Public Information is a potential consumer.

## Sources

- [Issue #203](https://github.com/wlambertz/rallyon/issues/203) and the `requirements-reconciliation` change, including STANDINGS-001 and -002.
- [Issue #185](https://github.com/wlambertz/rallyon/issues/185), whose BWF summary is secondary and unverified; reconcile overlapping group-rule work.
- The selected competition's authoritative competition regulations, applicable edition and any relevant governing-body or organizer provisions. Establish applicability and identify gaps or conflicts; do not assume that the DBV 2026/27 example applies to every event.
- Existing code, specifications and the context map for current behavior and proposed ownership, distinguishing implemented behavior from plans.

## Scope and exclusions

Select a concrete research competition and identify the responsible authority, rule edition and applicability. Investigate required result inputs, completion, ordered tie resolution and lot outcomes, and withdrawal and correction cases. Consider unsupported versions, ownership, prerequisites and possible migration, module, public API and compatibility impacts. Record unresolved choices and source limitations; do not invent rules when authority is unclear.

No production code, tests, migrations, runtime configuration or dependencies change. Draw and seeding research belongs to #185 except where needed to explain the standings boundary. No new frontend stack, universal competition policy, or accepted product requirements are implied.

## Deliverable and success criteria

Produce source-traced `findings.md` with a recommendation and reviewable examples for the selected and unsupported rule versions, equal match wins, incomplete results, unresolved ties and applicable withdrawal or correction cases. Trace the proposed contract to STANDINGS-001 and STANDINGS-002, reconcile #185, and document ownership and prerequisites. Clearly separate verified rules, examples, assumptions and unresolved questions. An independent checker verifies source mapping, scope and documentation checks; the maintainer reviews the findings. Potential follow-up issues are optional and require separate approval.

## Timebox or agreed budget

No timebox or budget has been agreed. Escalate if one is subsequently agreed and would be exceeded.

## Track choice

Research fits because applicability, exceptions and ownership are unresolved knowledge questions. The outcome is evidence and a recommendation, not an implemented capability or a settled documentation correction.

## G1 decision

Pending explicit human approval; record the quote and source in lifecycle evidence.
