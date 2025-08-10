package com.healthstore.dto;

import com.healthstore.model.Order;
import com.healthstore.model.OrderStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO for order responses.
 * Used to prevent circular references and control the data exposed by the API.
 */
@Data
public class OrderResponseDTO {
    private Long id;
    private Long userId;
    private String userFullName;
    private String userEmail;
    private List<OrderItemDTO> orderItems;
    private BigDecimal totalAmount;
    private String shippingAddress;
    private String billingAddress;
    private OrderStatus status;
    private String paymentStatus;
    private LocalDateTime orderDate;
    private LocalDateTime lastUpdated;

    /**
     * DTO for order item information in order responses.
     */
    @Data
    public static class OrderItemDTO {
        private Long productId;
        private String productName;
        private String productImageUrl;
        private int quantity;
        private BigDecimal unitPrice;
        private BigDecimal subtotal;

        public static OrderItemDTO fromOrderItem(com.healthstore.model.OrderItem orderItem) {
            if (orderItem == null) {
                return null;
            }
            
            OrderItemDTO dto = new OrderItemDTO();
            dto.setProductId(orderItem.getProduct().getId());
            dto.setProductName(orderItem.getProduct().getName());
            dto.setProductImageUrl(orderItem.getProduct().getImageUrl());
            dto.setQuantity(orderItem.getQuantity());
            dto.setUnitPrice(orderItem.getUnitPrice());
            dto.setSubtotal(orderItem.getSubtotal());
            return dto;
        }
    }

    /**
     * Maps an Order entity to an OrderResponseDTO.
     * 
     * @param order The order entity to map.
     * @return A new OrderResponseDTO instance.
     */
    public static OrderResponseDTO fromOrder(Order order) {
        if (order == null) {
            return null;
        }
        
        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setId(order.getId());
        
        if (order.getUser() != null) {
            dto.setUserId(order.getUser().getId());
            dto.setUserFullName(order.getUser().getFullName());
            dto.setUserEmail(order.getUser().getEmail());
        }
        
        dto.setOrderItems(order.getOrderItems().stream()
                .map(OrderItemDTO::fromOrderItem)
                .collect(Collectors.toList()));
        
        dto.setTotalAmount(order.getTotalAmount());
        dto.setShippingAddress(order.getShippingAddress());
        dto.setBillingAddress(order.getBillingAddress());
        dto.setStatus(order.getStatus());
        dto.setPaymentStatus(order.getPaymentStatus());
        dto.setOrderDate(order.getOrderDate());
        dto.setLastUpdated(order.getLastUpdated());
        
        return dto;
    }
}
