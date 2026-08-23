package com.smartshop.dto;

import com.smartshop.models.Product;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ProductResponse {
    private Long id;
    private String name;
    private String sku;
    private String barcode;
    private Long categoryId;
    private String categoryName;
    private String brand;
    private String modelNumber;
    private String unit;
    private BigDecimal costPrice; // Only visible to OWNER/ADMIN
    private BigDecimal sellingPrice;
    private Integer currentStock;
    private Integer minStockAlert;
    private String shelfLocation;
    private String description;
    private boolean active;
    private String stockStatus; // IN_STOCK, LOW_STOCK, OUT_OF_STOCK
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ProductResponse() {}

    public ProductResponse(Product p, boolean includeCostPrice) {
        this.id = p.getId();
        this.name = p.getName();
        this.sku = p.getSku();
        this.barcode = p.getBarcode();
        if (p.getCategory() != null) {
            this.categoryId = p.getCategory().getId();
            this.categoryName = p.getCategory().getName();
        }
        this.brand = p.getBrand();
        this.modelNumber = p.getModelNumber();
        this.unit = p.getUnit();
        this.costPrice = includeCostPrice ? p.getCostPrice() : null;
        this.sellingPrice = p.getSellingPrice();
        this.currentStock = p.getCurrentStock();
        this.minStockAlert = p.getMinStockAlert();
        this.shelfLocation = p.getShelfLocation();
        this.description = p.getDescription();
        this.active = p.isActive();
        this.createdAt = p.getCreatedAt();
        this.updatedAt = p.getUpdatedAt();

        if (p.getCurrentStock() <= 0) {
            this.stockStatus = "OUT_OF_STOCK";
        } else if (p.getCurrentStock() <= p.getMinStockAlert()) {
            this.stockStatus = "LOW_STOCK";
        } else {
            this.stockStatus = "IN_STOCK";
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public String getBarcode() { return barcode; }
    public void setBarcode(String barcode) { this.barcode = barcode; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getModelNumber() { return modelNumber; }
    public void setModelNumber(String modelNumber) { this.modelNumber = modelNumber; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public BigDecimal getCostPrice() { return costPrice; }
    public void setCostPrice(BigDecimal costPrice) { this.costPrice = costPrice; }

    public BigDecimal getSellingPrice() { return sellingPrice; }
    public void setSellingPrice(BigDecimal sellingPrice) { this.sellingPrice = sellingPrice; }

    public Integer getCurrentStock() { return currentStock; }
    public void setCurrentStock(Integer currentStock) { this.currentStock = currentStock; }

    public Integer getMinStockAlert() { return minStockAlert; }
    public void setMinStockAlert(Integer minStockAlert) { this.minStockAlert = minStockAlert; }

    public String getShelfLocation() { return shelfLocation; }
    public void setShelfLocation(String shelfLocation) { this.shelfLocation = shelfLocation; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public String getStockStatus() { return stockStatus; }
    public void setStockStatus(String stockStatus) { this.stockStatus = stockStatus; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
