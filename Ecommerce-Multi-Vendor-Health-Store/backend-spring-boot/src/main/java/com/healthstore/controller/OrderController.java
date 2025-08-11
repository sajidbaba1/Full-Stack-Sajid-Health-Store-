package com.healthstore.controller;

import com.healthstore.model.Order;
import com.healthstore.model.User;
import com.healthstore.model.Address;
import com.healthstore.service.OrderService;
import com.healthstore.service.UserService;
import com.healthstore.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing orders.
 * Provides endpoints for order operations such as creating, retrieving, and managing orders.
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;
    private final AddressService addressService;

    @Autowired
    public OrderController(OrderService orderService, 
                           UserService userService,
                           AddressService addressService) {
        this.orderService = orderService;
        this.userService = userService;
        this.addressService = addressService;
    }

    /**
     * Creates a new order from the user's cart.
     * @param userDetails The authenticated user details.
     * @param shippingAddressId The ID of the shipping address to use.
     * @return The created order.
     */
    @PostMapping("/checkout/{shippingAddressId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long shippingAddressId) {
        
        User user = userService.findUserByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
                
        Address shippingAddress = addressService.getAddressById(shippingAddressId)
                .orElseThrow(() -> new RuntimeException("Shipping address not found"));
        
        try {
            Order order = orderService.createOrderFromCart(user, shippingAddress);
            return new ResponseEntity<>(order, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Retrieves all orders for the authenticated user.
     * @param userDetails The authenticated user details.
     * @return A list of the user's orders.
     */
    @GetMapping("/my-orders")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Order>> getUserOrders(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        User user = userService.findUserByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
                
        List<Order> orders = orderService.getUserOrders(user).getContent();
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    /**
     * Retrieves a specific order by ID.
     * Users can only access their own orders, while admins can access any order.
     * @param orderId The ID of the order to retrieve.
     * @param userDetails The authenticated user details.
     * @return The requested order.
     */
    @GetMapping("/{orderId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getOrderById(
            @PathVariable Long orderId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        User user = userService.findUserByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
                
        return orderService.getOrderById(orderId)
                .map(order -> {
                    // Check if the user is the owner or an admin
                    if (!order.getUser().getId().equals(user.getId()) && 
                        !userDetails.getAuthorities().stream()
                            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                        return new ResponseEntity<>("Access denied", HttpStatus.FORBIDDEN);
                    }
                    return new ResponseEntity<>(order, HttpStatus.OK);
                })
                .orElse(new ResponseEntity<>("Order not found", HttpStatus.NOT_FOUND));
    }

    /**
     * Updates the status of an order (admin only).
     * @param orderId The ID of the order to update.
     * @param status The new status to set.
     * @return The updated order.
     */
    @PutMapping("/{orderId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam Order.OrderStatus status) {
        
        try {
            Order updatedOrder = orderService.updateOrderStatus(orderId, status);
            return new ResponseEntity<>(updatedOrder, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Retrieves all orders with pagination (admin only).
     * @param pageable The pagination information.
     * @return A page of orders.
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<Order>> getAllOrders(Pageable pageable) {
        Page<Order> orders = orderService.findAllOrders(pageable);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }
}