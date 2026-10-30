# Personas

> These personas describe RallyOn's three primary application audiences, named on [Home](Home.md), deepened with maintainer-supplied research notes from [issue #189](https://github.com/wlambertz/rallyon/issues/189), which keeps the full, unverified German source material for provenance. Rule-level specifics — exact call-time and disqualification windows, warm-up and rest durations, seeding and draw mechanics, fees, deadlines and GCR/ITTO citations — are intentionally left out here; they belong to [#184](https://github.com/wlambertz/rallyon/issues/184) (match-day rules), [#185](https://github.com/wlambertz/rallyon/issues/185) (draw/seeding) and [#186](https://github.com/wlambertz/rallyon/issues/186) (organizer compliance).

> **Architecture rule:** each app targets exactly one primary persona. A feature is owned by the persona it primarily serves and by one bounded context (see [the context map](../architecture/context-map.md)); other personas consume it through published APIs rather than duplicating business logic.
>
> **Working hypotheses:** device contexts and candidate metrics below guide refinement, but still need validation with organizers, players and spectators. The maintainer-supplied research above has its own provenance; these additions do not make its unverified details established requirements.

| Persona | Owning app | Keycloak role | Primary bounded contexts |
| --- | --- | --- | --- |
| Organizer | `application/organizer` | `rallyon-organizer` | Tournament Management, Scoring |
| Player | player app (no directory yet; design kit `ui_kits/player`) | `rallyon-participants` | Player Management, Public Info |
| Spectator | `application/audience` | `rallyon-audience` | Public Information |

`rallyon-service` is a machine identity, not a persona.

## Primary personas (application users)

These three map directly to the audiences RallyOn is built for.

### Organizer

Plans and runs a badminton tournament (Kaiserturnier, Swiss system, or knockout) end-to-end — everything non-technical and logistical. A separate on-court Referee owns technical/rules matters during play; the Organizer owns the prospectus, hall logistics, sponsoring, accommodation, federation communication and results.

- Wants automated match schedules, clear hall allocation, and integrated result entry; tooling to run entries/registration, produce draws and seeding, allocate courts dynamically, and publish live results; integrations with federation systems (e.g. nuLiga, turnier.de) to sync player data and results without manual re-entry.
- Pain points: schedule slippage from unpredictable long matches, late withdrawals that cascade into reseeding or promotion, keeping federation data exchange compliant, and getting tie-break scoring exactly right.
- Success: less manual effort, a smooth and on-time tournament day, transparent real-time information for everyone, and full compliance in seeding, draws and results.
- **Context of use (hypothesis):** laptop or tablet in a sports hall; unreliable Wi-Fi; interrupted frequently; time pressure on the day, calmer planning weeks before.
- **Metrics (candidate):** time from draft creation to publication; manual schedule edits per tournament; minutes of court idle time.

### Player

Takes part in a tournament, from check-in through to the prize ceremony.

- Wants current results, upcoming matches, and personal tournament history in real time; a clear, low-friction check-in and call process; and confidence that rest time, scheduling and officiating are fair.
- Pain points: the physical and mental load of multiple events (singles/doubles/mixed) with unpredictable waits between rounds; pressure to meet strict timing, equipment and dress requirements; and the risk of fees or ranking-point loss from late withdrawal or no-shows.
- Success: always knows where to be and when, plays as many matches as possible, and trusts the process to be fair, transparent, and well-officiated.
- **Context of use (hypothesis):** phone in the hall or on the way; glanceable, one-handed use; limited battery and data.
- **Metrics (candidate):** missed or late matches; support questions to organizers per player.

### Spectator

Follows a tournament live in the hall or online, from the designated public seating area — never from the field of play.

- Wants results, interim scores, and rankings on demand, clearly presented and current, via hall displays or a mobile device; enough insight into the format (scoring, draw type) to follow the standings; and clear hall etiquette so spectators and players don't get in each other's way (quiet during rallies, no flash photography, extra care at youth events).
- Pain points: losing track of a favourite match when many courts run in parallel without clear displays; schedule slippage cascading from long matches disrupting travel plans; and limited sightlines or noise from neighbouring courts.
- Success: can follow the action without asking anyone, and enjoys a fair, lively atmosphere.
- **Context of use (hypothesis):** phone or hall display; anonymous or lightly authenticated; many concurrent readers, read-only.
- **Metrics (candidate):** share of results viewed digitally versus asked for; concurrent viewers handled.

## Supporting stakeholders (not application users)

Facility Manager and Venue Administration don't use the RallyOn application — Home.md names exactly three audiences the product is built for, and no venue-booking feature area exists yet. They're captured here because their constraints (hall handover, capacity limits, noise/parking conditions, permit deadlines) shape what the Organizer coordinates on tournament day, and may matter if venue-side features are ever considered.

### Facility Manager (Hallenwart)

The on-site caretaker of the sports hall: ensures physical usability, enforces house rules, and is the primary contact for technical and building questions on tournament day. Interfaces directly with the Organizer, the club's setup crew, and the municipal facility administration.

- Responsibilities: opens and hands over the hall, referee rooms, changing rooms and sanitary facilities on time; briefs the Organizer on hall technology (lighting, ventilation, PA, scoreboards); provides and releases hall equipment; keeps emergency exits and first-aid areas accessible; handles technical faults during the event; does a joint walkthrough with the Organizer afterwards to check cleanliness, damage and equipment completeness; locks up.
- Pain points: unplanned hall overrun past the agreed closing time; litter and street-shoe dirt in competition areas; unauthorized use of hall areas or equipment; and poor upfront coordination on setup and ceremony timing.
- Success: a safe, damage-free hall with all technical systems working, and a clean, punctual handover.

### Venue Administration (Sportamt contact)

The municipal case worker who reviews, approves and manages the allocation of public sports venues to clubs and federations. Interfaces between club boards/Organizers, the Facility Manager, municipal politics, and oversight bodies (public order, fire safety).

- Responsibilities: processes hall-usage applications against the city's booking calendar; issues the official usage permit with court, stand and room allocation; sets occupancy limits and conditions on noise, parking, catering and youth protection; sets or waives usage fees; handles deposit refunds and damage claims afterwards.
- Pain points: incomplete or last-minute applications; resident complaints over noise or parking; occupancy limits being exceeded; and administrative burden resolving unclear damage liability after handover.
- Success: a legally sound, fair allocation of venue time, and a disruption-free event with no complaints or accidents.

## Open questions

- Is a Club/Association admin (teams, members across tournaments) in scope, or later?
- Do players need an account, or can registration be anonymous or link-based? (See the ADR drafts on passwordless login and social login.)
- Which device is the organizer's primary one on the day (laptop, tablet, phone)? This feeds the open organizer platform decision (`docs/adr/draft-organizer-platform-target.md`).
