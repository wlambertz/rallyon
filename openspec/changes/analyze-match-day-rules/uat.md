# Acceptance review

## Reviewed findings revision

Review [findings](findings.md), [claim inventory](inventory.md), the [source reference](../../../docs/wiki/References/Badminton-Regel-Kompendium.md), and the [independent G5 report](evidence/g5-independent.md) with its [delta recheck](evidence/g5-independent-delta.md). Lifecycle status binds the checked snapshot; changes require rechecking it.

## Summary presented for review

The supplied guide is a secondary reference, not an authoritative ruleset. Keep it with provenance and record each claim's scope and verification status. The examined BWF and NRW primary documents support some edition-specific statements but do not establish the guide's precise event, its full call sequence, its one-coach NRW limit or its Hövelhof venue rules. Current tracked scoring configuration does not establish live calls, intervals, result confirmation or officiating. Research these needs with organizers and officials before proposing product behavior. No follow-up issue is proposed yet; coordinate with existing related work before creating one.

## Maintainer walkthrough

Environment: repository working tree, documentation-only research. No deployed app or special data is needed.

1. Read the provenance banner and sample the original German source against the issue. Expected: the source is preserved and visibly identified as unverified and mixed-authority.
2. Trace M2-02–04 (calls), M3-05–06 (coach limits), and M3-01–03 (intervals) from the inventory to the findings and linked primary sources. Expected: event and edition limitations remain visible; proposed timers and a universal coach limit are not asserted as rules.
3. Read the product reconciliation and open decisions. Expected: configuration is distinguished from live behavior, human officials retain decisions, and gaps are not accepted requirements.
4. Follow the inaccessible U19 briefing link or inspect its limitation in the findings. Expected failure path: unavailable evidence is explicitly unverified, not silently substituted by the different U22 event.

No cleanup is required. Record actual human observations and acceptance only after the maintainer executes or witnesses the review.

## Limitations

The precise competition, applicable edition, NRW briefing and Hövelhof hall rules are unidentified; event-specific applicability, fine amounts and many informational claims remain unverified. The independent review is documentation and traceability review, not a legal or official rules certification. No production code, specs, issues or implementation are approved by acceptance.

## G8 human decision

Pending explicit acceptance of the reviewed findings.

## Separate merge decision

Pending explicit approval of the final reviewed revision.
