package com.healthstore.service;

import com.healthstore.exception.InsufficientStockException;
import com.healthstore.exception.ResourceNotFoundException;
import com.healthstore.model.ProductVariant;
import com.healthstore.repository.ProductVariantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Service for managing inventory operations including stock checks and updates.
 */
@Service
@Transactional
public class InventoryService {

    private static final int LOW_STOCK_THRESHOLD = 10; // Threshold for low stock alerts
    private static final int CRITICAL_STOCK_THRESHOLD = 3; // Threshold for critical stock alerts

    private final ProductVariantRepository productVariantRepository;
    private final NotificationService notificationService;

    @Autowired
    public InventoryService(ProductVariantRepository productVariantRepository,
                          NotificationService notificationService) {
        this.productVariantRepository = productVariantRepository;
        this.notificationService = notificationService;
    }

    /**
     * Checks if a product variant has sufficient stock.
     *
     * @param variantId The ID of the product variant
     * @param quantity The required quantity
     * @return true if sufficient stock is available, false otherwise
     */
    public boolean isInStock(Long variantId, int quantity) {
        ProductVariant variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new ResourceNotFoundException("Product variant not found with id: " + variantId));
        
        return variant.getStockQuantity() >= quantity;
    }

    /**
     * Reduces the stock level of a product variant.
     *
     * @param variantId The ID of the product variant
     * @param quantity The quantity to reduce
     * @throws InsufficientStockException if there is not enough stock available
     */
    public void reduceStock(Long variantId, int quantity) {
        ProductVariant variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new ResourceNotFoundException("Product variant not found with id: " + variantId));
        
        if (variant.getStockQuantity() < quantity) {
            throw new InsufficientStockException(
                String.format("Insufficient stock for variant %d. Requested: %d, Available: %d",
                    variantId, quantity, variant.getStockQuantity()));
        }
        
        variant.setStockQuantity(variant.getStockQuantity() - quantity);
        productVariantRepository.save(variant);
        
        // Check if stock is low after reduction
        checkAndNotifyLowStock(variant);
    }

    /**
     * Increases the stock level of a product variant.
     *
     * @param variantId The ID of the product variant
     * @param quantity The quantity to add
     */
    public void increaseStock(Long variantId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        
        ProductVariant variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new ResourceNotFoundException("Product variant not found with id: " + variantId));
        
        variant.setStockQuantity(variant.getStockQuantity() + quantity);
        productVariantRepository.save(variant);
    }

    /**
     * Updates the stock level of a product variant to a specific value.
     *
     * @param variantId The ID of the product variant
     * @param newStock The new stock quantity
     */
    public void updateStockLevel(Long variantId, int newStock) {
        if (newStock < 0) {
            throw new IllegalArgumentException("Stock level cannot be negative");
        }
        
        ProductVariant variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new ResourceNotFoundException("Product variant not found with id: " + variantId));
        
        variant.setStockQuantity(newStock);
        productVariantRepository.save(variant);
        
        // Check if stock is low after update
        checkAndNotifyLowStock(variant);
    }

    /**
     * Scheduled task to check for low stock items and send notifications.
     * Runs every hour.
     */
    @Scheduled(fixedRate = 3600000) // 1 hour in milliseconds
    public void checkLowStockItems() {
        List<ProductVariant> lowStockItems = productVariantRepository.findByStockQuantityLessThanEqual(LOW_STOCK_THRESHOLD);
        
        for (ProductVariant variant : lowStockItems) {
            checkAndNotifyLowStock(variant);
        }
    }

    /**
     * Checks if a variant is low or critical on stock and sends appropriate notifications.
     *
     * @param variant The product variant to check
     */
    private void checkAndNotifyLowStock(ProductVariant variant) {
        int currentStock = variant.getStockQuantity();
        
        if (currentStock <= 0) {
            // Out of stock
            notificationService.sendOutOfStockNotification(variant);
        } else if (currentStock <= CRITICAL_STOCK_THRESHOLD) {
            // Critical stock level
            notificationService.sendLowStockAlert(variant, "CRITICAL");
        } else if (currentStock <= LOW_STOCK_THRESHOLD) {
            // Low stock level
            notificationService.sendLowStockAlert(variant, "LOW");
        }
    }

    /**
     * Gets the current stock level for a product variant.
     *
     * @param variantId The ID of the product variant
     * @return The current stock quantity
     */
    public int getStockLevel(Long variantId) {
        return productVariantRepository.findById(variantId)
                .map(ProductVariant::getStockQuantity)
                .orElseThrow(() -> new ResourceNotFoundException("Product variant not found with id: " + variantId));
    }

    /**
     * Checks if multiple product variants are in stock.
     *
     * @param variantQuantities A map of variant IDs to quantities
     * @return true if all variants have sufficient stock, false otherwise
     */
    public boolean areAllInStock(Map<Long, Integer> variantQuantities) {
        return variantQuantities.entrySet().stream()
                .allMatch(entry -> isInStock(entry.getKey(), entry.getValue()));
    }

    /**
     * Reduces stock for multiple product variants in a single transaction.
     *
     * @param variantQuantities A map of variant IDs to quantities to reduce
     * @throws InsufficientStockException if any variant has insufficient stock
     */
    public void reduceStockBulk(Map<Long, Integer> variantQuantities) {
        // First, verify all items are in stock
        for (Map.Entry<Long, Integer> entry : variantQuantities.entrySet()) {
            if (!isInStock(entry.getKey(), entry.getValue())) {
                throw new InsufficientStockException("Insufficient stock for variant: " + entry.getKey());
            }
        }
        
        // Then process the stock reduction
        for (Map.Entry<Long, Integer> entry : variantQuantities.entrySet()) {
            reduceStock(entry.getKey(), entry.getValue());
        }
    }
}
