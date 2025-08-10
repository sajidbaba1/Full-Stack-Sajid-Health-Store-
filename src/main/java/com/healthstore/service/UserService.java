package com.healthstore.service;

import com.healthstore.model.User;
import com.healthstore.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

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

    /**
     * Constructor for UserService.
     * @param userRepository The repository to access user data.
     * @param passwordEncoder The encoder for hashing passwords.
     * @param cartService The service for cart operations.
     */
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, CartService cartService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.cartService = cartService;
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
     * Checks if a user with the given email already exists.
     * @param email The email address to check.
     * @return true if a user with this email exists, false otherwise.
     */
    public boolean userExists(String email) {
        return userRepository.existsByEmail(email);
    }
}