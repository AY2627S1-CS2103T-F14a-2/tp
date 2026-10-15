# Textbook testing and debugging

Last reviewed: 2026-10-13. Sources are the AY2627 S1 textbook chapters linked below, including expandable examples. These are engineering techniques, not new PE grading categories. PE uses functionality bugs, feature flaws, and documentation bugs; a Java exception is a diagnostic clue, not a severity label.

## Bugs to look for

| Failure family | Spotter-specific question | Best check |
| --- | --- | --- |
| Wrong requirements/design | Does a correctly implemented command still prevent a trainer's ordinary workflow? | Acceptance scenario against the user story, not just code |
| Off-by-one/stale state | Does an index select the wrong client after a filtered list changes? | Boundary and sequence tests |
| Branch/condition errors | Does a combined rule accept one invalid measurement or reject a valid one? | Exercise each condition, including short-circuit paths |
| Null/type errors | Can missing stored fields or assumed subtypes cause a crash? | External-boundary validation and parser/storage tests |
| Aliasing/mutation | Does reading a record expose a mutable collection, or an operation unintentionally alter another client's records? | State-before/after and identity tests |
| Collection/recursion errors | Is a collection modified during iteration? Can recursive handling lack a reachable termination case? | Small reproducible failure and debugger |
| Numeric/date errors | Overflow, lost decimal precision, incorrect units, wrong calendar rules, implicit locale dependence? | Value partitions, boundaries, and storage round trips |
| Component-contract mismatch | Do parser/model/storage/UI disagree about validity, identity, ordering, or results? | Integration checks across real components |
| Regression | Does a new command break an existing one or data from an earlier supported version? | Related automated tests and packaged-app sequence |
| Deployment/NFR failure | Does the JAR differ from IDE execution, hang with realistic data, or fail on supported platforms/scales? | System tests on the packaged artifact |
| Error-handling failure | Does a failure silently succeed, mutate partially, lose data, or report the wrong reason? | Negative tests that also assert retained state |
| Misleading test | Is the expected outcome wrong, does the test have hidden state, or does one invalid field mask another? | Verify the test oracle and isolate failure inputs |

This table applies textbook ideas to Spotter; it is not a requirement to add every feature or test every conceivable input. See [bug prevention](bug-prevention.md) for command-level probes and PE-specific limits.

## Design efficient tests

[Test Case Design](https://nus-cs2103-ay2627-s1.github.io/website/se-book-adapted/chapters/testCaseDesign.html): partition inputs and relevant object states by expected processing, choose representatives, and test on/near each boundary plus ordinary interior values. Ensure each selected valid value appears in a successful case; test each invalid input independently before combining invalid values. Use pairwise combinations for interacting factors when a full Cartesian product is wasteful. Include cross-field dependencies and use-case extensions. Every added test should target a distinct plausible fault.

[Testing](https://nus-cs2103-ay2627-s1.github.io/website/se-book-adapted/chapters/testing.html): use unit tests for local contracts, integration tests for component interactions, system tests against external specifications/NFRs, and acceptance tests against user needs. Combine scripted important workflows with exploratory follow-ups. Coverage indicates execution, not correct outcomes; branch and condition coverage differ. Inject controllable inputs/dependencies when time or storage makes a test unreliable, using existing architecture rather than a speculative framework. GUI automation is optional; manual GUI checks remain necessary when appropriate.

[Quality Assurance](https://nus-cs2103-ay2627-s1.github.io/website/se-book-adapted/chapters/qualityAssurance.html): check both whether the implementation follows its specification and whether the specification solves the user's problem. Code review/static analysis complement execution tests. Passing tests cannot establish absence of defects.

## Fix the cause

[Debugging](https://nus-cs2103-ay2627-s1.github.io/website/se-book-adapted/chapters/debugging.html): distinguish the originating mistake, software defect, corrupted runtime state, and visible failure. Reproduce the exact version, input, sequence, configuration, and starting state. Confirm the expected behaviour first; the test itself may be wrong. Reduce the failing case, form a falsifiable hypothesis, predict a distinguishing observation, and inspect state/logs/stack traces with the cheapest useful probe. Change one thing at a time. Explain all observed symptoms before keeping a fix; inspect similar paths, rerun the reproduction and relevant suite, retain a regression test, and remove temporary experiments. Treat AI diagnoses as hypotheses requiring evidence.

Common clues: null dereference, stale/out-of-range index, invalid cast, modifying an iterated collection (even in one thread), missing recursive base case, and unvalidated numeric parsing. The first application stack frame and wrapped causes often reveal more than the top library frame.

[Error Handling](https://nus-cs2103-ay2627-s1.github.io/website/se-book-adapted/chapters/errorHandling.html): handle unusual external failures with suitable exceptions/recovery; use assertions for internal invariants. Java assertions are disabled by default, so never place necessary work or essential input validation inside them. JUnit assertions are different. Logging aids diagnosis; defensive copies and enforced invariants prevent unintended state changes. Choose defensiveness proportionate to risk.

[Code Quality](https://nus-cs2103-ay2627-s1.github.io/website/se-book-adapted/chapters/codeQuality.html): examine ambiguous names, hidden shared state, unused code, ignored exceptions, copy-paste divergence, misleading default branches, reused variables, and unnecessary nesting. Prefer readable, consistent abstractions and documented rationale over cleverness or premature optimization.

[Integration](https://nus-cs2103-ay2627-s1.github.io/website/se-book-adapted/chapters/integration.html): small early integrations make incompatible assumptions easier to find; late/big-bang integration enlarges the debugging search space. CI supports this but does not replace user-facing checks.

## Security and requirement failures

[Secure Software Engineering](https://nus-cs2103-ay2627-s1.github.io/website/se-book-adapted/chapters/security.html): inspect trust boundaries, data integrity, confidentiality, availability, input interpretation, dependencies, and error paths. For Spotter, focus on local storage/config input, unsafe path handling, accidental disclosure in logs, and realistic resource exhaustion. Do not add accounts/authentication or a server: web examples in the chapter are not Spotter requirements. Never assume generated code is safe because it compiles. Security hardening can be valuable even where deliberate sabotage is outside PE bug scope.

[Requirements](https://nus-cs2103-ay2627-s1.github.io/website/se-book-adapted/chapters/requirements.html): requirements must be unambiguous and testable, and the set coherent and complete enough for its scope. Make important acceptance criteria explicit so an implementer cannot silently fill gaps with plausible guesses. Keep changed requirements synchronized with tests and guides.
