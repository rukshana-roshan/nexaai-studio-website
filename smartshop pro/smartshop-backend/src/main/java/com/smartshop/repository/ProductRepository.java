package com.smartshop.repository;

import com.smartshop.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByBarcodeAndActiveTrue(String barcode);
    Optional<Product> findBySkuAndActiveTrue(String sku);
    Optional<Product> findByBarcode(String barcode);
    Optional<Product> findBySku(String sku);

    List<Product> findByActiveTrue();
    List<Product> findByActiveFalse();
    List<Product> findByCategoryIdAndActiveTrue(Long categoryId);

    @Query("SELECT p FROM Product p WHERE p.active = true AND p.currentStock <= p.minStockAlert ORDER BY p.currentStock ASC")
    List<Product> findLowStockProducts();

    @Query("SELECT COUNT(p) FROM Product p WHERE p.active = true AND p.currentStock <= p.minStockAlert")
    long countLowStockProducts();

    @Query("SELECT COUNT(p) FROM Product p WHERE p.active = true AND p.currentStock = 0")
    long countOutOfStockProducts();

    @Query("SELECT p FROM Product p WHERE p.active = true AND " +
           "(LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.barcode) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.sku) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.brand) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Product> searchProducts(@Param("query") String query);

    @Query("SELECT COALESCE(SUM(p.costPrice * p.currentStock), 0) FROM Product p WHERE p.active = true")
    BigDecimal calculateTotalStockValueAtCost();

    @Query("SELECT COALESCE(SUM(p.sellingPrice * p.currentStock), 0) FROM Product p WHERE p.active = true")
    BigDecimal calculateTotalStockValueAtRetail();
}
