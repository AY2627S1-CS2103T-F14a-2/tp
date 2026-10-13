# Git, PR, CI, and authorship review

Verified on 2026-10-13: [GitHub appendix](https://nus-cs2103-ay2627-s1.github.io/website/admin/appendixE-gitHub.html), [Week 7](https://nus-cs2103-ay2627-s1.github.io/website/admin/tp-w7.html), [Week 10](https://nus-cs2103-ay2627-s1.github.io/website/admin/tp-w10.html), [Week 11 authorship panels](https://nus-cs2103-ay2627-s1.github.io/website/admin/tp-w11.html), and [standards](https://nus-cs2103-ay2627-s1.github.io/website/admin/standardsAndConventions.html). Recheck linked conventions before committing or assessing a violation.

## Course and team workflow

- Verify remote URLs and fork identities: `origin` is normally personal fork and `upstream` team repo; names alone prove nothing. Team/fork repo remains `tp`, team organization uses prescribed identity, and shared branch remains `master`.
- Distinguish the team-to-course tracking PR from contributor PRs. Verify tracking PR base/head, exact team-tag/product title, and plain-text 1-2 sentence user/value description against current instructions. Do not impose that special format on ordinary PRs.
- Use small tasks/issues with clear owners, milestone/deadline, applicable labels, and matching PRs. A corresponding issue is generally encouraged, not a universal course mandate; specific tasks and team rules may require one. Multiple assignees discouraged for normal shared tasks: split individual portions when practical.
- Contribution PRs use separate personal-fork branches into team `master`. The course allows a centralized branch workflow after v1.3, but Spotter retains its fork convention unless the team changes it. Parallel tasks need separate branches and focused diffs.
- Preserve merge history and PR branches. Avoid squash/rebase because changed timestamps affect progress tracking. Sync from team upstream to local/fork and merge updates into outstanding branches. Do not create duplicate team branches merely to mirror retained fork branches; flag ambiguous requirements.
- Confirm current CI, reviews, conflicts/draft state, and addressed feedback. Course CI exceptions exist for justified cases (e.g., untestable coverage), but do not weaken Spotter's required-check policy or approve an exception unilaterally.

## Commit conventions

Inspect every introduced commit, separating author changes, merge commits, and inherited history. [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html): meaningful imperative subject, capitalized first letter, no terminal period, aim at 50 characters and stay within 72. Bodies are optional; if present apply basic separation/wrapping rules. Scope/category prefixes are optional; Conventional Commits are not mandatory. Meaningful branch names are recommended.

Record each issue by SHA and subject. Do not amend/rewrite history without authorization. A later commit cannot repair an earlier commit's message. Java basic/intermediate conventions are required; advanced Java, Markdown style, and Google documentation style are optional. For unspecified conventions, use consistent team/existing style; Checkstyle is not the entire standard.

## PR evidence and conduct

Apply actual repository templates/review policy, not an invented universal reviewer count. Link the correct issue; use closing keywords only for completed scope. Explain final outcome, significant design choices, actual verification and limitations. Check staged/unstaged/untracked files for unrelated changes, secrets/private data, generated output, conflict markers, and case-sensitive paths.

Reviews should explain actionable concerns at relevant lines plus an overall assessment. Ask for rationale/offer alternatives where useful; group repeated concerns; write clear, professional English. Authors respond respectfully. Do not invent trivial review activity or post AI comments without human vetting.

Local checks cannot prove live CI, human approval, merge, or deployment. Inspect relevant matrix jobs, wrapper validation, whitespace/line-ending checks, coverage task/Codecov status, and current required checks. Local and deployed MarkBind versions can differ; record differences, and verify deployment separately because the docs workflow may run only on `master`. PR CI tests a temporary merge; newer team commits or OS-sensitive paths can explain local/remote differences. Recheck after synchronization/fixes.

## Authorship and reuse

AI-assisted commits belong to the responsible human, not a Codex/Claude identity. Do not commit all teammates' contributions under one account. Check Git author/email and RepoSense's attributed lines. If identity is missing, prepare evidence for course contact; do not send it without authorization.

Use `@@author` overrides only to correct genuine misattribution, for meaningful functional/test blocks; no need to annotate docs. A claimed block must be more than 80% authored by that contributor. Do not claim generated boilerplate, copied trivial changes, or pad tests. Record significant unused work according to the course's `_unused`/`-unused` guidance only if relevant and authorized.

Inspired/adapted external code needs source comments; non-trivial copied blocks also need `-reused` author tags. Acknowledge libraries in DG and significant relevant reuse in an applicable PPP. Follow licenses and Spotter's stricter prohibition on student solutions. Localized AI credit goes near use; widespread help needs tool/user/extent in DG. Review and understand generated work. Early sparing AI generation is a course recommendation, not a permanent prohibition.

Checking Git grants no permission to commit, push, merge, delete branches/tags, or change issues. Preserve AGENTS.md boundaries.
