package com.healthstore.repository;

import com.healthstore.model.Address;
import com.healthstore.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Address entities.
 * Extends JpaRepository to provide a complete set of CRUD operations.
 */
@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    
    /**
     * Finds all addresses associated with a specific user.
     *
     * @param user The user whose addresses to find.
     * @return A list of addresses belonging to the specified user.
     */
    List<Address> findByUser(User user);
    
    /**
     * Finds the default address for a specific user.
     *
     * @param user The user whose default address to find.
     * @return An Optional containing the default address if found, or empty if not found.
     */
    Optional<Address> findByUserAndIsDefaultTrue(User user);
    
    /**
     * Counts the number of addresses for a specific user.
     *
     * @param user The user whose addresses to count.
     * @return The number of addresses for the specified user.
     */
    long countByUser(User user);
}