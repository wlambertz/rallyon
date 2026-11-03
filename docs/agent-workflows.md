# RallyOn Agent Workflows

RallyOn keeps agent guidance small, shared, and inspectable. The canonical project instructions live in `AGENTS.md`; tool-specific files should point to that guidance or add only narrow tool behavior.

## Instruction Files

| Tool | Primary file | Notes |
| --- | --- | --- |
| Codex | `AGENTS.md` and nested `AGENTS.md` | Repo skills live in `.agents/skills/`. Use skills for repeatable workflows. |
| Claude Code | `CLAUDE.md` adapter plus `AGENTS.md` | Loads repo skills through the `.claude/skills` symlink to `.agents/skills/`. Keep Claude-specific notes minimal. |
| Cline | `AGENTS.md` (detected automatically) | Discovers project skills under `.claude/skills/`, so it reads the same symlinked skills. Has no repo-specific Cline file. |
| GitHub Copilot | `.github/copilot-instructions.md` pointer plus `AGENTS.md` | Do not duplicate repository guidance in the Copilot file. |

## Where Guidance Belongs

- Put always-on repository rules, validation commands, safety boundaries, and the educational project principle in root `AGENTS.md`.
- Put subtree-specific rules in the closest nested `AGENTS.md`.
- Put repeatable multi-step workflows in `.agents/skills/<skill-name>/SKILL.md`.
- Put explanatory background, maintenance checklists, and cross-tool orientation in `docs/`.
- Keep one-off task instructions in the issue, PR, or prompt instead of making them durable.

## Educational Project Principle

RallyOn is an educational project for learning modern technologies, architecture practices, tooling, and agent-assisted delivery methods. Agents should favor explainable, incremental changes and document important tradeoffs when introducing new methods or tools.

Learning notes belong close to the decision:

- Use code comments only when they clarify non-obvious implementation choices.
- Use nearby docs when a workflow, tool, or architecture decision changes.
- Use issue comments or PR descriptions for temporary rationale that does not need to become durable guidance.

## Skills

Repo-scoped skills live under `.agents/skills/` and are shared by Codex and Claude Code. Keep each skill focused on one workflow, with a concise description that clearly states when it should trigger. Prefer instructions over scripts unless deterministic behavior or external tooling is needed.

Use `.agents/skills/issue-implementation-planner/` when an agent is asked to create an implementation plan for a GitHub issue. The core requirement also stays in `AGENTS.md` so that agents that do not load skills still follow it: save the plan under `docs/issue-implementation-plans/`, comment it on the GitHub issue, and do not change implementation code while planning.

### Why `.claude/skills` is a symlink

Claude Code discovers project skills only under `.claude/skills/`, while Codex reads `.agents/skills/`. The tracked symlink `.claude/skills -> ../.agents/skills` lets both tools load one copy of every skill instead of maintaining duplicates.

Tradeoffs:

- Git checkouts with `core.symlinks=false` (the default on Windows without Developer Mode) write the symlink as a small text file, and Claude Code then silently finds no skills. Windows contributors should enable symlink support (`git config core.symlinks true` and re-checkout) or copy the directory locally without committing it.
- Skills are now read by more than one agent, so keep them agent-neutral and give per-agent values (such as `Co-authored-by` trailers) for each supported agent.
- `tools/scripts/prettier-files.mjs` skips tracked symlinks because Prettier rejects them as explicit paths; the skill files themselves are still checked through `.agents/skills/`.

`.agents/skills/pencil-design/` (Pencil.dev CLI design generation for organizer UI) is currently paused: `application/organizer` is an empty placeholder while the frontend stack is reconsidered, so there is no Angular/Storybook surface to keep Pencil designs aligned with.

When adding or changing a skill:

- Keep the front matter `name` stable unless replacing the workflow.
- Make the `description` specific enough for implicit invocation.
- Link only to references needed for that workflow.
- Validate relative links after moving files.

## Feature lifecycle

