package com.smartshop.repository;

import com.smartshop.enums.AuditAction;
import com.smartshop.models.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findTop100ByOrderByTimestampDesc();
    List<AuditLog> findByActionOrderByTimestampDesc(AuditAction action);
    List<AuditLog> findByTimestampBetweenOrderByTimestampDesc(LocalDateTime start, LocalDateTime end);

    @Query("SELECT a FROM AuditLog a WHERE " +
           "LOWER(a.performedByUsername) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(a.details) LIKE LOWER(CONCAT('%', :query, '%')) ORDER BY a.timestamp DESC")
    List<AuditLog> searchAuditLogs(@Param("query") String query);
}
