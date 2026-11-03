# Candidate scenarios for issue 183

These are review proposals, not accepted OpenSpec requirements. They do not authorize implementation or determine the legal rule set for an event. Their purpose is to make the open product and architecture choices in [inventory.md](inventory.md) concrete. Any selected capability needs its own approved scope, authority, examples and OpenSpec change.

## C1: preserve the competition's selected rules for standings

**Problem:** The reference assumes one ranking hierarchy, but the DBV examples in the inventory differ by competition. `TieBreakRules` has flags and preset types, not an ordered calculation contract.

**Candidate outcomes:** An organizer can identify the rule set applicable to a competition; a standings calculation uses that selected rule set and the recorded eligible results; the display identifies the rule set and its version. A correction or withdrawal has a traceable effect on derived standings without silently erasing the underlying record. Exact calculation and correction behavior remain to be specified for the selected competition.

**Examples to review:**

- Given an event explicitly using the DBV Spielordnung Anlage IV pool provision, when equal match wins require a tie-break, the selected order considers set difference and point difference before direct comparison, subject to the provision's applicability.
- Given an event explicitly using the DBV Jugendspielordnung Anlage I provision, when a participant cannot complete the group stage, the effect on ranking follows that provision and is distinguishable from deletion of the original match history.
- Given no selected or applicable rule set, the system reports that standings cannot be determined under an established rule rather than silently choosing an unrelated hierarchy.

**Decisions before a real spec:** event types and applicable rules; handling of residual ties and incomplete results; whether correction history is needed; source of participants and fixtures; and ownership between Tournament Management and the currently empty Scoring service. Validate examples against the exact regulation and effective date.

## C2: configure and publish essential tournament information

**Problem:** Venue and registration windows are stored, but no tested prospectus or publication gate has been established. The source's BWF checklist cannot be applied universally to club events.

**Candidate outcomes:** An organizer can prepare a reviewable event announcement using existing configuration and explicitly supplied contact and entry information. Missing information is identified according to the chosen event's stated requirements. The announcement identifies its state and the data version from which it was prepared.

**Examples to review:**

- Given an incomplete draft, when the organizer reviews an announcement, missing data is shown without preventing the draft itself from being saved.
- Given a selected competition with a verified required field, when that field is missing, publication reports the specific missing field instead of presenting the announcement as complete.
- Given a club event without a verified federation rule requiring accommodation information, absence of hotel details does not by itself block publication.

**Decisions before a real spec:** target event and actual mandatory fields; publication audience and access control; draft-to-published lifecycle and corrections; output format; and source authority for each mandatory condition. `TournamentStatus.PUBLISHED` alone does not establish a publication transition.

## C3: distinguish doubles pairs from league teams

**Problem:** Current draft roster validation uses team IDs for doubles. The source also requests national and regional team leagues; these are not established by the existing pair representation.

**Candidate outcomes:** Before adding a league capability, document separate meanings for a doubles pair, club team, roster, team tie and season; identify ownership and relationships to player identities and individual matches. A hypothetical result in one tie must be attributable to its competition and team participants without assuming an existing team service.

**Decisions before a real spec:** whether league events are a near-term audience, the applicable competition rules, aggregate and service boundaries, and compatibility with existing roster IDs. This requires architecture review; it does not prescribe a migration or a new module.
