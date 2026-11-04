# G5 bootstrap delta review

**Verdict: pending final task and evidence reconciliation.** Reviewer: `g5_checker`; source head `0f2bb8619cb7f0ec8c5527ba5d5d25383191928a`. This delta follows [the previous audit](g5-audit-followup.md). No source-level blocker was found in the bounded bootstrap changes; the maker's reported container executions were not independently rerun.

| Outcome | Finding |
| --- | --- |
| Prior bootstrap blocker resolved | The current Compose configuration retains the existing mount and sets `PGDATA`; the provisioning script creates a private uniquely named file as the container user and writes through stdin. The maker reports successful isolated fresh/existing PostgreSQL 18, safe PostgreSQL 17 refusal, stale root-owned file, repeated provisioning and real-token HTTP 201 checks. Only names and ports differed in the isolated Compose stack. README documents the manual major-version migration. |
| CI green for current head | `gh pr checks 209` reports all displayed checks passed, including [Java CI](https://github.com/wlambertz/rallyon/actions/runs/41899289468), [security scan and SBOM](https://github.com/wlambertz/rallyon/actions/runs/41899289391), lifecycle and commit lint. Record these current-head outcomes in the implementation evidence; the earlier follow-up documents only `bb3690b`. |
| Pending | `tasks.md` still leaves 0.2, 2.1, 2.2 and 2.4 unchecked. The maker must reconcile each item against actual results and current-head evidence; the checkboxes alone neither establish nor negate success. `bootstrap-validation.md` also still says hosted CI has not run for the new revision and should be clearly time-qualified when recording the later results. |
| Pre-existing check failure | `npm run format:check` remains red on `openspec/changes/issue-203/.lifecycle.json`. I extracted that file from `origin/main` and ran `prettier --check --parser json`; it also fails there. This is an established baseline failure, not evidence of a regression in this diff, but the required full check has **not passed** and must remain an explicit G6 limitation. Do not alter the unrelated file as part of this change or describe the check as green. |
| Rollout limitation | Real populated databases with differing Flyway histories and every PostgreSQL volume layout were not exercised. The maker tested selected isolated 17/18 layouts and documented the upgrade and history risks; no production compatibility claim is supported. |

## Inputs and commands

Read the prior delta audit, `bootstrap-validation.md`, `g5-validation-followup.md`, current tasks and specification; inspected `git diff bb3690b..HEAD` for the Compose and provisioning changes and the associated README note. Checked the PR head/status with `gh pr view` and `gh pr checks`; checked the unrelated formatting baseline with `git show origin/main:openspec/changes/issue-203/.lifecycle.json` and Prettier. Independent G6/G7 and witnessed human UAT remain separate decisions.
