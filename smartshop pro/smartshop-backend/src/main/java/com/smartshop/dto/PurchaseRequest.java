package com.smartshop.dto;

import com.smartshop.enums.PaymentMethod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class PurchaseRequest {
    @NotNull(message = "Supplier is required")
    private Long supplierId;

    private String invoiceNumber;
    private LocalDateTime purchaseDate = LocalDateTime.now();

    @NotNull(message = "Paid amount is required")
    private BigDecimal paidAmount = BigDecimal.ZERO;

    private PaymentMethod paymentMethod = PaymentMethod.CASH;
    private String notes;

    @NotEmpty(message = "At least one item must be in the purchase order")
    @Valid
    private List<PurchaseItemRequest> items;

    public PurchaseRequest() {}

    public Long getSupplierId() { return supplierId; }
    public void setSupplierId(Long supplierId) { this.supplierId = supplierId; }

    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }

    public LocalDateTime getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(LocalDateTime purchaseDate) { this.purchaseDate = purchaseDate; }

    public BigDecimal getPaidAmount() { return paidAmount; }
    public void setPaidAmount(BigDecimal paidAmount) { this.paidAmount = paidAmount; }

    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public List<PurchaseItemRequest> getItems() { return items; }
    public void setItems(List<PurchaseItemRequest> items) { this.items = items; }
}
