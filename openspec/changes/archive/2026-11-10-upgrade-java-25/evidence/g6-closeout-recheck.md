# G6 closeout QA delta recheck

**Verdict: PASS for the QA delta, with historical runtime and rollout limits.** Independent checker: `/root/g6_recheck`; current main `2d41479`; earlier tested application source `0f2bb86`. This is not UAT or merge approval.

| Scenario group | Evidence and disposition |
| --- | --- |
| Java 25 build and blocked-upgrade path | [Prior independent G6](g6-qa.md) maps the IAM and service tests, bytecode target and historical incompatibility to [preflight](java25-preflight.md) and [repair](flyway-repair.md). One existing service test was skipped. |
| Dev container, scanner and consumers | [Prior G6](g6-qa.md) maps dev-container startup, editor configuration, scanner runtime and IAM minimum to their evidence. The editor extension was not exercised interactively. |
| Service, token validation and migrations | [Prior G6](g6-qa.md) maps health, authenticated draft, invalid claims and both successful and failed migration paths to its independent tests and the isolated bootstrap evidence. |
| Existing database and provisioning | [Prior G6](g6-qa.md) maps isolated PostgreSQL 17/18 protection and Keycloak provisioning to maker-run [bootstrap evidence](bootstrap-validation.md). Real database histories and volume layouts were not validated. |

No service, IAM, workflow, dev-container, Keycloak or local infrastructure file differs between application head `0f2bb86` and current main. The implementation evidence therefore remains applicable to that unchanged source; it does not establish a fresh runtime execution. I independently confirmed PR #209's displayed build/test, security scan, SBOM and other checks pass. `npm run format:check`, strict OpenSpec validation, `bash -n admin/keycloak/provision_keycloak.sh` and `git diff --check` pass. The format check now excludes generated lifecycle JSON, so its success does not prove the previously failing issue-203 JSON was reformatted.

JDK 25 Maven, Docker, dev-container and API tests were not rerun: this host lacks JDK 25 and the Docker daemon is stopped. The previously recorded isolated executions and their stated limitations remain the runtime evidence.
