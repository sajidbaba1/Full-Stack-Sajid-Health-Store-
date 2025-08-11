package com.healthstore.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

/**
 * The Rating entity represents a user's rating of a product.
 * It stores the rating value and maintains relationships with User and Product.
 */
@Entity
@Table(name = "ratings")
@Getter
@Setter
@Audited
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Min(1)
    @Max(5)
    private int value; // Rating from 1 to 5

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @NotAudited
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotAudited
    private User user;

    /**
     * Gets the rating value.
     * This is an alias for getValue() to maintain compatibility with existing code.
     * @return The rating value.
     */
    @Transient
    public int getRatingValue() {
        return value;
    }

    /**
     * Sets the rating value.
     * This is an alias for setValue() to maintain compatibility with existing code.
     * @param ratingValue The rating value.
     */
    @Transient
    public void setRatingValue(int ratingValue) {
        this.value = ratingValue;
    }

    // Manual getter and setter methods to ensure compilation works when Lombok fails
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
