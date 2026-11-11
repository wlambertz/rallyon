# Statement inventory of the supplied guide

This inventory describes claims in [the preserved secondary source](source-reference.md), not accepted rules. IDs are stable within this research. German topic names have English glosses. The authority column records **what the guide claims**, including absent or ambiguous citations; it does not establish applicability. Primary-source findings belong in the research's source verification and findings.

Classification: **S** = software-relevant mechanism or candidate setting; **I** = informational; **D** = international/deferred scope. Handling: **verify** = pending primary-source and applicability check before any rule adoption; **model** = inspect current code and a concrete product scenario, without inferring implemented behavior; **defer** = preserve provenance, no first-slice adoption; **cross-ref** = duplicate/overlap, use the named ID as its canonical claim. These are research dispositions, not feature requirements. A D row may expose a generic S mechanism elsewhere without importing its BWF-specific policy.

## §1 — KO-Turnierbaum und Freilose / knockout tree and byes

| ID | German term / English gloss and faithful source assertion | Class | Claimed authority | Handling |
| --- | --- | --- | --- | --- |
| D1-01 | Zweierpotenz / power of two: knockout structure uses `2^n` slots. | S | Mathematical framing; GCR 11.5 in following sentence | model; separate arithmetic from regulation |
| D1-02 | Standardfeldgrößen / standard draw sizes: 4, 8, 16, 32, 64 or 128 slots. | S | GCR 11.5 | verify; do not infer supported product sizes |
| D1-03 | Halbierung / halving: remaining playing units halve each round, ending with two players or pairs in the final. | S | Explanatory consequence, no separate clause | model; qualify for byes and nonstandard formats |
| D1-04 | Freilos / bye: fills a draw when actual entries are not a power of two; player/pair skips the first round and advances automatically. | S | No independent citation at definition | model; distinguish bye from walkover |
| D1-05 | Paarung / pairing: two playing units face each other each round. | S | No clause | model |
| D1-06 | Ausscheiden / elimination: a match loser leaves the competition irreversibly. | S | No clause | model; applies to the described pure knockout, not every format |
| D1-07 | Mindestruhezeit / minimum rest: 60 minutes between matches. | S | GCR 11.4 | verify; no universal club default |
| D1-08 | Ruhezeitausnahme / rest exception: referee may shorten rest to at least 30 minutes in exceptional organizational circumstances. | S | GCR 11.4 by paragraph association | verify; actor and exception need checking |
| D1-09 | Freilosformel / bye formula: byes equal next higher power of two minus actual participants. | S | GCR 11.5.2 | model and verify attribution; define exact-power boundary |
| D1-10 | Rechenbeispiel / worked example: 17 players need a 32-slot tree and 15 byes. | S | Example of D1-09 | model; arithmetic example, no extra obligation |
| D1-11 | Erste Runde / first-round example: 17 players with 15 byes produce one played first-round match and 15 automatic advances. | S | Example of D1-09 | model; assumes valid bye placement |
| D1-12 | Einteilung vor Auslosung / allocation before draw: entries are divided among main draw, qualifying and reserve to obtain fair, compliant distribution. | S | Transition to §2, no clause | model; three-stage admission is not assumed universal |

## §2 — Hauptfeld, Qualifikation und Reserve / admission segments

