package com.healthstore.repository;

import com.healthstore.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Review entity.
 * Provides methods to access and manage review data.
 */
@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    /**
     * Find all reviews for a specific product.
     * 
     * @param productId The ID of the product.
     * @return A list of reviews for the specified product.
     */
    List<Review> findByProductId(Long productId);
    
    /**
     * Find all reviews by a specific user.
     * 
     * @param userId The ID of the user.
     * @return A list of reviews by the specified user.
     */
    List<Review> findByUserId(Long userId);
    
    /**
     * Check if a user has already reviewed a product.
     * 
     * @param userId The ID of the user.
     * @param productId The ID of the product.
     * @return true if the user has reviewed the product, false otherwise.
     */
    boolean existsByUserIdAndProductId(Long userId, Long productId);
    
    /**
     * Calculate the average rating for a product.
     * 
     * @param productId The ID of the product.
     * @return The average rating as a Double, or null if no ratings exist.
     */
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.id = :productId")
    Double findAverageRatingByProductId(@Param("productId") Long productId);
    
    /**
     * Count the number of reviews for a product.
     * 
     * @param productId The ID of the product.
     * @return The count of reviews for the specified product.
     */
    long countByProductId(Long productId);
}
