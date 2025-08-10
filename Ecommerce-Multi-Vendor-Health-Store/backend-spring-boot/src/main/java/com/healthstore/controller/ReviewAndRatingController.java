package com.healthstore.controller;

import com.healthstore.model.Rating;
import com.healthstore.model.Review;
import com.healthstore.model.User;
import com.healthstore.service.ReviewAndRatingService;
import com.healthstore.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Controller for handling review and rating related endpoints.
 * Provides APIs for creating and retrieving product reviews and ratings.
 */
@RestController
@RequestMapping("/api/products/{productId}")
public class ReviewAndRatingController {

    private final ReviewAndRatingService reviewAndRatingService;
    private final UserService userService;

    /**
     * Constructs a new ReviewAndRatingController with the required services.
     * @param reviewAndRatingService The service for review and rating operations.
     * @param userService The service for user operations.
     */
    public ReviewAndRatingController(ReviewAndRatingService reviewAndRatingService, UserService userService) {
        this.reviewAndRatingService = reviewAndRatingService;
        this.userService = userService;
    }

    /**
     * Creates a new review for a product.
     * @param userDetails The authenticated user details.
     * @param productId The ID of the product to review.
     * @param comment The review comment.
     * @return The created review or an error message.
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/reviews")
    public ResponseEntity<?> createReview(@AuthenticationPrincipal UserDetails userDetails,
                                        @PathVariable Long productId,
                                        @RequestBody String comment) {
        Optional<User> user = userService.findUserByEmail(userDetails.getUsername());
        if (user.isEmpty()) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
        try {
            Review review = reviewAndRatingService.createReview(productId, user.get(), comment);
            return new ResponseEntity<>(review, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Creates or updates a rating for a product.
     * @param userDetails The authenticated user details.
     * @param productId The ID of the product to rate.
     * @param ratingValue The rating value (1-5).
     * @return The created/updated rating or an error message.
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/ratings")
    public ResponseEntity<?> createOrUpdateRating(@AuthenticationPrincipal UserDetails userDetails,
                                                @PathVariable Long productId,
                                                @RequestBody int ratingValue) {
        Optional<User> user = userService.findUserByEmail(userDetails.getUsername());
        if (user.isEmpty()) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
        try {
            Rating rating = reviewAndRatingService.createOrUpdateRating(productId, user.get(), ratingValue);
            return new ResponseEntity<>(rating, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Retrieves all reviews for a specific product.
     * @param productId The ID of the product.
     * @return A list of reviews for the specified product.
     */
    @GetMapping("/reviews")
    public ResponseEntity<List<Review>> getReviews(@PathVariable Long productId) {
        List<Review> reviews = reviewAndRatingService.getReviewsByProductId(productId);
        return new ResponseEntity<>(reviews, HttpStatus.OK);
    }
}
