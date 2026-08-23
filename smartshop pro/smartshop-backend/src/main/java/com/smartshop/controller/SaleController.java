package com.smartshop.controller;

import com.smartshop.dto.ApiResponse;
import com.smartshop.dto.SaleRequest;
import com.smartshop.dto.SaleResponseDto;
import com.smartshop.enums.Role;
import com.smartshop.security.UserPrincipal;
import com.smartshop.service.SaleService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sales")
public class SaleController {

    private final SaleService saleService;

    @Autowired
    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }

    private boolean isOwnerOrAdmin(UserPrincipal principal) {
        return principal != null && (principal.getRole() == Role.ROLE_OWNER || principal.getRole() == Role.ROLE_ADMIN);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SaleResponseDto>> createSale(
            @Valid @RequestBody SaleRequest req,
            @AuthenticationPrincipal UserPrincipal principal,
            HttpServletRequest request) {
        SaleResponseDto sale = saleService.createSale(req, principal, request);
        return ResponseEntity.ok(ApiResponse.ok("Sale completed successfully", sale));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SaleResponseDto>>> getAllSales(@AuthenticationPrincipal UserPrincipal principal) {
        boolean isOwner = isOwnerOrAdmin(principal);
        if (!isOwner) {
            // Cashier sees only their own sales
            return ResponseEntity.ok(ApiResponse.ok("Sales retrieved", saleService.getMySales(principal.getId(), false)));
        }
        return ResponseEntity.ok(ApiResponse.ok("All sales retrieved", saleService.getAllSales(true)));
    }

    @GetMapping("/my-sales")
    public ResponseEntity<ApiResponse<List<SaleResponseDto>>> getMySales(@AuthenticationPrincipal UserPrincipal principal) {
        boolean isOwner = isOwnerOrAdmin(principal);
        return ResponseEntity.ok(ApiResponse.ok("My sales retrieved", saleService.getMySales(principal.getId(), isOwner)));
    }

    @GetMapping("/today")
    public ResponseEntity<ApiResponse<List<SaleResponseDto>>> getTodaySales(@AuthenticationPrincipal UserPrincipal principal) {
        boolean isOwner = isOwnerOrAdmin(principal);
        return ResponseEntity.ok(ApiResponse.ok("Today's sales retrieved", saleService.getTodaySalesForCashier(principal.getId(), isOwner)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SaleResponseDto>> getSaleById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        boolean isOwner = isOwnerOrAdmin(principal);
        return ResponseEntity.ok(ApiResponse.ok("Sale details", saleService.getSaleById(id, isOwner)));
    }

    @GetMapping("/invoice/{invoiceNumber}")
    public ResponseEntity<ApiResponse<SaleResponseDto>> getSaleByInvoice(
            @PathVariable String invoiceNumber,
            @AuthenticationPrincipal UserPrincipal principal) {
        boolean isOwner = isOwnerOrAdmin(principal);
        return ResponseEntity.ok(ApiResponse.ok("Sale details", saleService.getSaleByInvoice(invoiceNumber, isOwner)));
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasRole('OWNER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Object>> cancelSale(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal,
            HttpServletRequest request) {
        saleService.cancelSale(id, principal, request);
        return ResponseEntity.ok(ApiResponse.ok("Sale cancelled and stock restored successfully"));
    }
}
