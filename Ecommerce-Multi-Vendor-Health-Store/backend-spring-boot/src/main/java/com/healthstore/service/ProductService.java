package com.healthstore.service;

import com.healthstore.dto.ProductDTO;
import com.healthstore.model.Category;
import com.healthstore.model.Product;
import com.healthstore.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;

    public ProductService(ProductRepository productRepository, CategoryService categoryService) {
        this.productRepository = productRepository;
        this.categoryService = categoryService;
    }

    /**
     * Creates a new product from a DTO.
     * It finds the category by ID and then saves the product entity.
     * @param productDTO The product data from the request.
     * @return The saved product entity.
     * @throws RuntimeException if the category is not found.
     */
    public Product createProduct(ProductDTO productDTO) {
        // Validate category exists
        Category category = categoryService.getCategoryById(productDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found with ID: " + productDTO.getCategoryId()));
        
        // Create and save the product
        Product product = new Product();
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(BigDecimal.valueOf(productDTO.getPrice()));
        product.setStock(productDTO.getStock());
        product.setImageUrl(productDTO.getImageUrl());
        product.setCategory(category);
        
        return productRepository.save(product);
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
        return productRepository.findById(id).map(product -> {
            product.setName(productDTO.getName());
            product.setDescription(productDTO.getDescription());
            product.setPrice(productDTO.getPrice());
            product.setStock(productDTO.getStock());
            product.setImageUrl(productDTO.getImageUrl());
            
            if (productDTO.getCategoryId() != null) {
                Category category = categoryService.getCategoryById(productDTO.getCategoryId())
                        .orElseThrow(() -> new RuntimeException("Category not found"));
                product.setCategory(category);
            }
            
            return productRepository.save(product);
        }).orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }
    
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        productRepository.delete(product);
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
}
