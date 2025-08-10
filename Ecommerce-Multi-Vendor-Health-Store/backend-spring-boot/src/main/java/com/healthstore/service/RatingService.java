package com.healthstore.service;

import com.healthstore.dto.RatingRequestDTO;
import com.healthstore.exception.ResourceNotFoundException;
import com.healthstore.model.Product;
import com.healthstore.model.Rating;
import com.healthstore.model.User;
import com.healthstore.repository.ProductRepository;
import com.healthstore.repository.RatingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service class for handling rating-related business logic.
 */
@Service
@Transactional
public class RatingService {

    private final RatingRepository ratingRepository;
    private final ProductRepository productRepository;
    private final UserService userService;

    @Autowired
    public RatingService(RatingRepository ratingRepository, 
                        ProductRepository productRepository,
                        UserService userService) {
        this.ratingRepository = ratingRepository;
        this.productRepository = productRepository;
        this.userService = userService;
    }

    /**
     * Submit or update a rating for a product.
     * 
     * @param productId The ID of the product to rate
     * @param ratingValue The rating value (1-5)
     * @return The saved rating
     * @throws ResourceNotFoundException if the product is not found
     */
    public Rating rateProduct(Long productId, int ratingValue) {
        User currentUser = userService.getCurrentUser();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        // Check if user already rated this product
        Optional<Rating> existingRating = ratingRepository.findByProductIdAndUserId(productId, currentUser.getId());
        
        Rating rating = existingRating.orElse(new Rating());
        rating.setUser(currentUser);
        rating.setProduct(product);
        rating.setValue(ratingValue);

        return ratingRepository.save(rating);
    }

    /**
     * Get a user's rating for a specific product.
     * 
     * @param productId The ID of the product
     * @return The user's rating if it exists, empty otherwise
     */
    public Optional<Integer> getUserRatingForProduct(Long productId) {
        User currentUser = userService.getCurrentUser();
        return ratingRepository.findByProductIdAndUserId(productId, currentUser.getId())
                .map(Rating::getValue);
    }

    /**
     * Get the average rating for a product.
     * 
     * @param productId The ID of the product
     * @return The average rating, or 0 if no ratings exist
     */
    public double getAverageRatingForProduct(Long productId) {
        return ratingRepository.findAverageRatingByProductId(productId)
                .orElse(0.0);
    }

    /**
     * Remove a user's rating for a product.
     * 
     * @param productId The ID of the product
     * @return true if a rating was deleted, false otherwise
     */
    public boolean removeRating(Long productId) {
        User currentUser = userService.getCurrentUser();
        return ratingRepository.deleteByProductIdAndUserId(productId, currentUser.getId()) > 0;
    }
}
