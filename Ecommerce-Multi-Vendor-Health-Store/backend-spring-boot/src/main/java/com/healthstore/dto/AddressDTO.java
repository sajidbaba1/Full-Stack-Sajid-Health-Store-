package com.healthstore.dto;

import lombok.Data;

/**
 * Data Transfer Object for Address entity.
 * Contains address information for shipping and billing purposes.
 */
@Data
public class AddressDTO {
    private Long id;
    private String recipientName;
    private String street;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private String phoneNumber;
    private boolean isDefault;
}
