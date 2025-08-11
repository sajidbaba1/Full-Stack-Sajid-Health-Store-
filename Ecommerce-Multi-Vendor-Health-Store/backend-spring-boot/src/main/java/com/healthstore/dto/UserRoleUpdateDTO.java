package com.healthstore.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Data Transfer Object for updating a user's role.
 * This DTO is used when an admin needs to update a user's role.
 */
@Data
public class UserRoleUpdateDTO {
    
    /**
     * The name of the role to assign to the user.
     * This field is required and cannot be blank.
     */
    @NotBlank(message = "Role name is required")
    private String roleName;

    // Manual getter and setter methods to ensure compilation works when Lombok fails
    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}
