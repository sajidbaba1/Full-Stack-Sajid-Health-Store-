package com.healthstore.repository;

import com.healthstore.model.Cart;
import com.healthstore.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    
    Optional<Cart> findByUserId(Long userId);
    
    @Query("SELECT c FROM Cart c LEFT JOIN FETCH c.items ci LEFT JOIN FETCH ci.product WHERE c.user.id = :userId")
    Optional<Cart> findByUserIdWithItems(@Param("userId") Long userId);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM CartItem ci WHERE ci.cart.id = :cartId")
    void clearCartItems(@Param("cartId") Long cartId);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM CartItem ci WHERE ci.cart.id = :cartId AND ci.product.id = :productId")
    void removeCartItem(@Param("cartId") Long cartId, @Param("productId") Long productId);
    
    @Modifying
    @Transactional
    @Query("UPDATE CartItem ci SET ci.quantity = :quantity " +
           "WHERE ci.cart.id = :cartId AND ci.product.id = :productId")
    void updateCartItemQuantity(
            @Param("cartId") Long cartId,
            @Param("productId") Long productId,
            @Param("quantity") int quantity);
}
