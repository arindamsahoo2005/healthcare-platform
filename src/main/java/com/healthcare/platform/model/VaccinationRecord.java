package com.healthcare.platform.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "vaccination_records")
public class VaccinationRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String patientName;
    private String targetGroup = "ADULT"; // CHILD, ADULT, SENIOR

    @Column(nullable = false)
    private String vaccineName;

    private String doseNumber = "Dose 1";
    private LocalDate administeredDate;
    private LocalDate nextDueDate;

    private String providerName = "AIIMS Immunization Clinic";
    private String batchNumber = "VAC-2026-X88";
    private String certificateNumber = "CERT-IMM-99120";
    private boolean completed = true;
    private String notes;

    public VaccinationRecord() {}

    public VaccinationRecord(String patientName, String targetGroup, String vaccineName, String doseNumber, LocalDate administeredDate, LocalDate nextDueDate, String providerName, boolean completed) {
        this.patientName = patientName;
        this.targetGroup = targetGroup;
        this.vaccineName = vaccineName;
        this.doseNumber = doseNumber;
        this.administeredDate = administeredDate;
        this.nextDueDate = nextDueDate;
        this.providerName = providerName;
        this.completed = completed;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }

    public String getTargetGroup() { return targetGroup; }
    public void setTargetGroup(String targetGroup) { this.targetGroup = targetGroup; }

    public String getVaccineName() { return vaccineName; }
    public void setVaccineName(String vaccineName) { this.vaccineName = vaccineName; }

    public String getDoseNumber() { return doseNumber; }
    public void setDoseNumber(String doseNumber) { this.doseNumber = doseNumber; }

    public LocalDate getAdministeredDate() { return administeredDate; }
    public void setAdministeredDate(LocalDate administeredDate) { this.administeredDate = administeredDate; }

    public LocalDate getNextDueDate() { return nextDueDate; }
    public void setNextDueDate(LocalDate nextDueDate) { this.nextDueDate = nextDueDate; }

    public String getProviderName() { return providerName; }
    public void setProviderName(String providerName) { this.providerName = providerName; }

    public String getBatchNumber() { return batchNumber; }
    public void setBatchNumber(String batchNumber) { this.batchNumber = batchNumber; }

    public String getCertificateNumber() { return certificateNumber; }
    public void setCertificateNumber(String certificateNumber) { this.certificateNumber = certificateNumber; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
