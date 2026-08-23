package com.smartshop.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products", indexes = {
    @Index(name = "idx_product_barcode", columnList = "barcode"),
    @Index(name = "idx_product_sku", columnList = "sku"),
    @Index(name = "idx_product_name", columnList = "name")
})
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 50, unique = true)
    private String sku;

    @Column(length = 50, unique = true)
    private String barcode;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(length = 100)
    private String brand;

    @Column(length = 100)
    private String modelNumber;

    @Column(length = 30)
    private String unit = "pcs"; // pcs, set, box, kg, meter

    @NotNull
    @DecimalMin("0.00")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal costPrice = BigDecimal.ZERO;

    @NotNull
    @DecimalMin("0.00")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal sellingPrice = BigDecimal.ZERO;

    @NotNull
    @Column(nullable = false)
    private Integer currentStock = 0;

    @NotNull
    @Min(0)
    @Column(nullable = false)
    private Integer minStockAlert = 5;

    @Column(length = 100)
    private String shelfLocation;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;

    public Product() {}

    public Product(String name, String sku, String barcode, Category category, String brand, 
                   String unit, BigDecimal costPrice, BigDecimal sellingPrice, Integer currentStock, 
                   Integer minStockAlert, String shelfLocation, String description) {
        this.name = name;
        this.sku = sku;
        this.barcode = barcode;
        this.category = category;
        this.brand = brand;
        this.unit = unit != null ? unit : "pcs";
        this.costPrice = costPrice;
        this.sellingPrice = sellingPrice;
        this.currentStock = currentStock != null ? currentStock : 0;
        this.minStockAlert = minStockAlert != null ? minStockAlert : 5;
        this.shelfLocation = shelfLocation;
        this.description = description;
        this.active = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public String getBarcode() { return barcode; }
    public void setBarcode(String barcode) { this.barcode = barcode; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

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

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
