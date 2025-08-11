package com.healthstore.repository;

import com.healthstore.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User entities.
 * It extends JpaRepository to inherit methods for standard CRUD operations.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their email address.
     * @param email The email address of the user.
     * @return An Optional containing the user if found, or empty if not found.
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Checks if a user with the given email exists.
     * @param email The email address to check.
     * @return true if a user with the email exists, false otherwise.
     */
    boolean existsByEmail(String email);
    
    /**
     * Finds users who have placed orders within a specific date range.
     * @param startDate The start date of the period.
     * @param endDate The end date of the period.
     * @return A list of users who have placed orders in the specified period.
     */
    @Query("SELECT DISTINCT o.user FROM Order o WHERE o.orderDate BETWEEN :startDate AND :endDate")
    List<User> findActiveCustomers(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
    
    /**
     * Counts the number of new users registered within a specific date range.
     * @param startDate The start date of the period.
     * @param endDate The end date of the period.
     * @return The count of new users.
     */
    Long countByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Finds users who have spent the most money within a specific date range.
     * @param startDate The start date of the period.
     * @param endDate The end date of the period.
     * @param limit The maximum number of users to return.
     * @return A list of Object arrays containing the user and their total spending.
     */
    @Query(value = "SELECT o.user, SUM(o.totalAmount) as totalSpent " +
           "FROM Order o " +
           "WHERE o.orderDate BETWEEN :startDate AND :endDate " +
           "AND o.status = 'COMPLETED' " +
           "GROUP BY o.user " +
           "ORDER BY totalSpent DESC " +
           "LIMIT :limit", nativeQuery = true)
    List<Object[]> findTopSpendingUsers(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("limit") int limit
    );
    
    /**
     * Counts the number of users who have logged in after the specified date.
     * @param date The date to compare against.
     * @return The count of users who have logged in after the specified date.
     */
    long countByLastLoginDateAfter(LocalDateTime date);
    
    /**
     * Finds the most recently created users, ordered by creation date in descending order.
     * @return A list of users, ordered by creation date (newest first).
     */
    List<User> findTop10ByOrderByCreatedAtDesc();
    
    /**
     * Finds users who have logged in within a specific date range.
     * @param startDate The start date of the period.
     * @param endDate The end date of the period.
     * @return A list of users who have logged in during the specified period.
     */
    List<User> findByLastLoginDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Counts the number of users who have logged in within a specific date range.
     * @param startDate The start date of the period.
     * @param endDate The end date of the period.
     * @return The count of users who have logged in during the specified period.
     */
    long countByLastLoginDateBetween(LocalDateTime startDate, LocalDateTime endDate);
}
