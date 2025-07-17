package com.promotion.engine.exception;

/**
 * Exception for date format validation errors.
 */
public class DateFormatException extends RuntimeException {
    private final String field;

    /**
     * Creates a new DateFormatException.
     *
     * @param field   The field with the error
     * @param message The error message
     */
    public DateFormatException(String field, String message) {
        super(message);
        this.field = field;
    }

    /**
     * Gets the field name.
     *
     * @return The field name
     */
    public String getField() {
        return field;
    }
} 