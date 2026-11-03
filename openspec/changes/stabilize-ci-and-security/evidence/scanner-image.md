# Pre-implementation scanner identity

Read-only `docker buildx imagetools inspect owasp/dependency-check:13.0.0` resolved the official OCI index to:

`sha256:0e8f69ad5b00d62000937f4e57189812343f6903d9c83ee712012d51542a020a`

The linux/amd64 manifest is `sha256:a56d7e182706a22fdf1a5f70421fa415b9a096c9a4ba5a479cd4936e90dd8ba9`; linux/arm64 is `sha256:884c94a15e4579262665ba52c27927b7ee7ee9de5cfe4b30f10e30f2fcedf797`.

This confirms a registry identity only. No scanner run, data-feed update, vulnerability resolution or cold/warm cache validation has been claimed. The existing wrapper action's pinned Dockerfile starts from `owasp/dependency-check-action:latest`, so its action commit does not freeze the runtime scanner image.
