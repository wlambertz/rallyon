# Concrete event investigation: German Masters U11 2026

This is a research example for #183 and #203, not a product-target decision. The [DBV competition calendar](https://turniere.badminton.de/) lists German Masters U11 in Hofheim on 5–6 December 2026, with singles and doubles. The DBV's [2026 event allocation](https://www.badminton.de/news/badminton/turniervergabe-nachwuchsveranstaltungen-2026/) identifies a U11 Masters Finale on the same dates. This establishes a concrete scheduled event, not proof of the event-specific announcement, its final organizer, its execution, or any modifications to its rules.

## Rule sources and limitations

The [DBV 2026/27 regulations](https://www.badminton.de/fileadmin/dateien_upload/dateien_2026/juli_2026/dbv_satzungswerk_2026-27_stand_13.06.2026.pdf), Jugendspielordnung Anlage I § 13(3)–(4), PDF page 296, describe the U11 Masters singles and doubles preliminary rounds as three-player groups, with group winners advancing. Section 30, PDF page 301, describes group ranking criteria and treatment where an entrant cannot complete all group games. This is a different competition from the A-RLT U11 singles research example, whose § 11 specifies four-player preliminary groups.

The event-specific announcement and any valid modifications were not located in this search. It is therefore not yet established that this particular event actually used precisely that format or that § 30 applied without modification. The previous generic DBV pool example in the approved G2 specification remains a distinct provision. A verified link between this event and its binding announcement is needed before making it an implementation target.

## Worked ranking examples for review

Assuming the event uses § 30 without modifications:

1. In a completed three-player group A defeats B, B defeats C and C defeats A. Each has one match win; § 30(1) next compares set difference and, if necessary, point difference. The checked excerpt does not settle every residual tie; mark such a case unresolved pending authoritative clarification.
2. If C cannot complete its group games, § 30(2) excludes C's games from the group-ranking evaluation. The original recorded results are not necessarily deleted: the source states a calculation treatment, not a software retention requirement.
3. With only A and B remaining after the above exclusion and their head-to-head match completed, they cannot be tied in wins in a normal decisive badminton match. Thus the special two-way direct-comparison exception must not be demonstrated using impossible totals for this three-player case. A larger or otherwise incomplete group may need a separate legally valid fixture example.
4. If a score is later corrected, the authority, correction procedure, input eligibility and effect on an official ranking must be confirmed separately. Recalculation and historical traceability are proposed design concerns, not claimed obligations established by this source.

The context map's scoring ownership is proposed, not implemented. Result ownership, rule-version selection, public/API compatibility, possible migration and module boundaries remain open and require an architecture decision before implementation. This investigation does not change the approved spec or authorize runtime work.
