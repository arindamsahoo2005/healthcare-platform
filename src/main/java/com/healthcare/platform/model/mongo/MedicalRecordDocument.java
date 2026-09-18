package com.healthcare.platform.model.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Document(collection = "medical_records")
public class MedicalRecordDocument {

    @Id
    private String id;
    private String patientUid;
    private String patientName;
    private String recordType;
    private String title;
    private String facilityName;
    private String doctorName;
    private LocalDate recordDate;
    private String summaryText;
    private String keyFindings;
    private String fileName;
    private String fileType;
    private String fileSize;
    private Map<String, String> labMetrics = new HashMap<>();
    private LocalDateTime createdAt = LocalDateTime.now();

    public MedicalRecordDocument() {}

    public MedicalRecordDocument(String patientUid, String patientName, String recordType, String title,
                                 String facilityName, String doctorName, LocalDate recordDate,
                                 String summaryText, String keyFindings, String fileName, String fileType, String fileSize) {
        this.patientUid = patientUid;
        this.patientName = patientName;
        this.recordType = recordType;
        this.title = title;
        this.facilityName = facilityName;
        this.doctorName = doctorName;
        this.recordDate = recordDate;
        this.summaryText = summaryText;
        this.keyFindings = keyFindings;
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileSize = fileSize;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPatientUid() { return patientUid; }
    public void setPatientUid(String patientUid) { this.patientUid = patientUid; }

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

    public Map<String, String> getLabMetrics() { return labMetrics; }
    public void setLabMetrics(Map<String, String> labMetrics) { this.labMetrics = labMetrics; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
