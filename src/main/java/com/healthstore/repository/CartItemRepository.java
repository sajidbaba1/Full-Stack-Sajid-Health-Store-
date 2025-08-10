package com.healthstore.repository;

import com.healthstore.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for CartItem entities.
 * It extends JpaRepository to inherit methods for standard CRUD operations.
 */
@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
}
