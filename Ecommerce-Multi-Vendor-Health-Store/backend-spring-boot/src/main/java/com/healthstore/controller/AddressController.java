package com.healthstore.controller;

import com.healthstore.model.Address;
import com.healthstore.model.User;
import com.healthstore.service.AddressService;
import com.healthstore.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing user addresses.
 * Provides endpoints for adding, retrieving, and deleting user addresses.
 */
@RestController
@RequestMapping("/api/addresses")
public class AddressController {

    private final AddressService addressService;
    private final UserService userService;

    public AddressController(AddressService addressService, UserService userService) {
        this.addressService = addressService;
        this.userService = userService;
    }

    /**
     * Adds a new address for the authenticated user.
     * @param userDetails The authenticated user's details.
     * @param address The address to add.
     * @return The saved address with HTTP 201 status, or 404 if user not found.
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<Address> addAddress(
            @AuthenticationPrincipal UserDetails userDetails, 
            @RequestBody Address address) {
        
        Optional<User> user = userService.findUserByEmail(userDetails.getUsername());
        if (user.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Address savedAddress = addressService.saveAddress(user.get(), address);
        return new ResponseEntity<>(savedAddress, HttpStatus.CREATED);
    }

    /**
     * Retrieves all addresses for the authenticated user.
     * @param userDetails The authenticated user's details.
     * @return A list of the user's addresses with HTTP 200 status, or 404 if user not found.
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<List<Address>> getAddresses(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Optional<User> user = userService.findUserByEmail(userDetails.getUsername());
        if (user.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        List<Address> addresses = addressService.getAddressesByUser(user.get());
        return new ResponseEntity<>(addresses, HttpStatus.OK);
    }

    /**
     * Deletes a specific address for the authenticated user.
     * @param userDetails The authenticated user's details.
     * @param id The ID of the address to delete.
     * @return HTTP 204 status on success, or 404 if user not found.
     */
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAddress(
            @AuthenticationPrincipal UserDetails userDetails, 
            @PathVariable Long id) {
        
        Optional<User> user = userService.findUserByEmail(userDetails.getUsername());
        if (user.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        addressService.deleteAddress(user.get(), id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
