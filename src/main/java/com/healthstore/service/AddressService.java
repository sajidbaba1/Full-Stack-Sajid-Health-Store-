package com.healthstore.service;

import com.healthstore.model.Address;
import com.healthstore.model.User;
import com.healthstore.repository.AddressRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service class for handling address-related operations.
 */
@Service
@Transactional
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserService userService;

    public AddressService(AddressRepository addressRepository, UserService userService) {
        this.addressRepository = addressRepository;
        this.userService = userService;
    }

    /**
     * Retrieves an address by its ID.
     * @param id The ID of the address to retrieve.
     * @return An Optional containing the address if found, or empty if not found.
     */
    @Transactional(readOnly = true)
    public Optional<Address> getAddressById(Long id) {
        return addressRepository.findById(id);
    }

    /**
     * Saves an address for a specific user.
     * @param user The user to associate with the address.
     * @param address The address to save.
     * @return The saved address with the user association.
     */
    public Address saveAddress(User user, Address address) {
        // Set the user for the address
        address.setUser(user);
        
        // If this is the first address for the user, set it as default
        if (addressRepository.countByUser(user) == 0) {
            address.setDefault(true);
        }
        
        // If this address is set as default, update other addresses
        if (address.isDefault()) {
            setAsDefaultAddress(user, address);
        }
        
        return addressRepository.save(address);
    }
    
    /**
     * Updates an existing address.
     * @param user The user who owns the address.
     * @param addressId The ID of the address to update.
     * @param addressDetails The address details to update.
     * @return The updated address.
     */
    public Address updateAddress(User user, Long addressId, Address addressDetails) {
        return addressRepository.findById(addressId)
            .map(existingAddress -> {
                // Verify the address belongs to the user
                if (!existingAddress.getUser().getId().equals(user.getId())) {
                    throw new SecurityException("You don't have permission to update this address");
                }
                
                // Update address fields
                existingAddress.setStreet(addressDetails.getStreet());
                existingAddress.setCity(addressDetails.getCity());
                existingAddress.setState(addressDetails.getState());
                existingAddress.setPostalCode(addressDetails.getPostalCode());
                existingAddress.setCountry(addressDetails.getCountry());
                existingAddress.setPhoneNumber(addressDetails.getPhoneNumber());
                existingAddress.setAddressType(addressDetails.getAddressType());
                
                // Handle default address setting
                if (addressDetails.isDefault() && !existingAddress.isDefault()) {
                    setAsDefaultAddress(user, existingAddress);
                }
                
                return addressRepository.save(existingAddress);
            })
            .orElseThrow(() -> new IllegalArgumentException("Address not found with id: " + addressId));
    }
    
    /**
     * Sets an address as the default address for a user.
     * @param user The user who owns the address.
     * @param address The address to set as default.
     */
    private void setAsDefaultAddress(User user, Address address) {
        // Find the current default address and unset it
        addressRepository.findByUserAndIsDefaultTrue(user).ifPresent(currentDefault -> {
            if (!currentDefault.getId().equals(address.getId())) {
                currentDefault.setDefault(false);
                addressRepository.save(currentDefault);
            }
        });
        
        // Set the new default
        address.setDefault(true);
    }
    
    /**
     * Retrieves all addresses for a specific user.
     * @param user The user whose addresses to retrieve.
     * @return A list of addresses for the user.
     */
    @Transactional(readOnly = true)
    public List<Address> getAddressesByUser(User user) {
        return addressRepository.findByUser(user);
    }
    
    /**
     * Gets the default address for a user.
     * @param user The user whose default address to retrieve.
     * @return The default address if found, or empty if not found.
     */
    @Transactional(readOnly = true)
    public Optional<Address> getDefaultAddress(User user) {
        return addressRepository.findByUserAndIsDefaultTrue(user);
    }
    
    /**
     * Deletes an address if it belongs to the specified user.
     * @param user The user who owns the address.
     * @param addressId The ID of the address to delete.
     */
    public void deleteAddress(User user, Long addressId) {
        addressRepository.findById(addressId).ifPresent(address -> {
            // Verify the address belongs to the user
            if (!address.getUser().getId().equals(user.getId())) {
                throw new SecurityException("You don't have permission to delete this address");
            }
            
            // If this was the default address, set another address as default if available
            if (address.isDefault()) {
                List<Address> otherAddresses = addressRepository.findByUser(user);
                otherAddresses.stream()
                    .filter(a -> !a.getId().equals(addressId))
                    .findFirst()
                    .ifPresent(newDefault -> {
                        newDefault.setDefault(true);
                        addressRepository.save(newDefault);
                    });
            }
            
            // Delete the address
            addressRepository.delete(address);
        });
    }
    
    /**
     * Counts the number of addresses for a specific user.
     * @param user The user whose addresses to count.
     * @return The number of addresses for the user.
     */
    @Transactional(readOnly = true)
    public long countAddressesByUser(User user) {
        return addressRepository.countByUser(user);
    }
}
