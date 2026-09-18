package com.healthcare.platform.controller;

import com.healthcare.platform.config.MongoConfig;
import com.healthcare.platform.repository.mongo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Controller
public class MongoDatabaseController {

    @Autowired private MongoTemplate mongoTemplate;
    @Autowired private HospitalMongoRepository hospitalMongoRepository;
    @Autowired private DoctorMongoRepository doctorMongoRepository;
    @Autowired private VaccinationRecordMongoRepository vaccinationMongoRepository;
    @Autowired private LabTestMongoRepository labTestMongoRepository;
    @Autowired private MedicalRecordMongoRepository medicalRecordMongoRepository;
    @Autowired private UserMongoRepository userMongoRepository;

    @GetMapping("/mongodb-viewer")
    public String viewDatabase() {
        return "redirect:/portals";
    }

    @GetMapping("/api/mongo/status")
    @ResponseBody
    public ResponseEntity<?> getStatus() {
        Map<String, Object> resp = new HashMap<>();
        resp.put("status", "CONNECTED");
        resp.put("database", "healthcaredb");
        resp.put("connectionUri", MongoConfig.getActiveConnectionString());
        resp.put("port", MongoConfig.getActivePort());
        resp.put("isExternalServer", MongoConfig.isExternalServer());

        Set<String> collections = mongoTemplate.getCollectionNames();
        resp.put("collections", collections);

        Map<String, Long> counts = new HashMap<>();
        counts.put("hospitals", hospitalMongoRepository.count());
        counts.put("doctors", doctorMongoRepository.count());
        counts.put("vaccinations", vaccinationMongoRepository.count());
        counts.put("lab_tests", labTestMongoRepository.count());
        counts.put("medical_records", medicalRecordMongoRepository.count());
        counts.put("users", userMongoRepository.count());
        resp.put("documentCounts", counts);

        return ResponseEntity.ok(resp);
    }

    @GetMapping("/api/mongo/collection/{name}")
    @ResponseBody
    public ResponseEntity<?> getCollectionDocs(@PathVariable String name) {
        switch (name.toLowerCase()) {
            case "hospitals":
                return ResponseEntity.ok(hospitalMongoRepository.findAll());
            case "doctors":
                return ResponseEntity.ok(doctorMongoRepository.findAll());
            case "vaccinations":
                return ResponseEntity.ok(vaccinationMongoRepository.findAll());
            case "lab_tests":
                return ResponseEntity.ok(labTestMongoRepository.findAll());
            case "medical_records":
                return ResponseEntity.ok(medicalRecordMongoRepository.findAll());
            case "users":
                return ResponseEntity.ok(userMongoRepository.findAll());
            default:
                return ResponseEntity.badRequest().body(Map.of("error", "Unknown collection: " + name));
        }
    }
}
