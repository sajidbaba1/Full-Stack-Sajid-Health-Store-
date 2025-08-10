package com.healthstore.controller;

import com.healthstore.dto.RatingRequestDTO;
import com.healthstore.model.Rating;
import com.healthstore.service.RatingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for handling product rating operations.
 */
@RestController
@RequestMapping("/api/ratings")
public class RatingController {

    private final RatingService ratingService;

    @Autowired
    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    /**
     * Submit or update a rating for a product.
     * 
     * @param ratingRequest The rating request containing product ID and rating value
     * @return The saved rating
     */
    @PostMapping
    public ResponseEntity<Rating> rateProduct(@Valid @RequestBody RatingRequestDTO ratingRequest) {
        Rating rating = ratingService.rateProduct(
            ratingRequest.getProductId(), 
            ratingRequest.getValue()
        );
        return new ResponseEntity<>(rating, HttpStatus.CREATED);
    }

    /**
     * Get the current user's rating for a product.
     * 
     * @param productId The ID of the product
     * @return The user's rating if it exists, 404 otherwise
     */
    @GetMapping("/product/{productId}")
    public ResponseEntity<Integer> getUserRatingForProduct(@PathVariable Long productId) {
        return ratingService.getUserRatingForProduct(productId)
                .map(rating -> new ResponseEntity<>(rating, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * Get the average rating for a product.
     * 
     * @param productId The ID of the product
     * @return The average rating
     */
    @GetMapping("/product/{productId}/average")
    public ResponseEntity<Double> getAverageRatingForProduct(@PathVariable Long productId) {
        double averageRating = ratingService.getAverageRatingForProduct(productId);
        return new ResponseEntity<>(averageRating, HttpStatus.OK);
    }

    /**
     * Remove the current user's rating for a product.
     * 
     * @param productId The ID of the product
     * @return 204 No Content if successful, 404 if no rating was found
     */
    @DeleteMapping("/product/{productId}")
    public ResponseEntity<Void> removeRating(@PathVariable Long productId) {
        boolean deleted = ratingService.removeRating(productId);
        return deleted ? 
            new ResponseEntity<>(HttpStatus.NO_CONTENT) : 
            new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
