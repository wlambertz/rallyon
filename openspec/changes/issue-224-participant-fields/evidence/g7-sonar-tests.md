# G7 — Sonar delta tests and maintainability review

**Verdict: PASS.** Independent checker `field-review-tests`, 2026-11-11. No blocking finding. This delta-only review supplements the [prior tests review](g7-tests.md) after [G5](g5-sonar-delta.md) and [G6](g6-sonar-delta.md) passed. No maker files changed or gate registered.

| Severity | Changed scope | Finding / action |
| --- | --- | --- |
| None | Four-file Sonar remediation | No test or maintainability correction required. Preserve the separate changed-script UAT and refreshed CI/Sonar checks. |

- Read the complete raw staged delta against `f837f47`: exactly four files, 29 insertions and 17 deletions. SHA-256 is `ae2d96e77f6c9a619756032ed75897e21ae7d1110b4539bddd3b8a6938aa5635`, matching the independent implementation/QA reports.
- `FieldPreparation` bundles the same five nullable values in the original order. The service adapter and pure-test helper construct the same selection/source input; tournament ID, expected field version and actor remain separate. Destructuring invokes ordinary value accessors and preserves authorization/validation/source/load/save order.
- No test assertion or scenario was removed or weakened. Only the pure helper's call shape changed; its original boundary, snapshot, rejection and immutability assertions remain. The controller, public DTO, strict JSON parser, migration and PostgreSQL concurrency tests are unchanged.
- The UAT token command now resolves the repository's `bin/ro` from the script location and invokes an argument list without a shell. This removes a fixed shared-temporary executable dependency; building the ignored CLI is an explicit prerequisite in [remediation evidence](sonar-remediation.md). The extracted draft/intake URL variables preserve the original expressions, HTTP actions and all assertions.

## Checks and limits

- `git diff --cached --check`: **PASS**. Independently parsed the script with `ast.parse` and asserted that `parents[4] / "bin" / "ro"` equals the current repository root plus `bin/ro`: **PASS**. No workflow or token retrieval was executed by this review.
- Inspected `/private/tmp/rallyon-227-sonar-g6.log`: independent affected-suite execution reports **27 tests, zero failures/errors/skips; BUILD SUCCESS**. Counts remain 12 pure and 15 MockMvc tests.
- Inspected `/private/tmp/rallyon-227-sonar-verify.log`: maker full verification reports **149 tests, zero failures/errors, three existing optional skips; BUILD SUCCESS**. The current G5/G6 reports confirm both real PostgreSQL suites executed.
- No additional Maven rerun was necessary for this unchanged narrow delta. Unchanged storage/concurrency coverage retains the [prior QA trace](g6-qa.md) and detailed [prior review limits](g7-tests.md); this review does not claim a new independent full-build execution.
- Rerun the changed authenticated walkthrough before accepting this revision. Current Sonar/CI success and renewed human acceptance are separate evidence and are not inferred from these checks.
