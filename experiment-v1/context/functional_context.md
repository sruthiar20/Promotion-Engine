Promotion engine is responsible for fetching various types of promotions with promotions created in db based on given id and comprehensive validation.

## Exisiting Promotion Types in database:
- `fixed_amount`: Fixed currency amount discount
- `percentage`: Percentage-based discount
- `free_shipping`: Shipping fee waiver
- `buy_x_get_y`: Buy X Get Y discount
- `bundle_offer`: Bundle offer discount

### Procedural Steps to Implement the Promotion Engine

1.  Validate the presence and format of the request body fields.
    *   `status` is required.
    *   `starts_at` and `ends_at`, if provided, must be between the given dates and  valid ISO8601 date strings format.(example: starts_at:"2026-07-09T00:00:00Z")
    *   Ensure that either `product-id` or `category-id` is provided, but not both.
2.  If validation fails, return a `400 Bad Request` error with a descriptive message.
3.  If validation succeeds, store the response in database.

---

###  **Product/Category Filter Logic**

* **Condition Matching**:
filter documents based on whether `product_id` or `category_id` is provided in the request.
* **Primary Lookup:**
  * First, check if the given product_id or category_id exists in the promotion_engine_v1 database.
* **Fallback Lookup:**
  * If not found, search in the existing promotiondb database.  
* **Promotion Retrieval Logic:**:
  * If `product_id` is provided:
    * Search for documents where the `conditions` array contains an object with:
      * `"type": "product"`
      * `"value"` array includes the specified `product_id`
  * If `category_id` is provided:
    * Search for documents where the `conditions` array contains an object with:
      * `"type": "category"`
      * `"value"` array includes the specified `category_id`

---

### **Database Handling**

* Existing Source: promotiondb (used to fetch data if not found in new DB)

* New Target DB: Create a new database named promotion_engine_v1 inside the experiment-v1 service. Use it to store retrieved results from promotiondb for future direct access

---

**Date Validations**:

- **Validation**: End date is before start date
  **Error Message**: End date must be after start date
  
- **Validation**: Start date is in the past for new promotions
  **Error Message**: Start date must not be in the past

- **Validation**: Start date and End date fields must be in ISO 8601 (YYYY-MM-DDTHH:mm:ss.sssZ)
  **Error Message**: Invalid date format

**Status Validation**:

- **Validation**: status must always be "active" (case-insensitive)
- **Error Message**: status value must be as active

**product-id Validation**:
- **Validation**: product_id can be any non-empty string (e.g., SKU-PRO-001)
- **Error Message**: product_id must be a valid string

**category-id Validation**:
- **Validation**: category_id can be any non-empty string (e.g., SKU-CAT-001)
- **Error Message**: category_id must be a valid string

**IF BOTH PRODUCT_ID AND CATEGORY_ID IS GIVEN**:

**product_id and category_id Validation**:
- **Validation**: request cannot have both product_id and category_id
- **Error Message**: Fields product_id and category_id are mutually_exclusive_fields — only one must be provided

---

### Sample Error Response Format
```json
{
  "type": "validation_error",//always same for all error response
  "message": "Invalid field values in promotion request",//always same for all error response
  "details": [
    {
      "field": "product_id, category_id",
      "message": "Fields product_id and category_id are mutually_exclusive_fields — only one must be provided"//specific error message for given validation
    }
  ]
}
```
