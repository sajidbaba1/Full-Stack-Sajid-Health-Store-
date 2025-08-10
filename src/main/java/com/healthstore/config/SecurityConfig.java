package com.healthstore.config;

import com.healthstore.security.JwtRequestFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security configuration for the Health Store application.
 * This class defines the security rules, authentication manager, and password encoder.
 * We use @EnableWebSecurity to enable Spring Security's web security support.
 * The security filter chain is configured to be stateless, using JWT for authentication.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtRequestFilter jwtRequestFilter;

    /**
     * Constructor for SecurityConfig.
     * We use dependency injection to get an instance of the JwtRequestFilter.
     * @param jwtRequestFilter The JWT filter for token validation.
     */
    public SecurityConfig(JwtRequestFilter jwtRequestFilter) {
        this.jwtRequestFilter = jwtRequestFilter;
    }

    /**
     * Configures the security filter chain.
     * This method defines which URLs are accessible to the public and which
     * require authentication. It also configures session management to be stateless.
     * @param http The HttpSecurity object to configure.
     * @return The configured SecurityFilterChain.
     * @throws Exception if an error occurs during configuration.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Disable CSRF as JWT is stateless
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/auth/**").permitAll() // Public endpoints for authentication
                        .anyRequest().authenticated() // All other requests require authentication
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Make sessions stateless
                )
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class); // Add our JWT filter

        return http.build();
    }

    /**
     * Creates a bean for the password encoder.
     * We use BCryptPasswordEncoder for strong, one-way password hashing.
     * @return A PasswordEncoder instance.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Creates a bean for the AuthenticationManager.
     * This manager is used to authenticate a user.
     * @param authenticationConfiguration The authentication configuration.
     * @return An AuthenticationManager instance.
     * @throws Exception if an error occurs.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}