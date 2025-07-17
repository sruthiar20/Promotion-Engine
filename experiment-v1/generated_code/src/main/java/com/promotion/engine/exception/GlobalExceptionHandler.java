package com.promotion.engine.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.promotion.engine.dto.response.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebInputException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Global exception handler for the application.
 * Handles all exceptions and returns appropriate error responses.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final String VALIDATION_ERROR_TYPE = "validation_error";
    private static final String VALIDATION_ERROR_MESSAGE = "Invalid field values in promotion request";

    /**
     * Handles validation exceptions.
     *
     * @param ex The validation exception
     * @return The error response
     */
    @ExceptionHandler(WebExchangeBindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(WebExchangeBindException ex) {
        logger.error("Validation error: {}", ex.getMessage());
        
        List<ErrorResponse.ErrorDetail> details = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(error -> {
                    String fieldName = error instanceof FieldError 
                        ? ((FieldError) error).getField() 
                        : error.getObjectName();
                    
                    // Map Java field names to JSON property names
                    fieldName = mapToJsonPropertyName(fieldName);
                    
                    String errorMessage = error.getDefaultMessage();
                    return new ErrorResponse.ErrorDetail(fieldName, errorMessage);
                })
                .collect(Collectors.toList());
        
        return new ErrorResponse(VALIDATION_ERROR_TYPE, VALIDATION_ERROR_MESSAGE, details);
    }
    
    /**
     * Maps Java field names to JSON property names.
     *
     * @param fieldName The Java field name
     * @return The JSON property name
     */
    private String mapToJsonPropertyName(String fieldName) {
        switch (fieldName) {
            case "startsAt":
                return "starts_at";
            case "endsAt":
                return "ends_at";
            case "productId":
                return "product_id";
            case "categoryId":
                return "category_id";
            default:
                return fieldName;
        }
    }
    
    /**
     * Handles Jackson InvalidFormatException for date fields.
     *
     * @param ex The invalid format exception
     * @return The error response
     */
    @ExceptionHandler(InvalidFormatException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidFormatException(InvalidFormatException ex) {
        logger.error("Invalid format error: {}", ex.getMessage());
        
        List<ErrorResponse.ErrorDetail> details = new ArrayList<>();
        
        // Check if this is a date format error for Instant fields
        if (ex.getTargetType() != null && Instant.class.isAssignableFrom(ex.getTargetType())) {
            // Get the field name from the path
            String fieldName = getFieldNameFromPath(ex.getPath());
            
            if (fieldName != null) {
                details.add(new ErrorResponse.ErrorDetail(fieldName, "Invalid date format"));
            } else {
                // Fallback: assume both date fields are invalid
                details.add(new ErrorResponse.ErrorDetail("starts_at", "Invalid date format"));
                details.add(new ErrorResponse.ErrorDetail("ends_at", "Invalid date format"));
            }
        } else {
            // Generic invalid format error
            details.add(new ErrorResponse.ErrorDetail("request", "Invalid input data format"));
        }
        
        return new ErrorResponse(VALIDATION_ERROR_TYPE, VALIDATION_ERROR_MESSAGE, details);
    }
    
    /**
     * Handles JSON parsing errors including date format issues.
     *
     * @param ex The JSON processing exception
     * @return The error response
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleJsonParsingException(HttpMessageNotReadableException ex) {
        logger.error("JSON parsing error: {}", ex.getMessage());
        
        List<ErrorResponse.ErrorDetail> details = new ArrayList<>();
        
        // Check if this is caused by an InvalidFormatException
        Throwable cause = ex.getCause();
        boolean isDateFormatError = false;
        
        if (cause instanceof InvalidFormatException) {
            InvalidFormatException formatEx = (InvalidFormatException) cause;
            
            // Check if this is a date format error for Instant fields
            if (formatEx.getTargetType() != null && Instant.class.isAssignableFrom(formatEx.getTargetType())) {
                String fieldName = getFieldNameFromPath(formatEx.getPath());
                
                if (fieldName != null) {
                    details.add(new ErrorResponse.ErrorDetail(fieldName, "Invalid date format"));
                } else {
                    // Fallback: assume both date fields are invalid
                    details.add(new ErrorResponse.ErrorDetail("starts_at", "Invalid date format"));
                    details.add(new ErrorResponse.ErrorDetail("ends_at", "Invalid date format"));
                }
                isDateFormatError = true;
            }
        }
        
        if (!isDateFormatError) {
            // Check message for date-related errors
            String message = ex.getMessage();
            
            // Also check the cause chain for date format exceptions
            Throwable currentCause = cause;
            while (currentCause != null && !isDateFormatError) {
                String causeMessage = currentCause.getMessage();
                if (causeMessage != null && 
                    (causeMessage.contains("java.time.format.DateTimeParseException") ||
                     causeMessage.contains("Cannot deserialize value") ||
                     causeMessage.contains("Instant") ||
                     currentCause instanceof InvalidFormatException)) {
                    isDateFormatError = true;
                }
                currentCause = currentCause.getCause();
            }
            
            if (message != null && 
                (message.contains("java.time.format.DateTimeParseException") || 
                 message.contains("Cannot deserialize value") || 
                 message.contains("starts_at") || message.contains("ends_at") || message.contains("Instant") ||
                 message.contains("Failed to read HTTP message"))) {
                isDateFormatError = true;
            }
            
            if (isDateFormatError) {
                // This is likely a date format error - return specific field errors
                details.add(new ErrorResponse.ErrorDetail("starts_at", "Invalid date format"));
                details.add(new ErrorResponse.ErrorDetail("ends_at", "Invalid date format"));
            } else {
                // Generic JSON parsing error
                details.add(new ErrorResponse.ErrorDetail("request", "Invalid input data format"));
            }
        }
        
        return new ErrorResponse(VALIDATION_ERROR_TYPE, VALIDATION_ERROR_MESSAGE, details);
    }
    
    /**
     * Extracts the field name from Jackson's JsonMappingException path.
     *
     * @param path The path from the exception
     * @return The field name or null if not found
     */
    private String getFieldNameFromPath(List<JsonMappingException.Reference> path) {
        if (path != null && !path.isEmpty()) {
            for (JsonMappingException.Reference ref : path) {
                if (ref.getFieldName() != null) {
                    return mapToJsonPropertyName(ref.getFieldName());
                }
            }
        }
        return null;
    }
    
    /**
     * Handles input validation exceptions.
     *
     * @param ex The input validation exception
     * @return The error response
     */
    @ExceptionHandler(ServerWebInputException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleServerWebInputException(ServerWebInputException ex) {
        logger.error("Input validation error: {}", ex.getMessage());
        
        List<ErrorResponse.ErrorDetail> details = new ArrayList<>();
        
        // Check if the exception is related to date format parsing
        String message = ex.getMessage();
        Throwable cause = ex.getCause();
        
        // Check if this is a date format error by examining the message and cause
        boolean isDateFormatError = false;
        
        if (message != null && 
            (message.contains("java.time.format.DateTimeParseException") || 
             message.contains("Cannot deserialize value") || 
             message.contains("starts_at") || message.contains("ends_at") || message.contains("Instant"))) {
            isDateFormatError = true;
        }
        
        // Also check if message is "Failed to read HTTP message" - this often indicates JSON parsing issues
        // In our case, this is likely a date format error since we're expecting Instant fields
        if (message != null && message.contains("Failed to read HTTP message")) {
            // For promotion search requests, if we get a generic "Failed to read HTTP message", 
            // it's likely due to invalid date format in starts_at or ends_at fields
            isDateFormatError = true;
        }
        
        // Check the cause chain for date format exceptions
        Throwable currentCause = cause;
        while (currentCause != null && !isDateFormatError) {
            String causeMessage = currentCause.getMessage();
            if (causeMessage != null && 
                (causeMessage.contains("java.time.format.DateTimeParseException") ||
                 causeMessage.contains("Cannot deserialize value") ||
                 causeMessage.contains("Instant") ||
                 currentCause instanceof InvalidFormatException)) {
                isDateFormatError = true;
            }
            currentCause = currentCause.getCause();
        }
        
        if (isDateFormatError) {
            // This is likely a date format error - return specific field errors
            details.add(new ErrorResponse.ErrorDetail("starts_at", "Invalid date format"));
            details.add(new ErrorResponse.ErrorDetail("ends_at", "Invalid date format"));
        } else {
            // Generic input validation error
            details.add(new ErrorResponse.ErrorDetail(
                ex.getMethodParameter() != null ? ex.getMethodParameter().getParameterName() : "request", 
                "Invalid input data format"
            ));
        }
        
        return new ErrorResponse(VALIDATION_ERROR_TYPE, VALIDATION_ERROR_MESSAGE, details);
    }

    /**
     * Handles date format exceptions.
     *
     * @param ex The date format exception
     * @return The error response
     */
    @ExceptionHandler(DateFormatException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleDateFormatException(DateFormatException ex) {
        logger.error("Date format error: {}", ex.getMessage());
        
        List<ErrorResponse.ErrorDetail> details = new ArrayList<>();
        details.add(new ErrorResponse.ErrorDetail(ex.getField(), ex.getMessage()));
        
        return new ErrorResponse(VALIDATION_ERROR_TYPE, VALIDATION_ERROR_MESSAGE, details);
    }
    
    /**
     * Handles promotion not found exceptions.
     *
     * @param ex The promotion not found exception
     * @return The error response
     */
    @ExceptionHandler(PromotionNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handlePromotionNotFoundException(PromotionNotFoundException ex) {
        logger.error("Promotion not found: {}", ex.getMessage());
        
        List<ErrorResponse.ErrorDetail> details = new ArrayList<>();
        details.add(new ErrorResponse.ErrorDetail(ex.getField(), "No active promotion found for " + ex.getField() + ": " + ex.getValue()));
        
        return new ErrorResponse("not_found_error", "Promotion not found", details);
    }
    
    /**
     * Handles all other exceptions.
     *
     * @param ex The exception
     * @return The error response
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleException(Exception ex) {
        logger.error("Unexpected error: ", ex);
        
        List<ErrorResponse.ErrorDetail> details = new ArrayList<>();
        
        // Check if this is a date format error that wasn't caught by other handlers
        String message = ex.getMessage();
        if (message != null && 
            (message.contains("java.time.format.DateTimeParseException") || 
             message.contains("Cannot deserialize value") || 
             message.contains("Failed to read HTTP message") && 
             (message.contains("starts_at") || message.contains("ends_at") || message.contains("Instant")))) {
            
            // This is a date format error - return 400 with validation error
            details.add(new ErrorResponse.ErrorDetail("starts_at", "Invalid date format"));
            details.add(new ErrorResponse.ErrorDetail("ends_at", "Invalid date format"));
            
            return new ErrorResponse(VALIDATION_ERROR_TYPE, VALIDATION_ERROR_MESSAGE, details);
        }
        
        // Regular server error
        details.add(new ErrorResponse.ErrorDetail("general", "An unexpected error occurred"));
        
        return new ErrorResponse("server_error", "Internal server error", details);
    }
} 