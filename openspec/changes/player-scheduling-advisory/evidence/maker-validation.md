# Maker validation

Validated by Codex on 2026-11-13 using Java 25.0.5 from `/private/tmp/rallyon-jdk25/Contents/Home`.

- Backend `clean verify` after review fixes: passed, 98 tests, zero failures/errors, three skipped. Seven advisory tests passed. Two PostgreSQL integration tests and opt-in Modulith documentation generation were skipped; PostgreSQL behavior is not established by this run. Log: `/private/tmp/rallyon-advisory-verify.log`.
- `npm run format:check`: passed. Log: `/private/tmp/rallyon-advisory-format.log`.
- Explicit `npx prettier --check 'openspec/changes/player-scheduling-advisory/**/*.md'`: passed before this report was added.
- Pinned OpenSpec 1.14.2 strict validation: passed.
- Shared IAM installation was unnecessary: existing local artifacts resolved successfully.

The initial sandboxed backend attempt failed because Kotlin could not write daemon files outside the workspace. The successful rerun used approved access. The initial OpenSpec attempt could not resolve npm inside the sandbox; the approved rerun passed.

## Requirement traceability

All named tests are in `PlayerSchedulingAdvisoryTest`.

| Requirement | Automated evidence |
| --- | --- |
| Assess player overlap from supplied facts | `doubles member overlapping singles produces actual or potential findings`; `adjacent intervals and exact recovery boundary are not conflicts` |
| Assess recovery using a selected rule | `delay and correction are reassessed with provenance`; exact recovery boundary test; call-without-start test |
| Represent uncertainty without declaring safety | incomplete membership/missing rule/call test; estimated-end/invalid-interval test |
| Reassess changed facts without enforcing scheduling | `delay and correction are reassessed with provenance` |

These are maker results, not independent gate certification. Completion and coverage remain subject to independent checking.

Independent G5 checking identified a missing recovery comparison when the earlier start is unknown and a weak call-time assertion. The implementation now uses an actual end to establish order when sufficient, while retaining interval uncertainty. Regression tests cover both input orders and recovery-specific unresolved behavior for a call without a start.
