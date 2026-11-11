# Refinement: tournament announcement publication

## Problem and evidence

An organizer needs to make a tournament announcement available to prospective entrants and correct it when details change. The [#212 research](../issue-212-publication-research/findings.md) gives a real club announcement as an illustrative case, including provisional details and a reservation to change them. It does not establish organizer demand through interviews or select that event as RallyOn's product target.

In the current configuration service, draft creation, replacement and retrieval exist, while `publish` and `findPublic` throw `UnsupportedOperationException`. Existing authenticated configuration retrieval exposes a configuration contract and is not a public announcement contract. A status enum or draft fields alone do not provide a publication and correction workflow.

## Primary persona and owning app/context

Primary persona: a club tournament organizer preparing an announcement for prospective entrants. Candidate owner: Tournament Management, within the existing tournament management service. The context map proposes Public Information as a downstream context but it is not implemented; its role and boundaries require design. The organizer frontend is a placeholder, so this scope does not select or build a UI. Device, connectivity and time-saving assumptions are hypotheses, not observed user research.

## Scope and exclusions

Propose and, subject to later lifecycle approvals, deliver a bounded backend capability for an organizer to prepare, intentionally publish and correct an announcement for a club tournament, with a way for the intended audience to obtain the current published information. Define the announcement content and publication channel at specification time. Distinguish a draft, the published information and corrections; specify who can act, how stale edits are handled, and what readers see. Explore whether applicable event-specific requirements are verified rather than treating the Bommern case or NRW provisions as universal.

Include validation of successful and rejected publication and correction scenarios, data minimization, compatibility, persistence and ownership decisions. Use existing configuration data when appropriate without exposing participant rosters or the full configuration by default. A published artifact, read model and API are design candidates, not decisions made by this scope.

Exclude registration and invitations (#213), federation submission or integration, automatic notification, payments, match operations, a frontend stack or UI, and universal legal/compliance automation. Do not assume an external approval workflow, public anonymous access, new module, new dependency or a migration; surface these if the selected design needs them. Coordinate with #186 and #213 without assuming either is a completed prerequisite.

## Success criteria

- A selected organizer scenario, target audience, content and channel are explicitly justified, with assumptions and missing user or authority validation visible.
- The approved specification distinguishes draft and published information, intentional publication, correction, current reader view, authorization, invalid or incomplete input and stale edits through observable success and failure scenarios.
- Tests and a maintainer walkthrough demonstrate the approved behavior and ensure unrelated participant or private configuration data is not exposed.
- Existing REST, JWT and database contracts are assessed; any proposed break, migration, shared IAM change, new dependency or module is escalated before implementation. Existing shipped migrations remain append-only.

## Assumptions and agreed budget

The Bommern 2026 example is a research aid, not a chosen production target; actual organizer needs and rule applicability remain to be validated. The precise authorization and whether the published information is anonymous are open and may affect security. The choice of structured fields, free text, correction history, delivery channel, storage and version semantics remains open for G2. No timebox or numerical budget has been agreed.

Track: **full** (`rallyon-feature`), because this is a new capability with unresolved behavior and security implications, not a narrow documentation fix or research-only question. This refinement requests scope approval only; it does not approve a design, implementation, issue creation, deployment or merge.

## G1 decision

Pending explicit human approval; record the actual quote and source in lifecycle evidence.
