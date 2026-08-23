package com.smartshop.repository;

import com.smartshop.models.SupplierLedgerEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SupplierLedgerEntryRepository extends JpaRepository<SupplierLedgerEntry, Long> {
    List<SupplierLedgerEntry> findBySupplierIdOrderByTransactionDateAscIdAsc(Long supplierId);
    List<SupplierLedgerEntry> findBySupplierIdOrderByTransactionDateDescIdDesc(Long supplierId);

    @Query("SELECT l FROM SupplierLedgerEntry l WHERE l.supplier.id = :supplierId " +
           "AND l.transactionDate BETWEEN :startDate AND :endDate ORDER BY l.transactionDate ASC, l.id ASC")
    List<SupplierLedgerEntry> findBySupplierIdAndDateRange(
            @Param("supplierId") Long supplierId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}
