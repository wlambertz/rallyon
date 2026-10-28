---
name: commit-messages
description: write descriptive RallyOn commit messages that pass `ro git lint-message`. Use whenever you are about to run `git commit`, amend or reword a commit, or write a squash/PR title, and when a commit was rejected by the commit-msg hook or the Commit message lint check.
---

# Commit messages

Generic messages such as `chore: commit current CLI and spec changes` make the log useless for review and bisecting. The rules are enforced by `ro git lint-message` (`tools/cli/ro/pkg/cmd/git_lint.go`) through `.githooks/commit-msg` and the `Commit message lint` CI check. Thresholds live in `ro.yaml` under `git.commitLint`.

## Recipe

1. Read what is actually staged: `git diff --cached --stat` and, for anything non-trivial, `git diff --cached`.
2. Subject: `type(scope): summary`, at most 72 characters, imperative, naming the concrete change and where it happens. Types: feat, fix, chore, docs, refactor, test, build, ci, perf, style, revert.
3. Body (required when more than 3 files or 100 lines change): explain what changed and why, and mention anything reviewers cannot see in the diff (deleted files, follow-ups, skipped checks).
4. One logical change per commit. If the subject needs "and" to cover unrelated things, split the commit.
5. Add your agent `Co-authored-by` trailer as required by `AGENTS.md`.

## Examples

| Rejected | Why | Better |
| --- | --- | --- |
| `chore: commit current CLI and spec changes` | names no change | `style(ro): gofmt cmd files and add yaml.v3 dependency` |
| `chore: update files` | vague verb plus plural noun | `docs: document ro git lint-message in CLI manual` |
| `fix: address review feedback` | says nothing about the fix | `fix(deploy): fail fast when workflow input is missing` |
| `wip` | no type, no content | `feat(ro): scaffold spec create command` |

## Rules for agents

- Never pass `--no-verify`, `-n`, or override `core.hooksPath`; the Claude Code hook in `.claude/hooks/check-commit.sh` blocks it and CI re-checks every PR commit.
- If a commit is rejected, fix the message and commit again; do not weaken the rules to get a commit through. To change a rule, edit `ro.yaml` or `git_lint.go` in a dedicated, reviewed change.
- Check a draft first with `printf '%s' "<message>" | bin/ro git lint-message --stdin`.
- Do not rewrite or force-push shared history just to fix a message without the maintainer's explicit approval.
