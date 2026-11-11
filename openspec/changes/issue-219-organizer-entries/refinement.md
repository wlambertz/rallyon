# Refinement: organizer-managed tournament entries

## Problem and evidence

The accepted [#213 findings](../issue-213-registration-research/findings.md) distinguish an entry request, accepted draw roster, player identity and doubles pairing. Existing roster IDs and registration-policy enums do not provide an entry workflow. Whole-draft replacement can change roster configuration under authentication-only access; the dedicated registration and participant operations remain stubs. The Bommern example supports email and online intake and states that both doubles players must enter online, but it does not establish a confirmation mechanism or a universal rule.

An organizer needs to record a received entry, know its disposition, correct it and withdraw it without confusing these actions with publishing an announcement or altering a draw roster. No organizer interview or measured demand has been obtained; this is a scenario hypothesis to validate with the maintainer and, when available, an actual organizer.

## Primary persona and owning app/context

Primary persona: a club tournament organizer handling entries received outside RallyOn. Candidate owning context: Tournament Management. Player Management remains a placeholder, and external identity ownership is unresolved. The organizer app has no selected frontend stack. Device/connectivity assumptions from the personas remain hypotheses.

## Scope and exclusions

Propose a bounded backend capability for an authorized organizer to record, inspect, correct and withdraw entries for singles and doubles in a selected tournament discipline/bracket, with explicit entry disposition and intake opening/closure. Resolve whether acceptance/rejection is necessary in this first slice. Distinguish entry identity from player identity, doubles membership and roster membership. Decide the identity namespace and provenance, authority, minimal states, duplicate/capacity handling and concurrency semantics before implementation.

Use creator-only management as the initial authorization candidate, following the announcement precedent without assuming that a broad draft editor or caller-supplied organizer ID proves ownership. Treat supplied identity information as organizer-recorded unless separately verified; do not equate a Keycloak user ID with an external player ID. No automatic synchronization into the existing draw roster is assumed; its boundary must be explicitly decided at G2.

Exclude public self-registration, player accounts/onboarding, club delegation, invitation tokens, a Player Management service, federation integration, payments, automatic notifications, frontend work, and automatic legal/compliance enforcement. Preserve current REST/JWT behavior; assess any migration or new API as part of the specification. Existing draft authorization is a separate contract, not implicitly fixed here.

## Success criteria

- Validate a concrete organizer-handled scenario and explicitly decide actor authority, identity/provenance, singles/doubles membership and minimal entry states.
- Specify observable success and rejection scenarios for recording, correction, withdrawal and intake closure, including duplicates, capacity policy and concurrent edits.
- Ensure an entry cannot silently authorize another player, publish private data or become a confirmed draw participant without an explicit approved rule.
- Demonstrate the approved behavior with domain, persistence, security and API tests and local organizer UAT; assess compatibility and keep identity, entry, roster and announcement concerns distinct.
- Escalate migrations, shared IAM changes, dependencies, modules or public-contract breaks before implementation and document material architecture decisions.

## Assumptions and agreed budget

The first slice assumes organizer-managed intake of externally received requests; it does not assert that all clubs use the same workflow. Exact fields, status names, identity keys, authorizations, correction/withdrawal policy, capacity behavior and read/audit boundaries remain for G2. No timebox or numerical budget has been agreed.

Track: **full** (`rallyon-feature`), because this proposes a new behavior with identity, security and persistence implications. G1 approves the scope, not an implementation, schema, new issues, deployment or merge.

## G1 decision

Pending explicit approval of this scope; record the actual quote and source through the lifecycle helper.
