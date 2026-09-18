package com.healthcare.platform.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ambulance_requests")
public class AmbulanceRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String patientName;
    private String patientPhone;
    private String pickupAddress;
    private String destinationHospital;

    private Double userLatitude;
    private Double userLongitude;

    private String ambulanceType = "ALS"; // BLS, ALS, ICU Mobile
    private String vehicleNumber;
    private String driverName;
    private String driverPhone;

    // Status: DISPATCHED -> EN_ROUTE -> ARRIVED -> AT_HOSPITAL -> COMPLETED
    private String status = "DISPATCHED";
    private Integer etaMinutes = 8;
    private Double distanceKm;
    private boolean emergencyContactAlertSent = true;

    private LocalDateTime requestedAt = LocalDateTime.now();

    public AmbulanceRequest() {}

    public AmbulanceRequest(String patientName, String patientPhone, String pickupAddress, Double userLatitude, Double userLongitude, String ambulanceType) {
        this.patientName = patientName;
        this.patientPhone = patientPhone;
        this.pickupAddress = pickupAddress;
        this.userLatitude = userLatitude;
        this.userLongitude = userLongitude;
        this.ambulanceType = ambulanceType;
        this.vehicleNumber = "MED-" + ((int)(Math.random() * 9000) + 1000);
        this.driverName = "Suresh Paramedic";
        this.driverPhone = "+91 98765 43210";
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }

    public String getPatientPhone() { return patientPhone; }
    public void setPatientPhone(String patientPhone) { this.patientPhone = patientPhone; }

    public String getPickupAddress() { return pickupAddress; }
    public void setPickupAddress(String pickupAddress) { this.pickupAddress = pickupAddress; }

    public String getDestinationHospital() { return destinationHospital; }
    public void setDestinationHospital(String destinationHospital) { this.destinationHospital = destinationHospital; }

    public Double getUserLatitude() { return userLatitude; }
    public void setUserLatitude(Double userLatitude) { this.userLatitude = userLatitude; }

    public Double getUserLongitude() { return userLongitude; }
    public void setUserLongitude(Double userLongitude) { this.userLongitude = userLongitude; }

    public String getAmbulanceType() { return ambulanceType; }
    public void setAmbulanceType(String ambulanceType) { this.ambulanceType = ambulanceType; }

    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }

    public String getDriverName() { return driverName; }
    public void setDriverName(String driverName) { this.driverName = driverName; }

    public String getDriverPhone() { return driverPhone; }
    public void setDriverPhone(String driverPhone) { this.driverPhone = driverPhone; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getEtaMinutes() { return etaMinutes; }
    public void setEtaMinutes(Integer etaMinutes) { this.etaMinutes = etaMinutes; }

    public Double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(Double distanceKm) { this.distanceKm = distanceKm; }

    public boolean isEmergencyContactAlertSent() { return emergencyContactAlertSent; }
    public void setEmergencyContactAlertSent(boolean emergencyContactAlertSent) { this.emergencyContactAlertSent = emergencyContactAlertSent; }

    public LocalDateTime getRequestedAt() { return requestedAt; }
    public void setRequestedAt(LocalDateTime requestedAt) { this.requestedAt = requestedAt; }
}
