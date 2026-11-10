# Findings: organizer tournament publication

## Recommendation

A bounded publication and correction feature is worth refining, but is not ready to implement: first choose the intended audience and publication channel, permission model, announcement data and correction semantics. Use the Bommern announcement below as a concrete research case, not as an approved product target or a universal template. The [draft follow-up](issue-drafts.json) requests a separately gated feature lifecycle, not immediate production changes. Registration remains separate in [#213](https://github.com/wlambertz/rallyon/issues/213); reuse relevant work from [#186](https://github.com/wlambertz/rallyon/issues/186), which is still open, rather than treating its unverified international claims as settled national requirements.

## Case and evidence

On 24–25 October 2026, TuS Bommern's [announcement](https://tus-bommern.de/images/2026/badminton/Ausschreibung_achte_Wittener_Stadtmeisterschaften_2026.pdf) describes the 8th open Wittener Stadtmeisterschaften: a named organizer and venue, discipline start times, classes and eligibility, provisional group/knockout format, deadline, registration channels, fee, balls and prizes. It welcomes youth players, reserves the right to change individual details and says exact start times follow by email and on Turnier.de. The [club's dated notice](https://tus-bommern.de/news/badminton/einladung-zur-jahreshauptversammlung-der-badmintonabteilung-2027.html) also displays the event details, but not the PDF's change reservation. The notice and PDF document a published case, not an interview, actual correction or evidence of the organizers' software preferences. The PDF's privacy and liability statements are not adopted as legally valid or as product requirements.

The Badminton NRW [2026/27 download index](https://www.badminton.nrw/service/download-center-blank) identifies the following current competition-order annexes. [Annex 9, sections 1.3 and 2](https://www.badminton.nrw/fileadmin/Dateien/Broschueren-Hefte/Satzung-Ordnungen/2026/03_TO_Anlage_9.pdf) says other O19 private tournaments are normally deemed approved without application, but directs O19 tournaments permitting youth players to annex 10; it permits federation-site publication. [Annex 10, sections 1.1–1.2 and 4.1](https://www.badminton.nrw/fileadmin/Dateien/Broschueren-Hefte/Satzung-Ordnungen/2026/03_TO_Anlage_10.pdf) describes an application and submission of the announcement for a non-JWS private tournament with U19 eligibility and lists organizer/host, date/place, disciplines/classes, participant group, deadline, address, fees, shuttle type, schedule, tournament committee and other information as announcement content. This is a conditional reading based on the case's invitation to youth: the actual approval, classification, authority's interpretation, timing and any further requirements have not been confirmed with the club or federation. JWS-specific provisions are not assumed to apply.

The association's [private-tournaments page](https://www.badminton.nrw/erleben/bei-privatturnieren/privatturniere) instead describes approval before publication generally and requests entry at badmintonfreunde.de after approval. Its broad wording and annex 9's ordinary O19 default differ; page update date and precise reconciliation are unknown. Do not encode either as a universal software rule. No checked source here establishes a correction procedure, legal consent wording, a machine-readable federation interface, or a general German field list.

## Current RallyOn behavior and gap

Paths below are relative to `service/tournamentmgmt/src/main/kotlin/dev/wlambertz/rallyon/tournamentmgmt/setup/configuration/`.

| Evidence | Established behavior or limit |
| --- | --- |
| `api/Tournament.kt`, `api/Venue.kt`, `api/DisciplineConfig.kt` | API representation includes name, description, schedule, registration windows, venue/address and discipline data. These are inputs, not evidence of an announcement that meets annex 10 or of a public workflow. A single description may contain prose but no structured fee, ball or committee fields are established by this representation. |
| `web/ConfigurationController.kt`, `internal/ConfigurationServiceImpl.kt` | Draft creation, draft replacement and retrieval delegate to implemented use cases. `POST /{tournamentId}/publish` requires `If-Match` but delegates to `publish`, which throws `UnsupportedOperationException`; `findPublic` also throws. A route or `PUBLISHED` enum is not an operational publication workflow. |
| `openspec/specs/tournament-draft-update/spec.md` and `openspec/specs/tournament-configuration-read/spec.md` | Draft replacement uses version conflict handling; authenticated configuration retrieval is explicitly authentication-only, including private configurations belonging to another creator. It is not an anonymous publication contract or an authorization design for publishing. |
| `docs/architecture/context-map.md` | Tournament Management is the candidate owner; Public Information is proposed and not started. No ownership or transport decision for publication is established. |

The sources do not prove that no alternate publication exists outside the inspected configuration service. Existing API fields, controller paths and optimistic versioning should be considered for compatibility; the design must not expose the full configuration or participant rosters merely because an authenticated configuration GET exists.

## Candidate scenarios and decision points

These are hypotheses for refinement, not accepted requirements or findings from organizer interviews.

1. An authorized organizer prepares an announcement with the event, eligibility and contact details, sees missing information applicable to the selected event, and deliberately publishes an approved version to a chosen audience. Whether approval is recorded or checked externally remains open.
2. A prospective entrant can find the intended publication and see its current details and whether a schedule is provisional. Public access, format, channel and freshness guarantees remain open; posting to another service or sending email is not assumed.
3. After publication, an authorized organizer corrects a class or time and can tell which version is current. Determine whether review, visible change history, notifications, deadlines or external redistribution are needed; none are established by the example's change reservation.
4. Unauthorized or stale edits and incomplete information need an explicit outcome. Specify ownership, identities, sensitive data, validation and concurrent edits before choosing endpoints or persistence.

The chosen case suggests a meaningful distinction between an internal state transition, an announcement artifact and delivery to an audience. Possible implementations include a publishable snapshot with a limited read model or a generated artifact, but neither is selected. A later feature must decide public API compatibility, authorization, data minimization, migration and context boundaries; any such change is subject to its own lifecycle and escalation. The organizer UI remains blocked on its stack decision. No live federation or Turnier.de integration is assumed.

## Limitations and open questions

- Confirm the event's actual category, permit, applicable rule edition and details with the club or authority; this study has not done so. Verify whether section 4.1 applies as interpreted and resolve the federation page discrepancy.
- Validate organizer needs, corrections and audience with users. The example supports plausible needs, not measured demand or a selected product market.
- Decide what constitutes publication, which fields are structured versus prose, visibility and retention, who can publish/correct, and how readers learn of corrections. Resolve privacy and legal review before adopting consent language or exposing contact information.
- Confirm overlap and dependencies with #186 and #213 when those studies produce results. No requirement, new endpoint, migration, module or third-party integration is approved here.
