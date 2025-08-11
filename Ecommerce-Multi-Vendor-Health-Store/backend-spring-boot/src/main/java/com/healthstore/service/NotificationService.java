package com.healthstore.service;

import com.healthstore.model.ProductVariant;

/**
 * Service for sending various types of notifications.
 */
public interface NotificationService {

    /**
     * Sends a low stock alert for a product variant.
     *
     * @param variant The product variant with low stock
     * @param level The severity level (e.g., "LOW", "CRITICAL")
     */
    void sendLowStockAlert(ProductVariant variant, String level);
    
    /**
     * Sends an out-of-stock notification for a product variant.
     *
     * @param variant The product variant that is out of stock
     */
    void sendOutOfStockNotification(ProductVariant variant);
    
    /**
     * Sends a back-in-stock notification for a product variant.
     *
     * @param variant The product variant that is back in stock
     */
    void sendBackInStockNotification(ProductVariant variant);
}
