# G7 tests and maintainability delta

Verdict: **pass**. Reviewer: `review_tests`, independent non-maker, 2026-11-11. This rechecks the blocking finding in [the original test review](g7-tests.md) and the subsequent persistence and input-bound changes.

| Finding | Delta result |
| --- | --- |
| Competing corrections did not distinguish the winner | Resolved: `FlywayPostgresIntegrationTest` submits different titles, times, venues and bodies, expects one conflict and one success, then compares the entire retrieved `AnnouncementContent` to the successful caller's content and checks version 1. |
| Database-safe input limits | Unit tests reject NUL in each field and extreme instants without changing an existing snapshot; PostgreSQL tests exercise NUL rejection and round-trip both exact approved time endpoints. The current ANN-005 and design explicitly describe those limits. |
| Persistence adapter change | The original JDBC adapter was replaced with a JPA native-query adapter using conflict-safe insert and version-qualified update. The PostgreSQL integration test exercises the actual application service and adapter, including competing creation and correction. |

The independent [G5 boundary check](g5-approved-bounds-delta.md) ran the current PostgreSQL 18.4 integration test: **2 passed, 0 skipped**. The independent [G6 boundary check](g6-approved-bounds-delta.md) ran the current use-case tests: **5 passed, 0 skipped**. I inspected these results and the changed sources; I did not rerun Maven for this test-lens delta.

The original nonblocking limitation remains: MockMvc mocks the announcement service and the PostgreSQL integration test calls the service directly, so this is not a live-Keycloak HTTP-to-database end-to-end test. Concurrent starts do not force a particular SQL interleaving. UAT and merge remain separate decisions.
