package com.healthstore.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;

/**
 * Data Transfer Object for creating and updating orders.
 * Used to transfer order data between the client and the server.
 */
@Data
public class OrderDTO {

    /**
     * Data Transfer Object for order items.
     */
    @Data
    public static class OrderItemDTO {
        @NotNull(message = "Product ID is required")
        private Long productId;
        
        @NotNull(message = "Quantity is required")
        @Positive(message = "Quantity must be greater than 0")
        private Integer quantity;
    }

    @Valid
    @NotNull(message = "Order items are required")
    private List<OrderItemDTO> items;
    
    // Shipping information can be added here if needed
    // private ShippingInfoDTO shippingInfo;
    
    // Payment information can be added here if needed
    // private PaymentInfoDTO paymentInfo;
}
