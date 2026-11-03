# Issue 183: source-statement trace for sections 4–6

This checklist traces the **unverified source claims** in the reference of [issue #183](https://github.com/wlambertz/rallyon/issues/183). It is not a specification or an independent verification of the alleged GCR/ITTO rules. Source IDs `S4-*`, `S5-*`, `S6-*` identify distinct claims below; `N*` and `I*` link them to `inventory.md`. **Skipped** means the individual claim is explicitly skipped from final classification for the reason given in its row; it is not a claim that the feature is absent. **International, deferred** means the specific international-only claim is deferred under the issue's scope. Related source details sharing an international-only capability point to one canonical deferral ID, not separate adopted rules.

The DBV comparisons already documented in `inventory.md` apply only to the cited DBV competition provisions and edition. The reference's numerical values, timers, penalties, protocol and alleged legal effects below are not nationally verified here. No source claim is promoted to product behavior by appearing in this trace. Potential conflicts against a verified DBV rule must be assessed for a specified competition before a final Conflicts classification.

## §4: operational tournament control

| Source ID | Source statement (English gloss) | Inventory | Status / reason |
| --- | --- | --- | --- |
| S4-001 | Echtzeitverarbeitung und verzögerungsarme Koordination (realtime integration and minimizing delay) | Q-01, N4-01 | Skipped — no measurable latency or failure contract; distinguish NFR from functional guarantees. |
| S4-002 | Zeit-Trigger überwachen Ruhezeiten und Ansetzungsfolgen (time triggers monitor rest and scheduling) | N4-01, N4-02 | Skipped — operational scheduler not established; policy enum is not implementation. |
| S4-003 | Nach Spielende 60 Minuten automatische Aufrufsperre (60-minute call block after a match) | N4-02 | Skipped — source-specific rule; DBV 30-minute entitlement comparison in inventory does not establish universal applicability. |
| S4-004 | Referee darf ausnahmsweise auf 30 Minuten verkürzen (referee may reduce to 30 minutes) | N4-02 | Skipped — national scope, referee authority and override behavior are unverified. |
| S4-005 | Nur neun genannte Reihenfolgen für Major-Events-Fünf-Spiele-Ties (nine permitted five-match Major Events sequences) | I27 | International, deferred — all nine source enumerations belong to this single Major Events constraint. Generic league order remains N4-03. |
| S4-006 | Drei Matches Abstand bei Einzel- und Doppeleinsatz im selben Tie (three intervening matches for singles/doubles) | I61, N4-17 | International, deferred — the specific Major Events rule; skipped — generic player-conflict scheduling needs its own national rule and bounded code search. |
| S4-007 | Erster Aufruf und Feldbetreten (first call and entry onto court) | N4-10, N4-14 | Skipped — status/transition and event authority need definition. |
| S4-008 | Schiedsrichter übernimmt Position nach Feldbetreten (umpire takes position) | N4-10 | Skipped — determine whether this operational step needs a system state. |
| S4-009 | Zwei Minuten Feldgewöhnung mit Balltest (two-minute warm-up and shuttle test) | N4-11, N1-25 | Skipped — verify applicability and timing; the brand is a reference example, not a global product default. |
| S4-010 | Zweiter Aufruf fünf Minuten nach dem ersten (second call after five minutes) | N4-14 | Skipped — national equivalence unverified. |
| S4-011 | Referee-Benachrichtigung zwei Minuten später bei Abwesenheit (notify referee two minutes later) | N4-15 | Skipped — national equivalence and responsibility unverified. |
| S4-012 | Nach weiteren fünf Minuten Disqualifikationsoption (option to disqualify after a further five minutes) | N4-16 | Skipped — this is an option, not mandatory automatic disqualification; compare applicable rules and authorized decision maker. |
| S4-013 | Hallenspezifische Feldzahl und Bauart erfassen (record venue-specific court count and hall configuration) | N1-03, N4-05 | Skipped — `Court.kt` models courts; the full hall condition and rule workflow are not established. |
| S4-014 | Deckenberührung: Wiederholung beim Aufschlag, Fehler im Spiel (ceiling-contact treatment) | N4-05 | Skipped — source examples are venue-specific; verify the applicable authority, serve/play distinctions and recording need. |
| S4-015 | Korbgestänge: Wiederholung (basketball-structure contact treated as let) | N4-05 | Skipped — the cited obstruction is venue-specific and no applicable national rule was verified. |
| S4-016 | Einspielen nur in definierten Nebenhallen (warm-up only in designated secondary hall) | N1-18, N1-22–23 | Skipped — venue-specific restriction and any system-enforcement or referee exception remain unverified. |
| S4-017 | Blitzlicht unterbricht nur bei offizieller Entscheidung (external disturbance and official discretion) | N4-05 | Skipped — verify venue and rule authority before specifying; whether a decision needs recording is open. |
| S4-018 | Eigenmächtige Unterbrechung wegen Verzögerung sanktionieren (player interruption may incur delay sanction) | N4-05, N5-02 | Skipped — applicable referee authority, consequences and incident-recording need are unverified. |
| S4-019 | Maximal zwei Coaches/Physios am Feld (two coaches/physios courtside) | N4-18 | Skipped — verify authority and per-side/event scope; overlap #184. |
| S4-020 | Wechsel erst nach vollständigem Verlassen durch bisherige Person (substitution after departure) | N4-18 | Skipped — the applicable rule and need for software recording of this human procedure are unresolved. |
| S4-021 | Coaching nur in genannten Pausen, mit 1-/2-Minuten-Dauer (coaching only in stated intervals) | N4-18 | Skipped — 11-point and interval timing is source-specific; check effective scoring rules and event. |
| S4-022 | Coaches tragen Sportkleidung und keine Badelatschen/barfuß (coach attire) | N4-19 | Skipped — informational versus software enforcement; rule authority unverified. |
| S4-023 | Coaches dürfen am Stuhl keine elektronischen Geräte nutzen (no electronic devices at coach chair) | N4-19 | Skipped — verify rule applicability and any incident-recording need. |
| S4-024 | Ausrüstung platzsparend ablegen (compact equipment storage) | N4-20 | Skipped — physical operating guidance, possible software relevance unresolved. |
| S4-025 | Übergabe außerhalb Pausen zwingend über Referee, nicht direkt (equipment delivered through referee) | N4-20 | Skipped — verify authority and distinguish official procedure from system behavior. |
| S4-026 | Pflicht-Ersatzschläger, da Feld bei Saitenriss nicht verlassen werden dürfe (mandatory spare racket) | N4-20 | Skipped — verify both asserted rule and system's suggested mandatory check. |
| S4-027 | Klingeln/Nutzung von Kommunikationselektronik als Vergehen (electronic-device infringement) | N4-21 | Skipped — verify rule, actor and applicability; do not infer a generic automatic sanction. |
| S4-028 | Rote Karte und offizielle Sprachformel bei Geräteverstoß (red-card consequence and announcement) | N4-21, N5-04 | Skipped — independent sanction and announcement claims need separate verification. |
| S4-029 | Referee geht zuerst zum Feld und befragt Umpire (referee first attends and consults umpire) | N4-27 | Skipped — applicability and a software need for logging this medical/officiating action are unresolved. |
| S4-030 | Referee ruft gegebenenfalls Turnierarzt (call tournament doctor if needed) | N4-27 | Skipped — the applicable procedure and need to record this human medical decision are unresolved. |
| S4-031 | Umpire stoppt Verzögerungszeit (umpire times delay) | N4-28 | Skipped — verify rule and possible recorded timestamp. |
| S4-032 | Behandlung auf Feld verboten, außer Blutung (on-court treatment restriction and bleeding exception) | N4-29 | Skipped — verify medical protocol and scope; no automated medical decision assumed. |
| S4-033 | Eisspray auf Feld verboten (ice spray prohibited) | N4-29 | Skipped — independent authority and software relevance unresolved. |
| S4-034 | Spiel ruht bis Blutung gestillt und Feld gereinigt ist (pause for bleeding and cleaning) | N4-30 | Skipped — national applicability and the need to represent this safety procedure as an interruption state remain unverified. |
| S4-035 | Trikotwechsel auf Feld nur während offizieller Pause (on-court shirt change only during an interval) | N4-23 | Skipped — verify current authority and event applicability. |
| S4-036 | Mannschaftskollegen bedienen Zähltafel ohne Entscheidungskompetenz (teammates operate scoreboard without decision authority) | N4-13 | Skipped — verify operator role, permissions and applicability. |
| S4-037 | Handys der Zähltafelbediener müssen ausgeschaltet sein (scoreboard operators switch off phones) | N4-13 | Skipped — national applicability and software relevance of this human instruction are unresolved. |
| S4-038 | Beide Spieler bestätigen vor Verlassen des Felds per Unterschrift (players sign result before leaving court) | N4-08 | Skipped — verify national requirement and whether a digital confirmation is acceptable; not automatically an electronic-signature mandate. |
| S4-039 | Service-Richter unterstützen Umpire und gehen bei Fehler/IRS zum Umpire (service judge supports corrections) | N4-24 | Skipped — applicability of this operational guidance and any correction-recording need are unverified. |
| S4-040 | IRS-Ende ohne Linienrichter: IN/OUT, GAME, Ergebnisansage (specified challenge ending call) | N4-25 | Skipped — exact source phrase is informational; no selected officiating workflow. |
| S4-041 | Fault in Pause: besondere Ansage (fault announcement during interval) | N4-25 | Skipped — verify authority and whether software should display it. |
| S4-042 | Männeruniform ohne gestellte Uniform (specified male official uniform) | N4-26 | Skipped — source explicitly cites GCR; whether a generic national equivalent applies is unresolved, and no rule is adopted. |
| S4-043 | Frauenuniform ohne gestellte Uniform (specified female official uniform) | N4-26 | Skipped — same limits as S4-042; verify source and product relevance separately. |
| S4-044 | Sponsoren-/Herstellerlogo höchstens 20 cm² (logo size cap) | N4-26 | Skipped — verify authority and scope; source value is not adopted. |
| S4-045 | Livebetrieb und Berichte lösen Finanz-/Disziplinarverfahren aus (derive financial and disciplinary processing from live records) | N5-01–03 | Skipped — national record needs and automation are unverified; BWF-only processing requires separate treatment. |

## §5: finance, sanctions and prize money

| Source ID | Source statement (English gloss) | Inventory | Status / reason |
| --- | --- | --- | --- |
| S5-001 | Automatische Finanz- und Sanktions-Engine nach BWF-Statuten (BWF financial/sanction engine) | I28–I29, I35–I39, I44–I45 | International, deferred — the BWF-specific engine and amounts. A configurable national fee-information need is separate under N5-01. |
| S5-002 | Gebührenbescheid nach BWF-HQ-Abmeldefrist (charge after BWF withdrawal deadline) | I31, I28 | International, deferred — BWF-specific deadline and penalty; generic withdrawal and fees remain N5-01. |
| S5-003 | No-show: erster Vorfall 500 USD, weitere 1.000 USD (first/subsequent no-show penalties) | I28 | International, deferred — the two amounts are variants of the one BWF no-show penalty claim. |
| S5-004 | Zehn oder mehr Rückzüge eines Verbandes: 1.000 USD und Disziplinarprüfung (mass-withdrawal penalty) | I44 | International, deferred — international association-level sanction. |
| S5-005 | Bekleidungsverstoß: 250 USD (clothing violation fine) | I45 | International, deferred — BWF-specific dollar sanction. |
| S5-006 | Grade 1: alle Rückzugsgebühren an BWF (Grade 1 fee split) | I35 | International, deferred — one grade-specific variant of BWF fee distribution. |
| S5-007 | Grade 2: 50 USD an BWF, Rest an Ausrichter (Grade 2 fee split) | I35 | International, deferred — another variant of the same BWF fee distribution. |
| S5-008 | Grade 3: je 50 USD an BWF, Kontinentalverband und Ausrichter; Host-Ausnahme 50/50 (Grade 3 fee split and host exception) | I35 | International, deferred — grade-specific distribution; source does not establish a national fee rule. |
| S5-009 | Verband nach 60 Tagen Zahlungsverzug sperren (association suspension after 60 days) | I36 | International, deferred — BWF-specific process; no national rule inferred. |
| S5-010 | Gelbe Karten erfassen (record yellow cards) | N5-04 | Skipped — national recording need and authority unresolved. |
| S5-011 | Rote Karten erfassen (record red cards) | N5-04 | Skipped — consequence differs from a yellow card and needs an applicable rule. |
| S5-012 | Schwarze Karten erfassen (record black cards) | N5-05 | Skipped — assess referee/organizer workflow and verification. |
| S5-013 | Schwarze Karte: sofortiger Ausschluss aus gesamtem Turnier (black-card disqualification across tournament) | N5-05 | Skipped — source states an automatic consequence; DBV Anlage III §§ 39–40 are separately discussed in inventory but do not establish all scenarios. |
| S5-014 | Bei Team-Events Ausschluss aus allen verbleibenden Spielen und Ties (team-event black-card consequences) | N5-05 | Skipped — verify applicable league rule and team effects separately. |
| S5-015 | Referee reicht offiziellen Bericht binnen zwei Wochen ein (referee report within two weeks) | N5-03, N5-06 | Skipped — report and deadline require separate national verification; no deadline adopted. |
| S5-016 | Preisgeld/Weltranglistenpunkte nur bei bestätigtem Ergebnis (confirmed-result eligibility) | I29 | International, deferred — source couples this to BWF prize-money and world-ranking processing. |
| S5-017 | Erstes Freilos zählt nicht als bestätigtes Ergebnis (first-round bye excluded) | I29 | International, deferred — subcondition of BWF eligibility. |
| S5-018 | Walkover-Sieg und Aufgabe-Niederlage gelten als bestätigt (walkover and retirement treatment) | I29 | International, deferred — subconditions of BWF eligibility; match-result status remains a distinct national design question. |
| S5-019 | Ab 75.000 USD / Grade 2 Preisgeld binnen zwei Wochen an BWF/CC (prize-money remittance) | I37 | International, deferred — international financial deadline. |
| S5-020 | Bei Verzug fünf Prozent pro Jahr, wöchentlich berechnet (late-payment interest) | I38 | International, deferred — international financial calculation. |
| S5-021 | Quellensteuerbescheinigungen binnen neun Monaten, sonst künftige Sanktionierung gesperrt (withholding certificates and sanctioning) | I39 | International, deferred — international-specific tax/sanction process; no generalized legal conclusion. |

## §6: results, standings and reporting

| Source ID | Source statement (English gloss) | Inventory | Status / reason |
| --- | --- | --- | --- |
| S6-001 | Ergebnisse korrekt aggregieren für Wertung und Berichte (accurate results aggregation) | N6-04, Q-02 | Skipped — define accuracy and failure behavior for an actual selected workflow; BWF world-ranking eligibility is I29. |
| S6-002 | Gruppenrangliste zuerst nach gewonnenen Spielen/Ties (standings by wins first) | N6-06 | Skipped — national competition-specific applicability; #203 refines a selected group rule. |
| S6-003 | Bei genau zwei Gleichstehenden direktes Duell vor Differenzen (head-to-head for two tied entrants) | N6-13 | Skipped — conflicts with sequence in the cited DBV pool provision if that provision applies; not a universal national conflict. |
| S6-004 | Bei drei oder mehr Gleichstehenden Satzdifferenz (set difference for three or more tied) | N6-07, N6-14 | Skipped — verify applicability and complete tie-handling semantics. |
| S6-005 | Nach Satzdifferenz Rückkehr zum direkten Duell, wenn zwei gleich bleiben (head-to-head when two remain tied after sets) | N6-14 | Skipped — distinct transition, not established as a universal DBV rule. |
| S6-006 | Danach Punktdifferenz (point difference as next criterion) | N6-07 | Skipped — confirm selected competition and applicable rule edition. |
| S6-007 | Nach Punktdifferenz Rückkehr zum direkten Duell, wenn zwei gleich bleiben (head-to-head when two remain tied after points) | N6-14 | Skipped — separate transition requiring verification. |
| S6-008 | Danach Losverfahren (drawing lots) | N6-08 | Skipped — authority, trigger and recorded lot outcome need specification. |
| S6-009 | Einzel/Paar tritt nicht an oder gibt vor Ende aller Gruppenspiele auf: alle dessen Resultate aus Wertung nehmen (exclude all its results from standings) | N6-09 | Skipped — distinguish exclusion from derived calculation from deletion of recorded match history; youth DBV provision cited in inventory has defined applicability. |
| S6-010 | Mannschafts-Tie: abgebrochenes Spiel mit verbleibenden Punkten für Gegner auffüllen (fill remaining points for opposing side) | N6-10 | Skipped — competition-specific disqualification/injury treatment needs separate verification. |
| S6-011 | Alle Ergebnisse täglich an BWF übermitteln (daily BWF transmission) | I30 | International, deferred — BWF-specific delivery; national export is separate S6-012. |
| S6-012 | Alle Ergebnisse täglich im vorgegebenen Format an dbv.turnier.de übermitteln (daily national results export) | N6-11 | Skipped — interface, permission, format and national reporting duty are unverified. |
| S6-013 | Gesamt-Export spätestens 24 Stunden nach Turnierende für BWF-Weltrangliste (24-hour final export for BWF ranking) | I53 | International, deferred — international ranking-specific deadline. |
| S6-014 | Bei technischer Störung aktualisierte Tableaus als PDF zur BWF-Übermittlung (PDF fallback for BWF submission) | I54 | International, deferred — BWF-specific fallback; generic PDF output remains S6-015. |
| S6-015 | Druckfähige aktuelle Tableaus für Halle, Pult und Medien (printable current draws for venue and media) | N6-05, N6-12 | Skipped — candidate Organizer/Public Information output, applicability and data ownership unresolved. |

## Explicit mapping gaps and review limits

- Detailed IDs S4-001–045, S5-001–021 and S6-001–015 provide traceability to existing inventory concepts. Several map to a shared broad `N` ID; they must be split into distinct inventory classifications if their final Covered / Conflicts / Gap / Not applicable outcomes differ. Reusing an ID here does not assert that the existing row has a final classification.
- The source provides nine concrete Major Events orders in S4-005, but each is a value in one exclusively international rule set, not nine national requirements. The source's international financial table likewise contains variants of canonical I28 and I35. This checklist references each deferral concept once per source statement without redefining or duplicating an international feature's disposition.
- Source statements S4-036 and S4-037 were previously coalesced in N4-13; S4-019–023 in N4-18–19; and S4-039–044 in N4-24–26. Their software relevance and authority differ and need independent individual decisions. No absence conclusion is made solely from the absence of a named method.
- Section 6 mentions global ranking-point outcomes: only the BWF-specific reporting/eligibility aspects are deferred under I29 and I30; the core national and club standings algorithms remain in scope and unresolved.
- Re-run an independent source-to-statement coverage check against the exact live issue revision after integration. This trace is maker-produced evidence, not an independent audit, and it does not certify sections 1–3, classify every statement finally, or complete #183.
