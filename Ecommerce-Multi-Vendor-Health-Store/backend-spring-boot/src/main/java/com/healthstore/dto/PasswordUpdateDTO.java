package com.healthstore.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for updating a user's password.
 * This DTO is used to validate and transfer password update requests.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
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
    @Size(min = 8, message = "New password must be at least 8 characters long")
    private String newPassword;

    // Manual getter and setter methods to ensure compilation works when Lombok fails
    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
