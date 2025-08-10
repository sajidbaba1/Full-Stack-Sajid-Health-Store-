package com.healthstore.controller.admin;

import com.healthstore.dto.UserRoleUpdateDTO;
import com.healthstore.dto.UserUpdateDTO;
import com.healthstore.model.User;
import com.healthstore.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * AdminController handles administrative operations.
 * All endpoints in this controller require the 'ROLE_ADMIN' authority.
 */
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Retrieves all users in the system.
     * @return A list of all users with an OK status.
     */
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.findAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    /**
     * Updates a user's profile information by their ID.
     * @param id The ID of the user to update.
     * @param userUpdateDTO The DTO containing the updated user data.
     * @return The updated user with an OK status, or NOT_FOUND if the user doesn't exist.
     */
    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateDTO userUpdateDTO) {
        try {
            User updatedUser = userService.updateUser(id, userUpdateDTO);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Updates a user's role.
     * @param id The ID of the user to update.
     * @param roleUpdateDTO The DTO containing the new role name.
     * @return The updated user with an OK status, or NOT_FOUND if the user or role doesn't exist.
     */
    @PutMapping("/users/{id}/role")
    public ResponseEntity<?> updateUserRole(
            @PathVariable Long id,
            @Valid @RequestBody UserRoleUpdateDTO roleUpdateDTO) {
        try {
            User updatedUser = userService.updateUserRole(id, roleUpdateDTO.getRoleName());
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
