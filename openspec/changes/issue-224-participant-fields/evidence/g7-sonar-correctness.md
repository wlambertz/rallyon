# G7 — Sonar delta correctness and security review

**Verdict: PASS.** Independent checker `field_review_correct`, 2026-11-11. No blocking finding. This delta supplements the [prior correctness/security review](g7-correctness.md), following [G5](g5-sonar-delta.md) and [independent G6](g6-sonar-delta.md). No maker files or gates changed.

| Severity | Location | Finding / action |
| --- | --- | --- |
| None | Four-file staged Sonar remediation | Behavior and security constraints remain intact; no correction required. |

## Delta assessment

- `FieldPreparation` bundles the same five nullable selection/source inputs in their original order. The service adapter and pure-test helper map them consistently; tournament ID, expected field version and actor remain separate. Destructuring performs no source read or mutation. Creator authorization still precedes private loads, and key/input/source checks, field locking, replacement and save retain their original order. No validation or failure branch changed.
- The new value stays within the pure use-case package. Public request/controller binding, strict JSON parsing, response/error contracts, transaction configuration, persistence, V8 and security configuration are unchanged. The earlier JSON-coercion correction is unaffected.
- The UAT executable resolves from the script's own repository location to this checkout's ignored `bin/ro`, replacing a fixed executable in shared temporary storage. Invocation remains an explicit argument list with no shell; token-command arguments/environment and output capture are unchanged. Building the repository CLI is documented as a prerequisite. The change introduces no new external executable source or token disclosure.
- `draft_path` and `intake_path` preserve the former URL expressions. Every changed call uses the corresponding variable; HTTP methods, request data, expected statuses and workflow assertions remain unchanged.

## Checks and limits

- Inspected the complete staged delta against `f837f47`: exactly four files, 29 additions and 17 deletions. SHA-256: `ae2d96e77f6c9a619756032ed75897e21ae7d1110b4539bddd3b8a6938aa5635`.
- `git diff --cached --check`: passed. Independent Python AST parsing and assertion that `Path(__file__).resolve().parents[4]` equals the repository root passed.
- Independently inspected current logs: maker full verify **149 tests, zero failures/errors, three optional skips**; independent delta QA **27 tests, zero failures/errors/skips**. No additional Maven run for this bounded source-inspection lens.
- The unchanged concurrency/storage behavior retains prior review evidence and the current maker PostgreSQL runs. Rerunning the altered authenticated walkthrough, refreshed Sonar/CI and human acceptance for the changed revision remain separate evidence; this review does not reuse earlier human acceptance automatically.
