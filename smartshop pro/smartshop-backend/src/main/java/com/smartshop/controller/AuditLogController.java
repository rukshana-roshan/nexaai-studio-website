package com.smartshop.controller;

import com.smartshop.dto.ApiResponse;
import com.smartshop.models.AuditLog;
import com.smartshop.service.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/audit-logs")
@PreAuthorize("hasRole('OWNER') or hasRole('ADMIN')")
public class AuditLogController {

    private final AuditLogService auditLogService;

    @Autowired
    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AuditLog>>> getRecentLogs() {
        return ResponseEntity.ok(ApiResponse.ok("Audit logs retrieved", auditLogService.getRecentLogs()));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<AuditLog>>> searchLogs(@RequestParam(name = "q", required = false) String query) {
        return ResponseEntity.ok(ApiResponse.ok("Search results", auditLogService.searchLogs(query)));
    }
}
