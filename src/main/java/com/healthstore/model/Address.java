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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "street", nullable = false)
    private String street;
    
    @Column(nullable = false)
    private String city;
    
    @Column(nullable = false)
    private String state;
    
    @Column(name = "postal_code", nullable = false)
    private String postalCode;
    
    @Column(nullable = false)
    private String country;
    
    @Column(name = "phone_number")
    private String phoneNumber;
    
    @Column(name = "is_default")
    private boolean isDefault = false;
    
    @Column(name = "address_type")
    private String addressType; // e.g., "HOME", "WORK", "BILLING", "SHIPPING"
    
    // Explicit getters and setters for fields that might have naming conflicts with Lombok
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public boolean isDefault() {
        return isDefault;
    }
    
    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }
    
    public String getAddressType() {
        return addressType;
    }
    
    public void setAddressType(String addressType) {
        this.addressType = addressType;
    }
    
    // Convenience method to check if this is the default address
    public boolean getIsDefault() {
        return isDefault;
    }
    
    // Convenience method to set the default status
    public void setIsDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }
}