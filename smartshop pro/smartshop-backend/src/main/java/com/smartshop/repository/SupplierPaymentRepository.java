package com.smartshop.repository;

import com.smartshop.models.SupplierPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SupplierPaymentRepository extends JpaRepository<SupplierPayment, Long> {
    List<SupplierPayment> findBySupplierIdOrderByPaymentDateDesc(Long supplierId);
    List<SupplierPayment> findByPaymentDateBetweenOrderByPaymentDateDesc(LocalDateTime start, LocalDateTime end);
}
