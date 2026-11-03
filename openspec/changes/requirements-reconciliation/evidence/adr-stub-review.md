# Independent review: standings ownership ADR stub

- Reviewer: native agent `/root/adr_review`, independent of the makers.
- Outcome: **PASS for adequacy as an unresolved ADR stub only**. No option, target competition, result-access policy, implementation scope, G5 or merge is approved by this review.
- Reviewed artifact: `adr-stub-standings-ownership.md`, SHA-256 `f64dda3bf2e1f5fec76ced09d8e46aa0260b7396bacaeaeb2b5f49252c2b1165`.
- Context checked: `docs/architecture/context-map.md`, `docs/clean-architecture-and-clean-code.md`, `service/tournamentmgmt/AGENTS.md`, the candidate `design.md` and `specs/group-standings/spec.md`, existing configuration-read and Keycloak integration specifications, and `setup/rules/api/TieBreakRules.kt`.

## Findings

The stub accurately distinguishes the existing configuration and rule types from an implemented standings service. The checked `TieBreakRules` defines a preset/type and flags, but not an ordered standings algorithm. The context map describes Scoring and its cross-context contracts as proposed, while `service/scoring` remains a placeholder. The stub correctly leaves event versus synchronous integration undecided.

The three options are distinguishable and state practical tradeoffs: a separate Scoring context, a bounded internal slice in the existing service, or deferral. The first two require an explicit decision about ownership and implementation boundaries. A named API or port does not alone prove separation: any future choice must enforce Modulith exposure, avoid importing another module's internal packages or persistence, and keep domain/application dependencies directed inward.

The stub is consistent with STANDINGS-001–002: it calls for an identified rule version and states that incomplete results or ties need an explicit outcome. It does not choose an actual event or imply that a DBV example universally applies. Its acceptance checklist properly defers an architecture decision and independent review until a real competition, governing source and use cases are selected.

## Matters to resolve before any decision or implementation

- **Authority and concurrency:** decide who may enter, confirm, correct and publish results; how simultaneous corrections are ordered; what version or concurrency conflict is reported; and what constitutes an authoritative result. Current configuration retrieval's authentication-only policy does not establish an appropriate write policy for a future scoring capability.
- **Security and privacy:** establish organizer, participant, spectator and machine access separately for reads and writes; preserve current issuer, audience, role mapping and `rallyon_user_id` validation. Define what personal data and result history are necessary. These are open design questions, not a finding that existing authentication must change.
- **Consistency across boundaries:** specify the versioned relationship among competition, fixtures, eligible results, selected rules and any lot decision; failure/retry/idempotency and stale or corrected data behavior depend on the eventual transport. Avoid direct reads of another context's tables or implementation packages.
- **Compatibility and delivery:** inspect downstream consumers and existing REST/DTO behavior; decide whether new contracts, a module, persistence, append-only Flyway migration, dependencies and any operational deployment are needed. Obtain applicable human review before those changes. Preserve legacy REST, JWT and CLI contracts unless a break is separately authorized.
- **Validation:** trace an accepted competition's success, incomplete-input, withdrawal, tie, correction, role and unauthorized scenarios to independent tests; run the service/IAM verification and integration checks applicable to the chosen design. No tests or runtime validation were run for this documentation review.

The comparison establishes the stub's usefulness as a decision record starter, not the completion of the approved task requiring a reviewed ownership decision. A fresh review is required after selecting or materially changing an option.
