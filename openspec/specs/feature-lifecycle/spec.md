# Feature lifecycle

## Purpose

Provide an inspectable, resumable delivery workflow for RallyOn features while preserving explicit human decisions and independent checking. This capability governs agent workflow; it does not change tournament APIs, JWT claims, database behavior or `ro` commands.

## Requirements

### Requirement: Explicit lifecycle selection

Full feature work SHALL explicitly select the `rallyon-feature` schema. The repository default SHALL remain `spec-driven`. Planning readiness SHALL require refinement, proposal, specifications, design, value, tasks and issue drafts without requiring post-implementation reports.

#### Scenario: Prepare a proposal

- **WHEN** all planning artifacts exist but verification, review and UAT reports do not exist
- **THEN** OpenSpec exposes apply instructions that additionally require current G4 lifecycle evidence
- **AND** artifact existence alone does not authorize implementation

### Requirement: Human decisions and independent evidence

The lifecycle SHALL require explicit human decisions at G1, G2, G4, G8 and merge. It SHALL record approval quotes and source references and reject checker identities registered as implementation makers. Full G7 SHALL require architecture, correctness and tests review lenses.

#### Scenario: A delegated maker attempts checking

- **WHEN** a registered delegated maker submits a checker result
- **THEN** the helper rejects it and leaves that checking gate pending

#### Scenario: An approval is rejected

- **WHEN** a human decision is recorded as rejected
- **THEN** subsequent gates remain blocked and the record history is retained

### Requirement: Current evidence and resumability

The lifecycle SHALL persist gate records per change, bind approvals to reviewed artifact hashes, and bind implementation evidence to tracked, staged and nonignored untracked source content. Changes to inputs or preceding decisions SHALL invalidate affected downstream evidence without deleting history. Checker reports SHALL be hashed separately from their source snapshot.

#### Scenario: Source changes after verification

- **WHEN** source content or staged content changes after G6
- **THEN** the helper reports stale evidence and blocks downstream progression

#### Scenario: Resume a session

- **WHEN** the same change resumes with the same repository, primary maker and mode
- **THEN** its evidence and created issue URLs are retained
- **AND** a different active change must first be explicitly deactivated

#### Scenario: Change reduced-mode planning artifacts

- **WHEN** an existing proposal, design, value or specification artifact is edited, added or removed after reduced-mode specification approval
- **THEN** affected G2–G4 evidence becomes stale
- **AND** genuinely omitted artifacts remain optional

### Requirement: Exact approved issue creation

The supported issue-creation path SHALL submit only the structured payload approved at current G4, persist each successful result immediately, skip recorded successes on resume and stop after ambiguous outcomes until explicit reconciliation. Reusing a draft identifier with a changed attempted payload SHALL be rejected.

#### Scenario: Partial issue creation

- **WHEN** the first issue succeeds and a later creation has an ambiguous outcome
- **THEN** the first URL remains recorded and resumption does not recreate it
- **AND** the uncertain attempt requires verified reconciliation before retrying

#### Scenario: Rename or remove an uncertain draft

- **WHEN** an attempted creation has an ambiguous outcome and its draft is later renamed or removed
- **THEN** the unresolved attempt remains visible in status and blocks further issue creation and G5
- **AND** an active session can reconcile the saved attempted payload with a verification reference despite stale approvals
- **AND** subsequent issue creation still requires current G4 approval

### Requirement: Session-scoped enforcement and reduced work

The Claude Bash hook SHALL guard ordinary direct issue creation for the lifecycle repository only while a session is active, preserving the existing commit guard. Shared agent instructions SHALL require the checked helper across agents. Narrow bugs and documentation MAY use a reduced record with explicit reasons for omitted ceremony, while retaining human decisions, independent checking and applicable escalation.

#### Scenario: Unrelated work outside a session

- **WHEN** no lifecycle session is active
- **THEN** the lifecycle hook does not impose issue-creation approval on unrelated work

#### Scenario: Use the ordinary issue-creation alias

- **WHEN** an active session's repository is targeted through `gh issue new`
- **THEN** the hook requires the same checked creation path as `gh issue create`

#### Scenario: Execute creation inside a command substitution

- **WHEN** an issue-creation command for the lifecycle repository appears in an executable command substitution, including nested substitutions, backticks, line continuations or intervening flags
- **THEN** the hook blocks it even when the surrounding command prints documentation

#### Scenario: Change the tool working directory

- **WHEN** the tool cwd is outside the repository but `CLAUDE_PROJECT_DIR` identifies an active worktree session
- **THEN** the project session continues to guard issue creation
- **AND** sibling worktree sessions are not discovered through shared Git metadata

#### Scenario: Write documentation with heredocs

- **WHEN** a quoted heredoc contains text mentioning issue-creation commands
- **THEN** the hook treats the body as literal documentation
- **AND** executable expansions in unquoted heredocs and commands following their terminators remain inspected

#### Scenario: Reduced documentation delivery

- **WHEN** a narrow documentation correction uses reduced mode
- **THEN** its single lifecycle record explains omitted numerical scoring and three-lens review
- **AND** human review and independent evidence remain required
