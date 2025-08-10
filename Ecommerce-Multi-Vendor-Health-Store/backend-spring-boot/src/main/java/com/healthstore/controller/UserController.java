package com.healthstore.controller;

import com.healthstore.dto.PasswordUpdateDTO;
import com.healthstore.dto.UserUpdateDTO;
import com.healthstore.model.User;
import com.healthstore.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final OrderService orderService;

    public UserController(UserService userService, OrderService orderService) {
        this.userService = userService;
        this.orderService = orderService;
    }

    /**
     * Get paginated orders for the authenticated user.
     * @param userDetails The authenticated user details
     * @param pageable The pagination information
     * @return A page of orders for the user
     */
    @GetMapping("/orders")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<Order>> getUserOrders(
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(size = 10) Pageable pageable) {
        
        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
                
        Page<Order> orders = orderService.getOrdersByUser(user, pageable);
        return ResponseEntity.ok(orders);
    }

    /**
     * Endpoint to get the profile of the authenticated user.
     * Accessible only to authenticated users.
     * @param userDetails The details of the authenticated user.
     * @return A response entity with the user's profile.
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/profile")
    public ResponseEntity<User> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        Optional<User> user = userService.findUserByEmail(userDetails.getUsername());
        return user.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                   .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * Endpoint to update the profile of the authenticated user.
     * @param userDetails The details of the authenticated user.
     * @param userUpdateDTO The DTO containing the updated user data.
     * @return A response entity with the updated user or an error.
     */
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@AuthenticationPrincipal UserDetails userDetails,
                                           @Valid @RequestBody UserUpdateDTO userUpdateDTO) {
        try {
            User updatedUser = userService.updateUserProfile(userDetails.getUsername(), userUpdateDTO);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    /**
     * Endpoint to update the password of the authenticated user.
     * @param userDetails The details of the authenticated user.
     * @param passwordUpdateDTO The DTO containing the current and new password.
     * @return A response entity with a success message or an error.
     */
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/password")
    public ResponseEntity<?> updatePassword(@AuthenticationPrincipal UserDetails userDetails,
                                          @Valid @RequestBody PasswordUpdateDTO passwordUpdateDTO) {
        try {
            userService.updateUserPassword(
                userDetails.getUsername(),
                passwordUpdateDTO.getCurrentPassword(),
                passwordUpdateDTO.getNewPassword()
            );
            return new ResponseEntity<>("Password updated successfully", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
