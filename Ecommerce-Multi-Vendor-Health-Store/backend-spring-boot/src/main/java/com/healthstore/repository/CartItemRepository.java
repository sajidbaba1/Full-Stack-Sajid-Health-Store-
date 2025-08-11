package com.healthstore.repository;

import com.healthstore.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for CartItem entities.
 * Provides data access methods for cart items.
 */
@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    /**
     * Finds a cart item by cart ID and product ID.
     * @param cartId The ID of the cart.
     * @param productId The ID of the product.
     * @return An Optional containing the cart item if found, or empty otherwise.
     */
    Optional<CartItem> findByCartIdAndProductId(Long cartId, Long productId);
    
    /**
     * Deletes all cart items for a specific cart.
     * @param cartId The ID of the cart.
     */
    void deleteByCartId(Long cartId);
    
    /**
     * Deletes a specific cart item by its ID and cart ID.
     * @param id The ID of the cart item.
     * @param cartId The ID of the cart.
     */
    void deleteByIdAndCartId(Long id, Long cartId);
}
