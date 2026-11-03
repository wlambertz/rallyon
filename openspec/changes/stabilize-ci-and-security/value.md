# Advisory value assessment: issue #187 PR 1

## Audience, context and evidence

Primary audience: the maintainer/contributor in repository delivery/tooling. Product audiences benefit indirectly; no Organizer workflow improvement is invented. This assessment covers the first delivery package only. The whole issue was deliberately decomposed into two PRs; PR 2 remains separate work, not hidden inside this score or omitted from final issue completion.

Direct observations: main's security workflow failed while Java/lifecycle/CodeQL/Docker passed; the 141-dependency report mixes real affected versions and candidate product mismatches; Generate SBOM was skipped because it depended on the failed scan; the pinned wrapper loads an internal latest image. Raw findings, run/revision identity and the independently read registry digest are in the evidence directory. The PostgreSQL vendor advisory corroborates at least one genuine affected package. Expected remediation success, cold keyless update performance and broader ecosystem compatibility are not yet validated.

## Anchored estimates

| Dimension | Estimate | Rationale and uncertainty |
| --- | --- | --- |
| User value | 4 / 5 | Expected to remove recurring red-scan triage and skipped-inventory workarounds. Repeated failed runs are observed; time saved remains a hypothesis, not a measured product metric. |
| Learning value | 4 / 5 | Expected reusable insight into package/CPE identity, fail-closed scan execution, trusted caching and independent evidence. The benefit depends on documented actual verification, not adopting tools alone. |
| Risk reduction | 3 / 5 | Direct evidence establishes affected dependency versions, mutable execution and skipped inventory: material maintenance and regression risk. Product exploit reachability and prevention are not demonstrated, so a major/critical exposure reduction rating is not claimed. |
| Confidence | 0.75 | Corroborating reports, code inspection, maintainer priorities and vendor evidence; updates and environment behavior have not been proven. |
| Effort | 5 | Substantial integration across coordinated dependency families, IAM/API compatibility checks, container/cache/secret handling, workflow jobs, suppression review, negative tests, real cold/warm runs, documentation, independent reviews, CI and delivery. |

## Calculation and limitations

`(0.5 × 4 + 0.3 × 4 + 0.2 × 3) × 0.75 / 5 = 0.57`.

This is an advisory comparison, not an automatic pass, date, numerical work budget or cost estimate. The full two-PR program has large/decomposed relative effort (8); the second package still needs implementation/check/acceptance evidence and cannot be declared complete by PR 1's score. Relative estimates are not additive day counts.

The proposed go decision is to remediate and prove PR 1 without weakening the CVSS gate, then deliver PR 2. Remaining real blocking vulnerabilities, missing independent review, failed keyless cold initialization or incompatible IAM/API behavior cannot be waived by this score. A material change in scope, evidence, effort or conclusion returns to G2; no numeric budget was agreed.

## Decisions and checking

G1 is actually approved in the lifecycle record. G2 specification/go-no-go is pending the maintainer's review of proposal, delta specification, design and this assessment. G3 is pending a fresh independent checker after G2; no checker identity, successful calculation challenge or implementation evidence is fabricated here. Tasks and any exact issue-creation payload are prepared only after those gates.
