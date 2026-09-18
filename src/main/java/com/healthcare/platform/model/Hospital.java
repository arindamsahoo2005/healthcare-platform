package com.healthcare.platform.model;

import jakarta.persistence.*;

@Entity
@Table(name = "hospitals")
public class Hospital {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String type; // Multi-specialty, Super-specialty, Government, Private
    private String address;
    private String city;
    private String state;
    private String postalCode;

    // Location coordinates for Haversine proximity calculations
    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    private String phone;
    private String emergencyHelpline;
    private String email;

    // Real-time Bed and Room capacity
    private Integer icuBedsTotal = 30;
    private Integer icuBedsAvailable = 8;
    private Integer generalBedsTotal = 250;
    private Integer generalBedsAvailable = 42;
    private Integer ventilatorBedsTotal = 15;
    private Integer ventilatorBedsAvailable = 4;
    private Integer oxygenBedsTotal = 80;
    private Integer oxygenBedsAvailable = 20;

    // Registry & Accreditation
    private String abdmFacilityId;
    private String emergencyTraumaLevel = "Level 1 Trauma & Resuscitation Center";
    private boolean nabhAccredited = true;
    private Integer ambulanceCount = 6;

    // Facilities
    private boolean emergencyDept = true;
    private boolean pediatricServices = true;
    private boolean maternityServices = true;
    private boolean surgeryDepartments = true;
    private boolean diagnostics = true;
    private boolean pharmacy = true;

    @Column(length = 1000)
    private String departments = "Emergency, Cardiology, Neurology, Orthopedics, Pediatrics, Oncology, General Surgery";

    private Double rating = 4.8;
    private Integer reviewCount = 350;
    private String imageUrl;

    // Transient field for user proximity calculation
    @Transient
    private Double distanceKm;

    public Hospital() {}

