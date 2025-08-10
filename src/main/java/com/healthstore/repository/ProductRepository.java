package com.healthstore.repository;

import com.healthstore.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Product entities.
 * It extends JpaRepository to inherit methods for standard CRUD operations.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}
