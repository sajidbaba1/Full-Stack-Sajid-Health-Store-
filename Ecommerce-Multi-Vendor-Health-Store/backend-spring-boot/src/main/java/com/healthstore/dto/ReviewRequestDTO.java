package com.healthstore.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Data Transfer Object for creating or updating a product review.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequestDTO {
    
    @NotNull(message = "Product ID is required")
    private Long productId;
    
    @NotNull(message = "Review content cannot be empty")
    @Size(min = 10, max = 1000, message = "Review must be between 10 and 1000 characters")
    private String content;
    
    private List<String> imageUrls;
}
