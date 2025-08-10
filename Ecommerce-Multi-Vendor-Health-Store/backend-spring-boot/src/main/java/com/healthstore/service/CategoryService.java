package com.healthstore.service;

import com.healthstore.model.Category;
import com.healthstore.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service class for handling Category-related business logic.
 * This class interacts with the CategoryRepository to perform
 * operations like creating, updating, and fetching categories.
 */
@Service
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * Creates a new category in the database.
     * @param category The category object to be saved.
     * @return The saved category.
     */
    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    /**
     * Retrieves all categories from the database.
     * @return A list of all categories.
     */
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    /**
     * Finds a category by its ID.
     * @param id The ID of the category to find.
     * @return An Optional containing the category if found.
     */
    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    /**
     * Finds all top-level categories (categories with no parent).
     * @return A list of top-level categories.
     */
    public List<Category> getTopLevelCategories() {
        return categoryRepository.findByParentIsNull();
    }

    /**
     * Finds all active categories.
     * @return A list of active categories.
     */
    public List<Category> getActiveCategories() {
        return categoryRepository.findByActive(true);
    }

    /**
     * Updates an existing category.
     * @param id The ID of the category to update.
     * @param categoryDetails The category details to update.
     * @return The updated category.
     */
    public Category updateCategory(Long id, Category categoryDetails) {
        return categoryRepository.findById(id).map(category -> {
            category.setName(categoryDetails.getName());
            category.setDescription(categoryDetails.getDescription());
            category.setImageUrl(categoryDetails.getImageUrl());
            category.setActive(categoryDetails.isActive());
            category.setParent(categoryDetails.getParent());
            return categoryRepository.save(category);
        }).orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
    }
}
