# Findings: registration and invitations

## Recommendation

Refine a bounded organizer-managed entry workflow before player self-registration or invitation automation. Treat an entry and its disposition separately from the roster used for a draw, and represent doubles membership explicitly rather than inferring it from a team ID. This is a recommendation for a separately approved feature, not an adopted model. The concrete case supports investigating these choices but does not establish demand, identity ownership or invite-only requirements. No production changes are made.

## Concrete case and source limits

The [TuS Bommern notice](https://tus-bommern.de/news/badminton/einladung-zur-jahreshauptversammlung-der-badmintonabteilung-2027.html), dated 17 September 2026, describes the 24–25 October Wittener Stadtmeisterschaften. It accepts entries by email or Turnier.de and requests pairing, club, playing class, player IDs and telephone information. It announces a deadline and says detailed starting times follow later. These are organizer-published instructions, not a verified general rule or a privacy template.

The linked [event page](https://dbv.turnier.de/tournament/A046428D-B07E-46D8-8B94-41EF1DDD96F6), inspected 11 November 2026, displays an online-entry completeness condition requiring both members of doubles/mixed pairs to enter. It also displays entry opening, entry deadline and withdrawal deadline information. These are observations of the published page, not proof of actual processing, historical changes, capacity availability today or a universal federation procedure. Following its individual-entry link redirected to a login page; no account was created and no entry submitted. The authenticated confirmation/correction flow was not inspected. The regulations link could not be fetched, and the PDF link used in #212 was not accessible through the browsing tool in this run; no new claim relies on its unavailable text.

[NRW Turnierordnung annex 10](https://www.badminton.nrw/fileadmin/Dateien/Broschueren-Hefte/Satzung-Ordnungen/2026/03_TO_Anlage_10.pdf), section 4.1, lists participant group, entry deadline/address and fees among announcement content. Its JWS-specific section 3 describes starter/reserve/exclusion lists; it does not establish those procedures for the Bommern case. Applicability, actual permits, classification and any exception must be confirmed before adoption. The case's invitation to participate is not evidence of an invite-only admission policy. Overlap with #186 remains reference research, not completed verification of its international source claims.

## Current implementation evidence

Code paths in this table are relative to `service/tournamentmgmt/src/main/kotlin/dev/wlambertz/rallyon/tournamentmgmt/setup/configuration/`.

| Finding | Concrete source | Limit |
| --- | --- | --- |
| Roster input distinguishes player-ID and team-ID lists. | `api/ParticipantsRoster.kt`, `internal/tournament/domain/ParticipantsRoster.kt` | No pairing membership, account binding or confirmation follows from a team ID. |
| Draft replacement validates exclusivity, positive/nonduplicate roster IDs and singles/doubles bracket compatibility. | `internal/tournament/domain/DraftReplacement.kt`, functions `validateRoster`, `validateIds`, `validateBracketRosters` | These checks do not verify an external player's identity or willingness to participate. |
| Whole-draft replacement is implemented, including roster configuration; it preserves stored creator and organizer metadata and requires a current version and DRAFT status. | `DraftReplacement.kt`; `openspec/specs/tournament-draft-update/spec.md` | Authentication-only access does not establish organizer ownership or authorize acting for a player. |
| Named entry/lifecycle operations are stubs. | `internal/ConfigurationServiceImpl.kt`: `openRegistration`, `closeRegistration`, `lockConfiguration`, `setParticipantsRoster`, `setBracketRoster`, `addParticipant`, `removeParticipant` | These methods throw `UnsupportedOperationException`; this is not a claim that no other entry system exists anywhere. |
| OPEN/INVITE_ONLY are configuration values; tournament lifecycle has REGISTRATION_OPEN but no separate entry state machine. | `api/RegistrationPolicy.kt`, `api/TournamentStatus.kt` | Neither enum establishes invitation issuance, acceptance, eligibility or access control. |
| A persisted participant row records player/team references and insertion audit information. | `internal/tournament/persistence/entity/ParticipantEntity.kt` | It is not evidence of a verified player directory, pending entry or invitation lifecycle. |
| Announcement publication is independent and creator-controlled. | `api/AnnouncementService.kt`, `internal/tournament/usecase/AnnouncementUseCase.kt`, ADR 0005 | A public announcement is not registration opening or permission to enter. |

The [context map](../../../docs/architecture/context-map.md) proposes Player Management upstream of Tournament Management, but that service is a placeholder. The [glossary](../../../docs/architecture/glossary.md) explicitly flags participant versus player as unresolved. A Keycloak `rallyon_user_id`, an organizer ID, an external player ID and a pair/team ID must not be equated without an explicit mapping and authority decision.

## Actors, states and candidate scenarios

The following are product alternatives for refinement, not observed organizer interviews or accepted requirements.

| Actor or concept | Candidate responsibility | Unresolved authority |
| --- | --- | --- |
| Organizer / authorized delegate | Record a received entry, correct details, accept/reject or withdraw it and close intake. | Creator-only is available as a precedent; club delegation is not modeled. |
| Player | Supply identity and consent to an entry or pairing. | Whether an account, external identity, invitation link or organizer verification suffices is undecided. |
| Club representative | Submit or correct entries on behalf of players. | The case's email channel does not prove who may act or how authority is checked. |
| Doubles pair | Two explicitly associated players for a discipline. | Both players entering is stated as an online-entry completeness condition; a separate confirmation or consent mechanism was not observed. Email behavior remains unknown. |
| Entry | A request for a particular discipline/bracket, distinct from accepted roster membership. | Duplicate detection, acceptance, limits and retention remain choices. |
| Invitation | A targeted offer to enter, separate from admission itself. | Target, issuer, validity, revocation, forwarding and account binding need evidence. |

A minimal candidate state model is received → accepted/rejected, with a separate withdrawn outcome; doubles may additionally require an incomplete/pending-partner condition. These names and transitions are illustrative. Confirm whether the organizer actually needs each state before specifying it. Opening/closing intake belongs to tournament policy, not to an individual entry; accepting an entry need not publish player data. Invitation states (issued, accepted, expired, revoked) are also hypotheses and should be deferred without a confirmed invite-only scenario.

Observable scenarios to refine:

1. An organizer records an email entry with its source and discipline, distinguishes received from accepted, and can inspect what changed without publishing contact data.
2. A doubles entry identifies both players; incomplete or duplicate membership is surfaced rather than treating any arbitrary team ID as a confirmed pair.
3. A correction or withdrawal checks actor authority and the current version, preserves the winner of concurrent edits, and distinguishes withdrawing one discipline from removing a person everywhere.
4. Closing intake rejects or queues later requests according to an explicitly chosen policy; an existing announcement remains independent.
5. If invite-only is later selected, a forwarded, expired or revoked invitation and an acceptance by the wrong identity have explicit outcomes. No such behavior is inferred from the enum.

## Boundaries, compatibility and decision points

Tournament Management is the candidate owner of event-specific entry and intake policy. Player identity may be supplied by an organizer or future Player Management; this research selects neither a new context nor an identity provider. Prefer a separate entry contract to silently expanding existing roster/draft semantics. Preserve current REST/JWT behavior unless a separately approved feature changes it. Do not base new permissions solely on the broadly editable draft or expose registration contacts via the public announcement.

Before implementing, decide the first authorized actor, identity namespace and provenance, pair membership rules, minimal states and transitions, correction/withdrawal policy, intake closure, duplicate/capacity handling and audit/privacy retention. An organizer-managed first slice can avoid public account onboarding, invitation tokens and federation integration, but still needs an identity representation; it must not pretend IDs are verified or introduce Player Management by stealth. Any migration, module, dependency or public-contract change requires the feature lifecycle's explicit assessment.

## Open questions and proposed follow-up

Confirm these scenarios with an organizer and applicable competition rules; none were interviewed or contacted here. Do not copy phone collection, consent wording or account rules from another platform as RallyOn requirements. No supported federation API or shared external account linkage was verified. Consult #186 when its relevant research becomes available; no dependency on completion of its entire international handbook is justified by this case.

The [exact follow-up draft](issue-drafts.json) proposes refining organizer-managed entries under a new full feature lifecycle. Player self-service, club delegation and invitations remain explicit future decisions. Human findings acceptance, separate issue-creation approval and merge approval remain pending.
