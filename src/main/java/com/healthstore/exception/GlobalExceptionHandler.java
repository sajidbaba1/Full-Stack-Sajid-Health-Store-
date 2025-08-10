package com.healthstore.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Global exception handler for the application.
 * Provides centralized exception handling across all @RequestMapping methods.
 */
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Handles validation errors for @Valid annotated objects.
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, 
            HttpHeaders headers, 
            HttpStatusCode status, 
            WebRequest request) {
        
        Map<String, String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fieldError -> fieldError.getDefaultMessage() != null ? 
                                fieldError.getDefaultMessage() : "Validation error"
                ));
        
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                "Validation failed",
                "One or more fields have invalid values",
                status.value(),
                errors
        );
        
        return handleExceptionInternal(ex, errorResponse, headers, status, request);
    }
    
    /**
     * Handles ConstraintViolationException which is thrown for validation errors on method parameters.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(violation -> {
            String field = violation.getPropertyPath().toString();
            errors.put(field, violation.getMessage());
        });
        
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                "Constraint violation",
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                errors
        );
        
        return handleExceptionInternal(ex, errorResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }
    
    /**
     * Handles ResourceNotFoundException.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFound(ResourceNotFoundException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                "Resource not found",
                ex.getMessage(),
                HttpStatus.NOT_FOUND.value()
        );
        
        return handleExceptionInternal(ex, errorResponse, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }
    
    /**
     * Handles EntityNotFoundException.
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handleEntityNotFound(EntityNotFoundException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                "Entity not found",
                ex.getMessage(),
                HttpStatus.NOT_FOUND.value()
        );
        
        return handleExceptionInternal(ex, errorResponse, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }
    
    /**
     * Handles AccessDeniedException for unauthorized access.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDenied(AccessDeniedException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                "Access denied",
                "You don't have permission to access this resource",
                HttpStatus.FORBIDDEN.value()
        );
        
        return handleExceptionInternal(ex, errorResponse, new HttpHeaders(), HttpStatus.FORBIDDEN, request);
    }
    
    /**
     * Handles AuthenticationException for failed authentication.
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Object> handleAuthentication(AuthenticationException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                "Authentication failed",
                ex.getMessage(),
                HttpStatus.UNAUTHORIZED.value()
        );
        
        return handleExceptionInternal(ex, errorResponse, new HttpHeaders(), HttpStatus.UNAUTHORIZED, request);
    }
    
    /**
     * Handles IllegalArgumentException for invalid method arguments.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgument(IllegalArgumentException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                "Invalid argument",
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value()
        );
        
        return handleExceptionInternal(ex, errorResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }
    
    /**
     * Handles all other exceptions that don't have specific handlers.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                "Internal server error",
                "An unexpected error occurred: " + ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.value()
        );
        
        return handleExceptionInternal(
                ex, 
                errorResponse, 
                new HttpHeaders(), 
                HttpStatus.INTERNAL_SERVER_ERROR, 
                request
        );
    }
    
    /**
     * Custom error response class for standardized error messages.
     */
    public static class ErrorResponse {
        private LocalDateTime timestamp;
        private String error;
        private String message;
        private int status;
        private Map<String, String> details;
        
        public ErrorResponse(LocalDateTime timestamp, String error, String message, int status) {
            this.timestamp = timestamp;
            this.error = error;
            this.message = message;
            this.status = status;
            this.details = new HashMap<>();
        }
        
        public ErrorResponse(LocalDateTime timestamp, String error, String message, int status, Map<String, String> details) {
            this.timestamp = timestamp;
            this.error = error;
            this.message = message;
            this.status = status;
            this.details = details != null ? details : new HashMap<>();
        }
        
        // Getters and setters
        public LocalDateTime getTimestamp() {
            return timestamp;
        }
        
        public void setTimestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
        }
        
        public String getError() {
            return error;
        }
        
        public void setError(String error) {
            this.error = error;
        }
        
        public String getMessage() {
            return message;
        }
        
        public void setMessage(String message) {
            this.message = message;
        }
        
        public int getStatus() {
            return status;
        }
        
        public void setStatus(int status) {
            this.status = status;
        }
        
        public Map<String, String> getDetails() {
            return details;
        }
        
        public void setDetails(Map<String, String> details) {
            this.details = details;
        }
    }
}
