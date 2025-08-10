package com.healthstore.service;

import com.healthstore.dto.UserRoleUpdateDTO;
import com.healthstore.dto.UserUpdateDTO;
import com.healthstore.model.Role;
import com.healthstore.model.User;
import com.healthstore.repository.RoleRepository;
import com.healthstore.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.healthstore.dto.PasswordUpdateRequest;
import com.healthstore.exception.InvalidPasswordException;
import com.healthstore.exception.ResourceNotFoundException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Service class for handling user-related business logic.
 * This class interacts with the UserRepository to perform operations
 * like user registration and retrieval.
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CartService cartService;
    private final RoleRepository roleRepository;

    /**
     * Constructor for UserService.
     * @param userRepository The repository to access user data.
     * @param passwordEncoder The encoder for hashing passwords.
     * @param cartService The service for cart operations.
     */
    public UserService(UserRepository userRepository, 
                      PasswordEncoder passwordEncoder, 
                      CartService cartService,
                      RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.cartService = cartService;
        this.roleRepository = roleRepository;
    }

    /**
     * Registers a new user and creates an associated shopping cart.
     * This method encrypts the user's password using BCrypt and sets the creation timestamp.
     * @param user The user object to be saved.
     * @return The saved user object with an associated cart.
     */
    public User registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setRole("ROLE_USER");
        
        // Save the user first to get the generated ID
        User savedUser = userRepository.save(user);
        
        // Create a new cart for the user
        cartService.createCartForUser(savedUser);
        
        return savedUser;
    }

    /**
     * Finds a user by their email address.
     * @param email The email of the user to find.
     * @return An Optional containing the user if found.
     */
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    /**
     * Updates a user's profile information.
     * 
     * @param userId The ID of the user to update.
     * @param userUpdateDTO The DTO containing the updated user information.
     * @return The updated user.
     * @throws ResourceNotFoundException If the user is not found.
     */
    @Transactional
    public User updateUserProfile(Long userId, UserUpdateDTO userUpdateDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        // Update user fields if they are provided in the DTO
        if (userUpdateDTO.getFullName() != null) {
            user.setFullName(userUpdateDTO.getFullName());
        }
        if (userUpdateDTO.getEmail() != null) {
            user.setEmail(userUpdateDTO.getEmail());
        }
        if (userUpdateDTO.getPhoneNumber() != null) {
            user.setPhoneNumber(userUpdateDTO.getPhoneNumber());
        }
        if (userUpdateDTO.getAddress() != null) {
            user.setAddress(userUpdateDTO.getAddress());
        }
        
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }
    
    /**
     * Updates a user's password.
     * 
     * @param userId The ID of the user whose password to update.
     * @param passwordUpdate The DTO containing the current and new passwords.
     * @return true if the password was updated successfully.
     * @throws ResourceNotFoundException If the user is not found.
     * @throws InvalidPasswordException If the current password is incorrect or the new password is invalid.
     */
    @Transactional
    public boolean updatePassword(Long userId, PasswordUpdateRequest passwordUpdate) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        // Verify current password
        if (!passwordEncoder.matches(passwordUpdate.getCurrentPassword(), user.getPassword())) {
            throw new InvalidPasswordException("Current password is incorrect");
        }
        
        // Validate new password
        if (passwordUpdate.getNewPassword() == null || passwordUpdate.getNewPassword().length() < 8) {
            throw new InvalidPasswordException("New password must be at least 8 characters long");
        }
        
        if (!passwordUpdate.getNewPassword().equals(passwordUpdate.getConfirmPassword())) {
            throw new InvalidPasswordException("New password and confirmation do not match");
        }
        
        // Update the password
        user.setPassword(passwordEncoder.encode(passwordUpdate.getNewPassword()));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        
        return true;
    }
    
    /**
     * Resets a user's password (admin function).
     * 
     * @param userId The ID of the user whose password to reset.
     * @param newPassword The new password to set.
     * @return true if the password was reset successfully.
     * @throws ResourceNotFoundException If the user is not found.
     */
    @Transactional
    public boolean resetPassword(Long userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        
        return true;
    }
    
    /**
     * Finds all users in the system.
     * @return A list of all users.
     */
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }
    
    /**
     * Updates a user's information by their ID.
     * @param id The ID of the user to update.
     * @param userUpdateDTO The DTO containing the updated user data.
     * @return The updated user entity.
     */
    @Transactional
    public User updateUser(Long id, UserUpdateDTO userUpdateDTO) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));
            
        user.setFirstName(userUpdateDTO.getFirstName());
        user.setLastName(userUpdateDTO.getLastName());
        user.setMobile(userUpdateDTO.getMobile());
        
        return userRepository.save(user);
    }
    
    /**
     * Updates a user's role.
     * @param id The ID of the user to update.
     * @param roleName The name of the role to assign to the user.
     * @return The updated user entity.
     */
    @Transactional
    public User updateUserRole(Long id, String roleName) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));
            
        Role role = roleRepository.findByName(roleName)
            .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
            
        // Clear existing roles and add the new one
        user.getRoles().clear();
        user.getRoles().add(role);
        
        return userRepository.save(user);
    }

    /**
     * Checks if a user with the given email already exists.
     * @param email The email address to check.
     * @return true if a user with this email exists, false otherwise.
     */
    public boolean userExists(String email) {
        return userRepository.existsByEmail(email);
    }
}