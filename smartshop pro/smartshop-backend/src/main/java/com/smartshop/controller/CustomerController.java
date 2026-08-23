package com.smartshop.controller;

import com.smartshop.dto.ApiResponse;
import com.smartshop.dto.CustomerDto;
import com.smartshop.dto.CustomerRequest;
import com.smartshop.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CustomerDto>>> getAllCustomers() {
        return ResponseEntity.ok(ApiResponse.ok("Customers retrieved", customerService.getAllCustomers()));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<CustomerDto>>> searchCustomers(@RequestParam(name = "q", required = false) String query) {
        return ResponseEntity.ok(ApiResponse.ok("Search results", customerService.searchCustomers(query)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CustomerDto>> getCustomerById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Customer details", customerService.getCustomerById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CustomerDto>> createCustomer(@Valid @RequestBody CustomerRequest req) {
        CustomerDto created = customerService.createCustomer(req);
        return ResponseEntity.ok(ApiResponse.ok("Customer registered successfully", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CustomerDto>> updateCustomer(@PathVariable Long id, @Valid @RequestBody CustomerRequest req) {
        CustomerDto updated = customerService.updateCustomer(id, req);
        return ResponseEntity.ok(ApiResponse.ok("Customer updated successfully", updated));
    }
}
