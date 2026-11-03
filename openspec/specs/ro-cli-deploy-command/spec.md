# ro-cli-deploy-command Specification

## Purpose

Describe deployment via GitHub Actions workflow dispatch and existing safety gates. This command does not deploy directly to local Docker.

## Requirements

### Requirement: Prepare and dispatch deployment

`ro deploy` SHALL preserve its flags and require GITHUB_TOKEN, configured deploy.repo and deploy.workflow, and successful applicable preflight checks before dispatching the configured GitHub Actions workflow.

#### Scenario: Deploy successfully

- **WHEN** preflight passes, the user confirms or supplies --yes, and the dispatched workflow completes successfully while waiting is enabled
- **THEN** the CLI reports workflow status, conclusion and URL and succeeds

#### Scenario: Reject invalid deployment configuration

- **WHEN** the token, repository or workflow is missing, the checkout is detached, or a configured environment branch guard fails without --yes
- **THEN** the CLI returns an error describing that failed preflight condition before dispatch

#### Scenario: Preview deployment

- **WHEN** preflight passes and --dry-run or --check-only is supplied
- **THEN** the CLI reports the deployment plan without dispatching a workflow

#### Scenario: Report deployment failure

- **WHEN** a dispatched workflow concludes unsuccessfully while waiting is enabled
- **THEN** the CLI returns an error with the workflow conclusion

### Requirement: Honor deployment safety controls

Deployment SHALL enforce configured clean-workspace, environment-ref, branch-protection and green-commit checks, subject to the existing explicit --yes overrides; it SHALL prompt before dispatch without --yes.

#### Scenario: Reject a non-green commit

- **WHEN** deploy.requireGreen is enabled and GitHub reports an unsuccessful commit status, without --yes
- **THEN** preflight returns an error identifying the failing statuses

#### Scenario: Dispatch without waiting

- **WHEN** the effective wait setting is false and GitHub accepts the dispatch
- **THEN** the CLI reports dispatched and returns without claiming workflow completion
