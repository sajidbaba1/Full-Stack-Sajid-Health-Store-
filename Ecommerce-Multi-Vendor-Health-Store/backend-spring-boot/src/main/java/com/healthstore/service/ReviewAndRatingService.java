package com.healthstore.service;

import com.healthstore.model.Product;
import com.healthstore.model.Rating;
import com.healthstore.model.Review;
import com.healthstore.model.User;
import com.healthstore.repository.RatingRepository;
import com.healthstore.repository.ReviewRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service class for managing product reviews and ratings.
 * Provides methods to create and retrieve reviews and ratings for products.
 */
@Service
public class ReviewAndRatingService {

    private final ReviewRepository reviewRepository;
    private final RatingRepository ratingRepository;
    private final ProductService productService;

    /**
     * Constructs a new ReviewAndRatingService with the required repositories and services.
     * @param reviewRepository The repository for review operations.
     * @param ratingRepository The repository for rating operations.
     * @param productService The service for product-related operations.
     */
    public ReviewAndRatingService(ReviewRepository reviewRepository, 
                                 RatingRepository ratingRepository, 
                                 ProductService productService) {
        this.reviewRepository = reviewRepository;
        this.ratingRepository = ratingRepository;
        this.productService = productService;
    }

    /**
     * Creates a new review for a product.
     * @param productId The ID of the product to review.
     * @param user The user creating the review.
     * @param comment The review comment.
     * @return The created review.
     * @throws RuntimeException if the product is not found.
     */
    public Review createReview(Long productId, User user, String comment) {
        Optional<Product> product = productService.getProductById(productId);
        if (product.isEmpty()) {
            throw new RuntimeException("Product not found");
        }
        
        Review review = new Review();
        review.setProduct(product.get());
        review.setUser(user);
        review.setComment(comment);
        review.setCreatedAt(LocalDateTime.now());
        
        return reviewRepository.save(review);
    }

    /**
     * Creates or updates a rating for a product.
     * @param productId The ID of the product to rate.
     * @param user The user creating/updating the rating.
     * @param ratingValue The rating value (1-5).
     * @return The created or updated rating.
     * @throws IllegalArgumentException if the rating value is not between 1 and 5.
     * @throws RuntimeException if the product is not found.
     */
    public Rating createOrUpdateRating(Long productId, User user, int ratingValue) {
        if (ratingValue < 1 || ratingValue > 5) {
            throw new IllegalArgumentException("Rating value must be between 1 and 5.");
        }
        
        Optional<Product> product = productService.getProductById(productId);
        if (product.isEmpty()) {
            throw new RuntimeException("Product not found");
        }
        
        // Check if user already rated this product
        Optional<Rating> existingRating = ratingRepository.findByProductIdAndUserId(productId, user.getId());
        Rating rating = existingRating.orElse(new Rating());
        
        // Update rating details
        rating.setProduct(product.get());
        rating.setUser(user);
        rating.setRatingValue(ratingValue);
        
        return ratingRepository.save(rating);
    }

    /**
     * Retrieves all reviews for a specific product.
     * @param productId The ID of the product.
     * @return A list of reviews for the specified product.
     */
    public List<Review> getReviewsByProductId(Long productId) {
        return reviewRepository.findByProductId(productId);
    }
}
