# UAT: tournament announcement

## Persona and success measures

An organizer publishes a club tournament announcement, corrects it, withdraws it and explicitly republishes it. An anonymous prospective entrant reads only the intended current text. Another authenticated user and stale edits cannot change the announcement. A draft visibility change does not republish withdrawn content.

## Current revision, environment and setup

Source: the current uncommitted `issue-215-announcement-publication` change on base commit `0e30558`, with current G5, G6 and three G7 lenses passed. The local backend JAR was built from the current production sources using Temurin 25.0.5 and Maven `clean verify`. No organizer frontend exists.

The agent ran the service on `127.0.0.1:18080`, PostgreSQL 18.4 in the task-owned `rallyon-217-test-postgres` container using its separate `rallyon_uat` database, and Keycloak 26.6.4 in task-owned Compose project `rallyon-217-uat` on `127.0.0.1:18081`. The existing `admin/keycloak/provision_keycloak.sh` was copied to `/private/tmp/rallyon-217-uat/provision.sh` with only its Compose file redirected to the task-owned temporary Compose file. It provisioned two temporary users, creator ID 42 and other user ID 99. No existing Keycloak realm or production data was used.

To repeat while the temporary environment exists, run:

```sh
python3 /private/tmp/rallyon-217-uat/walkthrough.py
```

The script obtains short-lived tokens from the isolated Keycloak using the test-only client and users; it does not print tokens. It creates a new private draft and an explicitly authored announcement in the isolated database each run. The script and Compose file are temporary, not repository artifacts. To reconstruct them later, use the repository's `admin/keycloak/README.md` and `infrastructure/local/docker-compose.yml` workflow with a dedicated empty database and isolated realm, then follow the operations below and the endpoint contract in `service/tournamentmgmt/docs/tournament-announcements.md`.

## Scenarios

| Scenario | Actions | Expected outcome | Actual agent observation, 2026-11-11 |
| --- | --- | --- | --- |
| Publish and read | Authenticate creator 42; create a private tournament draft; POST an explicit announcement; anonymously GET its returned link. | Create 201/version 0; readable text includes venue and deadline. | Passed; 201 then 200 with intended text. |
| Reject unauthorized and stale edits | Other user 99 attempts PUT and DELETE; creator PUT uses stale version 99. Read again anonymously. | Other user gets 403; stale write gets 409; original text remains. | Passed; both 403, stale 409, original venue remained. |
| Correct and withdraw | Creator PUT with version 0 changes venue; anonymously read; creator DELETE with version 1; anonymously read again. | Correction version 1 and new venue; withdrawal version 2; public read returns 404. | Passed; returned versions and public results matched. |
| Visibility independence and explicit republication | Other authenticated user changes the draft visibility to PUBLIC; anonymously read; creator reads management view and PUTs using withdrawn version 2. | Public read remains 404 after the draft edit; creator sees withdrawn version 2 and deliberately republishes version 3. | Passed; withdrawn text stayed hidden until creator PUT, then corrected text was visible. |

## Limitations and cleanup

The script exercises real local HTTP, Keycloak JWT validation, service and PostgreSQL. It does not prove behavior of production infrastructure, externally cached copies, a frontend, federation integration or multi-user organization ownership. The concurrency tests use a start latch without guaranteeing a particular SQL interleaving. Making the tournament private does not automatically withdraw an already published announcement; the organizer must withdraw it explicitly.

After the human review is complete, stop the task-owned Java process and run `docker compose -p rallyon-217-uat -f /private/tmp/rallyon-217-uat/compose.yml down`; remove only the task-owned PostgreSQL container and `/private/tmp/rallyon-217-uat` if no longer needed. Do not remove unrelated containers, volumes or local realms.

## G8 human decision

Accepted by the maintainer. Actual quote: **“freigabe”**, in response to the presented G8 results of the four executed local scenarios. The lifecycle helper records this decision; it does not authorize merge.

## Separate merge decision

Pending explicit approval of the reviewed revision.
