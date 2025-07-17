package com.promotion.engine.exception;

/**
 * Represents a validation error with field name and message.
 */
public class ValidationError {
    private final String field;
    private final String message;

    /**
     * Creates a new validation error.
     *
     * @param field   The field with the error
     * @param message The error message
     */
    public ValidationError(String field, String message) {
        this.field = field;
        this.message = message;
    }

    /**
     * Gets the field name.
     *
     * @return The field name
     */
    public String getField() {
        return field;
    }

    /**
     * Gets the error message.
     *
     * @return The error message
     */
    public String getMessage() {
        return message;
    }
} 