Use the shared [feature-lifecycle skill](../.agents/skills/feature-lifecycle/SKILL.md) for idea → UAT work. The [gate contract](feature-lifecycle-gates.md) defines readiness and ownership; [ADR 0004](adr/0004-feature-lifecycle-orchestration.md) records the architecture and trust limits. Codex and Claude use native maker/checker agents, not a separate runner service. Phase C's product pilot remains separate from installing this workflow.

### Start and inspect

Run from the repository root with Python 3, Git, the pinned OpenSpec CLI and authenticated `gh` for approved issue creation. No extra Python package is required by the helper. Example commands below use a hypothetical change name; choose a real name when starting work.

```bash
npx -y @fission-ai/openspec@1.14.2 new change improve-draft-feedback --schema rallyon-feature
python3 .agents/skills/feature-lifecycle/scripts/lifecycle.py --change improve-draft-feedback start --repository wlambertz/rallyon --maker maker-session-id
python3 .agents/skills/feature-lifecycle/scripts/lifecycle.py --change improve-draft-feedback status
npx -y @fission-ai/openspec@1.14.2 status --change improve-draft-feedback --json
```

The repository default stays `spec-driven`. Explicitly select `rallyon-feature` for full runs. Narrow bugs/docs use `spec-driven` plus helper `start --mode reduced` and `lifecycle.md`, recording scope, criteria, checks, review and reasons for omitted scoring/review ceremony. Existing proposal, design, value and specification artifacts remain bound to G2–G4 approvals even in reduced mode; adding, removing or editing them makes those approvals stale. Omitted artifacts remain optional. Use OpenSpec `skip_specs` only when requirements do not change. Reduced mode is unsuitable for new capabilities, broad refactors, ambiguous scope or security-sensitive work.

