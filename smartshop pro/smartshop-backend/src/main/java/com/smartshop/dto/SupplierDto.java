package com.smartshop.dto;

import com.smartshop.models.Supplier;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class SupplierDto {
    private Long id;
    private String name;
    private String companyName;
    private String contactPerson;
    private String phone;
    private String email;
    private String address;
    private String taxNumber;
    private BigDecimal openingBalance;
    private BigDecimal currentBalance; // Total credit owed
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public SupplierDto() {}

    public SupplierDto(Supplier s) {
        this.id = s.getId();
        this.name = s.getName();
        this.companyName = s.getCompanyName();
        this.contactPerson = s.getContactPerson();
        this.phone = s.getPhone();
        this.email = s.getEmail();
        this.address = s.getAddress();
        this.taxNumber = s.getTaxNumber();
        this.openingBalance = s.getOpeningBalance();
        this.currentBalance = s.getCurrentBalance();
        this.active = s.isActive();
        this.createdAt = s.getCreatedAt();
        this.updatedAt = s.getUpdatedAt();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getContactPerson() { return contactPerson; }
    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getTaxNumber() { return taxNumber; }
    public void setTaxNumber(String taxNumber) { this.taxNumber = taxNumber; }

    public BigDecimal getOpeningBalance() { return openingBalance; }
    public void setOpeningBalance(BigDecimal openingBalance) { this.openingBalance = openingBalance; }

    public BigDecimal getCurrentBalance() { return currentBalance; }
    public void setCurrentBalance(BigDecimal currentBalance) { this.currentBalance = currentBalance; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
