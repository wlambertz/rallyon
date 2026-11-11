# G7 — Tests and maintainability review

**Verdict: PASS.** Checker `entry_review_tests`, independent of the makers, reviewed the staged entry-book production code, V7, three test classes, approved ENT-001–007/design/ADR and G5/G6 evidence on 2026-11-11. No blocking findings; no maker files changed and no gate registered.

| Severity | Location | Finding / action |
| --- | --- | --- |
| Low, nonblocking | `EntryBookPostgresIntegrationTest.migratesAndPreservesAtomicEntryWorkflow` | One sequential integration workflow gives relatively coarse failure localization and prevents later scenarios running after an earlier failure. Named phase helpers would improve diagnostics if this test grows; splitting it now would add repeated database setup without improving its assertions. |

## Assessment

- Pure use-case tests assert externally relevant transitions, keys/name limits, cardinality, case sensitivity, cross-discipline reuse, rejected duplicate reacceptance, creator authority and terminal withdrawal. Rejection helpers compare the complete book rather than only an exception.
- MockMvc checks every anonymous operation, actual controller argument/body mapping, positive principal identity, malformed requests, sanitized foreign/not-found errors, status mapping and no-store headers. Its mocked service/decoder boundary is accurately disclosed; this is not real JWT-to-database evidence.
- The PostgreSQL test really upgrades V6→V7, uses the Spring transaction adapter/store and verifies initial lazy storage rollback, acceptance/correction/withdrawal, released claims and preserved snapshots. The fresh-database and explicit mutation opt-ins prevent silent reuse of a populated schema.
- Concurrent initial opening, close/create, reversed doubles and differing corrections require exactly one success and one conflict. Subsequent reads must equal the complete returned winner, including records, intake, version and audit. This detects partial loser writes and lost winning content; unexpected exceptions propagate from futures.
- The duplicate race shares a book version, so by itself it proves version serialization rather than independently testing the database unique constraint. Sequential duplicate/reacceptance tests cover the domain rule, and the actual active-claim primary key backs it. G6 independently reconciled persisted claims and audit by SQL.
- Existing configuration/announcement security and module/layer tests ran in the full suite. New production logic remains separated into domain, use case, transaction mapping, store and web adapters; no unrelated framework/dependency or test-only production seam was added.

## Evidence checked

- Inspected raw staged diffs, all three test classes, production implementation, migration, endpoint documentation and approved requirements/design/ADR. The staged diff for `service/tournamentmgmt`, ADR 0006, design and specs has SHA-256 `1736fac7dd28274bfbb7fcd4ac9a5bc58981004d3d91bb1e8b26598d8a44bac7`.
- `git diff --cached --check`: passed.
- Inspected `/private/tmp/rallyon-219-verify.log`: **121 tests, zero failures/errors, three skips, BUILD SUCCESS**. Skips are the separate existing Flyway tests (two) and documentation opt-in (one); the new PostgreSQL entry test executed.
- Inspected `/private/tmp/rallyon-entry219-g6qa.log` and current Surefire XML: **38 tests, zero failures/errors/skips, BUILD SUCCESS**; entry use-case eight, controller eight, PostgreSQL one, existing configuration security thirteen and announcement security eight.
- G5/G6 record fresh dedicated PostgreSQL databases and exact Java 25/environment/command reproduction. No Maven rerun was needed for this review: two independent focused executions and the full run are already available for the unchanged source.

Limits: the start latch does not force particular SQL interleavings. There is no deterministic simultaneous draft-replacement or multi-query read/write test; those consistency guarantees additionally rely on inspected REQUIRES_NEW/REPEATABLE_READ and the joined selection query. The reports disclose this rather than claiming proof by the races. Live Keycloak HTTP-to-database UAT and explicit human acceptance remain G8 work. No browser coverage applies to the organizer placeholder.
