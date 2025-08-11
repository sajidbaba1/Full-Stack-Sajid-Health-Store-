package com.healthstore.service;

import com.healthstore.dto.report.SalesReportRequest;
import com.healthstore.dto.report.SalesReportResponse;
import com.healthstore.model.*;
import com.healthstore.repository.OrderRepository;
import com.healthstore.repository.ProductVariantRepository;
import com.healthstore.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoField.ALIGNED_WEEK_OF_YEAR;

/**
 * Service for generating sales reports with various filtering and grouping options.
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class SalesReportService {

    private static final Logger log = LoggerFactory.getLogger(SalesReportService.class);

    private final AdminReportService adminReportService;
    private final OrderRepository orderRepository;
    private final ProductVariantRepository productVariantRepository;
    private final UserRepository userRepository;
    private final ProductService productService;

    public SalesReportService(AdminReportService adminReportService,
                            OrderRepository orderRepository,
                            ProductVariantRepository productVariantRepository,
                            UserRepository userRepository,
                            ProductService productService) {
        this.adminReportService = adminReportService;
        this.orderRepository = orderRepository;
        this.productVariantRepository = productVariantRepository;
        this.userRepository = userRepository;
        this.productService = productService;
    }

    /**
     * Generates a comprehensive sales report based on the provided request parameters.
     *
     * @param request The sales report request containing filtering and grouping options
     * @return A SalesReportResponse containing the report data
     */
    public SalesReportResponse generateSalesReport(SalesReportRequest request) {
        log.info("Generating sales report for period: {} to {}", request.getStartDate(), request.getEndDate());
        
        // Adjust dates to cover the full day
        LocalDateTime startDateTime = request.getStartDate().atStartOfDay();
        LocalDateTime endDateTime = request.getEndDate().plusDays(1).atStartOfDay();
        
        // Initialize the response
        SalesReportResponse response = new SalesReportResponse();
        response.setPeriodStart(request.getStartDate());
        response.setPeriodEnd(request.getEndDate());
        
        // Get basic sales data
        response.setTotalRevenue(adminReportService.getTotalRevenue(request.getStartDate(), request.getEndDate()));
        response.setTotalOrders(adminReportService.getTotalOrders(request.getStartDate(), request.getEndDate()));
        response.setAverageOrderValue(adminReportService.getAverageOrderValue(request.getStartDate(), request.getEndDate()));
        
        // Get sales trend data
        response.setSalesTrend(adminReportService.getSalesTrend(
                request.getStartDate(), 
                request.getEndDate(), 
                request.getGroupBy()
        ));
        
        // Get top selling products
        List<SalesReportResponse.ProductSalesData> topProducts = adminReportService
                .getTopPerformingProducts(request.getStartDate(), request.getEndDate(), 10)
                .stream()
                .map(this::mapToProductSalesData)
                .collect(Collectors.toList());
        response.setTopSellingProducts(topProducts);
        
        // Get customer statistics if requested
        if (request.isIncludeCustomerStats()) {
            response.setCustomerStats(generateCustomerStats(startDateTime, endDateTime));
        }
        
        // Get inventory status if requested
        if (request.isIncludeInventoryStatus()) {
            response.setInventoryStatus(generateInventoryStatus(request.getLowStockThreshold()));
        }
        
        return response;
    }
    
    /**
     * Generates customer statistics for the report.
     */
    private SalesReportResponse.CustomerStats generateCustomerStats(LocalDateTime startDate, LocalDateTime endDate) {
        SalesReportResponse.CustomerStats stats = new SalesReportResponse.CustomerStats();
        
        // Get all active customers (customers who placed orders in the period)
        List<User> activeCustomers = userRepository.findActiveCustomers(startDate, endDate);
        stats.setTotalCustomers(activeCustomers.size());
        
        if (!activeCustomers.isEmpty()) {
            // Calculate average orders per customer
            long totalOrders = orderRepository.countByOrderDateBetween(startDate, endDate);
            stats.setAverageOrdersPerCustomer(
                    (double) totalOrders / activeCustomers.size()
            );
            
            // Calculate average spending per customer
            BigDecimal totalRevenue = orderRepository.calculateTotalRevenue(startDate, endDate)
                    .orElse(BigDecimal.ZERO);
            stats.setAverageSpendingPerCustomer(
                    totalRevenue.divide(BigDecimal.valueOf(activeCustomers.size()), 2, RoundingMode.HALF_UP)
            );
            
            // Get top spending customers
            stats.setTopSpendingCustomers(
                    userRepository.findTopSpendingUsers(startDate, endDate, 5)
                            .stream()
                            .map(result -> {
                                User user = (User) result[0];
                                BigDecimal totalSpent = (BigDecimal) result[1];
                                
                                SalesReportResponse.CustomerSpending customerSpending = new SalesReportResponse.CustomerSpending();
                                customerSpending.setCustomerId(user.getId());
                                customerSpending.setCustomerName(user.getFullName());
                                customerSpending.setCustomerEmail(user.getEmail());
                                customerSpending.setTotalSpent(totalSpent);
                                customerSpending.setOrderCount(
                                        orderRepository.countByUserAndOrderDateBetween(user, startDate, endDate).intValue()
                                );
                                return customerSpending;
                            })
                            .collect(Collectors.toList())
            );
        }
        
        return stats;
    }
    
    /**
     * Generates inventory status for the report.
     */
    public SalesReportResponse.InventoryStatus generateInventoryStatus(int lowStockThreshold) {
        Map<String, Object> inventoryReport = adminReportService.getInventoryStatusReport(lowStockThreshold);
        
        SalesReportResponse.InventoryStatus status = new SalesReportResponse.InventoryStatus();
        status.setTotalItems((long) inventoryReport.get("totalItems"));
        status.setInStock((long) inventoryReport.get("inStock"));
        status.setOutOfStock((long) inventoryReport.get("outOfStock"));
        status.setLowStock((long) inventoryReport.get("lowStock"));
        status.setTotalInventoryValue((BigDecimal) inventoryReport.get("totalInventoryValue"));
        
        return status;
    }
    
    /**
     * Maps a product variant and its sales data to a ProductSalesData DTO.
     */
    private SalesReportResponse.ProductSalesData mapToProductSalesData(Map<String, Object> productData) {
        ProductVariant variant = (ProductVariant) productData.get("productVariant");
        long quantitySold = (long) productData.get("quantitySold");
        BigDecimal revenue = (BigDecimal) productData.get("revenue");
        
        SalesReportResponse.ProductSalesData salesData = new SalesReportResponse.ProductSalesData();
        salesData.setProductId(variant.getProduct().getId());
        salesData.setProductName(variant.getProduct().getName());
        salesData.setVariantId(variant.getId());
        salesData.setVariantName(buildVariantName(variant));
        salesData.setQuantitySold(quantitySold);
        salesData.setRevenue(revenue);
        salesData.setCurrentStock(variant.getStockQuantity());
        
        return salesData;
    }
    
    /**
     * Builds a user-friendly name for a product variant based on its options.
     */
    private String buildVariantName(ProductVariant variant) {
        if (variant.getOptions() == null || variant.getOptions().isEmpty()) {
            return "Default";
        }
        
        return variant.getOptions().stream()
                .map(option -> option.getOptionName() + ": " + option.getOptionValue())
                .collect(Collectors.joining(", "));
    }
}
