package com.promotion.engine.validation;

import com.promotion.engine.dto.request.PromotionSearchRequest;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ConstraintValidatorContext.ConstraintViolationBuilder;
import jakarta.validation.ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

/**
 * Tests for the promotion validator.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class PromotionValidatorTest {

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private ConstraintViolationBuilder violationBuilder;

    @Mock
    private NodeBuilderCustomizableContext nodeBuilderContext;

    private PromotionValidator validator;
    private PromotionSearchRequest request;

    @BeforeEach
    void setUp() {
        validator = new PromotionValidator();
        request = new PromotionSearchRequest();
        
        // Setup lenient mocks for the constraint validation context
        lenient().doNothing().when(context).disableDefaultConstraintViolation();
        lenient().when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(violationBuilder);
        lenient().when(violationBuilder.addPropertyNode(anyString())).thenReturn(nodeBuilderContext);
        lenient().when(nodeBuilderContext.addConstraintViolation()).thenReturn(context);
    }

    @Test
    void testIsValid_ValidRequestWithProductId() {
        // Arrange
        request.setStatus("active");
        request.setProductId("SKU-PRO-001");
        request.setStartsAt(Instant.now().plusSeconds(86400)); // tomorrow
        request.setEndsAt(Instant.now().plusSeconds(172800)); // day after tomorrow

        // Act
        boolean result = validator.isValid(request, context);

        // Assert
        assertTrue(result, "Valid request with product ID should pass validation");
    }

    @Test
    void testIsValid_ValidRequestWithCategoryId() {
        // Arrange
        request.setStatus("active");
        request.setCategoryId("SKU-CAT-001");
        request.setStartsAt(Instant.now().plusSeconds(86400)); // tomorrow
        request.setEndsAt(Instant.now().plusSeconds(172800)); // day after tomorrow

        // Act
        boolean result = validator.isValid(request, context);

        // Assert
        assertTrue(result, "Valid request with category ID should pass validation");
    }

    @Test
    void testIsValid_ValidRequestWithPastDate() {
        // Arrange - past dates are now allowed for search API
        request.setStatus("active");
        request.setProductId("SKU-PRO-001");
        request.setStartsAt(Instant.now().minusSeconds(86400)); // yesterday (allowed for search)
        request.setEndsAt(Instant.now().plusSeconds(86400)); // tomorrow

        // Act
        boolean result = validator.isValid(request, context);

        // Assert
        assertTrue(result, "Request with past start date should pass validation for search API");
    }

    @Test
    void testIsValid_MissingStatus() {
        // Arrange
        request.setProductId("SKU-PRO-001");
        request.setStartsAt(Instant.now().plusSeconds(86400));
        request.setEndsAt(Instant.now().plusSeconds(172800));

        // Act
        boolean result = validator.isValid(request, context);

        // Assert
        assertFalse(result, "Request with missing status should fail validation");
    }

    @Test
    void testIsValid_InvalidStatus() {
        // Arrange
        request.setStatus("inactive"); // not "active"
        request.setProductId("SKU-PRO-001");
        request.setStartsAt(Instant.now().plusSeconds(86400));
        request.setEndsAt(Instant.now().plusSeconds(172800));

        // Act
        boolean result = validator.isValid(request, context);

        // Assert
        assertFalse(result, "Request with invalid status should fail validation");
    }

    @Test
    void testIsValid_MutuallyExclusiveFields() {
        // Arrange - both product-id and category-id
        request.setStatus("active");
        request.setProductId("SKU-PRO-001");
        request.setCategoryId("SKU-CAT-001");
        request.setStartsAt(Instant.now().plusSeconds(86400));
        request.setEndsAt(Instant.now().plusSeconds(172800));

        // Act
        boolean result = validator.isValid(request, context);

        // Assert
        assertFalse(result, "Request with both product-id and category-id should fail validation");
    }

    @Test
    void testIsValid_MissingProductAndCategoryId() {
        // Arrange - neither product-id nor category-id
        request.setStatus("active");
        request.setStartsAt(Instant.now().plusSeconds(86400));
        request.setEndsAt(Instant.now().plusSeconds(172800));

        // Act
        boolean result = validator.isValid(request, context);

        // Assert
        assertFalse(result, "Request without product-id and category-id should fail validation");
    }

    @Test
    void testIsValid_EndDateBeforeStartDate() {
        // Arrange
        request.setStatus("active");
        request.setProductId("SKU-PRO-001");
        request.setStartsAt(Instant.now().plusSeconds(172800)); // day after tomorrow
        request.setEndsAt(Instant.now().plusSeconds(86400)); // tomorrow (before start date)

        // Act
        boolean result = validator.isValid(request, context);

        // Assert
        assertFalse(result, "Request with end date before start date should fail validation");
    }

    @Test
    void testIsValid_EmptyProductId() {
        // Arrange
        request.setStatus("active");
        request.setProductId(""); // empty string
        request.setStartsAt(Instant.now().plusSeconds(86400));
        request.setEndsAt(Instant.now().plusSeconds(172800));

        // Act
        boolean result = validator.isValid(request, context);

        // Assert
        assertFalse(result, "Request with empty product ID should fail validation");
    }

    @Test
    void testIsValid_EmptyCategoryId() {
        // Arrange
        request.setStatus("active");
        request.setCategoryId("   "); // whitespace only
        request.setStartsAt(Instant.now().plusSeconds(86400));
        request.setEndsAt(Instant.now().plusSeconds(172800));

        // Act
        boolean result = validator.isValid(request, context);

        // Assert
        assertFalse(result, "Request with empty category ID should fail validation");
    }
} 