# Practical exam review

Source: [AY2627 S1 PE instructions](https://nus-cs2103-ay2627-s1.github.io/website/admin/tp-pe.html), including expandable preparation, reporting, response, and triaging panels. Reviewed 2026-10-13. Refresh phase dates and exact labels/comment formats before actual PE use. This reference prepares evidence; it does not authorize posting or submitting.

## Preparation and tester phase

- PE testing is individual. Report findings from your own testing, not another tester's results. AI/scripts are allowed as aids. Accept course organization/repository invitations and confirm Java 25; check assigned files and available announcements. Download encrypted files early, keep the personal password component private, combine it with the announced component, unzip fully, and run the actual JAR from a writable folder using the command line. Account for distribution renaming; double-clicking is a last resort.
- Smoke-test launch/basic commands first. If catastrophic failure persists after checking UG/Java/launch instructions, follow the current course contact/fallback process promptly; do not choose another product yourself. Once reassigned, stop testing the former product and handle its reports as instructed. Use the documented Linux display workaround only when applicable.
- Test product behaviour and rendered UG/DG, using manual-testing instructions as a starting point and then exploratory variations. Other website pages/README, code quality, and raw config/data are not direct PE targets unless they affect behaviour; alarming terminal output can matter.
- Put one defect per issue, complete reproducible steps/expected/actual/evidence in the issue body, and exactly one `type.*` and `severity.*` label. Use a professional tone and justify usage impact. Avoid cross-issue references and tester-created sub-issues; escape angle brackets when needed. Do not alter supplied label definitions or invent labels using reserved prefixes.
- Assess product target/value/CLI productivity/cohesion and UG/DG fitness honestly; estimate implementation effort relative to AB3, excluding tests/docs/management. Submit the overall TEAMMATES evaluation as well as bug reports.
- Keep no more than six reports for the developers; highest-scoring five count. Select by severity and confidence, not type; close excess reports and justify selected severities in their bodies before the cutoff. If between severities, choose the lower unless evidence clearly favors the higher. Status emails provide a buffer, not a deadline extension.

## Developer responses

Use the assigned team PE repository. Do not edit/transfer received issue bodies/titles or create new reports there. Assign genuine responsibility; assigning everybody counts as unassigned. No more than half of received reports may remain unassigned under the verified rule; check exact current penalty wording before reporting consequences. Assignment accepts responsibility, not an obligation to alter the frozen release.

Choose exactly one response: `response.Accepted`, `response.NotInScope`, `response.Rejected`, `response.CannotReproduce`, or `response.Unclear`. Correct mistaken type/severity labels rather than rejecting a real defect for labeling mistakes. A valid failure can be accepted even when the tester suggests the wrong solution. Make reasonable reproduction attempts beyond literal steps if evidence shows a defect.

Justify downgrades, non-acceptance, and duplicates in one comment beginning with `# T` on its own line, at most 500 words. Explain the causal/user evidence; do not reference other issues by number. The latest qualifying comment is used. No justification is required for acceptance as-is. Reserve separate `# U` complaints for actual unprofessional conduct, not weak reports.

Duplicates must be the same defect or failures that cannot be fixed independently. Independently fixable similar typos are separate. Use a single-level original/sub-issue grouping; duplicates inherit original labels/assignees. Explain duplicate reasoning and any inherited severity downgrade.

## Scope and classification

Use [bug prevention](bug-prevention.md) and [documentation review](documentation-review.md). Scope is a priority/resource argument, not an immunity claim. A useful future improvement usually belongs under NotInScope rather than Rejected if genuinely lower priority; essential usefulness failures or clearly better designs at similar effort may remain in scope. Graceful recovery and clearly disclosed limits support a scope argument but do not automatically establish it.

Ordinary mistakes must not crash/corrupt data. Deliberate sabotage alone is outside the course's functionality-bug scope; distinguish it from accidentally concatenated parameters or plausible long values. Judge clipping by whether it impedes use. OS-specific defects still count; inherited AB3 defects count. Known issues can reduce impact without removing defect status.

Documentation findings need reader impact. Incorrect UML, inconsistent omissions, overcrowded/unreadable diagrams, inaccurate requirements/examples, undisclosed accessible WIP, and unclear explanations can count. Deliberate simplification/optional notation is acceptable when it does not mislead. Obvious typos should be fixed; harmless minor grammar can qualify for deferral. A missed detail communicated adequately in-app need not also be in the UG.

## Tester responses and moderation

Wait for the announced response phase. Use the provided agreement checklist and separate objections for eligible aspects; each objection comment starts with `# D` and stays within 500 words. Do not change original title/body/labels. To retract an objection, remove its qualifying heading or delete the objection comment as instructed; hiding it or ticking agreement alone is insufficient. No objection means agreement for that aspect. New evidence can help but is weaker than timely initial evidence.

Accuracy matters: reconsider reasonable developer arguments rather than contesting everything. NotInScope can still earn tester credit; do not dispute a rejection solely to obtain NotInScope while expecting full credit. Tutors moderate contested cases; do not negotiate for an arbitrary compromise or claim the moderator must split the difference.

The four phases are testing/reporting, developer response, tester response, and tutor moderation. See Week 12/13 and current announcements for exact deadlines. Do not fix/release frozen code while drafting a response.
