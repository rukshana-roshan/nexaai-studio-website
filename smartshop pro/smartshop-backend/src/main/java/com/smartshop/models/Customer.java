package com.smartshop.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "customers", indexes = {
    @Index(name = "idx_customer_phone", columnList = "phone"),
    @Index(name = "idx_customer_name", columnList = "name")
})
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 120)
    private String name;

    @Column(length = 30)
    private String phone;

    @Column(length = 120)
    private String email;

    @Column(length = 255)
    private String address;

    @Column(length = 100)
    private String city;

    @NotNull
    @Column(nullable = false)
    private Integer totalPurchases = 0;

    @NotNull
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalSpent = BigDecimal.ZERO;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Customer() {}

    public Customer(String name, String phone, String email, String address, String city) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.city = city;
        this.totalPurchases = 0;
        this.totalSpent = BigDecimal.ZERO;
        this.createdAt = LocalDateTime.now();
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
