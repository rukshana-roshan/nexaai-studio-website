package com.smartshop.service;

import com.smartshop.dto.*;
import com.smartshop.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class AnalyticsService {

    private final SaleRepository saleRepository;
    private final SaleItemRepository saleItemRepository;
    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;
    private final CustomerRepository customerRepository;
    private final ExpenseRepository expenseRepository;

    @Autowired
    public AnalyticsService(SaleRepository saleRepository,
                            SaleItemRepository saleItemRepository,
                            ProductRepository productRepository,
                            SupplierRepository supplierRepository,
                            CustomerRepository customerRepository,
                            ExpenseRepository expenseRepository) {
        this.saleRepository = saleRepository;
        this.saleItemRepository = saleItemRepository;
        this.productRepository = productRepository;
        this.supplierRepository = supplierRepository;
        this.customerRepository = customerRepository;
        this.expenseRepository = expenseRepository;
    }

    @Transactional(readOnly = true)
    public DashboardSummaryDto getDashboardSummary() {
        DashboardSummaryDto summary = new DashboardSummaryDto();

        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();
        LocalDateTime endOfToday = LocalDate.now().plusDays(1).atStartOfDay();

        LocalDateTime startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime endOfMonth = LocalDate.now().plusMonths(1).withDayOfMonth(1).atStartOfDay();

        // 1. Today metrics
        summary.setTodaySales(saleRepository.sumTotalSalesBetween(startOfToday, endOfToday));
        summary.setTodayProfit(saleRepository.sumTotalProfitBetween(startOfToday, endOfToday));
        summary.setTodayOrdersCount(saleRepository.countSalesBetween(startOfToday, endOfToday));

        // 2. Month metrics
        BigDecimal monthSales = saleRepository.sumTotalSalesBetween(startOfMonth, endOfMonth);
        BigDecimal monthProfit = saleRepository.sumTotalProfitBetween(startOfMonth, endOfMonth);
        BigDecimal monthExpenses = expenseRepository.sumExpensesBetween(startOfMonth, endOfMonth);
        BigDecimal monthNetProfit = monthProfit.subtract(monthExpenses);

        summary.setThisMonthSales(monthSales);
        summary.setThisMonthProfit(monthProfit);
        summary.setThisMonthExpenses(monthExpenses);
        summary.setThisMonthNetProfit(monthNetProfit);

        // 3. Inventory & Supplier Health
        BigDecimal stockCost = productRepository.calculateTotalStockValueAtCost();
        BigDecimal stockRetail = productRepository.calculateTotalStockValueAtRetail();
        BigDecimal potentialProfit = stockRetail.subtract(stockCost);

        summary.setTotalInventoryValueAtCost(stockCost);
        summary.setTotalInventoryValueAtRetail(stockRetail);
        summary.setPotentialProfitInStock(potentialProfit);
        summary.setTotalSupplierPayable(supplierRepository.calculateTotalPayableBalance());

        // 4. Counts
        summary.setTotalProductsCount(productRepository.count());
        summary.setLowStockCount(productRepository.countLowStockProducts());
        summary.setOutOfStockCount(productRepository.countOutOfStockProducts());
        summary.setTotalCustomersCount(customerRepository.count());
        summary.setTotalSuppliersCount(supplierRepository.count());

        // 5. Visual Trends & Breakdown
        summary.setSalesTrend(getSalesTrend(7));
        summary.setTopProducts(getTopProducts(5));
        summary.setCategorySales(getCategoryDistribution());

        return summary;
    }

    @Transactional(readOnly = true)
    public List<SalesTrendDto> getSalesTrend(int days) {
        List<SalesTrendDto> trend = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = date.plusDays(1).atStartOfDay();

            BigDecimal sales = saleRepository.sumTotalSalesBetween(start, end);
            BigDecimal profit = saleRepository.sumTotalProfitBetween(start, end);
            BigDecimal expenses = expenseRepository.sumExpensesBetween(start, end);
            long count = saleRepository.countSalesBetween(start, end);

            String label = date.format(DateTimeFormatter.ofPattern("MMM dd"));
            trend.add(new SalesTrendDto(label, sales, profit, expenses, count));
        }

        return trend;
    }

    @Transactional(readOnly = true)
    public List<TopProductDto> getTopProducts(int limit) {
        LocalDateTime start = LocalDate.now().minusDays(30).atStartOfDay();
        LocalDateTime end = LocalDate.now().plusDays(1).atStartOfDay();

        List<Object[]> rows = saleItemRepository.findTopSellingProductsBetween(start, end);
        List<TopProductDto> result = new ArrayList<>();

        for (int i = 0; i < Math.min(rows.size(), limit); i++) {
            Object[] row = rows.get(i);
            Long prodId = ((Number) row[0]).longValue();
            String name = (String) row[1];
            String sku = (String) row[2];
            long qty = ((Number) row[3]).longValue();
            BigDecimal rev = (BigDecimal) row[4];
            BigDecimal prof = (BigDecimal) row[5];

            result.add(new TopProductDto(prodId, name, sku, qty, rev, prof));
        }

        return result;
    }

    @Transactional(readOnly = true)
    public List<CategorySalesDto> getCategoryDistribution() {
        LocalDateTime start = LocalDate.now().minusDays(30).atStartOfDay();
        LocalDateTime end = LocalDate.now().plusDays(1).atStartOfDay();

        List<Object[]> rows = saleItemRepository.findCategorySalesDistributionBetween(start, end);
        List<CategorySalesDto> result = new ArrayList<>();

        for (Object[] row : rows) {
            String catName = (String) row[0];
            long qty = ((Number) row[1]).longValue();
            BigDecimal total = (BigDecimal) row[2];

            result.add(new CategorySalesDto(catName, qty, total));
        }

        return result;
    }

    @Transactional(readOnly = true)
    public ProfitLossStatementDto getProfitLossStatement(LocalDateTime start, LocalDateTime end) {
        ProfitLossStatementDto dto = new ProfitLossStatementDto();
        dto.setStartDate(start);
        dto.setEndDate(end);

        BigDecimal revenue = saleRepository.sumTotalSalesBetween(start, end);
        BigDecimal cogs = saleRepository.sumTotalCostBetween(start, end);
        BigDecimal grossProfit = revenue.subtract(cogs);
        BigDecimal totalExpenses = expenseRepository.sumExpensesBetween(start, end);
        BigDecimal netProfit = grossProfit.subtract(totalExpenses);

        BigDecimal grossMargin = BigDecimal.ZERO;
        BigDecimal netMargin = BigDecimal.ZERO;

        if (revenue.compareTo(BigDecimal.ZERO) > 0) {
            grossMargin = grossProfit.multiply(BigDecimal.valueOf(100)).divide(revenue, 2, RoundingMode.HALF_UP);
            netMargin = netProfit.multiply(BigDecimal.valueOf(100)).divide(revenue, 2, RoundingMode.HALF_UP);
        }

        dto.setGrossSalesRevenue(revenue);
        dto.setCostOfGoodsSold(cogs);
        dto.setGrossProfit(grossProfit);
        dto.setGrossProfitMargin(grossMargin);
        dto.setTotalOperatingExpenses(totalExpenses);
        dto.setNetProfit(netProfit);
        dto.setNetProfitMargin(netMargin);
        dto.setTotalSalesCount(saleRepository.countSalesBetween(start, end));

        // Expense category breakdown
        List<Object[]> expRows = expenseRepository.sumExpensesByCategoryBetween(start, end);
        List<ProfitLossStatementDto.CategoryExpenseSummary> breakdown = new ArrayList<>();
        for (Object[] row : expRows) {
            breakdown.add(new ProfitLossStatementDto.CategoryExpenseSummary((String) row[0], (BigDecimal) row[1]));
        }
        dto.setExpenseBreakdown(breakdown);

        return dto;
    }
}
