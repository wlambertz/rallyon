# Tournament announcements

An announcement is an explicitly authored public text associated with an existing tournament. Its publication state is independent of the tournament's draft visibility: **making a tournament private does not withdraw its announcement**. To hide it, the creator must explicitly withdraw it. A link containing a tournament ID is public and is not a secret.

Only the authenticated user who created the tournament may publish, inspect, correct, withdraw or republish its announcement. Each write requires a valid `rallyon_user_id`; existing draft access rules are unchanged. The announcement is independent of draft content, so review every field before publishing and include no information that should not be public.

All paths below are relative to `/api/tournamentmgmt/announcements/{tournamentId}`:

| Method and path | Operation |
| --- | --- |
| `POST` | Create and publish explicit content; the response contains the public link and version. |
| `GET` | Read the current published announcement as UTF-8 plain text without authentication. |
| `GET /management` | Creator-only read of content, publication state and current version, including after withdrawal. |
| `PUT` | Creator-only full replacement with the current numeric announcement version in `If-Match`. Replacing a withdrawn announcement republishes it. |
| `DELETE` | Creator-only withdrawal with the current numeric announcement version in `If-Match`. |

The `POST` and `PUT` JSON body contains `title`, `eventStart`, `eventEnd`, `venue` and `body`. Times are ISO-8601 instants; the end cannot precede the start and the range must fit PostgreSQL's supported timestamp range. Text limits are 200, 500 and 10,000 characters respectively for title, venue and body; NUL characters are not accepted. Content is displayed as plain text, and no federation compliance is implied.

A stale version or conflicting concurrent write returns `409`; retrieve the management view and deliberately retry with the current version. A missing, unpublished or withdrawn announcement returns `404` to public readers. The service requests no caching, but cannot recall copies already downloaded by readers. Publishing, correcting or withdrawing does not notify readers or update external sites.
