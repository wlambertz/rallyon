# Proposal: reproducible dependency security evidence (issue #187, PR 1)

## Why

The maintainer currently receives persistent red security checks while unrelated main workflows pass. The scanner report mixes genuine affected dependencies with candidate product misidentifications, uses a mutable runtime image behind a pinned Action, and skips SBOM generation whenever the scan fails. This undermines actionable vulnerability review and hides useful inventory evidence.

The primary audience is the maintainer/contributor, in the repository delivery/tooling context. Organizer, Player and Spectator benefit indirectly from validated service releases; this change creates no product persona flow or business bounded context. G1 scope was explicitly accepted with “G1-Scope freigeben”; the lifecycle record retains its actual source.

## What changes

- Align service and shared IAM starter on Boot 4.1.2, compiler/runtime/plugins on Kotlin 2.4.21, and the existing Swagger UI transitive artifact on 5.34.1, without changing auth policy.
- Establish a source-linked classification for every finding in the recorded baseline. Fix genuine findings and correct only proven misidentifications using narrowly bounded, expiring rules.
- Replace the scanner wrapper's mutable image with official Dependency-Check 13.0.0 at the verified OCI digest; retain runtime-JAR and embedded-JavaScript analysis, active vulnerability-data updates and blocking CVSS >= 7 behavior.
- Split successful dependency preparation from independent scanner and CycloneDX jobs. Keep reports available for vulnerability failures without turning errors into success.
- Restore scanner data safely, allow cache writes only from trusted default-branch runs, and keep optional NVD credentials out of untrusted PRs, artifacts, caches and logs.

## Capabilities

### New capabilities

- `repository-dependency-security`: dependency alignment, triage, fail-closed scanner operation, narrowly justified suppressions, trusted cache/credential boundaries, independent SBOM generation and reviewable evidence.

### Modified capabilities

None. Existing main specifications, REST/JWT/role/database contracts, ro commands and module boundaries remain unchanged.

## Delivery and backward compatibility

This lifecycle change is PR 1 of the supplied two-PR plan in `docs/issue-implementation-plans/issue-187-ci-stabilization.md`. PR 2 still owns the remaining workflow/toolchain hardening, expanded CodeQL languages and safe Docker/release packaging. Its work is not included as incomplete implementation tasks in this first change, and #187 cannot close after PR 1 alone.

Affected areas are dependency metadata in the service/IAM starter, the security workflow, small security-runner/validation scripts, focused tests and operation/triage documentation. This proposal explicitly flags the shared IAM dependency change for human G2 review. There are no new runtime libraries, Flyway migrations, public signature changes or auth-policy changes. The existing Security scan & SBOM workflow filename and its Dependency vulnerability scan / Generate SBOM check names are retained; a preparation job is added. Secrets stay optional and validation performs no live release, tag publication or registry push.
