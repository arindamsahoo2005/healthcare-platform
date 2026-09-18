package com.healthcare.platform.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "blood_inventory")
public class BloodInventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String bloodBankName;

    private String hospitalAffiliation;
    private String address;
    private String city;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    private String contactPhone;

    @Column(nullable = false)
    private String bloodGroup; // A+, A-, B+, B-, AB+, AB-, O+, O-

    private String componentType = "WHOLE_BLOOD"; // WHOLE_BLOOD, PRBC, PLATELETS, FRESH_FROZEN_PLASMA
    private Integer unitsAvailable;
    private LocalDateTime lastUpdated = LocalDateTime.now();

    private String state;

    @Transient
    private Double distanceKm;

    public BloodInventory() {}

    public BloodInventory(String bloodBankName, String hospitalAffiliation, String address, String city, Double latitude, Double longitude, String contactPhone, String bloodGroup, String componentType, Integer unitsAvailable) {
        this.bloodBankName = bloodBankName;
        this.hospitalAffiliation = hospitalAffiliation;
        this.address = address;
        this.city = city;
        this.latitude = latitude;
        this.longitude = longitude;
        this.contactPhone = contactPhone;
        this.bloodGroup = bloodGroup;
        this.componentType = componentType;
        this.unitsAvailable = unitsAvailable;
    }

    public BloodInventory(String bloodBankName, String hospitalAffiliation, String address, String city, String state, Double latitude, Double longitude, String contactPhone, String bloodGroup, String componentType, Integer unitsAvailable) {
        this(bloodBankName, hospitalAffiliation, address, city, latitude, longitude, contactPhone, bloodGroup, componentType, unitsAvailable);
        this.state = state;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBloodBankName() { return bloodBankName; }
    public void setBloodBankName(String bloodBankName) { this.bloodBankName = bloodBankName; }

    public String getHospitalAffiliation() { return hospitalAffiliation; }
    public void setHospitalAffiliation(String hospitalAffiliation) { this.hospitalAffiliation = hospitalAffiliation; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }

    public String getComponentType() { return componentType; }
    public void setComponentType(String componentType) { this.componentType = componentType; }

    public Integer getUnitsAvailable() { return unitsAvailable; }
    public void setUnitsAvailable(Integer unitsAvailable) { this.unitsAvailable = unitsAvailable; }

    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }

    public Double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(Double distanceKm) { this.distanceKm = distanceKm; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public boolean isGovernment() {
        String n = (bloodBankName != null ? bloodBankName : "").toLowerCase();
        String h = (hospitalAffiliation != null ? hospitalAffiliation : "").toLowerCase();
        return n.contains("govt") || n.contains("government") || n.contains("red cross") ||
               n.contains("osmania") || n.contains("nims") || n.contains("gandhi") ||
               n.contains("rajiv gandhi") || n.contains("stanley") || n.contains("medical college") ||
               n.contains("kem") || n.contains("aiims") || n.contains("safdarjung") ||
               n.contains("victoria") || n.contains("rotary") ||
               h.contains("govt") || h.contains("government") || h.contains("medical college");
    }

    public String getSector() {
        return isGovernment() ? "Government" : "Private";
    }
}
