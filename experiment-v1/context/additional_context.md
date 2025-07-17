# Additional Context for Promotion Service Implementation

## Overview
This document captures critical implementation details, lessons learned, and specific technical decisions made during the successful Step 1-6 implementation of the promotion service. Use this as supplementary context to recreate the working solution.

## Critical Implementation Decisions

### 1. R2DBC Configuration Issues and Resolution
**Problem:** Initial R2DBC configuration with custom JSON converters caused entity mapping conflicts.
**Solution:** 
- **DELETE** any custom `R2dbcConfiguration.java` file
- Use Spring Boot's auto-configuration for R2DBC
- Implement manual JSON serialization in service layer using `ObjectMapper`
- Change database schema from JSONB to TEXT for conditions/rules storage

**Key Files:**
- `src/main/java/com/promotion/service/config/R2dbcConfiguration.java` - **MUST BE DELETED**
- Database migration: `V2__Change_jsonb_to_text.sql` - Convert JSONB to TEXT

### 2. Entity Model Design
**Critical Decision:** Use String fields instead of JsonNode for conditions/rules storage.

```java
// In Promotion.java entity
@Column("conditions_json")
private String conditionsJson;

@Column("rules_json") 
private String rulesJson;
```

**Rationale:** Avoids R2DBC JsonNode mapping complexities while maintaining JSON storage capability.

### 3. Service Layer JSON Handling
**Implementation Pattern:**
```java
// Manual JSON serialization in PromotionService
private String conditionsJson = objectMapper.writeValueAsString(request.getConditions());
private String rulesJson = objectMapper.writeValueAsString(request.getRules());

// Manual JSON deserialization
PromotionConditionRequest[] conditions = objectMapper.readValue(
    promotion.getConditionsJson(), PromotionConditionRequest[].class);
```

### 4. Database Schema Evolution
**Migration Strategy:**
- V1: Initial schema with JSONB columns
- V2: Convert JSONB to TEXT columns (required for R2DBC compatibility)

**Critical SQL:**
```sql
ALTER TABLE promotion_conditions ALTER COLUMN condition_data TYPE TEXT;
ALTER TABLE promotion_rules ALTER COLUMN rule_data TYPE TEXT;
```

### 5. Validation Architecture
**Multi-layer Validation:**
1. **Bean Validation:** `@NotNull`, `@Size`, `@DecimalMin` annotations
2. **Custom Validation:** `@ValidPromotionValue` for business rules
3. **Global Exception Handling:** Comprehensive error response formatting

**Key Validator Implementation:**
```java
// PromotionValueValidator - refactored to avoid checkstyle violations
// Split into smaller methods (max 50 lines per method)
private boolean validateValueForType(CreatePromotionRequest request, ConstraintValidatorContext context)
private String getValidationErrorMessage(String type, BigDecimal value)
```

### 6. Error Handling Strategy
**Comprehensive Exception Mapping:**
- `PromotionNotFoundException` → 404 NOT_FOUND
- `PromotionValidationException` → 400 BAD_REQUEST  
- `ServerWebInputException` → 400 BAD_REQUEST (JSON parsing errors)
- `DecodingException` → 400 BAD_REQUEST (request body issues)
- `MethodArgumentNotValidException` → 400 BAD_REQUEST (validation errors)

**Priority-based Error Messages:**
1. `ValidPromotionValue` errors (highest priority)
2. `DecimalMin` errors
3. Other validation errors

## Bruno API Test Integration

### Test Data Requirements
**Critical Test Data Patterns:**
```json
{
  "code": "UPPERCASE_CODES",  // Always uppercase
  "usage_limit": 100,         // Always required
  "conditions": [],           // Can be empty array
  "rules": []                // Can be empty array
}
```

### Test Execution Environment
**Prerequisites:**
- PostgreSQL running on localhost:5432
- Database: `promotion_service_v10`
- Application running on port 8080
- Bruno CLI installed and configured

**Test Execution Pattern:**
```bash
cd api-tests
bruno run Create-Promotion/percentage-discount --env Local
bruno run Create-Promotion/fixed-amount-discount --env Local  
bruno run Create-Promotion/free-shipping-discount --env Local
```

## Build and Deployment Considerations

### Gradle Build Configuration
**Critical Build Steps:**
1. Fix checkstyle violations before building
2. Use `./gradlew build -x test` for faster builds during development
3. Run full `./gradlew build` for production builds

**JAR Execution:**
```bash
java -jar build/libs/generated_code-1.0.0.jar --server.port=8080
```

