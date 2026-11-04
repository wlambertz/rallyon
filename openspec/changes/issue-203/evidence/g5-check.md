# G5 independent research check

**Verdict: fail; corrections and a delta recheck are required.**

| Severity | Location | Finding and requested fix |
| --- | --- | --- |
| Moderate | `findings.md`, source locators | The regulation's table of contents places Anlage IV Teil 2 § 10 on printed p. 235, not p. 236. Review the other printed-page references for the implementing provisions and youth § 30 as well, and distinguish printed from PDF page numbers. Correct locators to make verification reproducible. |
| Moderate | `findings.md`, example 4 | The approved charter calls for reviewable lot outcomes; the example describes a pending decision but no recorded outcome or its effect. Include an explicitly hypothetical authorized outcome and resulting treatment, without asserting an unverified procedure. |

The DBV primary edition confirms the quoted § 10 sequence and the conditional nature of application is clearly stated. Its implementing provisions name DBV as organizer and apply Anlagen III and IV to individual championships. The association report names the 2027 event and host. The youth § 30 belongs to youth ranking rules, so its withdrawal treatment must not be transferred. Current `TieBreakRules`, mapper, context map and STANDINGS candidates support the described representation and proposed ownership. Other examples are explicitly conditional or product proposals. The open event-specific format and exception rules are not represented as verified. The only worktree changes reported by `git status --short` are inside the change; no production changes were found.

Checks run independently:

- `npm run format:check` — pass (tracked files).
- `npx prettier --check openspec/changes/issue-203/charter.md openspec/changes/issue-203/findings.md` — pass (untracked artifacts).
- `npx -y @fission-ai/openspec@1.14.2 validate issue-203 --strict` — pass, `skip_specs` acknowledged. The sandboxed invocation did not return promptly; the escalated invocation passed.

Sources inspected: the DBV 2026/27 rules and 2026 association report linked in the findings, plus the cited repository paths and issue #185. No event-specific announcement was established by this check.
