package com.healthstore.admin;

import com.healthstore.dto.UserRoleUpdateDTO;
import com.healthstore.dto.UserUpdateDTO;
import com.healthstore.model.Order;
import com.healthstore.model.User;
import com.healthstore.service.OrderService;
import com.healthstore.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;
    private final OrderService orderService;

    public AdminController(UserService userService, OrderService orderService) {
        this.userService = userService;
        this.orderService = orderService;
    }

    /**
     * Endpoint to get all registered users.
     * Accessible only to users with the 'ROLE_ADMIN' role.
     * @return A list of all users.
     */
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.findAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    /**
     * Endpoint to update a user's profile information.
     * @param id The ID of the user to update.
     * @param userUpdateDTO The DTO with the updated user data.
     * @return A response entity with the updated user or an error.
     */
    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UserUpdateDTO userUpdateDTO) {
        try {
            User updatedUser = userService.updateUser(id, userUpdateDTO);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Endpoint to update a user's role.
     * @param id The ID of the user to update.
     * @param roleUpdateDTO The DTO containing the new role name.
     * @return A response entity with the updated user or an error.
     */
    @PutMapping("/users/{id}/role")
    public ResponseEntity<?> updateUserRole(@PathVariable Long id, @Valid @RequestBody UserRoleUpdateDTO roleUpdateDTO) {
        try {
            User updatedUser = userService.updateUserRole(id, roleUpdateDTO.getRoleName());
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
    
    /**
     * Endpoint to retrieve all orders with pagination.
     * @param page The page number (default: 0).
     * @param size The number of items per page (default: 10).
     * @return A page of orders.
     */
    @GetMapping("/orders")
    public ResponseEntity<?> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            return new ResponseEntity<>(orderService.findAllOrders(org.springframework.data.domain.PageRequest.of(page, size)), 
                                     HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Endpoint to update the status of an order.
     * @param orderId The ID of the order to update.
     * @param status The new status to set.
     * @return The updated order.
     */
    @PutMapping("/orders/{orderId}/status")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable Long orderId, 
            @RequestParam String status) {
        try {
            // Convert string status to enum
            Order.OrderStatus orderStatus = Order.OrderStatus.valueOf(status.toUpperCase());
            Order updatedOrder = orderService.updateOrderStatus(orderId, orderStatus);
            return new ResponseEntity<>(updatedOrder, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Invalid status value", HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
