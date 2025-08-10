package com.healthstore.service;

import com.healthstore.dto.ProductDTO;
import com.healthstore.model.Category;
import com.healthstore.model.Product;
import com.healthstore.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;

    public ProductService(ProductRepository productRepository, CategoryService categoryService) {
        this.productRepository = productRepository;
        this.categoryService = categoryService;
    }

    public Product createProduct(ProductDTO productDTO) {
        // ... existing logic
    }
    
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }
    
    public Product updateProduct(Long id, ProductDTO productDTO) {
        // ... existing logic
    }
    
    public void deleteProduct(Long id) {
        // ... existing logic
    }
    
    /**
     * Searches and filters products based on name and category ID.
     * @param name The product name to search for.
     * @param categoryId The category ID to filter by.
     * @return A list of products matching the criteria.
     */
    public List<Product> searchProducts(String name, Long categoryId) {
        if (name != null && categoryId != null) {
            return productRepository.findByNameContainingIgnoreCaseAndCategoryId(name, categoryId);
        } else if (name != null) {
            return productRepository.findByNameContainingIgnoreCase(name);
        } else if (categoryId != null) {
            return productRepository.findByCategoryId(categoryId);
        } else {
            return productRepository.findAll();
        }
    }
    
    /**
     * Advanced search with pagination and multiple filters
     */
    public Page<Product> searchProducts(String name, BigDecimal minPrice, BigDecimal maxPrice, 
                                      Long categoryId, Pageable pageable) {
        return productRepository.searchProducts(name, minPrice, maxPrice, categoryId, pageable);
    }
    
    /**
     * Get featured products (newest 10)
     */
    public List<Product> getFeaturedProducts() {
        return productRepository.findTop10ByOrderByCreatedAtDesc();
    }
    
    /**
     * Saves a product to the database.
     * @param product The product to save.
     * @return The saved product.
     */
    public Product save(Product product) {
        return productRepository.save(product);
    }
}
