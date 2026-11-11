# Implementation and validation evidence

## Maker results

The current uncommitted change implements ENT-001–007 through the private EntryBook API, pure domain/use-case rules, JPA store, explicit REPEATABLE READ command/read transactions, and append-only V7 migration. Accepted ADR 0006 records the identity/ownership and transaction decisions; `service/tournamentmgmt/docs/tournament-entries.md` documents usage and deliberate limitations.

| Requirements | Test evidence |
| --- | --- |
| ENT-001 | EntryBookUseCaseTest rejects foreign/invalid actors; EntryBookControllerSecurityTest exercises anonymous methods, principal claims, foreign access and existing protected contracts. |
| ENT-002 | Pure tests cover key/name boundaries, missing/duplicate/wrong-count members and invalid selection; PostgreSQL uses explicit singles/doubles snapshots. |
| ENT-003 | State tests and PostgreSQL test exercise acceptance, rejection, accepted correction to RECEIVED and terminal withdrawal. |
| ENT-004 | PostgreSQL test verifies initial CLOSED/version 0, no implicit storage from reads/rejections, reopening, close-versus-create, corrections after closure and permitted decisions/withdrawal. |
| ENT-005 | Reversed pairs and cross-bracket duplicate claims are rejected; rejection releases claims and later reacceptance rechecks them. |
| ENT-006 | PostgreSQL races first mutation, intake/creation, duplicate creation and different corrections; full winner records, state, version and audit are compared. Rejected requests preserve the complete book. |
| ENT-007 | Removing configured brackets preserves stored snapshots and withdrawal; invalid new selection is rejected. Participant rosters and announcements remain empty. |

Actual maker commands on macOS with temporary Temurin 25.0.5 and isolated Docker PostgreSQL 18.4:

- Focused `-Dtest=*Entry*Test,ConfigurationControllerSecurityTest,AnnouncementControllerSecurityTest clean test` with dedicated `RALLYON_ENTRY_TEST_*` environment: 35 tests passed before final readability/negative-test additions.
- `JAVA_HOME=/private/tmp/rallyon-jdk25/Contents/Home ./service/tournamentmgmt/mvnw -B -f service/tournamentmgmt/pom.xml clean verify` with `RALLYON_ENTRY_TEST_URL=jdbc:postgresql://127.0.0.1:64779/rallyon_entry219_final`, task-owned test credentials and `RALLYON_ENTRY_TEST_ALLOW_MUTATION=yes`: 121 tests, 0 failures/errors, 3 optional skips. EntryBookPostgresIntegrationTest executed, including actual V6→V7 migration and races.
- `npm run format:check`, explicit Prettier checks for new Markdown/JSON, and pinned OpenSpec strict validation: passed.

Tests against PostgreSQL require a fresh dedicated database; do not reuse or clear a database containing user data. Latches initiate competing threads but do not guarantee a particular SQL interleaving. Actual PostgreSQL serialization conflicts were observed and handled as entry conflicts. No live Keycloak HTTP-to-database UAT has yet run for this feature. The separate prior Flyway integration tests and documentation opt-in test were skipped in the full run; no shared IAM source changed.

## Independent gates

Pending independent G5, G6, three G7 reviews and human G8. Maker results are not gate certification.
