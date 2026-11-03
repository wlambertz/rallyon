# Issue 183: national requirements reconciliation (work in progress)

This is an analysis, not an accepted implementation specification. The source is the unverified reference in [issue 183](https://github.com/wlambertz/rallyon/issues/183); its six sections are mapped in the two `trace-sections-*` files, with unresolved assertions explicitly skipped and justified. A fresh independent review must confirm completeness and classifications before task 1.4 is complete. Current project specifications are in `openspec/specs/`, not the historical `docs/wiki/specs/tournamentmgmt/` directory. `ro spec create` produces legacy YAML and is not the project specification workflow; see `docs/wiki/specs/README.md` and `docs/legacy-spec-migration.md`.

## Initial evidence

| ID and source | German term (English gloss) | Initial classification and evidence | Open work |
| --- | --- | --- | --- |
| N1-01 §1 | Turnierkategorie (tournament classification) | Gap: `setup/configuration/api/Category.kt` enumerates SINGLES, DOUBLES and MIXED; these are disciplines, not championship, league, senior or invitation classifications. This difference in meaning is not by itself a contradiction. | Decide whether any classification is useful for club organizers and whether age, competition structure and invitation policy should be separate concepts. |
| N1-02 §1 | Einladungsturnier (invitational event) | Partial model only: `setup/configuration/api/RegistrationPolicy.kt` has INVITE_ONLY; it does not establish an invitation workflow. | Examine registration implementation and the need for invitations, acceptance, expiry and access control. |
| N1-03 §1 | Spielstätte und Felder (venue and courts) | Partial model only: `setup/configuration/api/Venue.kt` contains name, address and capacity; `Court.kt` contains label, type and availability. No complete prospectus follows from those fields. | Check prospectus fields, publication behavior and hall restrictions against relevant national or regional rules. |
| N2-01 §2 | Altersklassen (age groups) | Gap pending full code search: `Category.kt` models disciplines, not age. The source's calendar-year eligibility claims are unverified for national events. | Verify applicable national and regional rules, effective dates and exceptions before proposing behavior. |
| N3-01 §3 | Setzung (seeding) | Partial model only: `setup/rules/api/SeedingPolicy.kt` offers MANUAL, RATING_BASED and RANDOM and explicitly leaves rating source and randomness open. | Verify whether national seed caps and placement rules match the source, and separate generic draw behavior from federation-specific policy. Overlap: #185. |
| N3-02 §3 | Phasen, Gruppen und K.o.-Runden (phases, groups and knockout rounds) | Partial model only: `setup/configuration/api/TournamentFormat.kt` names several formats; `setup/phases/api/Phase.kt` is an empty interface. The model does not establish a draw or progression algorithm. | Analyze group fixtures, advancement and redraw separately; overlap #185. |
| N4-01 §4 | Feldzuweisung und Zeitplanung (court allocation and scheduling) | Partial model only: `setup/rules/api/CourtAllocationPolicy.kt` and `setup/configuration/api/SchedulingPolicy.kt` are policy enums, not evidence of an operational scheduler. | Verify national rest rules and review manual overrides and club needs; don't adopt the source's fixed 60/30-minute values without verification. |
| N6-01 §6 | Gruppenrangliste und Gleichstand (group standings and tie-break) | Gap: `setup/rules/api/TieBreakRules.kt` stores preset flags but does not establish the reference's ordered standings algorithm. | Verify the applicable hierarchy and handling of withdrawals; decide ownership between Tournament Management and the Scoring placeholder. |
| N6-02 §6 | Zählweise (scoring format) | Partial model only: `setup/rules/api/ScoringRules.kt` includes 21-point and 15-point presets; this does not establish result calculation or automatic selection by competition/date. | Check governing authority and effective date per competition. The DBV announcement below gives one specific future effective date; do not generalize it. |

Paths above are relative to `service/tournamentmgmt/src/main/kotlin/dev/wlambertz/rallyon/tournamentmgmt/` unless otherwise stated. Partial model means a limited representation exists; it does not mark the full source requirement as covered.

## Existing behavior checked against implementation and tests

| Capability | Evidence | What it establishes and its limit |
| --- | --- | --- |
| Save and retrieve setup | `openspec/specs/tournament-draft-update/spec.md`, `openspec/specs/tournament-configuration-read/spec.md`; `DraftPersistenceIntegrationTest.kt` | An editable draft can contain registration windows, venue, courts, disciplines and rosters and the persisted configuration can be reloaded. This is configuration storage, not an entry service, draw or publication workflow. |
| Validate draft inputs | `setup/configuration/internal/tournament/domain/DraftReplacement.kt`, `DraftUseCaseTest.kt` | Draft replacement validates time windows, venue/court details, discipline/bracket identifiers and roster shape; nonempty phases are explicitly rejected. The `TournamentStatus.kt` enum alone does not establish transitions into PUBLISHED or IN_PROGRESS. |
| Represent a roster | `setup/configuration/api/ParticipantsRoster.kt`, `DisciplineConfig.kt`, `TeamSize.kt`; `DraftUseCaseTest.kt` | Player and team ID lists are mutually exclusive, and the draft validator matches a bracket roster to singles or doubles team size. Here a doubles pair uses a team ID; this does not prove a national team-league squad, tie or season model exists. |
| Preserve nested configuration | `DraftPersistenceIntegrationTest.kt` and `UpdateDraftServiceTest.kt` | Tests exercise saved registration windows, tournament and bracket rosters and clearing/replacement of nested values. These tests do not demonstrate participant self-registration, age eligibility, invitation acceptance or federation synchronization. |

Test paths in the table are under `service/tournamentmgmt/src/test/kotlin/dev/wlambertz/rallyon/tournamentmgmt/setup/configuration/`; they were inspected, not rerun during this documentation-only analysis.

## Primary-source lead

The DBV [rules and regulations index](https://www.badminton.de/der-dbv/satzung-ordnungen-und-spielregeln/) says its regulations are updated annually; select the correct edition and competition before relying on a clause. The DBV [announcement on 3 × 15 scoring](https://www.badminton.de/news/badminton/neue-zaehlweise-dbv-praesidium-beschliesst-einfuehrung-von-3-x-15/) states that DBV-level individual U19 and O19 competitions change on 4 January 2027. It does not establish a universal date for every regional or club event. The primary-source search and clause-by-clause checks are not complete.

## Next analysis steps

Finish the §1–§6 feature and international deferral inventory; inspect relevant tests and mappings; verify candidate rules against applicable primary national or regional sources; assess personas, effort and context; reconcile overlap with #184–#186; and prepare reviewable candidate specifications and follow-up issue drafts. The group-standings candidate has been approved for further analysis and follow-up #203 exists; no final implementation requirement has been accepted.
