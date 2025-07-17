package com.promotion.engine.validation;

import com.promotion.engine.dto.request.PromotionSearchRequest;
import com.promotion.engine.exception.ValidationError;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

/**
 * Validator for promotion requests.
 * Implements all complex validation rules defined in the functional context.
 */
public class PromotionValidator implements ConstraintValidator<ValidPromotion, PromotionSearchRequest> {

    private static final String STATUS_ACTIVE = "active";

    @Override
    public void initialize(ValidPromotion constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(PromotionSearchRequest request, ConstraintValidatorContext context) {
        List<ValidationError> errors = new ArrayList<>();
        
        // Disable default constraint violation creation
        context.disableDefaultConstraintViolation();
        
        // Check status (required and must be "active")
        validateStatus(request, errors);
        
        // Check that either product-id or category-id is provided, but not both
        validateProductOrCategoryId(request, errors);
        
        // Date validations
        validateDates(request, errors);
        
        // Add all constraint violations
        for (ValidationError error : errors) {
            context.buildConstraintViolationWithTemplate(error.getMessage())
                  .addPropertyNode(error.getField())
                  .addConstraintViolation();
        }
        
        return errors.isEmpty();
    }
    
    private void validateStatus(PromotionSearchRequest request, List<ValidationError> errors) {
        String status = request.getStatus();
        
        if (status == null || status.trim().isEmpty()) {
            errors.add(new ValidationError("status", "Status is required"));
        } else if (!STATUS_ACTIVE.equalsIgnoreCase(status)) {
            errors.add(new ValidationError("status", "status value must be as active"));
        }
    }
    
    private void validateProductOrCategoryId(PromotionSearchRequest request, List<ValidationError> errors) {
        String productId = request.getProductId();
        String categoryId = request.getCategoryId();
        
        // Check for empty strings first (provided but invalid)
        if (productId != null && productId.trim().isEmpty()) {
            errors.add(new ValidationError("product_id", "product_id must be a valid string"));
            return; // Exit early to avoid other validations
        }
        
        if (categoryId != null && categoryId.trim().isEmpty()) {
            errors.add(new ValidationError("category_id", "category_id must be a valid string"));
            return; // Exit early to avoid other validations
        }
        
        // Check if both are provided (mutually exclusive)
        boolean hasProductId = productId != null && !productId.trim().isEmpty();
        boolean hasCategoryId = categoryId != null && !categoryId.trim().isEmpty();
        
        if (hasProductId && hasCategoryId) {
            // Both product-id and category-id provided (mutually exclusive)
            errors.add(new ValidationError("product_id, category_id", 
                "Fields product_id and category_id are mutually_exclusive_fields — only one must be provided"));
        } else if (!hasProductId && !hasCategoryId) {
            // Neither product-id nor category-id provided
            errors.add(new ValidationError("product_id, category_id", 
                "Either product_id or category_id must be provided"));
        }
    }
    
    private void validateDates(PromotionSearchRequest request, List<ValidationError> errors) {
        Instant startsAt = request.getStartsAt();
        Instant endsAt = request.getEndsAt();
        
        // Validate start date is not in the past or today
        if (startsAt != null) {
            // Check if start date is in the past or today (must be in the future)
            LocalDate startDate = startsAt.atZone(ZoneOffset.UTC).toLocalDate();
            LocalDate today = LocalDate.now(ZoneOffset.UTC);
            
            if (startDate.isBefore(today) || startDate.isEqual(today)) {
                errors.add(new ValidationError("starts_at", "Start date must not be in the past"));
            }
        }
        
        // Validate end date is after start date
        if (startsAt != null && endsAt != null) {
            if (endsAt.isBefore(startsAt)) {
                errors.add(new ValidationError("ends_at", "End date must be after start date"));
            }
        }
    }
} 