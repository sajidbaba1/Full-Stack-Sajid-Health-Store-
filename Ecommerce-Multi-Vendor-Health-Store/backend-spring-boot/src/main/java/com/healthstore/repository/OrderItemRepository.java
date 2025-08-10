package com.healthstore.repository;

import com.healthstore.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for OrderItem entities.
 * Provides data access methods for order items.
 */
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    /**
     * Finds all order items for a specific order.
     * @param orderId The ID of the order.
     * @return A list of order items for the specified order.
     */
    List<OrderItem> findByOrderId(Long orderId);
    
    /**
     * Finds all order items for a specific product.
     * @param productId The ID of the product.
     * @return A list of order items containing the specified product.
     */
    List<OrderItem> findByProductId(Long productId);
}
