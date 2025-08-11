package com.healthstore.repository;

import com.healthstore.model.Order;
import com.healthstore.model.User;
import com.healthstore.model.Order.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    Page<Order> findByUserId(Long userId, Pageable pageable);
    
    List<Order> findByOrderDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    List<Order> findByUserAndOrderDateBetween(User user, LocalDateTime startDate, LocalDateTime endDate);
    
    Long countByOrderDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Counts orders for a specific user within a date range.
     * @param user The user whose orders to count.
     * @param startDate The start date of the period.
     * @param endDate The end date of the period.
     * @return The count of orders for the user in the specified period.
     */
    Long countByUserAndOrderDateBetween(User user, LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.orderDate BETWEEN :startDate AND :endDate")
    Optional<BigDecimal> calculateTotalRevenue(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
    
    @Query("SELECT o FROM Order o JOIN FETCH o.user u WHERE o.orderDate BETWEEN :startDate AND :endDate")
    List<Order> findWithUserByOrderDateBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
    
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);
    
    @Query("SELECT o FROM Order o WHERE " +
           "o.user = :user AND " +
           "(:status IS NULL OR o.status = :status) AND " +
           "(cast(:startDate as date) IS NULL OR o.orderDate >= :startDate) AND " +
           "(cast(:endDate as date) IS NULL OR o.orderDate <= :endDate)")
    Page<Order> findUserOrdersWithFilters(
            @Param("user") User user,
            @Param("status") OrderStatus status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);
            
    @Query("SELECT o FROM Order o WHERE " +
           "o.orderDate >= :startDate AND o.orderDate <= :endDate")
    List<Order> findOrdersBetweenDates(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
            
    long countByStatus(OrderStatus status);
    
    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.status = 'DELIVERED' AND o.orderDate >= :startDate AND o.orderDate <= :endDate")
    Double calculateRevenueBetweenDates(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
            
    /**
     * Finds an order by its associated Stripe payment intent ID.
     *
     * @param paymentIntentId The Stripe payment intent ID
     * @return An Optional containing the order if found, or empty otherwise
     */
    Optional<Order> findByPaymentIntentId(String paymentIntentId);
    
    /**
     * Finds an order by its associated Stripe payment ID (charge ID).
     *
     * @param paymentId The Stripe payment/charge ID
     * @return An Optional containing the order if found, or empty otherwise
     */
    Optional<Order> findByPaymentId(String paymentId);
}
