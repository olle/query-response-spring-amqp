# Architecture Decision Records (ADR)

Architecture Decision Records provide a way to document the decisions
made in the architecture of a system. They help in understanding the context, 
decision-making process, and consequences of architectural choices.

## Accepted ADRs

The following ADRs have been accepted and are part of the project's architecture
documentation:

- [ADR-001-builder-beans](./ADR-001-builder-beans)

## Template for new ADRs

The [ADR-000-TEMPLATE.md](ADR-000-TEMPLATE.md) provides a structured format for 
documenting decisions. It includes sections for the title, status, context, 
decision, and consequences.

## Add a new ADR

1. Create a new PR by branching off the `main` branch, naming it
   `adr/XXX-title`, where `XXX` is the next available number and `title` is a
   very short description of the decision.

2. Add a new ADR file in the `xdocs/adr` directory using the available template.
   Ensure that the file follows the naming convention `ADR-XXX-title.md`, using
   the same number as the branch name.

3. Update the `xdocs/adr/adrs.md` file to include a link to the new ADR file.

4. Submit the PR for review.
