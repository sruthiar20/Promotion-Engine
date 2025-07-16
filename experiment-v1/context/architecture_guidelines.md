REST API end points - refer api_contracts.md for API contracts

Follow - domain driven design, TDD, clean code practises, Reactive programing style

# Guidelines for Reactive Java REST API Projects
## Use **reactive programming** paradigms (e.g., Project Reactor's Mono/Flux) for all service and controller logic.
## In **controller classes**:
  - Expose only REST API endpoints.
  - Do not implement business logic in controllers.
  - Only call the corresponding service method for each endpoint.
## In **service classes**:
  - Implement all business logic here.
  - Always handle exceptions within service methods using custom exception classes.
  - Use custom exceptions for domain-specific error scenarios.
  - Add both success and error logs in each service method.
  - Use constants for all hardcoded values (e.g., error messages, status codes, default values).
  - Prefer built-in Java and framework functions (e.g., Streams, Collections, Reactor operators) over manual or traditional approaches (e.g., avoid manual loops when map/filter can be used).
## Data Transformation:
  - Use functional transformation (map/flatMap) for converting between DTOs and Entities.
  - Avoid using setters or builders for field-by-field updates.
  - Create pure mapping functions that take input and return transformed output.
  - Keep transformations immutable and side-effect free.
  - Use Stream API or Reactor operators for collection transformations.
  - Always use snake_case in api request and respond 
- Maintain clear separation of concerns: controllers only delegate, services handle logic and error management.
- Follow RESTful conventions for endpoint naming and HTTP method usage.
- Ensure that error responses do not expose internal implementation details. 

