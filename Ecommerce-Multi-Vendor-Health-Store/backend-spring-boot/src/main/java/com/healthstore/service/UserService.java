package com.healthstore.service;

import com.healthstore.model.Role;
import com.healthstore.model.User;
import com.healthstore.repository.RoleRepository;
import com.healthstore.repository.UserRepository;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.healthstore.dto.PasswordUpdateDTO;
import com.healthstore.dto.UserUpdateDTO;
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
     * @param user The user object to be saved.
     * @return The saved user object with an associated cart.
     */
    public User registerUser(User user) {
        // Encode the user's password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        
        // Save the user first to get the generated ID
        User savedUser = userRepository.save(user);
        
        // Create a new cart for the user
        cartService.createCartForUser(savedUser);
        
        return savedUser;
    }

    /**
     * Registers a new user from OAuth2 provider (Google, Facebook)
     * @param user The user details from OAuth2 provider
     * @return The registered user
     */
    @Transactional
    public User registerOAuth2User(User user) {
        if (user.getEmail() == null) {
            throw new IllegalArgumentException("Email is required for OAuth2 registration");
        }
        
        // Check if user already exists
        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            return existingUser.get();
        }
        
        // Set default values for OAuth2 user
        user.setPassword(null); // No password for OAuth2 users
        user.setCreatedAt(LocalDateTime.now());
        
        User savedUser = userRepository.save(user);
        
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
     * Updates a user's password after validating the current password.
     * @param email The email of the user whose password is being updated.
     * @param currentPassword The user's current password for verification.
     * @param newPassword The new password to set.
     * @throws RuntimeException if the user is not found or the current password is incorrect.
     */
    @Transactional
    public void updateUserPassword(String email, String currentPassword, String newPassword) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            throw new RuntimeException("User not found with email: " + email);
        }

        User user = optionalUser.get();
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new RuntimeException("Incorrect current password.");
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
    
    /**
     * Checks if a user with the given email already exists.
     * @param email The email address to check.
     * @return true if a user with this email exists, false otherwise.
     */
    public boolean userExists(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Updates a user's profile information.
     * @param email The email of the user to update.
     * @param userUpdateDTO The DTO with the new profile data.
     * @return The updated user entity.
     * @throws RuntimeException if the user is not found.
     */
    public User updateUserProfile(String email, UserUpdateDTO userUpdateDTO) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            throw new RuntimeException("User not found with email: " + email);
        }

        User user = optionalUser.get();
        user.setFirstName(userUpdateDTO.getFirstName());
        user.setLastName(userUpdateDTO.getLastName());
        user.setMobile(userUpdateDTO.getMobile());

        return userRepository.save(user);
    }

    /**
     * Finds all users in the database.
     * @return A list of all users.
     */
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Updates a user's profile information by their ID.
     * @param id The ID of the user to update.
     * @param userUpdateDTO The DTO with the new profile data.
     * @return The updated user entity.
     * @throws RuntimeException if the user is not found.
     */
    public User updateUser(Long id, UserUpdateDTO userUpdateDTO) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            throw new RuntimeException("User not found with ID: " + id);
        }

        User user = optionalUser.get();
        user.setFirstName(userUpdateDTO.getFirstName());
        user.setLastName(userUpdateDTO.getLastName());
        user.setMobile(userUpdateDTO.getMobile());

        return userRepository.save(user);
    }

    /**
     * Finds a user by their ID.
     * @param id The ID of the user to find.
     * @return An Optional containing the user if found, or empty if not found.
     */
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
    
    /**
     * Gets the currently authenticated user.
     * @return The current user
     * @throws RuntimeException if no user is authenticated
     */
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || 
            authentication instanceof AnonymousAuthenticationToken) {
            throw new RuntimeException("No authenticated user found");
        }
        
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
    }
    
    /**
     * Find user by email
     * @param email User email
     * @return User or null if not found
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Updates a user's role.
     * @param id The ID of the user to update.
     * @param roleName The name of the role to assign to the user.
     * @return The updated user entity.
     * @throws RuntimeException if the user or role is not found.
     */
    public User updateUserRole(Long id, String roleName) {
        Optional<User> optionalUser = userRepository.findById(id);
        Optional<Role> optionalRole = roleRepository.findByName(roleName);
        
        if (optionalUser.isEmpty() || optionalRole.isEmpty()) {
            throw new RuntimeException("User or role not found");
        }
        
        User user = optionalUser.get();
        Set<Role> roles = user.getRoles();
        roles.clear();
        roles.add(optionalRole.get());
        user.setRoles(roles);
        
        return userRepository.save(user);
    }
}
