# Issue 183: source-statement trace for sections 1–3

Maker: `/root/section_1_3`. Source: the complete German **Reference** in [issue #183](https://github.com/wlambertz/rallyon/issues/183), reviewed by subsection and list/table row. This checklist traces the source, not verified requirements. The source text is a generated secondary document; dates, rules, values and legal claims require verification. `N…` and `Q…` point to the provisional classification and evidence in [inventory.md](inventory.md) or [analysis.md](analysis.md). `I…` is the single international-deferred inventory entry; multiple trace rows may point to the same entry when they split one source-specific mechanism. National/generic analogues are separate N entries and are not asserted to share international rules.

`U` means an unresolved classification or missing sufficiently specific existing ID; it is explicitly not counted as Covered or Gap. A skipped statement has no confirmed final issue classification. Where code establishes a narrower representation, it is identified separately and does not establish the full workflow. This is a reason for deferral, not a claim that the implementation is entirely absent. International-only rows marked deferred are excluded under the issue's stated scope. Source examples do not establish applicability to a national or club event. Reconcile this file with independent review before claiming exhaustive completion.

## §1 — master data and prospectus

| Source location and distinct claim (English gloss) | Trace and disposition |
| --- | --- |
| §1 introduction: structured tournament master data and configurable national rules | **Skipped:** the source does not define which national rules and tournament fields are mandatory for a particular event. N1-01, Q-02; scope and selected authority open. |
| §1 introduction: validation prevents entry conflicts and supports downstream operations | **Skipped:** entry-conflict conditions and the participating systems have not been identified. Q-02; U: testable integrity conditions and actual data boundaries have not been defined. |
| §1 introduction: legal protection and audit-safe data | **Skipped:** no authority supports the claimed legal guarantee or defines the required audit evidence. Q-02; U: legal effect and auditable evidence are unsubstantiated, and are not accepted as guaranteed outcomes. |
| Master data: expandable tournament category linked to world points and sanctions | **International, deferred:** the source-specific BWF claim. **National analogue skipped:** `Category.kt` identifies disciplines; a national event-grade model and target event have not been identified. N1-01 for generic classification; I8 for sanction-specific logic and I46 for world points, both international deferred. `Category.kt` models discipline types, not event grade. |
| Grade 1 list: Thomas, Uber, Sudirman, Suhandinata, Eye Level, BWF World and World Senior championships | **International, deferred.** I1, international deferred; event-name examples do not independently establish national requirements. |
| Grade 2: World Tour Finals and levels 2–6 | **International, deferred.** I2, international deferred. |
| Grade 3: continental International Challenge, International Series and Future Series | **International, deferred.** I3, international deferred. |
| BWF junior continental and international tiers | **International, deferred.** I4, international deferred. |
| AirBadminton worldwide, continental, open and national variants | **International, deferred.** I5, explicitly excluded by issue scope, including national variants. |
| National championships | **Skipped:** no championship-specific behavior has been identified; the searched configuration types do not establish full coverage. N1-04; determine event classification need. |
| National and regional team leagues | **Skipped:** no league-season or team-tie implementation was established; a doubles team ID is not proof of a league model. N1-05; team league is not established by doubles-team IDs. |
| International/BWF-sanctioned team leagues, including national leagues with international participation | **International, deferred.** I6, international deferred. |
| Senior events | **Skipped:** the relevant national competition and its applicable eligibility rules have not been selected. N1-06; age eligibility additionally N2-10. |
| Invitational events | **Skipped:** `RegistrationPolicy.INVITE_ONLY` represents a policy value; no invitation issuance or acceptance workflow has been established. N1-02; the policy enum is represented in `setup/configuration/api/RegistrationPolicy.kt`; issuance and acceptance are not established. |
| Federation hierarchy enforced as rights/roles matrix | **International, deferred.** I7, international deferred; this does not change existing IAM policy. |
| Grade 2 sanction fee percentage and upper prize-money cap | **International, deferred.** I8, international deferred; formula not adopted. |
| Grade 3 sanction fees set by continental body | **International, deferred.** I62, separate authority and mechanism. |
| Cancellation, postponement or prize reduction under 120-day threshold and fee | **International, deferred.** I9, international deferred. |
| Cancellation/change under 15-day threshold triggers BWF referral and possible sanctions | **International, deferred.** I63. |
| Force-majeure exception to cancellation sanctions | **International, deferred.** I64. |
| Prospectus generation | **Skipped:** the source does not establish the field requirements for the targeted national or club event, and a complete generation workflow has not been found. N1-07; applicable national prospectus conditions need primary-source verification. |
| Prospectus checklist and blocked publication while mandatory fields are incomplete | **Skipped:** the source gives a BWF checklist but no validated national checklist, and the bounded search did not establish a publication gate. N1-19; no universal BWF checklist is assumed for national events. |
| Organizer contact name, phone and email | **Skipped:** `Venue.kt` covers venue details rather than organizer contact; no separate organizer-contact workflow has been established. N1-08; venue representation alone does not establish organizer contact. |
| Venue name and full physical address | **Skipped:** `setup/configuration/api/Venue.kt` represents a venue name and address, but whether these satisfy the applicable event prospectus requirement is unverified. N1-03 and N1-08; venue fields are represented in `setup/configuration/api/Venue.kt`; the complete publication workflow is not established. |
| Number of courts and venue/court characteristics | **Skipped:** `setup/configuration/api/Court.kt` represents individual courts, but it does not establish the source's hall-specific publication conditions. N1-03, N1-09; partial court model, scope-specific conditions open. |
| Ceiling contact: replay on serve and fault during play | **Skipped:** the national or venue rule for ceiling contact and its relevance to software have not been verified. N1-09, U: exact national/venue applicability and whether system behavior is needed are unverified. |
| Other hall structures: replay on serve and during play | **Skipped:** the venue-specific rule for non-ceiling obstructions and its relevance to software have not been verified. N1-09, U: individual hall-condition claim not separately represented; verify applicable rule. |
| Record secondary halls, warm-up capacity and ceiling height | **Skipped:** `Venue.kt` and `Court.kt` do not establish a secondary-hall or ceiling-height model; a broader search is needed. N1-18; current court list does not establish separate warm-up facilities. |
| Prohibit warming up in foyer or corridors | **Skipped:** the authority for the stated foyer and corridor restriction is unidentified and its software relevance is unresolved. N1-22; facility-specific authority and system relevance open. |
| Prohibit use of main courts for warm-up except explicit referee/organizer authorization | **Skipped:** the authority and the responsible person for the proposed warm-up authorization have not been established. N1-23; authorization and exception handling not established. |
| Entry closing date | **Skipped:** `Tournament.kt` stores `registrationWindows`; the source does not establish an automatic closure workflow or its absence. N1-15; `setup/configuration/api/Tournament.kt` exposes registration windows; no automatic closure is established. |
| M&Q release date | **International, deferred:** the source-specific BWF claim. **National analogue skipped:** whether national competitions require an equivalent report and release date has not been verified. I12, international deferred; N1-10 remains the generic date candidate. |
| Draw date | **Skipped:** the bounded search has not established a distinct stored draw date or the applicable deadline rule. N1-16. |
| Monday withdrawal deadline in BWF HQ time | **International, deferred.** I31, international deferred; no national deadline inferred. |
| World ranking cutoffs for M&Q and seeding | **International, deferred:** the source-specific BWF claim. **National analogue skipped:** the national ranking source and cutoff for a chosen competition have not been identified. I32, international deferred; generic national ranking reference date N1-17. |
| Announce exclusive shuttle model | **Skipped:** which shuttle models are mandated or optional for the selected event has not been verified. N1-20; permitted models and exclusivity need applicable source. |
| State who supplies shuttles | **Skipped:** the bounded search did not establish a shuttle-supplier field or a complete announcement workflow. N1-20; distinct from the model field. |
| Provide used shuttle boxes courtside | **Skipped:** the source has not established whether courtside box placement is a national rule or a software responsibility. N1-24; software relevance and national applicability open. |
| Shuttle test in the two-minute court familiarization | **Skipped:** the source does not establish whether the two-minute timing applies to the target national competition. N1-25; timing is unverified nationally. |
| Entry fee and payment method | **Skipped:** the bounded search has not established a fee or payment workflow, and applicable national requirements have not been selected. N1-12; fee information and payments are separate decisions. |
| Announce prize total and allocation | **International, deferred:** the source-specific BWF claim. **National analogue skipped:** the applicable national prize-information requirements and output workflow have not been established. N1-21 for generic prize information; I11 for international-specific mandatory USD and tax conditions. |
| Withholding tax information | **International, deferred.** I11, international deferred; any national tax information needs a separate verified legal assessment. |
| Hotel booking and payment | **Skipped:** the need for organizer-managed accommodation or its boundary with external booking systems has not been established. N1-13; optional use and scope unresolved. |
| Transport from arrival location and between hotels and venue | **Skipped:** the need for transport coordination in the target event and any existing workflow have not been established. N1-13; distinct services, optional scope unresolved. |
| Visa-support letter process | **International, deferred.** I10, international deferred. |
| Reference apparel and advertising rules, including named prohibited ads | **International, deferred:** the source-specific BWF claim. **National analogue skipped:** the applicable national advertising rules and need for software enforcement have not been established. N1-14 for generic rules references; I47 for BWF ad restrictions, international deferred; national applicability unverified. |
| Referee contact details | **Skipped:** the bounded search did not establish published referee-contact fields or the intended visibility of those details. N1-14; distinguish published contact from internal role assignment. |

## §2 — integrations, entries and eligibility

| Source location and distinct claim (English gloss) | Trace and disposition |
| --- | --- |
| Introduction: realtime low-latency bidirectional federation synchronization | **Skipped:** the reference provides no numerical latency, update frequency or connection-failure criterion. Q-01; measurable latency, transport, failure handling and interface availability are unspecified. |
| Introduction: integrity, automated eligibility verification and elimination of transfer errors | **Skipped:** the reference does not define verification sources, failure behavior or measurable data-integrity criteria. Q-01 and Q-02; U: absolutes are unsupported and do not specify achievable integrity semantics. |
| Bidirectional REST API for federation exchange | **Skipped:** the national federation interface, data formats, access agreement and availability have not been verified. N2-02–03, Q-01; actual national interface, format and agreement unverified. |
| Import player master data and national member IDs | **Skipped:** `ParticipantsRoster.kt` stores IDs but does not establish a national player-data import or identity match. N2-02, N2-06; `setup/configuration/api/ParticipantsRoster.kt` exposes ID lists; federation import is not established. |
| Import association membership | **Skipped:** the national source of association membership, access agreement and identifier mapping are unknown. N2-02, N2-06; source ownership and identity mapping unresolved. |
| Import daily national ranking values | **Skipped:** an accessible national ranking source, refresh policy and applicable ranking date have not been verified. N2-02, N1-17; current national data access unknown. |
| Import BWF ID and current world/junior rankings | **International, deferred.** I48, international deferred. |
| Acknowledge BWF online entry | **International, deferred:** the source-specific BWF claim. **National analogue skipped:** whether the intended national entry workflow supports acknowledgement has not been established. I13, international deferred; generic acknowledgement is N2-11. |
| Alert federation when acknowledgement missing by stated Wednesday BWF deadline | **International, deferred.** I49. |
| Accept corrections/objections until stated Thursday BWF deadline | **International, deferred:** the source-specific BWF claim. **National analogue skipped:** the national correction deadline and responsible decision-maker have not been identified. I14, international deferred; generic correction N2-07. |
| Lock entry data after BWF correction deadline | **International, deferred:** the source-specific BWF claim. **National analogue skipped:** the national lock condition, responsible actor and reopening behavior have not been identified. I55, international deferred; generic locking N2-07. |
| Export national live results, match reports and entry lists in required format | **Skipped:** the target national system's supported export format, interface, authentication and encryption requirements are unknown. N2-03, N6-11, Q-01; interface and encryption requirements unverified. |
| Export equivalent data to BWF servers | **International, deferred.** I65; the daily delivery requirement is separately I30. |
| Generic rules engine to evaluate eligibility | **Skipped:** the source asserts a technical rules-engine solution without establishing a concrete national eligibility workflow. N2-01, N2-04; U: rule-engine architecture is an asserted solution, not an established product requirement. |
| BWF jurisdiction after relocation, 12-month default and acceptance exceptions | **International, deferred.** I15, international deferred. |
| BWF transfer form and three-month suspension if signature refused | **International, deferred.** I40, international deferred. |
| BWF contract-based ineligibility up to 12 months | **International, deferred.** I41, international deferred. |
| BWF deregistered-player return notification | **International, deferred.** I16, international deferred. |
| BWF top-200 in prior five years and three-month lead time | **International, deferred.** I50. |
| Junior U19 calendar-year eligibility | **Skipped:** the cited DBV season cutoff differs from the source's tournament-calendar-year rule; the specific competition has not been selected. N2-09; source's calendar-year test differs from the official DBV season cutoff cited in `inventory.md`. |
| Senior 35+, 40+, 45+ minimum ages in calendar year | **Skipped:** the applicable competition, senior-age cutoff and exceptions have not been selected or verified. N2-10; national cutoff and event exceptions require source-specific validation. |
| International representation triggered by selected team/individual appearances | **International, deferred.** I17, international deferred. |
| International federation change: prior representation and passport/registration duration conditions | **International, deferred.** I51. |
| Neutral BWF nationality for suspended federations, stateless persons or refugees | **International, deferred.** I52. |
| General personal-data handling and consent | **Skipped:** the concrete data flow, processing purpose, lawful basis and retention period have not been assessed. N2-05; legal basis, minimization and retention must be assessed for actual workflow. |
| Perpetual BWF rights assignment and waiver of moral rights | **International, deferred.** I18 covers assignment and I58 covers the separate waiver; neither is a national consent model. |
| Member association responsibility for consent before data entry | **International, deferred.** I59; no GDPR conclusion follows from this source. |
| WADA/NADO and betting/manipulation integrations | **International, deferred.** I19 covers anti-doping; I60 covers betting/manipulation monitoring. |
| Signed BWF Players Undertaking, personal contact fields and top-100 eligibility lock | **International, deferred.** I20, international deferred. |
| Generate main draw, qualifying and reserve lists | **International, deferred:** the source-specific BWF claim. **National analogue skipped:** the target national competition and its entry-list selection and reserve rules have not been established. N2-08; BWF-specific M&Q rules I23. |
| Host wildcard eligibility by BWF grade and representation | **International, deferred.** I21, international deferred. |
| Suggest highest-ranked host entry, including a pair with one local partner | **International, deferred.** I42, international deferred. |
| Confirm wildcard before first M&Q publication | **International, deferred.** I56. |
| Award wildcard ranking points only after a win; exclude walkover from a win | **International, deferred.** I43, international deferred. |
| Transfer verified entries into draw/seeding | **Skipped:** the identity, eligibility and draw ownership boundaries and corresponding workflow have not been established. N2-04, N3-01; boundary and eligibility authority remain open. |

## §3 — seeding, draw and bracket changes

| Source location and distinct claim (English gloss) | Trace and disposition |
| --- | --- |
| Introduction: deterministic accuracy of draw/seeding | **Skipped:** the source does not define required inputs, random-lot recording or measurable reproducibility and correctness criteria. Q-01/Q-02, U: reproducibility, randomness and independent verification require an explicit target rule; absolute correctness is not demonstrated. |
| Notional ranking from each partner's highest world points with 80% formula | **International, deferred.** I22, international deferred. |
| Team-event notional status after stated three-month and played-match condition | **International, deferred.** I22, international deferred. |
| Adjusted world points for 1–5 events, factor 10/5 | **International, deferred.** I33, international deferred. |
| Adjusted world points for 6 or 7 events, factors 10/6 or 10/7 | **International, deferred.** I33, international deferred. |
| Limit notional/adjusted seeds to fifth place except insufficient actual ranked pairs | **International, deferred.** I34, international deferred. |
| Number of seeds by entrant counts, including below 16 and 128+ | **Skipped:** DBV Anlage IV Teil 2 §3 differs from the source for specified field sizes; the target competition and its exceptions are not selected. N3-03; DBV provision differs for specific field sizes and exceptions. Do not assume source BWF table is nationally applicable. |
| Power-of-two brackets for 4 through 128 | **Skipped:** DBV Anlage IV Teil 2 §2 describes knockout draw sizes; applicability to the selected event and zero/one-entry cases remain unresolved. N3-08; applicability and small fields open. |
| Byes equal next full draw less entrants | **Skipped:** DBV Anlage IV Teil 2 §2 supports the formula for its knockout format, but no applicable target event or running draw algorithm has been established. N3-09; DBV Anlage IV Teil 2 §2 supports this for its KO format only. |
| Place first seed at top and second at bottom | **Skipped:** the target competition and the governing seed-placement provision have not been verified. N3-10; applicable draw needs verification. |
| Draw seeds 3–4 into remaining quarters | **Skipped:** the applicable national draw rule and seed-placement behavior have not been verified. N3-11. |
| Draw seeds 5–8 and 9–16 into corresponding eighths and sixteenths | **Skipped:** the applicable national draw rule for these seed groups and its implementation have not been verified. N3-11. |
| Draw lots for equal ranking points or ties across seed groups | **Skipped:** the applicable tie-breaking source and how a lot outcome is recorded have not been established. N3-17; reproducibility and authority open. |
| Fill remaining seeded positions from M&Q ranking order | **International, deferred.** I23, international deferred. |
| Place highest-ranked two same-nationality entries in different halves in Grade 3 | **International, deferred.** I24, international deferred; no generic national separation rule inferred. |
| Avoid first-round same-association matches where possible | **International, deferred:** the source-specific BWF claim. **National analogue skipped:** whether club or association separation applies to a target national draw has not been verified. I24, international deferred as Grade 3 assertion; N3-18 records the generic national analogue as an unresolved analogue. |
| Group scheduling for groups of 3–7 | **Skipped:** the reference asserts exact orders but supplies examples only for 3 and 4; rules for other group sizes are missing. N3-06; the source asserts an exact sequence but lists examples only for sizes 3 and 4. |
| Three-entry order 1–3, 2–3, 1–2 | **Skipped:** the governing national competition and applicability of this exact fixture order have not been verified. N3-12; national applicability unverified. |
| Four-entry order 1–4, 2–3, 1–3, 2–4, 1–2, 3–4 | **Skipped:** the governing national competition and applicability of this exact fixture order have not been verified. N3-13; national applicability unverified. |
| Two-group winner/runner-up positions in knockout bracket | **Skipped:** the cited World Seniors layout has no verified national equivalent for the target competition. N3-14; source explicitly cites World Seniors, and national applicability is unresolved rather than assumed. |
| Four-group winner positions, including draw for C/D | **Skipped:** the cited World Seniors layout and the C/D draw have no verified national equivalent for the target competition. N3-15; World Seniors pattern is not a general rule. |
| Four-group runners-up positions and prevention of same-group first-round rematches | **Skipped:** the cited World Seniors position and rematch restrictions have no verified national equivalent for the target competition. N3-15; authority and exceptions open. |
| Advance seeds on withdrawal before team managers' meeting ends | **International, deferred:** the source-specific BWF claim. **National analogue skipped:** the target national competition and its seed-withdrawal consequences have not been selected. I25, international deferred; generic change handling N3-07. |
| Lock repeated seed movement within the same discipline after one execution | **International, deferred.** I57. |
| Reserve acceptance: more than three days before meeting, 72 hours | **International, deferred:** the source-specific BWF claim. **National analogue skipped:** the target national competition and its reserve-promotion acceptance deadline have not been established. I26, international deferred; generic promotion N3-07. |
| Reserve acceptance: three to two days, 48 hours | **International, deferred.** I26, international deferred. |
| Reserve acceptance: two to one days, 24 hours | **International, deferred.** I26, international deferred. |
| Reserve acceptance: under 24 hours, no later than meeting | **International, deferred.** I26, international deferred. |
| Redraw only before discipline begins for input error or severe imbalance | **Skipped:** the target competition's redraw authority, allowed triggers and cutoff have not been verified. N3-16; source's BWF condition is not established nationally. |
| Validated draw handed to court operations | **Skipped:** the ownership, accepted draw-state transition and operational scheduling contract have not been defined. N3-02, N4-01; phase boundary and actual live scheduling workflow undefined. |

## Specific follow-up for the integrating maker

- The mapping is a detailed checklist, not a completed final issue classification; grouped `G/P` rows still need claim-level decisions, evidence and prioritization.
- I46–I48 and N3-18 address the initially missing IDs for world-point linkage, BWF advertising, world-ranking import and generic association separation; independent review is still required.
- Cross-check source-specific World Senior advancement versus generic N3-14–15; do not silently relabel World Senior rules national.
- Split Q-01/Q-02 if needed for latency, transfer security, auditability, determinism, legal protection and correctness claims; no source assertion proves those guarantees.
