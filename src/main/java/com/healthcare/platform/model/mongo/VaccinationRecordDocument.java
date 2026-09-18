package com.healthcare.platform.model.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document(collection = "vaccinations")
public class VaccinationRecordDocument {

    @Id
    private String id;
    private String patientName;
    private String category;
    private String vaccineName;
    private String doseNumber;
    private LocalDate administrationDate;
    private LocalDate nextDueDate;
    private String healthcareFacility;
    private String batchNumber;
    private String certificateNumber;
    private boolean verified = true;
    private String notes;

    public VaccinationRecordDocument() {}

    public VaccinationRecordDocument(String patientName, String category, String vaccineName,
                                     String doseNumber, LocalDate administrationDate,
                                     LocalDate nextDueDate, String healthcareFacility, boolean verified) {
        this.patientName = patientName;
        this.category = category;
        this.vaccineName = vaccineName;
        this.doseNumber = doseNumber;
        this.administrationDate = administrationDate;
        this.nextDueDate = nextDueDate;
        this.healthcareFacility = healthcareFacility;
        this.verified = verified;
        this.certificateNumber = "WB-COV-" + System.currentTimeMillis();
        this.batchNumber = "BATCH-" + (int)(Math.random() * 90000 + 10000);
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getVaccineName() { return vaccineName; }
    public void setVaccineName(String vaccineName) { this.vaccineName = vaccineName; }

    public String getDoseNumber() { return doseNumber; }
    public void setDoseNumber(String doseNumber) { this.doseNumber = doseNumber; }

    public LocalDate getAdministrationDate() { return administrationDate; }
    public void setAdministrationDate(LocalDate administrationDate) { this.administrationDate = administrationDate; }

    public LocalDate getNextDueDate() { return nextDueDate; }
    public void setNextDueDate(LocalDate nextDueDate) { this.nextDueDate = nextDueDate; }

    public String getHealthcareFacility() { return healthcareFacility; }
    public void setHealthcareFacility(String healthcareFacility) { this.healthcareFacility = healthcareFacility; }

    public String getBatchNumber() { return batchNumber; }
    public void setBatchNumber(String batchNumber) { this.batchNumber = batchNumber; }

    public String getCertificateNumber() { return certificateNumber; }
    public void setCertificateNumber(String certificateNumber) { this.certificateNumber = certificateNumber; }

    public boolean isVerified() { return verified; }
    public void setVerified(boolean verified) { this.verified = verified; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
