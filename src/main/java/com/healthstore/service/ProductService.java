package com.healthstore.service;

import com.healthstore.dto.ProductDTO;
import com.healthstore.model.Category;
import com.healthstore.model.Product;
import com.healthstore.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service class for handling Product-related business logic.
 * This class interacts with the ProductRepository to perform
 * operations like creating, fetching, and updating products.
 */
@Service
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
        Optional<Category> optionalCategory = categoryService.getCategoryById(productDTO.getCategoryId());
        if (optionalCategory.isEmpty()) {
            throw new RuntimeException("Category not found with ID: " + productDTO.getCategoryId());
        }
        Category category = optionalCategory.get();
        Product product = new Product();
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setStock(productDTO.getStock());
        product.setImageUrl(productDTO.getImageUrl());
        product.setCategory(category);

        return productRepository.save(product);
    }

    /**
     * Retrieves all products from the database.
     * @return A list of all products.
     */
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /**
     * Finds a product by its unique ID.
     * @param id The ID of the product.
     * @return An Optional containing the product if found.
     */
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    /**
     * Updates an existing product with new data.
     * @param id The ID of the product to update.
     * @param productDTO The DTO containing the updated product data.
     * @return The updated product.
     * @throws RuntimeException if the product or category is not found.
     */
    @Transactional
    public Product updateProduct(Long id, ProductDTO productDTO) {
        // Check if product exists
        Product existingProduct = productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));
        
        // Check if category exists if it's being updated
        if (productDTO.getCategoryId() != null) {
            Category category = categoryService.getCategoryById(productDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found with ID: " + productDTO.getCategoryId()));
            existingProduct.setCategory(category);
        }
        
        // Update product fields if they are not null in the DTO
        if (productDTO.getName() != null) {
            existingProduct.setName(productDTO.getName());
        }
        if (productDTO.getDescription() != null) {
            existingProduct.setDescription(productDTO.getDescription());
        }
        if (productDTO.getPrice() > 0) {
            existingProduct.setPrice(productDTO.getPrice());
        }
        if (productDTO.getStock() >= 0) {
            existingProduct.setStock(productDTO.getStock());
        }
        if (productDTO.getImageUrl() != null) {
            existingProduct.setImageUrl(productDTO.getImageUrl());
        }
        
        return productRepository.save(existingProduct);
    }
    
    /**
     * Deletes a product by its ID.
     * @param id The ID of the product to delete.
     * @throws RuntimeException if the product is not found or cannot be deleted.
     */
    @Transactional
    public void deleteProduct(Long id) {
        // Check if product exists
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found with ID: " + id);
        }
        
        // Check if product is associated with any order
        // This is a simplified check - in a real application, you might want to handle this differently
        // For example, you might want to set a flag to mark the product as inactive instead of deleting it
        
        productRepository.deleteById(id);
    }
}
