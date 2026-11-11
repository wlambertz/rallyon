# G5 approved input-bounds delta

Verdict: **pass**. Checker: `g3-value`, 2026-11-11. This rechecks the explicit ANN-005/design bounds and the added boundary test against [the previous G5 review](g5-g7-fixes-delta.md). It does not approve G6, G7, UAT or merge.

The implementation rejects U+0000 in title, venue and body and checks both instants against `-4712-01-01T00:00:00Z` and `+294276-12-31T23:59:59.999999Z`, inclusive; chronological ordering ensures both ends lie in range. The former astronomical year corresponds to 4713 BC. `AnnouncementServiceImpl` maps validation failures to `AnnouncementInvalid`; the controller advice maps that exception to HTTP 400. The prior G5 test covered invalid NUL on create and replacement and `Instant.MIN`/`MAX` rejection; the new PostgreSQL test round-trips both exact endpoints in one announcement.

Independently created the fresh isolated PostgreSQL 18.4 database `rallyon_checker_bounds`, then ran the Java 25 `FlywayPostgresIntegrationTest` with its required mutation opt-in: **2 passed, 0 failures, 0 skipped**. The test checks equality of the complete saved and retrieved boundary content. No existing database was deleted. Tomcat logged an asynchronous socket-close exception at shutdown; Maven returned success.

The mapping to 400 is supported by source inspection; this delta did not execute a real HTTP-to-PostgreSQL invalid-input request. Previous concurrency and live-identity limitations continue to apply. The lifecycle helper is authoritative for the stated human approval and current gate freshness.
