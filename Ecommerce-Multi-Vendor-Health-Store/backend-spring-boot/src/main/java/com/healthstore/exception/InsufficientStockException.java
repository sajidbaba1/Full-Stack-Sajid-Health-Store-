package com.healthstore.exception;

/**
 * Exception thrown when there is not enough stock available for a product variant.
 */
public class InsufficientStockException extends RuntimeException {

    /**
     * Constructs a new InsufficientStockException with the specified detail message.
     *
     * @param message The detail message
     */
    public InsufficientStockException(String message) {
        super(message);
    }

    /**
     * Constructs a new InsufficientStockException with the specified detail message and cause.
     *
     * @param message The detail message
     * @param cause The cause of the exception
     */
    public InsufficientStockException(String message, Throwable cause) {
        super(message, cause);
    }
}
