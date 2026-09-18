package com.healthcare.platform.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "doctors")
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String specialty; // Cardiology, Neurology, Pediatrics, Orthopedics, etc.

    private String subSpecialty;
    private String credentials; // MD, DM, MBBS, FRCP, etc.
    private Integer experienceYears;
    private String languages = "English, Hindi"; // Multilingual credentials
    private String consultationType = "BOTH"; // IN_PERSON, VIDEO, BOTH
    private Double consultationFee = 800.0;

    private String hospitalAffiliation;
    private String city;
    private String state = "West Bengal";
    private Double latitude;
    private Double longitude;

    private String availableDays = "Mon, Tue, Wed, Thu, Fri";
    private String availableHours = "10:00 AM - 02:00 PM, 05:00 PM - 08:00 PM";
    private boolean availableToday = true;

    // Outdoor (OPD) Visit & Weekly Roster Fields
    private String opdRoomNumber = "Room 102 (OPD Complex)";
    private String opdScheduleDays = "Mon, Tue, Wed, Thu, Fri";
    private String opdTimings = "09:30 AM - 01:30 PM";
    private Integer opdSeatsTotal = 30;
    private Integer opdSeatsAvailable = 22;
    private String opdDepartment = "General OPD";
    private Double opdFee = 20.0;

    // Doctor's Own Dedicated Private Chamber / Clinic Setup
    private String privateChamberName;
    private String privateChamberAddress;
    private String privateChamberCity;
    private String privateChamberDays = "Mon to Sat";
    private String privateChamberTimings = "05:30 PM - 08:30 PM";
    private Double privateChamberFee = 700.0;
    private String privateChamberPhone;

    private Double rating = 4.9;
    private Integer reviewCount = 180;
    private String profileImageUrl;

    @Transient
    private Double distanceKm;

    public Doctor() {}

    public Doctor(String name, String specialty, String subSpecialty, String credentials, Integer experienceYears, String languages, Double consultationFee, String hospitalAffiliation, String city, Double latitude, Double longitude) {
        this(name, specialty, subSpecialty, credentials, experienceYears, languages, consultationFee, hospitalAffiliation, city, "West Bengal", latitude, longitude);
    }

    public Doctor(String name, String specialty, String subSpecialty, String credentials, Integer experienceYears, String languages, Double consultationFee, String hospitalAffiliation, String city, String state, Double latitude, Double longitude) {
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

        // Smart distinct OPD attributes based on doctor name & sector
        int hash = Math.abs(name != null ? name.hashCode() : 1);
        this.opdRoomNumber = "Chamber " + (101 + (hash % 20));
        String[] dayPatterns = new String[] {
            "Mon to Sat",
            "Daily (Mon to Sun)",
            "Mon, Wed, Fri, Sat",
            "Mon to Fri",
            "Daily (Mon to Sun)"
        };
        this.opdScheduleDays = dayPatterns[hash % dayPatterns.length];
        String[] timingPatterns = new String[] {
            "09:00 AM - 10:30 AM",
            "10:30 AM - 12:00 PM",
            "12:00 PM - 01:30 PM",
            "04:00 PM - 05:30 PM",
            "05:30 PM - 07:00 PM"
        };
        this.opdTimings = timingPatterns[hash % timingPatterns.length];
        this.opdDepartment = (specialty != null ? specialty : "General Medicine") + " Outdoor";
        this.opdFee = isGovernment() ? 20.0 : Math.min(500.0, (consultationFee != null ? consultationFee : 500.0));
        this.opdSeatsTotal = 30;
        this.opdSeatsAvailable = 14 + (hash % 14);

        // Doctor's Own Dedicated Private Chamber / Clinic Details
        this.privateChamberName = name + "'s Clinic & Specialist Chamber";
        this.privateChamberAddress = (12 + (hash % 120)) + ", Main Road, Near " + (city != null ? city : "City") + " Station";
        this.privateChamberCity = city != null ? city : "Kolkata";
        this.privateChamberDays = (hash % 2 == 0) ? "Mon, Wed, Fri, Sat" : "Mon to Sat";
        this.privateChamberTimings = ((hash % 2 == 0) ? "05:30 PM - 08:30 PM" : "06:00 PM - 09:00 PM");
        this.privateChamberFee = consultationFee != null ? consultationFee : 700.0;
        this.privateChamberPhone = "+91 98300 " + (10000 + (hash % 90000));
    }

    public Doctor withOpdDetails(String room, String days, String timings, Double fee) {
        if (room != null) this.opdRoomNumber = room;
        if (days != null) this.opdScheduleDays = days;
        if (timings != null) this.opdTimings = timings;
        if (fee != null) this.opdFee = fee;
        return this;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public String getConsultationType() { return consultationType; }
    public void setConsultationType(String consultationType) { this.consultationType = consultationType; }

    public Double getConsultationFee() { return consultationFee; }
    public void setConsultationFee(Double consultationFee) { this.consultationFee = consultationFee; }

    public String getHospitalAffiliation() { return hospitalAffiliation; }
    public void setHospitalAffiliation(String hospitalAffiliation) { this.hospitalAffiliation = hospitalAffiliation; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public String getAvailableDays() { return availableDays; }
    public void setAvailableDays(String availableDays) { this.availableDays = availableDays; }

    public String getAvailableHours() { return availableHours; }
    public void setAvailableHours(String availableHours) { this.availableHours = availableHours; }

    public boolean isAvailableToday() { return availableToday; }
    public void setAvailableToday(boolean availableToday) { this.availableToday = availableToday; }

    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }

    public Integer getReviewCount() { return reviewCount; }
    public void setReviewCount(Integer reviewCount) { this.reviewCount = reviewCount; }

    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }

    public Double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(Double distanceKm) { this.distanceKm = distanceKm; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getQualification() { return credentials; }
    public void setQualification(String qualification) { this.credentials = qualification; }

    public boolean isGovernment() {
        String aff = (hospitalAffiliation != null ? hospitalAffiliation : "").toLowerCase();
        return aff.contains("govt") || aff.contains("government") || aff.contains("nims") ||
               aff.contains("osmania") || aff.contains("gandhi") || aff.contains("aiims") ||
               aff.contains("esi ") || aff.contains("esi hospital") || aff.contains("sskm") ||
               aff.contains("medical college") || aff.contains("mnj");
    }

    public String getSector() {
        return isGovernment() ? "Government" : "Private";
    }

    public String getOpdRoomNumber() { return opdRoomNumber; }
    public void setOpdRoomNumber(String opdRoomNumber) { this.opdRoomNumber = opdRoomNumber; }

    public String getOpdScheduleDays() { return opdScheduleDays; }
    public void setOpdScheduleDays(String opdScheduleDays) { this.opdScheduleDays = opdScheduleDays; }

    public String getOpdTimings() { return opdTimings; }
    public void setOpdTimings(String opdTimings) { this.opdTimings = opdTimings; }

    public Integer getOpdSeatsTotal() { return opdSeatsTotal; }
    public void setOpdSeatsTotal(Integer opdSeatsTotal) { this.opdSeatsTotal = opdSeatsTotal; }

    public Integer getOpdSeatsAvailable() { return opdSeatsAvailable; }
    public void setOpdSeatsAvailable(Integer opdSeatsAvailable) { this.opdSeatsAvailable = opdSeatsAvailable; }

    public String getOpdDepartment() { return opdDepartment; }
    public void setOpdDepartment(String opdDepartment) { this.opdDepartment = opdDepartment; }

    public Double getOpdFee() { return opdFee; }
    public void setOpdFee(Double opdFee) { this.opdFee = opdFee; }

    public boolean isAvailableOnDay(String dayName) {
        if (dayName == null || dayName.trim().isEmpty() || "All".equalsIgnoreCase(dayName.trim())) {
            return true;
        }
        String target = dayName.trim().toLowerCase();
        String days = (opdScheduleDays != null && !opdScheduleDays.trim().isEmpty()) 
                ? opdScheduleDays.toLowerCase() 
                : (availableDays != null ? availableDays.toLowerCase() : "mon, tue, wed, thu, fri");

        if (days.contains("daily") || days.contains("all") || days.contains("mon to sun") || days.contains("mon-sun")) {
            return true;
        }
        if (days.contains("mon to sat") || days.contains("mon-sat") || days.contains("mon - sat")) {
            return !target.startsWith("sun");
        }
        String prefix = target.substring(0, Math.min(3, target.length()));
        return days.contains(prefix);
    }

    public List<String> getAvailableTimeSlots() {
        if (privateChamberTimings != null && privateChamberTimings.contains("06:00 PM")) {
            return List.of("06:00 PM", "06:20 PM", "06:40 PM", "07:00 PM", "07:20 PM", "07:40 PM", "08:00 PM", "08:20 PM", "08:40 PM");
        } else {
            return List.of("05:30 PM", "05:50 PM", "06:10 PM", "06:30 PM", "06:50 PM", "07:10 PM", "07:30 PM", "07:50 PM", "08:10 PM");
        }
    }

    public String getAvailableTimeSlotsJoined() {
        return String.join(",", getAvailableTimeSlots());
    }

    public boolean isNursingHome() {
        if (hospitalAffiliation == null) return false;
        String ha = hospitalAffiliation.toLowerCase();
        return ha.contains("nursing") || ha.contains("day care") || ha.contains("maternity");
    }

    public String getHospitalCategory() {
        if (isNursingHome()) return "Nursing Home";
        if (isGovernment()) return "Government Hospital";
        return "Private Hospital";
    }

    public String getPrivateChamberName() {
        return privateChamberName != null ? privateChamberName : (name + "'s Specialist Chamber");
    }
    public void setPrivateChamberName(String privateChamberName) { this.privateChamberName = privateChamberName; }

    public String getPrivateChamberAddress() {
        return privateChamberAddress != null ? privateChamberAddress : ("Chamber Complex, Main Road, " + (city != null ? city : "Kolkata"));
    }
    public void setPrivateChamberAddress(String privateChamberAddress) { this.privateChamberAddress = privateChamberAddress; }

    public String getPrivateChamberCity() { return privateChamberCity != null ? privateChamberCity : city; }
    public void setPrivateChamberCity(String privateChamberCity) { this.privateChamberCity = privateChamberCity; }

    public String getPrivateChamberDays() { return privateChamberDays != null ? privateChamberDays : "Mon to Sat"; }
    public void setPrivateChamberDays(String privateChamberDays) { this.privateChamberDays = privateChamberDays; }

    public String getPrivateChamberTimings() { return privateChamberTimings != null ? privateChamberTimings : "05:30 PM - 08:30 PM"; }
    public void setPrivateChamberTimings(String privateChamberTimings) { this.privateChamberTimings = privateChamberTimings; }

    public Double getPrivateChamberFee() { return privateChamberFee != null ? privateChamberFee : (consultationFee != null ? consultationFee : 700.0); }
    public void setPrivateChamberFee(Double privateChamberFee) { this.privateChamberFee = privateChamberFee; }

    public String getPrivateChamberPhone() { return privateChamberPhone != null ? privateChamberPhone : "+91 98300 12345"; }
    public void setPrivateChamberPhone(String privateChamberPhone) { this.privateChamberPhone = privateChamberPhone; }
}
