# Persona UAT at G8

Prepare `uat.md` only after implementation, current QA and review evidence exist. Choose scenarios from the approved acceptance criteria that demonstrate the primary persona's outcome. For workflow/docs work, use maintainer walkthroughs; for backend work, use the existing local Docker Compose environment. Do not invent a deployed staging or frontend environment.

For each scenario state prerequisites and test data, exact user actions, expected observable result, cleanup and a place for actual observations. Include a representative failure path. Name environment, source revision, limitations and unresolved findings. Reduced mode can use a human documentation review or focused reproduction, with the choice recorded.

Present the runnable script and current evidence. The human executes or witnesses the scenarios and explicitly accepts the results; an agent's dry run cannot approve UAT. Record their actual acceptance quote and source reference through the helper. A failure returns to implementation/testing/review. Merge requires a separate explicit decision for the final reviewed revision.
