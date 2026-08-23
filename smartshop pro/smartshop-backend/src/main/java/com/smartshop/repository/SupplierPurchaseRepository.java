package com.smartshop.repository;

import com.smartshop.models.SupplierPurchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierPurchaseRepository extends JpaRepository<SupplierPurchase, Long> {
    Optional<SupplierPurchase> findByInvoiceNumber(String invoiceNumber);
    List<SupplierPurchase> findBySupplierIdOrderByPurchaseDateDesc(Long supplierId);
    List<SupplierPurchase> findByPurchaseDateBetweenOrderByPurchaseDateDesc(LocalDateTime start, LocalDateTime end);

    @Query("SELECT p FROM SupplierPurchase p LEFT JOIN FETCH p.items WHERE p.id = :id")
    Optional<SupplierPurchase> findByIdWithItems(@Param("id") Long id);

    @Query("SELECT COUNT(p) FROM SupplierPurchase p")
    long countTotalPurchases();
}
