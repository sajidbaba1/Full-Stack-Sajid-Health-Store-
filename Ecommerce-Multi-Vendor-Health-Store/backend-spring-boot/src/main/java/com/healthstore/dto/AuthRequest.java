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

    // Manual getter and setter methods to ensure compilation works when Lombok fails
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
