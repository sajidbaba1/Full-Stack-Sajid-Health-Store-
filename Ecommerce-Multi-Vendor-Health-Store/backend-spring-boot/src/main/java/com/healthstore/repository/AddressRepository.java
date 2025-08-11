package com.healthstore.repository;

import com.healthstore.model.Address;
import com.healthstore.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Address entities.
 * Extends JpaRepository to provide a complete set of CRUD operations.
 */
@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    
    /**
     * Finds all addresses associated with a specific user.
     * @param user The user whose addresses to find.
     * @return A list of addresses associated with the user.
     */
    List<Address> findByUser(User user);
    
    /**
     * Finds all addresses associated with a specific user ID.
     * @param userId The ID of the user whose addresses to find.
     * @return A list of addresses associated with the user.
     */
    List<Address> findByUserId(Long userId);
    
    /**
     * Unsets the default flag for all addresses of a user except the specified one.
     * @param userId The ID of the user.
     * @param excludeAddressId The ID of the address to exclude from being updated.
     */
    @Modifying
    @Query("UPDATE Address a SET a.defaultAddress = false WHERE a.user.id = :userId AND a.id != :excludeAddressId")
    void unsetDefaultAddresses(@Param("userId") Long userId, @Param("excludeAddressId") Long excludeAddressId);
}
