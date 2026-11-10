# G5 independent verification

Verdict: **changes requested**; one source-attribution ambiguity should be corrected before acceptance. The charter's G1 text is also stale relative to the reported lifecycle decision. Checker: `g5-check`, independent of maker, 2026-11-10.

| Severity | Location | Finding and requested action |
| --- | --- | --- |
| Minor | `findings.md`, Case and evidence | The sentence saying the club notice “also displays these facts” follows a paragraph including the reservation to change details. The [notice](https://tus-bommern.de/news/badminton/einladung-zur-jahreshauptversammlung-der-badmintonabteilung-2027.html) does not display that reservation; the linked [PDF](https://tus-bommern.de/images/2026/badminton/Ausschreibung_achte_Wittener_Stadtmeisterschaften_2026.pdf), page 1, does. Limit the notice claim to the supported event details. |
| Minor | `charter.md`, G1 decision | It says approval is pending although the maker reports a recorded human G1 decision. Refer to the lifecycle record without inventing or broadening the decision. |

## Source and scope checks

- Independently read the club announcement and notice, [NRW annex 9](https://www.badminton.nrw/fileadmin/Dateien/Broschueren-Hefte/Satzung-Ordnungen/2026/03_TO_Anlage_9.pdf), sections 1.3 and 2, [annex 10](https://www.badminton.nrw/fileadmin/Dateien/Broschueren-Hefte/Satzung-Ordnungen/2026/03_TO_Anlage_10.pdf), sections 1 and 4, and the [federation page](https://www.badminton.nrw/erleben/bei-privatturnieren/privatturniere). The case and conditional rule discussion otherwise match the cited source text; actual competition classification and interpretation remain explicitly unconfirmed.
- Checked `Tournament.kt`, `Venue.kt`, `ConfigurationController.kt`, `ConfigurationServiceImpl.kt`, and the draft-update and configuration-read specs. The cited representation, route, unimplemented service methods, conflict policy and authentication-only read statements are supported. Context ownership is described as a proposal, not a decision.
- The issue draft preserves the research/feature boundary and cites the findings. `git status --short` showed only the untracked change directory; no production, wiki, dependency, or accepted spec edits were observed. `skip_specs: true` is present; no change `specs/` directory exists.

## Commands

- `npm run format:check`: passed, exit 0.
- `./node_modules/.bin/prettier --check openspec/changes/issue-212-publication-research/{charter.md,findings.md,issue-drafts.json}`: passed, exit 0.
- `npx -y @fission-ai/openspec@1.14.2 validate issue-212-publication-research --strict`: passed, exit 0; `skip_specs` informational message.

This report does not approve G8 or issue creation. A delta recheck is required after the maker's fixes.
