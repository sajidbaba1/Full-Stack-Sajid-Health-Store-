package com.healthstore.service;

import com.healthstore.model.Address;
import com.healthstore.model.User;
import com.healthstore.repository.AddressRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service class for managing user addresses.
 * Provides methods for creating, retrieving, updating, and deleting addresses.
 */
@Service
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserService userService;

    public AddressService(AddressRepository addressRepository, UserService userService) {
        this.addressRepository = addressRepository;
        this.userService = userService;
    }

    /**
     * Saves an address for a specific user.
     * @param user The user to associate with the address.
     * @param address The address to save.
     * @return The saved address.
     */
    public Address saveAddress(User user, Address address) {
        address.setUser(user);
        return addressRepository.save(address);
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
     * Finds an address by its ID.
     * @param id The ID of the address to find.
     * @return An Optional containing the address if found, or empty if not found.
     */
    public Optional<Address> getAddressById(Long id) {
        return addressRepository.findById(id);
    }

    /**
     * Finds all addresses for a specific user.
     * @param userId The ID of the user.
     * @return A list of the user's addresses.
     */
    public List<Address> getUserAddresses(Long userId) {
        return addressRepository.findByUserId(userId);
    }
    
    /**
     * Gets all addresses for a specific user.
     * @param user The user whose addresses to retrieve.
     * @return A list of the user's addresses.
     */
    public List<Address> getAddressesByUser(User user) {
        return addressRepository.findByUser(user);
    }

    /**
     * Creates a new address for a user.
     * @param userId The ID of the user.
     * @param address The address details.
     * @return The created address.
     * @throws RuntimeException if the user is not found.
     */
    public Address createUserAddress(Long userId, Address address) {
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        address.setUser(user);
        return addressRepository.save(address);
    }

    /**
     * Updates an existing address.
     * @param addressId The ID of the address to update.
     * @param updatedAddress The updated address details.
     * @return The updated address.
     * @throws RuntimeException if the address is not found.
     */
    public Address updateAddress(Long addressId, Address updatedAddress) {
        return addressRepository.findById(addressId)
                .map(address -> {
                    address.setStreet(updatedAddress.getStreet());
                    address.setCity(updatedAddress.getCity());
                    address.setState(updatedAddress.getState());
                    address.setPostalCode(updatedAddress.getPostalCode());
                    address.setCountry(updatedAddress.getCountry());
                    address.setPhoneNumber(updatedAddress.getPhoneNumber());
                    address.setDefault(updatedAddress.isDefault());
                    
                    // If this is set as default, unset any other default addresses for this user
                    if (updatedAddress.isDefault()) {
                        addressRepository.unsetDefaultAddresses(address.getUser().getId(), addressId);
                    }
                    
                    return addressRepository.save(address);
                })
                .orElseThrow(() -> new RuntimeException("Address not found with id: " + addressId));
    }

    /**
     * Deletes an address by its ID.
     * @param id The ID of the address to delete.
     */
    public void deleteAddress(Long id) {
        addressRepository.deleteById(id);
    }
    
    /**
     * Deletes an address for a specific user.
     * @param user The user who owns the address.
     * @param addressId The ID of the address to delete.
     */
    public void deleteAddress(User user, Long addressId) {
        Optional<Address> address = addressRepository.findById(addressId);
        address.ifPresent(a -> {
            if (a.getUser().getId().equals(user.getId())) {
                addressRepository.delete(a);
            }
        });
    }
}
