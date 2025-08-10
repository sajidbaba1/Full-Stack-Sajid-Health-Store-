package com.healthstore.repository;

import com.healthstore.model.Order;
import com.healthstore.model.OrderItem;
import com.healthstore.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for OrderItem entities.
 * Provides methods to interact with the database for OrderItem operations.
 */
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    
    /**
     * Find all order items for a specific order.
     * @param order The order to find items for.
     * @return A list of order items for the given order.
     */
    List<OrderItem> findByOrder(Order order);
    
    /**
     * Find all order items for a specific product.
     * @param product The product to find order items for.
     * @return A list of order items containing the given product.
     */
    List<OrderItem> findByProduct(Product product);
    
    /**
     * Check if a product exists in any order item.
     * @param product The product to check.
     * @return true if the product exists in any order item, false otherwise.
     */
    boolean existsByProduct(Product product);
}
