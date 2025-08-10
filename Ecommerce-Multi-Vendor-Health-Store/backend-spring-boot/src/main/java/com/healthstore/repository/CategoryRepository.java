package com.healthstore.repository;

import com.healthstore.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Category entities.
 * Extends JpaRepository to provide standard CRUD operations
 * and includes custom query methods for category-specific operations.
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    /**
     * Finds all top-level categories (categories with no parent).
     * @return A list of top-level categories.
     */
    List<Category> findByParentIsNull();
    
    /**
     * Finds all active categories.
     * @param active Whether the categories should be active.
     * @return A list of active categories.
     */
    List<Category> findByActive(boolean active);
    
    /**
     * Finds a category by its name (case-insensitive).
     * @param name The name of the category to find.
     * @return The category if found, null otherwise.
     */
    Category findByNameIgnoreCase(String name);
    
    /**
     * Checks if a category with the given name exists (case-insensitive).
     * @param name The name to check.
     * @return true if a category with the name exists, false otherwise.
     */
    boolean existsByNameIgnoreCase(String name);
    
    /**
     * Finds all categories that have a specific parent category.
     * @param parentId The ID of the parent category.
     * @return A list of child categories.
     */
    @Query("SELECT c FROM Category c WHERE c.parent.id = :parentId")
    List<Category> findByParentId(@Param("parentId") Long parentId);
    
    /**
     * Finds all categories that contain products.
     * @return A list of categories that have at least one product.
     */
    @Query("SELECT DISTINCT c FROM Category c JOIN c.products p WHERE p.active = true")
    List<Category> findCategoriesWithActiveProducts();
}
