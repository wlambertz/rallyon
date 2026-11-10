# G7 tests and maintainability review

**Verdict: PASS with documented limitations.** Reviewer: `g7_tests`; reviewed source head `0f2bb8619cb7f0ec8c5527ba5d5d25383191928a` and current G6 evidence. No blocking findings in the reviewed change.

| Severity | Location | Finding and disposition |
| --- | --- | --- |
| Low | `evidence/bootstrap-validation.md` | PostgreSQL 18 volume safety, stale-file handling and real Keycloak provisioning were exercised in disposable isolated stacks rather than automated in CI. Keep the documented reproduction and rerun when changing these bootstrap paths. |
| Low | `FlywayPostgresIntegrationTest.java`, `.github/workflows/ci.yaml` | CI runs both migration tests on a dedicated PostgreSQL 17 database; PostgreSQL 18 startup and volume behavior rely on the separate isolated test evidence. Existing real data layouts remain a rollout limitation. |
| Existing limitation | `evidence/g6-qa.md` | Full `npm run format:check` fails on the unrelated issue-203 lifecycle file, independently reproduced on `origin/main`. Changed-file formatting passes; the full check must not be reported as green. The existing documentation test is skipped. |

The production Spring configuration is loaded by the migration success test, while a deliberately invalid migration checks startup failure. Database mutation requires explicit opt-in and a schema absence check. G6 independently reran the JDK 25 IAM and service verification with both PostgreSQL cases and checked CI, Go, Compose and OpenSpec evidence. The review inspected the specified raw diff and test sources; it did not rerun those tests.
