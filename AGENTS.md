    # Spotter team repository guidance

## Project context

This is the CS2103/T AY2627 Semester 1 team project for team F14a-2.

- Product name: **Spotter**
- Target user: freelance personal trainers who independently manage their own clients
- Value proposition: keep each client's contact details, sessions, and progress in one place so a trainer can quickly find clients, prepare for sessions, and track progress
- Product model: a single trainer using local data on one computer
- Interaction model: command-driven and optimized for users who prefer typing; core workflows must not depend on mouse-only interaction

The official [CS2103/T AY2627 S1 course website](https://nus-cs2103-ay2627-s1.github.io/website/) is the source of truth for course requirements. Check the current relevant course page before making grading-sensitive, deadline-sensitive, Git, release, documentation, or submission changes. Do not rely on instructions from another semester.

## Scope boundaries

Keep Spotter focused on managing a trainer's own client relationships.

- Client records are the primary entities.
- Sessions, measurements, goals, progress entries, notes, and tags may be attached to clients.
- Preserve the course's single-user constraint: do not add trainer/client accounts, authentication, shared workspaces, concurrent editing, cloud collaboration, or organisation-wide workflows.
- Do not add billing, payroll, workout-plan generation, or a general exercise library unless the team explicitly changes the agreed product scope.
- Prefer features that are useful with local, human-editable storage and typed commands.
- Use fictional information in sample data, tests, screenshots, and documentation. Never commit real client or personal data.

## Development approach

- Before implementing, fixing, or reviewing Spotter code, tests, build behaviour, or documentation, read and apply [spotter-quality](.agents/skills/spotter-quality/SKILL.md). Load only its references relevant to the task. Use review mode for read-only reviews.
- Use the same skill for requested iteration, submission, release, or PE readiness checks; refresh the specified week's official instructions and distinguish individual tasks from team completion.
- Treat this as a brownfield extension of the supplied AddressBook-Level3 codebase; preserve its architecture and conventions unless a requested change justifies otherwise.
- Implement only the requested issue, milestone, or iteration. Do not implement later course work or speculative features in advance.
- Choose the simplest design that satisfies the current requirements and fits the existing codebase.
- Avoid unrelated refactors and unnecessary dependencies.
- Keep commands fast to type, consistently formatted, and precise about validation and error messages.
- Add or update tests for changed behaviour, including invalid input and important boundary cases.
- Add Javadoc or comments for non-obvious classes, methods, fields, and design decisions; do not comment self-explanatory code.

## Repository layout and documentation

- Production Java code: `src/main/java`
- Test code: `src/test/java`
- MarkBind documentation: `docs`
- User-facing behaviour belongs in `docs/UserGuide.md`.
- Architecture, design decisions, and developer workflows belong in `docs/DeveloperGuide.md` and the relevant developer documents.
- Keep documentation, examples, screenshots, and command formats consistent with the implemented product.
- Do not rewrite `README.md` or reorganize documentation unless the current course requirement or assigned issue calls for it.

## Verification

Preserve the course runtime constraints: local human-editable storage without a DBMS; application file access only inside the folder containing the JAR and its subfolders; no OS configuration changes; portability across Windows, Linux, and macOS with Java 25 and no installer. Verify applicable course approval before adding third-party libraries or services. For packaging and GUI resolution/scaling requirements, consult the skill's course and release references.

Use JDK 25 and the repository's Gradle wrapper. For code changes, run the checks relevant to the change before declaring it complete.

On Windows:

```powershell
.\gradlew.bat clean test
.\gradlew.bat checkstyleMain checkstyleTest
```

On macOS/Linux:

```bash
./gradlew clean test
./gradlew checkstyleMain checkstyleTest
```

For documentation changes, build the MarkBind site using the commands documented in `docs/` or the course website. Also inspect the Git diff for accidental generated files, secrets, personal data, or unrelated edits.

## Git and collaboration safety

- The shared integration branch is `master`; do not rename it.
- Make work on a separate feature branch in a contributor's personal fork, then open a pull request to the team repository.
- Do not push directly to the team repository's `master` branch.
- Preserve the commit history required by the course. Do not squash, rebase, amend shared commits, force-push, delete branches/tags, or otherwise rewrite grading-sensitive history unless the current official instructions explicitly require it and the user confirms.
- Do not commit, push, merge, create tags/releases, or close issues unless the user explicitly asks.
- Before a pull request is merged, ensure the relevant tests and required CI status checks pass and follow the team's review policy.
- Keep each pull request focused on its linked issue and milestone, and explain important design choices in the PR description.

## Academic and AI-use safety

- Follow the current course rules for AI use, acknowledgements, reuse, and attribution.
- Do not copy or derive code or solutions from another current or former CS2103/T student's repository.
- Clearly identify reused third-party code, ideas, assets, or documentation when the course requires attribution.
- Explain significant generated changes so team members can understand, review, and maintain them.

## Keeping this file useful

Store only durable, team-wide context and working agreements here. Put temporary meeting notes, deadlines, task assignments, and evolving feature lists in the issue tracker or project documentation instead. Update this file through normal team review when the product direction or workflow changes.
