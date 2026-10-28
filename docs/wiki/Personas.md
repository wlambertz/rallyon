# Personas

> **Status: working draft.** Goals come from [Home](Home.md). Pains, contexts of use and metrics are **hypotheses** written to drive scope and architecture decisions; validate them with real organizers, players and spectators and then remove this notice.
>
> **Rule:** each app targets exactly one primary persona. A feature is owned by the persona it primarily serves and by one bounded context (see `docs/architecture/context-map.md`); other personas consume it through published APIs, not by getting their own copy of the logic.

| Persona   | Owning app                                                | Keycloak role          | Primary bounded contexts          |
| --------- | --------------------------------------------------------- | ---------------------- | --------------------------------- |
| Organizer | `application/organizer`                                   | `rallyon-organizer`    | Tournament Management, Scoring    |
| Player    | player app (no directory yet; design kit `ui_kits/player`) | `rallyon-participants` | Player Management, Public Info    |
| Spectator | `application/audience`                                    | `rallyon-audience`     | Public Information                |

`rallyon-service` is a machine identity, not a persona.

## Organizer

Plans and runs a badminton tournament (Kaiserturnier, Swiss system, knockout, round robin) for a club or group, often as a volunteer alongside other duties.

- **Jobs to be done:** configure a tournament (disciplines, brackets, courts, rules), open and close registration, generate and adjust the schedule, run match days, enter or correct results, publish standings.
- **Context of use:** laptop or tablet in a sports hall; unreliable Wi-Fi; interrupted constantly; time pressure on the day, calm planning weeks before.
- **Pains (hypotheses):** manual spreadsheets and paper draws; re-planning when courts or players drop out; courts sitting idle between rounds; answering the same "when do I play?" question repeatedly.
- **Success:** less manual effort and a smooth tournament day.
- **Metrics (candidate):** time from "create tournament" to "published"; manual schedule edits per tournament; minutes of court idle time.
- **Referee / score-keeper** is treated as an Organizer sub-role (same app, restricted result-entry permissions) until evidence shows it needs its own app.

## Player

Takes part in one or more disciplines of a tournament, recreational to competitive.

- **Jobs to be done:** find and register for a tournament, know the next match (time, court, opponent), see results and standing, keep a personal tournament history.
- **Context of use:** phone, in the hall or on the way; glanceable, one-handed; battery and data are limited.
- **Pains (hypotheses):** missing a call-up; not knowing the schedule after a delay; not trusting the draw or seeding; no record of past results.
- **Success:** always knows where to be and when, and trusts the process to be fair and transparent.
- **Metrics (candidate):** missed or late matches; support questions to organizers per player.

## Spectator

Follows a tournament live in the hall, on a hall screen ("Turnier-TV") or online; often a friend, relative or club member of a participant.

- **Jobs to be done:** see live scores, upcoming matches and rankings without asking anyone; follow a specific player or team.
- **Context of use:** phone or hall display; anonymous or lightly authenticated; many concurrent readers, read-only.
- **Pains (hypotheses):** results only on a paper board; no way to follow someone remotely.
- **Success:** can follow the action without asking anyone.
- **Metrics (candidate):** share of results viewed digitally versus asked for; concurrent viewers handled.

## Open questions

- Is a Club/Association admin (teams, members across tournaments) in scope, or later?
- Do players need an account, or can registration be anonymous or link-based? (See the ADR drafts on passwordless login and social login.)
- Which device is the organizer's primary one on the day (laptop, tablet, phone)? This feeds the open organizer platform decision (`docs/adr/draft-organizer-platform-target.md`).
