# Research charter: national requirements reconciliation

## Question and decision it informs

Which requirements in the reference in [issue #183](https://github.com/wlambertz/rallyon/issues/183) are relevant to national badminton formats, how do they relate to RallyOn's current specifications and code, and which merit adoption, adaptation, deferral or rejection? The findings inform the maintainer's prioritization and any separately approved follow-up work; they do not approve product behavior.

## Primary persona and owning app/context

The Organizer is the primary persona: a club tournament organizer needs reliable, understandable planning and match-day workflows. Player and Spectator value is assessed as well. Tournament Management is the initial owning context; ownership involving Scoring, Player Management and Public Information remains open. The Organizer app is a placeholder with no selected frontend stack. Persona device and connectivity assumptions remain hypotheses.

## Sources

- The complete reference and scope in issue #183, treated as an unverified generated idea source.
- Existing work in `openspec/changes/requirements-reconciliation/`, including both source traces, inventory, dispositions and open questions, as prior work to reassess rather than accepted findings.
- Current `openspec/specs/`, relevant Tournament Management code, `docs/wiki/Personas.md`, the context map and relevant architecture and design documents.
- Applicable primary DBV and regional federation rules, identified by provision and version or access date; distinguish verified applicability from unverified claims. Cross-reference issues #184, #185, #186 and the existing group-standings follow-up #203.

## Scope and exclusions

Cover national championships, national and regional team leagues, senior and invitational tournaments, and generic entry, draw, seeding, group standings, scheduling, live operations, results and printable output. Assess each in-scope source statement, retaining a stable ID, section, German term and English gloss; classify it as covered, conflict, gap or not applicable, or explicitly skip it with a reason. Cite concrete code or specifications for covered and conflicting claims. Record international-only claims as deferred and retain plausible generic national counterparts for verification.

Compare national formats with club tournaments and identify possible domain concepts and context boundaries without presuming placeholder services exist. Exclude international-only formats, sanctioning and related international rules as described in #183, production implementation, a frontend stack decision and unrelated specification rewrites. No production code, tests, migrations, runtime configuration or dependencies change. No public API, database, IAM or CLI contract changes are authorized.

## Deliverable and success criteria

Produce source-traced findings within this research change, reusing prior material where accurate. Include a complete statement-level trace, evidence-backed classifications, prioritized recommendations with hypothesized value for each persona, coarse effort and proposed context, and separate unresolved questions and unverified rules. Identify duplicates and dependencies with existing issues. Verify a rule against its applicable national primary source before recommending it as accepted behavior; if unavailable, leave it explicitly unverified. Prepare exact follow-up issue drafts where warranted, referencing findings and acceptance criteria; do not create them before the required human approval.

An independent checker reviews source-to-claim coverage, code and spec references, file scope, formatting and strict OpenSpec validation. The maintainer reviews the verified findings and limitations at G8. Follow-up issue creation and merge each require their own explicit decision.

## Timebox or agreed budget

No timebox or numerical budget has been agreed. Escalate if a budget is later agreed and exceeded.

## Track choice

Research (`rallyon-research`) fits because the answer and appropriate national rules are not yet established. Full is for a proposed capability; reduced is for narrow settled documentation or bug work. Candidate requirements remain in findings or follow-up drafts, not in this change's `specs/`; archive with `--skip-specs` after the applicable gates.

## G1 decision

Pending explicit human approval of this charter; record the actual quote and source in lifecycle evidence.
