package com.smartshop.service;

import com.smartshop.dto.CustomerDto;
import com.smartshop.dto.CustomerRequest;
import com.smartshop.models.Customer;
import com.smartshop.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Transactional(readOnly = true)
    public List<CustomerDto> getAllCustomers() {
        return customerRepository.findAll().stream().map(CustomerDto::new).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CustomerDto> searchCustomers(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllCustomers();
        }
        return customerRepository.searchCustomers(query.trim()).stream().map(CustomerDto::new).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CustomerDto getCustomerById(Long id) {
        Customer c = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));
        return new CustomerDto(c);
    }

    @Transactional
    public CustomerDto createCustomer(CustomerRequest req) {
        if (req.getPhone() != null && !req.getPhone().trim().isEmpty() &&
                customerRepository.findByPhone(req.getPhone().trim()).isPresent()) {
            throw new RuntimeException("Customer with phone " + req.getPhone() + " already exists");
        }

        Customer customer = new Customer(
                req.getName().trim(),
                req.getPhone() != null ? req.getPhone().trim() : null,
                req.getEmail(),
                req.getAddress(),
                req.getCity()
        );

        return new CustomerDto(customerRepository.save(customer));
    }

    @Transactional
    public CustomerDto updateCustomer(Long id, CustomerRequest req) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));

        customer.setName(req.getName().trim());
        customer.setPhone(req.getPhone());
        customer.setEmail(req.getEmail());
        customer.setAddress(req.getAddress());
        customer.setCity(req.getCity());

        return new CustomerDto(customerRepository.save(customer));
    }
}
