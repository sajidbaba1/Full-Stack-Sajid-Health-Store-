package com.healthstore.controller;

import com.healthstore.dto.AddressDTO;
import com.healthstore.dto.mapper.AddressMapper;
import com.healthstore.model.Address;
import com.healthstore.model.User;
import com.healthstore.service.AddressService;
import com.healthstore.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for managing user addresses.
 */
@RestController
@RequestMapping("/api/addresses")
public class AddressController {

    private final AddressService addressService;
    private final UserService userService;
    private final AddressMapper addressMapper;

    public AddressController(AddressService addressService, 
                           UserService userService,
                           AddressMapper addressMapper) {
        this.addressService = addressService;
        this.userService = userService;
        this.addressMapper = addressMapper;
    }

    /**
     * Creates a new address for the authenticated user.
     *
     * @param userDetails The authenticated user details.
     * @param addressDTO  The address details to create.
     * @return The created address.
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AddressDTO> createAddress(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody AddressDTO addressDTO) {
        
        User user = userService.findUserByEmail(userDetails.getUsername())
                .orElseThrow(() -> new SecurityException("User not found"));
        
        Address address = addressMapper.toEntity(addressDTO);
        Address savedAddress = addressService.saveAddress(user, address);
        
        return new ResponseEntity<>(addressMapper.toDto(savedAddress), HttpStatus.CREATED);
    }

    /**
     * Updates an existing address for the authenticated user.
     *
     * @param userDetails The authenticated user details.
     * @param id          The ID of the address to update.
     * @param addressDTO  The updated address details.
     * @return The updated address.
     */
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AddressDTO> updateAddress(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @Valid @RequestBody AddressDTO addressDTO) {
        
        User user = userService.findUserByEmail(userDetails.getUsername())
                .orElseThrow(() -> new SecurityException("User not found"));
        
        Address address = addressMapper.toEntity(addressDTO);
        address.setId(id);
        
        Address updatedAddress = addressService.updateAddress(user, id, address);
        return ResponseEntity.ok(addressMapper.toDto(updatedAddress));
    }

    /**
     * Retrieves all addresses for the authenticated user.
     *
     * @param userDetails The authenticated user details.
     * @return A list of the user's addresses.
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<AddressDTO>> getUserAddresses(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        User user = userService.findUserByEmail(userDetails.getUsername())
                .orElseThrow(() -> new SecurityException("User not found"));
        
        List<Address> addresses = addressService.getAddressesByUser(user);
        List<AddressDTO> addressDTOs = addresses.stream()
                .map(addressMapper::toDto)
                .collect(Collectors.toList());
                
        return ResponseEntity.ok(addressDTOs);
    }

    /**
     * Retrieves the default address for the authenticated user.
     *
     * @param userDetails The authenticated user details.
     * @return The user's default address, or 404 if not found.
     */
    @GetMapping("/default")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AddressDTO> getDefaultAddress(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        User user = userService.findUserByEmail(userDetails.getUsername())
                .orElseThrow(() -> new SecurityException("User not found"));
        
        return addressService.getDefaultAddress(user)
                .map(address -> ResponseEntity.ok(addressMapper.toDto(address)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Retrieves a specific address by ID for the authenticated user.
     *
     * @param userDetails The authenticated user details.
     * @param id          The ID of the address to retrieve.
     * @return The requested address if found and owned by the user.
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AddressDTO> getAddressById(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        
        User user = userService.findUserByEmail(userDetails.getUsername())
                .orElseThrow(() -> new SecurityException("User not found"));
        
        return addressService.getAddressById(id)
                .filter(address -> address.getUser().getId().equals(user.getId()))
                .map(address -> ResponseEntity.ok(addressMapper.toDto(address)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Deletes an address for the authenticated user.
     *
     * @param userDetails The authenticated user details.
     * @param id          The ID of the address to delete.
     * @return 204 No Content on success, or 404 if not found.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteAddress(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        
        User user = userService.findUserByEmail(userDetails.getUsername())
                .orElseThrow(() -> new SecurityException("User not found"));
        
        addressService.deleteAddress(user, id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Sets an address as the default for the authenticated user.
     *
     * @param userDetails The authenticated user details.
     * @param id          The ID of the address to set as default.
     * @return The updated address, or 404 if not found.
     */
    @PutMapping("/{id}/set-default")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AddressDTO> setDefaultAddress(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        
        User user = userService.findUserByEmail(userDetails.getUsername())
                .orElseThrow(() -> new SecurityException("User not found"));
        
        // Get the address and verify ownership
        Address address = addressService.getAddressById(id)
                .orElseThrow(() -> new IllegalArgumentException("Address not found with id: " + id));
        
        if (!address.getUser().getId().equals(user.getId())) {
            throw new SecurityException("You don't have permission to modify this address");
        }
        
        // Update the default status
        address.setDefault(true);
        Address updatedAddress = addressService.saveAddress(user, address);
        
        return ResponseEntity.ok(addressMapper.toDto(updatedAddress));
    }
}
