# Implementation verification

## Maker evidence

Implementation covers ANN-001–006 in the announcement domain/use case, JPA adapter, creator-only API, public text controller and service security chain. The accepted ADR is `docs/adr/0005-tournament-announcement-publication.md`; the API usage and independent visibility behavior are documented in `service/tournamentmgmt/docs/tournament-announcements.md`. Independent G5, G6 and three G7 review lenses have passed for the current source snapshot.

| Requirement | Evidence prepared |
| --- | --- |
| ANN-001 and ANN-005 | `AnnouncementUseCaseTest` checks explicit content, invalid values and limits; `FlywayPostgresIntegrationTest` checks the migration and database constraints. |
| ANN-002 | `AnnouncementUseCaseTest` checks creator versus another user; `AnnouncementControllerSecurityTest` checks authentication, principal handling and management access. |
| ANN-003 | `AnnouncementUseCaseTest` checks stale and losing compare-and-set; `FlywayPostgresIntegrationTest` runs two concurrent replacements on PostgreSQL. |
| ANN-004 | `AnnouncementControllerSecurityTest` checks public plain text, missing/withdrawn content, no caching and no unrelated private data; the use case reads only announcement state, not draft visibility. |
| ANN-006 | `AnnouncementUseCaseTest` covers withdrawal, management recovery, stale replacement and explicit republication; PostgreSQL service test covers persisted withdrawal. |

Commands and actual results on macOS with temporary Temurin 25.0.5:

- IAM install: `JAVA_HOME=/private/tmp/rallyon-jdk25/Contents/Home ./service/tournamentmgmt/mvnw -B -f 3rd_party/iam/pom.xml install` — passed.
- Focused `-Dtest='*Announcement*Test,ConfigurationControllerSecurityTest' test` — 23 passed before the final controller test addition; full verify below includes the final sources.
- `JAVA_HOME=/private/tmp/rallyon-jdk25/Contents/Home ./service/tournamentmgmt/mvnw -B -f service/tournamentmgmt/pom.xml clean verify` — passed after review fixes, 104 tests, 3 skipped opt-in cases.
- Separate `-Dtest=FlywayPostgresIntegrationTest,AnnouncementUseCaseTest clean test` with `RALLYON_FLYWAY_TEST_ALLOW_MUTATION=yes` against a dedicated Docker PostgreSQL 18.4 database — 7 passed after JPA and review fixes, including V5→V6 upgrade, competing initial publication and corrections, NUL/time rejection and actual foreign-actor draft visibility changes. The independent G5 checker previously repeated the expanded concurrency test in a fresh database: 2 passed. A start latch does not guarantee SQL overlap on every run.
- `npm run format:check` and explicit Prettier checks of the new Markdown/JSON — passed.

## Independent implementation and QA checks

G5 independent initial findings and passing deltas are in [the initial report](evidence/g5-implementation.md), [delta](evidence/g5-implementation-delta.md) and [test delta](evidence/g5-qa-tests-delta.md); G6 is in [the initial QA report](evidence/g6-qa.md) and [delta](evidence/g6-qa-delta.md). Subsequent current checks are recorded in evidence/g5-approved-bounds-delta.md, evidence/g6-approved-bounds-delta.md and the three evidence/g7-*-delta.md reports. The maker results above are not independent gate evidence. The subsequent local HTTP walkthrough used an isolated real Keycloak and PostgreSQL; its observations and human acceptance are in uat.md.
