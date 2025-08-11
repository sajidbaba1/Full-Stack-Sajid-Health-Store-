package com.healthstore.repository;

import com.healthstore.model.Product;
import com.healthstore.model.Review;
import com.healthstore.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    Page<Review> findByProductId(Long productId, Pageable pageable);
    
    Page<Review> findByUserId(Long userId, Pageable pageable);
    
    Optional<Review> findByProductAndUser(Product product, User user);
    
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.id = :productId")
    Double findAverageRatingByProductId(@Param("productId") Long productId);
    
    @Query("SELECT COUNT(r) FROM Review r WHERE r.product.id = :productId")
    long countByProductId(@Param("productId") Long productId);
    
    @Query("SELECT r FROM Review r WHERE " +
           "r.product.id = :productId AND " +
           "(:minRating IS NULL OR r.rating >= :minRating) AND " +
           "(:maxRating IS NULL OR r.rating <= :maxRating)")
    Page<Review> findProductReviewsWithFilters(
            @Param("productId") Long productId,
            @Param("minRating") Integer minRating,
            @Param("maxRating") Integer maxRating,
            Pageable pageable);
            
    @Query("SELECT r.rating, COUNT(r) FROM Review r WHERE r.product.id = :productId GROUP BY r.rating")
    List<Object[]> countReviewsByRating(@Param("productId") Long productId);
    
    boolean existsByProductIdAndUserId(Long productId, Long userId);
    
    /**
     * Find reviews by product ID (without pagination)
     */
    List<Review> findByProductId(Long productId);
    
    /**
     * Find a review by product ID and user ID
     */
    Optional<Review> findByProductIdAndUserId(Long productId, Long userId);
    
    /**
     * Delete a review by product ID and user ID
     */
    int deleteByProductIdAndUserId(Long productId, Long userId);
}
