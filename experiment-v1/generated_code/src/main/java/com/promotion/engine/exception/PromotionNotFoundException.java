package com.promotion.engine.exception;

/**
 * Exception thrown when no promotion is found for the given search criteria.
 */
public class PromotionNotFoundException extends RuntimeException {
    
    private final String field;
    private final String value;
    
    public PromotionNotFoundException(String field, String value) {
        super("No promotion found for " + field + ": " + value);
        this.field = field;
        this.value = value;
    }
    
    public String getField() {
        return field;
    }
    
    public String getValue() {
        return value;
    }
} 