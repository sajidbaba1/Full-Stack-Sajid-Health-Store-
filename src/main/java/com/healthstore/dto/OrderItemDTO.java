package com.healthstore.dto;

import lombok.Data;

/**
 * Data Transfer Object for OrderItem entity.
 * Used to transfer order item data between the client and server.
 */
@Data
public class OrderItemDTO {
    private Long id;
    private Long productId;
    private String productName;
    private double price;
    private int quantity;
}
