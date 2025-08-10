package com.healthstore.service;

import com.healthstore.model.Category;
import com.healthstore.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class for handling Category-related business logic.
 * This class interacts with the CategoryRepository to perform
 * operations like creating and fetching categories.
 */
@Service
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
     * Finds a category by its unique ID.
     * @param id The ID of the category to find.
     * @return An Optional containing the category if found, or empty if not found.
     */
    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }
}
