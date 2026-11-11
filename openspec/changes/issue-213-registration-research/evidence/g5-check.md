# G5 independent research verification

Verdict: **changes requested**. Checker: `research-213-check`, 2026-11-11.

| Severity | Location | Finding and fix |
| --- | --- | --- |
| Minor | `findings.md`, actor table, Doubles pair row | “Partner confirmation is observed” goes beyond the source. The event page says both players must enter to complete the online entry; no separate confirmation or consent mechanism was inspected. Describe only this stated completeness condition. |

## Source and scope checks

- Independently read the [club notice](https://tus-bommern.de/news/badminton/einladung-zur-jahreshauptversammlung-der-badmintonabteilung-2027.html): date, event, email/online channels and requested entry details support the bounded case claims.
- Reached the [event page](https://dbv.turnier.de/tournament/A046428D-B07E-46D8-8B94-41EF1DDD96F6) through the notice's link: both-player entry condition and published opening/entry/withdrawal information confirmed. Individual entry redirected to login; the regulations link failed. No entry was submitted. These are page observations, not verified backend behavior.
- Independently read [NRW annex 10](https://www.badminton.nrw/fileadmin/Dateien/Broschueren-Hefte/Satzung-Ordnungen/2026/03_TO_Anlage_10.pdf), sections 3 and 4. The findings preserve the JWS applicability limit rather than imposing these procedures on the case.
- Checked `ParticipantsRoster.kt`, `DraftReplacement.kt`, `RegistrationPolicy.kt`, `TournamentStatus.kt`, `ParticipantEntity.kt`, named service stubs, the draft-update spec, announcement use case, context map and glossary. The stated code capabilities, limits and unresolved identity ownership are supported.
- Invite-only behavior and candidate states are clearly hypotheses. The recommendation and follow-up draft do not approve production changes, new contexts, dependencies or identity mappings.
- `git status --short` shows only the untracked research change. No production, test, runtime, wiki or accepted-spec edits; `skip_specs: true`, no delta specs.

## Commands

- `npm run format:check` — passed.
- `./node_modules/.bin/prettier --check 'openspec/changes/issue-213-registration-research/*.{md,json}'` — passed.
- `npx -y @fission-ai/openspec@1.14.2 validate issue-213-registration-research --strict` — passed, with expected skip-specs information.

No G8, follow-up creation or merge approval is implied. Recheck the source-attribution correction before passing G5.
