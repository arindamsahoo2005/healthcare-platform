package com.healthcare.platform.controller;

import com.healthcare.platform.model.*;
import com.healthcare.platform.model.mongo.MedicalRecordDocument;
import com.healthcare.platform.model.mongo.UserDocument;
import com.healthcare.platform.repository.*;
import com.healthcare.platform.repository.mongo.MedicalRecordMongoRepository;
import com.healthcare.platform.repository.mongo.UserMongoRepository;
import com.healthcare.platform.service.GeoService;
import com.healthcare.platform.service.MedicineTimerService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class WebController {

    @Autowired private MedicineTimerService timerService;
    @Autowired private MedicineRepository medicineRepository;
    @Autowired private MedicineScheduleRepository scheduleRepository;
    @Autowired private MedicineLogRepository logRepository;
    @Autowired private HospitalRepository hospitalRepository;
    @Autowired private DoctorRepository doctorRepository;
    @Autowired private BloodInventoryRepository bloodRepository;
    @Autowired private LabTestRepository labRepository;
    @Autowired private MedicalRecordRepository recordRepository;
    @Autowired private MedicalRecordMongoRepository mongoRecordRepository;
    @Autowired private UserMongoRepository userMongoRepository;
    @Autowired private VitalLogRepository vitalRepository;
    @Autowired private InsurancePolicyRepository insuranceRepository;
    @Autowired private HealthExpenseRepository expenseRepository;
    @Autowired private NotificationItemRepository notificationRepository;
    @Autowired private FamilyMemberRepository familyRepository;
    @Autowired private AppointmentRepository appointmentRepository;
    @Autowired private AdmissionRequestRepository admissionRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private GeoService geoService;
    @Autowired private PharmacyOrderRepository pharmacyOrderRepository;
    @Autowired private HomeCareBookingRepository homeCareBookingRepository;
    @Autowired private AuditAccessLogRepository auditAccessLogRepository;
    @Autowired private VaccinationRecordRepository vaccinationRecordRepository;
    @Autowired private com.healthcare.platform.service.PharmacyRegistryService pharmacyRegistryService;
    @Autowired private com.healthcare.platform.service.AuthTokenService authTokenService;

    private HttpServletRequest getCurrentHttpRequest() {
        try {
            org.springframework.web.context.request.RequestAttributes attrs = 
                org.springframework.web.context.request.RequestContextHolder.getRequestAttributes();
            if (attrs instanceof org.springframework.web.context.request.ServletRequestAttributes) {
                return ((org.springframework.web.context.request.ServletRequestAttributes) attrs).getRequest();
            }
        } catch (Exception ignored) {}
        return null;
    }

    private boolean isFacilityInRegion(String facilityCity, String facilityState, String effectiveCity, String effectiveState) {
        if ("ALL".equalsIgnoreCase(effectiveCity) || "ALL".equalsIgnoreCase(effectiveState)) {
            return true;
        }
        if (effectiveCity != null && !effectiveCity.isBlank()) {
            String c = effectiveCity.trim().toLowerCase();
            String fc = (facilityCity != null) ? facilityCity.trim().toLowerCase() : "";

            if (c.contains("budge")) {
                return fc.contains("budge") || fc.contains("pujali") || fc.contains("nangi") || fc.contains("maheshtala") || fc.contains("24 parganas");
            }
            if (c.contains("salt lake") || c.contains("new town")) {
                return fc.contains("salt lake") || fc.contains("new town") || fc.contains("bidhannagar") || fc.contains("rajarhat");
            }
            if (c.contains("howrah")) {
                return fc.contains("howrah");
            }
            if (c.contains("siliguri")) {
                return fc.contains("siliguri");
            }
            if (c.contains("durgapur") || c.contains("asansol")) {
                return fc.contains("durgapur") || fc.contains("asansol");
            }
            if (c.contains("kolkata") || c.contains("calcutta")) {
                // In Kolkata, strictly exclude peripheral/separate towns like Budge Budge, Siliguri, Durgapur
                if (fc.contains("budge") || fc.contains("siliguri") || fc.contains("durgapur")) {
                    return false;
                }
                return fc.contains("kolkata") || fc.contains("salt lake") || fc.contains("new town") || fc.contains("howrah");
            }
            if (c.contains("delhi")) {
                return fc.contains("delhi") || fc.contains("ncr");
            }
            if (c.contains("mumbai")) {
                return fc.contains("mumbai") || fc.contains("thane");
            }
            if (c.contains("bengaluru") || c.contains("bangalore")) {
                return fc.contains("bengaluru") || fc.contains("bangalore");
            }
            if (c.contains("bhubaneswar") || c.contains("cuttack")) {
                return fc.contains("bhubaneswar") || fc.contains("cuttack");
            }
            if (c.contains("chennai") || c.contains("madras")) {
                return fc.contains("chennai");
            }
            if (c.contains("hyderabad") || c.contains("secunderabad")) {
                return fc.contains("hyderabad") || fc.contains("secunderabad");
            }
            if (c.contains("pune")) {
                return fc.contains("pune");
            }
            // General city fallback
            if (!fc.isEmpty() && (fc.equals(c) || fc.contains(c) || c.contains(fc))) {
                return true;
            }
            return false;
        }

        if (effectiveState != null && facilityState != null && facilityState.equalsIgnoreCase(effectiveState)) {
            return true;
        }
        return false;
    }

    private void populateCommonAttributes(Model model, HttpSession session) {
        User user = null;
        String photoUrl = null;
        HttpServletRequest currentReq = getCurrentHttpRequest();
        if (session != null && session.getAttribute("currentUser") == null && currentReq != null) {
            authTokenService.restoreSessionIfPresent(currentReq, session);
        }

        if (session != null) {
            if (session.getAttribute("currentUser") != null) {
                user = (User) session.getAttribute("currentUser");
                photoUrl = (String) session.getAttribute("photoUrl");
                if ((photoUrl == null || photoUrl.isBlank()) && user.getPhotoUrl() != null && !user.getPhotoUrl().isBlank()) {
                    photoUrl = user.getPhotoUrl();
                    session.setAttribute("photoUrl", photoUrl);
                }
            } else if (session.getAttribute("mongoUser") != null) {
                UserDocument mu = (UserDocument) session.getAttribute("mongoUser");
                user = userRepository.findByUsername(mu.getUsername()).orElse(null);
                if (user == null) {
                    user = new User(mu.getUsername(), mu.getFullName(), mu.getEmail(), mu.getPhone(), "PATIENT");
                    user.setFirebaseUid(mu.getFirebaseUid());
                    user.setPhotoUrl(mu.getPhotoUrl());
                    user = userRepository.save(user);
                }
                session.setAttribute("currentUser", user);
                photoUrl = mu.getPhotoUrl();
                session.setAttribute("photoUrl", photoUrl);
            }
        }

        model.addAttribute("currentUser", user);
        model.addAttribute("photoUrl", photoUrl);
        model.addAttribute("isAuthenticated", user != null);
        model.addAttribute("notifications", notificationRepository.findAllByOrderByCreatedAtDesc());
        model.addAttribute("unreadNotifCount", notificationRepository.countByIsReadFalse());
        model.addAttribute("nextDose", timerService.getNextDose());
        model.addAttribute("adherencePercentage", timerService.calculateAdherencePercentage());
    }

    private static final Map<String, String> IP_CITY_CACHE = new java.util.concurrent.ConcurrentHashMap<>();

    private String extractClientIp(HttpServletRequest request) {
        if (request == null) return null;
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            String[] parts = xff.split(",");
            if (parts.length > 0) {
                String ip = parts[0].trim();
                if (!ip.isBlank()) return ip;
            }
        }
        String realIp = request.getHeader("X-Real-IP");
        if (realIp != null && !realIp.isBlank()) {
            return realIp.trim();
        }
        return request.getRemoteAddr();
    }

    private String lookupIpCity(String ip) {
        if (ip == null || ip.isBlank() || ip.startsWith("127.") || ip.startsWith("10.") || ip.startsWith("192.168.") || ip.equals("::1") || ip.equals("0:0:0:0:0:0:0:1")) {
            return null;
        }
        if (IP_CITY_CACHE.containsKey(ip)) {
            return IP_CITY_CACHE.get(ip);
        }
        try {
            String encodedIp = java.net.URLEncoder.encode(ip.trim(), java.nio.charset.StandardCharsets.UTF_8);
            java.net.URI uri = new java.net.URI("https://get.geojs.io/v1/ip/geo/" + encodedIp + ".json");
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) uri.toURL().openConnection();
            conn.setConnectTimeout(1000);
            conn.setReadTimeout(1000);
            conn.setRequestProperty("User-Agent", "CarePulse-Server/1.0");
            if (conn.getResponseCode() == 200) {
                try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(conn.getInputStream()))) {
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) sb.append(line);
                    String resp = sb.toString();
                    int cityIdx = resp.indexOf("\"city\":\"");
                    if (cityIdx != -1) {
                        int end = resp.indexOf("\"", cityIdx + 8);
                        if (end != -1) {
                            String rawCity = resp.substring(cityIdx + 8, end);
                            String matched = matchKnownCity(rawCity);
                            if (matched != null) {
                                IP_CITY_CACHE.put(ip, matched);
                                return matched;
                            }
                        }
                    }
                }
            }
        } catch (Exception ignored) {}
        return null;
    }

    private String resolveClientCity(HttpServletRequest request, String requestedCity) {
        if (requestedCity != null && !requestedCity.isBlank() && !requestedCity.equalsIgnoreCase("ALL")) {
            return requestedCity.trim();
        }
        if ("ALL".equalsIgnoreCase(requestedCity)) {
            return "ALL";
        }
        if (request != null) {
            String cfCity = request.getHeader("CF-IPCity");
            if (cfCity != null && !cfCity.isBlank()) {
                String matched = matchKnownCity(cfCity.trim());
                if (matched != null) return matched;
            }
            String xGeoCity = request.getHeader("X-Geo-City");
            if (xGeoCity != null && !xGeoCity.isBlank()) {
                String matched = matchKnownCity(xGeoCity.trim());
                if (matched != null) return matched;
            }
            String clientIp = extractClientIp(request);
            if (clientIp != null) {
                String ipCity = lookupIpCity(clientIp);
                if (ipCity != null) return ipCity;
            }
        }
        return "Kolkata";
    }

    private String matchKnownCity(String city) {
        if (city == null || city.isBlank()) return "Kolkata";
        for (String c : new String[]{"Kolkata", "Salt Lake", "New Town", "Howrah", "Budge Budge", "Siliguri", "Durgapur", "Delhi", "New Delhi", "Mumbai", "Bengaluru", "Bangalore", "Bhubaneswar", "Chennai", "Hyderabad", "Pune"}) {
            if (c.equalsIgnoreCase(city) || city.toLowerCase().contains(c.toLowerCase()) || c.toLowerCase().contains(city.toLowerCase())) {
                if ("Bangalore".equalsIgnoreCase(c)) return "Bengaluru";
                if ("New Delhi".equalsIgnoreCase(c)) return "Delhi";
                return c;
            }
        }
        return city;
    }

    @GetMapping("/api/location/detect")
    @ResponseBody
    public Map<String, Object> detectLocation(HttpServletRequest request) {
        String city = resolveClientCity(request, null);
        double[] coords = GeoService.resolveCityCoordinates(city);
        String state = GeoService.resolveStateFromCity(city);
        Map<String, Object> res = new HashMap<>();
        res.put("city", city);
        res.put("locality", city + ", " + state);
        res.put("state", state);
        res.put("latitude", coords != null ? coords[0] : 22.5726);
        res.put("longitude", coords != null ? coords[1] : 88.3639);
        return res;
    }

    private double[] resolveCoordinates(Double lat, Double lon, String city) {
        if (lat != null && lon != null) {
            return new double[]{lat, lon};
        }
        return GeoService.resolveCityCoordinates(city);
    }

    @GetMapping("/")
    public String index(Model model, HttpSession session, HttpServletRequest request,
                        @RequestParam(required = false) Double lat,
                        @RequestParam(required = false) Double lon,
                        @RequestParam(required = false) String locality,
                        @RequestParam(required = false) String city) {
        populateCommonAttributes(model, session);
        String effectiveCity = resolveClientCity(request, city);
        String effectiveLocality = (locality != null && !locality.isBlank()) ? locality : (effectiveCity + ", " + GeoService.resolveStateFromCity(effectiveCity));
        String effectiveState = GeoService.resolveStateFromCity(effectiveCity);
        double[] coords = resolveCoordinates(lat, lon, effectiveCity);

        // Fetch all hospitals and doctors to rank by live proximity from the anchor
        List<Hospital> hospitals = hospitalRepository.findAll();
        List<Doctor> doctors = doctorRepository.findAll();

        if (coords != null) {
            geoService.sortByProximityHospitals(hospitals, coords[0], coords[1]);
            geoService.sortByProximityDoctors(doctors, coords[0], coords[1]);
            model.addAttribute("userLat", coords[0]);
            model.addAttribute("userLon", coords[1]);
        } else {
            hospitals.forEach(h -> h.setDistanceKm(null));
            doctors.forEach(d -> d.setDistanceKm(null));
            model.addAttribute("userLat", null);
            model.addAttribute("userLon", null);
        }

        // Strictly filter to the selected city/state so other states never leak into this city's view
        List<Hospital> cityHospitals = hospitals.stream()
                .filter(h -> isFacilityInRegion(h.getCity(), h.getState(), effectiveCity, effectiveState))
                .toList();
        if (cityHospitals.isEmpty()) {
            cityHospitals = hospitals.stream()
                    .filter(h -> effectiveState != null && effectiveState.equalsIgnoreCase(h.getState()))
                    .toList();
            if (cityHospitals.isEmpty()) cityHospitals = hospitals;
        }

        List<Doctor> cityDoctors = doctors.stream()
                .filter(d -> isFacilityInRegion(d.getCity(), d.getState(), effectiveCity, effectiveState))
                .toList();
        if (cityDoctors.isEmpty()) {
            cityDoctors = doctors.stream()
                    .filter(d -> effectiveState != null && effectiveState.equalsIgnoreCase(d.getState()))
                    .toList();
            if (cityDoctors.isEmpty()) cityDoctors = doctors;
        }

        model.addAttribute("nearbyHospitals", cityHospitals.stream().limit(6).toList());
        model.addAttribute("nearbyDoctors", cityDoctors.stream().limit(6).toList());
        model.addAttribute("nearbyPharmacies", pharmacyRegistryService.getNearbyPharmacies(effectiveCity, coords != null ? coords[0] : null, coords != null ? coords[1] : null, null).stream().limit(6).toList());
        model.addAttribute("allWbHospitals", cityHospitals);
        model.addAttribute("allWbDoctors", cityDoctors);
        model.addAttribute("activeMedicines", medicineRepository.findByActiveTrue());
        model.addAttribute("recentVitals", vitalRepository.findAllByOrderByTimestampDesc().stream().findFirst().orElse(null));
        model.addAttribute("selectedState", effectiveState);
        model.addAttribute("selectedCity", effectiveCity);
        model.addAttribute("selectedLocality", effectiveLocality);
        List<MedicalRecordDocument> mongoDocs = mongoRecordRepository.findAllByOrderByRecordDateDesc();
        model.addAttribute("recentRecord", mongoDocs.stream().findFirst().orElse(null));

        return "index";
    }

    @GetMapping("/medicine-timer")
    public String medicineTimer(Model model, HttpSession session) {
        populateCommonAttributes(model, session);
        model.addAttribute("activeMedicines", medicineRepository.findByActiveTrue());
        model.addAttribute("schedules", scheduleRepository.findByActiveTrueOrderByDoseTimeAsc());
        model.addAttribute("logs", logRepository.findAllByOrderByScheduledDateTimeDesc().stream().limit(10).toList());
        model.addAttribute("lowStockMeds", medicineRepository.findByRemainingPillsLessThanEqual(5));
        return "medicine-timer";
    }

    @GetMapping("/ai-assistant")
    public String aiAssistant(Model model, HttpSession session) {
        populateCommonAttributes(model, session);
        // Prioritize MongoDB records
        List<MedicalRecordDocument> mongoDocs = mongoRecordRepository.findAllByOrderByRecordDateDesc();
        model.addAttribute("records", mongoDocs);
        return "ai-assistant";
    }

    @GetMapping("/doctors")
    public String doctors(Model model, HttpSession session, HttpServletRequest request,
                          @RequestParam(required = false) String state,
                          @RequestParam(required = false) String specialty,
                          @RequestParam(required = false) Double lat,
                          @RequestParam(required = false) Double lon,
                          @RequestParam(required = false) String locality,
                          @RequestParam(required = false) String city) {
        populateCommonAttributes(model, session);
        String effectiveCity;
        String effectiveState;
        if (city != null && !city.isBlank()) {
            effectiveCity = city.trim();
            effectiveState = GeoService.resolveStateFromCity(effectiveCity);
        } else if (state != null && !state.isBlank()) {
            effectiveCity = null;
            effectiveState = state.trim();
        } else {
            effectiveCity = resolveClientCity(request, null);
            effectiveState = GeoService.resolveStateFromCity(effectiveCity);
        }

        double[] coords = resolveCoordinates(lat, lon, (effectiveCity != null) ? effectiveCity : ("ALL".equalsIgnoreCase(effectiveState) ? "Kolkata" : effectiveState));

        List<Doctor> doctors;
        boolean doctorLocalityFallback = false;
        if ("ALL".equalsIgnoreCase(effectiveState)) {
            if (specialty != null && !specialty.isBlank()) {
                doctors = doctorRepository.findBySpecialtyIgnoreCase(specialty);
            } else if (effectiveCity != null) {
                doctors = doctorRepository.findByCityIgnoreCase(effectiveCity);
                if (doctors.isEmpty()) {
                    doctors = doctorRepository.findAll();
                    doctorLocalityFallback = true;
                }
            } else {
                doctors = doctorRepository.findAll();
            }
        } else {
            if (effectiveCity != null && !effectiveCity.equalsIgnoreCase("ALL")) {
                if (specialty != null && !specialty.isBlank()) {
                    doctors = doctorRepository.findByCityIgnoreCaseAndSpecialtyIgnoreCase(effectiveCity, specialty);
                    if (doctors.isEmpty()) {
                        doctors = doctorRepository.findByStateIgnoreCaseAndSpecialtyIgnoreCase(effectiveState, specialty);
                    }
                } else {
                    doctors = doctorRepository.findByCityIgnoreCase(effectiveCity);
                    if (doctors.isEmpty()) {
                        doctors = doctorRepository.findByStateIgnoreCase(effectiveState);
                        doctorLocalityFallback = true;
                    }
                }
            } else if (specialty != null && !specialty.isBlank()) {
                doctors = doctorRepository.findByStateIgnoreCaseAndSpecialtyIgnoreCase(effectiveState, specialty);
            } else {
                doctors = doctorRepository.findByStateIgnoreCase(effectiveState);
            }
            // Strictly enforce that all returned doctors belong to the chosen region (no foreign states)
            final String fCity = effectiveCity;
            final String fState = effectiveState;
            doctors = doctors.stream()
                    .filter(d -> isFacilityInRegion(d.getCity(), d.getState(), fCity, fState))
                    .toList();
        }

        if (coords != null) {
            doctors = geoService.sortByProximityDoctors(doctors, coords[0], coords[1]);
            model.addAttribute("userLat", coords[0]);
            model.addAttribute("userLon", coords[1]);
        } else {
            doctors.forEach(d -> d.setDistanceKm(null));
            model.addAttribute("userLat", null);
            model.addAttribute("userLon", null);
        }

        model.addAttribute("doctors", doctors);
        model.addAttribute("localityFallback", doctorLocalityFallback);
        model.addAttribute("selectedState", effectiveState);
        model.addAttribute("selectedSpecialty", specialty);
        model.addAttribute("selectedCity", effectiveCity);
        model.addAttribute("selectedLocality", (locality != null && !locality.isBlank()) ? locality : (effectiveCity != null ? effectiveCity : null));
        model.addAttribute("appointments", appointmentRepository.findAllByOrderByAppointmentDateTimeAsc());
        return "doctors";
    }

    @GetMapping("/hospitals")
    public String hospitals(Model model, HttpSession session, HttpServletRequest request,
                            @RequestParam(required = false) String state,
                            @RequestParam(required = false) Double lat,
                            @RequestParam(required = false) Double lon,
                            @RequestParam(required = false) String locality,
                            @RequestParam(required = false) String city) {
        populateCommonAttributes(model, session);
        String effectiveCity;
        String effectiveState;
        if (city != null && !city.isBlank()) {
            effectiveCity = city.trim();
            effectiveState = GeoService.resolveStateFromCity(effectiveCity);
        } else if (state != null && !state.isBlank()) {
            effectiveCity = null;
            effectiveState = state.trim();
        } else {
            effectiveCity = resolveClientCity(request, null);
            effectiveState = GeoService.resolveStateFromCity(effectiveCity);
        }

        double[] coords = resolveCoordinates(lat, lon, (effectiveCity != null) ? effectiveCity : ("ALL".equalsIgnoreCase(effectiveState) ? "Kolkata" : effectiveState));

        List<Hospital> hospitals;
        boolean hospitalLocalityFallback = false;
        if ("ALL".equalsIgnoreCase(effectiveState)) {
            if (effectiveCity != null) {
                hospitals = hospitalRepository.findByCityIgnoreCase(effectiveCity);
                if (hospitals.isEmpty()) {
                    hospitals = hospitalRepository.findAll();
                    hospitalLocalityFallback = true;
                }
            } else {
                hospitals = hospitalRepository.findAll();
            }
        } else {
            if (effectiveCity != null && !effectiveCity.equalsIgnoreCase("ALL")) {
                hospitals = hospitalRepository.findByCityIgnoreCase(effectiveCity);
                if (hospitals.isEmpty()) {
                    hospitals = hospitalRepository.findByStateIgnoreCase(effectiveState);
                    hospitalLocalityFallback = true;
                }
            } else {
                hospitals = hospitalRepository.findByStateIgnoreCase(effectiveState);
            }
            // Strictly enforce that all returned hospitals belong to the chosen region (no foreign states)
            final String fCity = effectiveCity;
            final String fState = effectiveState;
            hospitals = hospitals.stream()
                    .filter(h -> isFacilityInRegion(h.getCity(), h.getState(), fCity, fState))
                    .toList();
        }

        if (coords != null) {
            hospitals = geoService.sortByProximityHospitals(hospitals, coords[0], coords[1]);
            model.addAttribute("userLat", coords[0]);
            model.addAttribute("userLon", coords[1]);
        } else {
            hospitals.forEach(h -> h.setDistanceKm(null));
            model.addAttribute("userLat", null);
            model.addAttribute("userLon", null);
        }

        List<Doctor> allDocsList = doctorRepository.findAll();
        Map<Long, List<Doctor>> hospitalDoctorsMap = new HashMap<>();
        for (Hospital h : hospitals) {
            String hName = h.getName() != null ? h.getName().toLowerCase() : "";
            List<Doctor> docList = allDocsList.stream()
                    .filter(d -> {
                        String aff = d.getHospitalAffiliation() != null ? d.getHospitalAffiliation().toLowerCase() : "";
                        return aff.contains(hName) || hName.contains(aff);
                    })
                    .toList();
            if (docList.isEmpty()) {
                docList = allDocsList.stream()
                        .filter(d -> d.getCity() != null && h.getCity() != null && d.getCity().equalsIgnoreCase(h.getCity()))
                        .limit(3)
                        .toList();
                if (docList.isEmpty()) {
                    docList = allDocsList.stream().limit(3).toList();
                }
            }
            hospitalDoctorsMap.put(h.getId(), docList);
        }

        model.addAttribute("hospitals", hospitals);
        model.addAttribute("hospitalDoctorsMap", hospitalDoctorsMap);
        model.addAttribute("localityFallback", hospitalLocalityFallback);
        model.addAttribute("selectedState", effectiveState);
        model.addAttribute("selectedCity", effectiveCity);
        model.addAttribute("selectedLocality", (locality != null && !locality.isBlank()) ? locality : (effectiveCity != null ? effectiveCity : null));

        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;
        if (currentUser != null) {
            String uName = currentUser.getFullName() != null ? currentUser.getFullName().trim().toLowerCase() : "";
            List<AdmissionRequest> userAdmissions = admissionRepository.findAllByOrderByRequestedAtDesc().stream()
                    .filter(a -> a.getPatientName() != null && !a.getPatientName().isBlank() &&
                            (a.getPatientName().toLowerCase().contains(uName) || uName.contains(a.getPatientName().toLowerCase())))
                    .toList();
            model.addAttribute("admissions", userAdmissions);
        } else {
            model.addAttribute("admissions", Collections.emptyList());
        }

        model.addAttribute("allDoctors", allDocsList);
        return "hospitals";
    }

    @GetMapping("/emergency")
    public String emergency(Model model, HttpSession session, HttpServletRequest request,
                            @RequestParam(required = false) Double lat,
                            @RequestParam(required = false) Double lon,
                            @RequestParam(required = false) String locality,
                            @RequestParam(required = false) String city) {
        populateCommonAttributes(model, session);
        String effectiveCity = resolveClientCity(request, city);
        String effectiveState = GeoService.resolveStateFromCity(effectiveCity);
        double[] coords = resolveCoordinates(lat, lon, (effectiveCity != null) ? effectiveCity : "Kolkata");

        List<Hospital> emergencyHospitals = hospitalRepository.findByEmergencyDeptTrue();
        List<BloodInventory> blood = bloodRepository.findAll();

        if (effectiveCity != null || !"ALL".equalsIgnoreCase(effectiveState)) {
            final String fCity = effectiveCity;
            final String fState = effectiveState;
            emergencyHospitals = emergencyHospitals.stream()
                    .filter(h -> isFacilityInRegion(h.getCity(), h.getState(), fCity, fState))
                    .toList();
            blood = blood.stream()
                    .filter(b -> isFacilityInRegion(b.getCity(), b.getState(), fCity, fState))
                    .toList();
        }

        if (coords != null) {
            emergencyHospitals = geoService.sortByProximityHospitals(emergencyHospitals, coords[0], coords[1]);
            blood = geoService.sortByProximityBlood(blood, coords[0], coords[1]);
            model.addAttribute("userLat", coords[0]);
            model.addAttribute("userLon", coords[1]);
        } else {
            emergencyHospitals.forEach(h -> h.setDistanceKm(null));
            blood.forEach(b -> b.setDistanceKm(null));
            model.addAttribute("userLat", null);
            model.addAttribute("userLon", null);
        }

        model.addAttribute("emergencyHospitals", emergencyHospitals);
        model.addAttribute("emergencyBlood", blood.stream().limit(6).toList());
        model.addAttribute("selectedCity", effectiveCity);
        model.addAttribute("selectedState", effectiveState);
        model.addAttribute("selectedLocality", (locality != null && !locality.isBlank()) ? locality : (effectiveCity + ", " + effectiveState));
        return "emergency";
    }

    @GetMapping("/blood-bank")
    public String bloodBank(Model model, HttpSession session, HttpServletRequest request,
                            @RequestParam(required = false) String group,
                            @RequestParam(required = false) Double lat,
                            @RequestParam(required = false) Double lon,
                            @RequestParam(required = false) String locality,
                            @RequestParam(required = false) String city) {
        populateCommonAttributes(model, session);
        String effectiveCity = resolveClientCity(request, city);
        String effectiveState = GeoService.resolveStateFromCity(effectiveCity);
        double[] coords = resolveCoordinates(lat, lon, (effectiveCity != null) ? effectiveCity : "Kolkata");

        List<BloodInventory> list = (group != null && !group.isBlank())
                ? bloodRepository.findByBloodGroupIgnoreCase(group)
                : bloodRepository.findAll();

        if (effectiveCity != null || !"ALL".equalsIgnoreCase(effectiveState)) {
            final String fCity = effectiveCity;
            final String fState = effectiveState;
            list = list.stream()
                    .filter(b -> isFacilityInRegion(b.getCity(), b.getState(), fCity, fState))
                    .toList();
        }

        if (coords != null) {
            list = geoService.sortByProximityBlood(list, coords[0], coords[1]);
            model.addAttribute("userLat", coords[0]);
            model.addAttribute("userLon", coords[1]);
        } else {
            list.forEach(b -> b.setDistanceKm(null));
            model.addAttribute("userLat", null);
            model.addAttribute("userLon", null);
        }

        model.addAttribute("bloodList", list);
        model.addAttribute("selectedGroup", group);
        model.addAttribute("selectedCity", effectiveCity);
        model.addAttribute("selectedState", effectiveState);
        model.addAttribute("selectedLocality", (locality != null && !locality.isBlank()) ? locality : (effectiveCity + ", " + effectiveState));
        return "blood-bank";
    }

    @GetMapping("/diagnostics")
    public String diagnostics(Model model, HttpSession session, HttpServletRequest request,
                              @RequestParam(required = false) String category,
                              @RequestParam(required = false) Double lat,
                              @RequestParam(required = false) Double lon,
                              @RequestParam(required = false) String locality,
                              @RequestParam(required = false) String city) {
        populateCommonAttributes(model, session);
        String effectiveCity = resolveClientCity(request, city);
        String effectiveState = GeoService.resolveStateFromCity(effectiveCity);
        double[] coords = resolveCoordinates(lat, lon, (effectiveCity != null) ? effectiveCity : "Kolkata");

        List<LabTest> tests = (category != null && !category.isBlank())
                ? labRepository.findByCategoryIgnoreCase(category)
                : labRepository.findAll();

        if (effectiveCity != null || !"ALL".equalsIgnoreCase(effectiveState)) {
            final String fCity = effectiveCity;
            final String fState = effectiveState;
            List<LabTest> filtered = tests.stream()
                    .filter(t -> isFacilityInRegion(t.getCity(), t.getState(), fCity, fState))
                    .toList();
            if (!filtered.isEmpty()) {
                tests = filtered;
            }
        }

        if (coords != null) {
            tests = geoService.sortByProximityLabs(tests, coords[0], coords[1]);
            model.addAttribute("userLat", coords[0]);
            model.addAttribute("userLon", coords[1]);
        } else {
            tests.forEach(t -> t.setDistanceKm(null));
            model.addAttribute("userLat", null);
            model.addAttribute("userLon", null);
        }

        model.addAttribute("labTests", tests);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("selectedCity", effectiveCity);
        model.addAttribute("selectedState", effectiveState);
        model.addAttribute("selectedLocality", (locality != null && !locality.isBlank()) ? locality : (effectiveCity + ", " + effectiveState));
        return "diagnostics";
    }

    @GetMapping("/medical-records")
    public String medicalRecords(Model model, HttpSession session) {
        populateCommonAttributes(model, session);
        // True MongoDB records
        List<MedicalRecordDocument> mongoDocs = mongoRecordRepository.findAllByOrderByRecordDateDesc();
        model.addAttribute("records", mongoDocs);
        return "medical-records";
    }

    @GetMapping("/family-care")
    public String familyCare(Model model, HttpSession session) {
        populateCommonAttributes(model, session);
        model.addAttribute("familyMembers", familyRepository.findAll());
        return "family-care";
    }

    @GetMapping("/vitals-wellness")
    public String vitalsWellness(Model model, HttpSession session) {
        populateCommonAttributes(model, session);
        model.addAttribute("vitalsList", vitalRepository.findAllByOrderByTimestampDesc());
        model.addAttribute("latestVitals", vitalRepository.findAllByOrderByTimestampDesc().stream().findFirst().orElse(null));
        return "vitals-wellness";
    }

    @GetMapping("/insurance-expenses")
    public String insuranceExpenses(Model model, HttpSession session) {
        populateCommonAttributes(model, session);
        model.addAttribute("policies", insuranceRepository.findAll());
        model.addAttribute("expenses", expenseRepository.findAllByOrderByExpenseDateDesc());
        return "insurance-expenses";
    }

    @GetMapping("/health-passport")
    public String healthPassport(Model model, HttpSession session) {
        populateCommonAttributes(model, session);
        model.addAttribute("activeMeds", medicineRepository.findByActiveTrue());
        return "health-passport";
    }

    private UserDocument getOrCreateMongoUser(HttpSession session) {
        if (session == null) return null;
        User currentUser = (User) session.getAttribute("currentUser");
        String uid = (String) session.getAttribute("firebaseUid");

        if (currentUser == null && (uid == null || uid.isBlank())) {
            return null; // Guest user is NOT logged in - do NOT leak mock profile!
        }

        UserDocument mongoUser = null;
        if (uid != null && !uid.isBlank()) {
            mongoUser = userMongoRepository.findByFirebaseUid(uid).orElse(null);
        }
        if (mongoUser == null && currentUser != null && currentUser.getEmail() != null && !currentUser.getEmail().isBlank()) {
            mongoUser = userMongoRepository.findByEmail(currentUser.getEmail()).orElse(null);
        }
        if (mongoUser == null && currentUser != null && currentUser.getUsername() != null) {
            mongoUser = userMongoRepository.findByUsername(currentUser.getUsername()).orElse(null);
        }
        if (mongoUser == null && currentUser != null) {
            String genUid = (uid != null && !uid.isBlank()) ? uid : ("usr_" + System.currentTimeMillis());
            mongoUser = new UserDocument(
                    genUid,
                    currentUser.getUsername(),
                    currentUser.getFullName(),
                    currentUser.getEmail(),
                    currentUser.getPhone()
            );
            mongoUser.setState("West Bengal");
            mongoUser.setCity(currentUser.getPreferredCity() != null ? currentUser.getPreferredCity() : "Kolkata");
            mongoUser.setBloodGroup(currentUser.getBloodGroup() != null ? currentUser.getBloodGroup() : "O+");
            mongoUser = userMongoRepository.save(mongoUser);
        }
        return mongoUser;
    }

    @GetMapping("/portals")
    public String portals(Model model, HttpSession session) {
        populateCommonAttributes(model, session);
        User currentUser = session != null ? (User) session.getAttribute("currentUser") : null;
        UserDocument mongoUser = getOrCreateMongoUser(session);
        boolean isAuthenticated = (currentUser != null || mongoUser != null);
        model.addAttribute("isAuthenticated", isAuthenticated);
        model.addAttribute("mongoUser", mongoUser);

        if (isAuthenticated) {
            String patientName = currentUser != null ? currentUser.getFullName() : (mongoUser != null ? mongoUser.getFullName() : "");
            String patientPhone = currentUser != null ? currentUser.getPhone() : (mongoUser != null ? mongoUser.getPhone() : "");
            
            // Show real appointments matching patient or all active if freshly booked
            List<Appointment> allAppts = appointmentRepository.findAllByOrderByAppointmentDateTimeAsc();
            List<Appointment> userAppts = allAppts.stream()
                    .filter(a -> (patientName != null && patientName.equalsIgnoreCase(a.getPatientName())) ||
                                 (patientPhone != null && patientPhone.equalsIgnoreCase(a.getPatientPhone())))
                    .toList();
            model.addAttribute("appointments", !userAppts.isEmpty() ? userAppts : allAppts);

            List<AdmissionRequest> allAdms = admissionRepository.findAllByOrderByRequestedAtDesc();
            List<AdmissionRequest> userAdms = allAdms.stream()
                    .filter(a -> (patientName != null && patientName.equalsIgnoreCase(a.getPatientName())) ||
                                 (patientPhone != null && patientPhone.equalsIgnoreCase(a.getContactPhone())))
                    .toList();
            model.addAttribute("admissions", !userAdms.isEmpty() ? userAdms : allAdms);
        } else {
            // Guest mode: zero leak of personal consultations or admissions!
            model.addAttribute("appointments", List.of());
            model.addAttribute("admissions", List.of());
        }

        model.addAttribute("hospitals", hospitalRepository.findAll());
        model.addAttribute("doctors", doctorRepository.findAll());
        model.addAttribute("activeMeds", medicineRepository.findByActiveTrue());
        model.addAttribute("orders", pharmacyOrderRepository.findAllByOrderByOrderedAtDesc());
        model.addAttribute("vitals", vitalRepository.findAllByOrderByTimestampDesc());
        model.addAttribute("mongoRecords", mongoRecordRepository.findAll());
        return "portals";
    }

    @GetMapping("/journey")
    public String journey(Model model, HttpSession session) {
        populateCommonAttributes(model, session);
        return "journey";
    }

    @GetMapping("/pharmacy")
    public String pharmacy(Model model, HttpSession session, HttpServletRequest request,
                           @RequestParam(required = false) Double lat,
                           @RequestParam(required = false) Double lon,
                           @RequestParam(required = false) String locality,
                           @RequestParam(required = false) String city) {
        populateCommonAttributes(model, session);
        String effectiveCity = resolveClientCity(request, city);
        double[] coords = resolveCoordinates(lat, lon, effectiveCity);

        model.addAttribute("activeMeds", medicineRepository.findByActiveTrue());
        model.addAttribute("lowStockMeds", medicineRepository.findByRemainingPillsLessThanEqual(5));
        model.addAttribute("orders", pharmacyOrderRepository.findAllByOrderByOrderedAtDesc());
        model.addAttribute("pharmacies", pharmacyRegistryService.getNearbyPharmacies(effectiveCity, coords != null ? coords[0] : null, coords != null ? coords[1] : null, null));
        model.addAttribute("selectedCity", effectiveCity);
        model.addAttribute("selectedLocality", (locality != null && !locality.isBlank()) ? locality : (effectiveCity + ", " + GeoService.resolveStateFromCity(effectiveCity)));
        model.addAttribute("userLat", coords != null ? coords[0] : null);
        model.addAttribute("userLon", coords != null ? coords[1] : null);
        return "pharmacy";
    }

    @GetMapping("/home-healthcare")
    public String homeHealthcare(Model model, HttpSession session, HttpServletRequest request,
                                 @RequestParam(required = false) Double lat,
                                 @RequestParam(required = false) Double lon,
                                 @RequestParam(required = false) String locality,
                                 @RequestParam(required = false) String city) {
        populateCommonAttributes(model, session);
        String effectiveCity = resolveClientCity(request, city);
        double[] coords = resolveCoordinates(lat, lon, effectiveCity);

        model.addAttribute("selectedCity", effectiveCity);
        model.addAttribute("selectedLocality", (locality != null && !locality.isBlank()) ? locality : (effectiveCity + ", " + GeoService.resolveStateFromCity(effectiveCity)));
        model.addAttribute("userLat", coords != null ? coords[0] : null);
        model.addAttribute("userLon", coords != null ? coords[1] : null);
        model.addAttribute("bookings", homeCareBookingRepository.findAllByOrderByBookedAtDesc());
        return "home-healthcare";
    }

    @GetMapping("/vaccination")
    public String vaccination(Model model, HttpSession session) {
        populateCommonAttributes(model, session);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;
        if (currentUser != null) {
            String uName = currentUser.getFullName() != null ? currentUser.getFullName().trim().toLowerCase() : "";
            List<VaccinationRecord> adult = vaccinationRecordRepository.findByTargetGroupIgnoreCase("ADULT").stream()
                    .filter(v -> v.getPatientName() != null && (v.getPatientName().toLowerCase().contains(uName) || uName.contains(v.getPatientName().toLowerCase())))
                    .toList();
            List<VaccinationRecord> child = vaccinationRecordRepository.findByTargetGroupIgnoreCase("CHILD").stream()
                    .filter(v -> v.getPatientName() != null && (v.getPatientName().toLowerCase().contains(uName) || uName.contains(v.getPatientName().toLowerCase())))
                    .toList();
            List<VaccinationRecord> all = vaccinationRecordRepository.findAllByOrderByAdministeredDateDesc().stream()
                    .filter(v -> v.getPatientName() != null && (v.getPatientName().toLowerCase().contains(uName) || uName.contains(v.getPatientName().toLowerCase())))
                    .toList();
            model.addAttribute("childVaccines", child);
            model.addAttribute("adultVaccines", adult);
            model.addAttribute("allVaccines", all);
            model.addAttribute("notSignedIn", false);
        } else {
            model.addAttribute("childVaccines", Collections.emptyList());
            model.addAttribute("adultVaccines", Collections.emptyList());
            model.addAttribute("allVaccines", Collections.emptyList());
            model.addAttribute("notSignedIn", true);
        }
        return "vaccination";
    }

    @GetMapping("/privacy-center")
    public String privacyCenter(Model model, HttpSession session) {
        populateCommonAttributes(model, session);
        model.addAttribute("auditLogs", auditAccessLogRepository.findAllByOrderByTimestampDesc());
        return "privacy-center";
    }

    @GetMapping("/profile")
    public String profile(Model model, HttpSession session) {
        populateCommonAttributes(model, session);
        UserDocument mongoUser = getOrCreateMongoUser(session);
        if (mongoUser == null && (session == null || session.getAttribute("currentUser") == null)) {
            return "redirect:/login";
        }
        model.addAttribute("mongoUser", mongoUser);
        return "profile";
    }

    @GetMapping("/login")
    public String loginPage(Model model, HttpSession session) {
        populateCommonAttributes(model, session);
        model.addAttribute("pageTitle", "Log In");
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model, HttpSession session) {
        populateCommonAttributes(model, session);
        model.addAttribute("pageTitle", "Create Account / Register");
        return "register";
    }

    @GetMapping(value = "/robots.txt", produces = org.springframework.http.MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public org.springframework.core.io.Resource getRobotsTxt() {
        return new org.springframework.core.io.ClassPathResource("static/robots.txt");
    }

    @GetMapping(value = "/sitemap.xml", produces = org.springframework.http.MediaType.APPLICATION_XML_VALUE)
    @ResponseBody
    public org.springframework.core.io.Resource getSitemapXml() {
        return new org.springframework.core.io.ClassPathResource("static/sitemap.xml");
    }

    @GetMapping(value = "/manifest.json", produces = "application/manifest+json")
    @ResponseBody
    public org.springframework.core.io.Resource getManifestJson() {
        return new org.springframework.core.io.ClassPathResource("static/manifest.json");
    }

    @GetMapping(value = "/sw.js", produces = "application/javascript")
    @ResponseBody
    public org.springframework.http.ResponseEntity<org.springframework.core.io.Resource> getServiceWorker() {
        return org.springframework.http.ResponseEntity.ok()
                .header("Service-Worker-Allowed", "/")
                .header("Cache-Control", "no-cache, no-store, must-revalidate")
                .contentType(org.springframework.http.MediaType.parseMediaType("application/javascript"))
                .body(new org.springframework.core.io.ClassPathResource("static/sw.js"));
    }
}
