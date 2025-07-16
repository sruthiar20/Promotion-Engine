 ## Write unit testcases for this class using JUnit:
* Cover all core business logic, test must cover success, failure and edge case scenarios.
* Follow assertions and mocking best practices using JUnit 5 and Mockito.
* Validate all input validation rules individually.
* Use the Arrange–Act–Assert (AAA) pattern in the test structure.
* Ensure test coverage meets SonarQube standards (e.g., >90% branch and line coverage).


## Common Test Case Coverage Techniques
1. Statement Coverage
    * Ensures every line of code is executed at least once.
    * Use in Cursor: Generate test cases via AI or Copilot that touch every line.
2. Branch Coverage (Decision Coverage)
    * Ensures every possible branch (if/else, switch cases) is executed.
    * Use in Cursor: Ask the AI to generate tests covering all logical paths.
3. Condition Coverage
    * Ensures every boolean sub-expression is tested for both true and false.
    * Use in Cursor: Useful when testing complex if conditions.
4. Path Coverage
    * Ensures every possible path through the code is executed.
    * More exhaustive, can be impractical for large functions with many branches.
5. Function/Method Coverage
    * Ensures every method or function is invoked during testing.
    * Basic, but important in modular codebases.
6. Loop Coverage
    * Ensures loops execute zero times, once, and multiple times.
