# G6 QA delta after G7 fixes

Verdict: **pending specification reconciliation**. Checker: `qa_g6`, 2026-11-11. This checks the changes after [the previous G6 delta](g6-qa-delta.md) and the three G7 review findings.

| Severity | Finding |
| --- | --- |
| Blocking | `specs/tournament-announcement-publication/spec.md`, ANN-001, and `Announcement.kt`: the accepted specification requires nonblank text within size limits and an ordered time range, but the implementation now rejects NUL characters and instants outside an additional fixed range. Those are sensible PostgreSQL protection measures, with tests and service documentation, but they are additional restrictions on previously accepted inputs not stated in the approved requirement. Resolve this specification ambiguity under the lifecycle before treating the changed behavior as approved. |

## Rechecked evidence

- The G7 security fix rejects NUL in all three text fields and `Instant.MIN`/`MAX` in unit tests; actual PostgreSQL tests cover first publication and replacement with NUL and replacement with an extreme instant, including preservation of published data. Exact finite timestamp boundaries have not been independently round-tripped.
- The G7 test fix supplies distinct correction snapshots, checks one success and one conflict, and compares the stored full content with the winner. PostgreSQL concurrent starts remain nondeterministic in their exact SQL interleaving.
- The JPA adapter uses PostgreSQL conflict-safe insertion and version-qualified update; the independently rerun G5 delta reports seven PostgreSQL/domain tests passed. MockMvc remains separated from the real persistence service; no live Keycloak or deployed end-to-end claim is made.
- Independent `JAVA_HOME=/private/tmp/rallyon-jdk25/Contents/Home ./service/tournamentmgmt/mvnw -B -f service/tournamentmgmt/pom.xml -Dtest='*Announcement*Test,ConfigurationControllerSecurityTest' test`: **26 passed, 0 failures, 0 skipped**. The maker reports full `clean verify` with 104 tests and three expected skips; the prior independent G6 full verification is recorded in [the original report](g6-qa.md).

The additional input limits and their public contract need a decision before a G6 pass. This report does not approve G7, UAT or merge.
