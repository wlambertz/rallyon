# G6 independent QA

Verdict: **fail — required rejection and boundary scenarios lack executable coverage**. Checker: `qa_g6`, 2026-11-11. Full verification passed, but it does not by itself establish every approved security/failure scenario.

| Severity | Requirement / location | Finding and requested action |
| --- | --- | --- |
| Blocking | ANN-002 and ANN-006; `AnnouncementUseCaseTest.kt` | The test executes foreign-actor create and management, but neither foreign-actor replacement nor foreign-actor withdrawal against the real use case. MockMvc tests anonymous DELETE and a mocked stale conflict; they do not prove that a different authenticated user's write is rejected without changing the published snapshot. Add executable foreign-actor replace and withdrawal cases and verify snapshot/state/version unchanged. |
| Blocking | ANN-001 and ANN-005; `AnnouncementUseCaseTest.kt` | Only overlong body, blank title and reversed end are exercised. The approved rejection scenario includes missing and oversized content, while title and venue length limits and missing dates/fields have no executed boundary checks. Add meaningful per-field boundary/invalid cases, including accepted limits and one-over rejection, and confirm the existing snapshot remains unchanged. |

## Scenario trace

| Requirement/scenarios | Evidence and limit |
| --- | --- |
| ANN-001 first publish / existing | `AnnouncementUseCaseTest` and `AnnouncementControllerSecurityTest` cover create, link and duplicate; PostgreSQL service test covers competing create and persisted snapshot. Missing/invalid cases remain incomplete as above. |
| ANN-005 bounds | Use-case test covers body 10,001; missing title/venue boundary checks above. |
| ANN-002 foreign, anonymous, management | Unit covers foreign create/management; controller tests anonymous and missing identity plus management response; foreign replace is missing as above. Existing draft compatibility is covered by existing regression tests and PostgreSQL visibility test. |
| ANN-003 correction, concurrency | Use-case test corrects venue after withdrawal and checks stale/losing CAS; PostgreSQL integration executes concurrent replacement; latch start does not guarantee SQL overlap every run. |
| ANN-004 public, visibility, missing | Controller security tests plain-text allowlist, anonymous read, withdrawn/missing 404 and no-store; PostgreSQL integration changes draft visibility through real service before and after withdrawal. |
| ANN-006 withdraw, stale, foreign | Use-case test covers withdrawal and republication; security test covers anonymous DELETE and mocked conflict. Actual foreign-actor withdrawal remains missing as above; stale withdrawal is not explicitly executed through the real use case. |

## Independent commands and limitations

- `JAVA_HOME=/private/tmp/rallyon-jdk25/Contents/Home ./service/tournamentmgmt/mvnw -B -f service/tournamentmgmt/pom.xml clean verify`: **102 tests, 0 failures/errors, 3 skipped**, macOS/Temurin 25.0.5. Two PostgreSQL opt-in cases were skipped without `RALLYON_FLYWAY_TEST_URL`; one documentation generator is opt-in.
- `npm run format:check` and explicit Prettier check of new Markdown/JSON: passed.
- Independent G5 delta separately ran PostgreSQL 18.4 migration and service concurrency tests: 2 passed; not rerun by this checker. MockMvc uses a mocked announcement service and is not a deployed end-to-end or live Keycloak test. No browser UAT occurred.

G6 remains pending until the maker fills the missing coverage and a non-maker checks the delta; this report does not certify G7, UAT or merge.
