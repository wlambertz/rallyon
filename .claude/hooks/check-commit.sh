#!/usr/bin/env bash
# PreToolUse hook for Bash: stop agents from bypassing the commit-msg hook
# (.githooks/commit-msg, rules in `ro git lint-message`). The message itself is
# linted by that hook at commit time, so its feedback reaches the agent as a
# failed `git commit`; parsing -m/heredoc text here would be fragile.
set -euo pipefail

cmd="$(jq -r '.tool_input.command // empty' 2>/dev/null || true)"
[[ "$cmd" == *commit* ]] || exit 0

# Only look at segments that start with `git ... commit`, and drop quoted text
# first, so docs or messages that merely mention --no-verify are not blocked.
segments="$(printf '%s\n' "$cmd" | sed -E "s/\"[^\"]*\"//g; s/'[^']*'//g" | sed -E 's/(&&|\|\||;|\|)/\n/g')"
while IFS= read -r seg; do
  [[ "$seg" =~ ^[[:space:]]*git([[:space:]].*)?[[:space:]]commit([[:space:]]|$) ]] || continue
  if [[ "$seg" =~ (^|[[:space:]])(--no-verify|-[a-zA-Z]*n[a-zA-Z]*)([[:space:]]|$) ]] || [[ "$seg" == *core.hooksPath* ]]; then
    echo "Blocked: do not skip commit hooks (--no-verify / -n / core.hooksPath). Fix the message instead; see .agents/skills/commit-messages/SKILL.md." >&2
    exit 2
  fi
done <<<"$segments"
exit 0
