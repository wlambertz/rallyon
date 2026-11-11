# G7 — Correctness and security review

**Verdict: PASS.** Independent checker `field_review_correct`, 2026-11-11. No blocking finding. Reviewed PF-001–007/design/ADR 0007 against the staged participant-field implementation, V8 and test evidence. No maker files changed or gate registered.

| Severity | Location                                       | Finding / action                                  |
| -------- | ---------------------------------------------- | ------------------------------------------------- |
| None     | Participant-field slice and additive migration | No correction required for the approved behavior. |

## Assessment

- **Authority/privacy:** controller actor identity comes from the existing principal provider and must be positive. The use case reads the stored creator before private book/field loads; existing draft replacement preserves that creator. New routes remain authenticated under the unchanged security chain, and the public announcement matcher cannot match them. Field data never enters the broadly readable legacy Tournament DTO. Scoped errors are static/sanitized and application responses request no-store; no added payload logging was found.
- **Strict request binding:** the actual PUT boundary now parses a JSON tree and checks object/string/array shapes plus integral signed-Long tokens before constructing the unchanged service DTO. Fractional/exponent/quoted/overflow values cannot silently select another entry or source revision. Required missing/null members are rejected. The initial G5 defect and actual endpoint delta execution are preserved; no global/legacy mapper relaxation or API-to-web dependency was introduced.
- **Selection/provenance:** use-case validation requires distinct positive tournament-local IDs, current source versions and a current target. Domain checks require ACCEPTED state, matching discipline/bracket/team size, valid single/pair members and unique selected keys. Canonical entry-ID ordering preserves pair-member order. Current target labels and original entry labels/members remain separate; no global/team identity is fabricated. Empty/one-unit selections and manual capacity/closed-intake behavior match the explicit scope.
- **Transaction boundary:** reads and commands establish REQUIRES_NEW/REPEATABLE_READ before authority/source reads. Book, current target, field units and audit therefore belong to a consistent source snapshot. The field row lock, lazy initialization and final expected-version update serialize competing field versions; initialization/content/audit roll back together. Serialization/deadlock/uniqueness errors are translated after the transaction exits, without retrying against newer user inputs.
- **Source changes:** no entry/configuration source row is mutated or frozen by field preparation. The approved guarantee is snapshot-time currentness, including possible immediate outdatedness after a concurrent source commit. Observed versions are returned; subsequent source-version inequality reports OUTDATED without rewriting saved content/version/audit. Removed brackets remain readable because snapshots do not depend on mutable bracket rows. Different fields have separate versions and no promised cross-field reservation.
- **Storage/compatibility:** V8 is additive, with tournament/field/unit ownership keys and case-sensitive member uniqueness. Latest full content is replaced atomically; minimal audit retains actor/time/count/source revisions without historical member payload. No existing migration, JWT, IAM, roster, entry, announcement or CLI implementation changed. Latest-only retention and independent caller-rollback semantics are explicit ADR decisions, not hidden exceptions.

## Evidence and checks

- Inspected raw staged service/ADR diff and complete new production files, V8, relevant MockMvc and PostgreSQL test phases; checked existing security matcher and creator-preserving draft mapping.
- `git diff --cached --check`: passed. SHA-256 of staged `service/tournamentmgmt` plus ADR 0007 diff: `b7c82cd393d6362bd51df01deaab74abec4659e448631894e3cc1f41b40f2936`.
- Independently inspected logs: latest maker full verify **149 tests, zero failures/errors, three unrelated optional skips**; G6 **66 tests, zero failures/errors/skips** against fresh PostgreSQL databases; G5 delta **15 actual MockMvc tests, zero failures/errors/skips**. No blanket Maven rerun for this source-inspection lens.
- Forced PostgreSQL source-overlap tests pause after a real source read, commit changes elsewhere, then verify the coherent earlier view and subsequent OUTDATED/new revision. Writer races compare the complete persisted winner; they do not force every possible SQL interleaving.

## Limits

Real Keycloak HTTP-to-database UAT remains pending. MockMvc mocks service/JWT decoding, and PostgreSQL tests call the real service/database. Exact correction and acceptance-after-preparation cases are composed from inspected entry version behavior plus field equality rather than separate forced field-integration phases, as G6 discloses. No load/throughput, permanent currentness, verified identity, capacity compliance or historical reconstruction guarantee is established. Separately staged #185 research was outside this runtime correctness review.
