# Participant-field implementation and maker validation

PF-001–007 are implemented through the additive private ParticipantField API, pure domain/use-case/store port, JPA adapter and append-only V8 migration. ADR 0007 records the G2/G4-approved transaction, ownership, identity and retention decisions. Existing entry, configuration, announcement, IAM and CLI implementations are unchanged.

| Requirements | Concrete automated evidence |
| --- | --- |
| PF-001 | Pure use-case identity tests prohibit private-source loads; MockMvc tests exercise both anonymous methods, unrelated/missing/nonpositive identity, invalid tokens and sanitized failures; PostgreSQL rejects a foreign real service actor without changes. |
| PF-002 | Pure tests cover key/name/ID/cardinality boundaries, original/current labels, canonical order, duplicates and nonaccepted/incompatible selections. PostgreSQL retains single/pair members, permits empty/one-unit fields and renamed labels, and rejects incompatible first preparation without storage. |
| PF-003 | PostgreSQL verifies virtual-zero state/no writes, failed initialization rollback, explicit clearing/identical replacement increments, independent fields, first-write/replacement races and complete winning views. Source versions and field version are distinct. |
| PF-004 | Pure and PostgreSQL tests preserve saved selection/members/audit/version after source changes and bracket removal; explicit reconciliation returns CURRENT. Real forced-overlap preparation/read scenarios demonstrate consistent earlier snapshots and subsequent OUTDATED observation. |
| PF-005 | PostgreSQL prepares two singles despite configured capacity one and closed intake, with MANUAL review and identical source book. Legacy roster/announcement tables remain empty. Pure tests confirm independent fields. |
| PF-006 | Pure tests retain only latest units plus minimal audit. PostgreSQL checks actor/time, one successful event per version, unchanged audit after rejected/losing writes and full persisted winner equality. |
| PF-007 | Fifteen MockMvc tests cover GET query and PUT/body/header mapping, UNPREPARED/CURRENT/OUTDATED responses, independent version arguments, 400/401/403/404/409, required selectors/headers, sanitized details and no-store. Numeric tokens, list contents, request shapes and Long boundaries are tested without mapper coercion. |

## Actual commands and results

Environment: macOS, temporary Temurin Java 25.0.5 at `/private/tmp/rallyon-jdk25/Contents/Home`, Docker PostgreSQL 18.4 in task-owned `rallyon-217-test-postgres`, `127.0.0.1:51922`. Shared IAM artifacts were already installed for Java 25; no shared IAM source/dependency changed.

- Latest focused `./service/tournamentmgmt/mvnw -B -f service/tournamentmgmt/pom.xml '-Dtest=*ParticipantField*Test,EntryBookControllerSecurityTest,ConfigurationControllerSecurityTest,AnnouncementControllerSecurityTest' test`: **57 tests, zero failures/errors/skips**. Field test URL `jdbc:postgresql://127.0.0.1:51922/rallyon_field224_fix`.
- Latest full `./service/tournamentmgmt/mvnw -B -f service/tournamentmgmt/pom.xml clean verify`: **149 tests, zero failures/errors, three unrelated optional skips**. Fresh separate DBs `rallyon_field224_fix_final` and `rallyon_entry224_fix_final`; both ParticipantFieldPostgresIntegrationTest and EntryBookPostgresIntegrationTest actually executed. Skips: two existing optional Flyway tests and one documentation-generation opt-in.
- For those commands, set JAVA_HOME above, `RALLYON_FIELD_TEST_URL` to the indicated fresh DB, `RALLYON_FIELD_TEST_USER=postgres`, `RALLYON_FIELD_TEST_PASSWORD=rallyon_test`, `RALLYON_FIELD_TEST_ALLOW_MUTATION=yes`. Full verify also sets equivalent `RALLYON_ENTRY_TEST_*` values for its separate fresh DB. These credentials are isolated local test values. Tests refuse a preexisting `tournamentmgmt` schema; do not reuse or delete a populated database.
- Root formatting, explicit Prettier for new Markdown/JSON, strict pinned OpenSpec validation and patch-whitespace checks passed. Latest focused/full logs: `/private/tmp/rallyon-field224-fix-focused.log` and `/private/tmp/rallyon-field224-fix-verify.log`.

The first compile exposed a Kotlin `field` backing-field-name collision in the freshness getter. Qualifying the value as `this.field` corrected it. Initial focused/full runs then passed 53/145 tests, but independent G5 subsequently found a genuine JSON-number coercion defect (see [initial report](evidence/g5-implementation.md)). Endpoint-scoped JsonNode parsing now rejects noninteger, quoted, exponent and overflowing numeric values, invalid list/root shapes and nontext bracket selectors before service invocation. The public service DTO and global/legacy mapper behavior are unchanged. Four additional MockMvc tests cover this correction; the latest 57/149-test runs above followed it. Initial logs remain `/private/tmp/rallyon-field224-focused.log` and `/private/tmp/rallyon-field224-verify.log` for historical evidence.

## Concurrency evidence and limits

`ParticipantFieldPostgresIntegrationTest` has named phases. Competing writers start together and require exactly one returned winner and one conflict; the persisted complete view equals the winner. This does not force every writer interleaving.

The distinct source-overlap phases **do force** commit ordering: a test-only dynamic proxy delegates a real `ParticipantFieldStore.book` read, then pauses via latches after the transaction snapshot exists. Another thread commits a real entry withdrawal and draft change before preparation resumes. The response retains the earlier consistent selection/versions; the next read is OUTDATED with identical saved content. A separate read pauses after source observation while another transaction changes sources and replaces the field; the paused read equals the complete earlier view, while a later read sees the new field version. No production test hook was added.

MockMvc uses a mocked service/JWT decoder; service-level PostgreSQL tests do not prove actual authenticated HTTP. Real Keycloak UAT remains pending after independent G5/G6/G7. No load/throughput claim, historical full-revision reconstruction, verified identity or automatic admission guarantee is made.

## Independent gates

Pending fresh independent implementation and QA checks, three review lenses and human UAT acceptance. Maker results do not certify gates. Accepted #185 research artifacts are preserved in this worktree alongside their separate lifecycle; they introduce no runtime behavior.
