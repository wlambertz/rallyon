# G5 delta: G7 review fixes

Verdict: **pass for G5 implementation completion**. Checker: `g3-value`, 2026-11-11. This rechecks only the changes after [the previous G5 test delta](g5-qa-tests-delta.md); the respective independent G7 reviewers still decide their own review findings.

| Change | Independent observation |
| --- | --- |
| Persistence adapter | `JpaAnnouncementStore` uses the transaction-bound JPA `EntityManager`, PostgreSQL `ON CONFLICT DO NOTHING` for first publication, and a version-qualified SQL update for compare-and-set. The existing primary key and foreign key remain in V6. The real PostgreSQL run covers creation, correction, withdrawal and migration. |
| Security input validation | Domain validation now rejects NUL in all three text fields and extreme instants before persistence. PostgreSQL service tests check NUL on both first publication and replacement, no public snapshot after failed creation and unchanged content after failed correction; they also reject `Instant.MAX`. Unit tests cover NUL in title, venue and body and `Instant.MIN`/`MAX`. Exact supported timestamp boundaries were not independently round-trip tested. |
| Competing corrections | PostgreSQL test submits distinguishable complete replacement contents, verifies one conflict and compares the entire winning content with the stored current snapshot and version. A shared start latch does not guarantee a particular SQL interleaving on every run. |
| ADR and documentation | The accepted ADR is now `docs/adr/0005-tournament-announcement-publication.md`; service documentation records the independent publication state, NUL restriction and timestamp range. |

## Independent PostgreSQL validation

Created fresh isolated databases `rallyon_checker_g7` and, after the additional first-publication NUL test, `rallyon_checker_g7b` inside the existing test container; existing databases were not removed. With Java 25 and PostgreSQL 18.4, ran `-Dtest='FlywayPostgresIntegrationTest,AnnouncementUseCaseTest' test` on each: **7 passed, 0 failures, 0 skipped**. The tests cover the V5→V6 upgrade, successful and competing creates/replacements, invalid data, independent withdrawal and draft visibility. Tomcat logged an asynchronous socket-close exception during shutdown; Maven exited successfully. No live Keycloak issuer or end-to-end HTTP-to-database flow was tested here.

No production files were edited by this checker. G6, the three G7 lenses, UAT and merge require their own current evidence and decisions.
