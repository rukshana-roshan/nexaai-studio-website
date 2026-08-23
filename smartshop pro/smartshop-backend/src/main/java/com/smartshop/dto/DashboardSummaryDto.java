package com.smartshop.dto;

import java.math.BigDecimal;
import java.util.List;

public class DashboardSummaryDto {
    // Today's metrics
    private BigDecimal todaySales;
    private BigDecimal todayProfit;
    private long todayOrdersCount;

    // Monthly metrics
    private BigDecimal thisMonthSales;
    private BigDecimal thisMonthProfit;
    private BigDecimal thisMonthExpenses;
    private BigDecimal thisMonthNetProfit; // (Profit - Expenses)

    // Inventory & Financial Health
    private BigDecimal totalInventoryValueAtCost;
    private BigDecimal totalInventoryValueAtRetail;
    private BigDecimal potentialProfitInStock;
    private BigDecimal totalSupplierPayable;
    private long totalProductsCount;
    private long lowStockCount;
    private long outOfStockCount;
    private long totalCustomersCount;
    private long totalSuppliersCount;

    // Trend & Distribution Lists
    private List<SalesTrendDto> salesTrend;
    private List<TopProductDto> topProducts;
    private List<CategorySalesDto> categorySales;

    public DashboardSummaryDto() {}

    public BigDecimal getTodaySales() { return todaySales; }
    public void setTodaySales(BigDecimal todaySales) { this.todaySales = todaySales; }

    public BigDecimal getTodayProfit() { return todayProfit; }
    public void setTodayProfit(BigDecimal todayProfit) { this.todayProfit = todayProfit; }

    public long getTodayOrdersCount() { return todayOrdersCount; }
    public void setTodayOrdersCount(long todayOrdersCount) { this.todayOrdersCount = todayOrdersCount; }

    public BigDecimal getThisMonthSales() { return thisMonthSales; }
    public void setThisMonthSales(BigDecimal thisMonthSales) { this.thisMonthSales = thisMonthSales; }

    public BigDecimal getThisMonthProfit() { return thisMonthProfit; }
    public void setThisMonthProfit(BigDecimal thisMonthProfit) { this.thisMonthProfit = thisMonthProfit; }

    public BigDecimal getThisMonthExpenses() { return thisMonthExpenses; }
    public void setThisMonthExpenses(BigDecimal thisMonthExpenses) { this.thisMonthExpenses = thisMonthExpenses; }

    public BigDecimal getThisMonthNetProfit() { return thisMonthNetProfit; }
    public void setThisMonthNetProfit(BigDecimal thisMonthNetProfit) { this.thisMonthNetProfit = thisMonthNetProfit; }

    public BigDecimal getTotalInventoryValueAtCost() { return totalInventoryValueAtCost; }
    public void setTotalInventoryValueAtCost(BigDecimal totalInventoryValueAtCost) { this.totalInventoryValueAtCost = totalInventoryValueAtCost; }

    public BigDecimal getTotalInventoryValueAtRetail() { return totalInventoryValueAtRetail; }
    public void setTotalInventoryValueAtRetail(BigDecimal totalInventoryValueAtRetail) { this.totalInventoryValueAtRetail = totalInventoryValueAtRetail; }

    public BigDecimal getPotentialProfitInStock() { return potentialProfitInStock; }
    public void setPotentialProfitInStock(BigDecimal potentialProfitInStock) { this.potentialProfitInStock = potentialProfitInStock; }

    public BigDecimal getTotalSupplierPayable() { return totalSupplierPayable; }
    public void setTotalSupplierPayable(BigDecimal totalSupplierPayable) { this.totalSupplierPayable = totalSupplierPayable; }

    public long getTotalProductsCount() { return totalProductsCount; }
    public void setTotalProductsCount(long totalProductsCount) { this.totalProductsCount = totalProductsCount; }

    public long getLowStockCount() { return lowStockCount; }
    public void setLowStockCount(long lowStockCount) { this.lowStockCount = lowStockCount; }

    public long getOutOfStockCount() { return outOfStockCount; }
    public void setOutOfStockCount(long outOfStockCount) { this.outOfStockCount = outOfStockCount; }

    public long getTotalCustomersCount() { return totalCustomersCount; }
    public void setTotalCustomersCount(long totalCustomersCount) { this.totalCustomersCount = totalCustomersCount; }

    public long getTotalSuppliersCount() { return totalSuppliersCount; }
    public void setTotalSuppliersCount(long totalSuppliersCount) { this.totalSuppliersCount = totalSuppliersCount; }

    public List<SalesTrendDto> getSalesTrend() { return salesTrend; }
    public void setSalesTrend(List<SalesTrendDto> salesTrend) { this.salesTrend = salesTrend; }

    public List<TopProductDto> getTopProducts() { return topProducts; }
    public void setTopProducts(List<TopProductDto> topProducts) { this.topProducts = topProducts; }

    public List<CategorySalesDto> getCategorySales() { return categorySales; }
    public void setCategorySales(List<CategorySalesDto> categorySales) { this.categorySales = categorySales; }
}
