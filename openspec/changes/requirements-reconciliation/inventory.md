# Issue 183: provisional source inventory

Status: topic index for the unverified reference under §1–§6 of [issue 183](https://github.com/wlambertz/rallyon/issues/183). Individual source statements and reasoned skips are in `trace-sections-1-3.md` and `trace-sections-4-6.md`; narrow confirmed findings are classified and prioritized in `dispositions.md`. Provisional topic labels: G = Gap candidate, subject to an absence search; P = Gap for the complete source capability, with only the stated partial representation covered by the cited code; D = international, deferred. These topic labels are not final classifications. Absence of demonstrated behavior is not proof of absence. Values and deadlines from the reference are claims, not requirements. Earlier independent audits found completeness not established; the revised snapshot requires fresh independent review.

| ID | § | Source item (English gloss) | Status | Follow-up |
| --- | --- | --- | --- | --- |
| N1-04 | 1 | Nationale Meisterschaften (national championships) | G | Decide whether championship classification or organizer-configured disciplines suffice. |
| N1-05 | 1 | Nationale und regionale Mannschaftsligen (national and regional team leagues) | G | Model teams, ties, league seasons and standings separately from individual events; proposed context requires a decision. |
| N1-06 | 1 | Seniorenturniere (senior events) | G | Age classes and eligibility require verified competition-specific rules; see N2-01. |
| N1-07 | 1 | Ausschreibung und Veröffentlichungssperre (prospectus and publication gate) | G | Prospectus generation, required fields and publication must be established separately; no generic GCR checklist is assumed. |
| N1-08 | 1 | Veranstalterkontakt und Hallenadresse (organizer contact and venue address) | P | Venue address exists (N1-03); contact and complete prospectus do not follow from it. |
| N1-09 | 1 | Hallenregeln und Einspielkapazität (venue restrictions and warm-up capacity) | G | Configurable venue information may be useful; example restrictions are not universal rules. Overlap #184 and #186. |
| N1-10 | 1 | Melde-, Auslosungs- und Ranglistenstichtage (entry, draw and ranking dates) | P | `setup/configuration/api/Tournament.kt` contains registration windows; it does not establish publication, draw or ranking deadlines. |
| N1-11 | 1 | Bälle und Testprozedere (shuttles and test procedure) | G | Determine which information is useful in a club prospectus; exact equipment and timing claims remain unverified. |
| N1-12 | 1 | Startgebühren und Zahlungsmodalitäten (entry fees and payment terms) | G | Separate fee information from payments and financial automation; avoid assuming currency or mandatory fee. |
| N1-13 | 1 | Unterbringung und Transport (accommodation and transport) | G | Optional information only as a candidate; do not assume the source's international process applies. |
| N1-14 | 1 | Regelwerke und Schiedsrichterkontakte (rules references and referee contacts) | G | Determine need and applicable rules; overlaps #184 and #186. |
| N2-02 | 2 | DBV-Mitgliedsdaten und Ranglisten importieren (federation and ranking import) | G | Actual interface availability, terms, ownership and privacy are unverified; Player Management is a placeholder. |
| N2-03 | 2 | Nationale Ergebnisse und Meldelisten exportieren (national results and entry export) | G | Confirm an available supported interface and format; do not assume an API exists. |
| N2-04 | 2 | Meldeliste, Qualifikation und Reserve (entry, qualification and reserve lists) | G | `ParticipantsRoster.kt` holds roster representation, not the reference's selection, report or promotion workflow. |
| N2-05 | 2 | Datenschutz bei Teilnehmerdaten (participant-data privacy) | G | Determine lawful processing, minimization and retention for actual workflows; international rights assignment is deferred, not adopted. |
| N3-03 | 3 | Anzahl Setzplätze (number of seeds) | G | The reference table differs from the DBV knockout provision below; specific competition applicability and implementation remain unresolved. Overlapping research belongs with #185. |
| N3-04 | 3 | Rastergröße und Freilose (bracket size and byes) | G | Define eligible draw sizes, placement, auditability and edge cases before proposing an algorithm. |
| N3-05 | 3 | Position und Losung von Setzgruppen (seed placement and drawing lots) | G | Confirm national placement rules and randomness requirements; overlap #185. |
| N3-06 | 3 | Gruppenansetzung und Übergang ins K.o.-Feld (group fixture order and knockout advancement) | G | Reference examples need verification and must not become universal constraints. |
| N3-07 | 3 | Rückzug, Nachrücken und Neuauslosung (withdrawals, promotions and redraw) | G | Separate state transitions and competing fairness rules; source's international timing is not assumed. |
| N4-02 | 4 | Ruhezeiten (rest periods) | G | Source's 60/30-minute claim requires applicable national or regional authority, competition and exception verification. |
| N4-03 | 4 | Mannschaftsreihenfolge und Doppelbelastung (team match order and player conflicts) | G | Nine named sequences are Major Events claims; national league applicability is unresolved. |
| N4-04 | 4 | Aufruf und Nichterscheinen (calling and no-show) | G | Model operational states only after need and applicable timings are established; overlap #184. |
| N4-05 | 4 | Hallenstörungen (venue interference) | G | Venue-specific guidance is not proof that the system must make officiating decisions; overlap #184. |
| N4-06 | 4 | Coaching, Geräte und Material (coaching, devices and equipment) | G | Reference proposes courtside procedures and enforcement; assess each generic rule's software relevance separately. No feature adoption is assumed; overlap #184. |
| N4-07 | 4 | Verletzung und Bekleidungswechsel (injury and clothing changes) | G | Separate event recording and scoring effects from medical and officiating procedures; assess relevance individually. Overlap #184. |
| N4-08 | 4 | Ergebnisbestätigung ohne Schiedsrichter (result confirmation without umpire) | G | A possible result-entry workflow; paper signatures and who confirms require user and national-rule validation. |
| N4-09 | 4 | Schiedsrichteransagen und Bekleidung (official announcements and uniform) | G | Source includes official guidance and proposed system enforcement; distinguish individual generic claims from international-specific requirements. Overlap #184. |
| N5-01 | 5 | Abmeldung und Gebühren (withdrawals and fees) | G | Generic withdrawal and organizer-configured fee information may matter; the stated international sanctions and allocation are deferred. |
| N5-02 | 5 | Karten und Disqualifikation (cards and disqualification) | G | Determine whether a result/status record is needed; scope and consequences depend on competition rules. Overlap #184 and #186. |
| N6-03 | 6 | Rückzug in Gruppenwertung (withdrawal in group standings) | G | Check the relevant authority's result treatment and historical audit before specifying; Scoring is a placeholder. |
| N6-04 | 6 | Ergebnisdaten und Berichte (results and reports) | G | Result capture, verification and report generation are absent from the cited configuration specs; ownership needs a decision. |
| N6-05 | 6 | Aushang und PDF (printable draws and standings) | G | Candidate for organizer and spectator information; do not assume a 24-hour international reporting requirement. |
| Q-01 | 2 | Echtzeit, Latenz, Datenintegrität und Verschlüsselung (realtime, latency, integrity and encryption) | G | Reference asserts these qualities without measurable targets, failure semantics or interface evidence; define against actual usage rather than adopt absolutes. |
| Q-02 | 1–6 | Regelkonformität und Auditierbarkeit (rule compliance and auditability) | G | Reference claims compliance without verifiable system boundaries; define applicable versioned rules and human decisions first. |

## International-only items deferred

- I1 §1: Grade 1 major events — international, deferred.
- I2 §1: Grade 2 World Tour — international, deferred.
- I3 §1: Grade 3 continental circuit — international, deferred.
- I4 §1: BWF international junior events — international, deferred.
- I5 §1: AirBadminton, including national variants explicitly excluded by #183 — international, deferred.
- I6 §1: International team leagues and nationally hosted BWF-sanctioned leagues with international participation — international, deferred.
- I7 §1: BWF governance hierarchy and role enforcement — international, deferred.
- I8 §1: Grade 2 sanction fee calculation — international, deferred.
- I62 §1: Grade 3 sanction fee set by the continental body — international, deferred.
- I9 §1: BWF cancellation/change sanction within 120 days — international, deferred.
- I63 §1: BWF referral and potential additional sanctions within 15 days — international, deferred.
- I64 §1: Force-majeure relief for BWF cancellation/change sanctions — international, deferred.
- I10 §1: Visa support — international, deferred.
- I11 §1: International prospectus prize money and withholding tax — international, deferred; national entry-fee information remains N1-12.
- I12 §1–2: BWF M&Q release dates — international, deferred; generic dates remain N1-10.
- I31 §1–2: BWF HQ withdrawal deadline — international, deferred; generic dates remain N1-10.
- I32 §1–2: BWF M&Q and seeding world-ranking cutoffs — international, deferred; generic ranking dates remain N1-17.
- I13 §2: BWF online-entry receipt and acknowledgement — international, deferred; generic entry workflows remain N2-07.
- I49 §2: BWF alert for missing acknowledgement by the specified deadline — international, deferred.
- I14 §2: BWF online-entry correction deadline — international, deferred; generic correction remains N2-07.
- I55 §2: BWF lock after correction deadline — international, deferred; generic lock remains N2-07.
- I15 §2: BWF international jurisdiction after relocation — international, deferred.
- I40 §2: BWF player transfer form and suspension after refusal — international, deferred.
- I41 §2: Contract-related international ineligibility — international, deferred.
- I16 §2: Return notification after BWF deregistration — international, deferred.
- I50 §2: Three-month lead time for returning former top-200 players — international, deferred.
- I17 §2: International representation established by selected team or individual appearances — international, deferred.
- I51 §2: International federation change, representation and nationality conditions — international, deferred.
- I52 §2: Neutral BWF nationality status — international, deferred.
- I18 §2: BWF assignment of name, image, performance and related rights — international, deferred; national data handling remains N2-05.
- I58 §2: Waiver of moral rights in BWF materials — international, deferred.
- I59 §2: BWF member-association responsibility for consent — international, deferred.
- I19 §2: BWF anti-doping integration — international, deferred.
- I60 §2: BWF betting and manipulation monitoring integration — international, deferred.
- I20 §2: BWF Players Undertaking and top-100 eligibility — international, deferred.
- I21 §2: Host wildcard eligibility — international, deferred.
- I42 §2: Host wildcard nomination — international, deferred.
- I56 §2: Host wildcard confirmation before M&Q publication — international, deferred.
- I43 §2: Host wildcard world-ranking points — international, deferred.
- I22 §3: World-ranking notional formula — international, deferred.
- I33 §3: World-ranking adjusted factors — international, deferred.
- I34 §3: Notional/adjusted-ranking seeding cap — international, deferred.
- I23 §3: Filling remaining seeds from BWF M&Q order — international, deferred.
- I24 §3: Grade 3 national association separation — international, deferred; generic separation remains unresolved.
- I25 §3: Seed movement after withdrawal at the team-managers' meeting — international, deferred; generic withdrawal remains N3-07.
- I57 §3: Prohibition on repeating that seed movement — international, deferred.
- I26 §3: Reserve acceptance deadlines tied to the team-managers' meeting — international, deferred; generic promotions remain N3-07.
- I27 §4: Nine prescribed Major Events team sequences — international, deferred; generic player conflicts remain N4-12.
- I61 §4: Major Events three-match spacing between singles and doubles — international, deferred; generic player conflicts remain N4-17.
- I65 §2: BWF export interface for live results, reports and entry lists — international, deferred; national exchange remains N2-03. The distinct daily reporting requirement is I30.
- I28 §5: BWF no-show penalty amounts — international, deferred; generic withdrawal and fee information remain N5-01.
- I44 §5: BWF mass-withdrawal penalty — international, deferred.
- I45 §5: BWF clothing violation penalty — international, deferred.
- I46 §1: Linking event grade to BWF world-ranking points — international, deferred.
- I47 §1: Specific BWF advertising prohibitions — international, deferred; generic rule references remain N1-14.
- I48 §2: Import of BWF IDs and world/junior world rankings — international, deferred; national identifiers remain N2-02.
- I35 §5: BWF withdrawal fee distribution — international, deferred.
- I36 §5: BWF federation suspension for unpaid invoices — international, deferred.
- I29 §5: BWF confirmed-result eligibility for prize money and world-ranking points — international, deferred.
- I37 §5: BWF prize-money remittance deadline — international, deferred.
- I38 §5: BWF late-payment interest — international, deferred.
- I39 §5: BWF withholding certificates and future sanctioning — international, deferred.
- I30 §6: Daily BWF results submission — international, deferred; national export remains N6-11.
- I53 §6: BWF 24-hour final results export for ranking — international, deferred.
- I54 §6: PDF fallback for BWF export failure — international, deferred; generic print remains N6-12.

## Detailed trace for grouped claims

The following rows split the larger entries above so that differing assumptions can be reviewed independently. `G` means a capability gap or behavior not established in the examined configuration, not that the feature should be built. `D` means international-only and deferred under issue scope. Numeric examples are unverified source claims unless linked to a checked primary source.

| ID | Source claim (English gloss) | Disposition | Relationship |
| --- | --- | --- | --- |
| N1-15 | Meldeschluss (entry closing date) | P | `Tournament.registrationWindows` stores windows; no automatic closing workflow is demonstrated. N1-10. |
| N1-16 | Auslosungstermin (draw date) | G | Separate from an entry window; N1-10. |
| N1-17 | Ranglistenstichtag für Setzung (ranking reference date) | G | N1-10 and N3-01; authority varies by event. |
| N1-18 | Warte- und Aufwärmflächen (warm-up spaces) | G | N1-09; recording space differs from enforcing access restrictions. |
| N1-19 | Ausschreibungsprüfung (prospectus completeness check) | G | N1-07; required fields need an applicable source, not the BWF checklist by default. |
| N2-06 | Spieler- und Vereinskennungen (player and club identifiers) | P | `ParticipantsRoster` stores IDs, not verified federation identity or synchronization. N2-02 and N2-04. |
| N2-07 | Meldungen korrigieren und sperren (correct and lock entries) | G | Generic state workflow remains open; BWF-specific deadlines and locks are I14 and I55. |
| N2-08 | Hauptfeld, Qualifikation und Reserve (main, qualifying and reserve entries) | G | N2-04; existing bracket IDs do not establish selection or a reserve queue. |
| N2-09 | Jugend-Altersgrenze (junior eligibility) | G | N2-01; reference calendar-year interpretation differs from the DBV season cutoff discussed below. |
| N2-10 | Senioren-Altersgrenze (senior eligibility) | G | N2-01; age group and effective date are separate from discipline. |
| N3-08 | Rastergröße als Zweierpotenz (power-of-two draw size) | G | N3-04; applicable format and small-field cases remain to be specified. |
| N3-09 | Freilose berechnen (calculate byes) | G | N3-04; the formula's inputs, zero/one-entry cases and placement rules need a selected format. |
| N3-10 | Erste und zweite Setzposition (top-seed placement) | G | N3-05; confirm chosen draw and competition authority. |
| N3-11 | Setzgruppen auslosen (draw seed groups) | G | N3-05; includes 3–4, 5–8 and 9–16 positions and tied rankings. Randomness and reproducibility require design. |
| N3-12 | Spielreihenfolge bei drei Teilnehmenden (three-entry fixture order) | G | N3-06; reference example is not yet a universal rule. |
| N3-13 | Spielreihenfolge bei vier Teilnehmenden (four-entry fixture order) | G | N3-06; compare exact order and authority with #185. |
| N3-14 | Zwei Gruppen ins K.-o.-Feld übernehmen (two-group advancement) | G | N3-06; source's World Senior mapping is event-specific, national applicability unverified. |
| N3-15 | Vier Gruppen ins K.-o.-Feld übernehmen (four-group advancement) | G | N3-06; reference's positions and avoidance of immediate rematches need independent authority and event scope. |
| N3-16 | Neue Auslosung nach Fehler oder Ungleichgewicht (redraw after error or imbalance) | G | N3-07; no claim that the source's cutoff and criteria apply nationally. |
| N4-10 | Aufrufstatus (call states) | G | N4-04; first call, second call, absent-player escalation and discretionary decision are separate actions. |
| N4-11 | Aufruf- und Aufwärmzeiten (call and warm-up timing) | G | N4-04; source's 2-minute, 5-minute and later windows need verification for the target event. |
| N4-12 | Spielerkonflikte und Ruhepausen (player conflicts and rest) | G | N4-02–03; team sequence and consecutive matches are separate constraints. |
| N4-13 | Zähltafel ohne Schiedsrichter (scoreboard without umpire) | G | N4-08; operator authority and confirmation mechanism remain undecided. |
| N5-03 | Schiedsrichterbericht (referee report) | G | N5-02; whether RallyOn records a report and the applicable due date are unresolved. |
| N6-06 | Gewonnene Spiele (match wins) | G | N6-01; selected competition determines ranking order. |
| N6-07 | Satz- und Punktdifferenz (set and point differences) | G | N6-01; order and inputs vary with the applicable competition rules. |
| N6-08 | Direkter Vergleich und Losentscheid (head-to-head and drawing lots) | G | N6-01; order is not universal, see the DBV comparisons below. |
| N6-09 | Aufgabe eines Einzelspielers oder Paars (individual or pair withdrawal) | G | N6-03; distinguish excluding results from a calculation from erasing their history. |
| N6-10 | Abgebrochenes Spiel im Mannschaftskampf (unfinished match in a team tie) | G | N6-03; source's assumed remaining-points treatment is unverified nationally. |
| N6-11 | Täglicher nationaler Datenexport (daily national data export) | G | N2-03 and N6-04; format, authority and supported interface are unverified. |
| N6-12 | Druckausgabe und technische Störung (print output and technical failure) | G | N6-05; distinguish ordinary venue notices from the international 24-hour export and fallback in I53–I54. |
| N1-20 | Ballmodell und Ballbereitstellung (shuttle model and provision) | G | N1-11; distinguish publishing a model and provider from on-court shuttle-box and testing procedures. |
| N1-21 | Preisgeldangaben (prize-money information) | G | N1-12; optional national prospectus information is distinct from BWF settlement I29. |
| N2-11 | Eingangsbestätigung einer Meldung (entry acknowledgement) | G | N2-04; generic acknowledgement is distinct from the BWF alert in I49. |
| N3-17 | Gleichstand bei Setzwerten (ties between seed ratings) | G | N3-11; need a chosen competition's tie rule and recorded lot procedure. |
| N3-18 | Vereins- oder Verbandstrennung (club or association separation) | G | Generic national analogue to I24; verify applicable association rule and feasibility before specifying. |
| N4-14 | Erste und zweite Aufrufe (first and second calls) | G | N4-10; source suggests second call five minutes after first, national applicability unverified. |
| N4-15 | Referee-Benachrichtigung nach ausbleibendem Erscheinen (referee notice after absence) | G | N4-10; proposed additional two-minute interval is unverified nationally. |
| N4-16 | Disqualifikationsoption nach weiterem Warten (disqualification option after further waiting) | G | N4-10; source suggests a further five-minute interval and an option, not mandatory automatic disqualification. |
| N4-17 | Mindestabstand bei Einzel und Doppel im Mannschaftskampf (spacing between singles and doubles in a team tie) | G | N4-12; generic conflict is in scope; source's exact Major Events three-match rule is I61. |
| N4-18 | Coaching-Personenzahl und Wechsel (coach count and substitution) | G | N4-06; source's two-person limit, replacement order and permitted coaching intervals need authority-specific verification; overlap #184. |
| N4-19 | Coachingkleidung und Geräte (coach clothing and devices) | G | N4-06; assess separately as human guidance or a software requirement, with #184. |
| N4-20 | Ersatzschläger, Ablage und Übergabe (spare racket, storage and transfer) | G | N4-06; source alleges a mandatory spare racket and referee-mediated transfer; verify separately and assess actual software value. |
| N4-21 | Elektronisches Kommunikationsgerät (electronic communication device) | G | N4-06; source alleges a sanction and wording; verify authority and whether recording is useful, with #184. |
| N4-22 | Verletzungsbehandlung und Blutung (injury treatment and bleeding) | G | N4-07; separate human medical procedure from any recordable match interruption. |
| N4-23 | Bekleidungswechsel während des Spiels (clothing change during play) | G | N4-07; verify authority and assess information versus system enforcement. |
| N4-24 | Service-Richter und Korrektur (service judge and correction) | G | N4-09; assess informational guidance and whether correction provenance is needed. |
| N4-25 | Schiedsrichter-Ansagen (umpire announcements) | G | N4-09; review separate service-judge, challenge, game-end and fault examples as guidance with #184. |
| N4-26 | Kleidung und Sponsorenlogos von Schiedsrichtern (official attire and logos) | G | N4-09; source's gendered uniform and logo limits are unverified; software relevance remains unresolved. |
| N5-04 | Gelbe und rote Karten erfassen (record yellow and red cards) | G | N5-02; determine whether organizer workflow requires recording and verified consequences. |
| N5-05 | Schwarze Karte und Ausschluss (black card and exclusion) | G | N5-02; source alleges tournament-wide exclusion including team ties; verify applicable competition rule. |
| N5-06 | Frist für Schiedsrichterbericht (referee-report deadline) | G | N5-03; source's two-week deadline is unverified for national events. |
| N6-13 | Gleichstand von zwei Teilnehmenden (tie between two entrants) | G | N6-08; source applies direct comparison immediately after match wins; the DBV comparison below differs. |
| N6-14 | Gleichstand von drei oder mehr Teilnehmenden (tie among three or more) | G | N6-07–08; source applies set then point difference, with a switch to head-to-head if two remain tied; applicability unverified. |
| N1-22 | Verbot des Einspielens in Foyer und Gängen (warm-up prohibition in foyer and corridors) | G | N1-18; a venue-specific operational rule, not proven to be a universal national software constraint. |
| N1-23 | Referee-Ausnahme für Einspielen auf Hauptfeldern (referee exception for main-court warm-up) | G | N1-18; verify facility authority and who records an exception. |
| N1-24 | Ballboxen am Feld (shuttle boxes at court) | G | N1-20; distinguish equipment guidance from inventory management and verify applicability. |
| N1-25 | Balltest in der Feldgewöhnung (shuttle test during warm-up) | G | N1-20; the source's two-minute timing is unverified for the chosen competition. |
| N4-27 | Referee prüft Verletzung und ruft Arzt (referee assesses injury and calls doctor) | G | N4-22; potential incident record is distinct from human medical judgment. |
| N4-28 | Umpire misst Verletzungsunterbrechung (umpire records delay duration) | G | N4-22; determine whether and how an interruption duration should be recorded. |
| N4-29 | Behandlung auf Feld und Eisspray (on-court treatment and ice spray) | G | N4-22; source exceptions and prohibitions require authority verification and software-relevance assessment. |
| N4-30 | Blutung stillen und Feld reinigen (control bleeding and clean court) | G | N4-22; human safety procedure, with potential interruption-state information; national applicability unverified. |

## Source-location checklist

This checklist is for a fresh independent completeness review; topic-level mappings do not by themselves prove that every statement was separated correctly. IDs in `analysis.md` are part of the same inventory.

| Reference location | Inventory IDs |
| --- | --- |
| §1 tournament types and sanctioning | N1-01–02, N1-04–06; I1–I9, I46, I62–I64 |
| §1 prospectus table: contacts, venue, warm-up | N1-03, N1-07–09, N1-18–19, N1-22–23 |
| §1 prospectus table: dates, shuttles, fees, logistics, rules | N1-10–17, N1-20–21, N1-24–25; I10–I12, I31–I32, I47 |
| §2 data exchange, acknowledgements and lock | N2-02–04, N2-06–08, N2-11, Q-01; I13–I14, I48–I49, I55, I65 |
| §2 eligibility, privacy, wildcard | N2-01, N2-05, N2-09–10; I15–I21, I40–I43, I50–I52, I56 |
| §3 notional/adjusted rankings and seed limits | N3-01, N3-03, N3-17; I22–I24, I33–I34 |
| §3 bracket sizes, byes, seed positions, group fixtures and advancement | N3-02, N3-04–06, N3-08–15, N3-17–18 |
| §3 withdrawal, promotion and redraw | N3-07, N3-16; I25–I26, I57 |
| §4 rest, team order and call sequence | N4-01–04, N4-10–12, N4-14–17; I27, I61 |
| §4 hall, coaching, equipment, injury and officials | N4-05–09, N4-13, N4-18–30 |
| §5 withdrawals, cards, reports and finance | N1-12, N5-01–06; I28–I29, I35–I39, I44–I45 |
| §6 group ranking, withdrawal and result reporting | N6-01, N6-03–14; I30, I53–I54 |
| Cross-cutting claims | Q-01–02; the scoring-format transition in `open-questions-and-unverified-rules.md` is separate context, not a source claim |

## Initial ordering and open decisions

| Candidate | Organizer / player / spectator value hypothesis | Effort | Context and dependencies |
| --- | --- | --- | --- |
| N3-02, N3-04–06: a small draw and progression slice | High / high / medium | High | Tournament Management; choose one club format and define phases, participant inputs and reproducible draw before implementation. |
| N6-01, N6-03–05: recorded results and standings | High / high / high | High | Scoring ownership and cross-context contract unresolved; result entry precedes meaningful standings and exports. |
| N1-03, N1-07–12: basic prospectus information | High / medium / medium | Medium | Tournament Management; choose fields based on a real club event, keeping federation publication requirements separate. |
| N2-04, N1-02: entries and invitation flow | High / high / low | High | Participant ownership, identity and registration policy unresolved; Player Management is currently a placeholder. |
| N4-01–04: scheduling and calling | High / high / medium | High | Depends on fixtures, results and court availability; hall connectivity and override workflow remain hypotheses. |
| N1-05, N1-06, N2-01: league and age concepts | Medium / medium / low | High | Need actual target-event evidence and governing regulations; avoid treating the reference as a product mandate. |
| N2-02–03: federation integrations | Medium / medium / medium | High / unknown | Verify supported interfaces, access agreements, data and operational dependencies before committing. |

This ordering is a qualitative hypothesis, not an agreed roadmap or calculated lifecycle value score. The group-standings candidate was accepted for further analysis, and #203 was created for that purpose; no implementation has been accepted. In particular, event-specific group tie-break order, age cutoffs, rest times, withdrawal consequences, redraw criteria, national data exchange and youth/senior applicability remain unverified. The seed threshold comparison below checks one DBV provision, not every competition.

## Primary-source cross-check: seed counts

The current DBV index links the [2026/27 regulations, dated 13 June 2026 and updated 7 July 2026](https://www.badminton.de/fileadmin/dateien_upload/dateien_2026/juli_2026/dbv_satzungswerk_2026-27_stand_13.06.2026.pdf). DBV Spielordnung, Anlage IV, Teil 2, § 3(1), printed page 230 (PDF page 232), permits one seed for seven or fewer entries and two from eight entries. It also describes qualification-event and justified exceptions. The issue reference instead claims two seeds for every field below 16 and 32 seeds from 128 entries. These are different rule sets: the issue's table must not be treated as a universal national rule. The DBV provision itself is conditional and must not be applied to every club or regional event without checking applicability. This cross-check informs N3-03 and #185; it does not accept an automated seeding requirement.

## Primary-source cross-check: age and group standings

- N2-01: The same DBV regulations, Spielordnung, Durchführungsbestimmungen § 10(1)–(2), printed pages 208–210 (PDF pages 210–211), define age classes and use 1 January following the start of the playing season as the classification date for official DBV tournaments. The source reference describes age within the tournament's calendar year. These can differ for a tournament after the season begins but before January; the source's test must not be generalized to official DBV tournaments. Confirm competition-specific eligibility and exceptions before defining a validation algorithm.
- N6-01: DBV Spielordnung, Anlage IV, Teil 2, § 10, printed page 235 (PDF page 237), lists match wins, set difference and point difference before direct comparison and drawing lots. The issue reference puts direct comparison immediately after wins for a tie between two players. These stated sequences differ. The same PDF's Jugendspielordnung, Anlage I, § 30 (printed page 299, PDF page 301), specifies a separate treatment including a direct-comparison exception after withdrawal. Applicability depends on event and rules version; neither sequence is a universal default. The current `TieBreakRules.kt` flags do not encode either complete sequence.

These comparisons verify the cited provisions, not their applicability to every competition, nor a product decision to support them. Age, result removal, remaining ties and version transitions need examples and independent domain review before specifications prescribe behavior.

## Primary-source cross-check: breaks, byes and interrupted games

- N4-02 and N4-11: [DBV 2026/27 regulations](https://www.badminton.de/fileadmin/dateien_upload/dateien_2026/juli_2026/dbv_satzungswerk_2026-27_stand_13.06.2026.pdf), Spielordnung Anlage III § 38 (PDF page 226), provide a player's entitlement to a 30-minute break between games. This does not establish the reference's blanket 60-minute call restriction or its proposed referee override. The Bundesliga provision (PDF page 257) also distinguishes the start of the next game from its official call; do not conflate these timestamps or extend Bundesliga details to other events.
- N3-08–09: Spielordnung Anlage IV Teil 2 § 2(1)–(3) (printed page 226, PDF page 228) describes power-of-two draw sizes, computes byes as the difference to the next full draw, and gives placement tables. This supports the general arithmetic for the provision's knockout format, but does not resolve zero/one-entry edge cases or prove it is correct for every format. No draw implementation is established by `BracketConfig` or `TournamentFormat`.
- N5-02 and N6-10: Spielordnung Anlage III §§ 39–40 (PDF page 226) distinguish disqualification from withdrawal and describe different treatment where the score matters. The reference's generic treatment of an interrupted team match therefore needs a competition-specific comparison. The printed examples use 21-point scores; the separate 2027 scoring announcement makes version/effective-date verification essential before reusing them.
- N4-04: The same Anlage III § 42 (printed page 225, PDF page 227) describes a possible exclusion after a player is not ready ten minutes after the communicated discipline start. The reference also describes a disqualification option, but uses a different sequence of call events and timings. Applicability and authority remain to be established for a selected event.

## Related issue boundaries

The following are proposed research ownership boundaries, not changes to the linked issues:

| Related issue | Shared material | Avoided duplication |
| --- | --- | --- |
| [#184](https://github.com/wlambertz/rallyon/issues/184) | N1-09, N1-11, N4-04–09, N5-02: hall and on-court rules, calls, equipment, medical and card protocols | Keep detailed match-day rule inventory and authority checks in #184; #183 records product relevance and links to that evidence. |
| [#185](https://github.com/wlambertz/rallyon/issues/185) | N3-01–07, N4-02, N6-01: seeding, byes, draws, withdrawals, group progression and rest | Keep detailed draw-rule comparisons in #185; reuse the DBV comparisons above with competition and edition attached. |
| [#186](https://github.com/wlambertz/rallyon/issues/186) | N1-07–14, N2-05, N5-01–02: prospectus, contacts, privacy, organizer fees and sanctions | Keep organizer-compliance and privacy-specific research in #186; do not infer that international rights or sanctions apply nationally. |

## Candidate follow-ups for review

These are problem statements, not approved issue payloads or specifications. Each requires a bounded scenario, agreed rule authority where relevant, acceptance criteria and a separate lifecycle decision before implementation.

1. **Define a first club tournament progression slice** (N3-02, N3-04–06): select a specific club format and participant input; define draw, byes, state and advancement. Depends on a decision about concrete phase ownership; existing `Phase` is empty.
2. **Define result recording and standings ownership** (N4-08, N6-01, N6-03–04): establish who records and corrects a result, which bounded context owns the authoritative result, and how a chosen competition's ordered tie-break rules are represented. Scoring is currently a placeholder, and no universal hierarchy can be inferred from the two DBV provisions above.
3. **Refine organizer prospectus information** (N1-07–12): select the information needed for one real club event and distinguish optional information from competition-specific publication rules. Existing venue, courts and registration windows are partial inputs.
4. **Investigate entries and invitations** (N1-02, N2-04): establish the roster and registration lifecycle and participant ownership before treating `INVITE_ONLY` as a usable invitation mechanism.
5. **Research national federation data exchange** (N2-02–03): verify supported integration, permissions, formats and maintenance burden before designing an automated import or export.
6. **Assess national age and team competitions** (N1-05–06, N2-01): establish actual target competitions and separate seasons, teams and ties from age-class eligibility. Use the relevant edition and any regional exceptions.

The first follow-up, [#203](https://github.com/wlambertz/rallyon/issues/203), was approved and created for further analysis of group standings; it does not authorize implementation. No other follow-up has been approved. No separate issue is proposed for a rule already covered by #184–#186 until its distinct value and dependency are established. Selection, final scope and ordering remain with the maintainer.
