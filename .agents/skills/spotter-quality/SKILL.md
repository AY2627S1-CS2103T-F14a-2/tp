---
name: spotter-quality
description: Implement, fix, or review Spotter code, tests, build behaviour, and documentation against CS2103/T AY2627 S1 requirements and textbook/PE bug guidance. Also use for Spotter iteration, submission, release, and practical-exam readiness reviews; preserve the assigned issue and iteration scope.
---

# Spotter quality

Reduce defects and identify relevant course requirements using observable evidence. Do not guarantee a grade, complete compliance, or a bug-free product. Read the repository's applicable AGENTS.md first; keep its product scope and authorization boundaries.

## Choose the work and references

- Implementation/fix: make the requested change, test changed behaviour, and synchronize affected documentation.
- Review: inspect without editing; report actionable findings with evidence. Do not implement fixes merely because a review finds them.
- Release review: assess the requested release; do not publish, tag, push, merge, or submit without explicit authorization.
- Iteration/submission review: check the specified week and personal/team tasks using current official instructions and evidence; do not automatically use today's week.
- PE review: prepare testing or response evidence for the appropriate phase, preserving individual testing and submission restrictions.

Read [course requirements](references/course-requirements.md) when establishing course constraints or grading-sensitive decisions. Then load only relevant references:

| Task | Reference |
| --- | --- |
| Java, tests, architecture, or build changes | [Implementation review](references/implementation-review.md) |
| Commands, validation, storage, UI, or acceptance testing | [Bug prevention](references/bug-prevention.md) |
| UG, DG, diagrams, examples, or changed user-visible behaviour | [Documentation review](references/documentation-review.md) |
| Packaging, release readiness, freeze limits, or submissions | [Release review](references/release-review.md) |
| Weekly tasks, milestones, or submission readiness | [Iteration review](references/iteration-review.md) |
| Branches, commits, PRs, CI, or authorship | [Git and PR review](references/git-review.md) |
| Testing strategy, root-cause debugging, or test gaps | [Textbook testing](references/textbook-testing.md) |
| PE preparation, reporting, or triaging | [PE review](references/pe-review.md) |
| Team adoption and example invocation prompts | [Team workflow](references/team-workflow.md) |

For the full source inventory and page-by-page coverage, read [course page coverage](references/course-page-coverage.md). Historical screenshot-only guidance is preserved separately in [briefing notes](references/briefing-notes.md); it is not a verified website rule.

The linked course pages are authoritative. References distinguish course requirements from team conventions and recommended checks. Verify the current relevant official page before changing grading-sensitive behaviour or acting on deadlines, freezes, Git workflows, dependencies, or submissions. If it cannot be accessed, identify the unverified rule and proceed only with independent work that does not depend on it.

## Apply to the requested change

1. Establish the intended behaviour from the issue/user request, relevant implementation, tests, and UG/DG. Read connected callers and storage/model paths as needed, rather than judging only the diff. Resolve material ambiguity before implementing dependent behaviour.
2. Confirm the feature helps Spotter's freelance trainer, remains centered on clients, fits other features, and belongs in the assigned iteration. Do not add speculative features to satisfy a checklist.
3. In implementation/fix mode, make the smallest complete change consistent with AB3 conventions. In every mode, use the relevant reference to select meaningful invalid-input, boundary, integration, regression, and manual checks. Record expected outcomes before checking actual results.
4. Compare code, user-facing messages, tests, and documentation. Fix scoped discrepancies only in implementation/fix mode; in review modes report them without editing. Report unrelated defects separately instead of expanding the task.
5. Run repository checks relevant to changed files. For Java behaviour changes, follow AGENTS.md's Gradle test and Checkstyle commands with JDK 25. For changes to the MarkBind site, run `npm run build` from `docs` and inspect affected rendered pages. Instruction-only skill changes require skill/link/diff verification; they do not require Java tests or a site build unless those surfaces also change.
6. Inspect the final diff for unrelated edits, generated output, attribution omissions, and real personal data. Preserve pre-existing user changes. Do not claim a check passed unless it ran successfully.

## Report results

For implementation, state what changed, checks performed and results, and material remaining limitations. For review, lead with confirmed defects, each with location, trigger, expected/actual behaviour, user impact, and the applicable rule where useful. Distinguish potential risks from reproduced defects and state checks not performed. A clean review means no defects found within the inspected scope, not proof of full compliance.

For iteration/submission checks, record reviewed revision/PRs, week, owner, artifact, deadline/timezone, evidence, and PASS / FAIL / UNKNOWN / N/A per requirement. Explain UNKNOWN and N/A. Report individual readiness separately from team completion; local passing checks do not establish merge, deployment, Canvas submission, or human review.

Classify product/documentation findings using the course's functionality-bug, feature-flaw, and documentation-bug distinctions when helpful. Justify severity by actual impact and likely usage; do not invent penalties or prioritize cosmetic findings over serious defects. Do not weaken the UG to hide failures, fabricate test results, or manipulate contribution attribution.

## Team use and maintenance

Keep this folder in the shared repository so teammates receive the same version. Teammates using Codex can explicitly request `$spotter-quality`; AGENTS.md also requires its use for applicable work. Other tools can be asked to read this file and relevant references directly; automatic discovery depends on the tool.

Update instructions through team review when official rules or demonstrated defects justify a change. Put temporary assignments and deadlines in issues/milestones. Turn confirmed product defects into meaningful regression tests where practical; avoid accumulating generic rules.

AI assistance acknowledgement: Helen used OpenAI Codex to design and draft this skill, its references, and the accompanying AGENTS.md additions on 2026-10-13. This records localized assistance for these instructions. Future product code or documentation generated with AI needs its own accurate attribution under the course policy; this acknowledgement does not cover future work.
