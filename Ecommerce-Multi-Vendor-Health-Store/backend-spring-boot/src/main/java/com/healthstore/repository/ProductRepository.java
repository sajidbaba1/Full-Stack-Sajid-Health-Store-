package com.healthstore.repository;

import com.healthstore.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    
    List<Product> findByNameContainingIgnoreCaseAndCategoryId(String name, Long categoryId);
    
    List<Product> findByNameContainingIgnoreCase(String name);
    
    List<Product> findByCategoryId(Long categoryId);
    
    @Query("SELECT p FROM Product p WHERE p.category.id IN :categoryIds AND p.id NOT IN :excludedProductIds")
    List<Product> findProductsByCategoriesAndNotInIds(
        @Param("categoryIds") Set<Long> categoryIds, 
        @Param("excludedProductIds") Set<Long> excludedProductIds, 
        Pageable pageable
    );
    
    /**
     * Advanced search with multiple filters
     */
    @Query("SELECT p FROM Product p WHERE " +
           "(:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:categoryId IS NULL OR p.category.id = :categoryId)")
    Page<Product> searchProducts(@Param("name") String name, 
                                @Param("categoryId") Long categoryId, 
                                Pageable pageable);
    
    /**
     * Get featured products (newest 10)
     */
    List<Product> findTop10ByOrderByCreatedAtDesc();
}
