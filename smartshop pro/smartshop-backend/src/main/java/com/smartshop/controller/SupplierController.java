package com.smartshop.controller;

import com.smartshop.dto.*;
import com.smartshop.models.SupplierPayment;
import com.smartshop.security.UserPrincipal;
import com.smartshop.service.SupplierService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
@PreAuthorize("hasRole('OWNER') or hasRole('ADMIN')")
public class SupplierController {

    private final SupplierService supplierService;

    @Autowired
    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SupplierDto>>> getAllSuppliers() {
        return ResponseEntity.ok(ApiResponse.ok("Suppliers retrieved", supplierService.getAllSuppliers()));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<SupplierDto>>> getActiveSuppliers() {
        return ResponseEntity.ok(ApiResponse.ok("Active suppliers retrieved", supplierService.getActiveSuppliers()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SupplierDto>> getSupplierById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Supplier details", supplierService.getSupplierById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SupplierDto>> createSupplier(
            @Valid @RequestBody SupplierRequest req,
            @AuthenticationPrincipal UserPrincipal principal,
            HttpServletRequest request) {
        SupplierDto created = supplierService.createSupplier(req, principal, request);
        return ResponseEntity.ok(ApiResponse.ok("Supplier created successfully", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SupplierDto>> updateSupplier(
            @PathVariable Long id,
            @Valid @RequestBody SupplierRequest req,
            HttpServletRequest request) {
        SupplierDto updated = supplierService.updateSupplier(id, req, request);
        return ResponseEntity.ok(ApiResponse.ok("Supplier updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteSupplier(@PathVariable Long id, HttpServletRequest request) {
        supplierService.deleteSupplier(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Supplier deactivated successfully"));
    }

    // Purchases & Stock Inward
    @PostMapping("/purchases")
    public ResponseEntity<ApiResponse<PurchaseResponseDto>> createPurchase(
            @Valid @RequestBody PurchaseRequest req,
            @AuthenticationPrincipal UserPrincipal principal,
            HttpServletRequest request) {
        PurchaseResponseDto purchase = supplierService.createPurchase(req, principal, request);
        return ResponseEntity.ok(ApiResponse.ok("Purchase bill recorded and inventory stock updated", purchase));
    }

    @GetMapping("/purchases")
    public ResponseEntity<ApiResponse<List<PurchaseResponseDto>>> getAllPurchases() {
        return ResponseEntity.ok(ApiResponse.ok("Purchases retrieved", supplierService.getAllPurchases()));
    }

    @GetMapping("/purchases/{id}")
    public ResponseEntity<ApiResponse<PurchaseResponseDto>> getPurchaseById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Purchase bill details", supplierService.getPurchaseById(id)));
    }

    // Supplier Payments & Debit
    @PostMapping("/payments")
    public ResponseEntity<ApiResponse<SupplierPayment>> recordPayment(
            @Valid @RequestBody SupplierPaymentRequest req,
            @AuthenticationPrincipal UserPrincipal principal,
            HttpServletRequest request) {
        SupplierPayment payment = supplierService.recordPayment(req, principal, request);
        return ResponseEntity.ok(ApiResponse.ok("Payment recorded and ledger updated", payment));
    }

    @GetMapping("/payments")
    public ResponseEntity<ApiResponse<List<SupplierPayment>>> getAllPayments() {
        return ResponseEntity.ok(ApiResponse.ok("Payments retrieved", supplierService.getAllPayments()));
    }

    @GetMapping("/{id}/payments")
    public ResponseEntity<ApiResponse<List<SupplierPayment>>> getSupplierPayments(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Supplier payments retrieved", supplierService.getSupplierPayments(id)));
    }

    // Running Ledger
    @GetMapping("/{id}/ledger")
    public ResponseEntity<ApiResponse<List<LedgerResponseDto>>> getSupplierLedger(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Supplier running ledger statement", supplierService.getSupplierLedger(id)));
    }
}
