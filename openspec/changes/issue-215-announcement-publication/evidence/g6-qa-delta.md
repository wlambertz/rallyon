# G6 independent QA delta

Verdict: **pass**, with the limits below. Checker: `qa_g6`, 2026-11-11. This rechecks the two blocking findings and stale-withdrawal gap in [the initial G6 report](g6-qa.md).

| Finding | Result |
| --- | --- |
| Foreign-actor writes and stale withdrawal | Resolved: `AnnouncementUseCaseTest` executes foreign replace and withdrawal plus stale creator withdrawal, asserting the full original public snapshot remains unchanged. |
| Missing fields and bounds | Resolved: the same test accepts title/venue/body at 200/500/10,000 characters, rejects one-over values, missing title/body/dates, blank venue and reversed dates, and checks the original snapshot after each rejection. |

Independent command: `JAVA_HOME=/private/tmp/rallyon-jdk25/Contents/Home ./service/tournamentmgmt/mvnw -B -f service/tournamentmgmt/pom.xml -Dtest=AnnouncementUseCaseTest test` — **5 tests passed**, no failures or skips. The prior report records independently run full Maven verification and formatting; this delta changes only tests, and the independent G5 delta covers PostgreSQL.

MockMvc still mocks the announcement service, and there is no deployed end-to-end, live Keycloak or browser UAT evidence. PostgreSQL concurrency uses concurrent starts, not a deterministic SQL interleaving. These are limits on evidence, not claims that such tests ran. G7 review, human UAT and merge remain separate.
