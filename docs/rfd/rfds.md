# Request For Discussion (RFD)

Request For Discussion (RFD) is a document that outlines a proposal or idea that
is open for discussion and feedback from the community. It serves as a way to 
gather input and suggestions before finalizing a decision or implementation.

## Accepted RFDs
The following RFDs have been accepted and are part of the project's 
documentation:

...

## Template for new RFDs

The [RFD-000-TEMPLATE.md](RFD-000-TEMPLATE.md) provides a structured format for 
documenting proposals. It includes sections for the title, status, context, 
proposal, and consequences.

## Add a new RFD

1. Create a new PR by branching off the `main` branch, naming it
   `rfd/XXX-title`, where `XXX` is the next available number and `title` is a
   very short description of the proposal.

2. Add a new RFD file in the `docs/rfd` directory using the available template.
   Ensure that the file follows the naming convention `RFD-XXX-title.md`, using
   the same number as the branch name.

3. Update the `docs/rfd/rfds.md` file to include a link to the new RFD file.

4. Submit the PR for review.
