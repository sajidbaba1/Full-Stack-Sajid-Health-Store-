package com.healthstore.dto;

import com.healthstore.model.Category;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for product responses.
 * Used to prevent circular references and control the data exposed by the API.
 */
@Data
public class ProductResponseDTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stockQuantity;
    private String imageUrl;
    private Double averageRating;
    private Integer ratingCount;
    private CategoryDTO category;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * DTO for category information in product responses.
     */
    @Data
    public static class CategoryDTO {
        private Long id;
        private String name;
        private String description;

        public static CategoryDTO fromCategory(Category category) {
            if (category == null) {
                return null;
            }
            CategoryDTO dto = new CategoryDTO();
            dto.setId(category.getId());
            dto.setName(category.getName());
            dto.setDescription(category.getDescription());
            return dto;
        }
    }

    /**
     * Maps a Product entity to a ProductResponseDTO.
     * 
     * @param product The product entity to map.
     * @return A new ProductResponseDTO instance.
     */
    public static ProductResponseDTO fromProduct(com.healthstore.model.Product product) {
        if (product == null) {
            return null;
        }
        
        ProductResponseDTO dto = new ProductResponseDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setStockQuantity(product.getStockQuantity());
        dto.setImageUrl(product.getImageUrl());
        dto.setAverageRating(product.getAverageRating());
        dto.setRatingCount(product.getRatingCount());
        dto.setCreatedAt(product.getCreatedAt());
        dto.setUpdatedAt(product.getUpdatedAt());
        
        // Map category
        if (product.getCategory() != null) {
            dto.setCategory(CategoryDTO.fromCategory(product.getCategory()));
        }
        
        return dto;
    }
}
