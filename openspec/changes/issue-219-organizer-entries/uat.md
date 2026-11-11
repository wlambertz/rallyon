# Organizer acceptance walkthrough

## Environment and prerequisites

Executed on 2026-11-11 against the reviewed, uncommitted `feat/issue-219-organizer-entries` implementation based on `b7f0d6a5246f579e5ce570203576a2901b24a2fd`. G5, G6 and all three G7 lenses passed before this run. No production files changed afterward.

The existing local Docker Compose workflow was isolated to PostgreSQL 18.4 (`rallyon-217-test-postgres`, localhost:64779, fresh database `rallyon_entry219_uat`) and Keycloak 26.6.4 (`rallyon-217-uat-keycloak`, localhost:18081). The verified Java 25 service JAR ran on localhost:18080. Real password-grant tokens used the provisioned test realm, audience and users: creator `dev.organizer` (42), unrelated `uat.other` (99). Only synthetic player names were used. No frontend is implemented or tested.

## Runnable actions and observations

Run `python3 openspec/changes/issue-219-organizer-entries/evidence/uat-http.py` with the isolated service, database and provisioned test realm running. The script contains exact HTTP calls and asserts statuses, book equality and cache headers without printing tokens. Its credentials are deliberately local test values. Each run creates a new private tournament.

| Scenario / organizer action | Expected result | Actual observation |
| --- | --- | --- |
| Create private tournament with singles/doubles fields; read its book as creator, anonymous user and unrelated user; try creation while closed. | CLOSED/0, no records; 401/403 for other readers; closed creation 409 without mutation. | PASS |
| Open intake; record singles and doubles, reusing one key across disciplines; submit reversed duplicate pair; attempt foreign withdrawal and stale intake change. | Two records; duplicate/foreign/stale operations rejected; complete book unchanged on failures. | PASS |
| Accept singles, correct its name, close intake, attempt creation/correction, then accept again. | Correction resets to RECEIVED; closure blocks content changes; explicit decision remains possible. | PASS |
| Remove disciplines and their empty draft rosters; read book; withdraw singles; attempt repeated withdrawal. | Stored labels/members remain unchanged; withdrawal succeeds while closed and is terminal; audit actor/time retained. | PASS |

Successful run: tournament **2**, book version **8**, two retained records, eight audit events, all attributed to creator 42. Every entry response, including expected errors, included `no-store`.

The first run completed scenarios 1–3, then the existing draft API correctly rejected removing fields while retaining their roster keys (400). The walkthrough was corrected to clear those draft rosters too; the full rerun passed. This required no production change. Its synthetic tournament 1 remains only in the isolated UAT database.

## Limits, cleanup and acceptance

This run proves authenticated HTTP-to-database behavior for the listed persona scenarios. It does not replace the separate PostgreSQL concurrency tests or establish throughput, verified player identity, automatic capacity admission or draw-roster synchronization. Concurrent draft replacement and multi-query reads remain source/isolation assessments rather than forced race tests. G7 has no unresolved blocking findings.

Cleanup: stop the task-owned service process and the two task-owned containers after recording the run; preserve their isolated data. A repeated walkthrough requires restarting those components and using a newly created tournament.

**Human acceptance: accepted.** The user replied **“ja”** in this chat to the explicit question asking acceptance of these documented UAT results. This approval applies to G8; merge requires a separate explicit decision for the final reviewed revision.
