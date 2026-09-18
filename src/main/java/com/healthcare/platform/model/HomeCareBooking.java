package com.healthcare.platform.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "home_care_bookings")
public class HomeCareBooking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String patientName;
    private String contactPhone;
    private String address;
    private String city;

    @Column(nullable = false)
    private String serviceType; // HOME_NURSE, PHYSIOTHERAPY, ELDERLY_CARE, POST_OP_CARE

    private LocalDate scheduledDate;
    private String preferredTimeSlot = "10:00 AM - 12:00 PM";
    private String clinicalNeeds;

    private String professionalAssigned = "Sister Ananya (Registered Clinical Nurse)";
    private String professionalPhone = "+91 98765 99881";
    private Double feePerVisit = 850.0;

    // Status: CONFIRMED, IN_PROGRESS, COMPLETED, CANCELLED
    private String status = "CONFIRMED";
    private LocalDateTime bookedAt = LocalDateTime.now();

    public HomeCareBooking() {}

    public HomeCareBooking(String patientName, String contactPhone, String address, String city, String serviceType, LocalDate scheduledDate, String clinicalNeeds, Double feePerVisit) {
        this.patientName = patientName;
        this.contactPhone = contactPhone;
        this.address = address;
        this.city = city;
        this.serviceType = serviceType;
        this.scheduledDate = scheduledDate;
        this.clinicalNeeds = clinicalNeeds;
        this.feePerVisit = feePerVisit;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }

    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getServiceType() { return serviceType; }
    public void setServiceType(String serviceType) { this.serviceType = serviceType; }

    public LocalDate getScheduledDate() { return scheduledDate; }
    public void setScheduledDate(LocalDate scheduledDate) { this.scheduledDate = scheduledDate; }

    public String getPreferredTimeSlot() { return preferredTimeSlot; }
    public void setPreferredTimeSlot(String preferredTimeSlot) { this.preferredTimeSlot = preferredTimeSlot; }

    public String getClinicalNeeds() { return clinicalNeeds; }
    public void setClinicalNeeds(String clinicalNeeds) { this.clinicalNeeds = clinicalNeeds; }

    public String getProfessionalAssigned() { return professionalAssigned; }
    public void setProfessionalAssigned(String professionalAssigned) { this.professionalAssigned = professionalAssigned; }

    public String getProfessionalPhone() { return professionalPhone; }
    public void setProfessionalPhone(String professionalPhone) { this.professionalPhone = professionalPhone; }

    public Double getFeePerVisit() { return feePerVisit; }
    public void setFeePerVisit(Double feePerVisit) { this.feePerVisit = feePerVisit; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getBookedAt() { return bookedAt; }
    public void setBookedAt(LocalDateTime bookedAt) { this.bookedAt = bookedAt; }
}
