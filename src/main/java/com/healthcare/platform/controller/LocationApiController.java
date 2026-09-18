package com.healthcare.platform.controller;

import com.healthcare.platform.model.*;
import com.healthcare.platform.repository.*;
import com.healthcare.platform.service.GeoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/location")
public class LocationApiController {

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private BloodInventoryRepository bloodRepository;

    @Autowired
    private LabTestRepository labRepository;

    @Autowired
    private GeoService geoService;

    @Autowired
    private com.healthcare.platform.service.PharmacyRegistryService pharmacyRegistryService;

    @GetMapping("/pharmacies")
    public ResponseEntity<List<com.healthcare.platform.model.PharmacyStore>> getNearbyPharmacies(
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lon,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String apiKey) {
        List<com.healthcare.platform.model.PharmacyStore> list = pharmacyRegistryService.getNearbyPharmacies(city, lat, lon, apiKey);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/hospitals")
    public ResponseEntity<List<Hospital>> getNearbyHospitals(
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lon,
            @RequestParam(required = false) String city,
            @RequestParam(defaultValue = "false") boolean emergencyOnly) {

        List<Hospital> list;
        if (emergencyOnly) {
            list = hospitalRepository.findByEmergencyDeptTrue();
        } else if (city != null && !city.isBlank()) {
            list = hospitalRepository.findByCityIgnoreCase(city);
            if (list.isEmpty()) list = hospitalRepository.findAll();
        } else {
            list = hospitalRepository.findAll();
        }

        // Apply coordinates from user or fallback to chosen city coordinates
        double targetLat = lat != null ? lat : (city != null && GeoService.CITY_COORDINATES.containsKey(city) ? GeoService.CITY_COORDINATES.get(city)[0] : 20.2961);
        double targetLon = lon != null ? lon : (city != null && GeoService.CITY_COORDINATES.containsKey(city) ? GeoService.CITY_COORDINATES.get(city)[1] : 85.8245);

        geoService.sortByProximityHospitals(list, targetLat, targetLon);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/doctors")
    public ResponseEntity<List<Doctor>> getNearbyDoctors(
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lon,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String specialty) {

        List<Doctor> list;
        if (specialty != null && !specialty.isBlank()) {
            list = doctorRepository.findBySpecialtyIgnoreCase(specialty);
            if (list.isEmpty()) list = doctorRepository.findAll();
        } else if (city != null && !city.isBlank()) {
            list = doctorRepository.findByCityIgnoreCase(city);
            if (list.isEmpty()) list = doctorRepository.findAll();
        } else {
            list = doctorRepository.findAll();
        }

        double targetLat = lat != null ? lat : (city != null && GeoService.CITY_COORDINATES.containsKey(city) ? GeoService.CITY_COORDINATES.get(city)[0] : 20.2961);
        double targetLon = lon != null ? lon : (city != null && GeoService.CITY_COORDINATES.containsKey(city) ? GeoService.CITY_COORDINATES.get(city)[1] : 85.8245);

        geoService.sortByProximityDoctors(list, targetLat, targetLon);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/blood-banks")
    public ResponseEntity<List<BloodInventory>> getNearbyBloodBanks(
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lon,
            @RequestParam(required = false) String bloodGroup) {

        List<BloodInventory> list;
        if (bloodGroup != null && !bloodGroup.isBlank()) {
            list = bloodRepository.findByBloodGroupIgnoreCase(bloodGroup);
        } else {
            list = bloodRepository.findAll();
        }

        double targetLat = lat != null ? lat : 20.2961;
        double targetLon = lon != null ? lon : 85.8245;

        geoService.sortByProximityBlood(list, targetLat, targetLon);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/labs")
    public ResponseEntity<List<LabTest>> getNearbyLabs(
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lon,
            @RequestParam(required = false) String query) {

        List<LabTest> list;
        if (query != null && !query.isBlank()) {
            list = labRepository.findByTestNameContainingIgnoreCase(query);
        } else {
            list = labRepository.findAll();
        }

        double targetLat = lat != null ? lat : 20.2961;
        double targetLon = lon != null ? lon : 85.8245;

        geoService.sortByProximityLabs(list, targetLat, targetLon);
        return ResponseEntity.ok(list);
    }
}
