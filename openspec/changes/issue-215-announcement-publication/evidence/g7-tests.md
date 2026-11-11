# G7 tests and maintainability review

Verdict: **changes requested**. Reviewer: `review_tests`, independent non-maker, 2026-11-11.

| Severity | Location | Finding and requested action |
| --- | --- | --- |
| Blocking | `FlywayPostgresIntegrationTest.java`, competing replacement block and `concurrentReplace` | Both competing corrections submit the same content, and the test checks only one success, one conflict and version 1. It cannot demonstrate ANN-003's requirement that the winning update is retained: a losing write that overwrote content could pass. Use distinguishable complete contents, capture the winner and compare the complete stored snapshot with it after both calls. Preserve the PostgreSQL test and one-success/one-conflict assertion. |
| Nonblocking | `AnnouncementControllerSecurityTest.java`, `FlywayPostgresIntegrationTest.java` | MockMvc replaces `AnnouncementService`, while PostgreSQL tests call the service directly. Together they cover both layers, but they do not execute a complete HTTP-to-database request with a live issuer. This limitation is already recorded in G6; avoid claiming deployed end-to-end or live Keycloak coverage. |

## Scope and evidence

Reviewed the ANN-001–006 scenarios, domain/use-case, controller/security and PostgreSQL migration/concurrency tests, production call sites, task trace, and the G5/G6 reports. Accepted text limits, missing values, foreign writes, stale withdrawal, public-path separation and announcement visibility independence now have explicit tests. PostgreSQL concurrency starts threads together but does not guarantee a particular SQL interleaving.

No new test command was run for this review; the independent G5 PostgreSQL run and G6 Java-25 runs are recorded in their cited reports. A fresh delta check is required after resolving the blocking finding.
