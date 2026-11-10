# Findings: national requirements reconciliation

## Answer and recommendation

RallyOn has useful configuration inputs, but the reference describes much more than the behavior demonstrated by those inputs. Prioritize a bounded organizer publication scenario and an entry/registration scenario for further refinement; use the completed standings research before deciding on a separate feature. Choose a real competition and applicable rule edition before adopting any numerical rule. Persona value and effort below are hypotheses, not a committed roadmap.

The detailed statement-level source trace is in [sections 1–3](trace-sections-1-3.md) and [sections 4–6](../requirements-reconciliation/trace-sections-4-6.md), with stable S row IDs, German topic names and English glosses, N/I/Q topic IDs, concrete code evidence where established, explicit reasons for skipped claims, and international deferrals indexed in the [inventory](../requirements-reconciliation/inventory.md). These are prior maker-produced artifacts, not a completed independent coverage audit. The narrow classifications and priorities in [dispositions](../requirements-reconciliation/dispositions.md), and the [open questions](../requirements-reconciliation/open-questions-and-unverified-rules.md), are part of the evidence. Where prior work says #203 is an open or pending follow-up, the current status and archived result below supersede it. Do not treat provisional G/P inventory labels as proven absence or partial implementation of a whole workflow.

## Source-traced findings

| Claim | Evidence | Status and limit |
| --- | --- | --- |
| A venue's name and address, registration windows, roster IDs and an `INVITE_ONLY` policy value can be represented. | `setup/configuration/api/Venue.kt`, `Tournament.kt`, `ParticipantsRoster.kt`, `RegistrationPolicy.kt`, under `service/tournamentmgmt/src/main/kotlin/dev/wlambertz/rallyon/tournamentmgmt/`; `openspec/specs/tournament-configuration-read/spec.md` | Verified representation; not evidence of a prospectus, self-registration or invitation workflow. N1-02–03, N1-08, N1-10, N2-04. |
| The existing configuration service operations for publication, opening/closing registration and locking configuration throw `UnsupportedOperationException`. | `setup/configuration/internal/ConfigurationServiceImpl.kt`, same source root, methods `publish`, `openRegistration`, `closeRegistration`, `lockConfiguration` | Verified gaps in these named operations; no inference that every possible alternate workflow is absent. N1-07, N1-15, N2-07. |
| `Category` means singles, doubles and mixed, not a national championship, league or age category. A doubles team ID alone does not establish a league squad or season. | `setup/configuration/api/Category.kt`, `TeamSize.kt`, `ParticipantsRoster.kt`; `setup/configuration/internal/tournament/domain/DraftReplacement.kt`, same source root | Verified distinction; different meanings are not by themselves a conflict. N1-01, N1-05, N2-01. |
| The reference's group-ranking sequence and seed-count table differ from specific DBV provisions. | [DBV 2026/27 rules](https://www.badminton.de/fileadmin/dateien_upload/dateien_2026/juli_2026/dbv_satzungswerk_2026-27_stand_13.06.2026.pdf), Spielordnung Anlage IV Teil 2 §§ 3 and 10, PDF pages 232 and 237; [inventory comparisons](../requirements-reconciliation/inventory.md#primary-source-cross-check-age-and-group-standings) | Verified discrepancy between texts for the cited provisions; the applicable competition and exceptions determine which provision applies. N3-03, N6-01. |
| DBV announced a 3 × 15 standard from 4 January 2027 for DBV-level individual competitions, with possible earlier adoption subject to announcement. | [DBV announcement, 25 June 2026](https://www.badminton.de/news/badminton/neue-zaehlweise-dbv-praesidium-beschliesst-einfuehrung-von-3-x-15/) | Verified announcement; do not generalize to all regional, club or team competitions. N6-02. |
| #203 has completed research on group standings, using the 2027 German O35–O80 individual championships as a research case. | [Archived #203 findings](../archive/2026-11-10-issue-203/findings.md) and [acceptance review](../archive/2026-11-10-issue-203/uat.md); [issue #203](https://github.com/wlambertz/rallyon/issues/203) | Completed and accepted research; the actual event announcement, pool format, overrides and several exception procedures remain unverified. The case is not an approved product target or implementation. |

The [DBV rules index](https://www.badminton.de/der-dbv/satzung-ordnungen-und-spielregeln/) describes annual editions. Primary-source comparisons in the inventory also address age classification, byes, breaks and withdrawal; they establish only the cited texts and their conditional applicability. No unverified reference rule is adopted here as a specification.

## Priorities and domain implications

| Candidate and trace | Recommendation | Organizer / Player / Spectator value; rough effort; context |
| --- | --- | --- |
| Publication and prospectus, N1-03, N1-07–14 | Adapt: refine a concrete club publication scenario and verify mandatory fields for its authority. | High / medium / medium; medium–high; Tournament Management. |
| Entries and invitations, N1-02, N1-15, N2-04, N2-07, N2-11 | Adapt: clarify registration states, participant identity and invite-only behavior before implementation. | High / high / low; high; Tournament Management and participant ownership undecided. |
| Group results and standings, N4-08, N6-01, N6-03–10 | Adapt: build on completed #203 research; verify the selected competition and resolve ownership before a feature. | High / high / high; high; Scoring ownership undecided. |
| Draw, phases and advancement, N3-01–18 | Adapt after selecting a format; reuse #185. | High / high / medium; high; Tournament Management. |
| Scheduling, calls and court allocation, N4-01–05, N4-10–17 | Defer implementation until fixtures and a verified operating scenario exist; coordinate #184. | High / high / medium; high; Tournament Management. |
| National championships, senior events, team leagues, N1-01, N1-04–06, N2-01 | Defer domain commitments until a target event is selected. Age class, competition classification, a doubles pair, league team, tie and season are distinct candidate concepts, not established requirements. | Medium / medium / low; high; Tournament Management and team/participant boundaries undecided. |
| Federation exchange, print, finance and officiating, N2-02–03, N4-06–09, N5-01–06, N6-04–05, N6-11–12 | Defer external integration until actual support and access are verified; consider printable output for an identified workflow; coordinate #184–#186. | Medium / medium / medium for exchange; high or unknown effort; ownership and interfaces undecided. |

## Open questions and unverified claims

The [separate register](../requirements-reconciliation/open-questions-and-unverified-rules.md) lists rule and product questions by ID. Key blockers are a selected event and governing authority, entry identity and permission model, result/standings ownership, the actual national API and its access terms, and applicable age, tie, withdrawal, rest and call rules. N2-12 in the section 2 trace records national anti-doping and integrity integration as a separate unverified analogue: authority, legal basis, access and actual product need are unresolved. Connectivity and persona value are hypotheses. International-only fees, deadlines, eligibility and sanction claims remain deferred; generic national counterparts are assessed separately. No claim of full absence follows from a missing search result.

## Proposed follow-ups

The accompanying exact draft payload proposes two bounded analysis issues for the confirmed publication and registration operation gaps. These are recommendations for review, not accepted feature work. #203 is completed; #184–#186 already cover overlapping rule-source research and must be reused. No separate issue is proposed for other provisional clusters until their product scope is established. A later implementation requires its own feature lifecycle, including any API, migration or module decision.

## G5 independent verification

Pending a fresh-context checker; file existence does not pass a gate.
