package com.healthstore.dto.report;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * DTO for requesting a sales report with various filtering options.
 */
@Data
@Schema(description = "Request object for generating sales reports")
public class SalesReportRequest {

    @NotNull(message = "Start date is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Schema(required = true, example = "2023-01-01", description = "Start date of the report period (inclusive)")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Schema(required = true, example = "2023-12-31", description = "End date of the report period (inclusive)")
    private LocalDate endDate;

    @Schema(description = "ID of the category to filter by (optional)", example = "1")
    private Long categoryId;

    @Schema(description = "ID of the product to filter by (optional)", example = "42")
    private Long productId;

    @Schema(description = "Grouping period for the report", 
            example = "month", 
            allowableValues = {"day", "week", "month", "quarter", "year"})
    private String groupBy = "day";

    @Schema(description = "Whether to include detailed product breakdown in the report", example = "false")
    private boolean includeProductDetails = false;

    @Schema(description = "Whether to include customer statistics in the report", example = "true")
    private boolean includeCustomerStats = true;

    @Schema(description = "Whether to include inventory status in the report", example = "false")
    private boolean includeInventoryStatus = false;

    @Schema(description = "Threshold for low stock alerts (used when includeInventoryStatus is true)", example = "10")
    private int lowStockThreshold = 10;

    // Manual getter and setter methods to ensure compilation works when Lombok fails
    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getGroupBy() {
        return groupBy;
    }

    public void setGroupBy(String groupBy) {
        this.groupBy = groupBy;
    }

    public boolean isIncludeProductDetails() {
        return includeProductDetails;
    }

    public void setIncludeProductDetails(boolean includeProductDetails) {
        this.includeProductDetails = includeProductDetails;
    }

    public boolean isIncludeCustomerStats() {
        return includeCustomerStats;
    }

    public void setIncludeCustomerStats(boolean includeCustomerStats) {
        this.includeCustomerStats = includeCustomerStats;
    }

    public boolean isIncludeInventoryStatus() {
        return includeInventoryStatus;
    }

    public void setIncludeInventoryStatus(boolean includeInventoryStatus) {
        this.includeInventoryStatus = includeInventoryStatus;
    }

    public int getLowStockThreshold() {
        return lowStockThreshold;
    }

    public void setLowStockThreshold(int lowStockThreshold) {
        this.lowStockThreshold = lowStockThreshold;
    }
}
