package com.healthstore.service;

import com.healthstore.model.Order;
import com.healthstore.model.OrderItem;
import com.healthstore.model.Product;
import com.healthstore.model.User;
import com.healthstore.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    private final OrderService orderService;
    private final ProductRepository productRepository;

    @Autowired
    public RecommendationService(OrderService orderService, ProductRepository productRepository) {
        this.orderService = orderService;
        this.productRepository = productRepository;
    }

    public List<Product> getRecommendationsForUser(User user) {
        // Fetch the user's latest orders to determine their interests
        Pageable pageable = PageRequest.of(0, 5); // Look at the last 5 orders
        Page<Order> recentOrders = orderService.findOrdersByUser(user, pageable);

        if (recentOrders.isEmpty()) {
            // If no orders, recommend top-selling products or new arrivals
            // For now, we'll just return a few random products
            return productRepository.findAll(PageRequest.of(0, 5)).getContent();
        }

        // Collect all category IDs from the recent purchases
        Set<Long> categoryIds = new HashSet<>();
        for (Order order : recentOrders) {
            for (OrderItem item : order.getOrderItems()) {
                item.getProduct().getCategories().forEach(category -> categoryIds.add(category.getId()));
            }
        }

        // Find other products in those same categories, excluding those already purchased
        Set<Long> purchasedProductIds = recentOrders.stream()
            .flatMap(order -> order.getOrderItems().stream())
            .map(item -> item.getProduct().getId())
            .collect(Collectors.toSet());

        return productRepository.findProductsByCategoriesAndNotInIds(categoryIds, purchasedProductIds, PageRequest.of(0, 10));
    }
}