package com.smartshop.repository;

import com.smartshop.models.ExpenseCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExpenseCategoryRepository extends JpaRepository<ExpenseCategory, Long> {
    Optional<ExpenseCategory> findByNameIgnoreCase(String name);
    Boolean existsByNameIgnoreCase(String name);
}