| ID | German term / English gloss and faithful source assertion | Class | Claimed authority | Handling |
| --- | --- | --- | --- | --- |
| D2-01 | Dreistufiger Zugang / three-stage admission: high-entry international and national events use main draw, qualifying and reserve. | S | GCR 12 | verify national applicability; model separately from capacity |
| D2-02 | Ranglistenstichtag / ranking cutoff: BWF World Ranking at an official cutoff primarily determines allocation. | D | GCR 12 | defer; no verified ranking integration |
| D2-03 | Hauptfeld / main draw: highest-ranked entries receive direct main-event access and can compete for the title. | D | GCR 12 table context | defer exact ranking policy; generic field selection in D2-01 |
| D2-04 | Qualifikation / qualifying draw: next-ranked players/pairs play knockout for a fixed number of main-draw places, e.g. Q_1/Q_2. | S | GCR 12 table context | verify policy; model qualifier slots separately |
| D2-05 | Reserveliste / reserve list: unseeded/lower-ranked entries after qualifying capacity move up in ranking order if accepted entries withdraw before play. | S | GCR 12 table context | verify distinction between seeding and admission ranking |
| D2-06 | Grade-1/2-Quote / grade-1/2 quota: one qualifying place per eight main-draw slots. | D | GCR 12.2.1 | defer; verify if reporting as fact |
| D2-07 | Grade-3-Quote / grade-3 quota: one qualifying place per four main-draw slots. | D | GCR 12.2.2 | defer; verify if reporting as fact |
| D2-08 | Ungerankte Länderlose / unranked nationality draw: after ranking-based admission leaves vacancies, unranked players are drawn by nationality first. | D | GCR 12.6 | defer; nationality data/authority unresolved |
| D2-09 | Nationale Rangfolge / national ranking order: member-association submitted order then determines unranked players' exact tableau positions. | D | GCR 12.6 | defer; distinguish admission ordering from draw placement |
| D2-10 | Rückkehrmeldepflicht / return notice: deregistered athletes who were top 200 in any discipline within five years must notify BWF through their association at least three months before returning. | D | GCR 5.7.4.1; technicalevents@bwf.sport | defer; identity/eligibility policy outside slice |
| D2-11 | Favoritenschutz / protecting favorites: seeding follows allocation to avoid favorite-vs-favorite matches in early rounds. | I | Transition to §3 | explanatory rationale only; algorithm claims in §3 |

## §3 — Setzung und Ländertrennung / seeding and association separation

| ID | German term / English gloss and faithful source assertion | Class | Claimed authority | Handling |
| --- | --- | --- | --- | --- |
| D3-01 | Setzzweck / seeding purpose: protect stronger units from meeting in early rounds. | I | GCR 11.6–11.11 | rationale, duplicate D2-11; no guarantee assumed |
| D3-02 | Setzgrundlage / seeding basis: use World Ranking at cutoff. | D | GCR 11.6–11.11 context | cross-ref D2-02; defer |
| D3-03 | Notional Ranking / hypothetical pair ranking: newly formed doubles use a calculated average. | D | GCR 11.7 context | defer; source omits complete formula |
| D3-04 | Adjusted Ranking / adjusted pair ranking: pairs with fewer than eight shared counting events in the last 52 weeks use an adjusted value. | D | GCR 11.7 | defer; verify thresholds/formula before reporting as fact |
| D3-05 | Rang-5-Grenze / fifth-seed limit: notional/adjusted pairs cannot be seeded above position 5 in the main draw. | D | GCR 11.7.4 | defer; verify interpretation |
| D3-06 | Ranggleichheit / equal ranking: equal World Ranking points are resolved by lot for higher seed position. | S | GCR 11.6.1 | verify; generic reproducible tie decision is a product choice |
| D3-07 | Unbesetzte Setzplätze / vacant seed slots: after regular/notional/adjusted rankings, fill remaining positions strictly in M&Q-report order. | D | GCR 11.7.5 | defer; no M&Q model assumed |
| D3-08 | Setzposition 1 / first seed: fixed at top slot 1. | S | GCR 11.9–11.11 | verify relevant draw table and event |
| D3-09 | Setzposition 2 / second seed: fixed at bottom slot. | S | GCR 11.9–11.11 | verify relevant draw table and event |
| D3-10 | Setzgruppe 3/4 / seeds 3–4: draw into remaining two quarters, described as tops of quarters 2 and 3. | S | GCR 11.9–11.11 | verify exact permitted positions; do not implement prose literally |
| D3-11 | Setzgruppe 5–8 / seeds 5–8: draw into remaining eighths. | S | GCR 11.9–11.11 | verify table and slot orientation |
| D3-12 | Setzgruppe 9–16 / seeds 9–16: draw into remaining sixteenths. | S | GCR 11.9–11.11 | verify table and slot orientation |
| D3-13 | Ländertrennung / association separation: top two Grade-3 units from one member association go in opposite halves. | D | GCR 11.12 | defer; country, association and club are not interchangeable |
| D3-14 | Erst­runden­trennung / first-round separation: units from one association should avoid each other where possible. | S | GCR 11.12, following Grade-3 statement | verify scope and soft/hard constraint; no club rule inferred |
| D3-15 | Setzkappe ≥128 / seed cap: at least 128 entries permits at most 32 seeds. | S | BWF statutes asserted; table gives no exact clause | verify; distinguish entries, draw size, cap and requirement |
| D3-16 | Setzkappe 64–127 / seed cap: 64–127 entries permits at most 16 seeds. | S | Same table as D3-15 | verify |
| D3-17 | Setzkappe 32–63 / seed cap: 32–63 entries permits at most eight seeds. | S | Same table as D3-15 | verify |
| D3-18 | Setzkappe 16–31 / seed cap: 16–31 entries permits at most four seeds. | S | Same table as D3-15 | verify |
| D3-19 | Setzkappe <16 / seed cap: fewer than 16 entries means two seeds; source does not write “maximal” in this row. | S | Same table as D3-15 | verify lower bounds and mandatory/maximum distinction |
| D3-20 | Absageabhängigkeit / withdrawal dependency: seeded and unseeded withdrawals before/during play cause promotion and adjustment processes. | S | Transition to §4 | model; detailed claims in §4 |

