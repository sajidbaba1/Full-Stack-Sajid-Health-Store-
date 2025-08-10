package com.healthstore.controller;

import com.healthstore.dto.ReviewRequest;
import com.healthstore.model.Review;
import com.healthstore.service.ReviewAndRatingService;
import com.healthstore.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for handling product reviews and ratings.
 */
@RestController
@RequestMapping("/api/products/{productId}")
public class ReviewAndRatingController {

    private final ReviewAndRatingService reviewAndRatingService;
    private final UserService userService;

    @Autowired
    public ReviewAndRatingController(ReviewAndRatingService reviewAndRatingService, 
                                   UserService userService) {
        this.reviewAndRatingService = reviewAndRatingService;
        this.userService = userService;
    }

    // Review Endpoints

    /**
     * Create a new review for a product.
     * 
     * @param productId The ID of the product to review.
     * @param userDetails The authenticated user.
     * @param request The review request containing the comment and rating.
     * @return The created review.
     */
    @PostMapping("/reviews")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createReview(
            @PathVariable Long productId,
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ReviewRequest request) {
        
        try {
            Long userId = getUserIdFromUserDetails(userDetails);
            Review review = reviewAndRatingService.createReview(
                    productId, 
                    userId, 
                    request.getComment(), 
                    request.getRating()
            );
            return new ResponseEntity<>(review, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Get all reviews for a product.
     * 
     * @param productId The ID of the product.
     * @return A list of reviews for the product.
     */
    @GetMapping("/reviews")
    public ResponseEntity<List<Review>> getProductReviews(@PathVariable Long productId) {
        List<Review> reviews = reviewAndRatingService.getProductReviews(productId);
        return ResponseEntity.ok(reviews);
    }

    /**
     * Update a review.
     * 
     * @param productId The ID of the product.
     * @param reviewId The ID of the review to update.
     * @param userDetails The authenticated user.
     * @param request The updated review data.
     * @return The updated review.
     */
    @PutMapping("/reviews/{reviewId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateReview(
            @PathVariable Long productId,
            @PathVariable Long reviewId,
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ReviewRequest request) {
        
        try {
            Long userId = getUserIdFromUserDetails(userDetails);
            Review updatedReview = reviewAndRatingService.updateReview(
                    reviewId,
                    userId,
                    request.getComment(),
                    request.getRating()
            );
            return ResponseEntity.ok(updatedReview);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Delete a review.
     * 
     * @param productId The ID of the product.
     * @param reviewId The ID of the review to delete.
     * @param userDetails The authenticated user.
     * @return A response indicating success or failure.
     */
    @DeleteMapping("/reviews/{reviewId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteReview(
            @PathVariable Long productId,
            @PathVariable Long reviewId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        try {
            Long userId = getUserIdFromUserDetails(userDetails);
            reviewAndRatingService.deleteReview(reviewId, userId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Rating Endpoints

    /**
     * Rate a product.
     * 
     * @param productId The ID of the product to rate.
     * @param userDetails The authenticated user.
     * @param ratingValue The rating value (1-5).
     * @return The created or updated rating.
     */
    @PostMapping("/rate")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> rateProduct(
            @PathVariable Long productId,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam int rating) {
        
        try {
            Long userId = getUserIdFromUserDetails(userDetails);
            return ResponseEntity.ok(reviewAndRatingService.rateProduct(productId, userId, rating));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Get the authenticated user's rating for a product.
     * 
     * @param productId The ID of the product.
     * @param userDetails The authenticated user.
     * @return The user's rating, or 0 if not rated.
     */
    @GetMapping("/my-rating")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Integer> getUserRating(
            @PathVariable Long productId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Long userId = getUserIdFromUserDetails(userDetails);
        Integer rating = reviewAndRatingService.getUserRatingForProduct(productId, userId);
        return ResponseEntity.ok(rating != null ? rating : 0);
    }

    /**
     * Get the average rating for a product.
     * 
     * @param productId The ID of the product.
     * @return The average rating, or 0 if no ratings exist.
     */
    @GetMapping("/average-rating")
    public ResponseEntity<Double> getAverageRating(@PathVariable Long productId) {
        Double averageRating = reviewAndRatingService.getAverageRating(productId);
        return ResponseEntity.ok(averageRating != null ? averageRating : 0.0);
    }

    /**
     * Get the number of ratings for a product.
     * 
     * @param productId The ID of the product.
     * @return The number of ratings.
     */
    @GetMapping("/rating-count")
    public ResponseEntity<Long> getRatingCount(@PathVariable Long productId) {
        return ResponseEntity.ok(reviewAndRatingService.getRatingCount(productId));
    }

    /**
     * Helper method to get the user ID from UserDetails.
     */
    private Long getUserIdFromUserDetails(UserDetails userDetails) {
        if (userDetails == null) {
            throw new IllegalStateException("User not authenticated");
        }
        return userService.findUserByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalStateException("User not found"))
                .getId();
    }
}

/**
 * DTO for review requests.
 */
class ReviewRequest {
    private String comment;
    private int rating;

    // Getters and setters
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
