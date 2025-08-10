package com.healthstore.service;

import com.healthstore.model.Address;
import com.healthstore.repository.AddressRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service class for handling address-related operations.
 */
@Service
public class AddressService {

    private final AddressRepository addressRepository;

    public AddressService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    /**
     * Retrieves an address by its ID.
     * @param id The ID of the address to retrieve.
     * @return An Optional containing the address if found, or empty if not found.
     */
    public Optional<Address> getAddressById(Long id) {
        return addressRepository.findById(id);
    }

    /**
     * Saves an address to the database.
     * @param address The address to save.
     * @return The saved address.
     */
    public Address save(Address address) {
        return addressRepository.save(address);
    }

    /**
     * Deletes an address by its ID.
     * @param id The ID of the address to delete.
     */
    public void deleteById(Long id) {
        addressRepository.deleteById(id);
    }
}
