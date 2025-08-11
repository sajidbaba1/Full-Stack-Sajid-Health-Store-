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
    public ResponseEntity<Cart> addProductToCart(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long productId,
            @RequestParam(defaultValue = "1") int quantity) {
        
        if (quantity <= 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        
        Optional<User> userOpt = userService.findByEmail(userDetails.getUsername());
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        try {
            Cart updatedCart = cartService.addProductToCart(userOpt.get(), productId, quantity);
            return ResponseEntity.ok(updatedCart);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
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
        Optional<User> userOpt = userService.findByEmail(userDetails.getUsername());
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        try {
            Cart cart = cartService.getCartByUser(userOpt.get());
            return ResponseEntity.ok(cart);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Updates the quantity of a product in the cart.
     * @param userDetails The details of the authenticated user.
     * @param productId The ID of the product to update.
     * @param quantity The new quantity.
     * @return A response entity with the updated cart or an error status.
     */
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/update/{productId}")
    public ResponseEntity<Cart> updateCartItem(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long productId,
            @RequestParam int quantity) {
        
        if (quantity <= 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        
        Optional<User> userOpt = userService.findByEmail(userDetails.getUsername());
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        try {
            Cart updatedCart = cartService.updateCartItemQuantity(userOpt.get(), productId, quantity);
            return ResponseEntity.ok(updatedCart);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Removes a product from the cart.
     * @param userDetails The details of the authenticated user.
     * @param productId The ID of the product to remove.
     * @return A response entity with the updated cart or an error status.
     */
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<Cart> removeFromCart(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long productId) {
        
        Optional<User> userOpt = userService.findByEmail(userDetails.getUsername());
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        try {
            Cart updatedCart = cartService.removeProductFromCart(userOpt.get(), productId);
            return ResponseEntity.ok(updatedCart);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Clears all items from the cart.
     * @param userDetails The details of the authenticated user.
     * @return A response entity with the cleared cart or an error status.
     */
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/clear")
    public ResponseEntity<Cart> clearCart(@AuthenticationPrincipal UserDetails userDetails) {
        Optional<User> userOpt = userService.findByEmail(userDetails.getUsername());
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        try {
            Cart clearedCart = cartService.clearCart(userOpt.get());
            return ResponseEntity.ok(clearedCart);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
