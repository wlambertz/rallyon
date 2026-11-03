# Proposal: retrieve a saved tournament configuration

## Why

The Organizer needs to reload a saved configuration to continue tournament setup. Creation and replacement are implemented, but `ConfigurationServiceImpl.get` still throws `UnsupportedOperationException`. The existing GET endpoint cannot yet serve that workflow.

This narrow capability is the Phase C pilot for #191. It exercises the full lifecycle against real backend behavior while reusing Phase A's application/persistence boundary and Phase B's evidence tooling. Human G1 scope approval is recorded in `.lifecycle.json`; the approved refinement remains unchanged.

## What Changes

- Implement the existing GET-by-ID operation through a pure `GetTournamentUseCase` and `GetTournamentService`, using `TournamentStore.findById`.
- Return the existing API `Tournament` snapshot for stored configurations, including partial drafts and any stored status; translate an unknown ID to the existing 404 ProblemDetail convention.
- Wrap lookup and mapping in a service-boundary read-only transaction. Retrieval does not write configuration, version, audit metadata or status.
- Preserve authentication-only access, as selected by the maintainer: any authenticated caller may retrieve another creator's configuration, including private configurations. Introduce no creator, organizer-role, visibility or new claim restriction.
- Add focused application, service, persistence and HTTP evidence, followed by full backend verification and a witnessed local API walkthrough.

Primary user group: Organizer. Owning bounded context: Tournament Management, existing `setup.configuration` module. The future organizer frontend is a consumer only; no UI work is included.

## Capabilities

### New Capabilities

- `tournament-configuration-read`: retrieve an existing configuration, report missing IDs, retain authentication-only access and leave stored state unchanged.

The current OpenSpec store has no tournament-read capability. Legacy YAML specs describe create/update/auth and contain historical endpoint descriptions; this change adds a focused capability without migrating or editing those legacy specifications.

### Modified Capabilities

None. The existing `feature-lifecycle` specification remains unchanged unless the pilot later demonstrates a workflow defect requiring separately reviewed correction.

## Impact and backward compatibility

`GET /api/tournamentmgmt/config/{tournamentId}`, `ConfigurationService.get(Long)` and the API DTO remain unchanged. The operation gains useful 200/404 behavior in place of its unimplemented failure. Create/update responses, shared JWT validation and role names, database schema and `ro` commands remain unchanged.

No migration, dependency, new bounded context/module, shared IAM modification or new ADR is proposed. Existing ADR 0003 governs the application/persistence separation. Excluded work includes ownership/role policy, frontend, list/search operations, lifecycle transitions, legacy-spec migration, CLI deprecation and dependency remediation.

Specification approval is a separate G2 human decision. Independent G3 checking, exact issue approval at G4 and actual implementation have not yet occurred. G8 requires witnessed API acceptance; the earlier Phase B UAT waiver does not apply.
