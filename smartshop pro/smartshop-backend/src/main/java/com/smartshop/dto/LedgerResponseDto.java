package com.smartshop.dto;

import com.smartshop.enums.LedgerTransactionType;
import com.smartshop.models.SupplierLedgerEntry;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class LedgerResponseDto {
    private Long id;
    private Long supplierId;
    private String supplierName;
    private LocalDateTime transactionDate;
    private LedgerTransactionType transactionType;
    private String referenceNumber;
    private String description;
    private BigDecimal debit; // Payment reducing debt
    private BigDecimal credit; // Purchase increasing debt
    private BigDecimal balanceAfter;
    private String createdByUsername;
    private LocalDateTime createdAt;

    public LedgerResponseDto() {}

    public LedgerResponseDto(SupplierLedgerEntry l) {
        this.id = l.getId();
        if (l.getSupplier() != null) {
            this.supplierId = l.getSupplier().getId();
            this.supplierName = l.getSupplier().getName();
        }
        this.transactionDate = l.getTransactionDate();
        this.transactionType = l.getTransactionType();
        this.referenceNumber = l.getReferenceNumber();
        this.description = l.getDescription();
        this.debit = l.getDebit();
        this.credit = l.getCredit();
        this.balanceAfter = l.getBalanceAfter();
        if (l.getCreatedBy() != null) {
            this.createdByUsername = l.getCreatedBy().getUsername();
        }
        this.createdAt = l.getCreatedAt();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSupplierId() { return supplierId; }
    public void setSupplierId(Long supplierId) { this.supplierId = supplierId; }

    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }

    public LocalDateTime getTransactionDate() { return transactionDate; }
    public void setTransactionDate(LocalDateTime transactionDate) { this.transactionDate = transactionDate; }

    public LedgerTransactionType getTransactionType() { return transactionType; }
    public void setTransactionType(LedgerTransactionType transactionType) { this.transactionType = transactionType; }

    public String getReferenceNumber() { return referenceNumber; }
    public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getDebit() { return debit; }
    public void setDebit(BigDecimal debit) { this.debit = debit; }

    public BigDecimal getCredit() { return credit; }
    public void setCredit(BigDecimal credit) { this.credit = credit; }

    public BigDecimal getBalanceAfter() { return balanceAfter; }
    public void setBalanceAfter(BigDecimal balanceAfter) { this.balanceAfter = balanceAfter; }

    public String getCreatedByUsername() { return createdByUsername; }
    public void setCreatedByUsername(String createdByUsername) { this.createdByUsername = createdByUsername; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
