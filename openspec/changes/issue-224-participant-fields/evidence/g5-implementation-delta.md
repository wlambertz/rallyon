# G5 — Implementation delta recheck

**Verdict: PASS.** Checker `field-implementation-check`, 2026-11-11. The blocking PF-007 finding in the [initial FAIL report](g5-implementation.md) is resolved. This report covers only that correction and links the unchanged completion assessment there. No maker files changed and no gate registered.

| Prior finding | Resolution evidence | Outcome |
| --- | --- | --- |
| Lossy JSON coercion into Long IDs/source revisions | The participant-field controller now accepts a JSON tree and requires an object, string bracket selector, array of IDs, and integral number nodes convertible to signed Long. Only then is the unchanged service DTO constructed. Fractional/exponent/string/boolean/null/overflow tokens are rejected before service invocation. | Resolved |

## Delta inspected

- `ParticipantFieldController.request` / `integer`: scoped parsing is confined to this web adapter. `isIntegralNumber` rejects decimal/exponent nodes before `longValue`; `canConvertToLong` rejects out-of-range integers. Missing/null members and wrong root/list/bracket shapes fail as sanitized 400 through the existing scoped advice.
- The public `ParticipantFieldRequest` DTO, domain/store/service behavior and global/legacy Jackson configuration are unchanged. No API-to-web dependency or mapper-wide compatibility change was introduced.
- Four new MockMvc tests cover each numeric scalar and list contents, missing/null fields, wrong root/list/bracket shapes and both overflow directions; every malformed case asserts 400/no-store and no service interaction. Valid integer Long.MAX_VALUE values map without narrowing. Existing valid mapping, empty selection, field/source version separation, authority and sanitized outcomes remain exercised.
- Endpoint documentation now states the strict token/range contract. Updated maker verification distinguishes initial 53/145 runs from corrected 57/149 runs and preserves the original finding rather than claiming the initial result was sufficient.

## Independent execution and evidence

```sh
JAVA_HOME=/private/tmp/rallyon-jdk25/Contents/Home \
./service/tournamentmgmt/mvnw -B -f service/tournamentmgmt/pom.xml \
  -Dtest=ParticipantFieldControllerSecurityTest test
```

**15 tests, 0 failures/errors/skips; BUILD SUCCESS.** Actual HTTP binding through MockMvc was exercised; no PostgreSQL opt-in or workflow-data mutation was needed for this delta. Log: `/private/tmp/rallyon-field224-g5-delta.log`. `git diff --cached --check` also passed.

Independently inspected revised maker logs: `/private/tmp/rallyon-field224-fix-focused.log` reports 57 tests, zero failures/errors/skips; `/private/tmp/rallyon-field224-fix-verify.log` reports 149 tests, zero failures/errors and the same three unrelated optional skips. Both real PostgreSQL integration classes executed in the full run. This checker did not rerun that full suite; the focused endpoint rerun directly verifies the corrected defect.

## Limits

The standalone DTO-binding probe from the initial report is intentionally not a pass criterion: the DTO retains normal mapper semantics, while the actual endpoint now validates before creating it. MockMvc still mocks the service/JWT decoder; fresh independent G6, three G7 lenses and real-Keycloak HTTP-to-database UAT remain later work. No additional scope/design exception was needed.
