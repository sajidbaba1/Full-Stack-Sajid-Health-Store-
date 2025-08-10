package com.healthstore.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The User entity represents a user in the e-commerce application.
 * It is mapped to the 'users' table in the database.
 * We use Lombok's @Data annotation to automatically generate getters, setters, toString,
 * equals, and hashCode methods, reducing boilerplate code.
 *
 * This entity is a foundational piece of our system, managing user details,
 * authentication, and relationships with other entities like Address and Order.
 */
@Entity
@Table(name = "users")
@Data
public class User {

    /**
     * Unique identifier for the user.
     * The @Id annotation marks this field as the primary key.
     * @GeneratedValue(strategy = GenerationType.IDENTITY) configures MySQL to
     * automatically generate a unique ID for each new user record.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String mobile;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    /**
     * The date and time the user account was created.
     */
    private LocalDateTime createdAt;

    /**
     * One-to-Many relationship with the Address entity.
     * A user can have multiple addresses. The 'mappedBy' attribute indicates that the
     * 'user' field in the Address entity is the owner of the relationship.
     * 'cascade = CascadeType.ALL' ensures that any operation on the User entity (e.g.,
     * deleting a user) will also affect the related Address entities.
     * 'orphanRemoval = true' ensures that if an address is removed from the user's list,
     * it is also deleted from the database.
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Address> addresses = new ArrayList<>();
    
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Cart cart;
}
