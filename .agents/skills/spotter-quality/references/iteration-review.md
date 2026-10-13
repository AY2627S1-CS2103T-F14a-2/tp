# Iteration and submission review

Verified public sources on 2026-10-13: [timeline](https://nus-cs2103-ay2627-s1.github.io/website/admin/tp-timeline.html), [FAQ](https://nus-cs2103-ay2627-s1.github.io/website/admin/appendixC-faq.html), and weekly TP pages listed in [coverage](course-page-coverage.md). Expand FAQs/cross-embedded panels when refreshing a check.

## Evidence procedure

Establish the requested week/version, personal versus team scope, repositories, branch/PRs, and reviewed SHA. Read that week's project/admin pages and available announcements. Do not infer Canvas, watched videos, human understanding, or another member's work from local files. Record unavailable private evidence as UNKNOWN.

Create a row for each applicable current requirement:

| Requirement | Owner / artifact | Deadline and timezone | Status | Evidence | Remaining action |
| --- | --- | --- | --- | --- | --- |
| Specific requirement | Individual or team owner; expected artifact | Verified current date; Singapore UTC+8 | PASS / FAIL / UNKNOWN / N/A | URL, revision, path, result | Action and owner |

Explain UNKNOWN/N/A. Distinguish REQUIRED course items, TEAM conventions, RECOMMENDED practices, and CONDITIONAL checks. Report blockers first; then individual readiness, team readiness, and concrete next actions. PR-ready, merge-ready, iteration-complete, and submission-complete are different verdicts.

## Weekly coverage

Dates are intentionally refreshed from linked sources rather than used as permanent rules. Historical tasks below are for audit/coverage; do not recreate them during routine development.

| Week | Summary of tasks and important qualifications |
| --- | --- |
| [3: kickoff](https://nus-cs2103-ay2627-s1.github.io/website/admin/tp-w3.html) | Each member explores the released AB3 JAR and submits initial-experience screenshot/suggestions through Canvas. Team agrees meeting time/venue/channels and submits collective iP status/help plan. Distinguish individual/team/one-person-per-team tasks. Stay at most one week ahead. |
| [4: direction](https://nus-cs2103-ay2627-s1.github.io/website/admin/tp-w4.html) | Start shared publicly viewable project notes, with links to subsidiary notes. Define product name, narrow user, problem/value, and scope before detailed features. Submit plain-text direction through TEAMMATES; value paragraph at most 50 words; notes link is view-only. All members agree direction. After the direction deadline, changes need course notification; drafting that notification does not authorize sending it. |
| [5: requirements](https://nus-cs2103-ay2627-s1.github.io/website/admin/tp-w5.html) | Brainstorm from persona, scope, and user journey; retain intermediate records and publicly reachable stories. Choose only indispensible MVP needs; retain discarded/deferred stories separately. Existing AB3 needs still count; MVP need not introduce new features. Set up organization/repo/site/tracking PR as needed. AI may critique a team's own persona/journey; do not invent unsupported traits. |
| [6: MVP specification](https://nus-cs2103-ay2627-s1.github.io/website/admin/tp-w6.html) | Translate stories into feature list and detailed external behaviour: syntax, validity/whitespace/case rules, messages/results, duplicates, errors, rationale, and UI mockups. Include needed existing AB3 features; omit irrelevant ones. Shared internal spec need not be polished; submit team-ID PDF as prescribed, with time-sensitive deadline. Set up forks/dev environment, retain required workflows, and complete codebase tutorials with required issue screenshots/branch PR evidence. Tutorial tests recommended, not compulsory. |
| [7: v1.1](https://nus-cs2103-ay2627-s1.github.io/website/admin/tp-w7.html) | Documentation-only workflow practice. Self-assigned photo issues/milestone/fork branch PRs and teammate review; lowercase username `.png` path (JPEG content allowed). Open AboutUs PRs in parallel, merge sequentially with conflict resolution, inspect final page. Update README/build badge/AB3 acknowledgement, one `Ui.png` mockup with original proportions, site branding, DG target/value/stories/use cases/NFRs/glossary. Each member PRs their own docs; do not split primary UG/DG files. Retain future requirements. |
| [8: v1.2](https://nus-cs2103-ay2627-s1.github.io/website/admin/tp-w8.html) | Allocate minimal MVP enhancements by feature. Each member aims to merge at least one functional increment; keep it compiling and working. No product release required. Package/class renaming optional, preferably early if chosen. Course permits early temporary test/doc debt but warns of risks; Spotter's stronger testing/doc rules still apply. Review AI findings before posting; sustained broken CI undermines QA. |
| [9: v1.3](https://nus-cs2103-ay2627-s1.github.io/website/admin/tp-w9.html) | Plan tasks/dependencies/order/owners/timeline, deliver working MVP by deadline. Release JAR with meaningful changes-since-AB3 notes and screenshots/recordings; wrap up milestone and reschedule pending work. Deadline compliance matters from this iteration. Avoid drastic build changes or WebView solely for UG display. Reflect on estimates and delivery. |
| [10: v1.4](https://nus-cs2103-ay2627-s1.github.io/website/admin/tp-w10.html) | Record process postmortem; implement usable initial versions of intended final features in priority order. Release JAR and detailed changes-since-last-release notes; wrap up iteration. Each student must have parallel PRs at least once. Begin UML updates: at least one per member recommended; this regular weekly task is separate from the v1.4 release deadline. Changes to original spec allowed; direction changes still require notification. |
| [11: v1.5](https://nus-cs2103-ay2627-s1.github.io/website/admin/tp-w11.html) | Alpha-test the JAR, record at least five issues per person with exact `alpha-bug` label; suggestions allowed during alpha. Two non-authors per feature recommended. Prioritize fixes/polish/code quality/tests before freeze; update UG/DG and actual screenshots. Check RepoSense authorship. Release smoke-tested JAR and current site by deadline; detailed release notes optional; wrap up milestone. |
| [12: v1.6](https://nus-cs2103-ay2627-s1.github.io/website/admin/tp-w12.html) | Observe per-member feature-freeze limits; fix prioritized latent defects. Submit tagged matching source/JAR and updated website/UG/DG through prescribed channels, exact filename/deadline. PPP optional except specified contribution/dispute cases. Prepare and take individual PE; after submission respect separate code freeze. Read release and PE references for details. |
| [13: post-release](https://nus-cs2103-ay2627-s1.github.io/website/admin/tp-w13.html) | Attend to PE developer/tester responses and announced post-release tasks. Refresh the phase-specific instructions rather than changing the submitted product. |

## Scheduling qualifications

Iterations generally end Thursday 23:59 unless specifically overridden; regular weekly tasks can have a different cutoff (e.g., Friday briefing). Early occasional overruns/errors have limited catch-up allowance; do not apply that allowance to time-sensitive tasks, v1.3 deadline checks, or final submission. Record actual lateness; moving an issue does not erase it. Confirm live deadlines and Singapore time rather than assuming a past snapshot.

Check current milestones, closed/open tasks, release/tag/SHA correspondence, deployed artifacts, and public links. Do not assume every iteration needs a JAR. Preserve each contributor's visible work and leave buffers. The rubric expects at least 75% of tasks on time and commits in four of weeks 7-12, not manufactured commits.

Check available project/admin announcements with the user when relevant evidence is missing. Do not promise exhaustive certification of private course requirements. Read [git review](git-review.md), [release review](release-review.md), and [PE review](pe-review.md) only when those checks apply.
