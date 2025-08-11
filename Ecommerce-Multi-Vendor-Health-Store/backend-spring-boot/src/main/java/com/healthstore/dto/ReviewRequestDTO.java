package com.healthstore.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

/**
 * Data Transfer Object for review creation requests.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequestDTO {
    
    @NotNull(message = "Product ID is required")
    private Long productId;
    
    @NotBlank(message = "Review content is required")
    private String content;
    
    private List<String> imageUrls;

    // Manual getter and setter methods to ensure compilation works when Lombok fails
    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }
}
