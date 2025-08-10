package com.healthstore.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserRoleUpdateDTO {
    @NotBlank(message = "Role name is required")
    private String roleName;
}
