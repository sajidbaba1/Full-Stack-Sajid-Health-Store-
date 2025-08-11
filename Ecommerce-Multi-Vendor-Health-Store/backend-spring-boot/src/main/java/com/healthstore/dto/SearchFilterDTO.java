package com.healthstore.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * Data Transfer Object for product search filters.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchFilterDTO {
    private String keyword;
    private Double minPrice;
    private Double maxPrice;
    private List<Long> categoryIds;
    
    // Manual getter methods to ensure compilation works
    public String getKeyword() {
        return keyword;
    }
    
    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
    
    public Double getMinPrice() {
        return minPrice;
    }
    
    public void setMinPrice(Double minPrice) {
        this.minPrice = minPrice;
    }
    
    public Double getMaxPrice() {
        return maxPrice;
    }
    
    public void setMaxPrice(Double maxPrice) {
        this.maxPrice = maxPrice;
    }
    
    public List<Long> getCategoryIds() {
        return categoryIds;
    }
    
    public void setCategoryIds(List<Long> categoryIds) {
        this.categoryIds = categoryIds;
    }
}
