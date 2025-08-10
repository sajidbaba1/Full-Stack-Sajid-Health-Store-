package com.healthstore.repository;

import com.healthstore.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Address entities.
 * Extends JpaRepository to provide a complete set of CRUD operations.
 */
@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
}