### Database Setup
**Required Database Objects:**
- Tables: `promotions`, `promotion_conditions`, `promotion_rules`
- Flyway migrations: V1 (initial), V2 (JSONB to TEXT conversion)
- Connection: R2DBC PostgreSQL driver

## Testing Strategy

### Integration Test Results (Final State)
**Bruno API Tests:** 100% Success Rate
- Percentage Discount: 1/1 requests, 11/11 tests, 7/7 assertions ✅
- Fixed Amount Discount: 1/1 requests, 10/10 tests, 7/7 assertions ✅
- Free Shipping Discount: 1/1 requests, 10/10 tests, 7/7 assertions ✅
- Code Validation (Positive): 3/3 requests, 12/12 tests, 12/12 assertions ✅
- Code Validation (Negative): 3/3 requests, 13/13 tests, 3/3 assertions ✅

**Total:** 9/9 requests, 56/56 tests, 36/36 assertions

### Unit Test Coverage (Current State)
- **Overall Coverage:** 7% (needs improvement)
- **Validation Package:** 90% (excellent)
- **DTO Request Package:** 17% (partial)
- **Service/Controller:** 0% (missing - requires TDD implementation)

## Common Pitfalls and Solutions

### 1. R2DBC Entity Mapping Errors
**Symptom:** `MappingException: Couldn't find PersistentEntity for property @Id UUID id`
**Solution:** Remove custom R2DBC configuration, use Spring Boot defaults

### 2. JSON Serialization Issues
**Symptom:** Empty conditions/rules arrays in responses
**Solution:** Implement manual JSON serialization in service layer

### 3. Checkstyle Violations
**Symptom:** Build failures due to method length violations
**Solution:** Refactor large methods into smaller, focused methods (max 50 lines)

### 4. Test Data Mismatches
**Symptom:** Bruno tests failing due to data format issues
**Solution:** Ensure uppercase codes, include usage_limit, proper JSON structure

### 5. Database Connection Issues
**Symptom:** Application startup failures
**Solution:** Verify PostgreSQL running, correct database name, R2DBC URL format

## Technology Stack Specifics

### Core Dependencies
```gradle
implementation 'org.springframework.boot:spring-boot-starter-webflux'
implementation 'org.springframework.boot:spring-boot-starter-data-r2dbc'
implementation 'org.springframework.boot:spring-boot-starter-validation'
implementation 'org.postgresql:r2dbc-postgresql'
implementation 'org.flywaydb:flyway-core'
implementation 'com.fasterxml.jackson.core:jackson-databind'
```

### Development Tools
- **Build:** Gradle 8.14
- **Java:** 24.0.1 (OpenJDK)
- **Database:** PostgreSQL 17.5
- **API Testing:** Bruno CLI
- **Code Quality:** Checkstyle, JaCoCo

## Success Metrics

### API Integration Tests
- **Target:** 100% test success rate
- **Achieved:** 9/9 requests, 56/56 tests, 36/36 assertions passing

### Database Persistence
- **Target:** Successful storage and retrieval of conditions/rules
- **Achieved:** Database logs show successful INSERT operations

### Error Handling
- **Target:** Comprehensive validation with clear error messages
- **Achieved:** Priority-based error message selection working correctly

### Code Quality
- **Target:** Clean code following project guidelines
- **Achieved:** Checkstyle compliant, proper separation of concerns

## Recommended Implementation Order

1. **Database Setup:** Create schema, run migrations
2. **Entity Models:** Simple POJOs with String JSON fields
3. **Repository Layer:** Basic R2DBC repositories
4. **Service Layer:** Business logic with manual JSON handling
5. **Controller Layer:** Thin REST endpoints
6. **Validation Layer:** Custom validators for business rules
7. **Exception Handling:** Global exception handler
8. **Integration Testing:** Bruno API tests
9. **Unit Testing:** TDD approach for remaining coverage

## Environment Configuration

### Local Development
```properties
spring.r2dbc.url=r2dbc:postgresql://localhost:5432/promotion_service_v7
spring.r2dbc.username=postgres
spring.r2dbc.password=password
spring.flyway.url=jdbc:postgresql://localhost:5432/promotion_service_v7
```

### Test Execution
```bash
# Start application
java -jar build/libs/generated_code-1.0.0.jar --server.port=8080 &

# Run Bruno tests
cd api-tests
bruno run Create-Promotion --env Local
```

This additional context should provide all the critical details needed to recreate the successful promotion service implementation from Steps 1-6. 