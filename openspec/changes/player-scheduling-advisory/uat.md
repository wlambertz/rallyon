# Human acceptance walkthrough

Status: **accepted by wlambertz on 2026-11-13** for the current snapshot following presentation of the walkthrough and test results. Merge requires a separate decision.

## Environment and revision

Local macOS workspace, Java 25.0.5, base `cd154dd` plus the uncommitted advisory implementation and tests. The current backend verification passed 156 tests with five skipped. See the [completion recheck](evidence/implementation-current.md) and [QA recheck](evidence/verification-current.md); the earlier [validation](evidence/maker-validation.md), [QA](evidence/verification.md) and [review](review.md) describe the preceding snapshot.

This increment has no caller, endpoint or organizer UI. Docker Compose cannot expose this pure domain operation; the bounded demonstration below uses the real domain tests and their assertions. It establishes internal advisory behavior only. Operational organizer acceptance needs later integration. Four PostgreSQL tests and opt-in documentation generation were skipped in full verification.

## Run and inspect

From the repository root, inspect `PlayerSchedulingAdvisoryTest.kt` under the tournament domain test package, then execute:

```sh
JAVA_HOME=/private/tmp/rallyon-jdk25/Contents/Home ./service/tournamentmgmt/mvnw -B -f service/tournamentmgmt/pom.xml test -Dtest=PlayerSchedulingAdvisoryTest
```

The seven tests must pass. Review the assertions for each scenario below while executing or witnessing the run. The test report is in `service/tournamentmgmt/target/surefire-reports/`. No fixture data is persisted and no cleanup is required.

| Scenario and supplied data | Expected observable assertion | Actual human observation |
| --- | --- | --- |
| Doubles member also in singles; intervals 10:00–10:30 and 10:20–10:50 | Actual overlap for player `p`; proposed start changes it to potential | Pending |
| Adjacent matches and exactly 30-minute recovery | No overlap at adjacency; no recovery concern at equality | Pending |
| Actual end delayed from 10:30 to 10:50; next proposed start 11:00 | Recovery concern retains supplied rule and proposed start; correcting the end removes it | Pending |
| Earlier start unknown; actual end 10:30 and next proposed start 10:45 | Recovery concern with exact evidence, plus unresolved occupied interval, in either input order | Pending |
| Missing start but call at 10:45 and supplied rule | Recovery remains unresolved; call does not produce a calculation | Pending |
| Incomplete membership, missing rule or estimated end | Explicit unresolved findings | Pending |
| Invalid reversed interval or duplicate match IDs | IllegalArgumentException, with no silent normalization | Pending |

## Acceptance record

Current snapshot: wlambertz, 2026-11-13. Actual acceptance quote: `accept`. Source: conversation reply to the current G8 UAT handoff. No separate scenario-level observations were supplied; the table retains those unreported observations as pending. The walkthrough covers the bounded domain increment; it does not establish official rule applicability, trusted identities or operational UI readiness.

Prior snapshot: wlambertz, 2026-11-13. Actual acceptance quote: `accepted`. Source: conversation reply to the earlier G8 UAT handoff.
