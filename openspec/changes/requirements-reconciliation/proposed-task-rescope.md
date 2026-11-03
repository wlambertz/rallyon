# Proposed task rescope for issue 183

This is a review proposal; it does not change the approved `tasks.md` or its G4 snapshot. Issue #183 asks for analysis, candidate specifications and linked follow-ups, without production implementation. Issue [#203](https://github.com/wlambertz/rallyon/issues/203) was created and approved as the analysis follow-up that selects a real competition, completes standings examples and resolves proposed ownership. The current G4 task list also requires these same downstream decisions within this change. That duplication makes #183 depend on completing the newly created follow-up, even though the requested output is a traceable analysis and follow-up.

## Proposed change to the G4 task list

Replace tasks 1.1–1.3 with:

- [ ] 1.1 Link #203 to STANDINGS-001–002 and document its unresolved competition, eligibility, correction, ownership and compatibility decisions; completion check: #203 exists with those scope and acceptance conditions, and this analysis does not claim the follow-up or implementation is complete.

Retain tasks 1.4 and 1.5 as the independent source-by-source analysis and validation. Keep human acceptance and separate merge approval, and continue to exclude production implementation. Preserve the existing approved `issue-drafts.json`; do not create another issue.

## Effect and decision

This would leave competition selection and an independently reviewed architecture decision in #203, exactly where its approved issue body places them. It would not resolve or waive any open rule question, claim applicability to the U11 example, or authorize implementation. #183 must still meet its own completeness, classification, source, prioritization and independent review criteria.

The proposal materially changes G4-bound task definitions. Before adopting it, show the actual replacement task text and current issue payload to the maintainer and obtain the lifecycle decision required for that changed G4 snapshot. After editing, rerun helper status and independent checks; prior G5 failure reports are not retroactive passes. The helper checks that the recorded issue payload is unchanged at G5 and skips IDs already recorded during issue creation. The existing #203 payload must stay byte-for-byte equivalent; it must not be recreated or represented as a new issue.
