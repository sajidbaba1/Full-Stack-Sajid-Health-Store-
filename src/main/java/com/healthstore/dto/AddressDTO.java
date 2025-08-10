package com.healthstore.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * Data Transfer Object for Address entity.
 * Used to transfer address data between the client and server.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AddressDTO {
    private Long id;
    private Long userId;  // ID of the user this address belongs to
    private String street;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private String phoneNumber;
    private boolean isDefault = false;
    private String addressType;  // e.g., "HOME", "WORK", "BILLING", "SHIPPING"
    
    // Additional fields that might be useful for the client
    private String fullAddress;
    
    /**
     * Gets the full address as a formatted string.
     * @return A formatted string representation of the full address.
     */
    public String getFullAddress() {
        if (fullAddress != null) {
            return fullAddress;
        }
        
        StringBuilder sb = new StringBuilder();
        if (street != null) sb.append(street).append(", ");
        if (city != null) sb.append(city).append(", ");
        if (state != null) sb.append(state).append(" ");
        if (postalCode != null) sb.append(postalCode).append(", ");
        if (country != null) sb.append(country);
        
        // Remove trailing comma and space if they exist
        String result = sb.toString().trim();
        if (result.endsWith(",")) {
            result = result.substring(0, result.length() - 1);
        }
        
        return result.trim();
    }
}
