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
    // You can add more filters here as needed, e.g., brands, ratings, etc.
}
