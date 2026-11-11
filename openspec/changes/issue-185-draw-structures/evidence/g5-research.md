# G5 — Independent research verification

**Verdict: PASS.** Fresh checker `draw-research-check`, 2026-11-11. No blocking finding. Reviewed the approved charter, preserved reference, inventory, primary checks, code reconciliation, findings and exact optional issue payload. No maker files modified and no gate registered.

| Severity | Location | Finding / action |
| --- | --- | --- |
| None | Research artifacts and optional participant-field draft | No correction required for the charter's research scope. |

## Independent checks

- Retrieved issue #185 independently with `gh issue view --json body`; the text between the reference markers matches the issue's supplied reference exactly after trimming only boundary whitespace. Source errors remain preserved; the Prettier ignore range is effective.
- Checked all six source sections against the inventory, including tables, withdrawal chronology, causal/rationale statements and the complete practical checklist. Confirmed **108 unique contiguous IDs** with section counts 12/11/20/18/17/30. Repetitions and overlap are explicit, not independent corroboration. The verification index covers every ID range and keeps relevance separate from acceptance.
- Independently opened the three official URLs P1/P2/P3 in [primary verification](../primary-verification.md). Titles, editions, page references and the bounded claims inspected agree with the retrieved text. Checked P1 sections 1, 10–16 and P2 Anlage IV Teil 2 §§ 1–3, 10–11 for the relevant assertions; checked P3's two-page announced case. No universal national applicability or actual event execution is claimed.
- Reproduced the important qualifications: P1's omitted conditions/exceptions are visible, its section 10 does not supply the guide's cited placement clause, and P2 must not be replaced by the guide's policy. Exact diagrams, checklist assertions and event-specific overrides remain explicitly unverified/deferred. This report does not duplicate the sports-rule tables or independently validate their diagrams.
- Inspected `EntryBookService.kt`, `EntryBook.kt`, `EntryBookUseCase.kt`, `ParticipantsRoster.kt`, `DraftReplacement.kt`, `ConfigurationServiceImpl.kt`, format/seeding/phase APIs and mapper/test references. The local-key versus numeric-reference distinction, explicit pair membership, entry-only disposition, broad legacy configuration access and unsupported phase paths are supported. Enum values and persistence tables are not misrepresented as implemented algorithms.
- Checked `tournament-configuration-read` GET-CONFIG-003, draft-update contracts, ADR 0006, context map and archived #203 findings. The recommendation preserves these boundaries and identifies unresolved authority, consistency, retention and migration decisions. Participant-field preparation is labelled a product hypothesis, not a federation requirement; pure knockout is not silently selected.
- Inspected the optional issue payload: one separately gated feature, grounded in the observed identity/field gap, with no automatic synchronization or invented global IDs. Creation and implementation are not claimed approved. No new accepted spec is introduced; `skip_specs: true` is set.

## Commands and results

- `npm run format:check`: **PASS**, 723 tracked files, exit 0.
- `npx --no-install prettier --check 'openspec/changes/issue-185-draw-structures/**/*.md' openspec/changes/issue-185-draw-structures/issue-drafts.json`: **PASS**, exit 0 (before this report was added).
- `npx -y @fission-ai/openspec@1.14.2 validate issue-185-draw-structures --strict`: **PASS**, exit 0; no behavior deltas expected.
- `git diff --cached --check`: **PASS**. Independent Python comparison: source match and ID continuity assertions passed.
- Staged scope: nine files, all within `openspec/changes/issue-185-draw-structures/`; no unstaged maker edits observed. No production, test, migration, runtime, dependency, wiki or accepted-spec files changed.
- SHA-256 of staged research diff excluding `.lifecycle.json`: `435f32347c57aa1971c9118f1c1a9d98b6d7121bcebad444c5f039c2958d7efe`. Base revision `dbab018feaa06fbf077bcde1fd521270e838cbef`.

## Limits

No Maven/runtime tests run: this is research-only. No interviews, actual competition execution, authoritative interpretation of unresolved rules, complete technical-diagram validation or HBV override audit was performed. The official texts support only the explicitly bounded assessments; deferred checklist material is preserved rather than certified. Human G8 acceptance, optional G4 issue creation and separate merge approval remain pending.
