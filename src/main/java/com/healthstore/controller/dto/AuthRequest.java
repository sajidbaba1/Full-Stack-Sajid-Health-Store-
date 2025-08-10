package com.healthstore.dto;

import lombok.Data;

/**
 * DTO for authentication requests.
 * It contains the email and password fields for user login.
 */
@Data
public class AuthRequest {
    private String email;
    private String password;
}