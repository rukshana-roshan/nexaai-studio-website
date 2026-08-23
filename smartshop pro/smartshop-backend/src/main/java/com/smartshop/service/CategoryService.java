package com.smartshop.service;

import com.smartshop.dto.CategoryDto;
import com.smartshop.enums.AuditAction;
import com.smartshop.models.Category;
import com.smartshop.repository.CategoryRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final AuditLogService auditLogService;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository, AuditLogService auditLogService) {
        this.categoryRepository = categoryRepository;
        this.auditLogService = auditLogService;
    }

    @Transactional(readOnly = true)
    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findAll().stream().map(CategoryDto::new).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CategoryDto> getActiveCategories() {
        return categoryRepository.findByActiveTrue().stream().map(CategoryDto::new).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Category getCategoryEntity(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
    }

    @Transactional
    public CategoryDto createCategory(CategoryDto dto, HttpServletRequest request) {
        if (categoryRepository.existsByNameIgnoreCase(dto.getName().trim())) {
            throw new RuntimeException("Category '" + dto.getName() + "' already exists");
        }

        Category category = new Category(dto.getName().trim(), dto.getDescription(), dto.getIcon());
        Category saved = categoryRepository.save(category);

        auditLogService.log(AuditAction.CREATE_CATEGORY, "Created category: " + saved.getName(), request);
        return new CategoryDto(saved);
    }

    @Transactional
    public CategoryDto updateCategory(Long id, CategoryDto dto, HttpServletRequest request) {
        Category category = getCategoryEntity(id);

        if (!category.getName().equalsIgnoreCase(dto.getName().trim()) &&
                categoryRepository.existsByNameIgnoreCase(dto.getName().trim())) {
            throw new RuntimeException("Category '" + dto.getName() + "' already exists");
        }

        category.setName(dto.getName().trim());
        category.setDescription(dto.getDescription());
        category.setIcon(dto.getIcon());
        category.setActive(dto.isActive());

        Category updated = categoryRepository.save(category);
        auditLogService.log(AuditAction.UPDATE_CATEGORY, "Updated category: " + updated.getName(), request);

        return new CategoryDto(updated);
    }

    @Transactional
    public void deleteCategory(Long id, HttpServletRequest request) {
        Category category = getCategoryEntity(id);
        category.setActive(false);
        categoryRepository.save(category);
        auditLogService.log(AuditAction.UPDATE_CATEGORY, "Deactivated category: " + category.getName(), request);
    }
}
