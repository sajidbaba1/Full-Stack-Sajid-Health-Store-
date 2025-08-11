package com.healthstore.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for authentication responses.
 * It contains the JWT token that is returned after a successful login.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String jwt;

    // Manual getter and setter methods to ensure compilation works when Lombok fails
    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }
}
