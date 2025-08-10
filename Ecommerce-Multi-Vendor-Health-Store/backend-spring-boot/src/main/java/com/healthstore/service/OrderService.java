package com.healthstore.service;

import com.healthstore.model.*;
import com.healthstore.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service class for handling Order-related business logic.
 */
@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final ProductService productService;
    private final UserService userService;

    public OrderService(OrderRepository orderRepository, 
                       CartService cartService, 
                       ProductService productService,
                       UserService userService) {
        this.orderRepository = orderRepository;
        this.cartService = cartService;
        this.productService = productService;
        this.userService = userService;
    }

    /**
     * Creates a new order from a user's cart.
     * This method is transactional to ensure atomicity of the order creation process.
     * @param user The user placing the order.
     * @param shippingAddress The shipping address for the order.
     * @return The newly created Order.
     * @throws RuntimeException if the cart is empty or the product stock is insufficient.
     */
    @Transactional
    public Order createOrderFromCart(User user, Address shippingAddress) {
        Cart cart = cartService.getUserCart(user);

        if (cart.getCartItems().isEmpty()) {
            throw new RuntimeException("Cannot create an order from an empty cart.");
        }

        Order order = new Order();
        order.setOrderNumber(generateOrderNumber());
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(Order.OrderStatus.PENDING);
        order.setShippingAddress(shippingAddress);

        BigDecimal totalAmount = BigDecimal.ZERO;
        
        for (CartItem cartItem : cart.getCartItems()) {
            Product product = cartItem.getProduct();
            int quantity = cartItem.getQuantity();

            if (product.getStock() < quantity) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
            }

            // Update product stock
            product.setStock(product.getStock() - quantity);
            productService.save(product);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(quantity);
            orderItem.setPriceAtPurchase(product.getPrice());
            orderItem.setFinalPrice(product.getPrice().multiply(BigDecimal.valueOf(quantity)));
            
            order.getOrderItems().add(orderItem);
            totalAmount = totalAmount.add(orderItem.getFinalPrice());
        }

        order.setTotalAmount(totalAmount);

        // Clear the user's cart after creating the order
        cart.getCartItems().clear();
        cartService.save(cart);

        return orderRepository.save(order);
    }

    /**
     * Generates a unique order number.
     * @return A unique order number string.
     */
    private String generateOrderNumber() {
        return "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    /**
     * Finds all orders for a specific user.
     * @param user The user whose orders to find.
     * @param pageable The pagination information.
     * @return A page of orders.
     */
    @Transactional(readOnly = true)
    public Page<Order> getOrdersByUser(User user, Pageable pageable) {
        return orderRepository.findByUserId(user.getId(), pageable);
    }

    /**
     * Finds an order by its ID.
     * @param orderId The ID of the order to find.
     * @return An Optional containing the order if found.
     */
    @Transactional(readOnly = true)
    public Optional<Order> getOrderById(Long orderId) {
        return orderRepository.findById(orderId);
    }

    /**
     * Updates the status of an order.
     * @param orderId The ID of the order to update.
     * @param newStatus The new status to set.
     * @return The updated order.
     * @throws RuntimeException if the order is not found.
     */
    @Transactional
    public Order updateOrderStatus(Long orderId, Order.OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
        
        order.setStatus(newStatus);
        
        // Update relevant timestamps based on status
        switch (newStatus) {
            case SHIPPED:
                order.setShippedDate(LocalDateTime.now());
                break;
            case DELIVERED:
                order.setDeliveredDate(LocalDateTime.now());
                break;
            case CANCELLED:
                order.setCancelledDate(LocalDateTime.now());
                // Restore product stock if order is cancelled
                restoreProductStock(order);
                break;
        }
        
        return orderRepository.save(order);
    }

    /**
     * Restores product stock when an order is cancelled.
     * @param order The cancelled order.
     */
    private void restoreProductStock(Order order) {
        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            product.setStock(product.getStock() + item.getQuantity());
            productService.save(product);
        }
    }

    /**
     * Finds all orders with pagination.
     * @param pageable The pagination information.
     * @return A page of orders.
     */
    @Transactional(readOnly = true)
    public Page<Order> findAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }
}
