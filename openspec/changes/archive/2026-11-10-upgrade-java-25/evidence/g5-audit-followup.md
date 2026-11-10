# G5 audit delta: local bootstrap and delivery evidence

**Verdict: pending / not complete.** Rechecker: `g5_checker`. This report follows [the initial audit](g5-audit.md) for PR #209 head `bb3690b950decc77e5d6567d16d0852b14612b4b`; the maker's follow-up work was not independently rerun.

| Outcome | Finding |
| --- | --- |
| Resolved | The maker reports a successful actual `devcontainer up`/post-create/post-attach on an isolated clone, Java 25.0.5 and the resolved `JavaSE-25` editor configuration. An interactive VS Code extension session was not verified, which is a disclosed limitation. |
| Resolved | `g5-validation-followup.md` now maps the stated PR head to successful Java CI and security/SBOM runs. `gh pr view 209 --json headRefOid,isDraft,statusCheckRollup` independently confirms those displayed checks are green for the same head. |
| Blocking | The existing local authentication flow was exercised only after changing **both** the PostgreSQL 18 volume mount and provisioning script copy in the temporary stack. The unmodified documented flow did not start/provision successfully. Those workarounds are accurately disclosed, and the authenticated 201 demonstrates the application can work with repaired bootstrap, but it does not complete the approved requirement to use the existing local flow as shipped. Repair and revalidate the bootstrap under an appropriately approved scope, or obtain explicit approval of a revised validation requirement. |
| Blocking | `tasks.md` 2.1–2.4 remain unchecked. Reconcile actual completion and deviations once the unresolved local-flow requirement is addressed. In particular, do not certify 2.3 solely from the patched temporary environment. |
| Existing failure | Root `npm run format:check` remains red on unrelated tracked `openspec/changes/issue-203/.lifecycle.json`; change-specific formatting and GitHub checks are reported green. Preserve this failure as a G6 limitation and establish its pre-existing status; do not describe the full local check as passing. |
| Rollout limitation | Existing nonempty databases without matching Flyway history remain untested and can fail startup; the README documents the need for database-specific review before rollout. This does not establish that production data was validated. |

## Inputs and check

Reviewed `g5-validation-followup.md`, `flyway-repair.md`, `tasks.md` and the initial audit; verified current PR head and displayed CI statuses with `gh pr view 209`. No application, infrastructure or test files were modified by this reviewer. Independent G6 scenario mapping, G7 reviews and witnessed human UAT remain separate gates.
