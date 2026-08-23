package com.smartshop.service;

import com.smartshop.dto.ProductRequest;
import com.smartshop.dto.ProductResponse;
import com.smartshop.enums.AuditAction;
import com.smartshop.models.Category;
import com.smartshop.models.Product;
import com.smartshop.repository.CategoryRepository;
import com.smartshop.repository.ProductRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final AuditLogService auditLogService;

    @Autowired
    public ProductService(ProductRepository productRepository,
                          CategoryRepository categoryRepository,
                          AuditLogService auditLogService) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.auditLogService = auditLogService;
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts(boolean isOwner) {
        return productRepository.findAll().stream()
                .map(p -> new ProductResponse(p, isOwner))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getActiveProducts(boolean isOwner) {
        return productRepository.findByActiveTrue().stream()
                .map(p -> new ProductResponse(p, isOwner))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getArchivedProducts(boolean isOwner) {
        return productRepository.findByActiveFalse().stream()
                .map(p -> new ProductResponse(p, isOwner))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getLowStockProducts(boolean isOwner) {
        return productRepository.findLowStockProducts().stream()
                .map(p -> new ProductResponse(p, isOwner))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> searchProducts(String query, boolean isOwner) {
        if (query == null || query.trim().isEmpty()) {
            return getActiveProducts(isOwner);
        }
        return productRepository.searchProducts(query.trim()).stream()
                .map(p -> new ProductResponse(p, isOwner))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductResponse findByBarcode(String barcode, boolean isOwner) {
        Product product = productRepository.findByBarcodeAndActiveTrue(barcode.trim())
                .orElseThrow(() -> new RuntimeException("Product not found with barcode: " + barcode));
        return new ProductResponse(product, isOwner);
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id, boolean isOwner) {
        Product product = getProductEntity(id);
        return new ProductResponse(product, isOwner);
    }

    @Transactional(readOnly = true)
    public Product getProductEntity(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }

    @Transactional
    public ProductResponse createProduct(ProductRequest req, HttpServletRequest request) {
        if (req.getBarcode() != null && !req.getBarcode().trim().isEmpty() &&
                productRepository.findByBarcode(req.getBarcode().trim()).isPresent()) {
            throw new RuntimeException("Product with barcode '" + req.getBarcode() + "' already exists");
        }
        if (req.getSku() != null && !req.getSku().trim().isEmpty() &&
                productRepository.findBySku(req.getSku().trim()).isPresent()) {
            throw new RuntimeException("Product with SKU '" + req.getSku() + "' already exists");
        }

        Category category = null;
        if (req.getCategoryId() != null) {
            category = categoryRepository.findById(req.getCategoryId()).orElse(null);
        }

        // Generate SKU / Barcode if blank
        String sku = (req.getSku() != null && !req.getSku().trim().isEmpty()) 
                ? req.getSku().trim() 
                : "SKU-" + System.currentTimeMillis() % 1000000;
        String barcode = (req.getBarcode() != null && !req.getBarcode().trim().isEmpty()) 
                ? req.getBarcode().trim() 
                : "BAR" + (System.currentTimeMillis() % 100000000);

        Product product = new Product(
                req.getName().trim(),
                sku,
                barcode,
                category,
                req.getBrand(),
                req.getUnit(),
                req.getCostPrice(),
                req.getSellingPrice(),
                req.getCurrentStock(),
                req.getMinStockAlert(),
                req.getShelfLocation(),
                req.getDescription()
        );
        product.setModelNumber(req.getModelNumber());

        Product saved = productRepository.save(product);

        auditLogService.log(
                AuditAction.CREATE_PRODUCT,
                "Created product: " + saved.getName() + " (SKU: " + saved.getSku() + ", Price: " + saved.getSellingPrice() + ")",
                request
        );

        return new ProductResponse(saved, true);
    }

    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest req, HttpServletRequest request) {
        Product product = getProductEntity(id);

        if (req.getBarcode() != null && !req.getBarcode().trim().isEmpty()) {
            productRepository.findByBarcode(req.getBarcode().trim())
                    .ifPresent(existing -> {
                        if (!existing.getId().equals(id)) {
                            throw new RuntimeException("Barcode '" + req.getBarcode() + "' is already assigned to another product");
                        }
                    });
            product.setBarcode(req.getBarcode().trim());
        }

        if (req.getSku() != null && !req.getSku().trim().isEmpty()) {
            productRepository.findBySku(req.getSku().trim())
                    .ifPresent(existing -> {
                        if (!existing.getId().equals(id)) {
                            throw new RuntimeException("SKU '" + req.getSku() + "' is already assigned to another product");
                        }
                    });
            product.setSku(req.getSku().trim());
        }

        if (req.getCategoryId() != null) {
            Category category = categoryRepository.findById(req.getCategoryId()).orElse(null);
            product.setCategory(category);
        }

        product.setName(req.getName().trim());
        product.setBrand(req.getBrand());
        product.setModelNumber(req.getModelNumber());
        product.setUnit(req.getUnit());
        product.setCostPrice(req.getCostPrice());
        product.setSellingPrice(req.getSellingPrice());
        product.setCurrentStock(req.getCurrentStock());
        product.setMinStockAlert(req.getMinStockAlert());
        product.setShelfLocation(req.getShelfLocation());
        product.setDescription(req.getDescription());

        Product updated = productRepository.save(product);

        auditLogService.log(
                AuditAction.UPDATE_PRODUCT,
                "Updated product: " + updated.getName() + " (SKU: " + updated.getSku() + ", Stock: " + updated.getCurrentStock() + ")",
                request
        );

        return new ProductResponse(updated, true);
    }

    @Transactional
    public void deleteProduct(Long id, HttpServletRequest request) {
        Product product = getProductEntity(id);
        product.setActive(false);
        productRepository.save(product);

        auditLogService.log(
                AuditAction.DELETE_PRODUCT,
                "Soft deleted (archived) product: " + product.getName() + " (ID: " + product.getId() + ")",
                request
        );
    }

    @Transactional
    public ProductResponse restoreProduct(Long id, HttpServletRequest request) {
        Product product = getProductEntity(id);
        product.setActive(true);
        Product restored = productRepository.save(product);

        auditLogService.log(
                AuditAction.RESTORE_PRODUCT,
                "Restored product: " + restored.getName() + " (ID: " + restored.getId() + ")",
                request
        );

        return new ProductResponse(restored, true);
    }
}
