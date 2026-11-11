# G6 — Independent QA

**Verdict: PASS.** No blocking finding. Checker `entry_qa` independently reviewed the approved ENT scenarios, design/tasks, ADR 0006, G5 report, implementation and actual test results on 2026-11-11. No maker files changed; no lifecycle gate registered.

## Scenario trace

`U` = EntryBookUseCaseTest; `H` = EntryBookControllerSecurityTest; `P` = EntryBookPostgresIntegrationTest. All executed in this check.

| Requirement / all specified scenarios | Concrete evidence |
| --- | --- |
| ENT-001: another user; anonymous access | U `foreign actors and unusable identity...` checks unchanged book for read/create/correct/decide/withdraw; intake authority checked separately. H `allEntryOperationsRejectAnonymousAccess` verifies zero service calls, foreign-user errors hide private payloads, missing/nonpositive identity is 403, invalid bearer is 401. P rejects real foreign read/correction/withdrawal. |
| ENT-002: singles/doubles; invalid input | P creates both sizes with persisted selection/member snapshots. U validates key/name boundaries, NUL, missing members/selection, count and distinctness without mutation; valid 64/200 boundaries, case sensitivity and other-discipline reuse are exercised. H verifies request mapping and 201 creation. |
| ENT-003: accepted correction; retained withdrawal; accept/reject | U/P exercise ACCEPTED → corrected RECEIVED, rejection and acceptance, terminal retained WITHDRAWN; U rejects same-state/invalid decisions and correction/repeated withdrawal of withdrawn records with complete book equality. |
| ENT-004: close before creation; withdrawal after closure | P proves CLOSED/version 0 without storage on reads/rejected first mutation, explicit reopen, close/create competition, blocked create/correction, allowed decision/withdrawal while still closed. Intake code has no draft/status/announcement dependency. |
| ENT-005: reversed/shared pair; rejected duplicate reacceptance | U/P reject reversed pairs across brackets; P also checks shared-player conflict on reacceptance after rejection releases claims. U verifies different-discipline reuse. Database claims were independently reconciled in both directions against active records: zero differences. |
| ENT-006: competing corrections/admissions; closure/create | P races initial opening, reversed-pair creation, distinct corrections and close/create; exactly one winner and one conflict, complete winning records/intake/version/audit preserved. Rejection helpers compare full before/after book. H covers numeric/missing/malformed version, 400/401/403/404/409 mapping, no-store and sanitized foreign/missing errors. Source confirms transaction rollback precedes database-conflict mapping. |
| ENT-007: removed selection; acceptance without roster synchronization | P removes brackets and compares full snapshot, rejects correction against removed selection, then withdraws; participant/announcement counts stay zero after acceptance. U independently checks removed-selection survival. Existing ConfigurationControllerSecurityTest and AnnouncementControllerSecurityTest pass. |

Privacy review found no public entry route, payload logging or historical content in audit. Actor derives from the existing authenticated principal and is checked against the stored creator before book access. API errors are consistently mapped by the scoped advice; existing security handles anonymous/invalid-token requests. Success and mapped failures request no-store; default security cache headers remain unchanged.

## Reproduction and result

Created fresh isolated DB with `docker exec rallyon-217-test-postgres createdb -U postgres rallyon_entry219_g6qa`; no existing DB was cleared. macOS, Temurin 25.0.5, Docker PostgreSQL 18.4 on `127.0.0.1:64779`:

```sh
JAVA_HOME=/private/tmp/rallyon-jdk25/Contents/Home \
RALLYON_ENTRY_TEST_URL=jdbc:postgresql://127.0.0.1:64779/rallyon_entry219_g6qa \
RALLYON_ENTRY_TEST_USER=postgres \
RALLYON_ENTRY_TEST_PASSWORD=rallyon_test \
RALLYON_ENTRY_TEST_ALLOW_MUTATION=yes \
./service/tournamentmgmt/mvnw -B -f service/tournamentmgmt/pom.xml \
  '-Dtest=*Entry*Test,ConfigurationControllerSecurityTest,AnnouncementControllerSecurityTest' test
```

**38 tests, 0 failures/errors/skips; BUILD SUCCESS.** Surefire XML confirms U=8, H=8, P=1, configuration security=13, announcement security=8; P actually migrated V6→V7 and used PostgreSQL. Log: `/private/tmp/rallyon-entry219-g6qa.log`.

Additional read-only SQL independently checked persisted audit actor/time, operation and prior/resulting disposition: tournament 601 has contiguous versions 1–15 and 15 events; 602 has version/event 1. Both missing and extra active-claim counts are zero. Output: `/private/tmp/rallyon-entry219-g6qa-audit.txt`.

Limits: this checker reran the focused suite, not full `clean verify` (maker evidence reports 121 tests with three unrelated optional skips). H uses MockMvc and a mocked service/JWT decoder; P calls the real service/database, not authenticated HTTP. Live Keycloak UAT remains G8 work. Races do not force every SQL interleaving. Simultaneous draft replacement and concurrent multi-query reads are supported by inspected REQUIRES_NEW/REPEATABLE_READ and the single selection JOIN, but are not directly forced in automated tests. No browser coverage applies to the organizer placeholder.