## §4 — Absagen und Nachrücken / withdrawals and promotions

| ID | German term / English gloss and faithful source assertion | Class | Claimed authority | Handling |
| --- | --- | --- | --- | --- |
| D4-01 | Ausgewogenheit / balance: withdrawal handling should preserve balance and minimize main-draw byes. | I | GCR 11.3, 13 and 15 | rationale only, not proof of a guarantee |
| D4-02 | Ablaufgrafik / chronology diagram: draw publication → Monday deadline → promotions → team managers' meeting/seed movement → play. | S | §4 diagram; no separate clause | verify internal conflict with D4-03's deadline before draw |
| D4-03 | Gebührenfreie Frist / penalty-free deadline: Monday 23:59 BWF-HQ time (+08:00 GMT) before draw day. | D | GCR 14.1.2 | defer BWF deadline; verify chronology if cited |
| D4-04 | Verspätungsfolge / late consequence: every later withdrawal, regardless of reason, creates association financial penalties. | D | GCR 14.1.2 by paragraph association | defer; absolute wording requires checking exceptions |
| D4-05 | Reserve-Nachrücken / reserve promotion: fill qualifying vacancies, or main-draw vacancies without qualifying, with highest available reserve units. | S | GCR 13.1 | verify ordering, availability and actor |
| D4-06 | Antwortfrist >3 Tage / response window: more than three days before TM meeting gives 72 hours. | D | GCR 13.1.3 | defer exact international window; cross-ref generic D4-05 |
| D4-07 | Antwortfrist 3–2 Tage / response window: three to two days before meeting gives 48 hours. | D | GCR 13.1.3 | defer; endpoint meaning unresolved |
| D4-08 | Antwortfrist 2–1 Tage / response window: two to one days before meeting gives 24 hours. | D | GCR 13.1.3 | defer; endpoint meaning unresolved |
| D4-09 | Antwortfrist <24h / response window: within 24 hours, answer by TM meeting start. | D | GCR 13.1.3 | defer; exact deadline model not adopted |
| D4-10 | Seed Withdrawal Movement / seeded withdrawal adjustment: withdrawal between publication and TM meeting end is filled by the next-highest seeded unit. | S | GCR 11.3.2 | verify precise movement conditions, not arbitrary reseeding |
| D4-11 | Beispiel Rang 2 / example: seed 2 withdrawal is filled from seeds 3/4 in the lower half. | S | D4-10 example, no additional clause | verify exact draw table/qualifications |
| D4-12 | Beispiel Kaskade / example cascade: vacated 3/4 position filled from 5–8 in that quarter, final gap by highest unseeded main-draw unit. | S | D4-10 example | verify; unseeded replacement is not automatically a promotion |
| D4-13 | Einmaligkeit / one-time rule: seed movement runs exactly once per discipline during TM meeting. | S | GCR 11.3.1 | verify “exactly once” vs permitted once, time and scope |
| D4-14 | Spätere Absagen / subsequent withdrawals: after seed movement no further seeded advancement occurs. | S | GCR 11.3.1 by paragraph association | verify consequences; no automatic redraw inference |
| D4-15 | Qualifikationsaufstieg / qualifying promotion: until first main-draw match, vacancies go to highest remaining qualifiers who have not lost a qualifying match. | S | GCR 13.1.2 | verify eligibility, timing and ranking |
| D4-16 | Forced Withdrawal / forced withdrawal: withdrawal or retirement in one discipline excludes the athlete from every further discipline of the event. | S | GCR 13.6.1 | verify before any cross-discipline automation |
| D4-17 | Senioren-Ausnahme / seniors exception: BWF World Senior Championships excepted from D4-16. | D | GCR 13.6.1 by paragraph association | defer exact exception; confirms event sensitivity |
| D4-18 | Gruppen-Vorlauf / group pre-stage: some formats play groups before qualifying units enter a knockout main draw. | S | Transition to §5 | model distinct phases; no required group format |

