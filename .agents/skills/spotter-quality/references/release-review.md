# Release review

Official basis: [Timeline](https://nus-cs2103-ay2627-s1.github.io/website/admin/tp-timeline.html), [Week 12](https://nus-cs2103-ay2627-s1.github.io/website/admin/tp-w12.html), [deliverables](https://nus-cs2103-ay2627-s1.github.io/website/admin/tp-deliverables.html), and [fork/PR workflow](https://nus-cs2103-ay2627-s1.github.io/website/admin/tp-w7.html). Last verified: 2026-10-13. Recheck the relevant weekly page before applying dates or freezes.

## Official rules to verify

At verification, the v1.6 feature freeze limits functional-code changes per contributor to the higher of 15% or 150 lines, beginning at the v1.5 deadline. It includes comments/blank lines in code files under `src/main`; tests, docs, and non-code assets are excluded. Use the official attribution/dashboard calculation, not a simple local diff estimate.

Final submission also has a subsequent 30-day code freeze requiring course permission for code updates except approved late submissions. Obtain current dates, exceptions, filenames, and submission steps from the official page. Distinguish this from the pre-release freeze.

Expanded FAQ qualifications: use final attributed lines, not current line count; repeated edits to one surviving line count once. Deleted lines do not themselves count as changed lines but lower the final denominator. Cosmetic edits/comments/blank lines count. Quota cannot be borrowed; helping another feature is allowed with honest attribution. New features are strongly discouraged even though the quantitative rule governs enforcement. The website states a five-percentage-point margin above 15%; do not plan to exploit it or compact code to game the count. Reverting an offending change for final submission can remedy a violation, but requires normal authorization and review, not rewriting history.

At this audit, final submission is Tue 2026-11-03 14:00 Singapore time (UTC+8); verify it again before acting. The Canvas product filename follows `[TEAM_ID][ProductName].jar`; GitHub naming can differ. Match tagged source, JAR, UG/DG and website; one team member submits with others verifying. The latest Canvas submission timestamp applies, even to repeated identical uploads; one second late counts. PPP is optional unless contribution/dispute circumstances require it. The demo deliverable is removed this semester: obsolete expanded demo instructions are not active requirements.

## Recommended readiness checks

- Compare source, executable, UG/DG, and website for the same release version.
- Run relevant JDK 25 tests and Checkstyle; build with the repository packaging task. Inspect the archive for size, unused assets, and accidental data.
- Launch the packaged JAR in a clean folder with Java 25. Exercise the core trainer workflow, ordinary invalid commands, restart, and persistence.
- Check portability, file boundaries, and screen settings against [course requirements](course-requirements.md). State untested platforms/scales explicitly.
- Check current scope, unresolved defects, examples, and manual-testing paths. Weigh important fixes against regression risk and verified freeze limits.
- For PR readiness, identify issue/milestone, focused diff, validation, remote CI status, and teammate review. Local checks do not prove remote CI success.

## Authorization

Use a contributor fork feature branch and PR into team `master`; do not push directly to team `master`. Running this skill grants no permission to commit, push, merge, tag, release, change issues, or submit. Follow AGENTS.md and explicit user authorization.

The skill prepares review evidence; it does not replace team approval, understanding, or official submission checks.
