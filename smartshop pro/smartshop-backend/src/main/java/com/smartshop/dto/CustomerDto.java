package com.smartshop.dto;

import com.smartshop.models.Customer;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CustomerDto {
    private Long id;
    private String name;
    private String phone;
    private String email;
    private String address;
    private String city;
    private Integer totalPurchases;
    private BigDecimal totalSpent;
    private LocalDateTime createdAt;

    public CustomerDto() {}

    public CustomerDto(Customer c) {
        this.id = c.getId();
        this.name = c.getName();
        this.phone = c.getPhone();
        this.email = c.getEmail();
        this.address = c.getAddress();
        this.city = c.getCity();
        this.totalPurchases = c.getTotalPurchases();
        this.totalSpent = c.getTotalSpent();
        this.createdAt = c.getCreatedAt();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public Integer getTotalPurchases() { return totalPurchases; }
    public void setTotalPurchases(Integer totalPurchases) { this.totalPurchases = totalPurchases; }

    public BigDecimal getTotalSpent() { return totalSpent; }
    public void setTotalSpent(BigDecimal totalSpent) { this.totalSpent = totalSpent; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
