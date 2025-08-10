package com.healthstore.controller;

import com.healthstore.dto.OrderResponseDTO;
import com.healthstore.model.Order;
import com.healthstore.model.OrderStatus;
import com.healthstore.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * REST controller for admin order management.
 * Provides endpoints for viewing and managing orders with admin privileges.
 */
@RestController
@RequestMapping("/api/admin/orders")
@PreAuthorize("hasRole('ADMIN')")
public class AdminOrderController {

    private final OrderService orderService;

    @Autowired
    public AdminOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Get all orders with pagination and filtering.
     * 
     * @param page The page number (0-based, default is 0).
     * @param size The number of items per page (default is 10, max is 50).
     * @param sortBy The field to sort by (default is 'orderDate').
     * @param direction The sort direction ('asc' or 'desc', default is 'desc').
     * @param status Filter by order status (optional).
     * @param startDate Filter orders after this date (optional).
     * @param endDate Filter orders before this date (optional).
     * @return A page of orders with pagination metadata.
     */
    @GetMapping
    public ResponseEntity<Page<OrderResponseDTO>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "orderDate") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) {
        
        // Validate page and size parameters
        if (page < 0) page = 0;
        if (size <= 0 || size > 50) size = 10;
        
        // Create sort direction
        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") ? 
                Sort.Direction.ASC : Sort.Direction.DESC;
        
        // Create pageable object
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        
        // Get orders with filters
        Page<Order> orders = orderService.findAllOrders(status, startDate, endDate, pageable);
        
        // Convert to DTOs
        Page<OrderResponseDTO> responseDTOs = orders.map(OrderResponseDTO::fromOrder);
        
        return new ResponseEntity<>(responseDTOs, HttpStatus.OK);
    }
    
    /**
     * Get a specific order by ID.
     * 
     * @param orderId The ID of the order to retrieve.
     * @return The order details or 404 if not found.
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDTO> getOrderById(@PathVariable Long orderId) {
        return orderService.findOrderById(orderId)
                .map(order -> new ResponseEntity<>(OrderResponseDTO.fromOrder(order), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    /**
     * Update the status of an order.
     * 
     * @param orderId The ID of the order to update.
     * @param status The new status to set.
     * @return The updated order or an error message.
     */
    @PutMapping("/{orderId}/status")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam OrderStatus status) {
        
        try {
            Order updatedOrder = orderService.updateOrderStatus(orderId, status);
            return new ResponseEntity<>(OrderResponseDTO.fromOrder(updatedOrder), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    /**
     * Get order statistics.
     * 
     * @param startDate The start date for the statistics (optional).
     * @param endDate The end date for the statistics (optional).
     * @return A map containing various order statistics.
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getOrderStatistics(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) {
        
        Map<String, Object> stats = new HashMap<>();
        
        // Total orders
        long totalOrders = orderService.countOrders(startDate, endDate);
        stats.put("totalOrders", totalOrders);
        
        // Total revenue
        BigDecimal totalRevenue = orderService.calculateTotalRevenue(startDate, endDate);
        stats.put("totalRevenue", totalRevenue);
        
        // Orders by status
        Map<OrderStatus, Long> ordersByStatus = orderService.countOrdersByStatus(startDate, endDate);
        stats.put("ordersByStatus", ordersByStatus);
        
        // Recent orders (last 5)
        Page<Order> recentOrders = orderService.findRecentOrders(PageRequest.of(0, 5, Sort.by("orderDate").descending()));
        stats.put("recentOrders", recentOrders.map(OrderResponseDTO::fromOrder).getContent());
        
        return new ResponseEntity<>(stats, HttpStatus.OK);
    }
    
    /**
     * Cancel an order.
     * 
     * @param orderId The ID of the order to cancel.
     * @return A success message or an error.
     */
    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<?> cancelOrder(@PathVariable Long orderId) {
        try {
            Order cancelledOrder = orderService.cancelOrder(orderId);
            return new ResponseEntity<>(
                    Map.of("message", "Order cancelled successfully", 
                           "order", OrderResponseDTO.fromOrder(cancelledOrder)),
                    HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    /**
     * Delete an order.
     * 
     * @param orderId The ID of the order to delete.
     * @return A success message or an error.
     */
    @DeleteMapping("/{orderId}")
    public ResponseEntity<?> deleteOrder(@PathVariable Long orderId) {
        try {
            orderService.deleteOrder(orderId);
            return new ResponseEntity<>(Map.of("message", "Order deleted successfully"), HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
