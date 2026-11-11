# G5 — Independent implementation check

**Verdict: PASS.** No blocking implementation findings against ENT-001–007 in the staged 24-file change for #219/#221/#222. Checker: `entry_implementation_check`, independent of the three makers. Reviewed on 2026-11-11; no maker files changed and no lifecycle gate registered by this checker.

| Severity | Location                                 | Finding / action                                          |
| -------- | ---------------------------------------- | --------------------------------------------------------- |
| None     | Entry-book production code, V7 and tests | No implementation defect requiring correction identified. |

## Requirement traceability

| Requirement | Independent assessment |
| --- | --- |
| ENT-001 | Controller resolves positive authenticated identity; use case compares stored creator before loading or mutating a book. Foreign/anonymous requests and existing announcement/configuration security are covered. |
| ENT-002 | Pure rules enforce local ASCII keys, UTF-16 name limits, NUL rejection, member count and distinctness. One selection query captures discipline/bracket labels and team size in the transaction snapshot. |
| ENT-003 | Create/correct yield RECEIVED, same-state decisions conflict, acceptance/rejection are explicit, and withdrawal is terminal. Invalid transitions preserve the complete book in tests. |
| ENT-004 | Absent storage reads CLOSED/version 0 without writes. Explicit intake gates creation/correction independently of configuration/announcement state; decisions and withdrawal remain available while closed. |
| ENT-005 | Domain checks span brackets and pair order; case-sensitive PostgreSQL active-claim uniqueness backs them. Rejection/withdrawal release claims, and reacceptance/correction recheck membership. No roster/capacity automation added. |
| ENT-006 | REQUIRES_NEW + REPEATABLE_READ, book-row locking, version comparison, atomic persistence/audit and post-rollback conflict mapping implement the ADR. Actual PostgreSQL races compare complete winning content, audit and version. Successful and mapped-error HTTP responses use no-store; unauthenticated requests are rejected by existing security. |
| ENT-007 | Saved selection/member snapshots survive bracket removal; decisions/withdrawal use stored content. V7 is additive and changes no shipped migration, existing endpoint, JWT contract, roster or announcement implementation. |

Reviewed the approved design/spec/tasks/verification, accepted ADR 0006, API documentation and all new production/test files. The documented independent transaction boundary matches the implementation; no unrecorded scope deviation was found. Delivery tasks 2.4/2.5 correctly remain pending subsequent gates and human decisions.

## Independently reproduced checks

- Created a new isolated database with `docker exec rallyon-217-test-postgres createdb -U postgres rallyon_entry219_g5check`; no existing database was deleted or cleared.
- Ran the following with temporary Temurin 25 and the task-owned PostgreSQL test credentials:

```sh
JAVA_HOME=/private/tmp/rallyon-jdk25/Contents/Home \
RALLYON_ENTRY_TEST_URL=jdbc:postgresql://127.0.0.1:64779/rallyon_entry219_g5check \
RALLYON_ENTRY_TEST_USER=postgres \
RALLYON_ENTRY_TEST_PASSWORD=rallyon_test \
RALLYON_ENTRY_TEST_ALLOW_MUTATION=yes \
./service/tournamentmgmt/mvnw -B -f service/tournamentmgmt/pom.xml \
  '-Dtest=*Entry*Test,ConfigurationControllerSecurityTest,AnnouncementControllerSecurityTest' test
```

Result: **38 tests, 0 failures, 0 errors, 0 skipped; BUILD SUCCESS**. EntryBookPostgresIntegrationTest actually executed, including V6→V7, simultaneous initial opening, close/create, duplicate creation, competing corrections, rollback and retained snapshots. Log: `/private/tmp/rallyon-entry219-g5check.log`.

- `git diff --cached --check`: passed.
- `npm run format:check`: passed.
- `npx -y @fission-ai/openspec@1.14.2 validate issue-219-organizer-entries --strict`: valid.

Limits: this checker reproduced the focused suite, not the maker's full `clean verify`. Race latches do not force every SQL interleaving. Consistency during a simultaneous draft replacement is supported by transaction/selection-query inspection; the database test directly proves preservation after removal. Live Keycloak HTTP-to-database UAT, G6/G7 and human acceptance remain outstanding.
