package com.healthstore.admin;

import com.healthstore.dto.AuditFilterDTO;
import com.healthstore.dto.UserRoleUpdateDTO;
import com.healthstore.dto.UserUpdateDTO;
import com.healthstore.model.Order;
import com.healthstore.model.User;
import com.healthstore.service.OrderService;
import com.healthstore.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.RevisionType;
import jakarta.persistence.EntityManager;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;
    private final OrderService orderService;
    private final EntityManager entityManager;

    public AdminController(UserService userService, OrderService orderService, EntityManager entityManager) {
        this.userService = userService;
        this.orderService = orderService;
        this.entityManager = entityManager;
    }

    /**
     * Get audit history for a specific product
     * @param productId The ID of the product to get audit history for
     * @return List of audit revisions for the product
     */
    @GetMapping("/audit/product/{productId}")
    public ResponseEntity<List<?>> getProductAuditHistory(@PathVariable Long productId) {
        AuditReader auditReader = AuditReaderFactory.get(entityManager);
        List<?> revisions = auditReader.createQuery()
            .forRevisionsOfEntity(com.healthstore.model.Product.class, false, true)
            .add(AuditEntity.property("id").eq(productId))
            .getResultList();
        return ResponseEntity.ok(revisions);
    }
    
    /**
     * Advanced search for audit logs with filtering capabilities
     * @param filter The filter criteria for the audit log search
     * @return List of audit log entries matching the filter criteria
     */
    @PostMapping("/audit/search")
    public ResponseEntity<List<?>> searchAuditLogs(@RequestBody AuditFilterDTO filter) {
        if (filter.getEntityName() == null || filter.getEntityName().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(List.of("Entity name is required"));
        }
        
        try {
            // Get the entity class from the entity name
            Class<?> entityClass = Class.forName("com.healthstore.model." + filter.getEntityName());
            
            // Create the base query
            AuditReader auditReader = AuditReaderFactory.get(entityManager);
            var query = auditReader.createQuery()
                .forRevisionsOfEntity(entityClass, false, true);
            
            // Apply entity ID filter if provided
            if (filter.getEntityId() != null) {
                query.add(AuditEntity.property("id").eq(filter.getEntityId()));
            }
            
            // Apply revision type filter if provided
            if (filter.getRevisionType() != null && !filter.getRevisionType().trim().isEmpty()) {
                query.add(AuditEntity.revisionType().eq(RevisionType.valueOf(filter.getRevisionType())));
            }
            
            // Apply date range filters if provided
            if (filter.getStartDate() != null) {
                query.add(AuditEntity.revisionProperty("timestamp").ge(filter.getStartDate()));
            }
            if (filter.getEndDate() != null) {
                query.add(AuditEntity.revisionProperty("timestamp").le(filter.getEndDate()));
            }
            
            // Execute the query and return results
            List<?> results = query.getResultList();
            return ResponseEntity.ok(results);
            
        } catch (ClassNotFoundException e) {
            return ResponseEntity.badRequest().body(List.of("Invalid entity name: " + filter.getEntityName()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(List.of("Invalid revision type: " + filter.getRevisionType()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(List.of("An error occurred: " + e.getMessage()));
        }
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
