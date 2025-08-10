package com.healthstore.model;

import jakarta.persistence.*;
import lombok.Data;

/**
 * The Product entity represents a product for sale in the health store.
 * It is mapped to the 'products' table.
 */
@Entity
@Table(name = "products")
@Data
public class Product {

    /**
     * Unique identifier for the product.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private double price;
    private int stock;
    private String imageUrl;

    /**
     * Many-to-One relationship with the Category entity.
     * A product belongs to one category.
     * @JoinColumn specifies the foreign key column.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
}
