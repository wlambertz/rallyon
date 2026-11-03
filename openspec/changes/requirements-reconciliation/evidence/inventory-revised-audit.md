# Independent bounded inventory audit after G5 revision

- Reviewer: native agent `/root/inventory_revised_audit`, independent of the registered makers.
- Outcome: **FAIL for substantive task 1.4 readiness**. The three immediately preceding blockers were addressed, but the current source-to-ID map contains incorrect references and does not yet establish the requested one-feature international deferrals. This is not a lifecycle gate record or an implementation, UAT or merge approval.
- Inputs: live [issue #183](https://github.com/wlambertz/rallyon/issues/183), including scope, acceptance criteria and the full six-section reference; `evidence/g5-final-review.md`; current topic inventory, both statement-level traces, dispositions, analysis and the separate open-questions and unverified-rules document. I also directly inspected `Venue.kt`, `RegistrationPolicy.kt`, `ParticipantsRoster.kt` and the four service operations cited below. I did not independently verify the applicability of every cited DBV rule, search all implementation names, assess privacy law or run project checks.
- Reviewed SHA-256 hashes:
  - `inventory.md`: `f7ee5ca7796cfff86c6c3a9e10218876df2b2d69144fbb302df77bd900c918ec`
  - `trace-sections-1-3.md`: `ddfb03378f13c9c1bb310673e7aa9939f90e1b4c510e1b9006b0010bd480a3b0`
  - `trace-sections-4-6.md`: `578f1b6a722ee7bf51d12fedd165597a9b5df6f566be7a02ef2144d936af5fef`
  - `dispositions.md`: `b2ee2baa265df02a7eff7780cc3098bee82ae819615dc3a50227932b0e3d6341`
  - `open-questions-and-unverified-rules.md`: `abe5514a1a5d024ee93e4a627ff0105675ca75ec7fb15e87589e42f6dbcc083f`
  - `analysis.md`: `971b9468189cff327c8e6bfc4b2b7cc54af723e199a1c58b11550c487951d20b`

## Prior G5 findings

1. **I8 and I9: addressed for the specific previously reported claims.** Grade 2 fee is I8 and Grade 3 continental fee is I62; the 120-day sanction is I9, the 15-day referral I63 and force-majeure relief I64. The §1 trace maps these specific source claims to those distinct IDs.
2. **Separate lists: addressed structurally.** `open-questions-and-unverified-rules.md` now has separate product/architecture questions and unverified-rule sections, with N-ID references and qualified DBV source references. The lists explicitly distinguish unverified conditions from adopted requirements.
3. **Honest task status: addressed.** Task 1.4 is unchecked and the current documents distinguish topic-level provisional `G/P`, source-level justified skips, narrow confirmed findings and a fresh-review requirement. This audit does not treat an unchecked box as an automatic failure; the mapping findings below are substantive.

## Findings still requiring correction

1. **The Major Events three-match rule is mapped to the wrong international item in the topic inventory.** `inventory.md` N4-17 says the three-match spacing is I27, while the canonical list defines I27 as the nine prescribed match sequences and I61 as the separate three-match spacing. The detailed S4-006 trace correctly uses I61. The source-location checklist's §4 row likewise names I27 without I61. This creates contradictory traceability for a rule the earlier audit specifically required to be separated.
2. **The BWF export interface and daily reporting are collapsed under an insufficiently defined ID.** The reference §2 asks for an encrypted export interface covering live results, match reports and entry lists to BWF servers. `trace-sections-1-3.md` maps that source statement to I30. The canonical list defines I30 only as daily BWF results submission, which is a separately timed §6 process. The generic national interface is separately skipped under N2-03; the BWF-only interface itself needs an accurate unique disposition or an explicit defensible feature boundary that covers both statements without losing the different inputs and timing. Do not assert that the separate national interface exists.

Both are source-trace findings: they do not imply that either international rule should be adopted or that national analogues are out of scope.

## Other acceptance observations and limits

- The national statement-level traces are substantially more detailed: source items are marked as skipped with reasons when applicable authority, implementation or software relevance is not established. The issue explicitly allows a reasoned skip; such a skip is not a confirmed Gap. The broad G/P rows are expressly a provisional topic index, while `dispositions.md` provides a narrower reviewed table and persona/effort/context hypotheses. I did not convert unresolved statements into false definitive classifications.
- Direct code inspection supports the limited representations of venue fields, `INVITE_ONLY` and roster IDs, and confirms that `publish`, `openRegistration`, `closeRegistration` and `lockConfiguration` throw `UnsupportedOperationException`. These facts do not establish the full publication, invitation, entry or qualification workflows. Their associated proposed priorities are estimates, not maintainer acceptance of implementation.
- `dispositions.md` identifies #203 as the accepted analysis follow-up for the standings candidate, and states other candidate clusters have not been accepted. I did not independently fetch #203 for this bounded audit. Whether the remotely linked local OpenSpec artifacts are durably delivered remains a delivery concern, not evidence of implemented behavior.
- The source traces and canonical I-list are greatly improved, but this review did not prove that every conceivable subcondition has a unique ID. After the two concrete mappings above are repaired, run a fresh source-to-ID check against the new hashes before claiming complete reconciliation. Do not reuse this fail report as a later pass.
