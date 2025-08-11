package com.healthstore.service;

import com.healthstore.dto.ProductDTO;
import com.healthstore.model.Category;
import com.healthstore.model.Product;
import com.healthstore.repository.ProductRepository;
import com.healthstore.specification.ProductSpecification;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.healthstore.dto.SearchFilterDTO;
import java.math.BigDecimal;
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

    @CacheEvict(value = "products", allEntries = true)
    public Product createProduct(ProductDTO productDTO) {
        Product product = new Product();
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setImageUrl(productDTO.getImageUrl());
        product.setActive(true);
        return productRepository.save(product);
    }
    
    @Cacheable(value = "products", key = "#pageable.pageNumber + '-' + #pageable.pageSize + '-' + #pageable.sort")
    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }
    
    /**
     * Find products based on the provided filters with pagination.
     * @param filters The search filters
     * @param pageable The pagination information
     * @return A page of filtered products
     */
    public Page<Product> findWithFilters(SearchFilterDTO filters, Pageable pageable) {
        return productRepository.findAll(ProductSpecification.filterBy(filters), pageable);
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }
    
    @CacheEvict(value = "products", allEntries = true)
    public Product updateProduct(Long id, ProductDTO productDTO) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setImageUrl(productDTO.getImageUrl());
        return productRepository.save(product);
    }
    
    @CacheEvict(value = "products", allEntries = true)
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
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
        return productRepository.searchProducts(name, categoryId, pageable);
    }
    
    /**
     * Advanced search with pagination and multiple filters (alias method)
     */
    public Page<Product> advancedSearch(String name, BigDecimal minPrice, BigDecimal maxPrice, 
                                       Long categoryId, Pageable pageable) {
        return searchProducts(name, minPrice, maxPrice, categoryId, pageable);
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
