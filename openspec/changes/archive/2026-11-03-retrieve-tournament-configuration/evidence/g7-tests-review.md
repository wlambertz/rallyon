# Independent G7 tests and maintainability review

Reviewer: `/root/phase_c_review_tests`, a fresh native context independent of all implementation makers and the G5/G6 checker. Lens: tests. Round: 1. Date: 2026-11-02.

Outcome: **pass**. No blocking test-coverage, oracle, reproducibility or maintainability defect was found. This report does not record a lifecycle gate, human UAT or merge approval.

## Reviewed snapshot and method

Reviewed the raw staged service implementation and test diff against HEAD `9ae7655` in `/private/tmp/rallyon-phase-c-retrieve-tournament`, with unchanged mapping, store, transaction, test configuration and architecture collaborators. Read root/service `AGENTS.md`, the lifecycle `references/review.md`, relevant architecture wiki pages, approved capability specification/design, maker implementation evidence and finalized independent G5/G6 reports.

Read-only helper import and `Session('.', 'retrieve-tournament-configuration').fingerprint('G7')` returned `8cfadcd552127a571395c3b979192a3f686ac846457f61cd6b29a17887d938ac`, matching G5/G6. Verified the same fingerprint after report formatting. The source and index were frozen; this reviewer changed only this evidence report and did not delegate, run Maven, record gates, update tasks, or contact GitHub.

Independently parsed all 16 current Surefire XML reports: 89 tests, 0 failures, 0 errors, 1 skip. Relevant suites are query 3, Java service 7, persistence 7, HTTP/security 13, Java mapper 7, layer dependency 2 and Modulith structure 1, all without failures/errors/skips. The sole overall skip is the opt-in documentation exporter. This is independent inspection of existing execution evidence, not a fresh execution claim. G6 provides the separate clean Maven execution and tooling/format/schema checks; no concurrent rebuild was needed.

## Coverage and oracle assessment

| Concern | Concrete evidence and assessment |
| --- | --- |
| Partial drafts and all statuses | `GetTournamentUseCaseTest` returns the exact supplied snapshot, exercises every `TournamentStatus.entries` value with incomplete fields, records requested IDs and zero saves, and verifies repeated reads. Real-service persistence coverage checks nullable optional fields and existing empty-collection normalization; HTTP verifies explicit JSON metadata/null representation without an If-Match header. This combination catches accidental write-time validation or status filtering. |
| Nested values and session lifetime | The persistence fixture stores schedule/windows, venue, courts, discipline/bracket and participant/bracket rosters, then marks the stored record PUBLISHED. OSIV is disabled and the test itself has no enclosing transaction. Assertions run after the service returns and verify no active transaction or bound entity manager, concrete nested values and preserved child IDs across repeated reads. This exercises actual service wiring and materialization rather than a mocked persistence session. |
| Independent expected values | Persistence `read` uses repository plus existing MapStruct mapper in its own transaction, bypassing the new query and its domain/API round trip. Expected fixture values are also asserted directly, including name/visibility, schedule, venue, discipline/bracket and roster contents. Entity child IDs are independently collected. The Java service round-trip fixture alone would be a weak mapping oracle, but these persistence assertions and explicit HTTP JSON prevent it from being the sole evidence. The repository oracle still shares the existing mapper; it is not a fresh specification of every legacy mapping field. |
| Missing record | Query verifies the requested missing ID and zero saves; Java verifies translation to the API-boundary exception; HTTP checks 404, ProblemDetail media type, status, title and detail. No layer relies only on the presence of an exception. |
| Read-only and committed state | Query's fake store fails on save and records save count. Repository snapshots and child IDs remain unchanged after repeated real-service reads. The replacement case compares GET to an independently loaded committed snapshot, explicitly checks version increment and modification actor, preserves creator data, and retains the established pre-flush update-response version behavior. GET is not incorrectly compared against that response's stale version. |
| Timestamp precision | The fixed clock includes nanoseconds; expectations use database-reloaded audit values rather than assuming response precision survives persistence. This is appropriate for stored-value retrieval. Because creation and replacement use the same fixed instant, this fixture does not independently prove that an update advances wall-clock time; that is unchanged write behavior, outside this query's contract. |
| Authentication and authorization | Anonymous and malformed-bearer requests assert 401 before any service invocation. The PRIVATE/different-creator fixture has no organizer authority; the nonnumeric-subject fixture protects the approved absence of an extra acting-user requirement. Successful `jwt()` fixtures intentionally bypass signature verification, while malformed input exercises decoder rejection only. Neither proves real-token issuer, audience or signature acceptance. |
| Compatibility and boundaries | Existing Java service and mapper suites remain Java and passed in G6, protecting Kotlin constructor/API interoperability. Layer rules inspect production code and include negative fixtures proving representative violations are rejected; Modulith verification covers exposed interfaces. The new query remains framework-free and the transaction stays in the service adapter. No dependency/schema/security/DTO change appears in the raw diff. |

## Findings and maintenance cost

Severity: **none requiring correction**. No revision request is raised.

Production changes are small: a one-lookup query, its existing-style interface/bean, and service mapping/error translation. No additional persistence abstraction, query projection, clock, cache, validator or lifecycle machinery is introduced. Test scaffolding is local and simple. The unit, Java boundary, HTTP and persistence checks overlap for different observable contracts; their size is proportionate to nullability, nested JPA loading, authorization and no-write requirements. Existing create/update tests remain intact.

The single fixed-clock limitation and shared existing-mapper portion of the repository oracle are informational bounds, not hidden failing checks or blockers for this read feature. Concrete expected-value checks and raw inspection of the non-mutating path provide complementary evidence.

## Reproducibility and remaining evidence

G6 records canonical `clean verify`, environment versions and exact helper/hook/schema/format/diff commands. The cached OpenSpec path is machine-specific but the maker evidence supplies the portable pinned `@fission-ai/openspec@1.14.2` equivalent. Results and XML counts agree. No new runtime or test dependency is needed.

H2 runs with Hibernate schema creation and Flyway disabled; PostgreSQL compatibility and deployed migration execution are not established here. MockMvc with a mocked `ConfigurationService` proves HTTP/security adapter behavior, while the separate persistence suite proves real service/storage behavior; their composition is not deployed end-to-end evidence. Real Keycloak tokens, PostgreSQL, the live API walkthrough and witnessed human G8 acceptance remain separate. No browser/UAT evidence is claimed, and no human approval is inferred.
