# Phase C pilot observations

## Planning and approval evidence

- G1 and G2 were actual maintainer replies recorded in `.lifecycle.json`; G3 used a fresh native checker context and a separate finalized report. Historical pending sections in approved planning files remain unchanged so appending decisions does not invalidate their approved hashes.
- The implementation task checklist keeps later checking/UAT/merge obligations outside its implementation checkboxes. Complete and stage implementation source/tasks before G5, because source evidence also binds the Git index. Evidence reports and the lifecycle record are excluded from that source snapshot by the existing helper.

## Formatting friction observed before G4

`npm run format:check:changed` reported `.lifecycle.json` after the helper recorded gate results: Python's two-space JSON output is not identical to the repository's Prettier formatting for short arrays/objects. This was an actual formatting failure, not a gate failure.

For this run, format the generated record with the existing Prettier command after helper writes and before formatting validation/commit, then recheck existing gate evidence. JSON whitespace does not change the parsed records or their artifact/dependency hashes. No helper behavior, formatter configuration or ignored-file rule has been changed. This manual step is recorded as workflow friction for the final pilot retrospective.

## Environment limitation

During refinement, Java 21 was available but the Docker daemon was unavailable. The daemon was subsequently started for the isolated walkthrough. Actual setup and API results are recorded in [UAT](../uat.md); explicit G8 acceptance is pending.

## Runtime friction observed during UAT preparation

The PostgreSQL 18 volume mount, copied Keycloak profile file permissions and missing automatic schema initialization each prevented the untouched local setup from working directly. Temporary runtime overrides and applying the existing V1–V5 SQL resolved the isolated environment without repository infrastructure, dependency, IAM or migration changes. The first HTTP failure is preserved, and all nine subsequent API scenarios passed. These are concrete follow-ups for local environment reliability; H2 tests use generated tables and do not establish PostgreSQL migration startup correctness. No behavioral lifecycle correction was needed during this pilot.
