package com.healthstore.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the application.
 * Provides centralized exception handling across all @RequestMapping methods.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles RuntimeException and its subclasses.
     * @param ex The exception that was thrown.
     * @param request The web request during which the exception was thrown.
     * @return A ResponseEntity containing error details and HTTP status code.
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleRuntimeException(RuntimeException ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", new Date());
        body.put("message", ex.getMessage());
        body.put("path", request.getDescription(false).replace("uri=", ""));
        
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles all other exceptions that don't have specific handlers.
     * @param ex The exception that was thrown.
     * @param request The web request during which the exception was thrown.
     * @return A ResponseEntity containing error details and HTTP status code.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGlobalException(Exception ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", new Date());
        body.put("message", "An unexpected error occurred: " + ex.getMessage());
        body.put("path", request.getDescription(false).replace("uri=", ""));
        
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
