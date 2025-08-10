package com.healthstore.repository;

import com.healthstore.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Product entities.
 * It extends JpaRepository to inherit methods for standard CRUD operations.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    /**
     * Finds products by name containing the given string (case-insensitive) and category ID
     * @param name The name to search for (partial match)
     * @param categoryId The category ID to filter by
     * @return List of matching products
     */
    List<Product> findByNameContainingIgnoreCaseAndCategoryId(String name, Long categoryId);
    
    /**
     * Finds products by name containing the given string (case-insensitive)
     * @param name The name to search for (partial match)
     * @return List of matching products
     */
    List<Product> findByNameContainingIgnoreCase(String name);
    
    /**
     * Finds products by category ID
     * @param categoryId The category ID to filter by
     * @return List of products in the specified category
     */
    List<Product> findByCategoryId(Long categoryId);
    
    /**
     * Finds products by name containing the given string (case-insensitive) and category ID with pagination
     * @param name The name to search for (partial match)
     * @param categoryId The category ID to filter by
     * @param pageable Pagination information
     * @return Page of matching products
     */
    Page<Product> findByNameContainingIgnoreCaseAndCategoryId(String name, Long categoryId, Pageable pageable);
    
    /**
     * Finds products by name containing the given string (case-insensitive) with pagination
     * @param name The name to search for (partial match)
     * @param pageable Pagination information
     * @return Page of matching products
     */
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);
    
    /**
     * Finds products by category ID with pagination
     * @param categoryId The category ID to filter by
     * @param pageable Pagination information
     * @return Page of products in the specified category
     */
    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);
    
    /**
     * Finds products by category ID with pagination.
     *
     * @param categoryId The ID of the category.
     * @param pageable   The pagination information.
     * @return A page of products in the specified category.
     */
    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId")
    Page<Product> findByCategoryId(@Param("categoryId") Long categoryId, Pageable pageable);
    
    /**
     * Finds products by category ID without pagination.
     *
     * @param categoryId The ID of the category.
     * @return A list of products in the specified category.
     * @deprecated Use {@link #findByCategoryId(Long, Pageable)} with pagination instead.
     */
    @Deprecated
    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId")
    List<Product> findByCategoryId(@Param("categoryId") Long categoryId);
    
    /**
     * Finds products by name containing the given string (case-insensitive) with pagination.
     *
     * @param name     The string to search for in product names.
     * @param pageable The pagination information.
     * @return A page of products matching the search criteria.
     */
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<Product> findByNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);
    
    /**
     * Finds products by price range with pagination.
     *
     * @param minPrice The minimum price (inclusive).
     * @param maxPrice The maximum price (inclusive).
     * @param pageable The pagination information.
     * @return A page of products within the specified price range.
     */
    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice")
    Page<Product> findByPriceBetween(
            @Param("minPrice") double minPrice,
            @Param("maxPrice") double maxPrice,
            Pageable pageable
    );
    
    /**
     * Finds products by stock level (products with stock greater than or equal to the specified value).
     *
     * @param minStock The minimum stock level (inclusive).
     * @param pageable The pagination information.
     * @return A page of products with stock greater than or equal to the specified value.
     */
    @Query("SELECT p FROM Product p WHERE p.stock >= :minStock")
    Page<Product> findByStockGreaterThanEqual(@Param("minStock") int minStock, Pageable pageable);
}
