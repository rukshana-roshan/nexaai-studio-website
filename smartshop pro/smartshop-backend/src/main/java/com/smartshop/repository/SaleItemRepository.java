package com.smartshop.repository;

import com.smartshop.models.SaleItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SaleItemRepository extends JpaRepository<SaleItem, Long> {
    List<SaleItem> findBySaleId(Long saleId);

    @Query("SELECT si.product.id, si.productName, si.sku, SUM(si.quantity), SUM(si.subtotal), SUM(si.itemProfit) " +
           "FROM SaleItem si WHERE si.sale.status = 'COMPLETED' AND si.sale.saleDate BETWEEN :start AND :end " +
           "GROUP BY si.product.id, si.productName, si.sku ORDER BY SUM(si.quantity) DESC")
    List<Object[]> findTopSellingProductsBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT si.product.category.name, SUM(si.quantity), SUM(si.subtotal) " +
           "FROM SaleItem si WHERE si.sale.status = 'COMPLETED' AND si.product.category IS NOT NULL AND si.sale.saleDate BETWEEN :start AND :end " +
           "GROUP BY si.product.category.name ORDER BY SUM(si.subtotal) DESC")
    List<Object[]> findCategorySalesDistributionBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
