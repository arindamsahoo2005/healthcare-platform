package com.healthcare.platform.model.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "doctors")
public class DoctorDocument {

    @Id
    private String id;
    private String name;
    private String specialty;
    private String subSpecialty;
    private String credentials;
    private Integer experienceYears;
    private String languages;
    private Double consultationFee;
    private String hospitalAffiliation;
    private String city;
    private String state;
    private Double latitude;
    private Double longitude;
    private String availableDays;
    private Double rating;

    public DoctorDocument() {}

    public DoctorDocument(String name, String specialty, String subSpecialty, String credentials,
                          Integer experienceYears, String languages, Double consultationFee,
                          String hospitalAffiliation, String city, String state,
                          Double latitude, Double longitude) {
        this.name = name;
        this.specialty = specialty;
        this.subSpecialty = subSpecialty;
        this.credentials = credentials;
        this.experienceYears = experienceYears;
        this.languages = languages;
        this.consultationFee = consultationFee;
        this.hospitalAffiliation = hospitalAffiliation;
        this.city = city;
        this.state = state != null ? state : "West Bengal";
        this.latitude = latitude;
        this.longitude = longitude;
        this.availableDays = "Mon, Tue, Wed, Thu, Fri";
        this.rating = 4.9;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSpecialty() { return specialty; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }

    public String getSubSpecialty() { return subSpecialty; }
    public void setSubSpecialty(String subSpecialty) { this.subSpecialty = subSpecialty; }

    public String getCredentials() { return credentials; }
    public void setCredentials(String credentials) { this.credentials = credentials; }

    public Integer getExperienceYears() { return experienceYears; }
    public void setExperienceYears(Integer experienceYears) { this.experienceYears = experienceYears; }

    public String getLanguages() { return languages; }
    public void setLanguages(String languages) { this.languages = languages; }

    public Double getConsultationFee() { return consultationFee; }
    public void setConsultationFee(Double consultationFee) { this.consultationFee = consultationFee; }

    public String getHospitalAffiliation() { return hospitalAffiliation; }
    public void setHospitalAffiliation(String hospitalAffiliation) { this.hospitalAffiliation = hospitalAffiliation; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public String getAvailableDays() { return availableDays; }
    public void setAvailableDays(String availableDays) { this.availableDays = availableDays; }

    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }
}
