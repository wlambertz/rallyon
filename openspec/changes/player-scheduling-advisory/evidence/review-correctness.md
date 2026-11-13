# Correctness and security review

Verdict: **pass**. Reviewer: `/root/review_correctness`, fresh native Codex context, separate from the maker. No implementation edits or helper approval recorded.

No blocking findings.

| Severity | Location | Finding |
| --- | --- | --- |
| Low | `AdvisoryMatch.confirmedPlayers` and `assess(matches)` | Kotlin read-only collections do not enforce deep immutability. The approved caller-supplied snapshot must remain stable during assessment; a later concurrent adapter should take defensive copies or explicitly enforce that ownership contract. No current caller or concurrent workflow is introduced here. |

## Reviewed behavior

- Confirmed IDs are intersected exactly; unrelated IDs are not unified. Incomplete membership produces unresolved findings independently of shared confirmed IDs.
- Strict interval comparisons implement half-open overlap boundaries. Only four actual time facts produce actual overlap; estimates/proposals retain their kinds and produce potential overlap.
- Recovery uses an actual end and actual/proposed start with the supplied rule and its provenance. Estimated starts/ends and absent rules remain unresolved. Calls are not used in calculations. Equality does not produce insufficient recovery.
- The G5 fix supports an actual end before a subsequent start when the earlier start is absent, retaining occupied-interval uncertainty; ambiguous order remains unresolved.
- Invalid intervals, duplicate match IDs, blank identifiers/provenance and negative recovery are rejected. The implementation neither changes schedules nor stores prior results.
- All changes stay in internal domain types and tests. No authentication, secrets, migrations, dependencies, published API or CLI changes occur. Caller-attested rule applicability and identity are explicit limits, not claims of external verification.

## Evidence and limits

Read the current two Kotlin files, approved proposal/design/specification, QA verification and G5 recheck. Independently inspected working-tree scope and source hashes. Source inspection supports the approved scenarios; no additional runtime rerun was needed for this review. QA records Java 25 clean verification with 98 tests, zero failures/errors, three skips. PostgreSQL behavior and organizer/deployed workflows remain outside this evidence.
