# Research charter: from entries to tournament draws

## Question and decision it informs

Which statements in the German guide supplied in [issue #185](https://github.com/wlambertz/rallyon/issues/185) are useful, verified and applicable to RallyOn's club-level tournament workflows, and what is the smallest justified next step from organizer-managed accepted entries to a draw? Investigate the full guide before recommending a format or algorithm; a simple knockout format is a candidate, not an approved product decision.

## Primary persona and owning app/context

Primary persona: the organizer preparing a participant field and explainable draw. Players benefit from understandable pairings and progression. Tournament Management is the candidate owner of draw preparation and phases; result/standings and participant-identity ownership must be distinguished. The existing entry book retains local, unverified player keys and deliberately does not synchronize draw rosters (ADR 0006). No frontend stack is selected.

## Sources

- Preserve the complete issue #185 reference verbatim under this change, with provenance and unverified-source labels. Keep its German terminology alongside English glosses; do not silently repair its typos or chronology.
- Reuse the #183 reconciliation, completed #203 standings research, #213 entry research and implemented #219 entry book. Inspect current code, tests and `openspec/specs/`, including configuration rosters, format/seeding policies, phase stubs and lifecycle methods. Older issue references to wiki specs and `ro spec` are historical, not instructions to create new legacy specs.
- Read personas, context map and ADRs for intended ownership. Cross-reference #184 and #186 for overlapping checklist, withdrawal and seeding topics, keeping a single classification for each duplicated claim within this research.
- Consult applicable primary DBV/national or regional competition rules for any proposed national obligation. Use official BWF GCR/Laws and relevant event-specific regulations to check a BWF claim when its context matters. Record edition, clause/page, access date and applicability; unavailable or unverified claims remain explicit gaps.
- Select a documented club/national competition as a concrete applicability case if sufficient sources exist. No interview, organizer demand or authority confirmation is claimed without actual evidence.

## Scope and exclusions

Inventory all six guide sections with stable claim IDs: knockout structure/byes and rest times; main draw/qualification/reserve; seeding and association separation; withdrawals/promotions; groups and group-to-knockout advancement; practical checklist. Classify each as software-relevant, informational or international/deferred and record its claimed authority. Distinguish mathematical properties, verified obligations, event-specific choices and unsupported assertions.

Map each software-relevant claim to concrete current code/spec evidence as Covered, Conflicts, Gap or Not applicable. An enum or policy input alone does not prove an implemented algorithm. Verify the stated seed thresholds, placement schemes, withdrawal chronology and promotion windows before proposing adoption. Reuse #203 for standings; this task does not redo or silently adopt its rules.

Investigate accepted-entry selection, singles/doubles units, participant-field ownership, configuration snapshots, reproducibility/randomness, organizer authority, withdrawal effects and the boundary between preparing a draw and running matches. Document architecture/ADR implications and dependencies of any proposed follow-up. Selection of a concrete first format remains a research recommendation for human review.

All changes stay under `openspec/changes/issue-185-draw-structures/`, including the source reference and any decision notes. No wiki edits are planned. No production code, tests, migrations, runtime configuration, dependencies, accepted behavior specs or UI are changed. No API/JWT/CLI contract changes, external integration, federation-wide rule engine, automatic entry-to-roster synchronization, penalty automation or deployed tournament operation is included. Candidate requirements remain in findings or optional issue drafts, outside `specs/`.

## Deliverable and success criteria

Produce the labelled original reference, complete claim inventory/source trace, current code/spec reconciliation, overlap map, unresolved questions, and a recommendation with persona value, rough effort and owning context. Every source statement is classified or explicitly skipped with a reason; every adopted-rule candidate has a primary-source trace and applicability statement. Preserve unverified and conflicting claims separately. Prepare exact follow-up issue drafts only when justified; their creation requires later explicit approval and implementation follows a separate lifecycle.

Run `npm run format:check`, explicit formatting checks for new artifacts and strict pinned OpenSpec validation. Obtain a fresh independent G5 claim/source and file-scope check, then explicit human G8 acceptance of findings and separate merge approval. Maven verification is unnecessary for research-only files; any omitted runtime checks will be disclosed.

## Timebox or agreed budget

No timebox or numerical budget has been agreed. If full primary-source access or a suitable concrete case is unavailable, document the limitation rather than inventing evidence or expanding the task.

## Track choice

Research (`rallyon-research`): the output is source-traced knowledge and a recommendation about uncertain applicability and workflow ownership, rather than an approved feature or settled documentation correction. No production code, tests, migrations, runtime configuration or dependencies change.

## G1 decision

Pending explicit human approval of this charter. Record the actual approval quote and source through the lifecycle helper before producing findings.
