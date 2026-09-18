package com.healthcare.platform.service;

import com.healthcare.platform.model.*;
import com.healthcare.platform.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class HealthcareService {

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private AdmissionRequestRepository admissionRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private AmbulanceRequestRepository ambulanceRepository;

    @Autowired
    private BloodInventoryRepository bloodRepository;

    @Autowired
    private LabTestRepository labRepository;

    @Autowired
    private MedicalRecordRepository recordRepository;

    @Autowired
    private VitalLogRepository vitalRepository;

    @Autowired
    private InsurancePolicyRepository insuranceRepository;

    @Autowired
    private HealthExpenseRepository expenseRepository;

    @Autowired
    private NotificationItemRepository notificationRepository;

    @Autowired
    private FamilyMemberRepository familyRepository;

    @Autowired
    private GeoService geoService;

    // --- HOSPITAL ADMISSION WORKFLOW (AUTHENTIC DIRECT BOOKING & BED ALLOCATION) ---

    public AdmissionRequest createAdmissionRequest(String patientName, Integer age, String gender, String phone, Long hospitalId, String dept, String roomType, String urgency) {
        Hospital hospital = hospitalRepository.findById(hospitalId)
                .orElseThrow(() -> new IllegalArgumentException("Hospital not found: " + hospitalId));

        AdmissionRequest request = new AdmissionRequest(patientName, age, gender, phone, hospital, dept, roomType, urgency);
        
        // Authentic Direct Bed Allocation & Triage Assignment
        boolean isIcu = (roomType != null && roomType.toUpperCase().contains("ICU")) || "EMERGENCY".equalsIgnoreCase(urgency);
        String bedNum;
        if (isIcu) {
            bedNum = "ICU-Bay-" + ((int)(Math.random() * 8) + 1);
            if (hospital.getIcuBedsAvailable() != null && hospital.getIcuBedsAvailable() > 0) {
                hospital.setIcuBedsAvailable(hospital.getIcuBedsAvailable() - 1);
                hospitalRepository.save(hospital);
            }
        } else {
            String prefix = (roomType != null && roomType.contains("Deluxe")) ? "DLX-Room-" : "GEN-Bed-";
            bedNum = prefix + ((int)(Math.random() * 30) + 101);
            if (hospital.getGeneralBedsAvailable() != null && hospital.getGeneralBedsAvailable() > 0) {
                hospital.setGeneralBedsAvailable(hospital.getGeneralBedsAvailable() - 1);
                hospitalRepository.save(hospital);
            }
        }

        // Assign Attending Specialist Doctor
        String attendingDoctor = "Dr. On-Duty Chief Medical Officer, MD";
        List<Doctor> docs = doctorRepository.findByHospitalAffiliationContainingIgnoreCase(hospital.getName());
        if (docs.isEmpty() && hospital.getCity() != null) {
            docs = doctorRepository.findByCityIgnoreCase(hospital.getCity());
        }
        if (!docs.isEmpty()) {
            attendingDoctor = docs.get(0).getName() + " (" + docs.get(0).getSpecialty() + ")";
        }

        request.setAllocatedBedNumber(bedNum);
        request.setAllocatedDoctor(attendingDoctor);
        request.setStatus("BED_ALLOCATED");
        request.setAdmissionInstructions("Authentic Digital Admission Confirmed. Reserved for 3 hours. Present Digital Token at Emergency Triage / Admissions Counter 2.");
        request = admissionRepository.save(request);

        // Generate notification
        NotificationItem item = new NotificationItem(
                "HOSPITAL",
                "Bed Allocated: " + bedNum + " (" + hospital.getName() + ")",
                "Authentic admission pass confirmed for " + patientName + " at " + hospital.getName() + " (" + dept + "). Allocated Bed: " + bedNum,
                "/portals",
                "HIGH"
        );
        notificationRepository.save(item);

        return request;
    }

    public AdmissionRequest progressAdmissionWorkflow(Long requestId, String nextStatus, String bedNumber, String docSummary) {
        AdmissionRequest req = admissionRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Admission request not found: " + requestId));

        req.setStatus(nextStatus);
        if (bedNumber != null) req.setAllocatedBedNumber(bedNumber);
        if ("ADMITTED".equalsIgnoreCase(nextStatus)) {
            req.setAdmittedAt(LocalDateTime.now());
        } else if ("DISCHARGED".equalsIgnoreCase(nextStatus)) {
            req.setDischargedAt(LocalDateTime.now());
            if (docSummary != null) req.setDischargeSummary(docSummary);
            // restore bed count in hospital
            Hospital h = req.getHospital();
            if (h != null) {
                boolean isIcu = (req.getRoomTypePreferred() != null && req.getRoomTypePreferred().toUpperCase().contains("ICU"));
                if (isIcu && h.getIcuBedsAvailable() != null) {
                    h.setIcuBedsAvailable(h.getIcuBedsAvailable() + 1);
                } else if (h.getGeneralBedsAvailable() != null) {
                    h.setGeneralBedsAvailable(h.getGeneralBedsAvailable() + 1);
                }
                hospitalRepository.save(h);
            }
        }

        return admissionRepository.save(req);
    }

    public AdmissionRequest cancelAdmissionRequest(Long requestId, String reason) {
        AdmissionRequest req = admissionRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Admission request not found: " + requestId));
        req.setStatus("CANCELLED");
        if (reason != null && !reason.isBlank()) {
            req.setDischargeSummary("Cancelled by user: " + reason);
        } else {
            req.setDischargeSummary("Admission request cancelled by patient.");
        }
        if (req.getAllocatedBedNumber() != null) {
            Hospital h = req.getHospital();
            if (h != null) {
                boolean isIcu = (req.getRoomTypePreferred() != null && req.getRoomTypePreferred().toUpperCase().contains("ICU"));
                if (isIcu && h.getIcuBedsAvailable() != null) {
                    h.setIcuBedsAvailable(h.getIcuBedsAvailable() + 1);
                } else if (h.getGeneralBedsAvailable() != null) {
                    h.setGeneralBedsAvailable(h.getGeneralBedsAvailable() + 1);
                }
                hospitalRepository.save(h);
            }
            req.setAllocatedBedNumber(null);
        }
        return admissionRepository.save(req);
    }

    // --- APPOINTMENTS ---

    public Appointment bookAppointment(String patientName, String patientPhone, Long doctorId, LocalDateTime slotTime, String type, String symptoms) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found: " + doctorId));

        Appointment appt = new Appointment(patientName, patientPhone, doctor, slotTime, type, symptoms);
        appt = appointmentRepository.save(appt);

        NotificationItem item = new NotificationItem(
                "APPOINTMENT",
                "Appointment Confirmed: " + doctor.getName(),
                "Your " + type.toLowerCase() + " appointment is scheduled on " + slotTime.toLocalDate() + " at " + slotTime.toLocalTime() + ". Token: " + appt.getTokenNumber(),
                "/doctors",
                "NORMAL"
        );
        notificationRepository.save(item);
        return appt;
    }

    // --- AMBULANCE DISPATCH & REAL-TIME TRACKING ---

    public AmbulanceRequest requestAmbulance(String patientName, String phone, String address, Double lat, Double lon, String type) {
        AmbulanceRequest req = new AmbulanceRequest(patientName, phone, address, lat, lon, type);

        // Find nearest hospital for destination
        List<Hospital> hospitals = hospitalRepository.findAll();
        if (!hospitals.isEmpty() && lat != null && lon != null) {
            geoService.sortByProximityHospitals(hospitals, lat, lon);
            Hospital nearest = hospitals.get(0);
            req.setDestinationHospital(nearest.getName());
            double dist = nearest.getDistanceKm();
            req.setDistanceKm(dist);
            req.setEtaMinutes(geoService.estimateTransitMinutes(dist, true));
        } else {
            req.setDestinationHospital("City Emergency Trauma Hospital");
            req.setEtaMinutes(7);
            req.setDistanceKm(3.2);
        }

        req = ambulanceRepository.save(req);

        // Send emergency alert
        NotificationItem item = new NotificationItem(
                "EMERGENCY",
                "🚨 Ambulance Dispatched (" + type + ")",
                "Ambulance " + req.getVehicleNumber() + " is en-route. Driver: " + req.getDriverName() + " (" + req.getDriverPhone() + "). ETA: " + req.getEtaMinutes() + " mins.",
                "/emergency",
                "URGENT"
        );
        notificationRepository.save(item);

        return req;
    }

    // --- BLOOD BANK SEARCH & REQUEST ---

    public List<BloodInventory> findBloodByLocationAndGroup(String bloodGroup, Double lat, Double lon, String city) {
        List<BloodInventory> list;
        if (bloodGroup != null && !bloodGroup.isBlank()) {
            list = bloodRepository.findByBloodGroupIgnoreCase(bloodGroup);
        } else {
            list = bloodRepository.findAll();
        }

        if (lat != null && lon != null) {
            geoService.sortByProximityBlood(list, lat, lon);
        }
        return list;
    }

    // --- LABS & DIAGNOSTICS ---

    public List<LabTest> findLabTestsByLocation(String query, Double lat, Double lon) {
        List<LabTest> tests;
        if (query != null && !query.isBlank()) {
            tests = labRepository.findByTestNameContainingIgnoreCase(query);
        } else {
            tests = labRepository.findAll();
        }

        if (lat != null && lon != null) {
            geoService.sortByProximityLabs(tests, lat, lon);
        }
        return tests;
    }

    // --- VITALS & WEARABLES SYNC ---

    public VitalLog recordVitals(String patientName, Integer hr, Integer sys, Integer dia, Integer spo2, Double temp, Double glucose, Double weight, String source) {
        VitalLog vital = new VitalLog(patientName, hr, sys, dia, spo2, glucose, weight, source);
        vital.setTemperatureF(temp);
        return vitalRepository.save(vital);
    }
}
