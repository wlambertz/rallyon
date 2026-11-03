# Open questions and unverified rules for issue 183

This file separates product/architecture decisions from assertions about governing rules. The source-level disposition and precise skip reason for each statement remain in `trace-sections-1-3.md` and `trace-sections-4-6.md`; IDs refer to `inventory.md`. The source is an unverified generated document. A topic appearing here is not an accepted rule or an implementation commitment.

## Unresolved product and architecture questions

1. **Target and personas:** Which concrete club or national event should RallyOn serve first, and which organizer problems have been observed? The youth example is a research case, not an agreed target. N1-01, N1-04–06, N2-01.
2. **Competition modeling:** Are age class, event classification, invitation policy, discipline, doubles pair, league team, tie and season separate concepts? Who owns each? N1-01–02, N1-05–06, N2-01, N3-02.
3. **Registration and identity:** Who can enter, confirm, correct or lock a registration; what participant identity is authoritative; and what must invitation-only actually enforce? Existing roster IDs and policy enum do not settle the workflow. N1-02, N1-15, N2-04, N2-06–08, N2-11.
4. **Prospectus and venue:** Which fields are mandatory for a selected event, who can publish and correct it, and which venue restrictions are information rather than automated enforcement? N1-03, N1-07–14, N1-18–25.
5. **Draw and standings:** Which format, result source, ordered rule version, lot process and correction model apply; how are an incomplete group and unresolved ranking represented? N3-01–18, N6-01, N6-03–10, N6-13–14. Follow-up #203 owns further standings refinement; #185 overlaps draw research.
6. **Context and contracts:** Which context owns confirmed results and standings, how are corrected data propagated, and are a new module, integration, API or migration needed? The [ADR stub](adr-stub-standings-ownership.md) records alternatives rather than a decision.
7. **Operations and officials:** Which calls, timers, court constraints, incidents, cards and confirmations should the system record, versus leave to officials or venue procedures? N4-01–30, N5-02–06; overlaps #184 and #186.
8. **External exchange and nonfunctional requirements:** Does a supported national API exist, with which data, agreements, security, error handling and freshness? What auditability and availability objectives are measurable? N2-02–03, N6-04–05, N6-11–12, Q-01–02.
9. **Privacy and finance:** Which participant data, legal basis, retention, payment and fee flows apply to a selected workflow? BWF rights assignment and sanctions do not answer this question for national events. N2-05, N1-12, N5-01; legal specifics remain for appropriate review.

## Unverified source rules and applicability

The following statements have **not** been adopted. They need a primary source applicable to the selected competition and its effective date; where a DBV comparison exists, it is explicitly bounded rather than a universal finding.

| Source claim or group | IDs | Verification still needed |
| --- | --- | --- |
| National eligibility for junior and senior age groups | N2-01, N2-09–10 | The DBV season-cutoff provision is cited in `inventory.md`, but specific event and regional exceptions are not established. |
| Seed limits, draw placement, association separation, group schedules and advancement | N3-01–18 | DBV bye and seed examples are bounded to cited provisions; the source's thresholds, World Senior placement and generic association-separation analogue are not validated for the chosen event. |
| Withdrawal, promotion, redraw and group-result treatment | N3-07, N3-16, N6-03, N6-09–10 | Verify each selected competition's state changes, unfinished match, withdrawal and correction treatment. The DBV U11 examples are conditional, not proof of the event announcement. |
| Group ranking and tie transitions | N6-01, N6-06–08, N6-13–14 | The source's immediate head-to-head sequence differs from the cited DBV pool provision; which rules apply to a real target event remains open. |
| Rest, calls, no-show option and warm-up | N4-02–04, N4-10–17, N1-18, N1-22–25 | The cited DBV 30-minute entitlement and exclusion provision are conditional; source 60/30-minute, call stages and two-minute shuttle-test values are not established nationally. |
| Venue obstructions, coaching, equipment, devices, medical events, clothing and officiating | N4-05–07, N4-09, N4-18–30 | Verify the applicable current national, regional or venue authority separately; distinguish informational/human procedure from any required software behavior. |
| Card consequences and referee reporting | N5-02–06 | Verify yellow/red/black-card consequences, team implications and report deadline for the selected event. |
| Prospectus fields, ball supply, fees, prize and national reporting | N1-07–14, N1-20–21, N2-03, N6-11–12 | The BWF prospectus, payment and export claims do not establish national obligations; inspect the actual competition announcement and supported exchange mechanism. |
| Scoring transition | N6-02 | `scoring-transition.md` cites a DBV announcement with specific event/date conditions; association rules and any event-specific early adoption remain to be confirmed. |

International-only assertions are listed once as deferred in I1–I65 in `inventory.md`; generic analogues above remain separately in scope. The full reference may contain further atomic subclaims within an ID, so a fresh independent source-by-source review is needed before claiming complete traceability.
