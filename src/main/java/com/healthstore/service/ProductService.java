package com.healthstore.service;

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

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Creates a new product in the database.
     * @param product The product object to be saved.
     * @return The saved product.
     */
    public Product createProduct(Product product) {
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
