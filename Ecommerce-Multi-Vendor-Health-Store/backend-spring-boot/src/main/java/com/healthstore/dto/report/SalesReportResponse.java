package com.healthstore.dto.report;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * DTO for the sales report response.
 */
@Data
@Schema(description = "Response object containing sales report data")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SalesReportResponse {

    @Schema(description = "Start date of the report period")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate periodStart;

    @Schema(description = "End date of the report period")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate periodEnd;

    @Schema(description = "Total revenue for the period")
    private BigDecimal totalRevenue;

    @Schema(description = "Total number of orders for the period")
    private long totalOrders;

    @Schema(description = "Average order value for the period")
    private BigDecimal averageOrderValue;

    @Schema(description = "Number of new customers for the period")
    private Long newCustomers;

    @Schema(description = "Number of returning customers for the period")
    private Long returningCustomers;

    @Schema(description = "Sales trend data grouped by the specified period")
    private Map<String, BigDecimal> salesTrend;

    @Schema(description = "List of top selling products for the period")
    private List<ProductSalesData> topSellingProducts;

    @Schema(description = "Customer activity statistics")
    private CustomerStats customerStats;

    @Schema(description = "Inventory status summary")
    private InventoryStatus inventoryStatus;

    // Manual getter and setter methods to ensure compilation works when Lombok fails
    public LocalDate getPeriodStart() {
        return periodStart;
    }

    public void setPeriodStart(LocalDate periodStart) {
        this.periodStart = periodStart;
    }

    public LocalDate getPeriodEnd() {
        return periodEnd;
    }

    public void setPeriodEnd(LocalDate periodEnd) {
        this.periodEnd = periodEnd;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public long getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(long totalOrders) {
        this.totalOrders = totalOrders;
    }

    public BigDecimal getAverageOrderValue() {
        return averageOrderValue;
    }

    public void setAverageOrderValue(BigDecimal averageOrderValue) {
        this.averageOrderValue = averageOrderValue;
    }

    public Long getNewCustomers() {
        return newCustomers;
    }

    public void setNewCustomers(Long newCustomers) {
        this.newCustomers = newCustomers;
    }

    public Long getReturningCustomers() {
        return returningCustomers;
    }

    public void setReturningCustomers(Long returningCustomers) {
        this.returningCustomers = returningCustomers;
    }

    public Map<String, BigDecimal> getSalesTrend() {
        return salesTrend;
    }

    public void setSalesTrend(Map<String, BigDecimal> salesTrend) {
        this.salesTrend = salesTrend;
    }

    public List<ProductSalesData> getTopSellingProducts() {
        return topSellingProducts;
    }

    public void setTopSellingProducts(List<ProductSalesData> topSellingProducts) {
        this.topSellingProducts = topSellingProducts;
    }

    public CustomerStats getCustomerStats() {
        return customerStats;
    }

    public void setCustomerStats(CustomerStats customerStats) {
        this.customerStats = customerStats;
    }

    public InventoryStatus getInventoryStatus() {
        return inventoryStatus;
    }

    public void setInventoryStatus(InventoryStatus inventoryStatus) {
        this.inventoryStatus = inventoryStatus;
    }

    @Data
    @Schema(description = "Product sales data")
    public static class ProductSalesData {
        @Schema(description = "Product ID")
        private Long productId;
        
        @Schema(description = "Product name")
        private String productName;
        
        @Schema(description = "Product variant ID")
        private Long variantId;
        
        @Schema(description = "Variant name/description")
        private String variantName;
        
        @Schema(description = "Number of units sold")
        private long quantitySold;
        
        @Schema(description = "Total revenue generated")
        private BigDecimal revenue;
        
        @Schema(description = "Current stock quantity")
        private Integer currentStock;

        // Manual getter and setter methods
        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }
        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }
        public Long getVariantId() { return variantId; }
        public void setVariantId(Long variantId) { this.variantId = variantId; }
        public String getVariantName() { return variantName; }
        public void setVariantName(String variantName) { this.variantName = variantName; }
        public long getQuantitySold() { return quantitySold; }
        public void setQuantitySold(long quantitySold) { this.quantitySold = quantitySold; }
        public BigDecimal getRevenue() { return revenue; }
        public void setRevenue(BigDecimal revenue) { this.revenue = revenue; }
        public Integer getCurrentStock() { return currentStock; }
        public void setCurrentStock(Integer currentStock) { this.currentStock = currentStock; }
    }

    @Data
    @Schema(description = "Customer statistics")
    public static class CustomerStats {
        @Schema(description = "Total number of unique customers")
        private long totalCustomers;
        
        @Schema(description = "Average orders per customer")
        private double averageOrdersPerCustomer;
        
        @Schema(description = "Average spending per customer")
        private BigDecimal averageSpendingPerCustomer;
        
        @Schema(description = "List of top spending customers")
        private List<CustomerSpending> topSpendingCustomers;

        // Manual getter and setter methods
        public long getTotalCustomers() { return totalCustomers; }
        public void setTotalCustomers(long totalCustomers) { this.totalCustomers = totalCustomers; }
        public double getAverageOrdersPerCustomer() { return averageOrdersPerCustomer; }
        public void setAverageOrdersPerCustomer(double averageOrdersPerCustomer) { this.averageOrdersPerCustomer = averageOrdersPerCustomer; }
        public BigDecimal getAverageSpendingPerCustomer() { return averageSpendingPerCustomer; }
        public void setAverageSpendingPerCustomer(BigDecimal averageSpendingPerCustomer) { this.averageSpendingPerCustomer = averageSpendingPerCustomer; }
        public List<CustomerSpending> getTopSpendingCustomers() { return topSpendingCustomers; }
        public void setTopSpendingCustomers(List<CustomerSpending> topSpendingCustomers) { this.topSpendingCustomers = topSpendingCustomers; }
    }

    @Data
    @Schema(description = "Customer spending information")
    public static class CustomerSpending {
        @Schema(description = "Customer ID")
        private Long customerId;
        
        @Schema(description = "Customer name")
        private String customerName;
        
        @Schema(description = "Customer email")
        private String customerEmail;
        
        @Schema(description = "Total amount spent")
        private BigDecimal totalSpent;
        
        @Schema(description = "Number of orders placed")
        private int orderCount;

        // Manual getter and setter methods
        public Long getCustomerId() { return customerId; }
        public void setCustomerId(Long customerId) { this.customerId = customerId; }
        public String getCustomerName() { return customerName; }
        public void setCustomerName(String customerName) { this.customerName = customerName; }
        public String getCustomerEmail() { return customerEmail; }
        public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
        public BigDecimal getTotalSpent() { return totalSpent; }
        public void setTotalSpent(BigDecimal totalSpent) { this.totalSpent = totalSpent; }
        public int getOrderCount() { return orderCount; }
        public void setOrderCount(int orderCount) { this.orderCount = orderCount; }
    }

    @Data
    @Schema(description = "Inventory status summary")
    public static class InventoryStatus {
        @Schema(description = "Total number of product variants")
        private long totalItems;
        
        @Schema(description = "Number of items in stock")
        private long inStock;
        
        @Schema(description = "Number of items out of stock")
        private long outOfStock;
        
        @Schema(description = "Number of items with low stock")
        private long lowStock;
        
        @Schema(description = "Total value of inventory")
        private BigDecimal totalInventoryValue;

        // Manual getter and setter methods
        public long getTotalItems() { return totalItems; }
        public void setTotalItems(long totalItems) { this.totalItems = totalItems; }
        public long getInStock() { return inStock; }
        public void setInStock(long inStock) { this.inStock = inStock; }
        public long getOutOfStock() { return outOfStock; }
        public void setOutOfStock(long outOfStock) { this.outOfStock = outOfStock; }
        public long getLowStock() { return lowStock; }
        public void setLowStock(long lowStock) { this.lowStock = lowStock; }
        public BigDecimal getTotalInventoryValue() { return totalInventoryValue; }
        public void setTotalInventoryValue(BigDecimal totalInventoryValue) { this.totalInventoryValue = totalInventoryValue; }
    }
}
