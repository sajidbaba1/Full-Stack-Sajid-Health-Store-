package com.healthstore.service.impl;

import com.healthstore.model.ProductVariant;
import com.healthstore.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Console-based implementation of the NotificationService.
 * Logs notifications to the console. In a production environment, this would be
 * replaced with email, SMS, or push notification implementations.
 */
@Service
public class ConsoleNotificationService implements NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(ConsoleNotificationService.class);

    @Override
    public void sendLowStockAlert(ProductVariant variant, String level) {
        String message = String.format(
            "[%s STOCK ALERT] Product: %s (Variant ID: %d) is running low on stock. Current stock: %d",
            level,
            variant.getProduct() != null ? variant.getProduct().getName() : "Unknown Product",
            variant.getId(),
            variant.getStockQuantity()
        );
        
        if ("CRITICAL".equalsIgnoreCase(level)) {
            logger.error(message);
        } else {
            logger.warn(message);
        }
    }

    @Override
    public void sendOutOfStockNotification(ProductVariant variant) {
        String message = String.format(
            "[OUT OF STOCK] Product: %s (Variant ID: %d) is now out of stock.",
            variant.getProduct() != null ? variant.getProduct().getName() : "Unknown Product",
            variant.getId()
        );
        logger.error(message);
    }

    @Override
    public void sendBackInStockNotification(ProductVariant variant) {
        String message = String.format(
            "[BACK IN STOCK] Product: %s (Variant ID: %d) is now back in stock. Current stock: %d",
            variant.getProduct() != null ? variant.getProduct().getName() : "Unknown Product",
            variant.getId(),
            variant.getStockQuantity()
        );
        logger.info(message);
    }
}
