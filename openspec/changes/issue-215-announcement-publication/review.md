# Independent review

## Architecture

Initial [findings](evidence/g7-architecture.md) identified a JPA/design mismatch and ADR naming issue; the independent [delta](evidence/g7-architecture-delta.md) passed after replacing the adapter with JPA and numbering the accepted ADR.

## Correctness and security

Initial [findings](evidence/g7-security.md) identified NUL handling and extreme timestamp uncertainty. After validation fixes and explicit human approval of the input bounds, the independent [delta](evidence/g7-security-delta.md) passed with PostgreSQL and focused tests.

## Tests and maintainability

Initial [findings](evidence/g7-tests.md) identified an incomplete concurrent-correction winner check; the independent [delta](evidence/g7-tests-delta.md) passed after comparing complete competing snapshots.

## Remaining limits

The PostgreSQL latch does not force a specific SQL interleaving. Automated MockMvc tests mock the service and do not alone establish a live Keycloak-to-database flow; the separate UAT walkthrough records actual local integration observations. No independent review is a human UAT or merge approval.
