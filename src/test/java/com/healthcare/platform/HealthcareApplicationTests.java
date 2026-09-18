package com.healthcare.platform;

import com.healthcare.platform.model.Hospital;
import com.healthcare.platform.service.AiHealthService;
import com.healthcare.platform.service.GeoService;
import com.healthcare.platform.service.MedicineTimerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class HealthcareApplicationTests {

    @Autowired
    private GeoService geoService;

    @Autowired
    private MedicineTimerService medicineTimerService;

    @Autowired
    private AiHealthService aiHealthService;

    @Autowired
    private com.healthcare.platform.repository.MedicineRepository medicineRepository;

    @Autowired
    private com.healthcare.platform.repository.MedicineScheduleRepository scheduleRepository;

    @Autowired
    private com.healthcare.platform.repository.mongo.MedicalRecordMongoRepository mongoRecordRepository;

    @Test
    void contextLoads() {
        assertNotNull(geoService);
        assertNotNull(medicineTimerService);
        assertNotNull(aiHealthService);
    }

    @Test
    void testHaversineDistanceCalculation() {
        // Distance between Bhubaneswar (20.2961, 85.8245) and AIIMS Bhubaneswar (20.2444, 85.7766)
        double dist = geoService.calculateDistance(20.2961, 85.8245, 20.2444, 85.7766);
        assertTrue(dist > 0 && dist < 20, "Distance should be approximately 7-9 km");
    }

    @Test
    void testProximitySorting() {
        List<Hospital> hospitals = new ArrayList<>();
        Hospital h1 = new Hospital();
        h1.setName("Far Hospital");
        h1.setLatitude(28.6139); // Delhi
        h1.setLongitude(77.2090);

        Hospital h2 = new Hospital();
        h2.setName("Close Hospital");
        h2.setLatitude(20.2444); // Near Bhubaneswar
        h2.setLongitude(85.7766);

        hospitals.add(h1);
        hospitals.add(h2);

        geoService.sortByProximityHospitals(hospitals, 20.2961, 85.8245);
        assertEquals("Close Hospital", hospitals.get(0).getName());
    }

    @Test
    void testMedicineTimerNextDose() {
        var med = new com.healthcare.platform.model.Medicine("Metformin Test", "Metformin HCl", "500mg", "Tablet", "AFTER_FOOD", 30, 30, "Oral antidiabetic agent.");
        med = medicineRepository.save(med);
        var sched = new com.healthcare.platform.model.MedicineSchedule(med, "MORNING", java.time.LocalTime.now().plusHours(1), "1 tablet", "Take after food");
        sched = scheduleRepository.save(sched);

        try {
            MedicineTimerService.NextDoseInfo nextDose = medicineTimerService.getNextDose();
            assertNotNull(nextDose, "Next dose info should be populated when active schedule exists");
            assertNotNull(nextDose.getMedicineName());
            assertNotNull(nextDose.getFoodRelation());
            assertTrue(nextDose.getSecondsRemaining() >= 0);
        } finally {
            scheduleRepository.delete(sched);
            medicineRepository.delete(med);
        }
    }

    @Test
    void testAiHealthEmergencyRedFlag() {
        AiHealthService.AiResponse response = aiHealthService.processHealthQuery("I have acute severe chest pain and left arm numbness", "en");
        assertTrue(response.isEmergencyRedFlag(), "Acute chest pain should trigger emergency red flag");
        assertTrue(response.getRecommendedAction().contains("108"));
    }

    @Autowired
    private com.healthcare.platform.repository.VaccinationRecordRepository vaccinationRecordRepository;

    @Test
    void testAiAskMyRecords() {
        var doc = new com.healthcare.platform.model.mongo.MedicalRecordDocument(
                "test-uid", "Test Patient", "BLOOD_TEST", "Complete Blood Count Test",
                "Apollo Diagnostics", "Dr. A. Sen", java.time.LocalDate.now(),
                "Fasting glucose: 95 mg/dL. Normal.", "All blood counts within normal limit.",
                "test-cbc.pdf", "PDF", "1.1 MB"
        );
        doc = mongoRecordRepository.save(doc);

        try {
            AiHealthService.AiResponse response = aiHealthService.answerFromMedicalRecords("When was my last blood test?", "en");
            assertNotNull(response.getAnswer());
            assertFalse(response.getSourceDocuments().isEmpty(), "Should cite authorized blood test report from MongoDB");
        } finally {
            mongoRecordRepository.delete(doc);
        }
    }

    @Autowired
    private com.healthcare.platform.repository.mongo.UserMongoRepository userMongoRepository;

    @Test
    void testMongoUserProfilePersistence() {
        var userDoc = new com.healthcare.platform.model.mongo.UserDocument(
                "test-google-uid-123", "arindam.test", "Arindam Sahoo",
                "arindam.test@gmail.com", "+91 98300 12345"
        );
        userDoc.setState("West Bengal");
        userDoc.setCity("Kolkata");
        userDoc.setLocality("Salt Lake Sector V");
        userDoc.setBloodGroup("O+");
        userDoc = userMongoRepository.save(userDoc);

        assertNotNull(userDoc.getId());
        try {
            var found = userMongoRepository.findByEmail("arindam.test@gmail.com");
            assertTrue(found.isPresent());
            assertEquals("West Bengal", found.get().getState());
            assertEquals("Kolkata", found.get().getCity());
            assertEquals("Salt Lake Sector V", found.get().getLocality());
            assertEquals("O+", found.get().getBloodGroup());
        } finally {
            userMongoRepository.delete(userDoc);
        }
    }
}
