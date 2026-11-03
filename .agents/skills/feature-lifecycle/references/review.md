# Fresh-context reviews at G7

Give each reviewer the approved requirements/design, raw diff, applicable architecture rules and current QA evidence. Use a different native agent context from the maker. Record its identity, reviewed snapshot, findings, severity and outcome; no self-certification or fabricated agent identities.

| Lens | Questions |
| --- | --- |
| architecture | Is ownership consistent with the context map, aggregates and Dependency Rule? Are published contracts preserved, and are required ADR/escalation decisions explicit? |
| correctness | Do behavior, failure paths, concurrency and security assumptions satisfy the actual scenarios? Are identity, secrets, migration and compatibility risks handled? |
| tests | Do tests prove observable behavior and boundary cases? Are checks reproducible, evidence accurate and maintenance costs proportionate? |

Consolidate findings in `review.md`. Resolve blocking findings, rerun affected tests and request independent rechecking. Record rounds and resolution evidence. Escalate disagreement remaining after two rounds instead of forcing consensus. The helper stores one G7 result for each lens; passing one lens does not substitute for the others.

Reduced mode uses one independent review report under the change's `evidence/` directory or in `review.md`; explain omitted ceremony in the planning record `lifecycle.md`. If no independent checker is available, provide the above inputs for a separate session and leave G7 pending.

Keep the report within the skill's [token budget](../SKILL.md#token-budget): verdict and findings first, no restated inputs, delta-only rechecks.
