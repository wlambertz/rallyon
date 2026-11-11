# Organizer acceptance walkthrough

## Environment and reviewed revision

Executed on **2026-11-11** against the corrected, reviewed implementation on `feat/issue-224-participant-fields`, based on `dbab018feaa06fbf077bcde1fd521270e838cbef`. No production file changed after G7. The reviewed staged service/ADR diff SHA-256 is `b7c82cd393d6362bd51df01deaab74abec4659e448631894e3cc1f41b40f2936`, recorded independently by all three review lenses.

Isolated local Docker Compose workflow: PostgreSQL 18.4 (`rallyon-217-test-postgres`, localhost:51922, fresh `rallyon_field224_uat` database), already provisioned Keycloak 26.6.4 (`rallyon-217-uat-keycloak`, localhost:18081), and the fully verified Java 25 service JAR on localhost:18080. Test realm/users: `dev.organizer` with ID 42 and unrelated `uat.other` with ID 99. Only synthetic member data was used. No frontend/browser coverage applies.

The existing repository `ro auth token --format raw` workflow supplied actual signed tokens with local test credentials via environment variables. The unchanged CLI was built temporarily at `/private/tmp/rallyon-224-ro`; tokens were captured privately and never printed. No issuer/audience/identity validation was relaxed, and no shared IAM code changed.

## Exact actions and observations

The runnable [HTTP walkthrough](evidence/uat-http.py) contains exact requests, assertions and local-only test values. Run `python3 openspec/changes/issue-224-participant-fields/evidence/uat-http.py` with those isolated components and the repository CLI available (build the unchanged CLI with `cd tools/cli/ro && go build -o ../../../bin/ro .`). Each run creates a fresh private tournament; it must not be redirected at a production environment.

| Organizer action | Expected observable result | Actual observation |
| --- | --- | --- |
| Create a private tournament and accepted singles/doubles entries; close intake; read an unprepared field as creator, anonymous user and unrelated user. Submit fractional IDs/source versions. | UNPREPARED/version 0; 401/403 for other readers/writers; malformed numbers 400 without field changes. | PASS |
| Explicitly select two singles despite bracket capacity one and closed intake; separately prepare a doubles field; try stale/unknown selections. | Canonical retained units, both pair members, CURRENT and MANUAL review; intake/source book and other field unchanged; stale 409/unknown 400 preserve contents. | PASS |
| Reopen intake, correct an already selected accepted entry, then accept it again and close intake; explicitly reconcile; withdraw the other entry. | Correction resets entry to RECEIVED; saved field stays unchanged and OUTDATED, cannot select the nonaccepted record; renewed acceptance still does not synchronize it. Explicit replacement adopts correction. Withdrawal also preserves saved field and marks divergence. | PASS |
| Rename the bracket and explicitly prepare a subset; remove the bracket; restore it; explicitly clear and repeat clearing. | Current target and original entry labels remain distinct. Removed target's field stays readable/OUTDATED, preparation returns 400. Restoration allows empty CURRENT field; each explicit operation advances version and retains minimal audit. Doubles field content/version remains unchanged. | PASS |

Actual successful run: **tournament 1**, singles field version **5**, doubles field version **1**. Five singles preparation audit events retained valid creator 42/time/PREPARE; latest singles contents are empty. All field responses, including expected failures, requested no-store. The complete walkthrough passed on its first execution. No production correction was needed during UAT.

## Validation context and limitations

Full backend verification passed **149 tests**, zero failures/errors, with three existing unrelated optional skips. Fresh independent QA passed **66 tests** without skips and checked persisted SQL invariants; all G7 lenses passed. Initial G5 detected lossy JSON coercion; its endpoint-specific correction and independent passing recheck remain documented. This walkthrough adds real authentication/HTTP/database evidence and explicitly executes correction and acceptance-after-preparation, supplementing the compositional automated coverage noted by reviewers.

This is an agent-executed persona walkthrough for the human to witness and accept, not human approval by proxy. It does not establish load capacity, all SQL interleavings, verified identities/consent, eligibility/capacity compliance, a draw or historical full-revision reconstruction. CURRENT refers to the observed snapshot and may become outdated after a concurrent/later source commit.

Cleanup: stop the task-owned local service and both task-owned containers after the run; preserve isolated test data. Restarting those components permits another walkthrough, which creates another private test tournament. No other user's containers or databases are removed.

## Human decisions

**Original revision G8 acceptance: accepted.** The user replied **“ja”** in this chat to the explicit acceptance question linking these documented results. The actual quote and source are recorded through the lifecycle helper. Merge requires a separate explicit approval of the final reviewed revision. No commit, push, PR or merge is claimed by this UAT record.

## PR 227 Sonar correction walkthrough

Rerun on 2026-11-11 after fresh G5, G6 and all three G7 delta checks, using the four-file reviewed delta SHA-256 `ae2d96e77f6c9a619756032ed75897e21ae7d1110b4539bddd3b8a6938aa5635` against `f837f47`. The corrected script executed the unchanged CLI from repository `bin/ro`, with signed tokens captured privately. All four scenario groups above **passed again**, including malformed input/privacy, manual capacity, correction/reacceptance, explicit reconciliation, removal/restoration, clearing and unchanged other-field contents. Actual result: tournament **1**, singles field version **5**, doubles version **1**.

Environment: Java 25 verified service JAR on localhost:18080, the existing local Keycloak on localhost:18081, and fresh synthetic database `rallyon_field227_sonar_uat` in PostgreSQL on localhost:54739. The script final output retains the original database label `rallyon_field224_uat`; the service JDBC configuration, not that report text, selects the actual fresh database named here. No production environment or shared IAM change was involved. Cleanup stops only the task-owned service and containers and preserves isolated data.

**Corrected revision G8 acceptance: pending.** The earlier quote “ja” accepted the original revision and is not reused for changed source. This successful agent-run walkthrough supplies evidence for a renewed human decision; merge remains a separate explicit decision.
