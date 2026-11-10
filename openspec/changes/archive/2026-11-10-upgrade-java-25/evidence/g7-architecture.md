# G7 architecture review

**Verdict: PASS, with documented rollout limitations.** Reviewer: `g7_arch`. Source snapshot: `0f2bb8619cb7f0ec8c5527ba5d5d25383191928a`; reviewed the committed diff against `origin/main` and current G6 evidence. This is not UAT or merge approval.

| Severity | Location | Finding and disposition |
| --- | --- | --- |
| No blocking finding | Service, IAM and CLI boundaries | The diff changes build targets and provisioning rather than domain dependencies, APIs, JWT contracts or CLI behavior. Existing migrations remain unchanged. |
| Risk | `README.md`, `infrastructure/local/docker-compose.yml` | The existing data-volume mount is retained, and isolated PostgreSQL 17/18 checks support safe refusal and persistence. Actual existing volume contents and Flyway histories remain unverified; inspect and back up before rollout, as documented. |
| Risk | `3rd_party/iam/*/pom.xml`, `README.md` | Java 25 bytecode intentionally raises the minimum runtime for IAM consumers. The maintainer-facing documentation states this compatibility impact; unknown external consumers remain a deployment consideration. |

The design flags shared IAM, new Flyway integration and local provisioning changes for human review and explains the absence of a separate ADR. The scanner's separate runtime handling remains unchanged. The G6 report preserves the pre-existing failed full-format check and identifies the limits of the dev-container editor check.

## Checks

- Read the G7 reference, approved design, Java baseline delta specification, G6 evidence and repository architecture/safety rules.
- Inspected `git diff origin/main...HEAD` for the service and IAM POMs, delivery workflows, dev container, local PostgreSQL/Keycloak bootstrap and associated documentation; verified HEAD identity.
- No runtime tests rerun for this architecture lens; G6 records independently executed runtime checks.
