# 0004 — Coordinate feature delivery with a shared lifecycle skill

## Status

Accepted for Phase B of issue #191; the Phase C product pilot remains separate.

## Context

Phase A defines human and independent-checker gates, architecture boundaries and value evidence. OpenSpec 1.14.2 supplies artifact dependencies and task tracking, but readiness is based on file existence. It cannot distinguish a real approval from an empty review artifact or stale test evidence. RallyOn needs a resumable workflow for Codex and Claude without adopting a standalone runner or requiring Orca.

## Decision

- Keep one shared `feature-lifecycle` skill and use native agents with fresh contexts for independent work. Makers cannot certify their own output; unavailable checking remains pending.
- Keep `spec-driven` as the default. Full runs explicitly select the `rallyon-feature` schema. Planning prerequisites end at issue drafts; verification, review and UAT stay outside apply's prerequisite closure so proposal preparation cannot manufacture implementation evidence.
- Use a Python standard-library helper for versioned per-change evidence and a worktree-local active-session pointer in Git's metadata directory. Bind approvals to reviewed artifact hashes and post-implementation evidence to the source-content snapshot. Changed inputs make affected and downstream evidence stale without erasing history.
- Record actual human approval quotes and source references at G1, G2, G4, G8 and merge. G4 covers the exact issue payload and continuation to implementation. These are auditable workflow records, not authenticated signatures or a security boundary.
- Require checked issue creation through the helper. Persist successful remote creation immediately, skip recorded successes on resume, and stop on an ambiguous remote failure until its outcome is reconciled. Avoid hidden retries that could duplicate issues.
- Apply the Claude Bash issue guard only while a lifecycle session is active; preserve the existing commit guard. Codex and other tools follow the same helper requirement through shared instructions. The hook cannot technically intercept connector calls, arbitrary shell obfuscation or all other runtimes.
- Support narrow bugs/docs with `spec-driven` and a reduced record explaining omitted scoring and review ceremony. Retain applicable human decisions, independent checking, escalation and stale-evidence rules.

## Consequences

The workflow is inspectable, resumable and shared between agents. Deterministic helpers cover fragile evidence and issue-creation behavior; native agents retain responsibility for implementation and judgment. Focused CI checks schema, helper/hook behavior and formatting without rebuilding the product backend.

There is additional evidence and prompt maintenance. Hashes detect changed inputs, not dishonest records or semantic correctness. The active pointer is local, so another worktree must explicitly start/resume its session. Independent review and explicit human UAT remain required; automation does not turn their absence into success. A pilot is still needed to establish practical usefulness and tune ceremony.

Existing REST, JWT, database and `ro` CLI contracts remain unchanged. This decision does not migrate legacy YAML specs, deprecate CLI commands, select a frontend stack or remediate dependency findings.

## Amendment — research track

Stories whose goal is knowledge, such as issue #183's requirements reconciliation, ran the full track. That produced a numeric value score for an analysis, a candidate spec that archiving would have merged as accepted behavior, and three review lenses that repeated the source audits which actually found errors. A third helper mode, `research`, with the `rallyon-research` schema, follows the spike/exploration-enabler practice: G1 charter with timebox, a single independent G5 findings check, G8 human acceptance, optional G4 follow-up issues after acceptance, and merge. Track choice is part of the approved G1 artifact and cannot change on resume. The tradeoff is fewer independent checks, so the G5 checker brief must cover source mapping, file scope and documentation checks thoroughly. Research never authorizes production changes or accepted specs.

## Related

- [Issue #191](https://github.com/wlambertz/rallyon/issues/191)
- [Phase B handoff](https://github.com/wlambertz/rallyon/blob/5d1176e/docs/issue-implementation-plans/issue-191-phase-b-lifecycle.md)
- [Gate contract](../feature-lifecycle-gates.md)
- [Operations and recovery](../agent-workflows.md#feature-lifecycle)
- [ADR 0002 — OpenSpec adoption](0002-adopt-openspec-dev.md)
