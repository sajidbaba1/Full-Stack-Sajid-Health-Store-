package com.healthstore.controller;

import com.healthstore.dto.PasswordUpdateRequest;
import com.healthstore.exception.InvalidPasswordException;
import com.healthstore.exception.ResourceNotFoundException;
import com.healthstore.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST controller for handling password-related operations.
 */
@RestController
@RequestMapping("/api/users")
public class UserPasswordController {

    private final UserService userService;

    @Autowired
    public UserPasswordController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Updates the current user's password.
     * 
     * @param userDetails The authenticated user.
     * @param request The password update request containing current and new passwords.
     * @return A success message if the password was updated.
     */
    @PostMapping("/me/password")
    public ResponseEntity<?> updatePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody PasswordUpdateRequest request) {
        
        try {
            // Get the current user's ID
            Long userId = userService.findUserByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"))
                    .getId();
            
            // Update the password
            userService.updatePassword(userId, request);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Password updated successfully");
            
            return ResponseEntity.ok(response);
            
        } catch (InvalidPasswordException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Admin endpoint to reset a user's password.
     * 
     * @param userId The ID of the user whose password to reset.
     * @param newPassword The new password to set.
     * @return A success message if the password was reset.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{userId}/reset-password")
    public ResponseEntity<?> resetPassword(
            @PathVariable Long userId,
            @RequestParam String newPassword) {
        
        try {
            userService.resetPassword(userId, newPassword);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Password reset successfully");
            
            return ResponseEntity.ok(response);
            
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred while resetting the password"));
        }
    }
}
