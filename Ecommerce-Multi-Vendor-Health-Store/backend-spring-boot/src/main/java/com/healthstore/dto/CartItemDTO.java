package com.healthstore.dto;

import lombok.Data;

/**
 * Data Transfer Object for CartItem entity.
 * Represents an item in the shopping cart with product details and quantity.
 */
@Data
public class CartItemDTO {
    private Long id;
    private Long productId;
    private String productName;
    private double productPrice;
    private int quantity;
}
