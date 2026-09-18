package com.healthcare.platform.service;

import com.healthcare.platform.model.*;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class GeoService {

    private static final double EARTH_RADIUS_KM = 6371.0;

    // City Coordinates Directory for quick selection / fallbacks across India
    public static final Map<String, double[]> CITY_COORDINATES = new HashMap<>();

    static {
        CITY_COORDINATES.put("Budge Budge", new double[]{22.4820, 88.1812});
        CITY_COORDINATES.put("South 24 Parganas", new double[]{22.4820, 88.1812});
        CITY_COORDINATES.put("Kolkata", new double[]{22.5726, 88.3639});
        CITY_COORDINATES.put("Salt Lake", new double[]{22.5867, 88.4178});
        CITY_COORDINATES.put("New Town", new double[]{22.5958, 88.4795});
        CITY_COORDINATES.put("Howrah", new double[]{22.5958, 88.2636});
        CITY_COORDINATES.put("Siliguri", new double[]{26.7271, 88.3953});
        CITY_COORDINATES.put("Durgapur", new double[]{23.5204, 87.3119});
        CITY_COORDINATES.put("Asansol", new double[]{23.6889, 86.9661});
        CITY_COORDINATES.put("Kharagpur", new double[]{22.3460, 87.2320});
        CITY_COORDINATES.put("New Delhi", new double[]{28.6139, 77.2090});
        CITY_COORDINATES.put("Delhi", new double[]{28.6139, 77.2090});
        CITY_COORDINATES.put("Mumbai", new double[]{19.0760, 72.8777});
        CITY_COORDINATES.put("Bengaluru", new double[]{12.9716, 77.5946});
        CITY_COORDINATES.put("Bangalore", new double[]{12.9716, 77.5946});
        CITY_COORDINATES.put("Bhubaneswar", new double[]{20.2961, 85.8245});
        CITY_COORDINATES.put("Chennai", new double[]{13.0827, 80.2707});
        CITY_COORDINATES.put("Hyderabad", new double[]{17.3850, 78.4867});
        CITY_COORDINATES.put("Pune", new double[]{18.5204, 73.8567});
    }

    public static String resolveStateFromCity(String cityName) {
        if (cityName == null || cityName.isBlank()) {
            return "West Bengal";
        }
        String c = cityName.trim().toLowerCase();
        if (c.contains("delhi")) return "Delhi";
        if (c.contains("mumbai") || c.contains("pune") || c.contains("thane") || c.contains("navi mumbai")) return "Maharashtra";
        if (c.contains("bengaluru") || c.contains("bangalore") || c.contains("mysore") || c.contains("mysuru")) return "Karnataka";
        if (c.contains("bhubaneswar") || c.contains("cuttack") || c.contains("puri") || c.contains("rourkela")) return "Odisha";
        if (c.contains("chennai") || c.contains("coimbatore")) return "Tamil Nadu";
        if (c.contains("hyderabad") || c.contains("secunderabad")) return "Telangana";
        if (c.contains("kolkata") || c.contains("budge budge") || c.contains("howrah") || c.contains("salt lake")
                || c.contains("new town") || c.contains("siliguri") || c.contains("durgapur") || c.contains("asansol")
                || c.contains("kharagpur") || c.contains("24 parganas")) {
            return "West Bengal";
        }
        return "ALL";
    }

    public static double[] resolveCityCoordinates(String cityName) {
        if (cityName == null || cityName.isBlank()) {
            return CITY_COORDINATES.get("Budge Budge");
        }
        String clean = cityName.trim();
        for (Map.Entry<String, double[]> entry : CITY_COORDINATES.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(clean)) {
                return entry.getValue();
            }
        }
        // Substring / partial match
        for (Map.Entry<String, double[]> entry : CITY_COORDINATES.entrySet()) {
            if (clean.toLowerCase().contains(entry.getKey().toLowerCase()) || entry.getKey().toLowerCase().contains(clean.toLowerCase())) {
                return entry.getValue();
            }
        }
        return CITY_COORDINATES.get("Budge Budge");
    }

    /**
     * Calculates distance between two latitude/longitude points in kilometers using Haversine formula
     */
    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = EARTH_RADIUS_KM * c;
        return Math.round(distance * 10.0) / 10.0; // Round to 1 decimal place
    }

    /**
     * Estimates transit time in minutes given distance in km
     */
    public int estimateTransitMinutes(double distanceKm, boolean isEmergency) {
        // Average speed: 30 km/h for normal city traffic, 45 km/h for emergency vehicle with siren
        double speed = isEmergency ? 45.0 : 30.0;
        int minutes = (int) Math.round((distanceKm / speed) * 60.0);
        return Math.max(3, minutes); // Minimum 3 minutes
    }

    public List<Hospital> sortByProximityHospitals(List<Hospital> list, double userLat, double userLon) {
        if (list == null) return new ArrayList<>();
        List<Hospital> result = list;
        for (Hospital h : result) {
            if (h.getLatitude() != null && h.getLongitude() != null) {
                h.setDistanceKm(calculateDistance(userLat, userLon, h.getLatitude(), h.getLongitude()));
            } else {
                h.setDistanceKm(999.0);
            }
        }
        try {
            result.sort(Comparator.comparingDouble(Hospital::getDistanceKm));
        } catch (UnsupportedOperationException e) {
            result = new ArrayList<>(result);
            result.sort(Comparator.comparingDouble(Hospital::getDistanceKm));
        }
        return result;
    }

    public List<Doctor> sortByProximityDoctors(List<Doctor> list, double userLat, double userLon) {
        if (list == null) return new ArrayList<>();
        List<Doctor> result = list;
        for (Doctor d : result) {
            if (d.getLatitude() != null && d.getLongitude() != null) {
                d.setDistanceKm(calculateDistance(userLat, userLon, d.getLatitude(), d.getLongitude()));
            } else {
                d.setDistanceKm(999.0);
            }
        }
        try {
            result.sort(Comparator.comparingDouble(Doctor::getDistanceKm));
        } catch (UnsupportedOperationException e) {
            result = new ArrayList<>(result);
            result.sort(Comparator.comparingDouble(Doctor::getDistanceKm));
        }
        return result;
    }

    public List<BloodInventory> sortByProximityBlood(List<BloodInventory> list, double userLat, double userLon) {
        if (list == null) return new ArrayList<>();
        List<BloodInventory> result = list;
        for (BloodInventory b : result) {
            if (b.getLatitude() != null && b.getLongitude() != null) {
                b.setDistanceKm(calculateDistance(userLat, userLon, b.getLatitude(), b.getLongitude()));
            } else {
                b.setDistanceKm(999.0);
            }
        }
        try {
            result.sort(Comparator.comparingDouble(BloodInventory::getDistanceKm));
        } catch (UnsupportedOperationException e) {
            result = new ArrayList<>(result);
            result.sort(Comparator.comparingDouble(BloodInventory::getDistanceKm));
        }
        return result;
    }

    public List<LabTest> sortByProximityLabs(List<LabTest> list, double userLat, double userLon) {
        if (list == null) return new ArrayList<>();
        List<LabTest> result = list;
        for (LabTest l : result) {
            if (l.getLatitude() != null && l.getLongitude() != null) {
                l.setDistanceKm(calculateDistance(userLat, userLon, l.getLatitude(), l.getLongitude()));
            } else {
                l.setDistanceKm(999.0);
            }
        }
        try {
            result.sort(Comparator.comparingDouble(LabTest::getDistanceKm));
        } catch (UnsupportedOperationException e) {
            result = new ArrayList<>(result);
            result.sort(Comparator.comparingDouble(LabTest::getDistanceKm));
        }
        return result;
    }
}
