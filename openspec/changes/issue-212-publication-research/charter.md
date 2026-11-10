# Research charter: organizer publication scenario

## Question and decision it informs

For one concrete German club badminton tournament, what does an organizer need to publish and subsequently correct a tournament announcement, and what bounded follow-up feature, if any, is justified? The findings inform a later decision on feature scope; they do not approve implementation.

## Primary persona and owning app/context

The organizer prepares the announcement before the tournament and needs reliable information for prospective participants. Tournament Management is the candidate owning context. Public Information may consume published information; its responsibility is an open question. The organizer app is currently a placeholder, with its frontend stack undecided. Persona device and connectivity assumptions remain hypotheses.

## Sources

- [Issue #212](https://github.com/wlambertz/rallyon/issues/212), the [#183 findings](../issue-183-national-reconciliation/findings.md), their source trace, and related [#186](https://github.com/wlambertz/rallyon/issues/186) research where applicable.
- Existing tournament configuration code, tests and OpenSpec contracts, including the `publish` operation, for demonstrated current behavior.
- A selected real club tournament's published announcement and applicable federation or regional primary sources, identifying edition, authority and applicability. If access or applicability cannot be established, record the limitation rather than infer a rule.
- Organizer persona and context map for intended product context, distinguished from validated user evidence.

## Scope and exclusions

Describe the selected scenario, actors, permissions, publication and correction needs and observable outcomes. Map existing inputs and the bounded gap; assess compatibility, ownership, dependencies and unresolved questions. Verify any claimed mandatory announcement fields against applicable primary sources. Treat the tournament as a research case, not an approved product target; do not assume its rules apply universally. Seek evidence for organizer needs without claiming an interview occurred.

Exclude production code, tests, migrations, runtime configuration, dependencies and accepted feature specifications. Do not select an organizer frontend stack, implement registration, integrations or publication, or adopt unverified legal or federation requirements. Record possible API and security implications without changing contracts or auth.

## Deliverable and success criteria

Produce source-traced findings in this change: selected case and authority; current behavior versus candidate needs; publication and correction scenarios; verified facts versus hypotheses and unresolved rules; compatibility and context assessment; recommendation and limitations. Propose an exact follow-up issue payload only if the evidence justifies one. A fresh independent checker verifies claims, file scope and documentation checks; the maintainer reviews the findings. Any feature requires its own lifecycle.

## Timebox or agreed budget

No timebox or numerical budget has been agreed. Escalate if an agreed budget is subsequently exceeded.

## Track choice

Research (`rallyon-research`): the answer and applicable requirements are uncertain. This is neither an approved new capability nor a narrow documentation correction. No production code, tests, migrations, runtime configuration or dependencies change.

## G1 decision

Pending explicit human approval; record the actual quote and source in lifecycle evidence.
