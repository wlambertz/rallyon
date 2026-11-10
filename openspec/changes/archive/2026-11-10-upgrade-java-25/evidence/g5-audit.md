# G5 independent implementation audit

**Verdict: pending / not complete.** Reviewer: `g5_checker`; examined PR #209 head `bb3690b950decc77e5d6567d16d0852b14612b4b` against the approved tasks and requirements. This is an implementation-readiness audit, not G6 QA or G7 review.

| Severity | Location | Finding and required follow-up |
| --- | --- | --- |
| Blocking | `tasks.md` 2.3; `evidence/flyway-repair.md` | The authenticated 201 smoke request used an isolated signing key and JWKS endpoint, while the approved task and design require the existing local Keycloak authentication flow. Run the documented local provisioning/token flow against an isolated database, or seek an explicit approved scope change; retain the existing signed-token result as separate evidence. |
| Blocking | `tasks.md` 2.2; `evidence/java25-preflight.md` | A temporary Dockerfile differing only in the JDK image built and `docker run ... java -version` reported 25, but the evidence explicitly says the full dev-container lifecycle and editor configuration were not validated. Build/start the actual configured dev container and capture its post-create outcome and editor/runtime settings, or obtain an approved change to the validation requirement. |
| Blocking | `tasks.md` 2.4 and `evidence/flyway-repair.md` | Final task completion and current-snapshot CI/security/SBOM results have not yet been incorporated in durable implementation evidence. Record exact PR head and workflow-run links/results, environment and remaining limitations; then reconcile the unchecked tasks after the missing checks complete. Historical statements that the candidate had not seen hosted CI should remain clearly time-qualified. |
| Existing failure | `tasks.md` 2.1; `openspec/changes/issue-203/.lifecycle.json` | Maker's `npm run format:check` fails on this unrelated tracked file; the PR diff itself passes `git diff --check`. Preserve the unrelated work, establish the baseline and report the exception for G6; do not claim the required full formatting check passed. |
| Rollout risk | `README.md`, Flyway integration | A populated schema without matching Flyway history may now prevent startup. This behavior is documented but requires database-specific assessment before rollout; no real existing database migration was verified. |

## Reviewed evidence and commands

- `git rev-parse HEAD`, `git diff --check origin/main...HEAD`, and `git diff origin/main...HEAD` over service, IAM, workflows, container and documentation: inspected; no whitespace errors. Service and IAM POM targets are 25; Kotlin target follows `java.version`; the scanner continues to strip inherited `JAVA_HOME`.
- Maker evidence reports JDK 25 IAM/service tests, both PostgreSQL migration scenarios, image health/authenticated smoke, local dependency scan and SBOM. These were not independently rerun in this G5 audit.
- `gh pr view 209 --json headRefOid,isDraft,statusCheckRollup` on 2026-11-04: PR remains draft at the above head; Java build/test, dependency preparation, vulnerability scan, SBOM, CodeQL and lifecycle checks report success. This observation is not a substitute for durable scenario-to-test traceability or G6.
- `rg` across POM files identifies the starter's dependency on IAM core and Tournament Management's dependency on the starter. The README states the Java 25 consumer minimum; external consumers cannot be established from this repository alone.
