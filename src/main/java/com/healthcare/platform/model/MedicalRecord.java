package com.healthcare.platform.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "medical_records")
public class MedicalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String patientName;
    
    @Column(nullable = false)
    private String recordType; // BLOOD_TEST, PRESCRIPTION, DOCTOR_NOTES, DISCHARGE_SUMMARY, IMAGING_REPORT, VACCINATION

    @Column(nullable = false)
    private String title;

    private String facilityName;
    private String doctorName;
    private LocalDate recordDate;

    @Column(length = 2000)
    private String summaryText; // Extracted text for "Ask My Records" AI engine

    @Column(length = 1000)
    private String keyFindings;

    private String fileName;
    private String fileType = "application/pdf";
    private String fileSize = "1.2 MB";

    public MedicalRecord() {}

    public MedicalRecord(String patientName, String recordType, String title, String facilityName, String doctorName, LocalDate recordDate, String summaryText, String keyFindings, String fileName) {
        this.patientName = patientName;
        this.recordType = recordType;
        this.title = title;
        this.facilityName = facilityName;
        this.doctorName = doctorName;
        this.recordDate = recordDate;
        this.summaryText = summaryText;
        this.keyFindings = keyFindings;
        this.fileName = fileName;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }

    public String getRecordType() { return recordType; }
    public void setRecordType(String recordType) { this.recordType = recordType; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getFacilityName() { return facilityName; }
    public void setFacilityName(String facilityName) { this.facilityName = facilityName; }

    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }

    public LocalDate getRecordDate() { return recordDate; }
    public void setRecordDate(LocalDate recordDate) { this.recordDate = recordDate; }

    public String getSummaryText() { return summaryText; }
    public void setSummaryText(String summaryText) { this.summaryText = summaryText; }

    public String getKeyFindings() { return keyFindings; }
    public void setKeyFindings(String keyFindings) { this.keyFindings = keyFindings; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }

    public String getFileSize() { return fileSize; }
    public void setFileSize(String fileSize) { this.fileSize = fileSize; }
}
