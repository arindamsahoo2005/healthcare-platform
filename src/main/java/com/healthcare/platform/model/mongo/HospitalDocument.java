package com.healthcare.platform.model.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "hospitals")
public class HospitalDocument {

    @Id
    private String id;
    private String name;
    private String type;
    private String address;
    private String city;
    private String state;
    private String postalCode;
    private Double latitude;
    private Double longitude;
    private String phone;
    private String emergencyHelpline;
    private String email;
    private Integer icuBedsTotal;
    private Integer icuBedsAvailable;
    private Integer generalBedsTotal;
    private Integer generalBedsAvailable;
    private Integer ventilatorBedsTotal = 15;
    private Integer ventilatorBedsAvailable = 5;
    private String abdmFacilityId;
    private String emergencyTraumaLevel = "Level 1 Trauma & Resuscitation Center";
    private Double rating;
    private String departments;

    public HospitalDocument() {}

    public HospitalDocument(String name, String type, String address, String city, String state,
                            Double latitude, Double longitude, String phone, String emergencyHelpline,
                            Integer icuBedsTotal, Integer icuBedsAvailable, Integer generalBedsTotal, Integer generalBedsAvailable) {
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
        this.rating = 4.8;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

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

    public Integer getVentilatorBedsTotal() { return ventilatorBedsTotal; }
    public void setVentilatorBedsTotal(Integer ventilatorBedsTotal) { this.ventilatorBedsTotal = ventilatorBedsTotal; }

    public Integer getVentilatorBedsAvailable() { return ventilatorBedsAvailable; }
    public void setVentilatorBedsAvailable(Integer ventilatorBedsAvailable) { this.ventilatorBedsAvailable = ventilatorBedsAvailable; }

    public String getAbdmFacilityId() { return abdmFacilityId != null ? abdmFacilityId : ("ABDM-" + (id != null ? id : "FAC101")); }
    public void setAbdmFacilityId(String abdmFacilityId) { this.abdmFacilityId = abdmFacilityId; }

    public String getEmergencyTraumaLevel() { return emergencyTraumaLevel; }
    public void setEmergencyTraumaLevel(String emergencyTraumaLevel) { this.emergencyTraumaLevel = emergencyTraumaLevel; }

    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }

    public String getDepartments() { return departments; }
    public void setDepartments(String departments) { this.departments = departments; }
}
