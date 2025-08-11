package com.healthstore.service;

import com.healthstore.dto.UserActivityReportDTO;
import com.healthstore.model.Order;
import com.healthstore.model.Product;
import com.healthstore.model.ProductVariant;
import com.healthstore.model.User;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Service interface for generating various administrative reports.
 */
public interface AdminReportService {

    /**
     * Generates a sales report for the specified date range.
     *
     * @param startDate The start date of the report period
     * @param endDate The end date of the report period
     * @return A map containing various sales metrics
     */
    Map<String, Object> generateSalesReport(LocalDate startDate, LocalDate endDate);

    /**
     * Generates a product performance report.
     *
     * @param startDate The start date of the report period
     * @param endDate The end date of the report period
     * @param limit The maximum number of products to include in the report
     * @return A list of product performance data
     */
    List<Map<String, Object>> getTopPerformingProducts(LocalDate startDate, LocalDate endDate, int limit);

    /**
     * Generates an inventory status report.
     *
     * @param lowStockThreshold The threshold below which items are considered low stock
     * @return A map containing inventory status information
     */
    Map<String, Object> getInventoryStatusReport(int lowStockThreshold);

    /**
     * Generates a customer activity report.
     *
     * @param startDate The start date of the report period
     * @param endDate The end date of the report period
     * @return A list of customer activity data
     */
    List<Map<String, Object>> getCustomerActivityReport(LocalDate startDate, LocalDate endDate);

    /**
     * Gets the total revenue for a specific period.
     *
     * @param startDate The start date of the period
     * @param endDate The end date of the period
     * @return The total revenue as a BigDecimal
     */
    BigDecimal getTotalRevenue(LocalDate startDate, LocalDate endDate);

    /**
     * Gets the total number of orders for a specific period.
     *
     * @param startDate The start date of the period
     * @param endDate The end date of the period
     * @return The total number of orders
     */
    long getTotalOrders(LocalDate startDate, LocalDate endDate);

    /**
     * Gets the average order value for a specific period.
     *
     * @param startDate The start date of the period
     * @param endDate The end date of the period
     * @return The average order value as a BigDecimal
     */
    BigDecimal getAverageOrderValue(LocalDate startDate, LocalDate endDate);

    /**
     * Gets a list of products that are running low on stock.
     *
     * @param threshold The stock threshold below which products are considered low
     * @return A list of products with low stock
     */
    List<ProductVariant> getLowStockProducts(int threshold);

    /**
     * Gets sales data grouped by a specific time period.
     *
     * @param startDate The start date of the period
     * @param endDate The end date of the period
     * @param period The time period to group by (e.g., "day", "week", "month")
     * @return A map of time periods to sales data
     */
    Map<String, BigDecimal> getSalesTrend(LocalDate startDate, LocalDate endDate, String period);
    
    /**
     * Generates a user activity report.
     *
     * @return A UserActivityReportDTO containing user activity metrics
     */
    UserActivityReportDTO getUserActivityReport();
}
