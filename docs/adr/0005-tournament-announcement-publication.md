# 0005 — Tournament announcement publication boundary

## Status

Accepted by the maintainer for [issue #215](https://github.com/wlambertz/rallyon/issues/215) and implementation issue [#217](https://github.com/wlambertz/rallyon/issues/217). Actual quote: **“ja”**, in response to the presented ADR acceptance question. Requirements and HTTP behavior are in the [OpenSpec change](../../openspec/changes/issue-215-announcement-publication/design.md).

## Context

An organizer needs a publicly readable announcement that can be corrected or withdrawn. The existing tournament draft replacement is authentication-only; another authenticated user can change draft fields and visibility. Publishing those fields automatically or deriving publication state from draft visibility would let a different caller affect public content. The current `publish` service method is unimplemented and the wide configuration DTO includes data unsuitable for anonymous readers.

## Decision

- Treat the announcement as an independently versioned consistency boundary owned by `setup.configuration` in Tournament Management. It refers to one existing tournament by ID; one tournament has at most one announcement. The creator is the tournament's stored `createdByUserId`, checked against the authenticated positive user ID for each management operation. This is deliberately creator-only, not an organization-membership model.
- The creator explicitly supplies title, time range, venue and plain-text body. Do not import draft content automatically. A publication snapshot contains only these fields, its state, version and timestamps. Withdrawal retains the snapshot as withdrawn; a later complete, explicitly authorized replacement republishes it. The creator's authenticated management read exposes the current version and state, including when withdrawn.
- Publication state is independent of tournament draft visibility. An announcement remains public when the tournament becomes private until the creator withdraws it. A draft update cannot publish or republish an announcement. The endpoint documentation and UAT must make this distinction conspicuous; the existing draft authorization behavior is unchanged and remains a separate concern.
- Use one PostgreSQL row per tournament, with a primary or unique tournament key, a foreign key to the existing tournament, a state and a non-null optimistic version. An append-only Flyway migration adds storage without creating announcements for existing tournaments. Verify the next migration number against the current branch; never rewrite shipped migrations. The exact physical names remain implementation details.
- Initial creation is transactional: check the stored creator and attempt insertion; the database uniqueness constraint decides competing creates, and the losing transaction becomes a conflict. Each correction, withdrawal or republication performs an atomic compare-and-set against the expected announcement version in a single transaction. A zero-row update or optimistic-lock failure is a conflict; only a committed winner advances the version. Return the committed new version, not the prior JPA version. No partial snapshot or state is visible. Test concurrent requests with PostgreSQL, not just mocks or H2.
- Keep business invariants and ports framework-free; adapters implement persistence and HTTP translation. The transaction covers creator lookup, announcement mutation and flush/constraint handling, with exceptions translated after rollback as appropriate. Concurrent tournament deletion, if supported later, must not leave an orphan; the foreign key enforces referential integrity. No cross-context event or new module is introduced.
- Provide one public GET returning only the allowlisted snapshot as readable UTF-8 `text/plain`, with `Cache-Control: no-store` and `X-Content-Type-Options: nosniff`. Construct text as text, not HTML. A separate authenticated management read and all mutations remain creator-only. Match the anonymous exception narrowly by method and path; the shared starter's path-only permit list is insufficient to establish this constraint. Retain issuer, JWKS, audience, claim and role validation; do not modify shared IAM. Test sibling paths, other methods, malformed or invalid credentials and all existing protected routes.

## Alternatives considered

- **Publish the existing draft or derive state from its visibility:** rejected because the current draft update contract does not enforce creator ownership, and the draft contains fields not intended for public disclosure.
- **Restrict all existing draft updates to the creator:** deferred because it changes an established authentication-only contract and requires a separate compatibility and authorization decision.
- **Public Information service, generated PDF or external platform:** deferred until an actual product need and context/transport decision are established. A simple plain-text link supports a first reader without choosing an organizer frontend stack.

## Consequences and validation

The design separates deliberate public content from the existing draft and supports concurrent corrections, at the cost of duplicate input and an independent visibility control. Users must explicitly withdraw announcements when desired; copies already downloaded by readers cannot be recalled. A public link using a tournament ID is not secret. The content is organizer-supplied and does not assert compliance with any federation's rules.

Validate field and state invariants, unauthorized callers, stale and concurrent mutations, visibility independence, literal rendering, response headers, protected routes and data minimization. Run the service's Maven `clean verify`, formatting checks and PostgreSQL migration/concurrency checks, recording actual commands and environment. Keep old API/JWT contracts and Flyway history intact. Future change to aggregate ownership, authentication or publication delivery requires its own decision.
