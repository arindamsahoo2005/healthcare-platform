package com.healthcare.platform.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "medicine_logs")
public class MedicineLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "schedule_id", nullable = false)
    private MedicineSchedule schedule;

    private LocalDateTime scheduledDateTime;
    private LocalDateTime actionDateTime;

    @Column(nullable = false)
    private String status; // PENDING, TAKEN, SNOOZED, SKIPPED, MISSED

    private Integer snoozeMinutes = 10;
    private LocalDateTime snoozeUntil;
    private String skipReason; // e.g. "Stomach upset", "Doctor advised hold", "Fasting", "Out of stock"
    private String safetyWarningText; // Reminder never to take an extra dose
    private boolean caregiverNotified = false;

    public MedicineLog() {}

    public MedicineLog(MedicineSchedule schedule, LocalDateTime scheduledDateTime, String status) {
        this.schedule = schedule;
        this.scheduledDateTime = scheduledDateTime;
        this.status = status;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public MedicineSchedule getSchedule() { return schedule; }
    public void setSchedule(MedicineSchedule schedule) { this.schedule = schedule; }

    public LocalDateTime getScheduledDateTime() { return scheduledDateTime; }
    public void setScheduledDateTime(LocalDateTime scheduledDateTime) { this.scheduledDateTime = scheduledDateTime; }

    public LocalDateTime getActionDateTime() { return actionDateTime; }
    public void setActionDateTime(LocalDateTime actionDateTime) { this.actionDateTime = actionDateTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getSnoozeMinutes() { return snoozeMinutes; }
    public void setSnoozeMinutes(Integer snoozeMinutes) { this.snoozeMinutes = snoozeMinutes; }

    public LocalDateTime getSnoozeUntil() { return snoozeUntil; }
    public void setSnoozeUntil(LocalDateTime snoozeUntil) { this.snoozeUntil = snoozeUntil; }

    public String getSkipReason() { return skipReason; }
    public void setSkipReason(String skipReason) { this.skipReason = skipReason; }

    public String getSafetyWarningText() { return safetyWarningText; }
    public void setSafetyWarningText(String safetyWarningText) { this.safetyWarningText = safetyWarningText; }

    public boolean isCaregiverNotified() { return caregiverNotified; }
    public void setCaregiverNotified(boolean caregiverNotified) { this.caregiverNotified = caregiverNotified; }
}
