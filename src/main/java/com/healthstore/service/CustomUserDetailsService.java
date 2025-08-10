package com.healthstore.service;

import com.healthstore.model.User;
import com.healthstore.repository.UserRepository;
import com.healthstore.security.CustomUserDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Custom implementation of Spring Security's UserDetailsService.
 * This service is responsible for loading user-specific data during authentication.
 * It retrieves a User from the database using their email and wraps it in a
 * CustomUserDetails object for Spring Security to use.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Constructor for CustomUserDetailsService.
     * @param userRepository The repository to access user data.
     */
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Loads a user by their username (in this case, email).
     * @param email The email address used for authentication.
     * @return A UserDetails object.
     * @throws UsernameNotFoundException if the user is not found.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        return new CustomUserDetails(user);
    }
}