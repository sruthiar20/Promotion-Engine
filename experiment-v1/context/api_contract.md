### API Contract Reference

#### Get Promotion API

**Endpoint:** `GET /admin/promotions/searchById`

**Authentication Required:** Yes (Admin token required)

**Headers:**

```
Content-Type: application/json
```

**Request Body:**

```json
{
  "starts_at": "2026-07-01T00:00:00Z", // Optional: ISO8601 date string
  "ends_at": "2026-08-31T23:59:59Z", // Optional: ISO8601 date string
  "status":"active",//required
  "product-id":"SKU-PRO-001", // either product or 
  "category-id":"SKU-CAT-001" // category optional
}
```

**Successful Response (200 OK):**

```json
{
    "id": "6f40aa03-4fe2-4307-8989-3f7b49fc1aba",  // Unique identifier
    "code": "SUMMER2024",           // Provided code
    "type": "fixed_amount",         // Promotion type
    "value": 10.99,         // Discount value
    "value_type": "fixed_amount",   // Value type
    "starts_at": "2026-06-01T00:00:00Z",
    "ends_at": "2026-08-31T23:59:59Z",
    "is_automatic": false,
    "usage_limit": 1000,
    "usage_count": 0,               // Initial usage count
    "status": "active",              // Initial status
    "conditions": [
    {
      "value": 50.00,
      "type": "minimum_cart_total",
      "currency": "USD"
    },
    {
      "type": "product",
      "value": ["SKU-PRO-001","SKU-PRO-002","SKU-PRO-003"]
    },
    {
      "type": "category",
      "value": ["SKU-CAT-001","SKU-CAT-002"]
    }
  ],
    "rules": [
    {
      "type": "customer_group",
      "attribute": "group",
      "operator": "in",
      "value": ["registered", "vip"]
    },
    {
        "type": "time_range",
        "attribute": "order_time",
        "operator": "between",
        "value": {
          "start_time": "15:00:00",
          "end_time": "10:00:00"
        }
      }
  ],
    "created_at": "2026-01-15T10:00:00Z",
    "updated_at": "2026-01-15T10:00:00Z"
}
```


**Error Responses:**

1. Validation Error (400 Bad Request):

```json
{
  "type": "validation_error",
  "message": "Invalid field values in promotion request",
  "details": [
    {
      "field": "code",
      "message": "Promotion code already exists"
    }
  ]
}
```

