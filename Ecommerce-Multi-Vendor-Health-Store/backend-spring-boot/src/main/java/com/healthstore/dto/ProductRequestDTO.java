package com.healthstore.dto;

import com.healthstore.model.ProductVariant;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class ProductRequestDTO {
    private String name;
    private String description;
    private String imageUrl;
    private Set<Long> categoryIds;
    private List<ProductVariant> variants;
}