    public Hospital(String name, String type, String address, String city, String state, Double latitude, Double longitude, String phone, String emergencyHelpline, Integer icuBedsTotal, Integer icuBedsAvailable, Integer generalBedsTotal, Integer generalBedsAvailable) {
        this.name = name;
        this.type = type;
        this.address = address;
        this.city = city;
        this.state = state;
        this.latitude = latitude;
        this.longitude = longitude;
        this.phone = phone;
        this.emergencyHelpline = emergencyHelpline;
        this.icuBedsTotal = icuBedsTotal;
        this.icuBedsAvailable = icuBedsAvailable;
        this.generalBedsTotal = generalBedsTotal;
        this.generalBedsAvailable = generalBedsAvailable;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmergencyHelpline() { return emergencyHelpline; }
    public void setEmergencyHelpline(String emergencyHelpline) { this.emergencyHelpline = emergencyHelpline; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Integer getIcuBedsTotal() { return icuBedsTotal; }
    public void setIcuBedsTotal(Integer icuBedsTotal) { this.icuBedsTotal = icuBedsTotal; }

    public Integer getIcuBedsAvailable() { return icuBedsAvailable; }
    public void setIcuBedsAvailable(Integer icuBedsAvailable) { this.icuBedsAvailable = icuBedsAvailable; }

    public Integer getGeneralBedsTotal() { return generalBedsTotal; }
    public void setGeneralBedsTotal(Integer generalBedsTotal) { this.generalBedsTotal = generalBedsTotal; }

    public Integer getGeneralBedsAvailable() { return generalBedsAvailable; }
    public void setGeneralBedsAvailable(Integer generalBedsAvailable) { this.generalBedsAvailable = generalBedsAvailable; }

    public boolean isEmergencyDept() { return emergencyDept; }
    public void setEmergencyDept(boolean emergencyDept) { this.emergencyDept = emergencyDept; }

    public boolean isPediatricServices() { return pediatricServices; }
    public void setPediatricServices(boolean pediatricServices) { this.pediatricServices = pediatricServices; }

    public boolean isMaternityServices() { return maternityServices; }
    public void setMaternityServices(boolean maternityServices) { this.maternityServices = maternityServices; }

    public boolean isSurgeryDepartments() { return surgeryDepartments; }
    public void setSurgeryDepartments(boolean surgeryDepartments) { this.surgeryDepartments = surgeryDepartments; }

    public boolean isDiagnostics() { return diagnostics; }
    public void setDiagnostics(boolean diagnostics) { this.diagnostics = diagnostics; }

    public boolean isPharmacy() { return pharmacy; }
    public void setPharmacy(boolean pharmacy) { this.pharmacy = pharmacy; }

    public String getDepartments() { return departments; }
    public void setDepartments(String departments) { this.departments = departments; }

    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }

    public Integer getReviewCount() { return reviewCount; }
    public void setReviewCount(Integer reviewCount) { this.reviewCount = reviewCount; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(Double distanceKm) { this.distanceKm = distanceKm; }

    public String getEmergencyPhone() { return emergencyHelpline; }
    public void setEmergencyPhone(String emergencyPhone) { this.emergencyHelpline = emergencyPhone; }

    public boolean isGovernment() {
        String t = (type != null ? type : "").toLowerCase();
        String n = (name != null ? name : "").toLowerCase();
        return t.contains("government") || t.contains("govt") || t.contains("autonomous") ||
               n.contains("government") || n.contains("govt") || n.contains("nims") ||
               n.contains("osmania") || n.contains("gandhi") || n.contains("aiims") ||
               n.contains("esi ") || n.contains("esi hospital") || n.contains("sskm") ||
               n.contains("medical college") || n.contains("cancer centre") || n.contains("mnj");
    }

    public Integer getVentilatorBedsTotal() { return ventilatorBedsTotal; }
    public void setVentilatorBedsTotal(Integer ventilatorBedsTotal) { this.ventilatorBedsTotal = ventilatorBedsTotal; }

    public Integer getVentilatorBedsAvailable() { return ventilatorBedsAvailable; }
    public void setVentilatorBedsAvailable(Integer ventilatorBedsAvailable) { this.ventilatorBedsAvailable = ventilatorBedsAvailable; }

    public Integer getOxygenBedsTotal() { return oxygenBedsTotal; }
    public void setOxygenBedsTotal(Integer oxygenBedsTotal) { this.oxygenBedsTotal = oxygenBedsTotal; }

    public Integer getOxygenBedsAvailable() { return oxygenBedsAvailable; }
    public void setOxygenBedsAvailable(Integer oxygenBedsAvailable) { this.oxygenBedsAvailable = oxygenBedsAvailable; }

    public String getAbdmFacilityId() { return abdmFacilityId != null ? abdmFacilityId : ("ABDM-" + (id != null ? id : 100) + "-IN"); }
    public void setAbdmFacilityId(String abdmFacilityId) { this.abdmFacilityId = abdmFacilityId; }

    public String getEmergencyTraumaLevel() { return emergencyTraumaLevel; }
    public void setEmergencyTraumaLevel(String emergencyTraumaLevel) { this.emergencyTraumaLevel = emergencyTraumaLevel; }

    public boolean isNabhAccredited() { return nabhAccredited; }
    public void setNabhAccredited(boolean nabhAccredited) { this.nabhAccredited = nabhAccredited; }

    public Integer getAmbulanceCount() { return ambulanceCount; }
    public void setAmbulanceCount(Integer ambulanceCount) { this.ambulanceCount = ambulanceCount; }

    public boolean isNursingHome() {
        String t = (type != null ? type : "").toLowerCase();
        String n = (name != null ? name : "").toLowerCase();
        return t.contains("nursing") || n.contains("nursing") || 
               t.contains("day care") || n.contains("day care") || 
               t.contains("maternity care") || n.contains("maternity care");
    }

    public String getFacilityCategory() {
        if (isNursingHome()) return "Nursing Home";
        if (isGovernment()) return "Government Hospital";
        return "Private Hospital";
    }

    public String getSector() {
        if (isNursingHome()) return "Nursing Home";
        return isGovernment() ? "Government" : "Private";
    }
}
