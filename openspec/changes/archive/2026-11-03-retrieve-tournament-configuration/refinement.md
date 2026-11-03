# Refinement: retrieve a saved tournament configuration

## Problem and evidence

An organizer can create and update a stored tournament draft but cannot retrieve it through the existing query endpoint. In baseline `9ae7655`, `ConfigurationController.get` maps `GET /api/tournamentmgmt/config/{tournamentId}` to `ConfigurationService.get`; `ConfigurationServiceImpl.get` throws `UnsupportedOperationException("Not yet implemented")`. This blocks an API client from reloading saved configuration to continue setup.

The application port `TournamentStore.findById` and the JPA adapter already support loading a domain snapshot. Existing API mapping and the not-found exception/advice can be reused. These are observed code facts; the expected reduction in organizer re-entry or interrupted setup work is a persona hypothesis, not a measured benefit.

This is also the Phase C pilot for issue #191: exercise the merged lifecycle with one useful backend capability and record observed workflow friction.

## Primary persona and owning app/context

- Primary persona: Organizer, continuing tournament setup after saving a configuration.
- Owning bounded context: Tournament Management, existing `setup.configuration` module in `service/tournamentmgmt`.
- Future consuming app: `application/organizer`; no frontend implementation or stack selection is included.
- Secondary learning outcome: the maintainer can inspect scope, requirements, value, issues, tests, independent reviews and witnessed acceptance from one real pilot.

## Scope and exclusions

Implement the existing GET-by-ID operation using a pure query use case behind `TournamentStore`, API mapping/error translation at the service boundary and a read-only transaction. Return the existing `Tournament` representation for a stored configuration, including nullable partial drafts and any stored lifecycle status. Unknown numeric IDs use the existing 404 ProblemDetail convention.

Preserve the current authentication-only policy, as explicitly selected in planning: any authenticated caller may retrieve a configuration, including one created by another user. This operation introduces no organizer-role, creator, membership or visibility restriction and no new claim requirement. Existing shared issuer, audience and JWT validation remain unchanged. An anonymous caller is rejected with 401.

Preserve the endpoint path, published service signature, DTO/JSON shape, audit metadata and version semantics. Retrieval must not modify stored data, version, audit fields or lifecycle status.

Excluded: migrations, dependencies, new bounded contexts/modules, shared IAM changes, frontend work, ownership/role policy, organizer lists, public search, other lifecycle operations, legacy-spec migration, CLI deprecation and dependency remediation.

## Success criteria

1. An authenticated GET for an existing ID returns 200 with its persisted configuration, including incomplete drafts.
2. A missing ID returns 404 using the existing ProblemDetail response; an anonymous request returns 401.
3. An authenticated caller different from the creator can retrieve the configuration without an organizer-role requirement, matching the selected access policy.
4. Repeated reads and retrieval after an update preserve configuration, version, audit values and lifecycle status; no write is performed by retrieval.
5. Focused behavior, service, persistence and HTTP checks pass, followed by full backend verification and architecture checks. Lifecycle/OpenSpec and formatting checks also pass.
6. The pilot records actual G1/G2/G4 human decisions, independent G3/G5/G6 checking, three G7 review lenses, explicit witnessed G8 acceptance and separate merge approval. No gate success is inferred from a task request or file existence.
7. A local API walkthrough covers create → retrieve → update → retrieve, repeated reads, unknown ID and anonymous access. The maintainer witnesses reported observations and explicitly accepts G8; the Phase B UAT waiver does not apply.
8. Observed workflow friction and justified improvements are recorded. After acceptance and merge, archive the change and mark Phase C complete while leaving #191's migration/deprecation follow-ups open.

## Assumptions and agreed budget

- No numerical work budget has been agreed.
- The existing local Docker Compose and auth/token workflow will supply the walkthrough environment; actual availability will be checked before use. No running environment or successful UAT is assumed.
- H2 integration checks do not establish PostgreSQL/Flyway behavior; local API evidence will name its actual environment and limitations.
- The isolated branch is `feat/issue-191-phase-c-retrieve-tournament`, based on current `origin/main` at `9ae7655`. The older main checkout and its untracked files are preserved.
- Existing behavior and architecture are authoritative over stale legacy YAML descriptions; new requirements will live in top-level OpenSpec artifacts after G1.

## G1 decision

Pending explicit human scope approval of this artifact. The previous request to implement the plan starts this lifecycle; it is not recorded as an unseen artifact approval. Record the actual decision quote and conversation reference in `.lifecycle.json` before specification preparation.
