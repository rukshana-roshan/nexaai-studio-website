package com.smartshop.dto;

import java.math.BigDecimal;

public class CategorySalesDto {
    private String categoryName;
    private long unitsSold;
    private BigDecimal totalSales;

    public CategorySalesDto() {}

    public CategorySalesDto(String categoryName, long unitsSold, BigDecimal totalSales) {
        this.categoryName = categoryName;
        this.unitsSold = unitsSold;
        this.totalSales = totalSales != null ? totalSales : BigDecimal.ZERO;
    }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public long getUnitsSold() { return unitsSold; }
    public void setUnitsSold(long unitsSold) { this.unitsSold = unitsSold; }

    public BigDecimal getTotalSales() { return totalSales; }
    public void setTotalSales(BigDecimal totalSales) { this.totalSales = totalSales; }
}