## §5 — Gruppen und KO-Übergang / groups and knockout advancement

| ID | German term / English gloss and faithful source assertion | Class | Claimed authority | Handling |
| --- | --- | --- | --- | --- |
| D5-01 | Kombisystem / combined format: group plus knockout used at World Senior Championships, intended to guarantee minimum match counts. | S | GCR 16 and GCR 10.3 | verify event/conditions; distinguish purpose from guarantee after withdrawal |
| D5-02 | Verbandsduell zuerst / association match first: same-association players/pairs must play their match as the first group match. | S | GCR 16.5 | verify handling when more than two share association |
| D5-03 | Absprachenvermeidung / collusion prevention: D5-02 allegedly eliminates all scope for tactical collusion/distortion in later rounds. | I | Source rationale, no separate clause | unsupported absolute causal claim; no guarantee adopted |
| D5-04 | Fünfstufige Wertung / five-step order: tied units follow strict hierarchy described in D5-05–11. | S | GCR 16.2 | verify through existing #203 research; no duplicate standings feature |
| D5-05 | Siege / wins: higher number of group match wins ranks higher. | S | GCR 16.2 context | verify; reuse #203 |
| D5-06 | Direktes Duell / head-to-head: exactly two tied units use their direct match winner. | S | GCR 16.2 context | verify tied-subset semantics; reuse #203 |
| D5-07 | Satzdifferenz / game difference: three-plus ties use all-group games won minus lost; source typo “Sätze gewohnt”. | S | GCR 16.2 context | verify; preserve source typo in reference |
| D5-08 | Zweierrest nach Sätzen / two-unit remainder: when D5-07 leaves two tied, immediately apply head-to-head. | S | GCR 16.2 context | verify; reuse #203 |
| D5-09 | Punktdifferenz / point difference: unresolved three-plus ties use all-group points scored minus conceded. | S | GCR 16.2 context | verify; reuse #203 |
| D5-10 | Zweierrest nach Punkten / two-unit remainder: when D5-09 leaves two tied, use head-to-head. | S | GCR 16.2 context | verify; reuse #203 |
| D5-11 | Losentscheid / drawing lots: absolute three-plus equality after points is resolved by referee draw. | S | GCR 16.2 context | verify actor and recording; reuse #203 |
| D5-12 | Zwei-Gruppen-Sieger / two-group winners: A winner goes to knockout slot 1, B winner to slot 4. | S | GCR 10.3 | verify exact event and table |
| D5-13 | Zwei-Gruppen-Zweite / two-group runners-up: A runner-up to slot 3 and B runner-up to slot 2; source says placed/drawn. | S | GCR 10.3 | verify fixed vs drawn ambiguity |
| D5-14 | Vier-Gruppen-A/B / four-group A/B winners: A winner slot 1 and B winner slot 8. | S | GCR 10.3 | verify exact event/table |
| D5-15 | Vier-Gruppen-C/D / four-group C/D winners: draw onto slots 3 or 6. | S | GCR 10.3 | verify exact event/table |
| D5-16 | Vier-Gruppen-Zweite / four-group runners-up: draw into remaining slots 2, 4, 5 and 7. | S | GCR 10.3 | verify draw constraints |
| D5-17 | Keine Wiederholung / no immediate rematch: same preliminary-group opponents must not meet in first knockout round. | S | GCR 10.3 | verify hard constraint and relation to D5-14–16 |

