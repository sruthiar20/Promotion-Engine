package com.promotion.engine.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

/**
 * DTO for returning error responses.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private String type;
    private String message;
    private List<ErrorDetail> details;

    /**
     * Default constructor.
     */
    public ErrorResponse() {
    }

    /**
     * Creates a new error response.
     *
     * @param type    Error type
     * @param message Error message
     * @param details Error details
     */
    public ErrorResponse(String type, String message, List<ErrorDetail> details) {
        this.type = type;
        this.message = message;
        this.details = details;
    }

    // Getters and setters

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<ErrorDetail> getDetails() {
        return details;
    }

    public void setDetails(List<ErrorDetail> details) {
        this.details = details;
    }

    /**
     * Represents a detailed error.
     */
    public static class ErrorDetail {
        private String field;
        private String message;

        /**
         * Default constructor.
         */
        public ErrorDetail() {
        }

        /**
         * Creates a new error detail.
         *
         * @param field   The field with the error
         * @param message The error message
         */
        public ErrorDetail(String field, String message) {
            this.field = field;
            this.message = message;
        }

        // Getters and setters

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
} 