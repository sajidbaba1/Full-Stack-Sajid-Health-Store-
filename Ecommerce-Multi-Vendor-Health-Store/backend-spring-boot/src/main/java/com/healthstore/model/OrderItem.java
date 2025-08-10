package com.healthstore.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

/**
 * The OrderItem entity represents a single item within an order.
 * It contains information about the product, quantity, and price at the time of purchase.
 */
@Entity
@Table(name = "order_items")
@Data
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

    @Column(name = "price_at_purchase", nullable = false, precision = 10, scale = 2)
    private BigDecimal priceAtPurchase;

    @Column(precision = 10, scale = 2)
    private BigDecimal discount = BigDecimal.ZERO;

    @Column(name = "final_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal finalPrice;

    @PrePersist
    @PreUpdate
    private void calculateFinalPrice() {
        if (priceAtPurchase != null && discount != null) {
            this.finalPrice = priceAtPurchase.subtract(discount).multiply(new BigDecimal(quantity));
        }
    }
}
