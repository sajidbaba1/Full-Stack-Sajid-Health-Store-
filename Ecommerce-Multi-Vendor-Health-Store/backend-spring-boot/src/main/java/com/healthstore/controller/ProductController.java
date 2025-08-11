package com.healthstore.controller;

import com.healthstore.dto.ProductDTO;
import com.healthstore.dto.ProductResponseDTO;
import com.healthstore.model.Product;
import com.healthstore.dto.SearchFilterDTO;
import com.healthstore.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.stream.Collectors;

import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    @PostMapping
    public ResponseEntity<?> createProduct(@Valid @RequestBody ProductDTO productDTO) {
        try {
            Product product = productService.createProduct(productDTO);
            return new ResponseEntity<>(product, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<Page<ProductResponseDTO>> getAllProducts(
            @PageableDefault(size = 10) Pageable pageable) {
        Page<Product> products = productService.getAllProducts(pageable);
        Page<ProductResponseDTO> productDTOs = products.map(this::convertToProductResponseDTO);
        return new ResponseEntity<>(productDTOs, HttpStatus.OK);
    }

    @GetMapping("/page")
    public ResponseEntity<Page<ProductResponseDTO>> getProductsPage(
            @PageableDefault(size = 10) Pageable pageable) {
        Page<Product> products = productService.getAllProducts(pageable);
        Page<ProductResponseDTO> dtoPage = products.map(this::convertToProductResponseDTO);
        return new ResponseEntity<>(dtoPage, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable Long id) {
        Optional<Product> product = productService.getProductById(id);
        return product.map(value -> new ResponseEntity<>(convertToProductResponseDTO(value), HttpStatus.OK))
                     .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductDTO productDTO) {
        try {
            Product updatedProduct = productService.updateProduct(id, productDTO);
            return new ResponseEntity<>(convertToProductResponseDTO(updatedProduct), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return new ResponseEntity<>("Product deleted successfully", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
    
    /**
     * Endpoint to search for products with optional name and category filters
     * @param name The product name to search for (optional).
     * @param categoryId The category ID to filter by (optional).
     * @return A response entity with a list of matching products.
     */
    /**
     * Endpoint to search for products with optional name and category filters.
     * @param name The product name to search for (optional).
     * @param categoryId The category ID to filter by (optional).
     * @return A response entity with a list of matching products.
     */
    @GetMapping("/search")
    public ResponseEntity<List<ProductResponseDTO>> searchProducts(@RequestParam(required = false) String name,
                                                                 @RequestParam(required = false) Long categoryId) {
        List<Product> products = productService.searchProducts(name, categoryId);
        List<ProductResponseDTO> dtos = products.stream()
                .map(this::convertToProductResponseDTO)
                .collect(Collectors.toList());
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }
    
    @GetMapping("/advanced-search")
    public ResponseEntity<Page<ProductResponseDTO>> advancedSearch(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Long categoryId,
            @PageableDefault(size = 10) Pageable pageable) {
        
        Page<Product> products = productService.advancedSearch(name, minPrice, maxPrice, categoryId, pageable);
            
        Page<ProductResponseDTO> dtoPage = products.map(this::convertToProductResponseDTO);
        return new ResponseEntity<>(dtoPage, HttpStatus.OK);
    }
    
    /**
     * Converts a Product entity to a ProductResponseDTO.
     * @param product The product to convert.
     * @return The converted ProductResponseDTO.
     */
    private ProductResponseDTO convertToProductResponseDTO(Product product) {
        ProductResponseDTO dto = new ProductResponseDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice().doubleValue());
        dto.setStock(product.getStock());
        dto.setImageUrl(product.getImageUrl());
        
        if (product.getCategory() != null) {
            dto.setCategoryId(product.getCategory().getId());
            dto.setCategoryName(product.getCategory().getName());
        }
        
        return dto;
    }
    
    /**
     * Get featured products (newest 10)
     */
    @GetMapping("/featured")
    public ResponseEntity<List<Product>> getFeaturedProducts() {
        List<Product> featuredProducts = productService.getFeaturedProducts();
        return new ResponseEntity<>(featuredProducts, HttpStatus.OK);
    }
}
