package com.smartshop.dto;

import com.smartshop.enums.PaymentMethod;
import com.smartshop.enums.PaymentStatus;
import com.smartshop.models.PurchaseItem;
import com.smartshop.models.SupplierPurchase;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PurchaseResponseDto {
    private Long id;
    private String invoiceNumber;
    private Long supplierId;
    private String supplierName;
    private LocalDateTime purchaseDate;
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private BigDecimal dueAmount;
    private PaymentStatus paymentStatus;
    private PaymentMethod paymentMethod;
    private String notes;
    private String createdByUsername;
    private List<PurchaseItemDetail> items = new ArrayList<>();
    private LocalDateTime createdAt;

    public static class PurchaseItemDetail {
        private Long id;
        private Long productId;
        private String productName;
        private String sku;
        private Integer quantity;
        private BigDecimal unitCost;
        private BigDecimal totalCost;

        public PurchaseItemDetail(PurchaseItem item) {
            this.id = item.getId();
            if (item.getProduct() != null) {
                this.productId = item.getProduct().getId();
                this.productName = item.getProduct().getName();
                this.sku = item.getProduct().getSku();
            }
            this.quantity = item.getQuantity();
            this.unitCost = item.getUnitCost();
            this.totalCost = item.getTotalCost();
        }

        public Long getId() { return id; }
        public Long getProductId() { return productId; }
        public String getProductName() { return productName; }
        public String getSku() { return sku; }
        public Integer getQuantity() { return quantity; }
        public BigDecimal getUnitCost() { return unitCost; }
        public BigDecimal getTotalCost() { return totalCost; }
    }

    public PurchaseResponseDto() {}

    public PurchaseResponseDto(SupplierPurchase p) {
        this.id = p.getId();
        this.invoiceNumber = p.getInvoiceNumber();
        if (p.getSupplier() != null) {
            this.supplierId = p.getSupplier().getId();
            this.supplierName = p.getSupplier().getName();
        }
        this.purchaseDate = p.getPurchaseDate();
        this.totalAmount = p.getTotalAmount();
        this.paidAmount = p.getPaidAmount();
        this.dueAmount = p.getDueAmount();
        this.paymentStatus = p.getPaymentStatus();
        this.paymentMethod = p.getPaymentMethod();
        this.notes = p.getNotes();
        if (p.getCreatedBy() != null) {
            this.createdByUsername = p.getCreatedBy().getUsername();
        }
        this.createdAt = p.getCreatedAt();
        if (p.getItems() != null) {
            for (PurchaseItem item : p.getItems()) {
                this.items.add(new PurchaseItemDetail(item));
            }
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }

    public Long getSupplierId() { return supplierId; }
    public void setSupplierId(Long supplierId) { this.supplierId = supplierId; }

    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }

    public LocalDateTime getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(LocalDateTime purchaseDate) { this.purchaseDate = purchaseDate; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public BigDecimal getPaidAmount() { return paidAmount; }
    public void setPaidAmount(BigDecimal paidAmount) { this.paidAmount = paidAmount; }

    public BigDecimal getDueAmount() { return dueAmount; }
    public void setDueAmount(BigDecimal dueAmount) { this.dueAmount = dueAmount; }

    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; }

    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getCreatedByUsername() { return createdByUsername; }
    public void setCreatedByUsername(String createdByUsername) { this.createdByUsername = createdByUsername; }

    public List<PurchaseItemDetail> getItems() { return items; }
    public void setItems(List<PurchaseItemDetail> items) { this.items = items; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
