package com.smartshop.dto;

import com.smartshop.models.ExpenseCategory;
import jakarta.validation.constraints.NotBlank;

public class ExpenseCategoryDto {
    private Long id;

    @NotBlank(message = "Category name is required")
    private String name;

    private String description;

    public ExpenseCategoryDto() {}

    public ExpenseCategoryDto(ExpenseCategory ec) {
        this.id = ec.getId();
        this.name = ec.getName();
        this.description = ec.getDescription();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
