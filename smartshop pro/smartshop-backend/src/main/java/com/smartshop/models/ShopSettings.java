package com.smartshop.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "shop_settings")
public class ShopSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 150)
    private String shopName = "SmartShop Pro";

    @Column(length = 200)
    private String tagline = "Home Appliances, Electronics, Plastics & Furniture";

    @Column(length = 255)
    private String address = "124 Market Boulevard, Commercial District";

    @Column(length = 50)
    private String phone = "+1 (555) 234-5678";

    @Column(length = 120)
    private String email = "support@smartshoppro.com";

    @Column(length = 100)
    private String website = "www.smartshoppro.com";

    @Column(length = 60)
    private String taxNumber = "GST-TAX-987654321";

    @Column(length = 10)
    private String currencySymbol = "$";

    @Column(length = 10)
    private String currencyCode = "USD";

    @Column(precision = 6, scale = 2)
    private BigDecimal defaultTaxRate = new BigDecimal("5.00");

    @Column(nullable = false)
    private boolean enableTax = true;

    @Column(nullable = false)
    private Integer defaultLowStockAlert = 5;

    @Column(length = 300)
    private String receiptHeader = "Thank you for shopping with SmartShop Pro!";

    @Column(length = 300)
    private String receiptFooter = "Goods once sold are covered under standard manufacturer warranty. Visit us again!";

    private LocalDateTime updatedAt = LocalDateTime.now();

    public ShopSettings() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getShopName() { return shopName; }
    public void setShopName(String shopName) { this.shopName = shopName; }

    public String getTagline() { return tagline; }
    public void setTagline(String tagline) { this.tagline = tagline; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }

    public String getTaxNumber() { return taxNumber; }
    public void setTaxNumber(String taxNumber) { this.taxNumber = taxNumber; }

    public String getCurrencySymbol() { return currencySymbol; }
    public void setCurrencySymbol(String currencySymbol) { this.currencySymbol = currencySymbol; }

    public String getCurrencyCode() { return currencyCode; }
    public void setCurrencyCode(String currencyCode) { this.currencyCode = currencyCode; }

    public BigDecimal getDefaultTaxRate() { return defaultTaxRate; }
    public void setDefaultTaxRate(BigDecimal defaultTaxRate) { this.defaultTaxRate = defaultTaxRate; }

    public boolean isEnableTax() { return enableTax; }
    public void setEnableTax(boolean enableTax) { this.enableTax = enableTax; }

    public Integer getDefaultLowStockAlert() { return defaultLowStockAlert; }
    public void setDefaultLowStockAlert(Integer defaultLowStockAlert) { this.defaultLowStockAlert = defaultLowStockAlert; }

    public String getReceiptHeader() { return receiptHeader; }
    public void setReceiptHeader(String receiptHeader) { this.receiptHeader = receiptHeader; }

    public String getReceiptFooter() { return receiptFooter; }
    public void setReceiptFooter(String receiptFooter) { this.receiptFooter = receiptFooter; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
