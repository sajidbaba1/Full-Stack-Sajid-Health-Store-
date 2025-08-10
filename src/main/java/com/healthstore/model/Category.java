package com.healthstore.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;

/**
 * The Category entity represents a product category in the e-commerce application.
 * It is mapped to the 'categories' table.
 */
@Entity
@Table(name = "categories")
@Data
public class Category {

    /**
     * Unique identifier for the category.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    /**
     * One-to-Many relationship with the Product entity.
     * A category can have multiple products. The 'mappedBy' attribute indicates
     * that the 'category' field in the Product entity is the owner of the relationship.
     */
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private Set<Product> products;
}
