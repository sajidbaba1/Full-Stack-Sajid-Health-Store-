package com.healthstore.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "addresses")
@Data
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String street;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    
    private String phoneNumber;
    private boolean defaultAddress;  // Changed from isDefault to match getter/setter pattern
    
    // Explicit getter and setter for defaultAddress to match the field name
    public boolean isDefault() {
        return defaultAddress;
    }
    
    public void setDefault(boolean isDefault) {
        this.defaultAddress = isDefault;
    }
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    @PrePersist
    public void prePersist() {
        if (this.defaultAddress) {
            // Ensure only one default address per user
            if (user != null) {
                user.getAddresses().stream()
                    .filter(a -> !a.equals(this) && a.isDefault())
                    .forEach(a -> a.setDefault(false));
            }
        }
    }

    // Manual getter and setter methods to ensure compilation works when Lombok fails
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
