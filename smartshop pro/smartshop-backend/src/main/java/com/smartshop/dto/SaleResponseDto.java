package com.smartshop.dto;

import com.smartshop.enums.PaymentMethod;
import com.smartshop.enums.SaleStatus;
import com.smartshop.models.Sale;
import com.smartshop.models.SaleItem;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SaleResponseDto {
    private Long id;
    private String invoiceNumber;
    private Long customerId;
    private String customerName;
    private String customerPhone;
    private Long cashierId;
    private String cashierUsername;
    private String cashierFullName;
    private LocalDateTime saleDate;
    private BigDecimal subtotal;
    private BigDecimal discountPercentage;
    private BigDecimal discountAmount;
    private BigDecimal taxRate;
    private BigDecimal taxAmount;
    private BigDecimal grandTotal;
    private BigDecimal paidAmount;
    private BigDecimal changeAmount;
    private PaymentMethod paymentMethod;
    private SaleStatus status;
    private BigDecimal totalCost; // Null for Cashier
    private BigDecimal totalProfit; // Null for Cashier
    private String notes;
    private List<SaleItemDetail> items = new ArrayList<>();
    private LocalDateTime createdAt;

    public static class SaleItemDetail {
        private Long id;
        private Long productId;
        private String productName;
        private String sku;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal discount;
        private BigDecimal subtotal;
        private BigDecimal costPrice; // Null for Cashier
        private BigDecimal itemProfit; // Null for Cashier

        public SaleItemDetail(SaleItem item, boolean includeFinancials) {
            this.id = item.getId();
            if (item.getProduct() != null) {
                this.productId = item.getProduct().getId();
            }
            this.productName = item.getProductName();
            this.sku = item.getSku();
            this.quantity = item.getQuantity();
            this.unitPrice = item.getUnitPrice();
            this.discount = item.getDiscount();
            this.subtotal = item.getSubtotal();
            this.costPrice = includeFinancials ? item.getCostPrice() : null;
            this.itemProfit = includeFinancials ? item.getItemProfit() : null;
        }

        public Long getId() { return id; }
        public Long getProductId() { return productId; }
        public String getProductName() { return productName; }
        public String getSku() { return sku; }
        public Integer getQuantity() { return quantity; }
        public BigDecimal getUnitPrice() { return unitPrice; }
        public BigDecimal getDiscount() { return discount; }
        public BigDecimal getSubtotal() { return subtotal; }
        public BigDecimal getCostPrice() { return costPrice; }
        public BigDecimal getItemProfit() { return itemProfit; }
    }

    public SaleResponseDto() {}

    public SaleResponseDto(Sale s, boolean includeFinancials) {
        this.id = s.getId();
        this.invoiceNumber = s.getInvoiceNumber();
        if (s.getCustomer() != null) {
            this.customerId = s.getCustomer().getId();
        }
        this.customerName = s.getCustomerName();
        this.customerPhone = s.getCustomerPhone();
        if (s.getCashier() != null) {
            this.cashierId = s.getCashier().getId();
            this.cashierUsername = s.getCashier().getUsername();
            this.cashierFullName = s.getCashier().getFullName();
        }
        this.saleDate = s.getSaleDate();
        this.subtotal = s.getSubtotal();
        this.discountPercentage = s.getDiscountPercentage();
        this.discountAmount = s.getDiscountAmount();
        this.taxRate = s.getTaxRate();
        this.taxAmount = s.getTaxAmount();
        this.grandTotal = s.getGrandTotal();
        this.paidAmount = s.getPaidAmount();
        this.changeAmount = s.getChangeAmount();
        this.paymentMethod = s.getPaymentMethod();
        this.status = s.getStatus();
        this.totalCost = includeFinancials ? s.getTotalCost() : null;
        this.totalProfit = includeFinancials ? s.getTotalProfit() : null;
        this.notes = s.getNotes();
        this.createdAt = s.getCreatedAt();
        if (s.getItems() != null) {
            for (SaleItem item : s.getItems()) {
                this.items.add(new SaleItemDetail(item, includeFinancials));
            }
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

    public Long getCashierId() { return cashierId; }
    public void setCashierId(Long cashierId) { this.cashierId = cashierId; }

    public String getCashierUsername() { return cashierUsername; }
    public void setCashierUsername(String cashierUsername) { this.cashierUsername = cashierUsername; }

    public String getCashierFullName() { return cashierFullName; }
    public void setCashierFullName(String cashierFullName) { this.cashierFullName = cashierFullName; }

    public LocalDateTime getSaleDate() { return saleDate; }
    public void setSaleDate(LocalDateTime saleDate) { this.saleDate = saleDate; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public BigDecimal getDiscountPercentage() { return discountPercentage; }
    public void setDiscountPercentage(BigDecimal discountPercentage) { this.discountPercentage = discountPercentage; }

    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }

    public BigDecimal getTaxRate() { return taxRate; }
    public void setTaxRate(BigDecimal taxRate) { this.taxRate = taxRate; }

    public BigDecimal getTaxAmount() { return taxAmount; }
    public void setTaxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; }

    public BigDecimal getGrandTotal() { return grandTotal; }
    public void setGrandTotal(BigDecimal grandTotal) { this.grandTotal = grandTotal; }

    public BigDecimal getPaidAmount() { return paidAmount; }
    public void setPaidAmount(BigDecimal paidAmount) { this.paidAmount = paidAmount; }

    public BigDecimal getChangeAmount() { return changeAmount; }
    public void setChangeAmount(BigDecimal changeAmount) { this.changeAmount = changeAmount; }

    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }

    public SaleStatus getStatus() { return status; }
    public void setStatus(SaleStatus status) { this.status = status; }

    public BigDecimal getTotalCost() { return totalCost; }
    public void setTotalCost(BigDecimal totalCost) { this.totalCost = totalCost; }

    public BigDecimal getTotalProfit() { return totalProfit; }
    public void setTotalProfit(BigDecimal totalProfit) { this.totalProfit = totalProfit; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public List<SaleItemDetail> getItems() { return items; }
    public void setItems(List<SaleItemDetail> items) { this.items = items; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
