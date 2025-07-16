**Goal:** Develop promotion service for creating, managing, and tracking various types of promotions with flexible rules, conditions, and comprehensive validation. Service should be well tested and ready to deploy

Step 1:  Process the following files functional_context.md, api_contract.md, db_schema.md, error_handling_guidelines.md, pre-conditions.md, java_stack_details.md,architecture_guidelines.md, coding_guidelines.md, sonar_details.md from current folder.

Step 2 : Process Bruno API Test files in /api-tests/Create-Promotion identify test scenarios and validate it against api_contract.md

Step 3 : all the pre-conditions defined in pre-conditions.md should be successful before proceeding

Step 4: Use experiment-v1/generated_code as project base folder for code generation. Create promotion engine based on given api-contract. Implement relevant core business logic for handling these business requirements and including validation.

Step 5: Build and validate the service using:
Use Bruno CLI to run tests under the folder: 
/api_tests/Get-Promotion/
and ensure all bruno test passes,verify the error messages strictly follow the definitions in error_handling_guidelines.md and functional_context.md
Update the service code (not the api-test) to correct the logic or error handling.The goal is to make the implementation fully compliant with both functional and validation expectations.

Step 6: Process test-guideline.md and Create comprehensive unit tests as mentioned in the guidelines. Generate a code coverage report (using Jacoco), and list the methods not covered and ensure its completed.

Step 7: Ensure SonarQube code coverage > 95% for both line and branch metrics.
Present a consolidated report containing:
Unit test results and coverage
Functional test results
API test results from Bruno
Summary of validation and error handling scenarios covered
