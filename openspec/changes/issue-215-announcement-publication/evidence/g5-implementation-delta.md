# G5 independent delta check

Verdict: **pass for implementation completion**, subject to normal task bookkeeping and subsequent G6/G7/UAT. Checker: `g3-value`, 2026-11-11. This rechecks only the findings in [the initial G5 report](g5-implementation.md).

| Finding | Delta result |
| --- | --- |
| Competing first publication | Resolved: two service calls start from a shared latch with different titles; the test asserts exactly one conflict, compares the entire stored content to the successful caller's content, and checks published state and version zero. The concurrent start does not deterministically prove overlapping SQL execution in every run; the real PostgreSQL primary key and transaction path are nevertheless exercised. |
| Draft visibility changes | Resolved: actor 10 replaces a draft created by actor 9, changing PRIVATE to PUBLIC while its announcement stays published; after withdrawal the actor changes visibility PRIVATE then PUBLIC and public reads remain not found. This uses the real service and PostgreSQL adapter, not a mocked visibility field. |
| Task bookkeeping | Tasks 1.1–1.6 were still unchecked when inspected. The maker should record completed tasks after incorporating verified evidence; subsequent workflow tasks remain pending until their gates pass. |

## Independent validation

Created a fresh dedicated `rallyon_checker` database in the existing `rallyon-217-test-postgres` container and ran `FlywayPostgresIntegrationTest` with Java 25, `RALLYON_FLYWAY_TEST_ALLOW_MUTATION=yes` and PostgreSQL 18.4: **2 tests passed, none skipped**, including V5→V6, competing create/replace, and visibility independence. The test database is isolated; no existing test database was deleted. An asynchronous socket-close message appeared during Tomcat shutdown, but Maven returned success and no test failed. The earlier independent focused run and remaining review limits are in the initial report.

This check neither approves G6/G7 nor implies human UAT or merge approval.
