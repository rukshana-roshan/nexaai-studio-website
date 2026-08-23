package com.smartshop.controller;

import com.smartshop.dto.ApiResponse;
import com.smartshop.dto.ProductRequest;
import com.smartshop.dto.ProductResponse;
import com.smartshop.enums.Role;
import com.smartshop.security.UserPrincipal;
import com.smartshop.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    private boolean isOwnerOrAdmin(UserPrincipal principal) {
        return principal != null && (principal.getRole() == Role.ROLE_OWNER || principal.getRole() == Role.ROLE_ADMIN);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getAllProducts(@AuthenticationPrincipal UserPrincipal principal) {
        boolean isOwner = isOwnerOrAdmin(principal);
        return ResponseEntity.ok(ApiResponse.ok("Products retrieved", productService.getAllProducts(isOwner)));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getActiveProducts(@AuthenticationPrincipal UserPrincipal principal) {
        boolean isOwner = isOwnerOrAdmin(principal);
        return ResponseEntity.ok(ApiResponse.ok("Active products retrieved", productService.getActiveProducts(isOwner)));
    }

    @GetMapping("/archived")
    @PreAuthorize("hasRole('OWNER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getArchivedProducts(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.ok("Archived products retrieved", productService.getArchivedProducts(true)));
    }

    @GetMapping("/low-stock")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getLowStockProducts(@AuthenticationPrincipal UserPrincipal principal) {
        boolean isOwner = isOwnerOrAdmin(principal);
        return ResponseEntity.ok(ApiResponse.ok("Low stock products retrieved", productService.getLowStockProducts(isOwner)));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> searchProducts(
            @RequestParam(name = "q", required = false) String query,
            @AuthenticationPrincipal UserPrincipal principal) {
        boolean isOwner = isOwnerOrAdmin(principal);
        return ResponseEntity.ok(ApiResponse.ok("Search results", productService.searchProducts(query, isOwner)));
    }

    @GetMapping("/barcode/{barcode}")
    public ResponseEntity<ApiResponse<ProductResponse>> findByBarcode(
            @PathVariable String barcode,
            @AuthenticationPrincipal UserPrincipal principal) {
        boolean isOwner = isOwnerOrAdmin(principal);
        return ResponseEntity.ok(ApiResponse.ok("Product found", productService.findByBarcode(barcode, isOwner)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        boolean isOwner = isOwnerOrAdmin(principal);
        return ResponseEntity.ok(ApiResponse.ok("Product details", productService.getProductById(id, isOwner)));
    }

    @PostMapping
    @PreAuthorize("hasRole('OWNER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
            @Valid @RequestBody ProductRequest req,
            HttpServletRequest request) {
        ProductResponse created = productService.createProduct(req, request);
        return ResponseEntity.ok(ApiResponse.ok("Product created successfully", created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('OWNER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest req,
            HttpServletRequest request) {
        ProductResponse updated = productService.updateProduct(id, req, request);
        return ResponseEntity.ok(ApiResponse.ok("Product updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('OWNER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Object>> deleteProduct(
            @PathVariable Long id,
            HttpServletRequest request) {
        productService.deleteProduct(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Product archived successfully"));
    }

    @PostMapping("/{id}/restore")
    @PreAuthorize("hasRole('OWNER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductResponse>> restoreProduct(
            @PathVariable Long id,
            HttpServletRequest request) {
        ProductResponse restored = productService.restoreProduct(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Product restored successfully", restored));
    }
}
