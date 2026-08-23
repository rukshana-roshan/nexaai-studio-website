package com.smartshop.repository;

import com.smartshop.models.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    List<Supplier> findByActiveTrue();
    List<Supplier> findByActiveFalse();

    @Query("SELECT s FROM Supplier s WHERE s.active = true AND " +
           "(LOWER(s.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(s.companyName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(s.phone) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Supplier> searchSuppliers(@Param("query") String query);

    @Query("SELECT COALESCE(SUM(s.currentBalance), 0) FROM Supplier s WHERE s.active = true")
    BigDecimal calculateTotalPayableBalance();
}
