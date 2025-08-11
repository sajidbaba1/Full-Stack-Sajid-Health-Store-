package com.healthstore.service;

import com.healthstore.exception.InsufficientStockException;
import com.healthstore.exception.ResourceNotFoundException;
import com.healthstore.model.*;
import com.healthstore.repository.OrderRepository;
import com.healthstore.repository.ProductVariantRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class for handling Order-related business logic.
 */
@Service
@Transactional
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final ProductService productService;
    private final UserService userService;
    private final InventoryService inventoryService;
    private final ProductVariantRepository productVariantRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository, 
                       CartService cartService, 
                       ProductService productService,
                       UserService userService,
                       InventoryService inventoryService,
                       ProductVariantRepository productVariantRepository) {
        this.orderRepository = orderRepository;
        this.cartService = cartService;
        this.productService = productService;
        this.userService = userService;
        this.inventoryService = inventoryService;
        this.productVariantRepository = productVariantRepository;
    }

    /**
     * Creates a new order from a user's cart.
     * This method is transactional to ensure atomicity of the order creation process.
     * @param user The user placing the order.
     * @param shippingAddress The shipping address for the order.
     * @return The newly created Order.
     * @throws RuntimeException if the cart is empty or the product stock is insufficient.
     */
    /**
     * Creates a new order from a user's cart with inventory validation.
     * This method is transactional to ensure atomicity of the order creation process.
     * @param user The user placing the order.
     * @param shippingAddress The shipping address for the order.
     * @return The newly created Order.
     * @throws InsufficientStockException if any product variant is out of stock
     * @throws ResourceNotFoundException if any resource is not found
     */
    @Transactional
    public Order createOrderFromCart(User user, Address shippingAddress) {
        Cart cart = cartService.getUserCart(user);

        if (cart.getCartItems().isEmpty()) {
            throw new IllegalArgumentException("Cannot create an order from an empty cart.");
        }

        // First, validate all items are in stock
        Map<Long, Integer> variantQuantities = new HashMap<>();
        for (CartItem cartItem : cart.getCartItems()) {
            if (cartItem.getProductVariant() == null) {
                throw new IllegalStateException("Cart item is missing product variant information");
            }
            variantQuantities.put(cartItem.getProductVariant().getId(), cartItem.getQuantity());
        }

        try {
            // Check if all items are in stock (throws InsufficientStockException if not)
            if (!inventoryService.areAllInStock(variantQuantities)) {
                throw new InsufficientStockException("One or more items are out of stock");
            }

            // Create the order
            Order order = new Order();
            order.setOrderNumber(generateOrderNumber());
            order.setUser(user);
            order.setOrderDate(LocalDateTime.now());
            order.setStatus(Order.OrderStatus.PENDING);
            order.setShippingAddress(shippingAddress);

            BigDecimal totalAmount = BigDecimal.ZERO;
            
            // Process each cart item
            for (CartItem cartItem : cart.getCartItems()) {
                ProductVariant variant = productVariantRepository.findById(cartItem.getProductVariant().getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Product variant not found: " + cartItem.getProductVariant().getId()));
                
                int quantity = cartItem.getQuantity();
                BigDecimal itemPrice = BigDecimal.valueOf(variant.getPrice());
                BigDecimal itemTotal = itemPrice.multiply(BigDecimal.valueOf(quantity));

                // Create order item
                OrderItem orderItem = new OrderItem();
                orderItem.setOrder(order);
                orderItem.setProductVariant(variant);
                orderItem.setQuantity(quantity);
                orderItem.setPriceAtPurchase(itemPrice);
                orderItem.setFinalPrice(itemTotal);
                
                order.getOrderItems().add(orderItem);
                totalAmount = totalAmount.add(itemTotal);
                
                // Update inventory (will throw InsufficientStockException if stock is not available)
                inventoryService.reduceStock(variant.getId(), quantity);
            }

            order.setTotalAmount(totalAmount);

            // Clear the user's cart after successful order creation
            cart.getCartItems().clear();
            cartService.save(cart);

            return orderRepository.save(order);
            
        } catch (Exception e) {
            logger.error("Error creating order: " + e.getMessage(), e);
            throw e;
        }
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
    
    /**
     * Updates the payment information for an order.
     * This is typically called from a webhook when a payment status changes.
     *
     * @param orderId The ID of the order to update
     * @param paymentIntentId The Stripe payment intent ID
     * @param paymentStatus The current payment status
     * @return The updated order
     * @throws ResourceNotFoundException if the order is not found
     */
    @Transactional
    public Order updateOrderPaymentInfo(Long orderId, String paymentIntentId, String paymentStatus) {
        logger.info("Updating payment info for order {}: paymentIntentId={}, status={}", 
                   orderId, paymentIntentId, paymentStatus);
        
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
            
        order.setPaymentIntentId(paymentIntentId);
        order.setPaymentStatus(paymentStatus);
        
        // Update order status based on payment status
        if ("paid".equalsIgnoreCase(paymentStatus)) {
            order.setStatus(Order.OrderStatus.PAID);
            order.setPaymentDate(LocalDateTime.now());
        } else if ("unpaid".equalsIgnoreCase(paymentStatus)) {
            order.setStatus(Order.OrderStatus.PENDING);
        } else if ("refunded".equalsIgnoreCase(paymentStatus)) {
            order.setStatus(Order.OrderStatus.REFUNDED);
            order.setRefunded(true);
            order.setRefundDate(LocalDateTime.now());
        }
        
        return orderRepository.save(order);
    }
    
    /**
     * Finds an order by its ID.
     * @param orderId The ID of the order to find.
     * @return The order if found.
     * @throws ResourceNotFoundException if the order is not found.
     */
    @Transactional(readOnly = true)
    public Order findById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
    }
    
    /**
     * Gets orders for a user (alias for getOrdersByUser).
     * @param user The user whose orders to retrieve.
     * @return A page of orders.
     */
    @Transactional(readOnly = true)
    public Page<Order> getUserOrders(User user) {
        return orderRepository.findByUserId(user.getId(), Pageable.unpaged());
    }
    
    /**
     * Finds orders for a user with pagination (alias method).
     * @param user The user whose orders to find.
     * @param pageable The pagination information.
     * @return A page of orders.
     */
    @Transactional(readOnly = true)
    public Page<Order> findOrdersByUser(User user, Pageable pageable) {
        return orderRepository.findByUserId(user.getId(), pageable);
    }
}
