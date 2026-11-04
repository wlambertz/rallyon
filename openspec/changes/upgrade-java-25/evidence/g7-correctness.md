# G7 correctness and security review

**Verdict: PASS with documented rollout limitations.** Reviewer: `g7_correct`; reviewed source head `0f2bb8619cb7f0ec8c5527ba5d5d25383191928a`. No blocking correctness or security findings in the reviewed scope.

| Severity | Location | Finding and disposition |
| --- | --- | --- |
| Risk | `infrastructure/local/docker-compose.yml`, `README.md` | Explicit `PGDATA` retains the existing named-volume path; PostgreSQL 17 data must not be treated as an automatic 18 upgrade. Isolated restart and incompatible-version tests are recorded in `bootstrap-validation.md`. Real existing layouts still require backup and inspection. |
| Risk | `service/tournamentmgmt/pom.xml`, `README.md` | Enabling Flyway changes startup against pre-existing schemas without matching history; documented review and backup remain necessary. No migration files are rewritten. |
| Limitation | `evidence/g6-qa.md` | The full formatting check remains failed on an independently reproduced `origin/main` file; changed-file formatting and runtime checks passed. Interactive editor behavior and real customer volumes were not independently exercised. |

Inspected `git diff origin/main...HEAD`, the approved requirements and design, and G6 traceability. Java and Kotlin targets, CI and dev-container versions align on 25. The IAM code and token-validation contract are unchanged. Keycloak provisioning creates a unique private file as the container user, and the documented isolated tests cover a root-owned stale file and repeat provisioning. The scanner's separate Java runtime handling is unchanged. No tests were rerun for this review; execution results are the independent G6 evidence.
