package com.healthstore.controller;

import com.healthstore.dto.ReviewRequestDTO;
import com.healthstore.model.Review;
import com.healthstore.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for handling product review operations.
 */
@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    /**
     * Submit or update a review for a product.
     * 
     * @param reviewRequest The review request containing product ID and content
     * @return The saved review
     */
    @PostMapping
    public ResponseEntity<Review> submitReview(@Valid @RequestBody ReviewRequestDTO reviewRequest) {
        Review review = reviewService.submitReview(
            reviewRequest.getProductId(),
            reviewRequest.getContent(),
            reviewRequest.getImageUrls()
        );
        return new ResponseEntity<>(review, HttpStatus.CREATED);
    }

    /**
     * Get the current user's review for a product.
     * 
     * @param productId The ID of the product
     * @return The user's review if it exists, 404 otherwise
     */
    @GetMapping("/product/{productId}")
    public ResponseEntity<Review> getUserReviewForProduct(@PathVariable Long productId) {
        return reviewService.getUserReviewForProduct(productId)
                .map(review -> new ResponseEntity<>(review, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * Get all reviews for a product.
     * 
     * @param productId The ID of the product
     * @return List of reviews for the product
     */
    @GetMapping("/product/{productId}/all")
    public ResponseEntity<List<Review>> getReviewsForProduct(@PathVariable Long productId) {
        List<Review> reviews = reviewService.getReviewsForProduct(productId);
        return new ResponseEntity<>(reviews, HttpStatus.OK);
    }

    /**
     * Remove the current user's review for a product.
     * 
     * @param productId The ID of the product
     * @return 204 No Content if successful, 404 if no review was found
     */
    @DeleteMapping("/product/{productId}")
    public ResponseEntity<Void> removeReview(@PathVariable Long productId) {
        boolean deleted = reviewService.removeReview(productId);
        return deleted ? 
            new ResponseEntity<>(HttpStatus.NO_CONTENT) : 
            new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
