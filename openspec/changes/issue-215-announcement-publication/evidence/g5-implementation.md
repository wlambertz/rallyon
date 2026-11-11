# G5 independent implementation check

Verdict: **fail — required security and concurrency evidence is incomplete**. Checker: `g3-value`, 2026-11-11. The inspected implementation largely maps to ANN-001–006, but the following approved validation cases are not demonstrated. This is not G6 or G7 approval.

| Severity | Location | Finding and requested action |
| --- | --- | --- |
| Blocking | `FlywayPostgresIntegrationTest.java`; ADR and `design.md` | Concurrent _replacement_ uses PostgreSQL, but competing initial creates are not exercised through the transactional service. The raw duplicate-key assertion only proves the constraint: it does not verify that the losing service transaction rolls back and maps to a conflict. Add two competing creator publishes on an empty tournament, assert exactly one success and one `AnnouncementConflict`, and verify one intact snapshot on PostgreSQL. |
| Blocking | `FlywayPostgresIntegrationTest.java`; ANN-004/006; `design.md` validation | There is no test changing an existing tournament's draft visibility as another authenticated user and then reading an existing published/withdrawn announcement. A PRIVATE fixture and separate announcement tests are insufficient to exercise the previously identified unauthorized re-exposure path. Exercise real draft replacement, independent publication/withdrawal state and public read before and after visibility changes. |
| Process | `tasks.md` | Implementation tasks 1.1–1.6 remain unchecked despite maker evidence. Update completion status only after corresponding work and checks are actually verified; do not mark pending G5/G6 or later workflow tasks complete. |

## Observed evidence

- ANN-001/005: explicit fields are validated and separately persisted; public controller constructs plain text. The use-case tests cover blank title, overlong body and invalid time range; all text-field boundaries and real HTTP invalid inputs are not exhaustively covered.
- ANN-002/003/006: creator ID is looked up from the tournament, compared to the actor, and creator management is protected. The JDBC adapter uses a primary-key insert and version compare-and-set; the PostgreSQL test exercises concurrent replacement and persisted withdrawal. Unit tests exercise withdrawal and republication. The outstanding cases above remain necessary.
- ANN-004: public GET is matched by numeric resource path and method, outputs UTF-8 plain text with no-store/nosniff, and excludes the roster and other DTO fields. MockMvc tests cover anonymous writes, sibling paths, HEAD, invalid bearer token, withdrawn rendering and an existing protected configuration route. Creator authorization and persistence are reviewed separately; MockMvc's service is mocked.
- V6 adds a foreign key, uniqueness, bounds and independent published/version fields without modifying earlier migrations. The PostgreSQL test prepares V5 before upgrading to V6 and checks existing data and no implicit announcements. The independent check did not rerun the destructive opt-in test against the already populated Docker database.
- ADR `docs/adr/draft-announcement-publication.md` records the maintainer's acceptance and the explicit PRIVATE-versus-withdraw distinction. `service/tournamentmgmt/docs/tournament-announcements.md` describes operations, public access, limits, versioning and that distinction.

## Independent commands and limitations

- `JAVA_HOME=/private/tmp/rallyon-jdk25/Contents/Home ./service/tournamentmgmt/mvnw -B -f service/tournamentmgmt/pom.xml -Dtest='*Announcement*Test,ConfigurationControllerSecurityTest' test` — **24 tests passed**, none skipped, on macOS. The first sandboxed attempt failed because the Kotlin daemon directory was not writable; the authorized rerun passed.
- The maker's complete Maven verification, PostgreSQL upgrade/concurrency run and formatting results are recorded in `verification.md`; they were not independently repeated here. No production files were changed by this reviewer.
