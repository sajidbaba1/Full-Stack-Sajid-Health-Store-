package com.healthstore.service;

import com.healthstore.dto.ReviewRequestDTO;
import com.healthstore.exception.ResourceNotFoundException;
import com.healthstore.model.Product;
import com.healthstore.model.Review;
import com.healthstore.model.User;
import com.healthstore.repository.ProductRepository;
import com.healthstore.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service class for handling review-related business logic.
 */
@Service
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserService userService;

    @Autowired
    public ReviewService(ReviewRepository reviewRepository,
                        ProductRepository productRepository,
                        UserService userService) {
        this.reviewRepository = reviewRepository;
        this.productRepository = productRepository;
        this.userService = userService;
    }

    /**
     * Submit or update a review for a product.
     * 
     * @param productId The ID of the product to review
     * @param content The review content
     * @param imageUrls Optional list of image URLs
     * @return The saved review
     * @throws ResourceNotFoundException if the product is not found
     */
    public Review submitReview(Long productId, String content, List<String> imageUrls) {
        User currentUser = userService.getCurrentUser();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        // Check if user already reviewed this product
        Optional<Review> existingReview = reviewRepository.findByProductIdAndUserId(productId, currentUser.getId());
        
        Review review = existingReview.orElse(new Review());
        review.setUser(currentUser);
        review.setProduct(product);
        review.setContent(content);
        
        if (imageUrls != null && !imageUrls.isEmpty()) {
            review.setImages(imageUrls);
        }
        
        review.setCreatedAt(LocalDateTime.now());
        
        return reviewRepository.save(review);
    }

    /**
     * Get a user's review for a specific product.
     * 
     * @param productId The ID of the product
     * @return The user's review if it exists, empty otherwise
     */
    public Optional<Review> getUserReviewForProduct(Long productId) {
        User currentUser = userService.getCurrentUser();
        return reviewRepository.findByProductIdAndUserId(productId, currentUser.getId());
    }

    /**
     * Get all reviews for a product.
     * 
     * @param productId The ID of the product
     * @return List of reviews for the product
     */
    public List<Review> getReviewsForProduct(Long productId) {
        return reviewRepository.findByProductId(productId);
    }

    /**
     * Remove a user's review for a product.
     * 
     * @param productId The ID of the product
     * @return true if a review was deleted, false otherwise
     */
    public boolean removeReview(Long productId) {
        User currentUser = userService.getCurrentUser();
        return reviewRepository.deleteByProductIdAndUserId(productId, currentUser.getId()) > 0;
    }
}
