# PR 1 local validation before design correction

Observed 2026-11-03 against dependency commit `025a152` and in-progress scanner/workflow changes. This is maker evidence, not independent G5/G6 approval. G5–G8 remain pending.

| Check | Actual result |
| --- | --- |
| Shared IAM reactor install and tests | 12 tests, no failures/errors/skips; generated RSA keys and loopback JWKS only |
| Service clean verify | 89 tests discovered, 88 executed successfully, 1 existing opt-in Modulith documentation skip, no failures/errors |
| Go `go test ./...` | Passed all packages |
| Security policy/runner fixtures | 15 passed after CPE-format correction; includes simulated nonzero finding/feed/runtime exits, credential cleanup and event trust |
| Lifecycle helper | 32 passed |
| Claude lifecycle hook | 10 passed |
| Schema readiness | 1 passed with cached OpenSpec 1.14.2 |
| OpenSpec strict all validation | 12 items passed |
| OpenSpec rallyon-feature schema validation | Passed |
| Actionlint 1.7.12 | Security workflow passed; shellcheck unavailable and not run |
| Prettier | Root formatting check passed before final evidence additions; repeat before commit |
| Relative documentation links | New operation/triage/blocker document links resolve locally |
| Real CycloneDX | Generated successfully; all 145 prepared runtime PURLs present |
| Actual official pinned container | `--version` reports 13.0.0 |
| Actual cold NVD scan | Failed, exit 13, upstream no-key bug; no findings report |
| Retry using incomplete cache | Failed, exit 13; not a successful warm-cache check |

The initial independent precheck detected ineffective CPE 2.3 rules; makers corrected those and added regression coverage. A final review of exact engine matching remains required. No post-upgrade security clearance, actual GitHub CI result, UAT or merge approval is claimed. No live auth requests, releases, tags, image pushes or deployments were used.
