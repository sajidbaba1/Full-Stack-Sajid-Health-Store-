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

    /**
     * Constructor for UserService.
     * @param userRepository The repository to access user data.
     * @param passwordEncoder The encoder for hashing passwords.
     */
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registers a new user.
     * This method encrypts the user's password using BCrypt and sets the creation timestamp.
     * @param user The user object to be saved.
     * @return The saved user object.
     */
    public User registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        // For the purpose of this example, we'll set the role to 'ROLE_USER' by default.
        // This will be expanded in a later step for different user types.
        user.setRole("ROLE_USER");
        return userRepository.save(user);
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
