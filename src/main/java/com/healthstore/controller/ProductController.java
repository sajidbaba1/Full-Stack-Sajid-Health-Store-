package com.healthstore.controller;

import com.healthstore.dto.ProductDTO;
import com.healthstore.dto.ProductResponseDTO;
import com.healthstore.model.Product;
import com.healthstore.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
     * Retrieves a paginated list of all products.
     *
     * @param page The page number (0-based).
     * @param size The number of items per page.
     * @param sortBy The field to sort by.
     * @param sortOrder The sort order (asc/desc).
     * @return A ResponseEntity containing a page of products.
     */
    @GetMapping
    public ResponseEntity<Page<ProductResponseDTO>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder) {
        
        Sort.Direction direction = sortOrder.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        Page<Product> products = productService.getAllProducts(pageable);
        Page<ProductResponseDTO> responseDTOs = products.map(ProductResponseDTO::fromProduct);
        return new ResponseEntity<>(responseDTOs, HttpStatus.OK);
    }

    /**
     * Endpoint to get a single product by its ID.
     * @param id The ID of the product.
     * @return A response entity with the product or a 'Not Found' status.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable Long id) {
        return productService.getProductById(id)
                .map(product -> new ResponseEntity<>(ProductResponseDTO.fromProduct(product), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * Endpoint to update an existing product.
     * This endpoint is only accessible to users with 'ROLE_SELLER' or 'ROLE_ADMIN'.
     * @param id The ID of the product to update.
     * @param productDTO The updated product data.
     * @return A response entity with the updated product or an error status.
     */
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductDTO productDTO) {
        try {
            Product updatedProduct = productService.updateProduct(id, productDTO);
            return new ResponseEntity<>(ProductResponseDTO.fromProduct(updatedProduct), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Endpoint to delete a product by its ID.
     * This endpoint is only accessible to users with 'ROLE_SELLER' or 'ROLE_ADMIN'.
     * @param id The ID of the product to delete.
     * @return A response entity with a success message or an error status.
     */
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return new ResponseEntity<>("Product deleted successfully", HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Endpoint to search for products by name with pagination.
     * This endpoint is accessible to all users without authentication.
     *
     * @param query The search query string.
     * @param page The page number (0-based, default is 0).
     * @param size The number of items per page (default is 10, max is 50).
     * @param sortBy The field to sort by (default is 'relevance' which is based on name matching).
     * @param direction The sort direction ('asc' or 'desc', default is 'asc').
     * @return A response entity with a page of products matching the search query and pagination metadata.
     */
    @GetMapping("/search")
    public ResponseEntity<Page<ProductResponseDTO>> searchProducts(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        
        // Validate page and size parameters
        if (page < 0) {
            page = 0;
        }
        if (size <= 0 || size > 50) {
            size = 10;
        }
        
        // Create sort direction
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? 
                Sort.Direction.DESC : Sort.Direction.ASC;
        
        // Create pageable object
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        
        // Search products with pagination and map to DTOs
        Page<Product> products = productService.findByNameContainingIgnoreCase(query, pageable);
        Page<ProductResponseDTO> responseDTOs = products.map(ProductResponseDTO::fromProduct);
        
        return new ResponseEntity<>(responseDTOs, HttpStatus.OK);
    }

    /**
     * Endpoint to get products within a price range with pagination.
     * This endpoint is accessible to all users without authentication.
     *
     * @param minPrice The minimum price (inclusive).
     * @param maxPrice The maximum price (inclusive).
     * @param page The page number (0-based, default is 0).
     * @param size The number of items per page (default is 10, max is 50).
     * @param sortBy The field to sort by (default is 'price').
     * @param direction The sort direction ('asc' or 'desc', default is 'asc').
     * @return A response entity with a page of products within the price range and pagination metadata.
     */
    @GetMapping("/price-range")
    public ResponseEntity<Page<ProductResponseDTO>> getProductsByPriceRange(
            @RequestParam double minPrice,
            @RequestParam double maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "price") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        
        // Validate price range
        if (minPrice < 0 || maxPrice < 0 || minPrice > maxPrice) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        
        // Validate page and size parameters
        if (page < 0) {
            page = 0;
        }
        if (size <= 0 || size > 50) {
            size = 10;
        }
        
        // Create sort direction
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? 
                Sort.Direction.DESC : Sort.Direction.ASC;
        
        // Create pageable object
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        
        // Get products by price range with pagination and map to DTOs
        Page<Product> products = productService.findByPriceBetween(minPrice, maxPrice, pageable);
        Page<ProductResponseDTO> responseDTOs = products.map(ProductResponseDTO::fromProduct);
        
        return new ResponseEntity<>(responseDTOs, HttpStatus.OK);
    }

    /**
     * Endpoint to get a paginated list of products by category ID.
     * This endpoint is accessible to all users without authentication.
     *
     * @param categoryId The ID of the category.
     * @param page The page number (0-based, default is 0).
     * @param size The number of items per page (default is 10, max is 50).
     * @param sortBy The field to sort by (default is 'id').
     * @param direction The sort direction ('asc' or 'desc', default is 'asc').
     * @return A response entity with a page of products in the specified category and pagination metadata.
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Page<ProductResponseDTO>> getProductsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        
        // Validate page and size parameters
        if (page < 0) {
            page = 0;
        }
        if (size <= 0 || size > 50) {
            size = 10;
        }
        
        // Create sort direction
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? 
                Sort.Direction.DESC : Sort.Direction.ASC;
        
        // Create pageable object
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        
        // Get products by category with pagination and map to DTOs
        Page<Product> products = productService.findByCategoryId(categoryId, pageable);
        Page<ProductResponseDTO> responseDTOs = products.map(ProductResponseDTO::fromProduct);
        
        return new ResponseEntity<>(responseDTOs, HttpStatus.OK);
    }
}
