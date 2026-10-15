# Bug prevention

Official basis: [PE bug triaging](https://nus-cs2103-ay2627-s1.github.io/website/admin/tp-pe.html) and [QA grading](https://nus-cs2103-ay2627-s1.github.io/website/admin/tp-grading.html). Last verified: 2026-10-13.

## Course distinctions

Functionality bugs violate specified or reasonably expected behaviour. Feature flaws undermine normal use through design or missing functionality. Documentation bugs require correcting explanations/instructions.

Inherited AB3 defects count. Known-issue notices may reduce impact but do not remove responsibility. Judge severity by usage impact: cosmetic only, rare minor inconvenience, occasional inconvenience, or major disruption for most users.

## Recommended Spotter probes

Select relevant cases with fictional data and explicit expected outcomes. These prompts do not authorize adding features.

| Changed area | Probes |
| --- | --- |
| Parser | Empty input; missing/blank values; repeated/unknown prefixes; extra parameters; spaces; accidentally missing separators |
| Selection | Zero/negative index; last valid/first invalid index; empty list; mutation after filtering or deletion |
| Names/search | Case/whitespace variations; realistic punctuation; similar client names; delimiters inside field values |
| Measurements | Supported boundaries and adjacent invalid values; zero/negative; decimals; overflow; non-finite values if parseable; units |
| Sessions | Invalid calendar dates; leap days; past records; boundary times; ordering/overlap only where specified |
| Related records | Intended client association; stale references; client edits/deletion; add/edit/remove sequences |
| Persistence | Save/restart round trip; valid manual edits; malformed input at promised support level; failed writes; precision |
| UI | Long values; many records; keyboard access; readable errors; required screen settings |
| Sequences | Failed edit then list/save/restart; delete then add; filtered list then mutation; repeated commands |

For a rejected command, compare state before and after to detect unintended partial changes. Check the actual result as well as feedback. Use a sandbox, not real trainer data.

## Acceptance checks

Recommended questions: Can a trainer finish the promised workflow? Is its result useful for the next session? Can they recover from a typo? Are commands easy to type and remember? Are restrictions justified by their work rather than parsing convenience?

Official PE guidance highlights overrestrictive validation, duplicate-detection limitations, case sensitivity, vague errors, and cumbersome commands. Check these where relevant; do not automatically add fuzzy matching or aliases. Manual storage edits need at least AB3 support, not arbitrary-corruption recovery unless promised.

## Findings

Capture setup, exact inputs/sequence, expected/actual outcome, and impact. Distinguish reproduced defects from risks. For a UG mismatch, identify the intended contract before deciding whether code or documentation should change. Do not rewrite promises just to hide failures.

Explain how a missing capability harms a delivered workflow before calling it a flaw. For actual PE triage, consult the full current scope/duplicate rules; this checklist grants no permission to change issues or submit responses.

For systematic partitions, interacting inputs, root-cause diagnosis, and test-oracle failures, load [textbook testing](textbook-testing.md). Check UI refresh across every visible panel after mutations, complete deletion/clear behaviour, window shutdown, realistic sorting/search semantics, and locale-independent date handling where affected. Prefer representative trainer data sizes over arbitrary sabotage. The course's PE triaging scope is narrower than all useful engineering hardening.
