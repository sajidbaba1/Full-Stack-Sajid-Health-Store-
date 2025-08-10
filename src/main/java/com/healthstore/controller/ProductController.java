package com.healthstore.controller;

import com.healthstore.dto.ProductDTO;
import com.healthstore.model.Product;
import com.healthstore.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing products.
 * It provides endpoints for creating, fetching, and updating products.
 * Access to create/update products is restricted to sellers and administrators.
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Endpoint to create a new product using a DTO.
     * This endpoint is only accessible to users with 'ROLE_SELLER' or 'ROLE_ADMIN'.
     * @param productDTO The product data transfer object from the request body.
     * @return A response entity with the created product or an error message.
     */
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    @PostMapping
    public ResponseEntity<?> createProduct(@Valid @RequestBody ProductDTO productDTO) {
        try {
            Product createdProduct = productService.createProduct(productDTO);
            return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Endpoint to get a list of all products.
     * This endpoint is accessible to all users without authentication.
     * @return A response entity with a list of all products.
     */
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    /**
     * Endpoint to get a single product by its ID.
     * @param id The ID of the product.
     * @return A response entity with the product or a 'Not Found' status.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Optional<Product> product = productService.getProductById(id);
        return product.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                      .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
