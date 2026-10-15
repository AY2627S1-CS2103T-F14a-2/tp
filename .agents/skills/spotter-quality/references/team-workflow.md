# Recommended team workflow

This is a proposed Spotter practice, not a prescribed course reviewer count. Adopt/refine through team review. It uses the course's recommendation for small increments, feature ownership, AI first-pass reviews, and accountable human review. Source: [Week 8](https://nus-cs2103-ay2627-s1.github.io/website/admin/tp-w8.html), including its AI review panel.

## Per change

1. The feature owner states the issue, intended behaviour, boundaries, and acceptance examples. Keep related code, tests, and UG/DG work with that owner where practical; a component owner advises/reviews rather than writing every change to that component.
2. Implement a small working increment on a dedicated fork branch. If AI writes it, read and understand its change. Use the skill during implementation to catch contract omissions early.
3. Before opening/marking the PR ready, ask for a read-only skill review of the branch against team `master`. Provide the issue and exact base/revision. Let it inspect connected code and run relevant checks, then personally judge findings. Request fixes separately or explicitly authorize fixing the scoped findings.
4. Record verified checks and important limitations in the PR. A teammate reviews the actual final change and relevant behaviour. They can use the skill as a first pass but must vet any comments before posting. AI analysis does not count as the teammate's understanding or approval.
5. After fixes, rerun affected checks on the new revision. Confirm current CI and applicable teammate review before merging through the team's policy. Synchronize by merging upstream changes; retain PR branches.

Do not run a full course audit after every edit. Use changed-path tests while developing, the required Java checks when ready, and manual checks proportional to the change. New bugs/failures justify additional testing; unchanged passing evidence need not be rerun repeatedly.

## Example prompts

Author after coding:

```text
Use $spotter-quality in review mode. Review my current branch against
upstream/master for issue #<number>. Inspect connected code, tests, and
affected UG/DG content, and run relevant checks. Do not edit files or post
comments. Report confirmed defects, untested risks, and check results.
```

Reviewer in their own checkout:

```text
Use $spotter-quality to review PR <URL> at its latest revision.
Check the issue's acceptance criteria, integration paths, regression risks,
and documentation. Keep this read-only and draft comments for me to vet.
```

Iteration coordinator:

```text
Use $spotter-quality for a Week <number> / v<version> readiness review.
Refresh the official requirements and expandable answers. Separate my
personal tasks from team completion and use PASS/FAIL/UNKNOWN/N/A with
evidence. Do not publish, merge, close issues, or submit anything.
```

Replace placeholders with actual information. Repository-local Codex discovery is documented in [Build skills](https://learn.chatgpt.com/docs/build-skills). Teammates receive the skill after updating their checkout; other assistants can be instructed to read SKILL.md directly. No shared chat or copying into personal skill folders is needed for repository use.

## Weekly and release checks

One coordinator refreshes the current official weekly tasks and records owners/artifacts/deadlines in issues or project notes. Each member checks their own evidence; the coordinator checks team artifacts. Share the source inventory instead of independently rereading all historical pages for every PR.

For alpha/release readiness, use the packaged JAR with fictional data. Cross-test features written by someone else and choose trainer workflows spanning features. Record real defects and regression tests. Assign platform/scaling checks according to available machines and state gaps honestly.

Keep one accountable owner for each project role with backups if useful. Attend the agreed weekly meeting, communicate in English, and give constructive feedback early. Peer-evaluation submissions are individual and must not be fabricated or completed using another person's private ratings.
