# Research charter: registration and invitation workflows

## Question and decision it informs

For a concrete German club badminton tournament, who may enter, confirm, correct or withdraw players and doubles pairs, what observable entry states are needed, and what does invitation-only mean? Determine whether a bounded registration feature is justified and which identity, permission and ownership decisions must precede it.

## Primary persona and owning app/context

Primary persona: the organizer managing entries; players and submitting club representatives are supporting actors to investigate. Tournament Management is the candidate owner of event entries and policy. Player Management is a placeholder; participant identity ownership is unresolved. No frontend stack is selected.

## Sources

- Issue #213; accepted findings from #183 and #212; the implemented announcement capability from #215/#217, including its independent publication state.
- Existing configuration, roster, registration policy, lifecycle methods, authentication contracts, tests and specifications; personas and the context map for intended architecture.
- A real club tournament announcement and its published registration instructions, preferably the Bommern 2026 case already used in #212 if sufficient evidence is available. Select another documented case if needed and explain why.
- Applicable primary national or regional rules for any claimed obligation. Reuse and distinguish overlapping material in #186; generated or secondary documents are not authority.

## Scope and exclusions

Trace actors, identity and permissions, singles/doubles representation, initial entry, confirmation, correction, withdrawal, registration opening/closure and invite-only behavior. Map current implementation and confirmed gaps. Separate observed facts, plausible product choices and unresolved rules. Explicitly distinguish an entry, a participant roster, a doubles pairing, an invitation and an authenticated user.

Assess context boundaries, compatibility, privacy and dependencies, including whether a minimal organizer-managed entry slice can precede player self-registration. No production code, tests, migrations, runtime configuration, dependencies, accepted behavior specs, frontend, external integration or legal compliance automation will change. No interviews, authority confirmation or API availability will be claimed unless actually obtained.

## Deliverable and success criteria

Produce source-traced findings with a concrete case, actor/state scenarios, code/spec mapping, identity and permission alternatives, boundary implications, unresolved questions and a recommendation for the smallest justified follow-up. Prepare exact follow-up issue drafts only if findings warrant them. Run formatting and strict OpenSpec checks; obtain independent claim/source and scope verification, then human acceptance. Any implementation follows a separate feature lifecycle.

## Timebox or agreed budget

No timebox or numerical budget has been agreed.

## Track choice

Research (`rallyon-research`): the desired outcome is knowledge about uncertain workflows and ownership, not approved production behavior. No production code, tests, migrations, runtime configuration or dependencies change.

## G1 decision

Pending explicit approval of this charter; record the actual quote and source through the lifecycle helper.
