package com.healthstore.controller;

import com.healthstore.model.Cart;
import com.healthstore.model.User;
import com.healthstore.service.CartService;
import com.healthstore.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * REST controller for managing a user's shopping cart.
 * All endpoints are secured and require user authentication.
 */
@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;
    private final UserService userService;

    public CartController(CartService cartService, UserService userService) {
        this.cartService = cartService;
        this.userService = userService;
    }

    /**
     * Adds a product to the authenticated user's cart.
     * @param userDetails The details of the authenticated user.
     * @param productId The ID of the product to add.
     * @param quantity The quantity of the product.
     * @return A response entity with the updated cart or an error status.
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/add/{productId}")
    public ResponseEntity<Cart> addProductToCart(@AuthenticationPrincipal UserDetails userDetails,
                                               @PathVariable Long productId,
                                               @RequestParam int quantity) {
        Optional<User> user = userService.findUserByEmail(userDetails.getUsername());
        if (user.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        try {
            Cart updatedCart = cartService.addProductToCart(user.get(), productId, quantity);
            return new ResponseEntity<>(updatedCart, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Retrieves the cart for the authenticated user.
     * @param userDetails The details of the authenticated user.
     * @return A response entity with the user's cart.
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<Cart> getCart(@AuthenticationPrincipal UserDetails userDetails) {
        Optional<User> user = userService.findUserByEmail(userDetails.getUsername());
        if (user.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        try {
            Cart cart = cartService.getUserCart(user.get());
            return new ResponseEntity<>(cart, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
