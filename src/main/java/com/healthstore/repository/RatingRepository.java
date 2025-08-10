package com.healthstore.repository;

import com.healthstore.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Rating entity.
 * Provides methods to access and manage rating data.
 */
@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    
    /**
     * Find a rating by user ID and product ID.
     * 
     * @param userId The ID of the user.
     * @param productId The ID of the product.
     * @return An Optional containing the rating if found, or empty otherwise.
     */
    Optional<Rating> findByUserIdAndProductId(Long userId, Long productId);
    
    /**
     * Find all ratings for a specific product.
     * 
     * @param productId The ID of the product.
     * @return A list of ratings for the specified product.
     */
    List<Rating> findByProductId(Long productId);
    
    /**
     * Calculate the average rating for a product.
     * 
     * @param productId The ID of the product.
     * @return The average rating as a Double, or null if no ratings exist.
     */
    @Query("SELECT AVG(r.value) FROM Rating r WHERE r.product.id = :productId")
    Double findAverageRatingByProductId(@Param("productId") Long productId);
    
    /**
     * Count the number of ratings for a product.
     * 
     * @param productId The ID of the product.
     * @return The count of ratings for the specified product.
     */
    long countByProductId(Long productId);
    
    /**
     * Check if a user has rated a product.
     * 
     * @param userId The ID of the user.
     * @param productId The ID of the product.
     * @return true if the user has rated the product, false otherwise.
     */
    boolean existsByUserIdAndProductId(Long userId, Long productId);
}
