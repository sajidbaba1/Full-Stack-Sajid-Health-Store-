package com.healthstore.controller;

import com.healthstore.dto.ProductResponseDTO;
import com.healthstore.model.Product;
import com.healthstore.model.User;
import com.healthstore.service.RecommendationService;
import com.healthstore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for handling product recommendations.
 */
@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;
    private final UserService userService;

    @Autowired
    public RecommendationController(RecommendationService recommendationService, 
                                  UserService userService) {
        this.recommendationService = recommendationService;
        this.userService = userService;
    }

    /**
     * Get personalized product recommendations for the authenticated user.
     * @param userDetails The authenticated user's details
     * @return List of recommended products as DTOs
     */
    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getRecommendations(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        // Get the authenticated user
        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Get recommendations
        List<Product> recommendedProducts = recommendationService.getRecommendationsForUser(user);
        
        // Convert to DTOs
        List<ProductResponseDTO> responseDTOs = recommendedProducts.stream()
                .map(this::convertToProductResponseDTO)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(responseDTOs);
    }
    
    /**
     * Converts a Product entity to a ProductResponseDTO.
     * @param product The product to convert.
     * @return The converted ProductResponseDTO.
     */
    private ProductResponseDTO convertToProductResponseDTO(Product product) {
        ProductResponseDTO dto = new ProductResponseDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice().doubleValue());
        dto.setStock(product.getStock());
        dto.setImageUrl(product.getImageUrl());
        
        if (product.getCategory() != null) {
            dto.setCategoryId(product.getCategory().getId());
            dto.setCategoryName(product.getCategory().getName());
        }
        
        return dto;
    }
}
