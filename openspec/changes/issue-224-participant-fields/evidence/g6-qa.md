# G6 — Independent QA

**Verdict: PASS.** Fresh checker `field-qa`, 2026-11-11. No blocking finding. Reviewed PF-001–007, approved design/tasks, ADR 0007, current production/test sources, maker verification and the initial G5 finding plus passing delta recheck. No maker files changed or lifecycle gate registered.

| Requirement / specified scenarios | Independent trace |
| --- | --- |
| PF-001: creator read; anonymous/unrelated actor | `ParticipantFieldUseCaseTest` rejects foreign/nonpositive actors before source/field loads. `ParticipantFieldControllerSecurityTest` checks both anonymous methods, missing/nonpositive identity, foreign sanitized errors and invalid tokens. PostgreSQL rejects a foreign service read/preparation without changes. |
| PF-002: explicit single/pair subset; invalid/mismatched selection; empty/one unit | Pure tests cover subset omission, canonical IDs, original/current labels, pair order, all nonaccepted dispositions, wrong discipline/bracket/team size, repeated/unknown/nonpositive/null IDs, duplicate keys and member boundaries. PostgreSQL persists actual single/pair snapshots, first-write one-unit selections, two-unit selection, explicit empty clearing and renamed-label provenance; incompatible first preparation leaves no root. |
| PF-003: virtual state; first/replacement/identical preparation; concurrent source commit; stale/missing versions; competing fields | Pure tests check virtual state, distinct versions, missing/negative/stale inputs and independent fields. PostgreSQL proves no rows from reads/rejected initialization, absent book version 0 without creation, empty/identical increments, separate fields and exactly one winner per raced field version. Persisted complete view equals the returned winner. Forced source-overlap preparation retains the coherent earlier snapshot, then reads OUTDATED. |
| PF-004: source withdrawal/correction/draft edit; unrelated intake/edit; bracket removal/restoration | Pure source-version tests preserve complete saved fields and reconcile explicitly. PostgreSQL forces withdrawal plus draft changes after observation, retains removed-bracket content, rejects preparation until restoration and then reconciles. A separate paused read overlaps source/field commits and equals the complete earlier view; the next read sees the new revision. Exact correction and acceptance-after-preparation flows are supported compositionally by inspected entry version changes and field version equality, not separate field-integration phases. |
| PF-005: closed intake/manual capacity; acceptance does not prepare | PostgreSQL saves two singles with capacity one and closed intake, reports MANUAL and preserves the complete source book. Different field writes leave the other field unchanged. Legacy participant/roster/announcement counts stay zero. Inspected `EntryBookUseCase`/store and V8 add no field writes/triggers to acceptance; field freshness depends on the changed book version only. |
| PF-006: replacement/clearing accountability | Pure and PostgreSQL tests retain latest units only, preserve previous minimal events and reject loser/invalid writes without audit changes. Independently queried roots have contiguous event versions, valid actor/time/PREPARE and latest count matching stored units. Schema audit contains no historical member payload; copied members have valid cardinality and unique field-local keys. |
| PF-007: auth outcomes; missing versus unprepared target; invalid/conflict; removed saved target | Actual MockMvc binding checks valid selector/version mapping, UNPREPARED/CURRENT/OUTDATED, 400/401/403/404/409, required headers/query/body and sanitized no-store responses. All numeric scalar/list positions reject fractions, quoted numbers, exponents, booleans, overflow, nulls and wrong shapes before service interaction; valid Long.MAX_VALUE maps unchanged. PostgreSQL verifies 200-style saved service reads after removal and real missing/unprepared distinctions. |

## Independent execution

Created new dedicated databases with `docker exec rallyon-217-test-postgres createdb -U postgres rallyon_field224_qa` and separately `rallyon_entry224_qa`; no existing database was cleared. macOS, Temurin 25.0.5, Docker PostgreSQL 18.4 at `127.0.0.1:51922`:

```sh
JAVA_HOME=/private/tmp/rallyon-jdk25/Contents/Home \
RALLYON_FIELD_TEST_URL=jdbc:postgresql://127.0.0.1:51922/rallyon_field224_qa \
RALLYON_FIELD_TEST_USER=postgres RALLYON_FIELD_TEST_PASSWORD=rallyon_test \
RALLYON_FIELD_TEST_ALLOW_MUTATION=yes \
RALLYON_ENTRY_TEST_URL=jdbc:postgresql://127.0.0.1:51922/rallyon_entry224_qa \
RALLYON_ENTRY_TEST_USER=postgres RALLYON_ENTRY_TEST_PASSWORD=rallyon_test \
RALLYON_ENTRY_TEST_ALLOW_MUTATION=yes \
./service/tournamentmgmt/mvnw -B -f service/tournamentmgmt/pom.xml \
  '-Dtest=*ParticipantField*Test,*Entry*Test,ConfigurationControllerSecurityTest,AnnouncementControllerSecurityTest' test
```

**66 tests, 0 failures/errors/skips; BUILD SUCCESS.** Field unit/controller/PostgreSQL counts: 12/15/1; entry unit/controller/PostgreSQL: 8/8/1; configuration security 13; announcement security 8. Both PostgreSQL tests actually migrated and executed; the field suite upgraded V7→V8. Log: `/private/tmp/rallyon-field224-g6-qa.log`. Maven finished and shared target is idle.

Additional read-only SQL in the field QA database independently found three prepared roots with version/event counts **8/8, 1/1, 1/1** and contiguous audit ranges starting at 1. Actor/time/operation checks all passed. Incorrect member cardinalities, duplicate field-local keys and latest audit unit-count mismatches are all **zero**. Output: `/private/tmp/rallyon-field224-g6-sql.txt`. `git diff --cached --check` passed.

## Limits

This checker reran the focused suite, not full `clean verify`; the recorded maker run reports 149 tests with three unrelated optional skips. MockMvc mocks the service/JWT decoder; the real PostgreSQL suite calls services and does not prove authenticated HTTP. Real Keycloak UAT remains pending. Writer start latches do not force every possible interleaving; the two source-overlap phases specifically force their documented commit ordering through a test-only port proxy, without a production hook. No throughput, historical reconstruction, verified identity or automatic admission claim is made. No browser coverage applies to the organizer placeholder.
