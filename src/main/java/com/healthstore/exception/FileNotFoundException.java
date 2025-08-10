package com.healthstore.exception;

/**
 * Exception thrown when a file cannot be found in the storage.
 */
public class FileNotFoundException extends RuntimeException {

    public FileNotFoundException(String message) {
        super(message);
    }

    public FileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
