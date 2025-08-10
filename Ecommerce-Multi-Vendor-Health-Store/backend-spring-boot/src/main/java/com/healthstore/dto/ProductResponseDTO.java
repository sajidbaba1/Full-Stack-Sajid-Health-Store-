package com.healthstore.dto;

import lombok.Data;

/**
 * Data Transfer Object for product responses.
 * This DTO is used to send product information to the client
 * without causing circular references in the JSON response.
 */
@Data
public class ProductResponseDTO {

    private Long id;
    private String name;
    private String description;
    private double price;
    private int stock;
    private String imageUrl;
    private Long categoryId;
    private String categoryName;
}
