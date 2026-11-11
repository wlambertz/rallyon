# Tasks

## 1. Entry-book core and persistence

- [x] 1.1 Prepare and obtain acceptance of the ADR for local identity, entry-book ownership, consistent selection snapshots, atomic intake/uniqueness/version handling and audit retention (ENT-001–007). Validation: accepted ADR specifies concrete PostgreSQL transaction/locking and rollback semantics before implementation.
- [x] 1.2 Implement pure content/state/intake/authority rules and application ports with tests for every valid and invalid transition (ENT-001–007). Validation: focused entry-domain/use-case tests via `./service/tournamentmgmt/mvnw -B -f service/tournamentmgmt/pom.xml -Dtest='*Entry*Test' test`, confirming tests actually ran.
- [x] 1.3 Add the append-only migration, JPA adapters and transactional mutation boundary; preserve consistent configuration snapshots and existing behavior (ENT-002–007). Validate upgrade from V6, atomic duplicates/closure/corrections, full winning content, audit and unchanged version on failure against a dedicated PostgreSQL database; record exact commands/environment.

## 2. Authenticated API, documentation and delivery

- [x] 2.1 Implement the additive creator-only API for entry-book reads, intake, creation, correction, decisions and withdrawal (ENT-001–007). Test unknown/foreign IDs, anonymous and foreign actors, malformed/stale versions, private responses and unchanged announcement/draft security. Validation: focused `*Entry*Test` and existing controller/security tests with actual test counts.
- [x] 2.2 Document local unverified identity, snapshots, manual capacity, lack of roster synchronization and audit retention (ENT-002, ENT-005, ENT-007). Validation: endpoint-documentation walkthrough and `npm run format:check`.
- [x] 2.3 Run complete backend verification and scenario traceability (ENT-001–007). Validation: IAM install if needed, then `./service/tournamentmgmt/mvnw -B -f service/tournamentmgmt/pom.xml clean verify`, formatting and strict OpenSpec validation; record separate PostgreSQL evidence.
- [ ] 2.4 Obtain independent G5/G6 and three G7 review lenses (ENT-001–007). Validation: current lifecycle evidence and resolved findings.
- [ ] 2.5 Prepare isolated local Keycloak/PostgreSQL organizer UAT, obtain human acceptance and separate merge approval (ENT-001–007). Validation: recorded scenarios, actual observations and explicit decisions.
