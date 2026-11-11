# G7 correctness and security delta review

Verdict: **pass** for the [original review findings](g7-security.md). Checker: `review-security`, independent of the makers. This is a delta review of the NUL/time validation and persistence replacement on the current working tree.

| Finding | Result |
| --- | --- |
| NUL rejected before PostgreSQL mutation | Resolved. Domain validation rejects U+0000 in each text field. Unit tests exercise all three fields, and the PostgreSQL service test exercises initial publication and replacement, verifies `AnnouncementInvalid` and confirms no public creation or snapshot change. The controller advice maps that exception to HTTP 400; the integration test exercises the service rather than a live authenticated HTTP request. |
| PostgreSQL timestamp range | Resolved for the approved contract. Validation enforces the inclusive limits stated in ANN-005; the PostgreSQL test persists and reads back both limits and rejects an out-of-range replacement without changing the current content. Unit tests cover extreme out-of-range instants. |
| Persistence change | The JPA adapter uses parameterized SQL, `ON CONFLICT DO NOTHING` for competing creation and version-guarded compare-and-set for changes. The PostgreSQL test checks winning content, the losing conflict and current publication state. The start latch does not force a particular SQL interleaving. |

## Independent validation

- With temporary Temurin 25.0.5 and a fresh dedicated PostgreSQL 18.4 database `rallyon_g7_security`, ran `./service/tournamentmgmt/mvnw -B -f service/tournamentmgmt/pom.xml '-Dtest=FlywayPostgresIntegrationTest,*Announcement*Test' test` with the documented `RALLYON_FLYWAY_TEST_*` test-only environment variables and `RALLYON_FLYWAY_TEST_ALLOW_MUTATION=yes`.
- Result: **15 tests passed, 0 failures, 0 skipped**, including both PostgreSQL integration tests, five announcement use-case tests and eight web/security tests.
- No live Keycloak issuer or deployed browser end-to-end test was performed. This delta report does not approve UAT or merge.
