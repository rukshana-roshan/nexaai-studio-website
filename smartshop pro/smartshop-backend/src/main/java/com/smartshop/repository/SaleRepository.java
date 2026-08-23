package com.smartshop.repository;

import com.smartshop.enums.SaleStatus;
import com.smartshop.models.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {
    Optional<Sale> findByInvoiceNumber(String invoiceNumber);

    List<Sale> findByCashierIdOrderBySaleDateDesc(Long cashierId);

    List<Sale> findByCashierIdAndSaleDateBetweenOrderBySaleDateDesc(Long cashierId, LocalDateTime start, LocalDateTime end);

    List<Sale> findBySaleDateBetweenOrderBySaleDateDesc(LocalDateTime start, LocalDateTime end);

    @Query("SELECT s FROM Sale s LEFT JOIN FETCH s.items WHERE s.id = :id")
    Optional<Sale> findByIdWithItems(@Param("id") Long id);

    @Query("SELECT COALESCE(SUM(s.grandTotal), 0) FROM Sale s WHERE s.status = 'COMPLETED' AND s.saleDate BETWEEN :start AND :end")
    BigDecimal sumTotalSalesBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COALESCE(SUM(s.totalProfit), 0) FROM Sale s WHERE s.status = 'COMPLETED' AND s.saleDate BETWEEN :start AND :end")
    BigDecimal sumTotalProfitBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COALESCE(SUM(s.totalCost), 0) FROM Sale s WHERE s.status = 'COMPLETED' AND s.saleDate BETWEEN :start AND :end")
    BigDecimal sumTotalCostBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(s) FROM Sale s WHERE s.status = 'COMPLETED' AND s.saleDate BETWEEN :start AND :end")
    long countSalesBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT s.paymentMethod, COUNT(s), SUM(s.grandTotal) FROM Sale s WHERE s.status = 'COMPLETED' AND s.saleDate BETWEEN :start AND :end GROUP BY s.paymentMethod")
    List<Object[]> getSalesByPaymentMethodBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
