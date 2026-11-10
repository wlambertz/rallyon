# G7 correctness and security closeout delta

**Verdict: PASS for the closeout delta, with the previous runtime and rollout limitations.** Independent reviewer: `/root/g7_correct`; compared application head `0f2bb86` to main `2d41479` and the new untracked main specification against its approved delta. This is not UAT or merge approval.

| Severity | Finding and disposition |
| --- | --- |
| None | No service, shared IAM, Keycloak provisioning, local infrastructure, dev-container or workflow file changed since the earlier correctness review. The intervening hooks, lifecycle documentation, format configuration and evidence do not alter the reviewed Java, JWT, migration or database startup behavior. |
| None | The new main specification preserves the delta's purpose, six requirements and all scenarios exactly; only the main-spec title and requirements heading differ. This adds no new behavior or security requirement. |
| Risk | Existing database volumes and Flyway histories still need inspection and backup before rollout. The earlier isolated PostgreSQL 17/18 checks do not establish compatibility for all real databases. |
| Limitation | No JDK 25, Docker, API, or authentication runtime checks were rerun in this delta review. Prior [correctness](g7-correctness.md), [QA](g6-qa.md), and [closeout QA](g6-closeout-recheck.md) evidence and their limitations remain applicable; one service test was skipped and interactive editor behavior was not verified. |

Inspected the raw intervening hook, documentation and format-configuration diff, design and prior review reports; compared main and delta specs with `git diff --no-index`; confirmed the implementation-area diff is empty. `git diff --check 0f2bb86 HEAD` and strict OpenSpec validation of `java-build-baseline` passed. The current full format check's exclusion of generated lifecycle JSON does not establish that the formerly failing JSON was reformatted.
