# G5 staged delta check

**Verdict: Pass.** The staged reference preserves the supplied German source. No lifecycle gate was recorded.

- `git diff --cached --check` passed with no output.
- Compared the staged reference from `git show :docs/wiki/References/Badminton-Regel-Kompendium.md` with the full reference text fetched from issue #184: the text matches exactly after excluding terminal newlines and the separate provenance banner. Removal of the trailing blank line does not change the source content.
- The staged paths comprise the named reference and research change artifacts; unrelated work remains untracked. This check is limited to the staged delta and source integrity; see [the previous delta report](g5-merge-delta.md) for the base and link review.
