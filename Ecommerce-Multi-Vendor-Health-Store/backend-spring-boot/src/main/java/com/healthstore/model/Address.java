package com.healthstore.model;

import jakarta.persistence.*;
import lombok.Data;

/**
 * The Address entity represents a user's address.
 * It is mapped to the 'addresses' table in the database.
 * This entity has a Many-to-One relationship with the User entity, as
 * an address belongs to one user.
 */
@Entity
@Table(name = "addresses")
@Data
public class Address {

    /**
     * Unique identifier for the address.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The user to whom this address belongs.
     * @ManyToOne annotation defines the many-to-one relationship.
     * @JoinColumn(name = "user_id") specifies the foreign key column in the
     * 'addresses' table that links to the 'users' table.
     */
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String streetAddress;
    private String city;
    private String state;
    private String pinCode;
    private String mobile;
}
