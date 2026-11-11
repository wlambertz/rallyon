# G5 — Independent implementation check

**Verdict: FAIL (one blocking contract defect).** Checker `field-implementation-check`, independent of makers `codex-issue-224`, `field-core` and `field-web`, 2026-11-11. No maker files changed and no gate registered.

| Severity | Location | Finding / required correction |
| --- | --- | --- |
| Medium, blocking | `ParticipantFieldService.kt:5`, request binding; `ParticipantFieldControllerSecurityTest.malformedSelectorsHeadersAndBodiesArePrivateBadRequestsBeforeService` | PF-007/design promise integer syntax and malformed-input 400, but the actual Spring Jackson mapper silently truncates floating JSON tokens into Long IDs/source versions. Scope request parsing to this endpoint/DTO, reject non-integer tokens including list elements, preserve global/legacy mapper behavior, and add real MockMvc regression tests asserting 400/no-store and no service invocation. |

## Reproduced defect

Using the already built Java 25 runtime, instantiated the actual Spring application and obtained its `tools.jackson.databind.json.JsonMapper` bean. The probe used existing isolated `rallyon_field224_final` with V8 already applied, Hibernate DDL disabled and no entry/field operations. The context closed after probing.

Each request populated `disciplineId`, `entryIds[0]`, `sourceTournamentVersion` and `sourceEntryBookVersion` with the same token:

| JSON token | Actual bound value for all four Long positions |
| ---------- | ---------------------------------------------- |
| `1.9`      | `1`                                            |
| `"1"`      | `1`                                            |
| `1e0`      | `1`                                            |
| `1`        | `1` (valid control)                            |

The lossy `1.9` conversion alone demonstrates the defect: a malformed identifier/revision can become a different valid value instead of failing. Pure use-case validation sees only the already-coerced Long and cannot recover the original token. Existing malformed-body tests do not cover this case. Probe source `/private/tmp/ParticipantFieldCoercionProbe.java`; output `/private/tmp/rallyon224-coercion-probe.log`. No Maven/shared-target or workflow-data mutation was needed.

## Other completion evidence inspected

- Reviewed approved PF-001–007, design/tasks, ADR 0007 and raw staged production/test additions. Only new participant-field runtime files/V8 are present; existing entry/configuration/announcement/IAM/CLI sources are unchanged. Separately staged accepted #185 research was treated as preserved evidence, not a runtime change or re-review of sports rules.
- Creator authority precedes private source/field loads; the API is separate from the legacy DTO and uses scoped sanitized no-store errors. Domain/use-case ports remain framework/API independent.
- Selection preserves original entry/current target provenance, explicit one/two-member units and canonical IDs; empty selection, manual capacity and intake independence match the approved scope.
- Field locking/CAS, lazy initialization, atomic latest-member replacement and minimal audit implement the independent version boundary. REQUIRES_NEW/REPEATABLE_READ and post-rollback error mapping match the documented snapshot guarantee; no source writes or automatic synchronization were found.
- Inspected named PostgreSQL test phases, including complete winning-view comparisons, removed-target retention and dynamic-proxy latches forcing source commits after real snapshot observation. Their assertions distinguish a coherent old response from the next OUTDATED read.
- Maker full log independently inspected: **145 tests, 0 failures/errors, 3 unrelated optional skips, BUILD SUCCESS**; both real PostgreSQL classes executed. Focused log: **53 tests, 0 failures/errors/skips, BUILD SUCCESS**. These are inspected maker executions, not an independent Maven rerun.
- `git diff --cached --check`: passed. Maker formatting/strict OpenSpec checks were reported passed; they do not detect JSON coercion.

## Recheck scope and limits

Recheck only endpoint-scoped parsing, its new malformed-body tests, unchanged valid mapping and unchanged legacy behavior after the fix. Update maker validation with actual revised test results before passing G5. G6, three G7 lenses and real-Keycloak UAT remain later work. No full-load/performance, independent full-build execution or actual authenticated HTTP-to-database UAT is claimed by this check.
