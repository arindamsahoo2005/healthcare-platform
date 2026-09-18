package com.healthcare.platform.model;

import jakarta.persistence.*;

@Entity
@Table(name = "lab_tests")
public class LabTest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String testName;

    private String category; // Blood, Imaging, Pathology, Cardiac
    private String centerName;
    private String address;
    private String city;

    private Double latitude;
    private Double longitude;
    private String phone;

    private Double price;
    private boolean homeCollectionAvailable = true;
    private boolean fastingRequired = false;
    private Integer turnaroundHours = 24;
    private String sampleType = "Blood";
    private String state;
    private String preparationInstructions;

    @Transient
    private Double distanceKm;

    public LabTest() {}

    public LabTest(String testName, String category, String centerName, String city, Double latitude, Double longitude, Double price, boolean homeCollectionAvailable, boolean fastingRequired, Integer turnaroundHours, String preparationInstructions) {
        this.testName = testName;
        this.category = category;
        this.centerName = centerName;
        this.city = city;
        this.latitude = latitude;
        this.longitude = longitude;
        this.price = price;
        this.homeCollectionAvailable = homeCollectionAvailable;
        this.fastingRequired = fastingRequired;
        this.turnaroundHours = turnaroundHours;
        this.preparationInstructions = preparationInstructions;
    }

    public LabTest(String testName, String category, String centerName, String city, String state, Double latitude, Double longitude, Double price, boolean homeCollectionAvailable, boolean fastingRequired, Integer turnaroundHours, String preparationInstructions) {
        this(testName, category, centerName, city, latitude, longitude, price, homeCollectionAvailable, fastingRequired, turnaroundHours, preparationInstructions);
        this.state = state;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTestName() { return testName; }
    public void setTestName(String testName) { this.testName = testName; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getCenterName() { return centerName; }
    public void setCenterName(String centerName) { this.centerName = centerName; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public boolean isHomeCollectionAvailable() { return homeCollectionAvailable; }
    public void setHomeCollectionAvailable(boolean homeCollectionAvailable) { this.homeCollectionAvailable = homeCollectionAvailable; }

    public boolean isFastingRequired() { return fastingRequired; }
    public void setFastingRequired(boolean fastingRequired) { this.fastingRequired = fastingRequired; }

    public Integer getTurnaroundHours() { return turnaroundHours; }
    public void setTurnaroundHours(Integer turnaroundHours) { this.turnaroundHours = turnaroundHours; }

    public String getSampleType() { return sampleType; }
    public void setSampleType(String sampleType) { this.sampleType = sampleType; }

    public String getPreparationInstructions() { return preparationInstructions; }
    public void setPreparationInstructions(String preparationInstructions) { this.preparationInstructions = preparationInstructions; }

    public Double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(Double distanceKm) { this.distanceKm = distanceKm; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public boolean isGovernment() {
        String c = (centerName != null ? centerName : "").toLowerCase();
        String t = (testName != null ? testName : "").toLowerCase();
        return c.contains("govt") || c.contains("government") || c.contains("nims") ||
               c.contains("osmania") || c.contains("gandhi") || c.contains("rajiv gandhi") ||
               c.contains("stanley") || c.contains("aiims") || c.contains("kem") ||
               c.contains("safdarjung") || c.contains("victoria");
    }

    public String getSector() {
        return isGovernment() ? "Government" : "Private";
    }
}
