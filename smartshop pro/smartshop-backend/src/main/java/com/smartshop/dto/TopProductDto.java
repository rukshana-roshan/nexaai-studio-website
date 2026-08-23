package com.smartshop.dto;

import java.math.BigDecimal;

public class TopProductDto {
    private Long productId;
    private String productName;
    private String sku;
    private long unitsSold;
    private BigDecimal totalRevenue;
    private BigDecimal totalProfit;

    public TopProductDto() {}

    public TopProductDto(Long productId, String productName, String sku, long unitsSold, BigDecimal totalRevenue, BigDecimal totalProfit) {
        this.productId = productId;
        this.productName = productName;
        this.sku = sku;
        this.unitsSold = unitsSold;
        this.totalRevenue = totalRevenue != null ? totalRevenue : BigDecimal.ZERO;
        this.totalProfit = totalProfit != null ? totalProfit : BigDecimal.ZERO;
    }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public long getUnitsSold() { return unitsSold; }
    public void setUnitsSold(long unitsSold) { this.unitsSold = unitsSold; }

    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }

    public BigDecimal getTotalProfit() { return totalProfit; }
    public void setTotalProfit(BigDecimal totalProfit) { this.totalProfit = totalProfit; }
}
