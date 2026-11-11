# G7 correctness and security review

Verdict: **fail**. Checker: `review-security`, independent of the makers. Review performed on the working tree of 2026-11-11 before subsequent fixes.

| Severity | Location | Finding and required fix |
| --- | --- | --- |
| Medium | `internal/tournament/domain/Announcement.kt:12`, `internal/tournament/persistence/JdbcAnnouncementStore.kt:31`, `internal/AnnouncementServiceImpl.kt:53` | Nonblank announcement text containing the JSON-encodable NUL character (`\u0000`) passes validation, but PostgreSQL text rejects NUL. The resulting JDBC data-access exception is not translated by the service, so creator publication or correction can return a server error rather than a controlled validation failure. Reject unsupported text before mutation or otherwise map this input to a deliberate 400, and verify both initial publication and correction against PostgreSQL with no partial state change. |

The review also checked creator identity, independence from mutable draft visibility, exact public GET matcher versus protected methods and management path, public projection, withdrawal/version compare-and-set, migration constraints and starter JWT configuration. No additional blocking finding was established in those inspected paths. The public-link and management tests mock the announcement service, so they are not an end-to-end live JWT proof.

## Evidence and limits

- Read the approved design/specification, ADR, API, controller/advice, security chain, domain, use case, JDBC adapter, migration, starter security configuration and focused tests.
- `docker exec rallyon-217-test-postgres psql -U postgres -d rallyon_test3 -c 'select chr(0)'` — PostgreSQL 18.4 reported `null character not permitted`; input acceptance and missing exception mapping follow from the cited source. This is not a live HTTP reproduction.
- Very large instants are accepted by domain validation, while PostgreSQL has a bounded timestamp range; `select '1000000-01-01 00:00:00+00'::timestamptz` reported out of range. Check actual JDBC/HTTP behavior and define an input bound or controlled error if it can reach storage.
- No independent full test suite was run in this review; G6 records separate validation. This report does not approve UAT or merge.
