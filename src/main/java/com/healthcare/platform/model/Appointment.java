package com.healthcare.platform.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String patientName;
    private String patientPhone;
    private String patientEmail;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    private LocalDateTime appointmentDateTime;
    private String consultationType = "IN_PERSON"; // IN_PERSON, VIDEO
    private String status = "CONFIRMED"; // CONFIRMED, COMPLETED, CANCELLED, RESCHEDULED

    private String tokenNumber; // Queue token, e.g. "A-14"
    private Double consultationFee;
    private String preVisitSymptoms;
    private String aiAppointmentPrepNotes;
    private String doctorClinicalNotes;
    private String prescriptionSummary;
    private String videoMeetingLink;

    // Outdoor Visit (OPD) & Private Chamber Specific Fields
    private Integer opdSeatNumber;
    private String chamberRoom;
    private String reportingTime;
    private String opdCategory = "General Outdoor";
    private String opdDate;
    private String hospitalName;

    // Direct Doctor Private Chamber Details
    private String consultationMode = "IN_PERSON"; // PRIVATE_CHAMBER, HOSPITAL_OPD, VIDEO
    private String chamberAddress;
    private String timeSlot;

    private LocalDateTime createdAt = LocalDateTime.now();

    public Appointment() {}

    public Appointment(String patientName, String patientPhone, Doctor doctor, LocalDateTime appointmentDateTime, String consultationType, String preVisitSymptoms) {
        this.patientName = patientName;
        this.patientPhone = patientPhone;
        this.doctor = doctor;
        this.appointmentDateTime = appointmentDateTime;
        this.consultationType = consultationType;
        this.preVisitSymptoms = preVisitSymptoms;
        this.consultationFee = doctor != null ? doctor.getConsultationFee() : 500.0;
        this.tokenNumber = "TK-" + ((int)(Math.random() * 900) + 100);
        if ("VIDEO".equalsIgnoreCase(consultationType)) {
            this.videoMeetingLink = "https://meet.healthcare-platform.local/room/" + ((int)(Math.random() * 90000) + 10000);
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }

    public String getPatientPhone() { return patientPhone; }
    public void setPatientPhone(String patientPhone) { this.patientPhone = patientPhone; }

    public String getPatientEmail() { return patientEmail; }
    public void setPatientEmail(String patientEmail) { this.patientEmail = patientEmail; }

    public Doctor getDoctor() { return doctor; }
    public void setDoctor(Doctor doctor) { this.doctor = doctor; }

    public LocalDateTime getAppointmentDateTime() { return appointmentDateTime; }
    public void setAppointmentDateTime(LocalDateTime appointmentDateTime) { this.appointmentDateTime = appointmentDateTime; }

    public String getConsultationType() { return consultationType; }
    public void setConsultationType(String consultationType) { this.consultationType = consultationType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getTokenNumber() { return tokenNumber; }
    public void setTokenNumber(String tokenNumber) { this.tokenNumber = tokenNumber; }

    public Double getConsultationFee() { return consultationFee; }
    public void setConsultationFee(Double consultationFee) { this.consultationFee = consultationFee; }

    public String getPreVisitSymptoms() { return preVisitSymptoms; }
    public void setPreVisitSymptoms(String preVisitSymptoms) { this.preVisitSymptoms = preVisitSymptoms; }

    public String getAiAppointmentPrepNotes() { return aiAppointmentPrepNotes; }
    public void setAiAppointmentPrepNotes(String aiAppointmentPrepNotes) { this.aiAppointmentPrepNotes = aiAppointmentPrepNotes; }

    public String getDoctorClinicalNotes() { return doctorClinicalNotes; }
    public void setDoctorClinicalNotes(String doctorClinicalNotes) { this.doctorClinicalNotes = doctorClinicalNotes; }

    public String getPrescriptionSummary() { return prescriptionSummary; }
    public void setPrescriptionSummary(String prescriptionSummary) { this.prescriptionSummary = prescriptionSummary; }

    public String getVideoMeetingLink() { return videoMeetingLink; }
    public void setVideoMeetingLink(String videoMeetingLink) { this.videoMeetingLink = videoMeetingLink; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Integer getOpdSeatNumber() { return opdSeatNumber; }
    public void setOpdSeatNumber(Integer opdSeatNumber) { this.opdSeatNumber = opdSeatNumber; }

    public String getChamberRoom() { return chamberRoom; }
    public void setChamberRoom(String chamberRoom) { this.chamberRoom = chamberRoom; }

    public String getReportingTime() { return reportingTime; }
    public void setReportingTime(String reportingTime) { this.reportingTime = reportingTime; }

    public String getOpdCategory() { return opdCategory; }
    public void setOpdCategory(String opdCategory) { this.opdCategory = opdCategory; }

    public String getOpdDate() { return opdDate; }
    public void setOpdDate(String opdDate) { this.opdDate = opdDate; }

    public String getHospitalName() { return hospitalName; }
    public void setHospitalName(String hospitalName) { this.hospitalName = hospitalName; }

    public String getConsultationMode() { return consultationMode; }
    public void setConsultationMode(String consultationMode) { this.consultationMode = consultationMode; }

    public String getChamberAddress() { return chamberAddress; }
    public void setChamberAddress(String chamberAddress) { this.chamberAddress = chamberAddress; }

    public String getTimeSlot() { return timeSlot; }
    public void setTimeSlot(String timeSlot) { this.timeSlot = timeSlot; }
}
