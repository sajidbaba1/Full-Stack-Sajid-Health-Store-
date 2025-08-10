package com.healthstore.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * The Cart entity represents a user's shopping cart.
 * It has a one-to-one relationship with the User entity.
 * It contains a list of CartItems.
 */
@Entity
@Table(name = "carts")
@Data
public class Cart {

    /**
     * Unique identifier for the cart.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * One-to-one relationship with the User entity.
     * A user has exactly one cart.
     */
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * One-to-Many relationship with the CartItem entity.
     * A cart can have multiple items.
     * 'mappedBy' indicates that the 'cart' field in CartItem is the owner.
     * 'cascade' ensures that operations on the cart affect its items.
     */
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> cartItems = new ArrayList<>();
}
