# Findings

## Answer and recommendation

Keep the guide as a provenance-labelled secondary reference, not a rule set or accepted specification. The [inventory](inventory.md) records its claims, repeats and verification status. The source combines potentially different authorities, events and editions, and some absolute statements do not match the primary documents examined. The relevant competition, date, applicable rules and event briefing were not identified; no particular rule is established for that unknown event. Do not hardcode its timers, equipment, sanctions or coach limit.

RallyOn has configuration for scoring and scheduling policies, but the inspected tracked implementation and current specs do not establish the proposed live match-day operations. A gap is an observation, not acceptance of a feature. Research with organizers and officials should establish value and responsibility before proposing requirements. Existing #183, #185, #186, #228 and #230/#231 cover adjacent research or work; avoid duplicate issues. At present, the event and workflow uncertainty makes a distinct issue premature, so no issue payload is proposed. A future feature lifecycle should assess calls, result recording or other justified needs separately, with event-specific authority and human decision rights.

## Sources and scope

- [Supplied German guide](../../../docs/wiki/References/Badminton-Regel-Kompendium.md), verbatim from [#184](https://github.com/wlambertz/rallyon/issues/184); secondary, mixed authority, undated and unverified as a whole.
- [BWF Laws of Badminton, section 4.1, version 5.0 (2), in force 26 April 2025](<https://extranet.bwf.sport/docs/document-system/81/1466/1470/Section%204.1%20-%20Laws%20of%20Badminton%20-%2026%20April%202025%20V5.0%20(2)%20.pdf>), especially 7, 9.1.2, 13.3, 16.2, 16.5 and 16.7. This comparison is limited to that edition and ruleset, not every event or a future edition.
- [BWF Instructions to Technical Officials (ITTO), section 4.1.1, version 8.0, in force 17 November 2025](https://badminton.dk/wp-content/uploads/2025/11/Section-4.1.1-Instructions-to-Technical-Officials-ITTO-17-November-2025-V8.0.pdf), BWF document hosted by Badminton Denmark. Clauses 3.5.17, 5.1.4, 5.2.2.1, 5.4.13, 5.6.7, 5.6.11, 5.7, 5.12–5.14 are scoped to this version and its officials.
- [BWF General Competition Regulations (GCR), section 5.1, version 5.0, in force 28 April 2024](https://afbv.fr/sites/default/files/club/images/Documents/Section%205.1%20-%20General%20Competition%20Regulations%20-%2028%20April%202024%20V5.0.pdf), clause 13.7; a historical BWF document hosted by a club. Its current validity and the unknown event's applicability are not established.
- [Badminton NRW referee briefing, Westdeutsche Meisterschaften U22, 2024](https://www.badminton.nrw/fileadmin/Dateien/Spielbetrieb/O19/Meisterschaft/2023_24/Info_Referee_U22_2024.pdf), headings Aufruf zum Spiel, Coaching and medizinische Betreuung. It is a comparison for a different event, not the unidentified source briefing. [NRW's 2025 U19 notice](https://www.badminton.nrw/news-detailansicht?cHash=42aa7a77ab6eb656b4b91cc06d87c9ca&tx_news_pi1%5Baction%5D=detail&tx_news_pi1%5Bnews%5D=4705) links a referee briefing, but the [briefing PDF](https://www.badminton.nrw/fileadmin/Dateien/Spielbetrieb/U19/Meisterschaft/2025/20251108_WDMU19_BriefingReferee.pdf) was inaccessible during research; its content is not assumed.
- [Badminton NRW regulations download centre](https://www.badminton.nrw/service/download-center-blank) lists separately versioned regulations. Neither the guide's exact NRW briefing nor its Hövelhof hall rules were identified. DBV and event-specific applicability remain unverified.
- The [requirements reconciliation](../requirements-reconciliation/inventory.md), [draw and seeding research issue #185](https://github.com/wlambertz/rallyon/issues/185), [player-rest research issue #228](https://github.com/wlambertz/rallyon/issues/228), current `openspec/specs/` and tracked Tournament Management code inform overlap and product coverage, not external rule validity.

## Source-traced findings

| IDs | Evidence and conclusion |
| --- | --- |
| M2-01–05 | GCR 2024 §13.7 permits the referee to declare a no-show and award a walkover when an eligible player/pair fails to arrive in time. It does not establish the guide's 5+2+5 sequence, automatic disqualification or amounts. The different 2024 NRW U22 briefing calls the referee after the second call and allows disqualification five minutes later; it does not establish the first-call or two-minute timings. These examples cannot settle an unidentified competition. |
| M2-06–08, M5-02 | ITTO 2025 §5.2.2.1 describes a normal two-minute warm-up from umpire taking the chair until “Play”, with a ready call at 90 seconds and subject to referee instructions. This does not establish a universal timer for unofficiated matches or the source's exact shuttle model and provider. |
| M2-10–11 | Laws 2025 §13.3.3 and 13.3.5 treat ceiling and outside-object contact as faults, with a local obstruction bylaw proviso. The guide's alleged Hövelhof exceptions remain unverified and may depend on valid local rules. |
| M3-01–03 | Laws 2025 §16.2 allow at most 60 seconds when the leading score reaches 11, except handicap matches, and at most 120 seconds between games; referee-mandated fixed intervals can apply to televised matches. ITTO procedures and the scoring system matter before assuming an interval can always be shortened. |
| M3-05–07 | In ITTO 2025, §3.5.17 concerns scheduling/rest, not the guide's coach limit. §§5.4.13, 5.6.7 and 5.6.11 limit accredited coaches joining each side on court during specified intervals to two; that is not proof of a universal seating limit. The different NRW U22 2024 briefing also permits two people during specified intervals. Neither verifies the guide's one-coach NRW assertion or its unspecified event. |
| M3-08–10, M5-05 | ITTO 2025 §5.7 describes umpire recording/reporting duties; it does not establish that teammates operate an unofficiated scoreboard or jointly confirm a scoresheet with players. The exact local briefing is unresolved. |
| M1-11–12, M3-12 | ITTO 2025 §5.1.4 says player devices must be switched off **or silent**, while §5.12 addresses coaches' devices. The blanket immediate red-card claim is not established by Laws 2025 §16.7, which distinguishes warnings and faults including flagrant cases; officials decide the circumstances. |
| M3-14, M5-12 | Laws 2025 §16.5.1 restricts advice to when the shuttle is not in play and before players take service/receive positions. Law 9.1.2 addresses undue delay after the backswing; the guide's citation of 9.2 alone is imprecise. |
| M4-02–03, M4-06 | ITTO 2025 §5.14.1 addresses treatment causing undue delay, not a blanket diagnosis-only rule. The 2024 NRW U22 briefing permits an ice-spray administration in stated circumstances; this is a counterexample to a universal ban, not permission for another event. Laws §16.7 allow warnings or faults according to circumstances rather than proving every delay gets an immediate identical sanction. Medical decisions belong to officials and qualified professionals. |

The remaining inventory entries remain explicitly unverified or informational. Failure to find a source is not proof that a claim is false. Exact fine amounts, clothing, equipment, incident procedures and event-specific references require the relevant governing texts before operational use.

## Reconciliation with current product

`Gap` means absent from the inspected tracked current specs and Tournament Management main code; it does not assert a product requirement or exclude uninspected systems. The untracked `PlayerSchedulingAdvisory` work belongs to another change and is not treated as merged behavior. `Not applicable` means no basis for assigning the human rule to software, not that the rule itself is invalid.

| IDs | Current evidence | Disposition |
| --- | --- | --- |
| M2-01–04 | `setup/configuration/api/SchedulingPolicy.kt` defines `MAX_PARALLEL_MATCHES` and `ROUND_PACING`; `setup/rules/api/MatchDurationPolicy.kt` defines `FIXED_TIMEBOX` and `BEST_OF_N_GAMES`. `Tournament.kt` stores nullable policy fields. None establishes calling, arrival, notifications or no-show decisions. | Gap in inspected behavior. Role, recorded events, exceptions and authorization need research; officials decide sanctions. |
| M2-06–07, M5-02 | The policy enums and `openspec/specs/tournament-draft-update/spec.md` describe configuration, not court warm-up execution. | Gap; no verified universal timer or shuttle-test workflow. |
| M3-01–03 | `setup/rules/api/ScoringRules.kt` defines points, games, win-by-two and cap for 21-, 15-point and custom presets. `RulePresetTest.kt` and `RuleMappingTest.kt` check presets/mapping; they do not establish live score or interval execution. | Gap in live interval behavior; presets are partial configuration, not interval coverage. |
| M3-05–06 | The inspected tournament configuration and `openspec/specs/tournament-configuration-read/spec.md` expose configuration, not an event coach-limit workflow. | Gap if an organizer need is established; no accepted default or automated enforcement. |
| M3-08, M3-10, M5-05 | Inspected tracked Tournament Management main code and current specs did not reveal scoreboard-operator assignment or scoresheet confirmation. | Gap; determine whether a role, attestation or result correction is useful and who owns it. |
| M2-05, M1-01, M1-02–10, M1-13, M2-08–12, M3-04, M3-07, M3-09, M3-11–14, M4-01–08, M5-01, M5-03–04, M5-06–12 | Human guidance, physical equipment, venue and officiating or international sanctions; no established software need from this source alone. | Not applicable to a proposed software behavior at this stage; preserve source/uncertainty, reconsider individual recording needs separately. |

A future design may require an ADR if it introduces a new cross-context responsibility, authoritative event rules or officials' decision workflow. No ADR decision is made here. Organizer UI stack remains undecided; `service/scoring`, `service/playermgmt` and `application/audience` are placeholders, not evidence of implemented operations.

## Open decisions and limitations

Identify competition, jurisdiction, event date, exact primary editions, regional briefing and venue rules. Confirm which operational pain points exist with organizers and officials; establish actor authority, result correction and audit needs, scoring variants, timing exceptions and accessibility. No verified source in this research establishes the claimed universal one-coach limit, exact call sequence, Hövelhof exceptions, scoreboard workflow, fine amounts or shuttle brand. Verify further claims before specifying them, and distinguish event-specific policy from Laws. Findings do not prescribe medical or sanction decisions.

## Proposed follow-ups

No issue creation is proposed at this stage. Coordinate with #183, #185, #186 and #228 before drafting distinct scoped issues; #230/#231 have separate scheduling-advisory work. If research establishes a distinct need, prepare a separate feature lifecycle and exact issue payload for human approval after acceptance of these findings.

## G5 independent verification

Pending a fresh-context independent checker and current deterministic checks.
