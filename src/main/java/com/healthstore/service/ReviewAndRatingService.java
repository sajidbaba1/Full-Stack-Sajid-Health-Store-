package com.healthstore.service;

import com.healthstore.exception.ResourceNotFoundException;
import com.healthstore.model.Product;
import com.healthstore.model.Review;
import com.healthstore.model.Rating;
import com.healthstore.model.User;
import com.healthstore.repository.ReviewRepository;
import com.healthstore.repository.RatingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service class for handling business logic related to product reviews and ratings.
 */
@Service
@Transactional
public class ReviewAndRatingService {

    private final ReviewRepository reviewRepository;
    private final RatingRepository ratingRepository;
    private final ProductService productService;
    private final UserService userService;

    @Autowired
    public ReviewAndRatingService(ReviewRepository reviewRepository, 
                                 RatingRepository ratingRepository,
                                 ProductService productService,
                                 UserService userService) {
        this.reviewRepository = reviewRepository;
        this.ratingRepository = ratingRepository;
        this.productService = productService;
        this.userService = userService;
    }

    // Review Methods
    
    /**
     * Create a new review for a product.
     * 
     * @param productId The ID of the product being reviewed.
     * @param userId The ID of the user creating the review.
     * @param comment The review comment.
     * @param rating The rating value (1-5).
     * @return The created review.
     * @throws ResourceNotFoundException If the product or user is not found.
     * @throws IllegalArgumentException If the user has already reviewed the product.
     */
    public Review createReview(Long productId, Long userId, String comment, int rating) {
        // Check if the product exists
        Product product = productService.getProductById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
        
        // Check if the user exists
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        // Check if the user has already reviewed this product
        if (reviewRepository.existsByUserIdAndProductId(userId, productId)) {
            throw new IllegalArgumentException("You have already reviewed this product");
        }
        
        // Create and save the review
        Review review = new Review(comment, rating, product, user);
        Review savedReview = reviewRepository.save(review);
        
        // Update the product's rating statistics
        updateProductRatingStats(productId);
        
        return savedReview;
    }
    
    /**
     * Get all reviews for a product.
     * 
     * @param productId The ID of the product.
     * @return A list of reviews for the product.
     */
    @Transactional(readOnly = true)
    public List<Review> getProductReviews(Long productId) {
        return reviewRepository.findByProductId(productId);
    }
    
    /**
     * Update an existing review.
     * 
     * @param reviewId The ID of the review to update.
     * @param userId The ID of the user making the update.
     * @param comment The updated comment (optional).
     * @param rating The updated rating (1-5, optional).
     * @return The updated review.
     * @throws ResourceNotFoundException If the review is not found.
     * @throws IllegalStateException If the user is not the author of the review.
     */
    public Review updateReview(Long reviewId, Long userId, String comment, Integer rating) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));
        
        // Verify the user is the author of the review
        if (!review.getUser().getId().equals(userId)) {
            throw new IllegalStateException("You are not authorized to update this review");
        }
        
        // Update the review fields if provided
        if (comment != null) {
            review.setComment(comment);
        }
        
        if (rating != null) {
            review.setRating(rating);
        }
        
        Review updatedReview = reviewRepository.save(review);
        
        // Update the product's rating statistics
        updateProductRatingStats(review.getProduct().getId());
        
        return updatedReview;
    }
    
    /**
     * Delete a review.
     * 
     * @param reviewId The ID of the review to delete.
     * @param userId The ID of the user making the request.
     * @throws ResourceNotFoundException If the review is not found.
     * @throws IllegalStateException If the user is not the author of the review.
     */
    public void deleteReview(Long reviewId, Long userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));
        
        // Verify the user is the author of the review or an admin
        if (!review.getUser().getId().equals(userId) && !userService.isAdmin(userId)) {
            throw new IllegalStateException("You are not authorized to delete this review");
        }
        
        Long productId = review.getProduct().getId();
        reviewRepository.delete(review);
        
        // Update the product's rating statistics
        updateProductRatingStats(productId);
    }
    
    // Rating Methods
    
    /**
     * Add or update a rating for a product.
     * 
     * @param productId The ID of the product being rated.
     * @param userId The ID of the user submitting the rating.
     * @param value The rating value (1-5).
     * @return The created or updated rating.
     * @throws ResourceNotFoundException If the product or user is not found.
     */
    public Rating rateProduct(Long productId, Long userId, int value) {
        // Check if the product exists
        Product product = productService.getProductById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
        
        // Check if the user exists
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        // Check if the user has already rated this product
        Rating rating = ratingRepository.findByUserIdAndProductId(userId, productId)
                .orElse(new Rating(value, product, user));
        
        // Update the rating value if it's an existing rating
        if (rating.getId() != null) {
            rating.setValue(value);
        }
        
        Rating savedRating = ratingRepository.save(rating);
        
        // Update the product's rating statistics
        updateProductRatingStats(productId);
        
        return savedRating;
    }
    
    /**
     * Get a user's rating for a product.
     * 
     * @param productId The ID of the product.
     * @param userId The ID of the user.
     * @return The user's rating for the product, or null if not found.
     */
    @Transactional(readOnly = true)
    public Integer getUserRatingForProduct(Long productId, Long userId) {
        return ratingRepository.findByUserIdAndProductId(userId, productId)
                .map(Rating::getValue)
                .orElse(null);
    }
    
    /**
     * Get the average rating for a product.
     * 
     * @param productId The ID of the product.
     * @return The average rating, or null if no ratings exist.
     */
    @Transactional(readOnly = true)
    public Double getAverageRating(Long productId) {
        return ratingRepository.findAverageRatingByProductId(productId);
    }
    
    /**
     * Get the number of ratings for a product.
     * 
     * @param productId The ID of the product.
     * @return The number of ratings.
     */
    @Transactional(readOnly = true)
    public long getRatingCount(Long productId) {
        return ratingRepository.countByProductId(productId);
    }
    
    /**
     * Update a product's rating statistics.
     * 
     * @param productId The ID of the product.
     */
    private void updateProductRatingStats(Long productId) {
        Double averageRating = ratingRepository.findAverageRatingByProductId(productId);
        long ratingCount = ratingRepository.countByProductId(productId);
        
        // Update the product's rating in the database
        productService.updateProductRating(productId, averageRating, (int) ratingCount);
    }
}
