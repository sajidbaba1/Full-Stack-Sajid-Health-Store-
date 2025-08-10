package com.healthstore.service;

import com.healthstore.dto.OrderDTO;
import com.healthstore.exception.ResourceNotFoundException;
import com.healthstore.model.*;
import com.healthstore.repository.OrderRepository;
import com.healthstore.repository.ProductRepository;
import com.healthstore.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for handling order-related business logic.
 * Manages operations like creating, retrieving, and updating orders.
 */
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartService cartService;

    @Autowired
    public OrderService(OrderRepository orderRepository,
                       UserRepository userRepository,
                       ProductRepository productRepository,
                       CartService cartService) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.cartService = cartService;
    }

    /**
     * Creates a new order from the user's cart.
     * @param userId The ID of the user placing the order.
     * @param orderDTO The order details.
     * @return The created order.
     */
    @Transactional
    public Order createOrder(Long userId, OrderDTO orderDTO) {
        // Find the user
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Create a new order
        Order order = new Order();
        order.setUser(user);
        order.setStatus(Order.OrderStatus.PENDING);
        order.setTotalAmount(0.0);

        // Process order items
        double totalAmount = 0.0;
        for (OrderDTO.OrderItemDTO itemDTO : orderDTO.getItems()) {
            Product product = productRepository.findById(itemDTO.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + itemDTO.getProductId()));

            // Check if there's enough stock
            if (product.getStock() < itemDTO.getQuantity()) {
                throw new IllegalStateException("Insufficient stock for product: " + product.getName());
            }

            // Create order item
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(itemDTO.getQuantity());
            orderItem.setPricePerUnit(product.getPrice());
            orderItem.setTotalPrice(product.getPrice() * itemDTO.getQuantity());
            
            // Add to order
            order.addOrderItem(orderItem);
            totalAmount += orderItem.getTotalPrice();
            
            // Update product stock
            product.setStock(product.getStock() - itemDTO.getQuantity());
            productRepository.save(product);
        }

        order.setTotalAmount(totalAmount);
        
        // Clear the user's cart after order is placed
        cartService.clearCart(userId);
        
        return orderRepository.save(order);
    }

    /**
     * Retrieves an order by its ID.
     * @param orderId The ID of the order to retrieve.
     * @return The order if found.
     */
    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
    }

    /**
     * Retrieves all orders for a specific user.
     * @param userId The ID of the user.
     * @return A list of the user's orders.
     */
    public List<Order> getOrdersByUserId(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        return orderRepository.findByUserOrderByCreatedAtDesc(user);
    }

    /**
     * Updates the status of an order.
     * @param orderId The ID of the order to update.
     * @param status The new status.
     * @return The updated order.
     */
    @Transactional
    public Order updateOrderStatus(Long orderId, Order.OrderStatus status) {
        Order order = getOrderById(orderId);
        order.setStatus(status);
        return orderRepository.save(order);
    }

    /**
     * Cancels an order and restores product stock.
     * @param orderId The ID of the order to cancel.
     * @return The cancelled order.
     */
    @Transactional
    public Order cancelOrder(Long orderId) {
        Order order = getOrderById(orderId);
        
        // Only allow cancelling pending or processing orders
        if (order.getStatus() != Order.OrderStatus.PENDING && 
            order.getStatus() != Order.OrderStatus.PROCESSING) {
            throw new IllegalStateException("Cannot cancel order with status: " + order.getStatus());
        }
        
        // Restore product stock
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            product.setStock(product.getStock() + item.getQuantity());
            productRepository.save(product);
        }
        
        // Update order status
        order.setStatus(Order.OrderStatus.CANCELLED);
        return orderRepository.save(order);
    }
}
