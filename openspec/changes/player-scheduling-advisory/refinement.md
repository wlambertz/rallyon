# Refinement: player scheduling advisory

## Problem and evidence

An organizer running parallel badminton disciplines needs to identify a player assigned overlapping matches or insufficient recovery, even when matches are delayed or participants enter doubles and singles. The [accepted #228 research](../player-rest-scheduling/findings.md) identifies a real representative event, official rule sources and important limits: applicability and exception procedures for the event remain unresolved. The maintainer requests an advisory that prioritizes official data; 15 minutes is an unverified idea, not a default.

## Primary persona and owning app/context

The organizer is the primary persona; Tournament Management owns the proposed capability. Players benefit from fair recovery and reliable scheduling. Organizer interruptions and unreliable hall connectivity are persona hypotheses. Player identity, team membership and match data may cross context boundaries; ownership is not yet resolved. The organizer frontend is a placeholder and no UI stack is assumed.

## Scope and exclusions

Define a bounded advisory that assesses overlaps and recovery using confirmed identities, individual membership in doubles entries, match times and a selected applicable rule authority and edition. Distinguish match end, call, proposed start and actual start; reconsider the assessment when relevant facts change. Show reasons and unknowns for missing or uncertain data. Prioritize official sources and record applicability and provenance; do not extend Bundesliga-specific call timing or DBV rules to an event without evidence.

This is advisory: do not block scheduling or invent a universal rest duration, waiver or exception. Do not introduce a frontend stack, a full scheduling algorithm, fixture generation, authoritative identity management, new placeholder services or automated rule discovery. Identify prerequisites and decisions before design; changes to public contracts, persistence, dependencies or modules require their applicable escalation. Preserve existing API and security contracts unless separately approved.

## Success criteria

- An organizer can distinguish a confirmed player overlap, a potential recovery concern and an unresolved assessment, with the evidence and missing facts apparent.
- Scenarios cover overlapping matches, insufficient recovery, a delay and correction, and a doubles player in another discipline, including unknown identity and missing times.
- Any rule-derived duration or exception traces to an applicable official event and versioned authority; unresolved applicability is explicit and no 15-minute default is introduced.
- Requirements, architecture, compatibility and validation are reviewed before implementation; automated tests trace to the agreed behavior.

## Assumptions and agreed budget

Fixture availability, authoritative identity and team membership, match-time capture, rule selection and ownership are open decisions; current configuration fields do not establish an operational scheduler. Whether a narrow in-memory assessment can provide useful value before those dependencies exist is a design question, not a commitment. No timebox or numerical budget has been agreed.

## Track choice and G1 decision

The full feature track applies because this is new product behavior with unresolved architecture and data contracts. Pending explicit human approval of this refinement; record the actual quote and source in lifecycle evidence.
