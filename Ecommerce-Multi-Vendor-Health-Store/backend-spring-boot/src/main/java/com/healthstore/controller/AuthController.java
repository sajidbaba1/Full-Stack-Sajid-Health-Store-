package com.healthstore.controller;

import com.healthstore.dto.AuthRequest;
import com.healthstore.dto.AuthResponse;
import com.healthstore.model.User;
import com.healthstore.security.CustomUserDetailsService;
import com.healthstore.util.JwtUtil;
import com.healthstore.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for handling authentication-related API endpoints.
 * This includes user registration and login.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtUtil jwtUtil;

    /**
     * Constructor for AuthController.
     * @param userService The service for user-related business logic.
     * @param authenticationManager The authentication manager for user authentication.
     * @param customUserDetailsService The user details service to load user data.
     * @param jwtUtil The utility for generating and validating JWT tokens.
     */
    public AuthController(UserService userService, AuthenticationManager authenticationManager,
                         CustomUserDetailsService customUserDetailsService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.customUserDetailsService = customUserDetailsService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * API endpoint for user registration.
     * It checks if a user with the given email already exists, and if not,
     * registers a new user with the provided details.
     * @param user The user details provided in the request body.
     * @return A response entity with the created user or a conflict status.
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        if (userService.userExists(user.getEmail())) {
            return new ResponseEntity<>("User with this email already exists", HttpStatus.CONFLICT);
        }
        User registeredUser = userService.registerUser(user);
        return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
    }

    /**
     * API endpoint for user login.
     * It authenticates the user using the provided email and password.
     * If successful, it generates a JWT and returns it in the response.
     * @param request The authentication request DTO with email and password.
     * @return A response entity with the JWT token or an unauthorized status.
     * @throws Exception if authentication fails.
     */
    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthRequest request) throws Exception {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new Exception("Incorrect username or password", e);
        }

        final UserDetails userDetails = customUserDetailsService.loadUserByUsername(request.getEmail());
        final String jwt = jwtUtil.generateToken(userDetails);
        
        AuthResponse response = new AuthResponse();
        response.setJwt(jwt);
        return ResponseEntity.ok(response);
    }
}
