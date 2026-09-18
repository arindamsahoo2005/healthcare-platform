package com.healthcare.platform.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_access_logs")
public class AuditAccessLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String accessedBy; // Dr. Rajesh Sharma, Caregiver, First Responder
    private String role; // DOCTOR, CAREGIVER, FIRST_RESPONDER, PATIENT
    private String recordAccessed; // "Complete Lipid & HbA1c Panel", "Emergency Medical QR"
    private String accessType; // VIEW, EXPORT, EMERGENCY_SCAN, CONSENT_UPDATE
    private LocalDateTime timestamp = LocalDateTime.now();
    private String ipAddress = "127.0.0.1";
    private String status = "AUTHORIZED";

    public AuditAccessLog() {}

    public AuditAccessLog(String accessedBy, String role, String recordAccessed, String accessType, String status) {
        this.accessedBy = accessedBy;
        this.role = role;
        this.recordAccessed = recordAccessed;
        this.accessType = accessType;
        this.status = status;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAccessedBy() { return accessedBy; }
    public void setAccessedBy(String accessedBy) { this.accessedBy = accessedBy; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getRecordAccessed() { return recordAccessed; }
    public void setRecordAccessed(String recordAccessed) { this.recordAccessed = recordAccessed; }

    public String getAccessType() { return accessType; }
    public void setAccessType(String accessType) { this.accessType = accessType; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
