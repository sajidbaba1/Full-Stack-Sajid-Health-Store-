package com.healthstore.model;

import jakarta.persistence.*;
import lombok.Data;

/**
 * The CartItem entity represents a single item within a shopping cart.
 * It links a product to a cart and stores the quantity.
 */
@Entity
@Table(name = "cart_items")
@Data
public class CartItem {

    /**
     * Unique identifier for the cart item.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Many-to-One relationship with the Cart entity.
     * A cart item belongs to one cart.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    /**
     * Many-to-One relationship with the Product entity.
     * A cart item is for one product.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    private int quantity;
}
