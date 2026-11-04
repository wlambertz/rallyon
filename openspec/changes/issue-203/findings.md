# Findings: group standings for a selected competition

## Answer and recommendation

Use the 40th German individual championships O35–O80, 7–9 May 2027, as a **research case**, not an agreed RallyOn target. The DBV names Grün Weiß Mülheim as host ([2026 association report, p. 176](https://www.badminton.de/fileadmin/dateien_upload/dateien_2026/mai_2026/berichtsheft_62._ovt_2026_essen_2.korrektur_haushalt_r.pdf#page=176)). The DBV is the competition authority. Its [2026/27 rules, dated 13 June 2026](https://www.badminton.de/fileadmin/dateien_upload/dateien_2026/juli_2026/dbv_satzungswerk_2026-27_stand_13.06.2026.pdf) are the candidate edition: implementing provisions § 1(1),(5), printed p. 204, make DBV organizer and apply Anlagen III and IV to German individual championships. Anlage IV Teil 2 § 10, printed p. 235, specifies pool evaluation. This is a **conditional** application: an event-specific announcement, pool format and any overriding provisions or subsequent amendments have not been verified. Do not declare this version supported for the actual event until they are checked with the authority. The [DBV edition page](https://www.badminton.de/der-dbv/verbandsfuehrung/) identifies the edition and update date; applicability at the event date remains to be confirmed.

Recommend a future separately approved feature that binds a competition to an identified, immutable rule version, accepts validated result revisions and explicit decisions, and reports provisional or unresolved standings rather than silently applying another version. Confirm the competition and exception rules before committing to a calculator. This investigation changes no runtime.

## Source-traced findings

| Claim | Source | Status |
| --- | --- | --- |
| Pool winner criteria are match wins, set difference, point difference, direct comparison of two players, then lots, in that order. | DBV rules, Anlage IV Teil 2 § 10, printed p. 235 | Verified as written; event applicability conditional |
| Section 10 speaks of a pool winner and a direct comparison of two; handling a residual tie of three, a partial ranking and a missing head-to-head needs authoritative clarification. | Same § 10 | Open interpretation; no invented algorithm |
| DBV's youth ranking regulation § 30(2), printed p. 299, removes all results of an entrant unable to play every group match and has a special two-player comparison. Its surrounding section concerns youth ranking tournaments, not the selected championships. | Same PDF, Jugendspielordnung Anlage I §§ 20–30 | Verified distinction; do not transfer the rule |
| The secondary BWF summary says head-to-head precedes set difference for two tied entrants. Its source and applicability are unverified; it must not override the DBV sequence. Draw and seeding remain with #185. | [#185](https://github.com/wlambertz/rallyon/issues/185), section 5; DBV § 10 | Discrepancy verified between texts, not a verified BWF rule |
| `TieBreakRules` exposes a type and three flags, not this ordered, versioned sequence. Configuration exposes and persists those flags; no rule version is encoded there. | `service/tournamentmgmt/src/main/kotlin/dev/wlambertz/rallyon/tournamentmgmt/setup/rules/api/TieBreakRules.kt`, `setup/configuration/api/Tournament.kt`, `setup/configuration/internal/tournament/persistence/mapping/TournamentMapper.kt` | Verified current representation; not proof of a complete standings engine |
| Scoring is a placeholder; the context map proposes Tournament Management supplying rules and fixtures to Scoring and Scoring supplying results and standings to Public Information. Transport and ownership are not settled. | `docs/architecture/context-map.md`; `service/scoring` | Verified proposed architecture, not implemented integration |

## Reviewable examples and candidate contract

These are hypothetical inputs and proposed safe product behavior, not event results or an assertion that the event actually uses pools. Set and point differences are won minus lost over eligible matches. Eligibility and valid completion require an authority decision where exceptions apply.

1. **Selected version / equal wins:** A has two wins and set difference +3; B has two wins and +1; B beat A. If § 10 applies and the aggregates are valid, A precedes B on sets, without reaching the direct comparison. This follows § 10; the totals are illustrative, not a complete fixture dataset.
2. **Unsupported version:** an organizer selects an unknown edition. Return “unsupported/unresolved”, identify the requested edition, and do not label the DBV example as its result. This is a proposed safeguard, not DBV text.
3. **Incomplete:** one scheduled match has no final result. A provisional view may show recorded results, but must not assert a final winner. Whether a walkover, retirement or administrative decision completes a fixture and its numerical contribution remains unverified.
4. **Unresolved and lot:** A, B and C have equal wins, set and point differences; the source does not explain how to apply a two-player direct comparison to this three-way tie. Mark it unresolved pending authoritative interpretation. Hypothetically, if the authority confirms that a lot decides these positions and records B, A, C as its outcome, display that order with the decision and its provenance; without the outcome leave it unresolved. This illustrates a proposed contract, not a verified lot procedure. Never generate an undisclosed random ordering.
5. **Withdrawal:** A withdraws after beating B, before playing C. Do not silently remove A's match or award C a score: event-specific withdrawal treatment is unverified. Preserve the recorded result and expose the unresolved effect until an authorized decision supplies the applicable treatment.
6. **Correction:** an authorized correction changes a recorded set score and reverses a set-difference comparison. Recalculate from the corrected revision, retain provenance of the earlier input and any lot decision, and flag any downstream published or advancing result for review. This is a proposed workflow, not a verified DBV correction procedure.

The candidates refine [STANDINGS-001 and STANDINGS-002](../requirements-reconciliation/specs/group-standings/spec.md): identify the selected version, never substitute one, apply its ordered criteria to eligible results, and preserve unresolved status and repeatability. They are research candidates, not accepted requirements. A future contract needs group membership and fixtures, match status and winner, per-set scores, eligibility and authorized decisions, rule identity/version, result revisions and recorded lot outcomes. It must define exactly when each input is sufficient.

## Ownership, prerequisites and compatibility

Proposed boundary: Tournament Management owns competition configuration, selected policy and fixtures; Scoring owns authoritative result revisions, eligibility decisions and derived standings; Public Information consumes a published view. This is a recommendation subject to an architecture decision, not an existing service contract. Clarify who records referee decisions and whether policy interpretation belongs with configuration or Scoring. Result capture, fixture identity, correction authority and a published cross-context contract precede implementation. UI work depends on the organizer stack decision.

New rule identities, results and audit records may require persistence and an append-only Flyway migration; introducing a Scoring module/service or dependency and changing public DTOs/APIs require separate review. Existing nullable `tieBreakRules` fields and stored flags cannot safely imply the new version; legacy data needs an explicit compatibility/migration decision. Preserve current REST, JWT and CLI contracts until separately authorized. No migration or API change is decided by this research.

## Open questions and unverified claims

- Obtain the actual 2027 announcement, format, relevant overrides and any later DBV amendments; confirm § 10 applies to the particular pool and edition.
- Confirm three-way ties, lower placements, lot procedure, incomplete matches, walkovers, retirements, withdrawals and corrections with the responsible body.
- Confirm scoring-system validity on the event date. The [DBV announcement of 25 June 2026](https://www.badminton.de/news/badminton/neue-zaehlweise-dbv-praesidium-beschliesst-einfuehrung-von-3-x-15/) announces a 2027 transition; don't infer match scores from an older format.
- Confirm whether this competition is a product priority and whether a new Scoring context is warranted. No maintainer budget was agreed.

## Proposed follow-ups

After authority confirmation and acceptance, consider a separately scoped feature for result capture and versioned standings. No issue payload is proposed while the event rules and ownership remain unresolved.

## G5 independent verification

Pending a fresh-context checker; file existence does not pass a gate.
