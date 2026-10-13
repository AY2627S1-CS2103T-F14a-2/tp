# Documentation review

Official basis: [Deliverables](https://nus-cs2103-ay2627-s1.github.io/website/admin/tp-deliverables.html), [documentation grading](https://nus-cs2103-ay2627-s1.github.io/website/admin/tp-grading.html), and [attribution policy](https://nus-cs2103-ay2627-s1.github.io/website/admin/appendixB-policies.html). Last verified: 2026-10-13.

## Course requirements

Keep primary contributions in `docs/UserGuide.md` and `docs/DeveloperGuide.md`; describe current behaviour accurately and distinguish future features. Store PlantUML sources under `docs/diagrams` when used.

The DG needs manual-testing instructions for changed/new user-testable features; untouched AB3 features need not be repeated. Align implemented requirements with behaviour, retain future requirements, and use use cases for non-trivial unique interaction patterns. Maintain acknowledgements.

## Recommended review

- UG: verify target trainer, value proposition, setup, syntax, parameters, examples, outcomes, limits, and recovery advice against behaviour.
- Run changed examples with fictional data and stated preconditions. Avoid unexplained indices that depend on hidden filtering.
- DG: read as a new maintainer. Check component relationships, design decisions, requirements, and manual-testing paths against code.
- Check course UML notation, arrows, labels, diagram purpose, and useful level of detail.
- Check user stories for matching role/goal/benefit, use cases for meaningful extensions, NFRs for measurable scope, and glossary for necessary terms.
- Inspect visuals, formatting, terminology, and links. Prefer useful examples/screenshots over repetitive ones.
- Keep informative runtime errors for obscure invalid inputs rather than documenting every possible variation. Do not remove useful documentation to avoid maintenance.

## Verification and attribution

For site-content changes, run `npm run build` from `docs` using the repository setup and inspect affected rendered pages. Build success alone does not establish accuracy/readability. Avoid committing generated output or unrelated dependency changes.

Instruction-only skill/AGENTS.md changes do not enter the MarkBind site: verify Markdown, links, and diff instead.

Credit localized AI assistance near affected material; for widespread use, record the actual tool, contributor, and extent in DG Acknowledgements. Preserve factual ownership; do not invent human-review claims or attribute another member's work to the requester.

## Expanded deliverable qualifications

Keep editable diagram sources; use a consistent team tool unless another is needed for a concrete limitation. PlantUML is recommended, not mandatory. Do not use unreviewed IDE-generated UML: inferred relationships/multiplicities and excessive detail can misrepresent design. Existing-diagram updates count; a feature with little diagram impact can still be documented through a relevant explanation or proposed design where appropriate. Diagrams should explain one level/component's details at a time, integrate with nearby text, and remain readable without excessive zoom.

DG is a minimal sufficient roadmap and rationale for maintainers, not a transcription of code. Avoid long snippets; remove/update obsolete proposed-feature descriptions if retained. Requirements must include all current stories and meaningful unique use cases; not every simple feature needs a separate use case. Don't delete ancillary developer pages solely because ungraded/outdated if doing so breaks links.

Product website: consistent branding and AboutUs roles/profile links, with current `Ui.png` showing representative, well-populated fictional data, clean crop, and useful resolution. Keep original screenshot proportions for course aggregation. Course profile photos/allowed privacy alternatives are separate authorized admin artifacts, not client data. PPP, when applicable, uses `docs/team/<lowercase-username>.md`, summarizes genuine implementation/docs/team/review contributions and code links, and needs no PDF submission. Demo instructions shown under a removed-deliverable panel are historical, not compulsory.
