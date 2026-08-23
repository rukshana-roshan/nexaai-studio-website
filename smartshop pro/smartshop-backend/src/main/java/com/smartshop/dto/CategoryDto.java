package com.smartshop.dto;

import com.smartshop.models.Category;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

public class CategoryDto {
    private Long id;

    @NotBlank(message = "Category name is required")
    private String name;

    private String description;
    private String icon;
    private boolean active = true;
    private LocalDateTime createdAt;

    public CategoryDto() {}

    public CategoryDto(Category c) {
        this.id = c.getId();
        this.name = c.getName();
        this.description = c.getDescription();
        this.icon = c.getIcon();
        this.active = c.isActive();
        this.createdAt = c.getCreatedAt();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
