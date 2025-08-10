package com.healthstore.exception;

/**
 * Exception thrown when there's an issue with password validation or update.
 */
public class InvalidPasswordException extends RuntimeException {
    
    public InvalidPasswordException() {
        super();
    }
    
    public InvalidPasswordException(String message) {
        super(message);
    }
    
    public InvalidPasswordException(String message, Throwable cause) {
        super(message, cause);
    }
}
