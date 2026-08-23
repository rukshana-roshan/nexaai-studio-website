package com.smartshop.dto;

import com.smartshop.models.ShopSettings;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

public class ShopSettingsDto {
    private Long id;

    @NotBlank(message = "Shop name is required")
    private String shopName;

    private String tagline;
    private String address;
    private String phone;
    private String email;
    private String website;
    private String taxNumber;
    private String currencySymbol;
    private String currencyCode;
    private BigDecimal defaultTaxRate;
    private boolean enableTax;
    private Integer defaultLowStockAlert;
    private String receiptHeader;
    private String receiptFooter;

    public ShopSettingsDto() {}

    public ShopSettingsDto(ShopSettings s) {
        this.id = s.getId();
        this.shopName = s.getShopName();
        this.tagline = s.getTagline();
        this.address = s.getAddress();
        this.phone = s.getPhone();
        this.email = s.getEmail();
        this.website = s.getWebsite();
        this.taxNumber = s.getTaxNumber();
        this.currencySymbol = s.getCurrencySymbol();
        this.currencyCode = s.getCurrencyCode();
        this.defaultTaxRate = s.getDefaultTaxRate();
        this.enableTax = s.isEnableTax();
        this.defaultLowStockAlert = s.getDefaultLowStockAlert();
        this.receiptHeader = s.getReceiptHeader();
        this.receiptFooter = s.getReceiptFooter();
    }

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
}
