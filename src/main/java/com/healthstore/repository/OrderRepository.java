package com.healthstore.repository;

import com.healthstore.model.Order;
import com.healthstore.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Order entities.
 * Provides methods to interact with the database for Order operations.
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    /**
     * Find all orders for a specific user, ordered by creation date in descending order.
     * @param user The user whose orders to find.
     * @return A list of orders for the given user.
     */
    List<Order> findByUserOrderByCreatedAtDesc(User user);
    
    /**
     * Find all orders with a specific status, ordered by creation date in descending order.
     * @param status The status to filter orders by.
     * @return A list of orders with the given status.
     */
    List<Order> findByStatusOrderByCreatedAtDesc(Order.OrderStatus status);
    
    /**
     * Find all orders for a specific user with a specific status, ordered by creation date.
     * @param user The user whose orders to find.
     * @param status The status to filter orders by.
     * @return A list of matching orders.
     */
    List<Order> findByUserAndStatusOrderByCreatedAtDesc(User user, Order.OrderStatus status);
}
