package com.healthstore.controller;

import com.healthstore.dto.UserActivityReportDTO;
import com.healthstore.dto.AuditFilterDTO;
import com.healthstore.dto.report.SalesReportRequest;
import com.healthstore.dto.report.SalesReportResponse;
import jakarta.validation.Valid;
import com.healthstore.service.AdminReportService;
import com.healthstore.service.SalesReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * REST controller for administrative reporting operations.
 * Requires ADMIN role for all endpoints.
 */
@RestController
@RequestMapping("/api/admin/reports")
@Tag(name = "Admin Reports", description = "Administrative reporting operations")
@PreAuthorize("hasRole('ADMIN')")
public class AdminReportController {

    private final SalesReportService salesReportService;
    private final AdminReportService adminReportService;

    @Autowired
    public AdminReportController(SalesReportService salesReportService, AdminReportService adminReportService) {
        this.salesReportService = salesReportService;
        this.adminReportService = adminReportService;
    }

    /**
     * Generates a sales report based on the provided criteria.
     *
     * @param request The sales report request containing filtering and grouping options
     * @return A SalesReportResponse containing the report data
     */
    @PostMapping("/sales")
    @Operation(summary = "Generate a sales report", 
                 description = "Generates a comprehensive sales report with various metrics and analytics")
    @ApiResponse(responseCode = "200", description = "Successfully generated sales report")
    @ApiResponse(responseCode = "400", description = "Invalid request parameters")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    public ResponseEntity<SalesReportResponse> generateSalesReport(
            @Valid @RequestBody SalesReportRequest request
    ) {
        SalesReportResponse response = salesReportService.generateSalesReport(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Generates a quick sales report for a given date range.
     * This is a simplified version of the sales report endpoint.
     *
     * @param startDate Start date of the report period (inclusive)
     * @param endDate End date of the report period (inclusive)
     * @param groupBy Grouping period (day, week, month, quarter, year)
     * @return A SalesReportResponse containing the report data
     */
    @GetMapping("/sales/quick")
    @Operation(summary = "Generate a quick sales report", 
                 description = "Generates a simplified sales report for a given date range")
    @ApiResponse(responseCode = "200", description = "Successfully generated quick sales report")
    @ApiResponse(responseCode = "400", description = "Invalid date range")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    public ResponseEntity<SalesReportResponse> generateQuickSalesReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "month") String groupBy
    ) {
        // Validate date range
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must be before or equal to end date");
        }

        // Create request object
        SalesReportRequest request = new SalesReportRequest();
        request.setStartDate(startDate);
        request.setEndDate(endDate);
        request.setGroupBy(groupBy);
        request.setIncludeCustomerStats(true);
        request.setIncludeInventoryStatus(true);

        SalesReportResponse response = salesReportService.generateSalesReport(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Gets the current inventory status.
     *
     * @param lowStockThreshold Threshold for considering items as low stock
     * @return Inventory status information
     */
    @GetMapping("/inventory/status")
    @Operation(summary = "Get inventory status", 
                 description = "Gets the current status of inventory including low stock items")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved inventory status")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    public ResponseEntity<SalesReportResponse.InventoryStatus> getInventoryStatus(
            @RequestParam(defaultValue = "10") int lowStockThreshold
    ) {
        return ResponseEntity.ok(salesReportService.generateInventoryStatus(lowStockThreshold));
    }
    
    /**
     * Generates a user activity report.
     *
     * @return A UserActivityReportDTO containing user activity metrics
     */
    @GetMapping("/user-activity")
    @Operation(summary = "Generate a user activity report", 
                 description = "Generates a report on user activity including total users, active users, and recent signups")
    @ApiResponse(responseCode = "200", description = "Successfully generated user activity report")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    public ResponseEntity<UserActivityReportDTO> getUserActivityReport() {
        UserActivityReportDTO report = adminReportService.getUserActivityReport();
        return ResponseEntity.ok(report);
    }
}