Stories whose goal is knowledge rather than a feature use the [research track](feature-lifecycle-gates.md#research-path): `rallyon-research` plus `start --mode research`. Its sequence is G1 (approved `charter.md`), G5 (independent findings check), G8 (human acceptance in `uat.md`), optional G4 for follow-up issues when `issue-drafts.json` exists, then merge. The helper reports the other gates as not applicable and rejects records for them:

```bash
npx -y @fission-ai/openspec@1.14.2 new change reconcile-league-rules --schema rallyon-research
python3 .agents/skills/feature-lifecycle/scripts/lifecycle.py --change reconcile-league-rules start --repository wlambertz/rallyon --maker maker-session-id --mode research
python3 .agents/skills/feature-lifecycle/scripts/lifecycle.py --change reconcile-league-rules check G5 --checker checker-session-id --evidence openspec/changes/reconcile-league-rules/evidence/g5-findings.md --outcome pass
npx -y @fission-ai/openspec@1.14.2 archive reconcile-league-rules --skip-specs
```

The helper writes versioned evidence to `openspec/changes/<name>/.lifecycle.json`. Its active-session pointer is the worktree-specific path returned by `git rev-parse --git-path rallyon-lifecycle.json`, outside tracked source. Starting a different change requires explicit `deactivate`; this preserves records and grants no approval. To resume, start the same change with the same repository/mode/maker identity, inspect status and read evidence. Continue from the earliest pending or stale gate. Register every delegated maker using repeatable `start --additional-maker <identity>` with the original repository/mode/maker arguments. Checks reject all registered makers; adding a checker as a maker invalidates their earlier checks.

### Record actual decisions and checks

Use `--help` on the helper or a subcommand for exact arguments. Only record approval after the human explicitly approves the displayed artifacts; `--quote` and `--source` retain their actual words and conversation reference. For example, after actual G1 approval:

```bash
python3 .agents/skills/feature-lifecycle/scripts/lifecycle.py --change improve-draft-feedback approve G1 --actor maintainer --quote 'actual approval text' --source conversation-reference
```

Repeat at G2, G4, G8 and `merge` as those decisions occur. `status` is compact. It shows gate states and the G1/G2/G4 hashes and file counts. Use `status --verbose` to get the full manifests for an approval preview, and `explain GATE [--lens LENS]` to list exactly which paths, preceding decisions or reports changed since a gate was recorded. G4 must explicitly cover the exact proposed issues and continuing implementation. UAT requires the human to execute or witness the agreed scenarios. Merge approval is separate. A rejection must remain actionable evidence; neither silence nor an artifact's existence counts as acceptance.

For G3/G5/G6/G7, a fresh checker records an existing repository-relative evidence file and its actual outcome. Put reports under the change's `evidence/` directory, or use `verification.md`, `review.md` and `uat.md`. Use separate finalized reports for successive checks; changing a shared report invalidates earlier checks referencing it. In reduced mode, `lifecycle.md` holds the planning/omission record and reports remain separate:

```bash
python3 .agents/skills/feature-lifecycle/scripts/lifecycle.py --change improve-draft-feedback check G6 --checker checker-session-id --evidence openspec/changes/improve-draft-feedback/verification.md --outcome pass
python3 .agents/skills/feature-lifecycle/scripts/lifecycle.py --change improve-draft-feedback check G7 --checker architecture-review-session-id --evidence openspec/changes/improve-draft-feedback/review.md --outcome pass --lens architecture
```

Full-mode G7 also needs `correctness` and `tests` lens results. Record findings, review rounds and resolutions in the evidence file. Checkers receive bounded raw inputs and cannot be the maker. If fresh agents are unavailable, leave checking pending and hand off the artifacts to another person or session. Escalate disagreement after two unresolved rounds and the risks named in the gate contract.

Before implementation, require current G1–G4 evidence:

```bash
python3 .agents/skills/feature-lifecycle/scripts/lifecycle.py --change improve-draft-feedback check G4 --require
```

This is an additional lifecycle check, separate from OpenSpec artifact readiness. G5 also requires every approved issue to have been created or reconciled. G4 binds task definitions and order while allowing OpenSpec completion checkboxes to advance without renewed approval. Verification/review/UAT artifacts are not planning prerequisites and must contain actual post-implementation evidence. Changed inputs invalidate affected gate records and downstream evidence. Source snapshots include staged, unstaged and nonignored untracked content, excluding lifecycle state and the report locations above to avoid circular hashes; report hashes are checked separately. Run status again after edits; do not relabel old checks as current without performing the necessary rechecks.

### Approved issue creation and recovery

Prepare `issue-drafts.json` in the change directory with this shape:

```json
{
  "repository": "wlambertz/rallyon",
  "issues": [
    {
      "id": "draft-feedback",
      "title": "Concrete proposed issue title",
      "body": "Requirement references, acceptance criteria and validation commands",
      "depends_on": [],
      "labels": [],
      "assignees": []
    }
  ]
}
```

Use stable draft IDs for dependencies and optional `milestone` only when required. Preview the complete payload, including metadata, dependency ordering and body text, before G4. After approval:

```bash
python3 .agents/skills/feature-lifecycle/scripts/lifecycle.py --change improve-draft-feedback create-issues
```

The helper verifies current G1–G4 evidence and submits the approved payload. Successful issue URLs are recorded immediately; retry/resume skips those successes. If the process fails after a remote request might have succeeded, including a 60-second creation timeout, every further creation and G5 remain blocked until all uncertain attempts are reconciled. Status lists unresolved attempts even if their draft IDs were renamed or removed. Inspect GitHub for the exact saved attempted payload before resuming. Use `create-issues --reconcile <draft-id> --url <existing-issue-url> --source <verification-reference>` when it exists, or `create-issues --reconcile <draft-id> --retry --source <verification-reference>` only after confirming no issue was created. Reconciliation records verified facts locally and requires an active session and verification reference; it remains available after scope changes or stale approvals, without restoring removed drafts. Any subsequent creation still requires current G4 approval. Never blindly retry an ambiguous request or hand-edit records to claim success. A changed payload requires renewed G4 approval.

Every helper invocation holds an exclusive worktree-local lock at `git rev-parse --git-path rallyon-lifecycle.lock`, recording its PID, change and command. Normal exits release it. After a crash, inspect the lock and verify that its process has ended before removing it; then reconcile any ambiguous issue attempt before resuming.

Finish or switch the local session explicitly:

```bash
python3 .agents/skills/feature-lifecycle/scripts/lifecycle.py --change improve-draft-feedback deactivate
```

### Token budget

The lifecycle runs many fresh agents, so most of its cost is context. The skill's [token budget](../.agents/skills/feature-lifecycle/SKILL.md#token-budget) rules apply to every agent:
- Run deterministic checks before dispatching.
- Give checkers bounded briefs.
- Ask for findings-first reports of about 60 lines.
- Recheck only the changed delta.
- Use a smaller or lower-effort model for mechanical checks.

Records stay small too. Source-snapshot gates (G5 and later) store only a fingerprint and file count. The full manifest goes into a deduplicated, worktree-local cache at `git rev-parse --git-path rallyon-lifecycle-snapshots`, which `explain` reads. Staleness never depends on this cache. Records written before this change keep their inline manifests and remain valid. `.gitattributes` marks `.lifecycle.json` as generated so that diffs don't print it. Agents should still use `status` and `explain` rather than reading these files.

| Agent | Lower-cost checker | Continuing a checker for a delta recheck | Fresh independent context |
| --- | --- | --- | --- |
| Claude Code | Agent tool `effort: "lower"` or a smaller `model` | Message the same checker agent | New Agent call |
| Codex | Subagent `model` / `model_reasoning_effort` in the spawn request or a `.codex/agents/*.toml` agent | Steer the same checker thread | New subagent |
| Cline | Select a smaller model for the checker task | Continue the checker task | New task with only the bounded brief |

Keep these per-agent settings in local configuration. The repository does not pin models.

### Enforcement and validation limits

Claude's Bash `PreToolUse` guard blocks direct issue creation (`gh issue create` and `gh issue new`) for the lifecycle repository while its session is active, preserving the existing commit-hook guard. It prefers the worktree session identified by `CLAUDE_PROJECT_DIR` and falls back to tool cwd when there is no project session, so changing cwd outside the repository does not drop the active project guard. It does not discover sessions in sibling worktrees. Command substitutions are inspected before documentation-command exemptions, including nested substitutions, backticks and executable line continuations. Quoted heredoc bodies are literal documentation; unquoted heredoc bodies are inspected for executable expansions. Unrelated work without an active session is unaffected. Codex follows the same helper-only rule through shared instructions. The Claude guard does not technically intercept connectors, arbitrary shell obfuscation or every runtime; bypassing it remains contrary to the shared workflow. Approval records are auditable assertions, not authenticated signatures or a security boundary.

Do not create live test issues or implement a product feature to test this workflow. Exercise full/reduced flows in temporary repositories with a fake GitHub executable. Validate the custom schema, helper/hooks and formatting using the focused CI workflow. Existing dependency-scan failures remain visible and are tracked separately.

## MCP Policy

MCP is for external tools and context, not for local repo conventions. Use it when an agent needs live access to systems such as GitHub, design tools, docs servers, or knowledge bases.

Do not commit:

- secrets, tokens, or OAuth state,
- personal account identifiers,
- machine-specific local paths,
- private server URLs unless they are already documented public project infrastructure.

Commit only safe examples or documentation for recommended project-level MCP usage. Keep private MCP authentication in user-local configuration.

## Maintenance Checklist

Before changing agent guidance:

1. Check whether the rule belongs in `AGENTS.md`, a nested `AGENTS.md`, `.agents/skills/`, or docs.
2. Avoid duplicating the same rule in multiple tool-specific files.
3. Confirm adapters still point to canonical guidance.
4. Run `npm run format:check:changed`.
5. Run `git diff --check`.
6. Inspect the diff for secrets, local paths, stale links, and conflicting instructions.
