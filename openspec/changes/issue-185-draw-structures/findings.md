# Findings: entries, participant fields and tournament draws

## Answer and recommendation

Refine a **creator-prepared, versioned participant field for one bracket** before implementing a draw generator. Current accepted entries retain local player keys and explicit pair members; legacy rosters use numeric player/team references. Neither acceptance nor an unchecked conversion establishes a draw field. A deliberate field snapshot can bridge this gap while preserving identity provenance and private access. This is a product recommendation, not a sports-rule obligation or accepted behavior.

The supplied guide is useful as an indexed reference and a source of questions, not as one universal rule set. Preserve its [original text](source-reference.md), [108 statement IDs](inventory.md), [primary-source checks](primary-verification.md) and [code reconciliation](code-reconciliation.md) together. The official texts reveal qualifications and a faulty citation; no new behavior specification is written. Choosing a pure knockout generator immediately would assume both a product format and an identity bridge that have not been decided.

## Source-traced findings

| Claim | Source | Status |
| --- | --- | --- |
| Acceptance is independent of draw membership. Entries use unverified string keys and one/two explicit members; legacy rosters use positive numeric references. Automatic conversion would invent identity semantics. | ADR 0006; `EntryBookService.kt`, `EntryBook.kt`, `ParticipantsRoster.kt`; detailed [contract mapping](code-reconciliation.md#existing-contracts-that-constrain-a-follow-up) | Verified current representations; proposed bridge remains a hypothesis. |
| Format/seeding values do not constitute a draw engine; phases have explicit unsupported paths. | `TournamentFormat.kt`, `SeedingPolicy.kt`, `Phase.kt`, `ConfigurationServiceImpl.definePhases`, `DraftReplacement.validateDraftBasics`, mapper/tests; [mapping](code-reconciliation.md) | Verified in inspected source/spec surfaces, not a runtime or repository-wide absence claim. |
| The guide cannot establish a national seed policy, exact group-to-KO scheme or universal withdrawal cascade. | P1/P2 checks and inventory index in [primary verification](primary-verification.md) | Verified discrepancies/qualifications; unresolved placements remain unverified. |
| A documented regional competition illustrates participant-count-dependent format decisions rather than proving demand for pure knockout. | P3, [concrete-case assessment](primary-verification.md#p3-concrete-case-and-inference) | Organizer-published plan, not observed execution, interview evidence or agreed RallyOn target. |
| Group standings already have conditional research; phase advancement also needs fixtures/results and revision ownership. | [Completed #203 findings](../archive/2026-11-10-issue-203/findings.md), context map, phase/result placeholders | Verified prior research and inspected boundaries; candidate architecture is not an existing integration. |
| Existing authenticated draft reads/replacement are broader than entry access. Adding private entry snapshots to that DTO could expose them. | `tournament-configuration-read` GET-CONFIG-003; draft-update spec; EntryBook creator checks; [mapping](code-reconciliation.md) | Verified contract difference; follow-up must explicitly choose its private authority. |

## Candidate next workflow and boundaries

The following is a reviewable product alternative, not an adopted rule set:

1. The tournament creator reads the accepted entries for one selected bracket and explicitly chooses the playing units. A doubles unit retains both local player keys; neither member is replaced by a fabricated global/team ID.
2. Preparation checks current source revisions, accepted state, selection compatibility and duplicate membership, then retains a private participant-field snapshot with provenance and its own revision. Manual selection does not certify real-world eligibility or partner consent.
3. Later entry corrections, withdrawal or draft replacement do not silently rewrite that field. The next feature must choose explicit reconciliation/invalidation behavior and make stale inputs visible. A stale command or foreign actor must not overwrite or expose another field.
4. A later draw consumes a deliberate field revision. Format identity, supported sizes, seeds, byes, ordering/randomness, recorded lot outcomes, regeneration and publication are separate decisions. A stored seed alone is insufficient if input ordering or algorithm version can change; retain the resulting arrangement and relevant provenance.

Candidate owner: Tournament Management. Decide whether the new consistency boundary belongs within existing configuration or phases, and document the tradeoff in an ADR before implementation. Preserve pure domain/use-case ports, published API boundaries and existing REST/JWT/CLI contracts. New storage would need an additive migration, privacy/retention and rollback assessment; no such production change is authorized by this research. Do not create Player Management, Scoring, federation integration or frontend dependencies implicitly.

## Priority, value and alternatives

| Candidate | Recommendation and dependency | Persona value / rough effort |
| --- | --- | --- |
| Explicit private participant-field preparation | Smallest recommended follow-up; resolve local identity, revision and reconciliation contracts in a full feature lifecycle. | Organizer high, player medium, spectator low until publication; medium effort. |
| Pure knockout preview | Next possible slice after field/format/policy selection; no generator or pure-KO enum is assumed. | Organizer/player plausibly high; medium–high effort including policy and reproducibility. |
| Groups then knockout | Defer until fixtures, result ownership, applicable standings and advancement decisions exist; reuse #203. | Potentially high for all personas; high effort. |
| Federation-ranked qualification/reserves, international sanctions and automatic seed movement | Defer without confirmed target competition, authority and integration support. | Unknown local benefit; high/unknown effort. |

These are qualitative hypotheses, not measured demand, a numerical priority score or schedule. No interviews were performed and no budget was agreed. P3 does not establish that a pure knockout format satisfies organizers' needs; confirm the selected workflow with the maintainer.

## Open questions and unverified claims

- Confirm the first actual competition/format and the organizer's desired selection process; no universal federation defaults follow from this research.
- Decide local playing-unit identity, per-bracket field ownership, manual capacity treatment, source revision checks and correction/withdrawal reconciliation. Determine whether multiple prepared revisions are retained and how future draws reference them.
- Decide privacy, authority, audit retention and how to avoid the broad legacy configuration read/update contract.
- Exact placement diagrams and World Senior group-to-KO schemes remain unverified; the cited GCR section does not establish them. Primary-document screenshots were unavailable, so no diagram was visually validated.
- International ranking, promotion and eligibility workflows remain deferred. Checklist rules are preserved and cross-referenced to #184/#186; they are not declared accurate or adopted. Numerical match-day rules require event/edition-specific verification.
- A later generator needs an explicit format/policy version, supported edge cases (empty/one/two units and exact powers of two), deterministic replay/lot provenance, publication and authorized regeneration behavior. Standings, walkovers and retirement must not be conflated with entry withdrawal.

## Proposed follow-ups

The [exact optional issue draft](issue-drafts.json) proposes one bounded feature refinement for participant-field preparation, linked to #185 and dependent on the already merged #219. Accepted-entry data and code contracts justify investigating that bridge; primary sports rules do not dictate its design. A separate full lifecycle must approve its scope/specification, ADR, migration and implementation. The issue is not yet created. No extra issue is proposed for #184/#186 overlap or unsettled draw algorithms.

## Validation and G5 independent verification

Maker documentation checks and a fresh independent claim/source and scope verification are required before human findings acceptance. No Maven/runtime tests are appropriate or claimed for this research-only change. No production code, tests, migrations, runtime configuration, dependencies, wiki pages or accepted specs changed. G5 remains pending until the independent checker reports and the helper records its outcome; file existence is not certification.
