package com.smartshop.models;

import com.smartshop.enums.LedgerTransactionType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "supplier_ledger_entries", indexes = {
    @Index(name = "idx_ledger_supplier_date", columnList = "supplier_id, transactionDate")
})
public class SupplierLedgerEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime transactionDate = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private LedgerTransactionType transactionType;

    @Column(length = 60)
    private String referenceNumber; // e.g. Purchase Invoice # or Payment Receipt #

    @Column(length = 255)
    private String description;

    @NotNull
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal debit = BigDecimal.ZERO; // Payment made / reduces payable balance

    @NotNull
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal credit = BigDecimal.ZERO; // New purchase / increases payable balance

    @NotNull
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal balanceAfter = BigDecimal.ZERO; // Running balance after transaction

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id")
    private User createdBy;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public SupplierLedgerEntry() {}

    public SupplierLedgerEntry(Supplier supplier, LocalDateTime transactionDate, LedgerTransactionType transactionType,
                               String referenceNumber, String description, BigDecimal debit, BigDecimal credit,
                               BigDecimal balanceAfter, User createdBy) {
        this.supplier = supplier;
        this.transactionDate = transactionDate != null ? transactionDate : LocalDateTime.now();
        this.transactionType = transactionType;
        this.referenceNumber = referenceNumber;
        this.description = description;
        this.debit = debit != null ? debit : BigDecimal.ZERO;
        this.credit = credit != null ? credit : BigDecimal.ZERO;
        this.balanceAfter = balanceAfter != null ? balanceAfter : BigDecimal.ZERO;
        this.createdBy = createdBy;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Supplier getSupplier() { return supplier; }
    public void setSupplier(Supplier supplier) { this.supplier = supplier; }

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

    public User getCreatedBy() { return createdBy; }
    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
