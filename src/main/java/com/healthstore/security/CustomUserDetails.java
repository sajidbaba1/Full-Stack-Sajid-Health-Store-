package com.healthstore.security;

import com.healthstore.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * Custom implementation of Spring Security's UserDetails.
 * This class wraps our User entity, providing the necessary details for
 * authentication and authorization, such as username, password, and authorities (roles).
 */
public class CustomUserDetails implements UserDetails {

    private final User user;

    /**
     * Constructor for CustomUserDetails.
     * @param user The User entity from the database.
     */
    public CustomUserDetails(User user) {
        this.user = user;
    }

    /**
     * Returns the user's granted authorities (roles).
     * @return A collection of GrantedAuthority.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(user.getRole()));
    }

    /**
     * Returns the password used to authenticate the user.
     * @return The user's password.
     */
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /**
     * Returns the username used to authenticate the user.
     * In our case, the username is the user's email.
     * @return The user's email.
     */
    @Override
    public String getUsername() {
        return user.getEmail();
    }

    /**
     * Indicates whether the user's account has expired.
     * We'll return true as we are not implementing account expiration for now.
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user is locked or unlocked.
     * We'll return true as we are not implementing account locking for now.
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Indicates whether the user's credentials have expired.
     * We'll return true as we are not implementing credential expiration for now.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user is enabled or disabled.
     * We'll return true as we are not implementing user disabling for now.
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}