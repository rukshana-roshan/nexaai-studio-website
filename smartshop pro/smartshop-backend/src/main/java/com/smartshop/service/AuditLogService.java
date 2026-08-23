package com.smartshop.service;

import com.smartshop.enums.AuditAction;
import com.smartshop.models.AuditLog;
import com.smartshop.repository.AuditLogRepository;
import com.smartshop.security.UserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Autowired
    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional
    public void log(AuditAction action, String details, HttpServletRequest request) {
        String username = "ANONYMOUS";
        String fullName = "System / Anonymous";

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserPrincipal principal) {
            username = principal.getUsername();
            fullName = principal.getFullName();
        }

        String ipAddress = "0:0:0:0:0:0:0:1";
        if (request != null) {
            ipAddress = request.getHeader("X-Forwarded-For");
            if (ipAddress == null || ipAddress.isEmpty()) {
                ipAddress = request.getRemoteAddr();
            }
        }

        AuditLog log = new AuditLog(action, details, username, fullName, ipAddress);
        auditLogRepository.save(log);
    }

    @Transactional
    public void logWithUser(AuditAction action, String details, String username, String fullName, HttpServletRequest request) {
        String ipAddress = "0:0:0:0:0:0:0:1";
        if (request != null) {
            ipAddress = request.getHeader("X-Forwarded-For");
            if (ipAddress == null || ipAddress.isEmpty()) {
                ipAddress = request.getRemoteAddr();
            }
        }

        AuditLog log = new AuditLog(action, details, username, fullName, ipAddress);
        auditLogRepository.save(log);
    }

    @Transactional(readOnly = true)
    public List<AuditLog> getRecentLogs() {
        return auditLogRepository.findTop100ByOrderByTimestampDesc();
    }

    @Transactional(readOnly = true)
    public List<AuditLog> searchLogs(String query) {
        if (query == null || query.trim().isEmpty()) {
            return auditLogRepository.findTop100ByOrderByTimestampDesc();
        }
        return auditLogRepository.searchAuditLogs(query.trim());
    }

    @Transactional(readOnly = true)
    public List<AuditLog> getLogsByDateRange(LocalDateTime start, LocalDateTime end) {
        return auditLogRepository.findByTimestampBetweenOrderByTimestampDesc(start, end);
    }
}
