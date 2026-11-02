---
name: feature-lifecycle
description: Run or resume RallyOn's idea-to-UAT feature lifecycle with OpenSpec artifacts, independent native agents, durable gate evidence and explicit human decisions. Use for full feature lifecycle work or its documented reduced path for narrow bugs and documentation.
---

# Feature lifecycle

Use the [gate contract](../../../docs/feature-lifecycle-gates.md) and [operation and recovery guide](../../../docs/agent-workflows.md#feature-lifecycle). Run from the repository root. The helper is `python3 .agents/skills/feature-lifecycle/scripts/lifecycle.py`; inspect its `--help` for argument syntax.

## Start, status and resume

1. Inspect the working tree, root/nearest `AGENTS.md`, owning-area skill, relevant wiki and architecture docs. Read the idea or issue and any existing change before proposing work.
2. For a new capability use OpenSpec 1.14.2 with explicit schema `rallyon-feature`; keep the repository default `spec-driven`. For a narrow bug/docs change, use `spec-driven` and reduced mode, recording why omitted scoring and review ceremony are not applicable. Do not use reduced mode for new capabilities, broad refactors, ambiguous scope or security-sensitive work. Use `skip_specs` only when requirements do not change.
3. Start the helper session for that change. Only one session may be active per working tree; explicitly deactivate an earlier session before switching. Deactivation preserves evidence and grants no approval.
4. On resume or status, read helper status and the durable records, then inspect OpenSpec status. Resume at the earliest pending or stale gate. File existence, completed task boxes and a green CI run do not establish approval.

Delegate artifact/apply mechanics to the existing `openspec-*` skills, preserving the selected schema and change. The lifecycle helper adds gate checks; OpenSpec readiness alone does not authorize implementation. Generated skills must not be edited.

## Run the gates

| Stage | Action and evidence |
| --- | --- |
| G1 | Use [persona refinement](references/refinement.md). Present scope, exclusions, outcomes, assumptions and agreed budget; obtain explicit human scope approval. |
| G2 | Prepare proposal, testable requirements, design, compatibility/ADR assessment, validation approach and value evidence. Present concrete artifacts for human specification approval and go/no-go. |
| G3 | Dispatch a fresh independent checker using [value challenge](references/value-check.md). Record its actual result. Material changes to value or specification return to G2. |
| G4 | Prepare structured issue drafts with stable IDs, target repo, exact titles/bodies, metadata, dependencies, requirement references, acceptance criteria and validation commands. Preview the exact payload. Ask explicitly for approval to create those issues **and continue implementation**. Record that approval, then use only the checked helper to create issues. |
| G5 | Check current G1–G4 evidence before apply. Implement approved scope through native makers and OpenSpec task tracking. Record implementation traceability and deviations; an independent checker confirms completion. |
| G6 | Use [QA traceability](references/qa.md). A fresh checker verifies scenarios, runs the required runtime checks and records commands, results, environment and limitations against the current source snapshot. |
| G7 | Dispatch [three independent review lenses](references/review.md). Resolve blocking findings and recheck changed work; escalate disagreement remaining after two rounds. |
| G8 | Prepare [persona UAT](references/uat.md), a reproducible environment and current review evidence. Obtain explicit human acceptance after they execute or witness the scenarios. |
| Merge | Present the final reviewed revision and current checks. Obtain separate explicit human merge approval. |

Finish all authorized preparation before requesting a human decision. Approval quotes and conversation/source references must describe actual explicit approval of the displayed artifacts. Never invent approval, infer it from silence or reuse it for materially changed content. Rejections record actionable reasons and never advance the gate.

## Independent work and failure handling

Use native agent tools with fresh contexts and bounded raw inputs. Record maker/checker identities and their actual findings; a maker cannot certify their own output. Register every delegated maker with repeatable `start --additional-maker ID` before recording checks, retaining the original `--maker`, repository and mode on resume. A checker who later becomes a maker invalidates their earlier check. Give reviewers the artifact/diff, relevant requirements, applicable architecture and actual test evidence, without telling them the intended conclusion. When independent agents are unavailable, leave checking pending and provide a handoff for a different person or fresh session.

Changed inputs stale affected approvals and downstream evidence. Use helper status before advancing and return to the earliest affected gate. Do not repair staleness by copying prior results into a new snapshot without rechecking. Retain rejected, stale and superseded evidence for diagnosis.

Escalate changes in shared IAM, Flyway migrations, public REST/JWT/CLI breaks, new dependencies/modules, spec ambiguity during apply, unresolved reviewer disagreement after two rounds, and exceeding an agreed budget. Record a budget only when one was agreed. UI lifecycle work remains blocked on the organizer stack decision.

Issue creation must go through the helper in Codex and Claude; do not bypass it through shell commands or connectors. It records successful creations immediately and resumes without repeating them. An ambiguous remote outcome requires reconciliation against GitHub before further creation. See the operation guide for recovery and hook limitations.
