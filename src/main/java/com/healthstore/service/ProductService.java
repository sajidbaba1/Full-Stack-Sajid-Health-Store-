package com.healthstore.service;

import com.healthstore.dto.ProductDTO;
import com.healthstore.model.Category;
import com.healthstore.model.Product;
import com.healthstore.repository.ProductRepository;
import org.springframework.stereotype.Service;

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
}
