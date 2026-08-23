package com.smartshop.dto;

import java.math.BigDecimal;

public class SalesTrendDto {
    private String label; // e.g., "Mon", "2026-08-20", "Jan"
    private BigDecimal sales = BigDecimal.ZERO;
    private BigDecimal profit = BigDecimal.ZERO;
    private BigDecimal expenses = BigDecimal.ZERO;
    private long ordersCount = 0;

    public SalesTrendDto() {}

    public SalesTrendDto(String label, BigDecimal sales, BigDecimal profit, BigDecimal expenses, long ordersCount) {
        this.label = label;
        this.sales = sales != null ? sales : BigDecimal.ZERO;
        this.profit = profit != null ? profit : BigDecimal.ZERO;
        this.expenses = expenses != null ? expenses : BigDecimal.ZERO;
        this.ordersCount = ordersCount;
    }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public BigDecimal getSales() { return sales; }
    public void setSales(BigDecimal sales) { this.sales = sales; }

    public BigDecimal getProfit() { return profit; }
    public void setProfit(BigDecimal profit) { this.profit = profit; }

    public BigDecimal getExpenses() { return expenses; }
    public void setExpenses(BigDecimal expenses) { this.expenses = expenses; }

    public long getOrdersCount() { return ordersCount; }
    public void setOrdersCount(long ordersCount) { this.ordersCount = ordersCount; }
}
