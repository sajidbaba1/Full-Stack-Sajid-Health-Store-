package com.healthstore.dto;

import lombok.Data;

/**
 * Data Transfer Object for CartItem entity.
 * Used to transfer cart item data between the client and server.
 */
@Data
public class CartItemDTO {
    private Long id;
    private Long productId;
    private String productName;
    private double productPrice;
    private int quantity;
}
