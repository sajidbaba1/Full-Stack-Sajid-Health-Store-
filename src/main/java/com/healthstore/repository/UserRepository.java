package com.healthstore.repository;

import com.healthstore.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for User entities.
 * It extends JpaRepository to inherit methods for standard CRUD operations.
 * We also define a custom query method to find a user by their email address.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their email address.
     * Spring Data JPA automatically generates the query based on the method name.
     * @param email The email address of the user.
     * @return An Optional containing the User if found, or an empty Optional otherwise.
     */
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}