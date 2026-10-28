# Ubiquitous language

One shared vocabulary for code, specs, UI copy and conversation. English terms are canonical in code and specs; German aliases are listed because the product pitch and the organizers use them. Terms are grouped by the context that **owns** the meaning (see `docs/architecture/context-map.md`). Status: **draft**; entries marked _tbc_ need confirmation from the maintainer.

Rules:

- Use the canonical term in class names, spec requirements and UI strings. Do not invent synonyms.
- A term has one meaning per context. If another context needs the concept, it refers to it by id or by a published-language type, and may name its own local view differently.
- Adding, renaming or splitting a term is part of a change's `design.md` and updates this file.

## Tournament Management

| Term | German alias (tbc where noted) | Meaning | Code |
| --- | --- | --- | --- |
| Tournament | Turnier | An event run by one organizer, with a lifecycle from draft to completed or canceled. | `Tournament`, `TournamentStatus` |
| Draft | Entwurf | A tournament still being configured; not visible to participants. | `TournamentStatus.DRAFT` |
| Organizer | Organisator | The person or club that runs a tournament; the primary persona of this context. | `Tournament.organizerId` |
| Discipline | Disziplin (tbc) | An event variant inside a tournament, such as Mixed Doubles; declares category, team size and brackets. | `DisciplineConfig` |
| Category | Kategorie (tbc) | Singles, doubles or mixed classification of a discipline. | `Category` |
| Team size | Teamgröße (tbc) | Number of players per side in a discipline. | `TeamSize` |
| Bracket | Teilnehmerfeld, Feld | A skill tier or open division within a discipline, with its own tournament format and capacity. | `BracketConfig`, `BracketId` |
| Tournament format | Turniermodus (tbc) | How matches are paired in a bracket: Kaiser, Swiss, KO with poule, KO A/B/C, round robin, king of the court, lottery, ranking. | `TournamentFormat` |
| Capacity | Kapazität (tbc) | Maximum number of participants or people, tournament-wide or per bracket or venue. | `Capacity` |
| Venue | Halle, Spielstätte (tbc) | The place where the tournament is held, with address and people capacity. | `Venue` |
| Court | Spielfeld, Court (tbc) | A single playing area in the venue with availability and type. | `Court` |
| Time window | Zeitfenster | A start–end interval, used for the schedule and registration periods. | `TimeWindow`, `TimeWindowRange` |
| Registration policy | Anmeldemodus (tbc) | Whether registration is open or invite-only. | `RegistrationPolicy` |
| Visibility | Sichtbarkeit | Who may see the tournament: private, organization, public. | `Visibility` |
| Roster | Teilnehmerliste (tbc) | The participants entered in a tournament or bracket. | `ParticipantsRoster` |
| Seeding policy | Setzung (tbc) | How participants are ranked for the draw: manual, rating-based or random. | `SeedingPolicy` |
| Phase | Phase | A stage of a tournament with its own pairing logic. Concrete types are not defined yet. | `Phase` |
| Scheduling policy | Spielplanregel (tbc) | How matches are laid out in time: maximum parallel matches or round pacing. | `SchedulingPolicy` |
| Court allocation | Hallenbelegung | Which match gets which court. | `CourtAllocationPolicy` |

## Scoring

| Term | German alias | Meaning | Code |
| --- | --- | --- | --- |
| Match | Spiel, Match | One contest between two sides. | not modelled yet |
| Game | Satz | One played set within a match, to a number of points, optionally win by two with a cap. | `ScoringRules.pointsPerGame` |
| Scoring rules | Wertungsregeln (tbc) | Points per game, games per match, win-by-two and cap. | `ScoringRules` |
| Tie-break | Tiebreak (tbc) | Rule to order participants with equal results: set difference, points ratio, Buchholz. | `TieBreakRules` |
| Standing | Rangliste | The ordered ranking of participants in a bracket. | not modelled yet |

## Player Management

| Term | German alias | Meaning | Code |
| --- | --- | --- | --- |
| Player | Spieler | A person who takes part in tournaments; the primary persona here. | not modelled yet (`ParticipantEntity` is a stand-in) |
| Participant | Teilnehmer | A player (or team) entered in a specific tournament bracket. | `ParticipantEntity` |
| Team | Team | One or two players competing together in a doubles or mixed discipline. | not modelled yet |

## Public Information

| Term       | German alias | Meaning                                                     | Code                    |
| ---------- | ------------ | ----------------------------------------------------------- | ----------------------- |
| Spectator  | Zuschauer    | A person following a tournament without taking part.        | role `rallyon-audience` |
| Turnier-TV | Turnier-TV   | A hall display showing live schedule, scores and standings. | not started             |

## Known ambiguities to resolve

- **"Feld"** is used for both _bracket_ (Teilnehmerfeld, per `service/tournamentmgmt/docs/discipline-bracket-model.md`) and, in everyday badminton German, a _court_. Keep **Bracket** and **Court** distinct in English; decide which German word is used for each in UI copy.
- **Kaiser vs king of the court**: `TournamentFormat` has both `KAISER` and `KING_OF_THE_COURT`. Clarify whether they are the same format or two, and name them once.
- **Participant vs player**: today a participant record doubles as the player. When Player Management is built, the tournament should reference a `PlayerId`, not own player data.
- **Capacity unit**: `PARTICIPANTS` versus `PEOPLE` differs for doubles and for spectators; the Venue rule already forces `PEOPLE`.
