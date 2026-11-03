# UAT: retrieve a saved tournament configuration

## Persona and success measures

Organizer resumes setup by retrieving saved configuration. Success means the existing API returns a partial draft and its committed update, repeated reads preserve the stored snapshot, a missing ID gives 404 ProblemDetail, and anonymous or malformed-token requests remain unauthorized. The approved authentication-only policy is separately covered by automated cross-creator/no-role tests.

## Current revision, environment and setup

- Reviewed source/index fingerprint: `8cfadcd552127a571395c3b979192a3f686ac846457f61cd6b29a17887d938ac`, baseline `9ae7655`, issue #194.
- G5, G6 and all three G7 lenses passed; their exact evidence is linked from `verification.md` and `review.md`.
- Local Compose project `rallyon-phase-c`, based on the existing repository Compose and Dockerfiles. A temporary untracked override outside this worktree isolates container names and database host port 55432, leaving the existing unrelated database on 5432 untouched.
- The PostgreSQL 18.4 image requires mounting its volume at `/var/lib/postgresql`; this is corrected only in the temporary runtime override. The repository's Compose file is unchanged.
- Keycloak runs on localhost:8081, service on localhost:8080. Existing realm provisioning is reused with temporary environment-held credentials. A temporary provisioning wrapper grants read access to the copied non-secret user-profile JSON, whose host-created 0600 mode otherwise prevented the Keycloak runtime user from reading it. No repository auth/bootstrap code or JWT settings changed.
- Credentials are stored outside the repository with mode 0600 for this local session. Bearer tokens remain in process memory and are not recorded in evidence.

## Runnable walkthrough

The standard-library [walkthrough script](evidence/run-api-walkthrough.py) requires current G7 evidence and obtains a real local token through the existing `ro auth token --format raw` command. Supply `RALLYON_CLIENT_SECRET` and `RALLYON_DEV_PASSWORD` in the environment; optionally point `RO_BINARY` at an installed `ro`. Do not paste secrets into command arguments or commit them.

```bash
python3 openspec/changes/retrieve-tournament-configuration/evidence/run-api-walkthrough.py
```

It creates one uniquely named pilot record, uses its returned ID and persisted version for replacement, compares repeated full snapshots, and writes only token-free observations to [results](evidence/api-walkthrough-results.json). The record is retained in the isolated local database for inspection; this script does not delete existing data.

## Scenarios and observations

Executed against the real local Keycloak/PostgreSQL service on 2026-11-02 at 14:41 UTC; all nine requests returned their expected status. The [token-free results](evidence/api-walkthrough-results.json) retain the observed responses. Human acceptance remains pending.

| Request | Observed response |
| --- | --- |
| POST `/api/tournamentmgmt/config/drafts?organizerId=42` with name and PRIVATE visibility | 201; ID 1, DRAFT, version 0 |
| GET `/api/tournamentmgmt/config/1` | 200; saved partial draft, null schedule and venue |
| Repeat GET | 200; full response exactly equals the previous stored snapshot |
| PUT `/api/tournamentmgmt/config/1/draft` with `If-Match: 0`, updated name and description | 200; existing update response precedes flush and still reports version 0 |
| GET after update | 200; committed name/description, version 1, creator audit retained |
| Repeat GET after update | 200; full response exactly equals the committed snapshot |
| GET `/api/tournamentmgmt/config/0` | 404; `Not Found`, `Tournament 0 was not found` |
| GET saved ID without bearer token | 401 |
| GET saved ID with malformed bearer token | 401 |

The stored creation timestamp remained `2026-11-02T14:41:57.561791Z`. The committed modification timestamp was `2026-11-02T14:41:57.997503Z`; creator and modifier IDs were 42. PostgreSQL stores microsecond precision, while existing create/update responses expose pre-flush nanoseconds. Repeated reads preserved the stored precision and all other response fields. The local pilot record is retained for inspection.

## Limitations and failures

This is a local API walkthrough, not browser usability validation or a production deployment. The Java runtime in the existing service Dockerfile differs from the Java 21 build/test host. Environment workarounds above are temporary and do not silently change repository infrastructure. The [first attempt](evidence/api-walkthrough-first-attempt.json) failed at create with 500: `tournamentmgmt.tournaments` did not exist. Startup was healthy but did not run the shipped migrations. To initialize only this isolated empty database, apply the unchanged V1–V5 files in filename order, each using:

```bash
docker exec -i rallyon-phase-c-db psql -U rallyon_app -d rallyon \
  --single-transaction -v ON_ERROR_STOP=1 < path/to/existing/migration.sql
```

This manual SQL setup does not establish Flyway history or validate automatic migration startup. Automatic startup migration wiring remains an observed follow-up outside this pilot; no dependency or migration files were modified. An intermediate runner invocation also failed before HTTP because its wrapper supplied incorrectly named credential environment variables; correcting the wrapper to the documented `RALLYON_CLIENT_SECRET`/`RALLYON_DEV_PASSWORD` names resolved it without source changes. The successful run above followed these setup corrections. Neither failed attempt counts as acceptance.

## G8 human decision

Pending the maintainer's explicit acceptance after reviewing the actual observed walkthrough results. An automated run alone does not pass G8. The Phase B UAT waiver does not apply.

## Separate merge decision

Pending explicit human approval for the final reviewed revision.