## §6 — Praxis-Checkliste / athlete and coach checklist

| ID | German term / English gloss and faithful source assertion | Class | Claimed authority | Handling |
| --- | --- | --- | --- | --- |
| D6-01 | Melde-/Abmeldefrist / entry/withdrawal deadline: repeats Monday 23:59 BWF-HQ time before draw; late/no-show penalties. | D | GCR 14.1.2 and 13.7 | cross-ref D4-03–04; overlap #186 |
| D6-02 | No-Show-Gebühren / no-show fees: first event incident per player/pair “US 500”; later incidents in calendar year “US 1.000”. | D | GCR 13.7 | defer; source omits currency sign; overlap #186 |
| D6-03 | Massenabsage / mass withdrawal: association pays US$1,000 for ten-plus player withdrawals after deadline. | D | No separate clause in checklist | defer; overlap #186 |
| D6-04 | Promotionsbestätigung / promotion confirmation: accept within 72h/48h/24h/TM-meeting windows. | D | No new clause; repeats §4 | cross-ref D4-06–09; overlap #186 |
| D6-05 | Schweigen als Ablehnung / nonresponse rejection: no promotion response counts as rejection. | S | No clause | verify; model only if promotion workflow justified |
| D6-06 | Team-Manager-Teilnahme / manager attendance: official manager must attend TM meeting for Grades 1/2. | D | GCR 9.3.3 | defer |
| D6-07 | Aufgedruckter Nachname / printed surname: exact BWF-database match and at least two Latin letters. | I | GCR 23.3 | informational; overlap #184/#186, no software adoption |
| D6-08 | Namenshöhe / name height: player-name print 6–10cm; source calls it “Spielersuche-Name”. | I | GCR 23.2 | informational; preserve apparent typo; overlap #184/#186 |
| D6-09 | Länderhöhe / country-name height: country text 5cm. | I | GCR 23.2 | informational; overlap #184/#186 |
| D6-10 | Bekleidungsstrafe / apparel penalty: US$250 per incident and player. | D | GCR 20–24 | defer; overlap #186 |
| D6-11 | Werbeflächen / advertisement areas: at most five shirt areas, each at most 20cm². | I | GCR 24 | informational; overlap #184/#186 |
| D6-12 | Tabakwerbeverbot / tobacco advertising ban: tobacco and e-cigarette/vaping advertising forbidden. | I | GCR 24.5.5 | informational; overlap #184/#186 |
| D6-13 | Erster Aufruf / first call: move immediately toward court area. | I | No clause | informational conduct; overlap #184 |
| D6-14 | Zweiter Aufruf / second call: five minutes after first call. | S | No clause | verify national/regional/event authority; overlap #184 |
| D6-15 | Referee-Meldung / referee notice: two minutes after second call if players absent. | S | No clause | verify; overlap #184 |
| D6-16 | Walkover / walkover: five minutes after second call (normally three after referee notice), referee may award match; source equates it with disqualification. | S | No clause | verify discretion and distinguish statuses; overlap #184 |
| D6-17 | Ersatzschläger / spare racket: second ready racket compulsory; player may not leave court after string break to collect replacement. | I | No clause | informational; universal obligation unverified; overlap #184 |
| D6-18 | Materialübergabe / equipment delivery: outside official intervals, racket/towel/drink handover must be through referee, never directly by coach. | I | ITTO 3.5.16 | informational; overlap #184/#186 |
| D6-19 | Geräteverbot / device prohibition: smartphones, smartwatches and other communication devices forbidden at court and immediate surrounds. | I | ITTO 5.15.1 / Vocab 5.29 | informational; scope unverified; overlap #184/#186 |
| D6-20 | Gerätesanktion / device sanction: ringing/use by player is Law 16.6.4 misconduct, punished red under Law 16.7; prescribed phrase “You used your communication device”. | I | ITTO 5.15.1, Vocab 5.29, Laws 16.6.4/16.7 | informational; overlap #184/#186 |
| D6-21 | Feldgewöhnung / warm-up: exactly two minutes from umpire taking chair. | S | No clause | verify start/length; overlap #184 |
| D6-22 | Balltest / shuttle test: must finish within those same two minutes; example approved Yonex AS40. | I | No clause | informational; example does not establish model approval; overlap #184 |
| D6-23 | Coachanzahl / coach count: maximum two coaches/support staff per side at court. | S | ITTO 3.5.17 / GCR 20 | verify event applicability, not hardcoded club limit; overlap #184/#186 |
| D6-24 | Coachverhalten / coach conduct: sit in designated chairs, sporting clothes, no sandals/bare feet or electronic devices on coach seat. | I | ITTO 3.5.17 / GCR 20 paragraph context | informational; overlap #184/#186 |
| D6-25 | Coachwechsel / staff exchange: during game interval, when two present, one must fully leave court/surrounds before new coach/physio/doctor enters. | I | ITTO 3.5.17 / GCR 20 paragraph context | informational; overlap #184/#186 |
| D6-26 | Pausen / intervals: at most one minute at 11 points; at most two minutes between games. | S | Law 16.2 | verify event/scoring applicability; overlap #184 |
| D6-27 | Trikotwechsel / shirt change: on-court only during normal intervals. | I | ITTO 5.10.11 | informational; overlap #184/#186 |
| D6-28 | Verletzungszutritt / injury access: only referee and doctor at referee request may enter court. | I | ITTO 3.5 / 5.14.2 | informational; overlap #184/#186 |
| D6-29 | Behandlung / treatment: prohibited on court except wound care for flowing blood. | I | ITTO 3.5 / 5.14.2 | informational; no medical recommendation; overlap #184/#186 |
| D6-30 | Eisspray / ice spray: physio/doctor use on court strictly forbidden; diagnosis only; player/coach may use only in normal game intervals. | I | No additional clause, injury paragraph context | informational; no medical recommendation; overlap #184/#186 |

