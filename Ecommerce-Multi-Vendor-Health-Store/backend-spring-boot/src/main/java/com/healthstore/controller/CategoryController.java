package com.healthstore.controller;

import com.healthstore.model.Category;
import com.healthstore.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing product categories.
 * It provides endpoints for creating and fetching categories.
 * Access to create categories is restricted to administrators.
 */
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * Endpoint to create a new category.
     * This endpoint is only accessible to users with the 'ROLE_ADMIN' role.
     * @param category The category object from the request body.
     * @return A response entity with the created category.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Category> createCategory(@RequestBody Category category) {
        Category createdCategory = categoryService.createCategory(category);
        return new ResponseEntity<>(createdCategory, HttpStatus.CREATED);
    }

    /**
     * Endpoint to get all categories.
     * This endpoint is accessible to all authenticated users.
     * @return A response entity with a list of all categories.
     */
    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    /**
     * Endpoint to get all top-level categories.
     * @return A response entity with a list of top-level categories.
     */
    @GetMapping("/top-level")
    public ResponseEntity<List<Category>> getTopLevelCategories() {
        List<Category> categories = categoryService.getTopLevelCategories();
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    /**
     * Endpoint to get all active categories.
     * @return A response entity with a list of active categories.
     */
    @GetMapping("/active")
    public ResponseEntity<List<Category>> getActiveCategories() {
        List<Category> categories = categoryService.getActiveCategories();
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    /**
     * Endpoint to get a category by its ID.
     * @param id The ID of the category to retrieve.
     * @return A response entity with the category or a not found status.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
        return categoryService.getCategoryById(id)
                .map(category -> new ResponseEntity<>(category, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * Endpoint to update a category.
     * This endpoint is only accessible to users with the 'ROLE_ADMIN' role.
     * @param id The ID of the category to update.
     * @param categoryDetails The category details to update.
     * @return A response entity with the updated category or a not found status.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable Long id, @RequestBody Category categoryDetails) {
        try {
            Category updatedCategory = categoryService.updateCategory(id, categoryDetails);
            return new ResponseEntity<>(updatedCategory, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
