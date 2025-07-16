# Promotion-Engine

## Discount Applicability Levels
- Product/Variant/Offer
- Product Hierarchy: Category/Subcategory
- Brand
- Payment method 
- Shipping method
- Loyalty membership
- Event
- Location

## General Discounts Configuration
- Stacking / no stacking
- Usage limits (optional)
- Start and end dates
- Start and end time for each day
- Promotion code (Optional)

## Status Management
- Discounts are created with status as drafts
- Can be set to active or in-active
- Automatic deactivation when end date is reached or usage limit is reached

## Promotion Combination Rules
1. **Stacking Rules**
   - Maximum 3 promotions can be stacked per order
   - Percentage discounts cannot be stacked with other percentage discounts
   - Free shipping can be combined with any other promotion type
   - Bundle offers cannot be combined with other promotions on same products

2. **Priority Rules (Highest to Lowest)**
   - Bundle offers
   - Buy X Get Y
   - Percentage discounts
   - Flat discounts
   - Free shipping
   - Within same type, higher discount value takes precedence

3. **Application Order**
   - Product-level discounts applied first
   - Category-level discounts applied second
   - Cart-level discounts applied last
   - Free shipping applied after all other discounts

4. **Special Rules**
   - Loyalty discounts can always stack
   - Payment method discounts applied last
   - Maximum total discount cannot exceed 70% of original price
   - Some premium products may be excluded from discounts
