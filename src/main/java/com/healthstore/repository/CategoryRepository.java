package com.healthstore.repository;

import com.healthstore.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Category entities.
 * It extends JpaRepository to inherit methods for standard CRUD operations.
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}
