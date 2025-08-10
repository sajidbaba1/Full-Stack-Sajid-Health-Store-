package com.healthstore.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Data Transfer Object for updating a user's password.
 * This DTO is used to validate and transfer password update requests.
 */
@Data
public class PasswordUpdateDTO {
    
    /**
     * The user's current password.
     * This field is required and cannot be blank.
     */
    @NotBlank(message = "Current password is required")
    private String currentPassword;
    
    /**
     * The user's new password.
     * This field is required and cannot be blank.
     * In a real application, you would want to add additional validation
     * for password strength (e.g., minimum length, special characters, etc.).
     */
    @NotBlank(message = "New password is required")
    private String newPassword;
}
