package com.smartshop.controller;

import com.smartshop.dto.ApiResponse;
import com.smartshop.dto.PasswordResetRequest;
import com.smartshop.dto.RegisterCashierRequest;
import com.smartshop.dto.UserDto;
import com.smartshop.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasRole('OWNER') or hasRole('ADMIN')")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserDto>>> getAllUsers() {
        return ResponseEntity.ok(ApiResponse.ok("Users retrieved", userService.getAllUsers()));
    }

    @GetMapping("/cashiers")
    public ResponseEntity<ApiResponse<List<UserDto>>> getCashiers() {
        return ResponseEntity.ok(ApiResponse.ok("Cashiers retrieved", userService.getCashiers()));
    }

    @PostMapping("/cashier")
    public ResponseEntity<ApiResponse<UserDto>> createCashier(@Valid @RequestBody RegisterCashierRequest req, HttpServletRequest request) {
        UserDto created = userService.createCashier(req, request);
        return ResponseEntity.ok(ApiResponse.ok("Cashier account created successfully", created));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<UserDto>> toggleStatus(@PathVariable Long id, HttpServletRequest request) {
        UserDto updated = userService.toggleUserStatus(id, request);
        return ResponseEntity.ok(ApiResponse.ok("User status updated", updated));
    }

    @PostMapping("/{id}/reset-password")
    public ResponseEntity<ApiResponse<Object>> resetPassword(@PathVariable Long id, @Valid @RequestBody PasswordResetRequest req, HttpServletRequest request) {
        userService.resetPassword(id, req, request);
        return ResponseEntity.ok(ApiResponse.ok("Password reset successfully"));
    }
}
