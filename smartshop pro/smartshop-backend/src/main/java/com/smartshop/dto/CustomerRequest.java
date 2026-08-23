package com.smartshop.dto;

import com.smartshop.models.Customer;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CustomerRequest {
    @NotBlank(message = "Customer name is required")
    private String name;

    private String phone;
    private String email;
    private String address;
    private String city;

    public CustomerRequest() {}

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
}
