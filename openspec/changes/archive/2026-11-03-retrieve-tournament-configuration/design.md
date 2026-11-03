# Design: retrieve a saved tournament configuration

## Context and constraints

The existing controller delegates GET-by-ID to `ConfigurationService.get(Long)`, whose implementation is a stub. `TournamentStore.findById` and `JpaTournamentStore` already load a domain snapshot, including mapped nested values. The existing not-found advice returns a 404 ProblemDetail. Phase A separates pure application code from API and persistence adapters; this query follows that structure.

Human G1 approved the refinement. The caller policy is explicitly authentication-only, including callers other than the creator and without an organizer role. This pilot does not establish an ownership or public-discovery policy. The query reads stored configurations of any status, including incomplete drafts.

## Decisions and tradeoffs

1. Add `GetTournamentUseCase.execute(tournamentId: Long): domain.Tournament` and `GetTournamentService`, depending only on `TournamentStore`. Look up once; return the stored domain snapshot or throw the existing domain `TournamentNotFoundException`. Do not call a domain write operation, creation/replacement validator, clock or `store.save`.
2. Construct the pure query in `TournamentUseCaseConfiguration` and inject it into `ConfigurationServiceImpl`. Implement its existing `get(Long)` with `@Transactional(readOnly = true)`, existing domain-to-API mapping and translation to the existing API-boundary `TournamentNotFoundException`.
3. Keep lookup and mapping inside that transaction so JPA lazy collections are resolved before returning. Reuse the existing adapter instead of introducing a query projection or another persistence port for one identifier lookup.
4. Keep `ConfigurationController.get`, the published interface, API DTOs, resource-server configuration and IAM libraries unchanged. GET does not acquire an acting-user parameter or impose a numeric-user-ID requirement beyond existing authentication.
5. Preserve existing normalization of empty/null collections and persistence timestamp precision. The update response currently precedes the final version increment; GET returns the stored version after commit. Tests compare stored snapshots and do not require GET's version to equal a pre-flush update response.

The extra application interface makes the same dependency boundary explicit as create/update. It permits focused query tests and error translation without importing JPA into application code. Reusing the existing mapping adds no new mapping policy; tests cover representative nested data and nullable configurations to catch omissions.

## Bounded contexts, invariants and dependency direction

The sole owner is Tournament Management's existing `setup.configuration` module. The existing `Tournament` aggregate is read but not mutated. No events, new aggregates or cross-context calls are introduced.

Flow: controller → published `ConfigurationService` → transactional service adapter → pure query use case → `TournamentStore.findById` → existing JPA adapter; the service adapter maps the returned domain snapshot to the existing API DTO. Missing data is translated back through the existing exception/advice boundary.

Domain and application remain framework-free and do not depend on published DTOs. Persistence, transactions and HTTP translation remain outer details. Existing ArchUnit and Modulith tests continue to enforce these boundaries.

## Compatibility and escalation assessment

- REST path, published service signature and JSON representation: unchanged; the stub gains 200/404 behavior. No ETag, caching, pagination, conditional GET or new status-code policy is introduced.
- JWT issuer/audience/role mapping and `rallyon_user_id`: unchanged. A different authenticated creator/role is intentionally not a denial condition in this pilot.
- Database/Flyway: no schema change or migration. Existing stored states remain readable.
- Dependencies, bounded contexts/modules, shared IAM and `ro`: no changes.
- No new ADR is needed: this is a small application of ADR 0003, with the access choice documented in the approved refinement and capability specification.
- Any discovered need for a migration, shared IAM change, compatibility break, new dependency/module or changed specification returns to the applicable human gate before implementation expands.

## Validation, rollout and recovery

| Requirement | Planned evidence |
| --- | --- |
| GET-CONFIG-001 | New pure `GetTournamentUseCaseTest` for existing snapshots, nullable drafts and another status; Java `ConfigurationServiceImplTest` for query delegation/mapping; persistence and HTTP success cases |
| GET-CONFIG-002 | Unit missing-record exception, service exception translation and HTTP 404 ProblemDetail |
| GET-CONFIG-003 | Existing security suite extended with anonymous GET rejection and an authenticated caller without an organizer role reading another creator's PRIVATE configuration |
| GET-CONFIG-004 | Unit store-save count stays zero; `DraftPersistenceIntegrationTest` covers repeated reads, nested identities, persisted version/audit values and retrieval after committed replacement |

Use the existing Java service test as an interoperability gate and extend its constructor mocks for the injected query. Use actual service wiring in persistence coverage so tests exercise the read-only transaction and mapping after its session closes. MockMvc `jwt()` coverage establishes request authorization behavior; it does not claim to verify signatures, issuer or audience cryptographically. Existing shared IAM validation tests remain the evidence for that unchanged contract, supplemented by the real Keycloak walkthrough.

Run focused query/service/persistence/HTTP/architecture tests before full verification. Required final checks are IAM installation when needed, service `clean verify`, helper/hook/schema-readiness tests, pinned OpenSpec validation, formatting, link and diff checks. Do not regenerate committed architecture exports as part of verification. H2 tests have Flyway disabled and do not prove PostgreSQL migration behavior.

For witnessed G8, use the existing local Docker Compose and `ro auth token` workflow. Prepare commands for create → GET → update → GET, repeated GET, unknown ID and anonymous access. Record actual observed responses and environment; redact bearer tokens. Docker was unavailable during refinement, so no working stack or successful walkthrough is assumed. Availability is a prerequisite for real UAT, not evidence that has already passed.

Deliver one implementation issue after G4, link it and #191 in the feature PR, and record requirement/check traceability plus pilot observations. Human acceptance and merge remain separate. There is no data migration to roll back; reverting the feature restores the previous stub without modifying tournament records. After acceptance and merge, sync/archive the OpenSpec change and mark Phase C complete without closing remaining migration/deprecation follow-ups.

## ADR assessment

No new architectural decision is required. ADR 0003 supplies the separation and mapping approach; ADR 0004 supplies lifecycle evidence and gate handling. No implementation, post-implementation report or issue creation is authorized by file readiness alone.