## Coverage and deduplication notes

The inventory has **108 rows** (12 + 11 + 20 + 18 + 17 + 30). Each source paragraph, table row, numbered/bulleted assertion and checklist item maps above. Table headings, section titles and connective sentences that only announce the next section add no independent rule and are not separately counted. The “fair/compliant” aspiration before §2 is D1-12, favorite-protection rationale is D2-11/D3-01, withdrawal-balance rationale is D4-01, collusion-elimination assertion is D5-03, and checklist prevention rhetoric is informational framing rather than evidence of guaranteed outcomes.

The same rule stated again in §6 points to its earlier canonical ID; it is not a second authority. Topics overlapping #184 (calls, warm-up, equipment, coaching, intervals, injury) and #186 (withdrawal/penalty/seeding and general compliance) retain their provenance here without becoming additional feature requirements. The exact rule texts, editions and the precise issue-to-issue correspondence must be recorded in the source verification, rather than inferred from this secondary guide.

All rows remain unverified **as source claims** at inventory creation. In particular, S is relevance, not acceptance; D is an explicit scope deferral, not a claim that a national analogue does not exist; I avoids an unrelated compliance implementation, not a validation of the advice. Primary-source verification may contradict, qualify or relocate a claimed clause while the stable IDs and preserved original remain unchanged.
