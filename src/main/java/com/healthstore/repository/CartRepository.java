package com.healthstore.repository;

import com.healthstore.model.Cart;
import com.healthstore.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Cart entities.
 * It includes a custom method to find a cart by its associated user.
 */
@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    /**
     * Finds a cart by the user it belongs to.
     * @param user The user entity associated with the cart.
     * @return An Optional containing the Cart if found, or an empty Optional.
     */
    Optional<Cart> findByUser(User user);
}
