package com.smartshop.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class ProductRequest {
    @NotBlank(message = "Product name is required")
    private String name;

    private String sku;
    private String barcode;
    private Long categoryId;
    private String brand;
    private String modelNumber;
    private String unit = "pcs";

    @NotNull(message = "Cost price is required")
    @DecimalMin(value = "0.00", message = "Cost price cannot be negative")
    private BigDecimal costPrice;

    @NotNull(message = "Selling price is required")
    @DecimalMin(value = "0.00", message = "Selling price cannot be negative")
    private BigDecimal sellingPrice;

    @NotNull(message = "Current stock is required")
    private Integer currentStock = 0;

    @NotNull(message = "Min stock alert threshold is required")
    @Min(value = 0, message = "Min stock alert cannot be negative")
    private Integer minStockAlert = 5;

    private String shelfLocation;
    private String description;

    public ProductRequest() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public String getBarcode() { return barcode; }
    public void setBarcode(String barcode) { this.barcode = barcode; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

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
}
