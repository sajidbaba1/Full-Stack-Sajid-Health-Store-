package com.healthstore.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.envers.Audited;

/**
 * The Rating entity represents a user's rating of a product.
 * It stores the rating value and maintains relationships with User and Product.
 */
@Entity
@Table(name = "ratings")
@Data
@Audited
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int ratingValue;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
