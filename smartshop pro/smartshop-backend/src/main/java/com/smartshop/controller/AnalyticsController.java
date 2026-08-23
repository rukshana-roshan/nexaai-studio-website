package com.smartshop.controller;

import com.smartshop.dto.*;
import com.smartshop.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/analytics")
@PreAuthorize("hasRole('OWNER') or hasRole('ADMIN')")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @Autowired
    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<DashboardSummaryDto>> getDashboardSummary() {
        return ResponseEntity.ok(ApiResponse.ok("Dashboard metrics retrieved", analyticsService.getDashboardSummary()));
    }

    @GetMapping("/sales-trend")
    public ResponseEntity<ApiResponse<List<SalesTrendDto>>> getSalesTrend(
            @RequestParam(name = "days", defaultValue = "7") int days) {
        return ResponseEntity.ok(ApiResponse.ok("Sales trend retrieved", analyticsService.getSalesTrend(days)));
    }

    @GetMapping("/top-products")
    public ResponseEntity<ApiResponse<List<TopProductDto>>> getTopProducts(
            @RequestParam(name = "limit", defaultValue = "10") int limit) {
        return ResponseEntity.ok(ApiResponse.ok("Top products retrieved", analyticsService.getTopProducts(limit)));
    }

    @GetMapping("/category-distribution")
    public ResponseEntity<ApiResponse<List<CategorySalesDto>>> getCategoryDistribution() {
        return ResponseEntity.ok(ApiResponse.ok("Category sales distribution", analyticsService.getCategoryDistribution()));
    }

    @GetMapping("/profit-loss")
    public ResponseEntity<ApiResponse<ProfitLossStatementDto>> getProfitLossStatement(
            @RequestParam(name = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(name = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        LocalDateTime start = (startDate != null) ? startDate.atStartOfDay() : LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime end = (endDate != null) ? endDate.plusDays(1).atStartOfDay() : LocalDate.now().plusDays(1).atStartOfDay();

        return ResponseEntity.ok(ApiResponse.ok("Profit & Loss statement", analyticsService.getProfitLossStatement(start, end)));
    }
}
