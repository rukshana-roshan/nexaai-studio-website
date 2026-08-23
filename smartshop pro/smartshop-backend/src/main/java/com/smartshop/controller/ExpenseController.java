package com.smartshop.controller;

import com.smartshop.dto.ApiResponse;
import com.smartshop.dto.ExpenseCategoryDto;
import com.smartshop.dto.ExpenseRequest;
import com.smartshop.dto.ExpenseResponseDto;
import com.smartshop.security.UserPrincipal;
import com.smartshop.service.ExpenseService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expenses")
@PreAuthorize("hasRole('OWNER') or hasRole('ADMIN')")
public class ExpenseController {

    private final ExpenseService expenseService;

    @Autowired
    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ExpenseResponseDto>>> getAllExpenses() {
        return ResponseEntity.ok(ApiResponse.ok("Expenses retrieved", expenseService.getAllExpenses()));
    }

    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<ExpenseCategoryDto>>> getAllCategories() {
        return ResponseEntity.ok(ApiResponse.ok("Expense categories retrieved", expenseService.getAllCategories()));
    }

    @PostMapping("/categories")
    public ResponseEntity<ApiResponse<ExpenseCategoryDto>> createCategory(@Valid @RequestBody ExpenseCategoryDto dto) {
        ExpenseCategoryDto created = expenseService.createCategory(dto);
        return ResponseEntity.ok(ApiResponse.ok("Expense category created", created));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ExpenseResponseDto>> createExpense(
            @Valid @RequestBody ExpenseRequest req,
            @AuthenticationPrincipal UserPrincipal principal,
            HttpServletRequest request) {
        ExpenseResponseDto created = expenseService.createExpense(req, principal, request);
        return ResponseEntity.ok(ApiResponse.ok("Expense logged successfully", created));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteExpense(@PathVariable Long id, HttpServletRequest request) {
        expenseService.deleteExpense(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Expense deleted successfully"));
    }
}
