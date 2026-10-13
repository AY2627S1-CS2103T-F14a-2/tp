# Course requirements

CS2103/T AY2627 S1. Last verified: 2026-10-13. This summary is not a replacement for the official sources. Recheck relevant rules before grading-sensitive decisions and update the verification date through team review.

## Official course requirements

| Area | Requirement or emphasis | Source |
| --- | --- | --- |
| Direction | Evolve AB3 into contact management for a specific typing-oriented user group | [Overview](https://nus-cs2103-ay2627-s1.github.io/website/admin/tp-overview.html) |
| Product design: 5 marks | Cohesive features and target-user fit | [Grading](https://nus-cs2103-ay2627-s1.github.io/website/admin/tp-grading.html) |
| Implementation: 15 marks | Code quality and attributable effort | [Grading](https://nus-cs2103-ay2627-s1.github.io/website/admin/tp-grading.html) |
| QA: 10 marks | Developer and system/acceptance testing | [Grading](https://nus-cs2103-ay2627-s1.github.io/website/admin/tp-grading.html) |
| Documentation: 10 marks | Useful, accurate UG/DG and contributions | [Grading](https://nus-cs2103-ay2627-s1.github.io/website/admin/tp-grading.html) |
| Project management: 10 marks | Incremental process, timely work, GitHub workflow, team contributions | [Grading](https://nus-cs2103-ay2627-s1.github.io/website/admin/tp-grading.html) |
| Testing | Individual automated-testing evidence; no mandated coverage percentage | [Expectations](https://nus-cs2103-ay2627-s1.github.io/website/admin/tp-expectations.html) |
| AI use | Expected; creating useful skills is explicitly suggested | [Expectations](https://nus-cs2103-ay2627-s1.github.io/website/admin/tp-expectations.html) |

Do not inflate code or scope to chase marks, manufacture activity, or manipulate contribution attribution. A skill is supporting workflow, not a substitute for functional contributions or understanding the product.

## Runtime constraints

Official source: [TP constraints](https://nus-cs2103-ay2627-s1.github.io/website/admin/tp-constraints.html).

- Brownfield, incremental development; primarily object-oriented implementation.
- Single user; typing-preferred interaction; local human-editable text storage with at least AB3's manual-edit support; no DBMS.
- Application file access only within its JAR folder and subfolders; no OS configuration changes.
- Windows, Linux, and macOS; Java 25; no installer or dependence on the team's own remote server.
- Third-party libraries/services require applicable prior course approval, compatible licensing, and no user installation. Check existing approvals first.
- GUI works well at 1920x1080 or higher with 100%/125% scaling; remains usable at 1280x720 or higher with 150% scaling.
- Single JAR packaging, single ZIP fallback if necessary; product at most 100 MB without unnecessary bloat.

## Attribution and team conventions

Official source: [Reuse and AI attribution policy](https://nus-cs2103-ay2627-s1.github.io/website/admin/appendixB-policies.html).

Record sources when reusing work and respect licenses. Localized AI assistance needs credit near the affected work; widespread assistance needs the tool, user, and extent recorded in DG Acknowledgements. This skill's acknowledgement does not cover future generated product changes.

The course allows some credited public student-work reuse, but Spotter's AGENTS.md prohibits deriving solutions from other students' repositories. Follow that stricter team convention. Check the policy's distinct treatment of course-material and within-team reuse before applying external-code attribution tags.

AGENTS.md owns Spotter scope, fictional data, Git boundaries, and verification commands. Other references provide recommended project checks, not additional course requirements. For iteration decisions, consult the [timeline](https://nus-cs2103-ay2627-s1.github.io/website/admin/tp-timeline.html) and linked weekly page; do not hard-code today's milestone into standing rules.
