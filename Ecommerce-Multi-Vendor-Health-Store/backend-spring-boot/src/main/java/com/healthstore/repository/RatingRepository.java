package com.healthstore.repository;

import com.healthstore.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing Rating entities.
 * Provides methods to interact with the ratings table in the database.
 */
@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    /**
     * Finds a rating by product ID and user ID.
     * @param productId The ID of the product.
     * @param userId The ID of the user.
     * @return An Optional containing the rating if found, or empty otherwise.
     */
    Optional<Rating> findByProductIdAndUserId(Long productId, Long userId);
}
