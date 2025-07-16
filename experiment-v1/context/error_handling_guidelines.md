# Error Handling and Validation Guidelines

## 1. Validation Strategy by Category

To ensure consistency, validation rules are grouped into categories, each with a defined implementation strategy. When adding new validations, first identify the appropriate category and then apply its specified strategy.

### Category 1: Single-Field Validation (DTO-Level)

*   **Description**: Rules that check a single field in isolation. This includes format, length, range, and not-null checks.
*   **Implementation Strategy**: Always implement these using standard **Bean Validation annotations** (e.g., `@NotBlank`, `@Size`, `@Min`, `@Max`, `@Pattern`) directly on the fields of the DTO classes 

### Category 2: Cross-Field Validation (Class-Level)

*   **Description**: Rules that depend on the values of two or more fields within the same request object.
*   **Implementation Strategy**: Handle these within a single, **custom class-level validator**. Use the existing `@ValidPromotion` annotation and add the logic to `Validator class`. This keeps all complex object-level logic in one place.

### Category 3: Service-Layer Business Validation

*   **Description**: Complex business rules that may require database lookups, external API calls, or coordination with other services. These cannot be handled by simple annotations.
*   **Implementation Strategy**: Implement these checks within the **Service layer**  These checks should occur after initial validation passes and should throw custom, specific exceptions.

---

## 2. Standardized Error Response Format

All validation and business error responses **must** follow this structure to provide a consistent experience for API clients.

```json
{
  "type": "validation_error",
  "message": "Invalid field values in promotion request",
  "details": [
    {
      "field": "field_name",
      "message": "specific error message"
    }
  ]
}
```

---

## Validation Handling
- Use `@Valid` annotation on request DTOs
- Create custom validators when needed
- Handle `WebExchangeBindException` globally
- Return consistent validation error responses

## Global Exception Handler
- Implement using `@ControllerAdvice`
- Handle both framework and custom exceptions
- Map exceptions to appropriate HTTP status codes
- Return standardized error response format


To support multiple field-level validation errors in a single response follow this structure:
example : given invalid code and invalid value
```json
{
  "type": "validation_error",
  "message": "Invalid field values in promotion request",
  "details": [
    {
      "field": "code",
      "message": "Promotion code must contain only letters, numbers, underscores, and hyphens"
    },
    {
      "field": "value",
      "message": "Percentage value must be between 1 and 100"
    }
  ]
}
```

## HTTP Status Codes
Use appropriate status codes:
- 400: Bad Request (validation errors) 
- 401: Unauthorized
- 403: Forbidden
- 404: Not Found
- 409: Conflict
- 422: Unprocessable Entity (business rule violations)
- 500: Internal Server Error

###  Error Handling Strategy
**Comprehensive Exception Mapping:**
- `ProductIdNotFoundException` → 404 NOT_FOUND
- `CategoryIdNotFoundException` → 400 BAD_REQUEST  

## Logging Guidelines
- Log all exceptions at appropriate levels
- Include correlation IDs in logs
- Mask sensitive data
- Structure:
  ```
  ERROR [correlationId] Exception class: Message | Context: {relevant_data}
  ```

## Best Practices
1. **Never expose internal details**
   - Mask stack traces
   - Use friendly messages for clients
   - Log detailed errors server-side

2. **Consistent Error Handling**
   - Use the same format across all endpoints
   - Handle all possible error scenarios
   - Provide actionable error messages

3. **Validation**
   - Validate early
   - Use bean validation where possible
   - Create custom validators for complex rules

4. **Reactive Error Handling**
   - Use `onErrorResume` for recovery flows
   - Handle errors close to their source
   - Maintain the reactive chain
