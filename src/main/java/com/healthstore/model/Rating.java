package com.healthstore.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entity representing a product rating.
 * Stores the rating value and maintains relationships with User and Product.
 */
@Data
@Entity
@Table(name = "ratings", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "product_id"})
})
public class Rating {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private int value;  // Rating value from 1 to 5
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructor for JPA
    protected Rating() {}
    
    public Rating(int value, Product product, User user) {
        if (value < 1 || value > 5) {
            throw new IllegalArgumentException("Rating value must be between 1 and 5");
        }
        this.value = value;
        this.product = product;
        this.user = user;
    }
    
    // Custom setter to validate rating value
    public void setValue(int value) {
        if (value < 1 || value > 5) {
            throw new IllegalArgumentException("Rating value must be between 1 and 5");
        }
        this.value = value;
    }
}
