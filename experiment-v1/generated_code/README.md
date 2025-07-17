# Promotion Engine v1

A reactive Spring Boot application for managing promotions with fallback database lookup functionality.

## Features

- **Fallback Database Lookup**: Primary lookup in `promotion_engine_v1`, fallback to `promotiondb`
- **Reactive Programming**: Built with Spring WebFlux and Project Reactor
- **JSONB Support**: Efficient JSON storage and querying in PostgreSQL
- **Comprehensive Validation**: Input validation with detailed error messages
- **Clean Architecture**: Domain-driven design with clear separation of concerns

## Database Architecture

The application uses a dual-database approach:

1. **Primary Database**: `promotion_engine_v1` - New database for optimized storage
2. **Fallback Database**: `promotiondb` - Legacy database for backward compatibility

### Database Schema

```sql
CREATE TABLE promotions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(255) UNIQUE NOT NULL,
    type VARCHAR(50) NOT NULL,
    value NUMERIC,
    value_type VARCHAR(50),
    starts_at TIMESTAMPTZ,
    ends_at TIMESTAMPTZ,
    is_automatic BOOLEAN DEFAULT false,
    usage_limit INT,
    usage_count INT DEFAULT 0,
    status VARCHAR(50) NOT NULL,
    conditions JSONB,
    rules JSONB,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);
```

## API Documentation

### Search Promotions

**Endpoint**: `GET /admin/promotions/searchById`

**Request Body**:
```json
{
  "status": "active",
  "product-id": "SKU-PRO-001",
  "starts_at": "2026-07-01T00:00:00Z",
  "ends_at": "2026-08-31T23:59:59Z"
}
```

**Response** (200 OK):
```json
{
  "id": "6f40aa03-4fe2-4307-8989-3f7b49fc1aba",
  "code": "SUMMER2024",
  "type": "fixed_amount",
  "value": 10.99,
  "value_type": "fixed_amount",
  "starts_at": "2026-06-01T00:00:00Z",
  "ends_at": "2026-08-31T23:59:59Z",
  "is_automatic": false,
  "usage_limit": 1000,
  "usage_count": 0,
  "status": "active",
  "conditions": [...],
  "rules": [...],
  "created_at": "2026-01-15T10:00:00Z",
  "updated_at": "2026-01-15T10:00:00Z"
}
```

## Configuration

### Database Configuration

Update `application.properties`:

```properties
# Primary Database (promotion_engine_v1)
spring.r2dbc.primary.host=localhost
spring.r2dbc.primary.port=5432
spring.r2dbc.primary.database=promotion_engine_v1
spring.r2dbc.primary.username=postgres
spring.r2dbc.primary.password=postgres

# Fallback Database (promotiondb)
spring.r2dbc.fallback.host=localhost
spring.r2dbc.fallback.port=5432
spring.r2dbc.fallback.database=promotiondb
spring.r2dbc.fallback.username=postgres
spring.r2dbc.fallback.password=postgres
```

## How It Works

### Fallback Lookup Logic

1. **Primary Search**: Application first searches in `promotion_engine_v1` database
2. **Fallback Search**: If no results found, searches in `promotiondb` database
3. **Response**: Returns the first match found, or empty if no matches

### Product/Category Matching

The application searches for promotions where:
- For product search: `conditions` array contains `{"type": "product", "value": ["product-id"]}`
- For category search: `conditions` array contains `{"type": "category", "value": ["category-id"]}`

### Validation Rules

- **Status**: Must be "active" (required)
- **Product/Category**: Either `product-id` or `category-id` must be provided (mutually exclusive)
- **Dates**: Optional, but if provided, `ends_at` must be after `starts_at`
- **Format**: Dates must be in ISO 8601 format

## Running the Application

### Prerequisites

- Java 17+
- PostgreSQL 12+
- Two databases: `promotion_engine_v1` and `promotiondb`

### Setup

1. **Database Setup**:
   ```bash
   createdb promotion_engine_v1
   createdb promotiondb
   ```

2. **Build and Run**:
   ```bash
   ./gradlew build
   ./gradlew bootRun
   ```

3. **Test**:
   ```bash
   ./gradlew test
   ```

### Example Usage

```bash
curl -X GET http://localhost:8080/admin/promotions/searchById \
  -H "Content-Type: application/json" \
  -d '{
    "status": "active",
    "product-id": "SKU-PRO-001",
    "starts_at": "2026-07-01T00:00:00Z",
    "ends_at": "2026-08-31T23:59:59Z"
  }'
```

## Error Handling

The application returns structured error responses:

```json
{
  "type": "validation_error",
  "message": "Invalid field values in promotion request",
  "details": [
    {
      "field": "product_id, category_id",
      "message": "Fields product_id and category_id are mutually_exclusive_fields — only one must be provided"
    }
  ]
}
```

## Testing

Run the test suite:
```bash
./gradlew test jacocoTestReport
```

## Architecture

- **Controller**: Handles HTTP requests and validation
- **Service**: Contains business logic and fallback lookup
- **Repository**: Data access layer with JSONB queries
- **Model**: Domain entities with proper JSON mapping
- **Validation**: Custom validators for complex business rules

## Dependencies

- Spring Boot 3.4.4
- Spring WebFlux (Reactive Web)
- Spring Data R2DBC (Reactive Database Access)
- PostgreSQL R2DBC Driver
- Jackson (JSON Processing)
- JUnit 5 (Testing)
- Mockito (Mocking)

## Performance Considerations

- **Reactive Streams**: Non-blocking I/O for better scalability
- **JSONB Queries**: Efficient JSON operations in PostgreSQL
- **Database Indexing**: Recommended indexes on `status`, `conditions`, and date columns
- **Connection Pooling**: Configured for both primary and fallback databases 