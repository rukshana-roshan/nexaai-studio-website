package com.smartshop.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ProfitLossStatementDto {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private BigDecimal grossSalesRevenue;
    private BigDecimal costOfGoodsSold;
    private BigDecimal grossProfit; // Revenue - COGS
    private BigDecimal grossProfitMargin; // (Gross Profit / Revenue) * 100
    private BigDecimal totalOperatingExpenses;
    private BigDecimal netProfit; // Gross Profit - Expenses
    private BigDecimal netProfitMargin; // (Net Profit / Revenue) * 100
    private long totalSalesCount;
    private List<CategoryExpenseSummary> expenseBreakdown;

    public static class CategoryExpenseSummary {
        private String category;
        private BigDecimal amount;

        public CategoryExpenseSummary() {}
        public CategoryExpenseSummary(String category, BigDecimal amount) {
            this.category = category;
            this.amount = amount;
        }

        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }

        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
    }

    public ProfitLossStatementDto() {}

    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }

    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }

    public BigDecimal getGrossSalesRevenue() { return grossSalesRevenue; }
    public void setGrossSalesRevenue(BigDecimal grossSalesRevenue) { this.grossSalesRevenue = grossSalesRevenue; }

    public BigDecimal getCostOfGoodsSold() { return costOfGoodsSold; }
    public void setCostOfGoodsSold(BigDecimal costOfGoodsSold) { this.costOfGoodsSold = costOfGoodsSold; }

    public BigDecimal getGrossProfit() { return grossProfit; }
    public void setGrossProfit(BigDecimal grossProfit) { this.grossProfit = grossProfit; }

    public BigDecimal getGrossProfitMargin() { return grossProfitMargin; }
    public void setGrossProfitMargin(BigDecimal grossProfitMargin) { this.grossProfitMargin = grossProfitMargin; }

    public BigDecimal getTotalOperatingExpenses() { return totalOperatingExpenses; }
    public void setTotalOperatingExpenses(BigDecimal totalOperatingExpenses) { this.totalOperatingExpenses = totalOperatingExpenses; }

    public BigDecimal getNetProfit() { return netProfit; }
    public void setNetProfit(BigDecimal netProfit) { this.netProfit = netProfit; }

    public BigDecimal getNetProfitMargin() { return netProfitMargin; }
    public void setNetProfitMargin(BigDecimal netProfitMargin) { this.netProfitMargin = netProfitMargin; }

    public long getTotalSalesCount() { return totalSalesCount; }
    public void setTotalSalesCount(long totalSalesCount) { this.totalSalesCount = totalSalesCount; }

    public List<CategoryExpenseSummary> getExpenseBreakdown() { return expenseBreakdown; }
    public void setExpenseBreakdown(List<CategoryExpenseSummary> expenseBreakdown) { this.expenseBreakdown = expenseBreakdown; }
}
