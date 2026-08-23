package com.smartshop.dto;

import com.smartshop.enums.PaymentMethod;
import com.smartshop.models.Expense;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ExpenseResponseDto {
    private Long id;
    private String title;
    private Long categoryId;
    private String categoryName;
    private BigDecimal amount;
    private LocalDateTime expenseDate;
    private PaymentMethod paymentMethod;
    private String referenceNumber;
    private String notes;
    private String createdByUsername;
    private LocalDateTime createdAt;

    public ExpenseResponseDto() {}

    public ExpenseResponseDto(Expense e) {
        this.id = e.getId();
        this.title = e.getTitle();
        if (e.getCategory() != null) {
            this.categoryId = e.getCategory().getId();
            this.categoryName = e.getCategory().getName();
        }
        this.amount = e.getAmount();
        this.expenseDate = e.getExpenseDate();
        this.paymentMethod = e.getPaymentMethod();
        this.referenceNumber = e.getReferenceNumber();
        this.notes = e.getNotes();
        if (e.getCreatedBy() != null) {
            this.createdByUsername = e.getCreatedBy().getUsername();
        }
        this.createdAt = e.getCreatedAt();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public LocalDateTime getExpenseDate() { return expenseDate; }
    public void setExpenseDate(LocalDateTime expenseDate) { this.expenseDate = expenseDate; }

    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getReferenceNumber() { return referenceNumber; }
    public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getCreatedByUsername() { return createdByUsername; }
    public void setCreatedByUsername(String createdByUsername) { this.createdByUsername = createdByUsername; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
