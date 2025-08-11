package com.healthstore.service.impl;

import com.healthstore.model.*;
import com.healthstore.repository.OrderRepository;
import com.healthstore.repository.ProductRepository;
import com.healthstore.repository.ProductVariantRepository;
import com.healthstore.repository.UserRepository;
import com.healthstore.dto.UserActivityReportDTO;
import com.healthstore.service.AdminReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of the AdminReportService interface.
 */
@Service
@Transactional(readOnly = true)
public class AdminReportServiceImpl implements AdminReportService {

    private final OrderRepository orderRepository;
    private final ProductVariantRepository productVariantRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Autowired
    public AdminReportServiceImpl(OrderRepository orderRepository,
                                 ProductVariantRepository productVariantRepository,
                                 UserRepository userRepository,
                                 ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productVariantRepository = productVariantRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    @Override
    public Map<String, Object> generateSalesReport(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> report = new HashMap<>();
        
        // Adjust end date to include the entire end day
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay();
        
        // Get all orders in the date range
        List<Order> orders = orderRepository.findByOrderDateBetween(startDateTime, endDateTime);
        
        // Calculate metrics
        BigDecimal totalRevenue = orders.stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        long totalOrders = orders.size();
        BigDecimal averageOrderValue = totalOrders > 0 
                ? totalRevenue.divide(BigDecimal.valueOf(totalOrders), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        
        // Get top products
        List<Map<String, Object>> topProducts = getTopPerformingProducts(startDate, endDate, 5);
        
        // Get sales trend
        Map<String, BigDecimal> salesTrend = getSalesTrend(startDate, endDate, "day");
        
        // Prepare report
        report.put("periodStart", startDate);
        report.put("periodEnd", endDate);
        report.put("totalRevenue", totalRevenue);
        report.put("totalOrders", totalOrders);
        report.put("averageOrderValue", averageOrderValue);
        report.put("topProducts", topProducts);
        report.put("salesTrend", salesTrend);
        
        return report;
    }

    @Override
    public List<Map<String, Object>> getTopPerformingProducts(LocalDate startDate, LocalDate endDate, int limit) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay();
        
        return productVariantRepository.findTopSellingProducts(startDateTime, endDateTime, 
                PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "quantity")))
                .stream()
                .map(result -> {
                    Map<String, Object> productData = new HashMap<>();
                    ProductVariant variant = (ProductVariant) result[0];
                    Long quantity = (Long) result[1];
                    BigDecimal revenue = (BigDecimal) result[2];
                    
                    productData.put("productVariant", variant);
                    productData.put("quantitySold", quantity);
                    productData.put("revenue", revenue);
                    return productData;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getInventoryStatusReport(int lowStockThreshold) {
        Map<String, Object> report = new HashMap<>();
        
        // Get all product variants
        List<ProductVariant> variants = productVariantRepository.findAll();
        
        // Calculate metrics
        long totalItems = variants.size();
        long inStock = variants.stream().filter(v -> v.getStockQuantity() > 0).count();
        long outOfStock = variants.stream().filter(v -> v.getStockQuantity() <= 0).count();
        long lowStock = variants.stream()
                .filter(v -> v.getStockQuantity() > 0 && v.getStockQuantity() <= lowStockThreshold)
                .count();
        
        // Get low stock items
        List<ProductVariant> lowStockItems = getLowStockProducts(lowStockThreshold);
        
        // Calculate inventory value
        BigDecimal totalInventoryValue = variants.stream()
                .map(v -> BigDecimal.valueOf(v.getPrice()).multiply(BigDecimal.valueOf(v.getStockQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Prepare report
        report.put("totalItems", totalItems);
        report.put("inStock", inStock);
        report.put("outOfStock", outOfStock);
        report.put("lowStock", lowStock);
        report.put("lowStockItems", lowStockItems);
        report.put("totalInventoryValue", totalInventoryValue);
        
        return report;
    }

    @Override
    public List<Map<String, Object>> getCustomerActivityReport(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay();
        
        return userRepository.findActiveCustomers(startDateTime, endDateTime)
                .stream()
                .map(user -> {
                    Map<String, Object> activity = new HashMap<>();
                    List<Order> userOrders = orderRepository.findByUserAndOrderDateBetween(
                            user, startDateTime, endDateTime);
                    
                    BigDecimal totalSpent = userOrders.stream()
                            .map(Order::getTotalAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    
                    activity.put("user", user);
                    activity.put("orderCount", userOrders.size());
                    activity.put("totalSpent", totalSpent);
                    activity.put("lastOrderDate", userOrders.stream()
                            .map(Order::getOrderDate)
                            .max(LocalDateTime::compareTo)
                            .orElse(null));
                    
                    return activity;
                })
                .sorted((a, b) -> ((Long) b.get("orderCount")).compareTo((Long) a.get("orderCount")))
                .collect(Collectors.toList());
    }

    @Override
    public BigDecimal getTotalRevenue(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay();
        
        return orderRepository.calculateTotalRevenue(startDateTime, endDateTime)
                .orElse(BigDecimal.ZERO);
    }

    @Override
    public long getTotalOrders(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay();
        
        return orderRepository.countByOrderDateBetween(startDateTime, endDateTime);
    }

    @Override
    public BigDecimal getAverageOrderValue(LocalDate startDate, LocalDate endDate) {
        long orderCount = getTotalOrders(startDate, endDate);
        if (orderCount == 0) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal totalRevenue = getTotalRevenue(startDate, endDate);
        return totalRevenue.divide(BigDecimal.valueOf(orderCount), 2, RoundingMode.HALF_UP);
    }

    @Override
    public UserActivityReportDTO getUserActivityReport() {
        // Get total number of users
        long totalUsers = userRepository.count();
        
        // Get active users (logged in within last 30 days)
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        long activeUsersLast30Days = userRepository.countByLastLoginDateAfter(thirtyDaysAgo);
        
        // Get 10 most recent users
        List<User> recentUsers = userRepository.findTop10ByOrderByCreatedAtDesc();
        
        // Create and return the DTO using setter methods since constructor with args doesn't work
        UserActivityReportDTO dto = new UserActivityReportDTO();
        dto.setTotalUsers(totalUsers);
        dto.setActiveUsersLast30Days(activeUsersLast30Days);
        dto.setRecentUsers(recentUsers);
        return dto;
    }
    
    @Override
    public List<ProductVariant> getLowStockProducts(int threshold) {
        return productVariantRepository.findByStockQuantityLessThanEqual(threshold);
    }

    @Override
    public Map<String, BigDecimal> getSalesTrend(LocalDate startDate, LocalDate endDate, String period) {
        Map<String, BigDecimal> trend = new TreeMap<>();
        LocalDate current = startDate;
        
        // Initialize the trend map with all periods in the range
        while (!current.isAfter(endDate)) {
            String periodKey = formatPeriod(current, period);
            trend.put(periodKey, BigDecimal.ZERO);
            current = incrementPeriod(current, period);
        }
        
        // Get all orders in the date range
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay();
        List<Order> orders = orderRepository.findByOrderDateBetween(startDateTime, endDateTime);
        
        // Group orders by period and sum amounts
        orders.forEach(order -> {
            String periodKey = formatPeriod(order.getOrderDate().toLocalDate(), period);
            BigDecimal currentTotal = trend.getOrDefault(periodKey, BigDecimal.ZERO);
            trend.put(periodKey, currentTotal.add(order.getTotalAmount()));
        });
        
        return trend;
    }
    
    private String formatPeriod(LocalDate date, String period) {
        switch (period.toLowerCase()) {
            case "day":
                return date.toString();
            case "week":
                return "Week " + date.get(ChronoField.ALIGNED_WEEK_OF_YEAR) + ", " + date.getYear();
            case "month":
                return date.getMonth().toString() + " " + date.getYear();
            case "quarter":
                int quarter = (date.getMonthValue() - 1) / 3 + 1;
                return "Q" + quarter + " " + date.getYear();
            case "year":
                return String.valueOf(date.getYear());
            default:
                return date.toString();
        }
    }
    
    private LocalDate incrementPeriod(LocalDate date, String period) {
        switch (period.toLowerCase()) {
            case "day":
                return date.plusDays(1);
            case "week":
                return date.plusWeeks(1);
            case "month":
                return date.plusMonths(1);
            case "quarter":
                return date.plusMonths(3);
            case "year":
                return date.plusYears(1);
            default:
                return date.plusDays(1);
        }
    }
}
