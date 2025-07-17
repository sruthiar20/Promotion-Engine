package com.promotion.engine.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom validation annotation for promotion request validation.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PromotionValidator.class)
public @interface ValidPromotion {
    String message() default "Invalid promotion request";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
} 