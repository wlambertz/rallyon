# keycloak-auth-integration Specification

## Purpose

Describe shared Keycloak token validation and principal construction while preserving configured JWT claim names and role mappings. The default security chain requires authentication; authorization policies belong to consuming applications. Principal construction is performed by the real bearer-token authority converter and by endpoints that request an application principal. An already-authenticated request can reach a read endpoint without invoking the endpoint principal provider; a test-supplied Jwt authentication is not evidence of real bearer-token validation. The legacy ADMIN-only endpoint claim has no implemented counterpart; see [the migration inventory](../../../docs/legacy-spec-migration.md).

## Requirements

### Requirement: Validate bearer tokens

The shared starter SHALL use the configured issuer and JWKS or issuer discovery for JWT signature validation, the standard issuer and time validators, and the configured audience when nonblank.

#### Scenario: Reject invalid bearer token

- **WHEN** a request to a protected endpoint presents an expired, malformed, wrongly signed, wrong-issuer or wrong-required-audience bearer token
- **THEN** the resource server rejects authentication with 401 Unauthorized

#### Scenario: Require authentication

- **WHEN** a request without authentication targets an endpoint outside the configured permit-all patterns
- **THEN** the default security chain responds with 401 Unauthorized

### Requirement: Resolve the application principal

Principal construction SHALL require a nonblank subject, at least one mapped role and a numeric configured user ID claim, defaulting to `rallyon_user_id`.

#### Scenario: Resolve valid application identity

- **WHEN** claims include a subject, a mapped realm role and rallyon_user_id as a number or parseable integer string
- **THEN** the factory returns a principal with that user ID and the mapped authorities

#### Scenario: Reject missing or malformed identity

- **WHEN** the factory receives missing or nonnumeric user ID claims, a missing subject or no mapped roles
- **THEN** it throws KeycloakValidationException

#### Scenario: Map configured client roles

- **WHEN** resourceAccessClientId is configured and the token includes mapped roles for that client
- **THEN** the factory combines those roles with mapped realm roles

### Requirement: Keep default role mapping and authorization scope

The default mappings SHALL retain `rallyon-organizer`, `rallyon-participants`, `rallyon-audience` and `rallyon-service` as ROLE_ORGANIZER, ROLE_PARTICIPANTS, ROLE_AUDIENCE and ROLE_SERVICE respectively. The default filter chain SHALL require authentication for requests outside permit-all patterns.

#### Scenario: Authenticate a protected application operation

- **WHEN** a valid token satisfies token and principal validation and a protected endpoint is called
- **THEN** authentication supplies the application principal and operation-specific behavior determines the response, such as 201 for valid draft creation

#### Scenario: Retain authenticated-only default authorization

- **WHEN** an authenticated request reaches a protected endpoint through the default filter chain
- **THEN** the chain checks authentication without imposing an ADMIN or per-endpoint role requirement
