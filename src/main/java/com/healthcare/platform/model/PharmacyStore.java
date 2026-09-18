package com.healthcare.platform.model;

public class PharmacyStore {

    private String id;
    private String name;
    private String type; // Jan Aushadhi Kendra, Retail Pharmacy Chain, Hospital Dispensary, Heritage Chemist
    private String address;
    private String city;
    private String state;
    private Double latitude;
    private Double longitude;
    private String phone;
    private String openHours = "24x7 Open";
    private boolean emergencyOxygenAvailable = true;
    private boolean homeDeliveryAvailable = true;
    private Integer deliveryTimeMin = 20;
    private Integer discountPercent = 15;
    private boolean janAushadhi = false;
    private boolean verifiedByApiKey = true;
    private Double rating = 4.8;
    private Double distanceKm;

    public PharmacyStore() {}

    public PharmacyStore(String id, String name, String type, String address, String city, String state,
                         Double latitude, Double longitude, String phone, Integer deliveryTimeMin,
                         boolean emergencyOxygenAvailable, boolean janAushadhi, Integer discountPercent) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.address = address;
        this.city = city;
        this.state = state;
        this.latitude = latitude;
        this.longitude = longitude;
        this.phone = phone;
        this.deliveryTimeMin = deliveryTimeMin;
        this.emergencyOxygenAvailable = emergencyOxygenAvailable;
        this.janAushadhi = janAushadhi;
        this.discountPercent = discountPercent;
    }

    // Getters and Setters
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

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getOpenHours() { return openHours; }
    public void setOpenHours(String openHours) { this.openHours = openHours; }

    public boolean isEmergencyOxygenAvailable() { return emergencyOxygenAvailable; }
    public void setEmergencyOxygenAvailable(boolean emergencyOxygenAvailable) { this.emergencyOxygenAvailable = emergencyOxygenAvailable; }

    public boolean isHomeDeliveryAvailable() { return homeDeliveryAvailable; }
    public void setHomeDeliveryAvailable(boolean homeDeliveryAvailable) { this.homeDeliveryAvailable = homeDeliveryAvailable; }

    public Integer getDeliveryTimeMin() { return deliveryTimeMin; }
    public void setDeliveryTimeMin(Integer deliveryTimeMin) { this.deliveryTimeMin = deliveryTimeMin; }

    public Integer getDiscountPercent() { return discountPercent; }
    public void setDiscountPercent(Integer discountPercent) { this.discountPercent = discountPercent; }

    public boolean isJanAushadhi() { return janAushadhi; }
    public void setJanAushadhi(boolean janAushadhi) { this.janAushadhi = janAushadhi; }

    public boolean isVerifiedByApiKey() { return verifiedByApiKey; }
    public void setVerifiedByApiKey(boolean verifiedByApiKey) { this.verifiedByApiKey = verifiedByApiKey; }

    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }

    public Double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(Double distanceKm) { this.distanceKm = distanceKm; }
}
