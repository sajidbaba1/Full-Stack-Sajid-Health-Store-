package com.healthstore.model;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Represents an item within an order.
 * Links a product to an order with quantity and price information.
 */
@Data
@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Double pricePerUnit;

    @Column(nullable = false)
    private Double totalPrice;

    // Calculate total price before persisting
    @PrePersist
    @PreUpdate
    public void calculateTotalPrice() {
        if (pricePerUnit != null && quantity != null) {
            this.totalPrice = pricePerUnit * quantity;
        }
    }
}
