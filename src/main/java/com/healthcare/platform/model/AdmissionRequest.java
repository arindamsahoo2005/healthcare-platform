package com.healthcare.platform.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "admission_requests")
public class AdmissionRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String patientName;
    private Integer patientAge;
    private String patientGender;
    private String contactPhone;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "hospital_id", nullable = false)
    private Hospital hospital;

    private String department; // Cardiology, Surgery, ICU, Pediatrics, etc.
    private String roomTypePreferred; // General Ward, Semi-Private, Deluxe Single, ICU
    private String urgency; // ROUTINE, URGENT, EMERGENCY

    // Workflow stages:
    // 1. REQUESTED
    // 2. DOCS_UPLOADED
    // 3. INSURANCE_VERIFIED
    // 4. HOSPITAL_CONFIRMED
    // 5. BED_ALLOCATED
    // 6. INPATIENT_STAY
    // 7. DISCHARGED
    private String status = "REQUESTED";

    private String allocatedBedNumber;
    private String allocatedDoctor;
    private String insuranceProvider;
    private String policyNumber;
    private String uploadedDocuments = "Doctor_Referral.pdf, Aadhaar_Card.pdf";
    private String admissionInstructions;
    private String dischargeSummary;

    private LocalDateTime requestedAt = LocalDateTime.now();
    private LocalDateTime admittedAt;
    private LocalDateTime dischargedAt;

    public AdmissionRequest() {}

    public AdmissionRequest(String patientName, Integer patientAge, String patientGender, String contactPhone, Hospital hospital, String department, String roomTypePreferred, String urgency) {
        this.patientName = patientName;
        this.patientAge = patientAge;
        this.patientGender = patientGender;
        this.contactPhone = contactPhone;
        this.hospital = hospital;
        this.department = department;
        this.roomTypePreferred = roomTypePreferred;
        this.urgency = urgency;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }

    public Integer getPatientAge() { return patientAge; }
    public void setPatientAge(Integer patientAge) { this.patientAge = patientAge; }

    public String getPatientGender() { return patientGender; }
    public void setPatientGender(String patientGender) { this.patientGender = patientGender; }

    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    public Hospital getHospital() { return hospital; }
    public void setHospital(Hospital hospital) { this.hospital = hospital; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getRoomTypePreferred() { return roomTypePreferred; }
    public void setRoomTypePreferred(String roomTypePreferred) { this.roomTypePreferred = roomTypePreferred; }

    public String getUrgency() { return urgency; }
    public void setUrgency(String urgency) { this.urgency = urgency; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getAllocatedBedNumber() { return allocatedBedNumber; }
    public void setAllocatedBedNumber(String allocatedBedNumber) { this.allocatedBedNumber = allocatedBedNumber; }

    public String getAllocatedDoctor() { return allocatedDoctor; }
    public void setAllocatedDoctor(String allocatedDoctor) { this.allocatedDoctor = allocatedDoctor; }

    public String getInsuranceProvider() { return insuranceProvider; }
    public void setInsuranceProvider(String insuranceProvider) { this.insuranceProvider = insuranceProvider; }

    public String getPolicyNumber() { return policyNumber; }
    public void setPolicyNumber(String policyNumber) { this.policyNumber = policyNumber; }

    public String getUploadedDocuments() { return uploadedDocuments; }
    public void setUploadedDocuments(String uploadedDocuments) { this.uploadedDocuments = uploadedDocuments; }

    public String getAdmissionInstructions() { return admissionInstructions; }
    public void setAdmissionInstructions(String admissionInstructions) { this.admissionInstructions = admissionInstructions; }

    public String getDischargeSummary() { return dischargeSummary; }
    public void setDischargeSummary(String dischargeSummary) { this.dischargeSummary = dischargeSummary; }

    public LocalDateTime getRequestedAt() { return requestedAt; }
    public void setRequestedAt(LocalDateTime requestedAt) { this.requestedAt = requestedAt; }

    public LocalDateTime getAdmittedAt() { return admittedAt; }
    public void setAdmittedAt(LocalDateTime admittedAt) { this.admittedAt = admittedAt; }

    public LocalDateTime getDischargedAt() { return dischargedAt; }
    public void setDischargedAt(LocalDateTime dischargedAt) { this.dischargedAt = dischargedAt; }
}
