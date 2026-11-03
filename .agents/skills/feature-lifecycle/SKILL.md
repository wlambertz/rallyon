---
name: feature-lifecycle
description: Run or resume RallyOn's idea-to-UAT feature lifecycle with OpenSpec artifacts, independent native agents, durable gate evidence and explicit human decisions. Use for full feature lifecycle work, its reduced path for narrow bugs and documentation, or its research track for stories whose goal is knowledge (requirements reconciliation, source integration, spikes) rather than a feature.
---

# Feature lifecycle

Run from the repository root. The helper is `python3 .agents/skills/feature-lifecycle/scripts/lifecycle.py` (`--help` per subcommand). Open the [gate contract](../../../docs/feature-lifecycle-gates.md) only at the section for the current gate or track. Open the [operation guide](../../../docs/agent-workflows.md#feature-lifecycle) only for issue creation, recovery or locks.

## Start, status and resume

1. Read the idea or issue, any existing change, the nearest `AGENTS.md` and the owning-area skill. Read wiki and architecture pages only when the change touches them.
2. Choose a [track](../../../docs/feature-lifecycle-gates.md#choosing-a-track) and record it, with its reason, in the G1 artifact:
   - **Full:** for a new capability, use schema `rallyon-feature`.
   - **Reduced:** for a narrow bug or docs change, use `spec-driven` with `--mode reduced`. Never use it for new capabilities, broad refactors, ambiguous scope or security-sensitive work.
   - **Research:** for a knowledge goal, use `rallyon-research` with `--mode research`. If the findings require production changes, propose a follow-up feature instead.
   - Use `skip_specs` only when requirements do not change.
3. Start the helper session. Only one session can be active per working tree. To switch changes, `deactivate` first; deactivating preserves evidence and grants no approval.
4. On resume, run `status` and `openspec status`, then continue at the earliest pending or stale gate. For a stale gate, run `explain GATE` to list what changed. **Do not read `.lifecycle.json`, the snapshot cache or old evidence reports wholesale.** File existence, ticked task boxes and green CI are not approval.

Delegate artifact and apply mechanics to the `openspec-*` skills, keeping the selected schema and change. Do not edit the generated skills. OpenSpec readiness alone does not authorize implementation.

## Run the gates

| Stage | Action and evidence |
| --- | --- |
| G1 | Use [persona refinement](references/refinement.md). Present scope, exclusions, outcomes, assumptions and the agreed budget, then get explicit human scope approval. |
| G2 | Present the proposal, testable requirements, design, compatibility/ADR assessment, validation approach and value evidence for human approval and go/no-go. |
| G3 | A fresh checker runs the [value challenge](references/value-check.md). Material changes return to G2. |
| G4 | Preview the exact `issue-drafts.json` payload. Get explicit approval to create those issues **and continue implementation**, then create them only through the helper. |
| G5 | Run `check G4 --require` before apply. Implement through makers and OpenSpec tasks, recording traceability and deviations. An independent checker confirms completion. |
| G6 | A fresh checker uses [QA traceability](references/qa.md) against the current snapshot. |
| G7 | Run [three independent review lenses](references/review.md). Recheck fixes, and escalate disagreement that remains after two rounds. |
| G8 | Prepare [persona UAT](references/uat.md). Get explicit human acceptance after the human executes or witnesses the scenarios. |
| Merge | Get separate explicit human approval of the final reviewed revision. |

**Research track:** G1 approves `charter.md`. At G5, one fresh checker verifies the claim-to-source mapping, the file scope and the documentation checks. G8 is human acceptance of the findings. An optional G4 then approves and creates follow-up issues. Merge comes last. G2, G3, G6 and G7 do not apply. Keep candidate requirements out of `specs/`, and archive with `--skip-specs`.

Finish preparation before you ask for a human decision. Record the actual approval quote and its source. Never invent approval, infer it from silence, or reuse it for changed content. A rejection records actionable reasons and never advances the gate.

## Independent checks

- Makers cannot certify their own output. Register every delegated maker with `start --additional-maker ID`, keeping the original maker, repository and mode. A checker who later becomes a maker invalidates their earlier checks.
- If no independent agent is available, leave the gate pending and hand off to another person or session.
- Changed inputs stale the affected and downstream evidence. Recheck it instead of copying old results.

## Token budget

These rules apply to every agent: Codex, Claude, Cline and others. The [per-agent table](../../../docs/agent-workflows.md#token-budget) shows how each tool applies them.

- **Run deterministic checks first.** Before dispatching a checker, run format, `openspec validate <change> --strict` and the area's tests. Fix red mechanical checks without a checker.
- **Write a bounded brief.** Give the gate, the exact files (the change artifacts or `git diff --stat`), the gate's reference file, the commands to run and the report path. Do not state the expected conclusion, and do not ask for repository-wide exploration unless the gate needs it.
- **Keep reports short.** Lead with the verdict, then give a findings table (severity, location, fix), then the commands and results. Do not restate inputs. Aim for about 60 lines.
- **Recheck only the delta.** After fixes, send the checker only the previous report, its findings and the changed files. Write a new report that covers just the delta and links to the earlier one. You can continue the same non-maker checker context.
- **Match model to judgement.** Use a smaller or lower-effort model for mechanical or traceability checks. Use the default model for value, correctness/security and architecture judgement.

## Escalation and issues

Escalate shared IAM, Flyway migration, public REST/JWT/CLI breaks, new dependencies or modules, spec ambiguity during apply, disagreement left after two review rounds, and an exceeded agreed budget. UI lifecycle work stays blocked on the organizer stack decision.

Every agent creates issues only through the helper, never through shell commands or connectors. The helper records successes immediately. An ambiguous outcome must be reconciled against GitHub before any further creation.
