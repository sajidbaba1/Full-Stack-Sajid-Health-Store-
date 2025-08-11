package com.healthstore.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;

/**
 * The Product entity represents a product in the e-commerce store.
 * It includes details like name, description, price, and stock information.
 * Products are associated with categories and can have multiple images.
 */
@Entity
@Table(name = "products")
@Data
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference("product-variants")
    private List<ProductVariant> variants = new ArrayList<>();

    private String imageUrl;
    
    @ElementCollection
    @CollectionTable(name = "product_images", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "image_url")
    private List<String> additionalImages = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User seller;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference("product-ratings")
    private List<Rating> ratings = new ArrayList<>();
    
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference("product-reviews")
    private List<Review> reviews = new ArrayList<>();
    
    @Transient
    private Double averageRating;

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Gets the price of the product from the first variant.
     * @return The price as a BigDecimal, or BigDecimal.ZERO if no variants exist.
     */
    @Transient
    public BigDecimal getPrice() {
        if (variants != null && !variants.isEmpty()) {
            return BigDecimal.valueOf(variants.get(0).getPrice());
        }
        return BigDecimal.ZERO;
    }
    
    /**
     * Gets the stock quantity from the first variant.
     * @return The stock quantity, or 0 if no variants exist.
     */
    @Transient
    public int getStock() {
        if (variants != null && !variants.isEmpty()) {
            return variants.get(0).getStockQuantity();
        }
        return 0;
    }
    
    /**
     * Sets the stock quantity for the first variant.
     * @param stock The stock quantity to set.
     */
    @Transient
    public void setStock(int stock) {
        if (variants != null && !variants.isEmpty()) {
            variants.get(0).setStockQuantity(stock);
        }
    }
    
    /**
     * Gets the categories associated with this product.
     * @return List of categories.
     */
    @Transient
    public List<Category> getCategories() {
        return category != null ? List.of(category) : List.of();
    }

    // Manual getter and setter methods to ensure compilation works when Lombok fails
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<ProductVariant> getVariants() {
        return variants;
    }

    public void setVariants(List<ProductVariant> variants) {
        this.variants = variants;
    }
}
