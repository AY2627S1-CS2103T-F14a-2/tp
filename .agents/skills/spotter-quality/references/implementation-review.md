# Implementation review

Official basis: [Implementation grading](https://nus-cs2103-ay2627-s1.github.io/website/admin/tp-grading.html), [Java coding standard](https://se-education.org/guides/conventions/java/index.html), and [testing expectations](https://nus-cs2103-ay2627-s1.github.io/website/admin/tp-expectations.html). Last verified: 2026-10-13.

## Course criteria

Review standards manually as well as with Checkstyle. Consider abstraction consistency, deep nesting/long methods, duplication (including tests), and appropriate logging, exceptions, assertions, and defensive coding. Evidence of those techniques across contributed work does not require inserting every technique into every method.

## Recommended Spotter checks

- Trace connected parser, command, model, storage, and UI paths where applicable. Check existing callers before changing a contract.
- Follow surrounding AB3 architecture. Avoid introducing a second design style or a generic framework for a single use case.
- Handle external input with recoverable errors. Assertions belong to internal invariants; essential validation must work with assertions disabled.
- Check exception handling boundaries, failed persistence, and success feedback. Avoid swallowing failures or logging client details unnecessarily.
- Review identity, equality, mutable collections, and client references for related entities. Check attached records after client edits/deletion.
- Examine conversion and serialization together, including precision and validation of stored values.
- Extract duplication when it clarifies the contract; keep methods at a consistent abstraction level.

## Verification

Test changed outcomes, invalid inputs, and important boundaries. Add meaningful regression tests for confirmed defects where practical. Use integration checks when correctness spans components. Avoid tests that mirror implementation details.

Use JDK 25 and AGENTS.md's Gradle test/Checkstyle commands for Java changes. Report environment failures and skipped checks accurately. No fixed coverage threshold is imposed, but risky untested paths deserve attention.

Use [bug prevention](bug-prevention.md) for user-visible behaviour and [documentation review](documentation-review.md) for affected UG/DG content.
