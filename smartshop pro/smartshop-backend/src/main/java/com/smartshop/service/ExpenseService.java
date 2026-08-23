package com.smartshop.service;

import com.smartshop.dto.ExpenseCategoryDto;
import com.smartshop.dto.ExpenseRequest;
import com.smartshop.dto.ExpenseResponseDto;
import com.smartshop.enums.AuditAction;
import com.smartshop.models.Expense;
import com.smartshop.models.ExpenseCategory;
import com.smartshop.models.User;
import com.smartshop.repository.ExpenseCategoryRepository;
import com.smartshop.repository.ExpenseRepository;
import com.smartshop.repository.UserRepository;
import com.smartshop.security.UserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final ExpenseCategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;

    @Autowired
    public ExpenseService(ExpenseRepository expenseRepository,
                          ExpenseCategoryRepository categoryRepository,
                          UserRepository userRepository,
                          AuditLogService auditLogService) {
        this.expenseRepository = expenseRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.auditLogService = auditLogService;
    }

    @Transactional(readOnly = true)
    public List<ExpenseResponseDto> getAllExpenses() {
        return expenseRepository.findAll().stream()
                .sorted((a, b) -> b.getExpenseDate().compareTo(a.getExpenseDate()))
                .map(ExpenseResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ExpenseCategoryDto> getAllCategories() {
        return categoryRepository.findAll().stream().map(ExpenseCategoryDto::new).collect(Collectors.toList());
    }

    @Transactional
    public ExpenseCategoryDto createCategory(ExpenseCategoryDto dto) {
        if (categoryRepository.existsByNameIgnoreCase(dto.getName().trim())) {
            throw new RuntimeException("Expense category '" + dto.getName() + "' already exists");
        }
        ExpenseCategory cat = new ExpenseCategory(dto.getName().trim(), dto.getDescription());
        return new ExpenseCategoryDto(categoryRepository.save(cat));
    }

    @Transactional
    public ExpenseResponseDto createExpense(ExpenseRequest req, UserPrincipal principal, HttpServletRequest request) {
        ExpenseCategory category = categoryRepository.findById(req.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Expense category not found with id: " + req.getCategoryId()));

        User user = principal != null ? userRepository.findById(principal.getId()).orElse(null) : null;

        Expense expense = new Expense();
        expense.setTitle(req.getTitle().trim());
        expense.setCategory(category);
        expense.setAmount(req.getAmount());
        expense.setExpenseDate(req.getExpenseDate() != null ? req.getExpenseDate() : LocalDateTime.now());
        expense.setPaymentMethod(req.getPaymentMethod());
        expense.setReferenceNumber(req.getReferenceNumber());
        expense.setNotes(req.getNotes());
        expense.setCreatedBy(user);

        Expense saved = expenseRepository.save(expense);

        auditLogService.log(
                AuditAction.CREATE_EXPENSE,
                "Logged expense: " + saved.getTitle() + " | Category: " + category.getName() + " | Amount: " + saved.getAmount(),
                request
        );

        return new ExpenseResponseDto(saved);
    }

    @Transactional
    public void deleteExpense(Long id, HttpServletRequest request) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expense not found with id: " + id));

        expenseRepository.delete(expense);
        auditLogService.log(
                AuditAction.DELETE_EXPENSE,
                "Deleted expense: " + expense.getTitle() + " | Amount: " + expense.getAmount(),
                request
        );
    }
}
