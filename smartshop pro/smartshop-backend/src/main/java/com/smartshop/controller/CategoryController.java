package com.smartshop.controller;

import com.smartshop.dto.ApiResponse;
import com.smartshop.dto.CategoryDto;
import com.smartshop.service.CategoryService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryDto>>> getAllCategories() {
        return ResponseEntity.ok(ApiResponse.ok("Categories retrieved", categoryService.getAllCategories()));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<CategoryDto>>> getActiveCategories() {
        return ResponseEntity.ok(ApiResponse.ok("Active categories retrieved", categoryService.getActiveCategories()));
    }

    @PostMapping
    @PreAuthorize("hasRole('OWNER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CategoryDto>> createCategory(@Valid @RequestBody CategoryDto dto, HttpServletRequest request) {
        CategoryDto created = categoryService.createCategory(dto, request);
        return ResponseEntity.ok(ApiResponse.ok("Category created successfully", created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('OWNER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CategoryDto>> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryDto dto, HttpServletRequest request) {
        CategoryDto updated = categoryService.updateCategory(id, dto, request);
        return ResponseEntity.ok(ApiResponse.ok("Category updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('OWNER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Object>> deleteCategory(@PathVariable Long id, HttpServletRequest request) {
        categoryService.deleteCategory(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Category deactivated successfully"));
    }
}
