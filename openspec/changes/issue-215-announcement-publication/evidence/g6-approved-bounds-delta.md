# G6 QA delta: approved input bounds

Verdict: **pass** for the revised specification and current implementation. Checker: `qa_g6`, 2026-11-11. This resolves the blocking specification mismatch in [the prior G6 delta](g6-g7-fixes-delta.md); the prior G6 reports retain the broader scenario trace.

| Check | Result |
| --- | --- |
| Specification and design | ANN-005 now expressly rejects U+0000 and records the inclusive event-instant bounds. The lower bound, 4713 BC, corresponds to ISO astronomical year `-4712`; the implementation and design use the same endpoints. |
| Lifecycle freshness | Independently queried helper `status`: G1–G5 passed, G6 pending; the recorded G2 snapshot reflects the revised inputs. The helper remains authoritative for actual human decisions. |
| Executable trace | Unit cases reject NUL in each text field and `Instant.MIN`/`MAX`, preserving the original snapshot; the independent [G5 boundary delta](g5-approved-bounds-delta.md) round-trips both exact endpoints against PostgreSQL 18.4 and records two passed integration tests. Controller/advice source maps invalid input to 400. |

Independent command: `JAVA_HOME=/private/tmp/rallyon-jdk25/Contents/Home ./service/tournamentmgmt/mvnw -B -f service/tournamentmgmt/pom.xml -Dtest=AnnouncementUseCaseTest test` — **5 passed, 0 failures or skips**. Prettier checks on the updated spec, design and G5 delta passed. Earlier independent G6 full verification and G7-fix focused checks are recorded in [the original QA](g6-qa.md) and [the previous delta](g6-g7-fixes-delta.md).

Limits: this delta did not independently rerun PostgreSQL or execute a real HTTP-to-PostgreSQL invalid-input flow; the G5 checker ran the database boundary test. Unit tests use extreme out-of-range values rather than values immediately outside each endpoint. MockMvc does not exercise a live Keycloak issuer or deployed end-to-end service, and concurrent starts do not force every SQL interleaving. G7 review, UAT and merge remain separate.
