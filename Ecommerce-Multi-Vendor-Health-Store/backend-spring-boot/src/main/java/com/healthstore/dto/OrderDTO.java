package com.healthstore.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Data Transfer Object for Order entity.
 * Contains order details including items, status, and shipping information.
 */
@Data
public class OrderDTO {
    private Long id;
    private String orderNumber;
    private Long userId;
    private List<OrderItemDTO> orderItems;
    private LocalDateTime orderDate;
    private String status;
    private double totalAmount;
    private AddressDTO shippingAddress;
    private LocalDateTime shippedDate;
    private LocalDateTime deliveredDate;
    private LocalDateTime cancelledDate;
}
