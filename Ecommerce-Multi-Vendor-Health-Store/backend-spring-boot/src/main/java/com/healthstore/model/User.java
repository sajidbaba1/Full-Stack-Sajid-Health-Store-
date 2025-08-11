package com.healthstore.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

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
@Audited
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
    private String phoneNumber;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @NotAudited
    private Set<Role> roles = new HashSet<>();

    /**
     * The date and time the user account was created.
     */
    private LocalDateTime createdAt;

    /**
     * The date and time when the user last logged in.
     */
    private LocalDateTime lastLoginDate;

    private String provider; // google, facebook, etc.
    private String providerId; // unique ID from provider

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    // Manual getter and setter methods to ensure compilation works when Lombok fails
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }
    
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    
    public Set<Role> getRoles() { return roles; }
    public void setRoles(Set<Role> roles) { this.roles = roles; }
    
    public List<Address> getAddresses() { return addresses; }
    public void setAddresses(List<Address> addresses) { this.addresses = addresses; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getLastLoginDate() { return lastLoginDate; }
    public void setLastLoginDate(LocalDateTime lastLoginDate) { this.lastLoginDate = lastLoginDate; }

    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }
    
    public String getProviderId() { return providerId; }
    public void setProviderId(String providerId) { this.providerId = providerId; }

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
    @NotAudited
    private List<Address> addresses = new ArrayList<>();
    
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Cart cart;
    
    /**
     * Sets the user's role by clearing existing roles and adding the new one.
     * @param roleName The name of the role to set.
     */
    public void setRole(String roleName) {
        this.roles.clear();
        Role role = new Role();
        role.setName(Role.RoleName.valueOf(roleName));
        this.roles.add(role);
    }
    
    /**
     * Gets the full name of the user by combining first and last name.
     * @return The full name as a string.
     */
    public String getFullName() {
        if (firstName != null && lastName != null) {
            return firstName + " " + lastName;
        } else if (firstName != null) {
            return firstName;
        } else if (lastName != null) {
            return lastName;
        } else {
            return "";
        }
    }
}
