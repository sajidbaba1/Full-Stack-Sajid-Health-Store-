package com.healthstore.dto;

import lombok.Data;

/**
 * Data Transfer Object for OrderItem entity.
 * Represents an item within an order with product details and purchase information.
 */
@Data
public class OrderItemDTO {
    private Long id;
    private Long orderId;
    private Long productId;
    private String productName;
    private double price;
    private double discount;
    private double finalPrice;
    private int quantity;
}
