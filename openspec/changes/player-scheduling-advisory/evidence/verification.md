# Independent QA verification

Verdict: **pass for G6**, with coverage limitations below. Checker: `/root/advisory_qa`, fresh native Codex context; no implementation edits or helper gate recording.

| Severity | Location | Finding |
| --- | --- | --- |
| Low | `PlayerSchedulingAdvisoryTest.kt` | Scenario coverage is present, but explicit assertions for affected match IDs, estimated-start recovery, disjoint identifiers, and invalid rule metadata would strengthen regression protection. Source inspection confirms those paths; no blocking scenario failure found. |

## Scenario traceability

All tests below are in `PlayerSchedulingAdvisoryTest`; names are abbreviated.

| Approved scenario | Test and inspected assertions |
| --- | --- |
| Doubles and singles overlap | `doubles member...`: actual overlap, player `p`, four supporting times; source attaches both match IDs. |
| Proposed times overlap | `doubles member...`: proposed start produces potential overlap; source retains original time kinds. |
| Adjacent intervals | `adjacent intervals...`: no actual overlap at equality; source uses strict half-open comparisons. |
| Proposed start has insufficient recovery | `known end permits...`: 10:30 actual end and 10:45 proposed start produce a concern with exact time facts and supplied rule; both input orders checked. |
| Call is not start | `call does not provide...`: supplied rule and call at 10:45 still produce recovery-order unresolved and no concern when start is missing. |
| Exact recovery boundary | `adjacent intervals...`: no recovery concern at exactly 30 minutes. |
| Unknown doubles membership | `uncertain membership...`: incomplete membership produces player-independent unresolved finding. Source emits this even without shared confirmed IDs. |
| Unknown rule | `uncertain membership...`: known starts with absent rule produce a rule-related unresolved diagnostic; no numeric default exists in source. |
| Estimated end | `estimated end...`: estimated end produces unresolved; source excludes it from recovery calculations. |
| Delayed match and correction | `delay and correction...`: 10:30 to 10:50 end correction against 11:00 proposed start creates a concern with provenance; reassessing original facts clears it. |

Invalid interval and duplicate match IDs are rejected in tests. Source additionally rejects blank identifiers, blank player IDs, negative recovery and blank provenance. Findings are returned from a stateless operation with no caller-list, match, persistence, or schedule mutation. Existing REST/JWT/CLI/schema contracts are untouched; no new security interface requires security-specific test additions.

## Commands, environment and limitations

- Independently inspected current Kotlin source/tests, approved spec/design/tasks/issue criteria and maker log `/private/tmp/rallyon-advisory-verify.log`.
- Maker's `JAVA_HOME=/private/tmp/rallyon-jdk25/Contents/Home ./service/tournamentmgmt/mvnw -B -f service/tournamentmgmt/pom.xml clean verify` completed successfully on Java 25.0.5 at 12:21:53 on 2026-11-13: 98 tests, zero failures/errors, three skipped; all seven advisory tests passed. Runtime was not redundantly rerun by this checker.
- Independently ran `npm run format:check`: passed.
- Independently ran `npx prettier --check 'openspec/changes/player-scheduling-advisory/**/*.md'`: passed before adding this report.
- Independently ran pinned OpenSpec 1.14.2 `validate player-scheduling-advisory --strict`: passed.
- Two PostgreSQL integration tests and opt-in Modulith documentation generation were skipped. H2 tests do not establish PostgreSQL/Flyway behavior. This pure domain increment has no deployed or organizer UI workflow; no browser or end-to-end operational evidence is claimed. Human UAT and merge remain separate decisions.
