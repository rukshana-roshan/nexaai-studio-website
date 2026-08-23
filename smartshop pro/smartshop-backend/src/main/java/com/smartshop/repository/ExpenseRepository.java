package com.smartshop.repository;

import com.smartshop.models.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByExpenseDateBetweenOrderByExpenseDateDesc(LocalDateTime start, LocalDateTime end);
    List<Expense> findByCategoryIdOrderByExpenseDateDesc(Long categoryId);

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.expenseDate BETWEEN :start AND :end")
    BigDecimal sumExpensesBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT e.category.name, SUM(e.amount) FROM Expense e WHERE e.expenseDate BETWEEN :start AND :end GROUP BY e.category.name")
    List<Object[]> sumExpensesByCategoryBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
