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

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    Page<Order> findByUserId(Long userId, Pageable pageable);
    
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
}
