# Tasks

## 1. Decision and implementation

- [x] 1.1 For ANN-001–006, draft an ADR covering the independent announcement boundary, creator authorization, visibility separation, atomic versioning, public-read security and alternatives; obtain its acceptance before production implementation. Validation: accepted ADR explicitly addresses the tradeoffs in `design.md`.
- [x] 1.2 Implement the announcement domain and use cases for explicit content, bounds, creator authority, publication, withdrawal, republication and optimistic conflict handling (ANN-001, ANN-002, ANN-003, ANN-005, ANN-006), with focused domain/use-case tests. Validation: `./service/tournamentmgmt/mvnw -B -f service/tournamentmgmt/pom.xml -Dtest='*Announcement*Test' test` and verify matching tests actually ran.
- [x] 1.3 Add an append-only Flyway migration and persistence adapter for one snapshot per tournament, retained withdrawal state and concurrent version/creation protection (ANN-001, ANN-003, ANN-006). Validate migration and concurrent operations on PostgreSQL, including upgrade from an existing schema; record the actual environment and commands in `verification.md`.
- [x] 1.4 Implement the scoped public plain-text read and authenticated creator management/write contracts, preserving existing JWT and REST behavior (ANN-001–006). Test exact path/method security, literal text, data minimization, cache headers, invalid JWTs, wrong creator, non-disclosure of withdrawn content, and draft-visibility independence. Validation: `./service/tournamentmgmt/mvnw -B -f service/tournamentmgmt/pom.xml -Dtest='*Announcement*Test,ConfigurationControllerSecurityTest' test` and verify matching tests actually ran.
- [x] 1.5 Document the endpoint contract and the independently controlled announcement state, including explicit withdrawal when a tournament becomes private (ANN-002, ANN-004, ANN-006). Validation: `npm run format:check` and inspect the resulting API documentation.
- [x] 1.6 Verify the complete change and trace every scenario to evidence (ANN-001–006). Validation: install IAM if needed using `./service/tournamentmgmt/mvnw -B -f 3rd_party/iam/pom.xml install`, then run `./service/tournamentmgmt/mvnw -B -f service/tournamentmgmt/pom.xml clean verify` and `npm run format:check`.

## 2. Workflow follow-up

- [ ] 2.1 For ANN-001–006, obtain independent G5 implementation and G6 QA checks; validation: current lifecycle status and scenario-to-test evidence.
- [ ] 2.2 For ANN-001–006, obtain three independent G7 review lenses and recheck fixes; validation: current lifecycle status and review reports.
- [ ] 2.3 For ANN-001–006, prepare and witness organizer UAT, then obtain explicit acceptance and separate merge approval; validation: current lifecycle status and recorded human decisions.
