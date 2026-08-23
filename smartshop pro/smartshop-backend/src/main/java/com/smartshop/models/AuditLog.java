package com.smartshop.models;

import com.smartshop.enums.AuditAction;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs", indexes = {
    @Index(name = "idx_audit_time", columnList = "timestamp"),
    @Index(name = "idx_audit_action", columnList = "action")
})
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private AuditAction action;

    @Column(length = 1000)
    private String details;

    @Column(length = 60)
    private String performedByUsername;

    @Column(length = 100)
    private String performedByFullName;

    @Column(length = 50)
    private String ipAddress;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();

    public AuditLog() {}

    public AuditLog(AuditAction action, String details, String performedByUsername, String performedByFullName, String ipAddress) {
        this.action = action;
        this.details = details;
        this.performedByUsername = performedByUsername;
        this.performedByFullName = performedByFullName;
        this.ipAddress = ipAddress;
        this.timestamp = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public AuditAction getAction() { return action; }
    public void setAction(AuditAction action) { this.action = action; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public String getPerformedByUsername() { return performedByUsername; }
    public void setPerformedByUsername(String performedByUsername) { this.performedByUsername = performedByUsername; }

    public String getPerformedByFullName() { return performedByFullName; }
    public void setPerformedByFullName(String performedByFullName) { this.performedByFullName = performedByFullName; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
