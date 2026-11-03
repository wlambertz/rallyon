# tournament-configuration-read Specification

## Purpose

Allow the Organizer to reload an existing tournament configuration through the established configuration API without changing stored tournament state. Access retains the current authentication-only policy explicitly selected for the Phase C pilot.

## Requirements

### Requirement: Retrieve stored configuration (GET-CONFIG-001)

The configuration service SHALL return an existing tournament's stored configuration through `GET /api/tournamentmgmt/config/{tournamentId}` with HTTP 200 and the existing API `Tournament` representation. It MUST support partial drafts and any stored status without write-time validation, preserving existing mapping conventions.

#### Scenario: Retrieve a partial draft

- **WHEN** an authenticated caller requests an existing draft with optional configuration fields unset
- **THEN** the response is HTTP 200 with the draft's identifier, stored name, visibility, version, audit metadata and existing optional-field representation
- **AND** the operation does not require missing publication configuration or an `If-Match` header

#### Scenario: Retrieve a configuration in another stored status

- **WHEN** an authenticated caller requests an existing configuration whose stored status is not DRAFT
- **THEN** the response is HTTP 200 with that stored status and configuration
- **AND** no lifecycle transition is performed

#### Scenario: Retrieve nested configuration

- **WHEN** an authenticated caller requests a stored configuration with courts, disciplines, brackets and participant rosters
- **THEN** those supported values and identifiers are returned through the existing API representation
- **AND** mapping completes within the query's transaction rather than requiring an open persistence session after it returns

### Requirement: Report an unknown configuration (GET-CONFIG-002)

The configuration service SHALL report an unknown numeric tournament identifier through the existing tournament-not-found exception convention. The GET operation SHALL return HTTP 404 with the existing ProblemDetail representation, including status 404 and title `Not Found`, rather than returning a null body, an empty tournament or an unimplemented-operation error.

#### Scenario: Missing tournament

- **WHEN** an authenticated caller requests a numeric identifier that has no stored tournament
- **THEN** the response is HTTP 404 with a ProblemDetail describing the missing tournament
- **AND** no record is created or modified

### Requirement: Retain authentication-only access (GET-CONFIG-003)

The GET operation SHALL require authentication under the existing resource-server configuration and MUST NOT introduce a creator, organizer-role, membership, visibility or additional claim restriction. Existing shared JWT validation, issuer, audience and role mapping contracts SHALL remain unchanged.

#### Scenario: Anonymous request

- **WHEN** an unauthenticated caller requests a tournament configuration
- **THEN** the request is rejected with HTTP 401 before configuration retrieval

#### Scenario: Authenticated caller differs from the creator

- **WHEN** an authenticated caller without an organizer role requests an existing PRIVATE configuration created by another user
- **THEN** the response is HTTP 200 with that configuration
- **AND** no creator or organizer-membership comparison is added

#### Scenario: JWT rejected by existing validation

- **WHEN** a supplied token is rejected by the existing resource-server JWT validation
- **THEN** the GET operation remains unauthorized under that configuration
- **AND** the query implementation does not bypass or replace shared token validation

### Requirement: Leave stored state unchanged (GET-CONFIG-004)

Retrieval SHALL be a read-only operation. It MUST NOT invoke the store's save operation, advance the tournament version, update audit metadata, change status or alter nested configuration. It SHALL reflect committed updates using stored version and timestamp values without changing existing create/update response semantics.

#### Scenario: Repeated reads

- **WHEN** an existing configuration is retrieved repeatedly without an intervening write
- **THEN** its persisted configuration, child identifiers, version, audit metadata and status remain unchanged
- **AND** no save is performed by retrieval

#### Scenario: Read after committed draft replacement

- **WHEN** a draft replacement has committed and an authenticated caller retrieves that tournament
- **THEN** the response reflects the replacement's persisted configuration, version and audit values
- **AND** retrieving it again causes no further changes
