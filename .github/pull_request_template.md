## Summary

Describe the concrete problem and resulting behavior. Explain significant architecture decisions and tradeoffs.

## Related issue and specification

Use `Refs #123` for partial delivery; use `Closes #123` only when the entire issue is complete. Link applicable OpenSpec requirements and ADRs.

## Acceptance criteria and evidence

| Criterion / requirement | Implementation or test evidence | Outcome |
| ----------------------- | ------------------------------- | ------- |
|                         |                                 |         |

## Validation

Record commands actually run and their outcomes. Mark inapplicable checks with a reason; explain skipped checks and remaining gaps.

- Formatting: `npm run format:check`
- Backend/IAM: `./service/tournamentmgmt/mvnw -B -f 3rd_party/iam/pom.xml install` if needed, then `./service/tournamentmgmt/mvnw -B -f service/tournamentmgmt/pom.xml clean verify`
- CLI: `cd tools/cli/ro && go test ./...`
- Manual / UAT evidence and environment:

## Definition of Done

- [ ] Acceptance criteria trace to implementation and passing validation.
- [ ] Applicable runtime checks and architecture boundaries pass.
- [ ] REST/JWT/CLI compatibility and database/auth risks assessed.
- [ ] Required ADRs and nearby documentation match the implementation.
- [ ] Independent review findings are resolved; limitations are explicit.
- [ ] Applicable human approvals and UAT evidence are linked.
- [ ] No unrelated generated output, secrets or environment-specific changes.

Follow the [lifecycle gates](https://github.com/wlambertz/rallyon/blob/main/docs/feature-lifecycle-gates.md); small bugs/docs may use the documented reduced path. Mark individual items “not applicable — reason” when appropriate. Passing CI is evidence for testing, and human merge approval remains separate.
