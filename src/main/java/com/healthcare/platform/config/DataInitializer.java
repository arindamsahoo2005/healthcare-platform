package com.healthcare.platform.config;

import com.healthcare.platform.model.*;
import com.healthcare.platform.model.mongo.*;
import com.healthcare.platform.repository.*;
import com.healthcare.platform.repository.mongo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired private HospitalRepository hospitalRepository;
    @Autowired private DoctorRepository doctorRepository;
    @Autowired private BloodInventoryRepository bloodRepository;
    @Autowired private LabTestRepository labRepository;
    @Autowired private VaccinationRecordRepository vaccinationRecordRepository;
    @Autowired private MedicineRepository medicineRepository;
    @Autowired private MedicineScheduleRepository scheduleRepository;
    @Autowired private PharmacyOrderRepository pharmacyOrderRepository;
    @Autowired private AdmissionRequestRepository admissionRepository;

    // MongoDB Repositories
    @Autowired private HospitalMongoRepository hospitalMongoRepository;
    @Autowired private DoctorMongoRepository doctorMongoRepository;
    @Autowired private VaccinationRecordMongoRepository vaccinationMongoRepository;
    @Autowired private LabTestMongoRepository labTestMongoRepository;
    @Autowired private MedicalRecordMongoRepository mongoRecordRepository;
    @Autowired private UserMongoRepository userMongoRepository;

    @Override
    public void run(String... args) throws Exception {
        admissionRepository.deleteAll(); // Clean any previous test admissions
        seedHospitals();
        seedDoctors();
        seedBloodInventory();
        seedLabTests();
        seedVaccinations();
        seedMedicalRecords();
        seedMedicines();
        seedPharmacyOrders();
        seedUsers();
    }

    private void saveHospital(Hospital h) {
        hospitalRepository.save(h);
        hospitalMongoRepository.save(new HospitalDocument(
                h.getName(), h.getType(), h.getAddress(), h.getCity(), h.getState(),
                h.getLatitude(), h.getLongitude(), h.getPhone(), h.getEmergencyHelpline(),
                h.getIcuBedsTotal(), h.getIcuBedsAvailable(), h.getGeneralBedsTotal(), h.getGeneralBedsAvailable()
        ));
    }

    private void saveDoctor(Doctor d) {
        doctorRepository.save(d);
        doctorMongoRepository.save(new DoctorDocument(
                d.getName(), d.getSpecialty(), d.getSubSpecialty(), d.getCredentials(),
                d.getExperienceYears(), d.getLanguages(), d.getConsultationFee(),
                d.getHospitalAffiliation(), d.getCity(), d.getState(),
                d.getLatitude(), d.getLongitude()
        ));
    }

    private void saveLabTest(LabTest t) {
        labRepository.save(t);
        labTestMongoRepository.save(new LabTestDocument(
                t.getTestName(), t.getCategory(), t.getSampleType(), t.getTurnaroundHours() != null ? t.getTurnaroundHours() + " Hours" : "24 Hours",
                t.getPrice(), t.getPreparationInstructions(), t.getCenterName(), t.isHomeCollectionAvailable()
        ));
    }

    private void seedHospitals() {
        hospitalRepository.deleteAll();
        hospitalMongoRepository.deleteAll();

        // --- LOCAL BUDGE BUDGE & SOUTH 24 PARGANAS FACILITIES ---

        // 1. ESI Hospital Budge Budge
        saveHospital(new Hospital(
                "ESI Hospital Budge Budge", "Government Super-Speciality Hospital",
                "MG Road, Budge Budge, South 24 Parganas, West Bengal 700137", "Budge Budge", "West Bengal",
                22.4820, 88.1812, "+91 33 2482 1245", "102 / 033-24821245", 35, 10, 200, 45
        ));

        // 2. Budge Budge Municipal Hospital
        saveHospital(new Hospital(
                "Budge Budge Municipal Hospital", "Government General Hospital & Emergency",
                "Old Kalibari Road, Budge Budge, Kolkata, West Bengal 700137", "Budge Budge", "West Bengal",
                22.4785, 88.1750, "+91 33 2482 1102", "102 / 033-24821102", 20, 6, 100, 24
        ));

        // 3. Kasturi Nursing Home & Diagnostic Centre
        saveHospital(new Hospital(
                "Kasturi Nursing Home & Diagnostic Centre", "Nursing Home & Day Care Surgery",
                "Diamond Harbour Road, Thakurpukur, Kolkata, West Bengal 700104", "Budge Budge", "West Bengal",
                22.4640, 88.3050, "+91 33 2453 7800", "033-24537888", 25, 8, 150, 32
        ));

        // 4. Budge Budge Care Nursing Home
        saveHospital(new Hospital(
                "Budge Budge Care Nursing Home", "Private Nursing Home & Maternity Care",
                "72, MG Road, Near Railway Station, Budge Budge 700137", "Budge Budge", "West Bengal",
                22.4810, 88.1830, "+91 33 2482 3344", "033-24823344", 12, 4, 60, 18
        ));

        // 5. Mother & Child Care Nursing Home
        saveHospital(new Hospital(
                "Mother & Child Care Nursing Home", "Private Nursing Home & Maternity Care",
                "Budge Budge Trunk Road, Gopalpur, Maheshtala, Kolkata 700141", "Budge Budge", "West Bengal",
                22.4910, 88.2450, "+91 33 2492 5566", "033-24925566", 15, 5, 80, 22
        ));

        // 6. Diamond City Nursing Home & Polyclinic
        saveHospital(new Hospital(
                "Diamond City Nursing Home & Polyclinic", "Nursing Home & Day Care Surgery",
                "Budge Budge Road, Sarsuna Main Road, Kolkata 700061", "Budge Budge", "West Bengal",
                22.4720, 88.2780, "+91 33 2498 7711", "033-24987711", 10, 3, 50, 15
        ));

        // 7. Pujali Municipal Health Centre & Nursing Unit
        saveHospital(new Hospital(
                "Pujali Municipal Health Centre & Nursing Unit", "Government Nursing Unit & Day Care",
                "Pujali Municipality Road, Budge Budge 700138", "Budge Budge", "West Bengal",
                22.4690, 88.1480, "+91 33 2482 9900", "102 / 033-24829900", 8, 3, 40, 12
        ));

        // 8. Maheshtala Municipal Hospital & Matri Sadan
        saveHospital(new Hospital(
                "Maheshtala Municipal Hospital & Matri Sadan", "Government General Hospital & Emergency",
                "Santoshpur Govt Colony, Maheshtala, South 24 Parganas 700142", "Maheshtala", "West Bengal",
                22.5020, 88.2580, "+91 33 2490 1200", "102 / 033-24901200", 18, 6, 110, 26
        ));

        // 9. South Suburban Nursing Home Behala
        saveHospital(new Hospital(
                "South Suburban Nursing Home", "Private Nursing Home & Day Care",
                "Diamond Harbour Road, Sakher Bazar, Behala, Kolkata 700008", "Kolkata", "West Bengal",
                22.4890, 88.3120, "+91 33 2445 8899", "033-24458899", 14, 4, 70, 19
        ));

        // 10. Balananda Brahmachari Hospital & Nursing Home
        saveHospital(new Hospital(
                "Balananda Brahmachari Hospital & Nursing Home", "Multi-Speciality Hospital & Nursing Home",
                "115/1, Diamond Harbour Road, Behala, Kolkata 700034", "Kolkata", "West Bengal",
                22.4960, 88.3180, "+91 33 2468 4500", "033-24684500", 22, 7, 120, 28
        ));

        // 11. Diamond Harbour Govt Medical College & Hospital
        saveHospital(new Hospital(
                "Diamond Harbour Govt Medical College & Hospital", "Government Teaching Hospital & 24x7 Trauma",
                "Harindanga, Diamond Harbour, South 24 Parganas 743331", "Diamond Harbour", "West Bengal",
                22.1912, 88.1963, "+91 3174 255 120", "102 / 03174-255120", 40, 12, 500, 85
        ));

        // --- GOVERNMENT APEX TEACHING HOSPITALS (WEST BENGAL) ---

        // 12. Vidyasagar State General Hospital Behala
        saveHospital(new Hospital(
                "Vidyasagar State General Hospital", "Government State General Hospital",
                "Diamond Harbour Road, Behala Chowrasta, Kolkata 700034", "Kolkata", "West Bengal",
                22.4950, 88.3150, "+91 33 2446 6200", "102 / 033-24466200", 30, 9, 250, 48
        ));

        // 13. SSKM Hospital & IPGMER Kolkata
        saveHospital(new Hospital(
                "SSKM Hospital & IPGMER Kolkata", "Government Apex Super-Speciality",
                "244, AJC Bose Road, Bhowanipore, Kolkata, West Bengal 700020", "Kolkata", "West Bengal",
                22.5385, 88.3429, "+91 33 2223 1589", "102 / 033-22231589", 60, 15, 1800, 220
        ));

        // 14. Medical College & Hospital Kolkata
        saveHospital(new Hospital(
                "Medical College & Hospital, Kolkata", "Premier Government Teaching Hospital",
                "88, College Street, Bowbazar, Kolkata, West Bengal 700073", "Kolkata", "West Bengal",
                22.5735, 88.3630, "+91 33 2255 1621", "102 / 033-22551633", 45, 10, 1500, 180
        ));

        // 15. Nil Ratan Sircar (NRS) Medical College & Hospital
        saveHospital(new Hospital(
                "Nil Ratan Sircar (NRS) Medical College & Hospital", "Government Apex Super-Speciality",
                "138, AJC Bose Road, Sealdah, Raja Bazar, Kolkata 700014", "Kolkata", "West Bengal",
                22.5645, 88.3712, "+91 33 2286 0033", "102 / 033-22860033", 50, 14, 1600, 195
        ));

        // 16. R. G. Kar Medical College & Hospital
        saveHospital(new Hospital(
                "R. G. Kar Medical College & Hospital", "Premier Government Teaching Hospital",
                "1, Khudiram Bose Sarani, Bidhan Sarani, Shyam Bazar, Kolkata 700004", "Kolkata", "West Bengal",
                22.6044, 88.3745, "+91 33 2555 7656", "102 / 033-25557656", 45, 11, 1400, 170
        ));

        // 17. Calcutta National Medical College & Hospital (CNMC)
        saveHospital(new Hospital(
                "Calcutta National Medical College & Hospital", "Government Teaching Hospital & ER",
                "32, Gorachand Road, Beniapukur, Kolkata 700014", "Kolkata", "West Bengal",
                22.5420, 88.3720, "+91 33 2284 4834", "102 / 033-22844834", 38, 9, 1200, 145
        ));

        // 18. Chittaranjan National Cancer Institute (CNCI)
        saveHospital(new Hospital(
                "Chittaranjan National Cancer Institute (CNCI)", "Government Apex Cancer Institute",
                "Street No. 299, Plot No. DJ-01, Action Area I, New Town, Kolkata 700156", "New Town", "West Bengal",
                22.5830, 88.4710, "+91 33 2324 0050", "033-23240050", 40, 10, 460, 68
        ));

        // --- PREMIER PRIVATE SUPER-SPECIALITY HOSPITALS ---

        // 19. Apollo Multispeciality Hospitals Kolkata
        saveHospital(new Hospital(
                "Apollo Multispeciality Hospitals Kolkata", "Private Super-Speciality Hospital",
                "58, Canal Circular Road, Kadapara, Phool Bagan, Kankurgachi, Kolkata 700054", "Kolkata", "West Bengal",
                22.5744, 88.3995, "+91 33 2320 3040", "1066 / 033-23202122", 95, 24, 550, 88
        ));

        // 20. Fortis Hospital Anandapur Kolkata
        saveHospital(new Hospital(
                "Fortis Hospital Anandapur", "Private Super-Speciality Hospital",
                "730, Anandapur, E.M. Bypass Road, Kolkata, West Bengal 700107", "Kolkata", "West Bengal",
                22.5186, 88.4011, "+91 33 6628 4444", "033-66284100", 70, 18, 400, 62
        ));

        // 21. Tata Medical Center New Town Kolkata
        saveHospital(new Hospital(
                "Tata Medical Center", "Private Super-Speciality Hospital",
                "14, Major Arterial Road (DH Block), Action Area I, New Town, Kolkata 700156", "New Town", "West Bengal",
                22.5958, 88.4795, "+91 33 6605 7000", "033-66057222", 40, 8, 450, 55
        ));

        // 22. Medica Superspecialty Hospital Mukundapur
        saveHospital(new Hospital(
                "Medica Superspecialty Hospital", "Private Super-Speciality Hospital",
                "127, Mukundapur, E.M. Bypass, Kolkata, West Bengal 700099", "Kolkata", "West Bengal",
                22.4988, 88.3991, "+91 33 6652 0000", "033-66520100", 80, 20, 500, 74
        ));

        // 23. B.M. Birla Heart Research Centre Alipore
        saveHospital(new Hospital(
                "B.M. Birla Heart Research Centre", "Private Super-Speciality Hospital",
                "1/1, National Library Avenue, Alipore, Kolkata 700027", "Kolkata", "West Bengal",
                22.5310, 88.3305, "+91 33 4088 4088", "033-40884000", 65, 16, 210, 36
        ));

        // 24. RTIICS Narayana Health Kolkata
        saveHospital(new Hospital(
                "RTIICS Narayana Health Kolkata", "Private Super-Speciality Hospital",
                "124, Mukundapur, E.M. Bypass, Kolkata, West Bengal 700099", "Kolkata", "West Bengal",
                22.4975, 88.4002, "+91 33 7122 2222", "033-71222100", 90, 22, 650, 95
        ));

        // 25. Ruby General Hospital
        saveHospital(new Hospital(
                "Ruby General Hospital", "Private Super-Speciality Hospital",
                "Kasba Golpark, E.M. Bypass, Sector I, Kolkata, West Bengal 700107", "Kolkata", "West Bengal",
                22.5130, 88.4008, "+91 33 3987 1800", "033-39871888", 40, 12, 320, 50
        ));

        // 26. Woodlands Multispeciality Hospital Alipore
        saveHospital(new Hospital(
                "Woodlands Multispeciality Hospital", "Private Super-Speciality Hospital",
                "8/5, Alipore Road, Alipore, Kolkata, West Bengal 700027", "Kolkata", "West Bengal",
                22.5332, 88.3310, "+91 33 4033 7000", "033-40337033", 35, 9, 240, 40
        ));

        // 27. Belle Vue Clinic
        saveHospital(new Hospital(
                "Belle Vue Clinic", "Private Super-Speciality Hospital",
                "9, Dr. U. N. Brahmachari Street, Elgin, Kolkata, West Bengal 700017", "Kolkata", "West Bengal",
                22.5454, 88.3551, "+91 33 6688 8888", "033-66888800", 40, 10, 280, 42
        ));

        // 28. Peerless Hospitex Hospital Panchasayar
        saveHospital(new Hospital(
                "Peerless Hospitex Hospital & Research", "Private Super-Speciality Hospital",
                "360, Panchasayar, Garia, Kolkata, West Bengal 700094", "Kolkata", "West Bengal",
                22.4831, 88.3970, "+91 33 4011 1222", "033-40111200", 50, 13, 400, 60
        ));

        // 29. Desun Hospital
        saveHospital(new Hospital(
                "Desun Hospital Kolkata", "Private Super-Speciality Hospital",
                "720, Anandapur, Desun More, Kolkata, West Bengal 700107", "Kolkata", "West Bengal",
                22.5145, 88.4012, "+91 33 7122 2000", "033-71222000", 85, 20, 300, 45
        ));

        // 30. Narayana Superspeciality Hospital Howrah
        saveHospital(new Hospital(
                "Narayana Superspeciality Hospital Howrah", "Private Super-Speciality Hospital",
                "Chunavate, Near Nabanna, Andul Road, Howrah, West Bengal 711109", "Howrah", "West Bengal",
                22.5658, 88.3032, "+91 33 7122 2222", "033-71222111", 60, 16, 300, 52
        ));

        // 31. Charnock Hospital VIP Road
        saveHospital(new Hospital(
                "Charnock Hospital", "Private Super-Speciality Hospital",
                "BMC 195, Biswa Bangla Sarani, Teghoria, VIP Road, Kolkata 700052", "Kolkata", "West Bengal",
                22.6280, 88.4320, "+91 33 4050 0500", "033-40500555", 45, 12, 280, 46
        ));

        // 32. AMRI Hospital Salt Lake
        saveHospital(new Hospital(
                "AMRI Hospital Salt Lake (Manipal)", "Private Super-Speciality Hospital",
                "JC-16 & 17, Sector III, Salt Lake City, Bidhannagar, Kolkata 700098", "Salt Lake", "West Bengal",
                22.5702, 88.4140, "+91 33 6680 0000", "033-66800100", 45, 11, 220, 38
        ));

        // 33. The Mission Hospital Durgapur
        saveHospital(new Hospital(
                "The Mission Hospital Durgapur", "Private Super-Speciality Hospital",
                "Sector 2C, Bidhannagar, Durgapur, West Bengal 713212", "Durgapur", "West Bengal",
                23.5204, 87.3119, "+91 343 253 5555", "0343-2535500", 50, 14, 350, 60
        ));

        // 34. Neotia Getwel Siliguri
        saveHospital(new Hospital(
                "Neotia Getwel Multispecialty Hospital", "Private Super-Speciality Hospital",
                "Uttorayon Township, NH 31, Matigara, Siliguri, West Bengal 734010", "Siliguri", "West Bengal",
                26.7271, 88.3953, "+91 353 660 3000", "0353-6603099", 35, 9, 250, 45
        ));

        // 35. AIIMS New Delhi (National Reference)
        saveHospital(new Hospital(
                "AIIMS New Delhi", "Apex Autonomous Government Institute",
                "Sri Aurobindo Marg, Ansari Nagar, New Delhi 110029", "Delhi", "Delhi",
                28.5672, 77.2100, "+91 11 26588500", "011-26588700", 90, 18, 2200, 240
        ));

        // 36. Safdarjung Hospital & VMMC Delhi
        saveHospital(new Hospital(
                "Safdarjung Hospital & VMMC", "Government Super-Speciality Hospital",
                "Ring Road, Opposite AIIMS, New Delhi 110029", "Delhi", "Delhi",
                28.5708, 77.2078, "+91 11 26707444", "011-26707400", 80, 15, 1800, 210
        ));

        // 37. Fortis Escorts Heart Institute Delhi
        saveHospital(new Hospital(
                "Fortis Escorts Heart Institute", "Private Super-Speciality Hospital",
                "Okhla Road, Sukhdev Vihar Metro Station, New Delhi 110025", "Delhi", "Delhi",
                28.5603, 77.2789, "+91 11 47135000", "011-47135100", 55, 12, 310, 48
        ));

        // 38. Max Super Speciality Hospital Saket Delhi
        saveHospital(new Hospital(
                "Max Super Speciality Hospital Saket", "Private Super-Speciality Hospital",
                "1, 2 Press Enclave Marg, Saket Institutional Area, New Delhi 110017", "Delhi", "Delhi",
                28.5273, 77.2125, "+91 11 26515050", "011-26515055", 70, 16, 500, 68
        ));

        // 39. Indraprastha Apollo Hospitals Delhi
        saveHospital(new Hospital(
                "Indraprastha Apollo Hospitals", "Private Super-Speciality Hospital",
                "Sarita Vihar, Delhi Mathura Road, New Delhi 110076", "Delhi", "Delhi",
                28.5393, 77.2832, "+91 11 26925858", "011-26925801", 85, 20, 710, 85
        ));

        // 40. Sir Ganga Ram Hospital Delhi
        saveHospital(new Hospital(
                "Sir Ganga Ram Hospital", "Premier Multi-Speciality Hospital",
                "Sir Ganga Ram Hospital Marg, Old Rajinder Nagar, New Delhi 110060", "Delhi", "Delhi",
                28.6385, 77.1895, "+91 11 25750000", "011-25861463", 75, 14, 675, 72
        ));

        // 41. Medanta - The Medicity (Delhi NCR)
        saveHospital(new Hospital(
                "Medanta - The Medicity", "Quaternary Super-Speciality Hospital",
                "CH Bakhtawar Singh Rd, Sector 38, Gurugram, Delhi NCR 122001", "Delhi", "Delhi",
                28.4394, 77.0425, "+91 124 4141414", "1068 / 0124-4141414", 120, 28, 1250, 160
        ));

        // --- MUMBAI PREMIER HOSPITALS ---
        // 42. Tata Memorial Hospital Mumbai
        saveHospital(new Hospital(
                "Tata Memorial Hospital", "Apex Cancer Institute & Research Centre",
                "Dr E Borges Road, Parel, Mumbai, Maharashtra 400012", "Mumbai", "Maharashtra",
                19.0048, 72.8432, "+91 22 24177000", "022-24177070", 65, 14, 700, 80
        ));

        // 43. Lilavati Hospital and Research Centre Mumbai
        saveHospital(new Hospital(
                "Lilavati Hospital and Research Centre", "Private Super-Speciality Hospital",
                "A-791, Bandra Reclamation, Bandra West, Mumbai 400050", "Mumbai", "Maharashtra",
                19.0514, 72.8290, "+91 22 26751000", "022-26568000", 60, 15, 323, 44
        ));

        // 44. Kokilaben Dhirubhai Ambani Hospital Mumbai
        saveHospital(new Hospital(
                "Kokilaben Dhirubhai Ambani Hospital", "Quaternary Care Multi-Speciality",
                "Rao Saheb Achutrao Patwardhan Marg, Four Bungalows, Andheri West, Mumbai 400053", "Mumbai", "Maharashtra",
                19.1314, 72.8251, "+91 22 42696969", "022-42699999", 90, 22, 750, 92
        ));

        // 45. P. D. Hinduja National Hospital Mumbai
        saveHospital(new Hospital(
                "P. D. Hinduja National Hospital", "Premier Multi-Speciality Hospital",
                "Veer Savarkar Marg, Mahim West, Mumbai, Maharashtra 400016", "Mumbai", "Maharashtra",
                19.0330, 72.8396, "+91 22 24451515", "022-24447000", 55, 12, 400, 50
        ));

        // 46. Breach Candy Hospital Mumbai
        saveHospital(new Hospital(
                "Breach Candy Hospital", "Premier Super-Speciality Hospital",
                "60 A, Bhulabhai Desai Marg, Breach Candy, Cumballa Hill, Mumbai 400026", "Mumbai", "Maharashtra",
                18.9716, 72.8052, "+91 22 23667788", "022-23667800", 40, 10, 210, 32
        ));

        // 47. Sir H. N. Reliance Foundation Hospital Mumbai
        saveHospital(new Hospital(
                "Sir H. N. Reliance Foundation Hospital", "Private Super-Speciality Hospital",
                "Raja Rammohan Roy Rd, Prarthana Samaj, Girgaon, Mumbai 400004", "Mumbai", "Maharashtra",
                18.9567, 72.8198, "+91 22 61305000", "022-61305005", 50, 14, 345, 48
        ));

        // 48. Fortis Hospital Mulund Mumbai
        saveHospital(new Hospital(
                "Fortis Hospital Mulund", "Private Super-Speciality Hospital",
                "Mulund Goregaon Link Road, Industrial Area, Bhandup West, Mumbai 400078", "Mumbai", "Maharashtra",
                19.1648, 72.9360, "+91 22 49254444", "022-49254100", 45, 11, 315, 42
        ));

        // --- BENGALURU & BHUBANESWAR HOSPITALS ---
        // 49. Manipal Hospital Old Airport Road Bengaluru
        saveHospital(new Hospital(
                "Manipal Hospital Old Airport Road", "Private Super-Speciality Hospital",
                "98, HAL Old Airport Rd, Kodihalli, Bengaluru, Karnataka 560017", "Bengaluru", "Karnataka",
                12.9592, 77.6534, "+91 80 25024444", "080-25023344", 60, 15, 600, 75
        ));

        // 50. Narayana Health City Bengaluru
        saveHospital(new Hospital(
                "Narayana Health City", "Cardiac & Quaternary Care Institute",
                "258/A, Bommasandra Industrial Area, Anekal Taluk, Bengaluru 560099", "Bengaluru", "Karnataka",
                12.8222, 77.6895, "+91 80 71222222", "080-71222111", 100, 25, 1400, 180
        ));

        // 51. AIIMS Bhubaneswar
        saveHospital(new Hospital(
                "AIIMS Bhubaneswar", "Apex Autonomous Government Institute",
                "Sijua, Patrapada, Bhubaneswar, Odisha 751019", "Bhubaneswar", "Odisha",
                20.2312, 85.7745, "+91 674 2476789", "0674-2476999", 70, 16, 1000, 140
        ));

        // 52. Apollo Hospitals Bhubaneswar
        saveHospital(new Hospital(
                "Apollo Hospitals Bhubaneswar", "Quaternary Care Multi-Speciality",
                "Plot No. 251, Sainik School Road, Unit 15, Bhubaneswar, Odisha 751005", "Bhubaneswar", "Odisha",
                20.3087, 85.8331, "+91 674 6661066", "0674-6661016", 60, 15, 350, 48
        ));

        // 53. KIMS Super Speciality Hospital Bhubaneswar
        saveHospital(new Hospital(
                "KIMS Super Speciality Hospital", "Premier Teaching & Super-Speciality Hospital",
                "KIIT Campus 5, Patia, Bhubaneswar, Odisha 751024", "Bhubaneswar", "Odisha",
                20.3533, 85.8197, "+91 674 7105300", "0674-7105301", 85, 20, 1600, 190
        ));

        // 54. SUM Ultimate Medicare Bhubaneswar
        saveHospital(new Hospital(
                "SUM Ultimate Medicare", "Next-Gen Quaternary Healthcare Institute",
                "K-8, Kalinga Nagar, Ghatikia, Bhubaneswar, Odisha 751003", "Bhubaneswar", "Odisha",
                20.2818, 85.7674, "+91 674 3500500", "0674-3500555", 75, 18, 375, 52
        ));

        // 55. Fortis Hospital Bannerghatta Road Bengaluru
        saveHospital(new Hospital(
                "Fortis Hospital Bannerghatta Road", "Super-Speciality Tertiary Care Hospital",
                "154/9, Bannerghatta Rd, Opposite IIM-B, Sahyadri Layout, Bengaluru 560076", "Bengaluru", "Karnataka",
                12.8938, 77.5978, "+91 80 66214444", "080-66214100", 55, 14, 284, 38
        ));

        // 56. Aster CMI Hospital Hebbal Bengaluru
        saveHospital(new Hospital(
                "Aster CMI Hospital Hebbal", "Quaternary Care Multi-Speciality Hospital",
                "No. 43/42, NH 44, Sahakar Nagar, Hebbal, Bengaluru, Karnataka 560092", "Bengaluru", "Karnataka",
                13.0587, 77.5925, "+91 80 43420100", "080-43420111", 65, 16, 500, 68
        ));

        // 57. Apollo Hospitals Greams Road Chennai
        saveHospital(new Hospital(
                "Apollo Hospitals Greams Road", "Flagship Quaternary Care Hospital",
                "21 Greams Lane, Off Greams Road, Thousand Lights, Chennai, Tamil Nadu 600006", "Chennai", "Tamil Nadu",
                13.0604, 80.2508, "+91 44 28290200", "1066 / 044-28293333", 80, 20, 600, 85
        ));

        // 58. Apollo Health City Jubilee Hills Hyderabad
        saveHospital(new Hospital(
                "Apollo Health City Jubilee Hills", "Premier Integrated Healthcare City",
                "Road No. 72, Opposite Bharatiya Vidya Bhavan School, Jubilee Hills, Hyderabad 500033", "Hyderabad", "Telangana",
                17.4168, 78.4116, "+91 40 23607777", "1066 / 040-23607788", 90, 24, 550, 78
        ));

        // 59. Ruby Hall Clinic Pune
        saveHospital(new Hospital(
                "Ruby Hall Clinic", "Multi-Speciality Tertiary Care Centre",
                "40, Sassoon Road, Sangamvadi, Pune, Maharashtra 411001", "Pune", "Maharashtra",
                18.5314, 73.8776, "+91 20 66455100", "020-66455111", 70, 18, 595, 72
        ));

        // --- TELANGANA / HYDERABAD (GOVERNMENT & PREMIER PRIVATE HOSPITALS) ---

        // 60. Nizam's Institute of Medical Sciences (NIMS) - Government Apex
        saveHospital(new Hospital(
                "Nizam's Institute of Medical Sciences (NIMS)", "Government Apex Super-Speciality Institute",
                "Punjagutta Main Road, Somajiguda, Hyderabad, Telangana 500082", "Hyderabad", "Telangana",
                17.4239, 78.4529, "+91 40 2348 9000", "040-23489100 / 108", 45, 14, 1400, 120
        ));

        // 61. Osmania General Hospital - Government Apex
        saveHospital(new Hospital(
                "Osmania General Hospital", "Government Apex Tertiary Teaching Hospital",
                "Afzal Gunj, High Court Road, Old City, Hyderabad, Telangana 500012", "Hyderabad", "Telangana",
                17.3685, 78.4735, "+91 40 2460 0121", "108 / 040-24600122", 40, 12, 1168, 98
        ));

        // 62. Gandhi Hospital & Medical College - Government
        saveHospital(new Hospital(
                "Gandhi Hospital & Medical College", "Government Tertiary Care & Trauma Hospital",
                "Musheerabad, Padmarao Nagar, Secunderabad, Hyderabad, Telangana 500003", "Hyderabad", "Telangana",
                17.4246, 78.5034, "+91 40 2750 5566", "040-27505567 / 108", 38, 10, 1200, 85
        ));

        // 63. MNJ Institute of Oncology & Regional Cancer Centre - Government
        saveHospital(new Hospital(
                "MNJ Institute of Oncology & Regional Cancer Centre", "Government Regional Cancer Centre & Research Institute",
                "Red Hills, Lakdikapul, Hyderabad, Telangana 500004", "Hyderabad", "Telangana",
                17.3995, 78.4612, "+91 40 2331 4458", "040-23314459", 20, 6, 450, 38
        ));

        // 64. Government Maternity Hospital Nayapul - Government
        saveHospital(new Hospital(
                "Government Maternity Hospital (Nayapul)", "Government Apex Mother & Child Hospital",
                "Nayapul, High Court Road, Petlaburj, Hyderabad, Telangana 500002", "Hyderabad", "Telangana",
                17.3621, 78.4719, "+91 40 2452 4001", "040-24524002", 16, 5, 600, 52
        ));

        // 65. AIG Hospitals (Asian Institute of Gastroenterology) Gachibowli - Private
        saveHospital(new Hospital(
                "AIG Hospitals", "Private Quaternary Care & Organ Transplant Hospital",
                "1-66/AIG/2 to 5, Mindspace Road, Gachibowli, Hyderabad, Telangana 500032", "Hyderabad", "Telangana",
                17.4422, 78.3625, "+91 40 4244 4222", "040-42444244 / 1066", 75, 24, 800, 82
        ));

        // 66. KIMS Hospitals (Krishna Institute of Medical Sciences) Begumpet - Private
        saveHospital(new Hospital(
                "KIMS Hospitals Begumpet", "Private Super-Speciality & Quaternary Care Hospital",
                "1-8-31/1, Minister Road, Krishna Nagar Colony, Begumpet, Secunderabad, Telangana 500003", "Hyderabad", "Telangana",
                17.4363, 78.4878, "+91 40 4488 5000", "040-44885100 / 1066", 80, 26, 1000, 95
        ));

        // 67. Yashoda Hospitals Somajiguda - Private
        saveHospital(new Hospital(
                "Yashoda Hospitals Somajiguda", "Private Tertiary & Super-Speciality Hospital",
                "Raj Bhavan Road, Somajiguda, Hyderabad, Telangana 500082", "Hyderabad", "Telangana",
                17.4258, 78.4568, "+91 40 4567 4567", "040-45674568 / 1066", 60, 18, 750, 70
        ));

        // 68. Care Hospitals Banjara Hills - Private
        saveHospital(new Hospital(
                "Care Hospitals Banjara Hills", "Private Multi-Speciality Cardiac & Critical Care Hospital",
                "Road No. 1, Banjara Hills, Hyderabad, Telangana 500034", "Hyderabad", "Telangana",
                17.4156, 78.4485, "+91 40 6165 6565", "040-61656666", 50, 16, 435, 52
        ));

        // 69. Continental Hospitals Financial District - Private
        saveHospital(new Hospital(
                "Continental Hospitals", "Private Quaternary Care JCI-Accredited Hospital",
                "Plot No. 3, Road No. 2, Financial District, Nanakramguda, Gachibowli, Hyderabad, Telangana 500032", "Hyderabad", "Telangana",
                17.4182, 78.3475, "+91 40 6700 0000", "040-67000100", 55, 18, 750, 68
        ));

        // 70. Rainbow Children's Hospital Banjara Hills - Private
        saveHospital(new Hospital(
                "Rainbow Children's Hospital & BirthRight", "Private Pediatric & Perinatal Super-Speciality Hospital",
                "Road No. 2, Banjara Hills, Hyderabad, Telangana 500034", "Hyderabad", "Telangana",
                17.4205, 78.4348, "+91 40 4466 5555", "1800 2122", 30, 10, 250, 35
        ));

        // --- TAMIL NADU / CHENNAI (GOVERNMENT & PREMIER PRIVATE HOSPITALS) ---

        // 71. Rajiv Gandhi Government General Hospital (Madras Medical College) - Government Apex
        saveHospital(new Hospital(
                "Rajiv Gandhi Government General Hospital", "Government Apex Teaching Hospital & Multi-Trauma Center",
                "EVR Periyar Salai, Park Town, Chennai, Tamil Nadu 600003", "Chennai", "Tamil Nadu",
                13.0805, 80.2785, "+91 44 2530 5000", "108 / 044-25305100", 65, 18, 2722, 230
        ));

        // 72. Tamil Nadu Government Multi Super Speciality Hospital (Omandurar) - Government Apex
        saveHospital(new Hospital(
                "Tamil Nadu Govt Multi Super Speciality Hospital", "Government Apex Quaternary Super-Speciality Institute",
                "Omandurar Government Estate, Anna Salai, Chennai, Tamil Nadu 600002", "Chennai", "Tamil Nadu",
                13.0683, 80.2741, "+91 44 2566 6000", "108 / 044-25666100", 50, 15, 400, 48
        ));

        // 73. Government Stanley Medical College Hospital - Government
        saveHospital(new Hospital(
                "Government Stanley Hospital & Medical College", "Government Tertiary Care & Trauma Teaching Hospital",
                "Old Jail Road, Royapuram, Chennai, Tamil Nadu 600001", "Chennai", "Tamil Nadu",
                13.1075, 80.2878, "+91 44 2528 0900", "108 / 044-25280901", 45, 12, 1280, 110
        ));

        // 74. Government Kilpauk Medical College Hospital - Government
        saveHospital(new Hospital(
                "Government Kilpauk Medical College Hospital", "Government Tertiary Care & Apex Burns/Trauma Center",
                "822, Poonamallee High Road, Kilpauk, Chennai, Tamil Nadu 600010", "Chennai", "Tamil Nadu",
                13.0800, 80.2420, "+91 44 2836 4951", "108 / 044-28364952", 35, 10, 850, 75
        ));

        // 75. Government Hospital for Women and Children Egmore - Government
        saveHospital(new Hospital(
                "Government Hospital for Women and Children (Egmore)", "Government Apex Maternity & Pediatric Hospital",
                "Police Commissioner Office Road, Egmore, Chennai, Tamil Nadu 600008", "Chennai", "Tamil Nadu",
                13.0762, 80.2588, "+91 44 2819 1982", "108 / 044-28191983", 30, 8, 900, 80
        ));

        // 76. SIMS Hospital (SRM Institutes for Medical Science) Vadapalani - Private
        saveHospital(new Hospital(
                "SIMS Hospital Vadapalani", "Private Quaternary Multi-Speciality Hospital",
                "No. 1, Jawaharlal Nehru Salai, Vadapalani, Chennai, Tamil Nadu 600026", "Chennai", "Tamil Nadu",
                13.0505, 80.2105, "+91 44 4567 4567", "044-20002020 / 1066", 60, 18, 345, 42
        ));

        // 77. MIOT International (Madras Institute of Orthopaedics) Manapakkam - Private
        saveHospital(new Hospital(
                "MIOT International", "Private Quaternary Orthopedics & Multi-Speciality Hospital",
                "4/112, Mount Poonamallee High Rd, Manapakkam, Chennai, Tamil Nadu 600089", "Chennai", "Tamil Nadu",
                13.0232, 80.1788, "+91 44 4200 2288", "044-22492288", 70, 22, 1000, 95
        ));

        // 78. Fortis Malar Hospital Adyar - Private
        saveHospital(new Hospital(
                "Fortis Malar Hospital", "Private Super-Speciality Cardiac & Critical Care Hospital",
                "No. 52, 1st Main Rd, Gandhi Nagar, Adyar, Chennai, Tamil Nadu 600020", "Chennai", "Tamil Nadu",
                13.0062, 80.2575, "+91 44 4289 2222", "044-42892100", 40, 12, 180, 24
        ));

        // 79. MGM Healthcare Aminjikarai - Private
        saveHospital(new Hospital(
                "MGM Healthcare", "Private Quaternary Heart & Lung Transplant Institute",
                "No. 72, Nelson Manickam Road, Aminjikarai, Chennai, Tamil Nadu 600029", "Chennai", "Tamil Nadu",
                13.0725, 80.2185, "+91 44 4524 2424", "044-45242400", 75, 24, 400, 52
        ));

        // 80. Gleneagles Global Health City Perumbakkam - Private
        saveHospital(new Hospital(
                "Gleneagles Global Health City", "Private Quaternary Multi-Organ Transplant Hospital",
                "439, Cheran Nagar, Perumbakkam, Chennai, Tamil Nadu 600100", "Chennai", "Tamil Nadu",
                12.9068, 80.1982, "+91 44 4477 7000", "044-44777100", 80, 25, 1000, 110
        ));

        // 81. Dr. Rela Institute & Medical Centre Chromepet - Private
        saveHospital(new Hospital(
                "Dr. Rela Institute & Medical Centre", "Private Quaternary Liver Disease & Transplant Institute",
                "No. 7, CLC Works Rd, Nagappa Nagar, Chromepet, Chennai, Tamil Nadu 600044", "Chennai", "Tamil Nadu",
                12.9555, 80.1415, "+91 44 6666 7777", "044-66667788", 65, 20, 450, 48
        ));

        // 82. Prashanth Super Speciality Hospital Velachery - Private
        saveHospital(new Hospital(
                "Prashanth Super Speciality Hospital", "Private Tertiary Surgery & Advanced Medical Care",
                "No. 36 & 36A, Velachery Main Road, Velachery, Chennai, Tamil Nadu 600042", "Chennai", "Tamil Nadu",
                12.9815, 80.2180, "+91 44 4680 5555", "044-46805500", 35, 11, 200, 32
        ));
    }

    private void seedDoctors() {
        doctorRepository.deleteAll();
        doctorMongoRepository.deleteAll();

        // --- LOCAL BUDGE BUDGE & SOUTH 24 PARGANAS FACILITIES ROSTER ---

        // 1. Budge Budge Municipal Hospital (Government) - Daily Staggered Attending Doctors
        Doctor bbmh1 = new Doctor("Dr. Ratan Dasgupta", "General Medicine", "Emergency Trauma & Family Medicine",
                "MBBS, MD (General Medicine)", 18, "English, Bengali, Hindi", 20.0,
                "Budge Budge Municipal Hospital", "Budge Budge", "West Bengal", 22.4785, 88.1750);
        bbmh1.withOpdDetails("Chamber 101", "Daily (Mon to Sun)", "09:00 AM - 10:30 AM", 20.0);
        saveDoctor(bbmh1);

        Doctor bbmh2 = new Doctor("Dr. Sunita Roy", "Gynecology", "Maternity, Normal Delivery & Antenatal Care",
                "MBBS, DGO, MS (Obstetrics & Gynecology)", 15, "English, Bengali, Hindi", 20.0,
                "Budge Budge Municipal Hospital", "Budge Budge", "West Bengal", 22.4785, 88.1750);
        bbmh2.withOpdDetails("Chamber 102", "Daily (Mon to Sun)", "10:30 AM - 12:00 PM", 20.0);
        saveDoctor(bbmh2);

        Doctor bbmh3 = new Doctor("Dr. Bikash Samanta", "Pediatrics", "Child Health, Immunization & Primary Pediatrics",
                "MBBS, DCH, DNB (Pediatrics)", 13, "English, Bengali, Hindi", 20.0,
                "Budge Budge Municipal Hospital", "Budge Budge", "West Bengal", 22.4785, 88.1750);
        bbmh3.withOpdDetails("Chamber 103", "Daily (Mon to Sun)", "12:00 PM - 01:30 PM", 20.0);
        saveDoctor(bbmh3);

        Doctor bbmh4 = new Doctor("Dr. Prasanta Ganguly", "Orthopedics", "Bone, Joint Trauma & Arthritis Management",
                "MBBS, MS (Orthopedics)", 16, "English, Bengali, Hindi", 20.0,
                "Budge Budge Municipal Hospital", "Budge Budge", "West Bengal", 22.4785, 88.1750);
        bbmh4.withOpdDetails("Chamber 104", "Daily (Mon to Sun)", "04:00 PM - 05:30 PM", 20.0);
        saveDoctor(bbmh4);

        Doctor bbmh5 = new Doctor("Dr. Abhishek De", "Dermatology", "Clinical Dermatology, Allergy & Skin Infections",
                "MBBS, MD (Dermatology)", 15, "English, Bengali, Hindi", 20.0,
                "Budge Budge Municipal Hospital", "Budge Budge", "West Bengal", 22.4785, 88.1750);
        bbmh5.withOpdDetails("Chamber 105", "Daily (Mon to Sun)", "05:30 PM - 07:00 PM", 20.0);
        saveDoctor(bbmh5);

        // 2. ESI Hospital Budge Budge (Government Super-Speciality) - Daily Staggered Attending Doctors
        Doctor esi1 = new Doctor("Dr. Soumen Roy", "General Medicine", "Internal Medicine, Diabetes & Fever Clinic",
                "MBBS, MD (General Medicine)", 14, "English, Bengali, Hindi", 20.0,
                "ESI Hospital Budge Budge", "Budge Budge", "West Bengal", 22.4820, 88.1812);
        esi1.withOpdDetails("Chamber 101", "Daily (Mon to Sun)", "09:00 AM - 10:30 AM", 20.0);
        saveDoctor(esi1);

        Doctor esi2 = new Doctor("Dr. Sharmistha Sen", "Gynecology", "High-Risk Pregnancy & Laparoscopic Gynae",
                "MBBS, DGO, MD, MRCOG (UK)", 17, "English, Bengali, Hindi", 20.0,
                "ESI Hospital Budge Budge", "Budge Budge", "West Bengal", 22.4820, 88.1812);
        esi2.withOpdDetails("Chamber 102", "Daily (Mon to Sun)", "10:30 AM - 12:00 PM", 20.0);
        saveDoctor(esi2);

        Doctor esi3 = new Doctor("Dr. Arpita Das", "Pediatrics", "Child Health, Immunization & Neonatology",
                "MBBS, DCH, DNB (Pediatrics)", 12, "English, Bengali, Hindi", 20.0,
                "ESI Hospital Budge Budge", "Budge Budge", "West Bengal", 22.4820, 88.1812);
        esi3.withOpdDetails("Chamber 103", "Daily (Mon to Sun)", "12:00 PM - 01:30 PM", 20.0);
        saveDoctor(esi3);

        Doctor esi4 = new Doctor("Dr. Kalyan Chakraborty", "General Surgery", "Laparoscopy, Trauma & Emergency Surgery",
                "MBBS, MS (General Surgery)", 19, "English, Bengali, Hindi", 20.0,
                "ESI Hospital Budge Budge", "Budge Budge", "West Bengal", 22.4820, 88.1812);
        esi4.withOpdDetails("Chamber 104", "Daily (Mon to Sun)", "04:00 PM - 05:30 PM", 20.0);
        saveDoctor(esi4);

        Doctor esi5 = new Doctor("Dr. Shantanu Panja", "ENT", "Micro-Ear Surgery & Sinus Endoscopy",
                "MBBS, MS (ENT), DNB, FRCS", 19, "English, Bengali, Hindi", 20.0,
                "ESI Hospital Budge Budge", "Budge Budge", "West Bengal", 22.4820, 88.1812);
        esi5.withOpdDetails("Chamber 105", "Daily (Mon to Sun)", "05:30 PM - 07:00 PM", 20.0);
        saveDoctor(esi5);

        // 3. Budge Budge Care Nursing Home (Private Nursing Home) - Staggered Visiting Doctors
        Doctor bbc1 = new Doctor("Dr. Anita Banerjee", "Gynecology", "Maternity, Normal Delivery & Women Wellness",
                "MBBS, DGO, MS (Obstetrics & Gynecology)", 14, "English, Bengali, Hindi", 500.0,
                "Budge Budge Care Nursing Home", "Budge Budge", "West Bengal", 22.4810, 88.1830);
        bbc1.withOpdDetails("Room 201", "Daily (Mon to Sun)", "09:00 AM - 10:30 AM", 500.0);
        saveDoctor(bbc1);

        Doctor bbc2 = new Doctor("Dr. Priya Mukherjee", "Pediatrics", "Neonatal Care & Pediatric Immunization",
                "MBBS, MD (Pediatrics), DCH", 13, "English, Bengali, Hindi", 500.0,
                "Budge Budge Care Nursing Home", "Budge Budge", "West Bengal", 22.4810, 88.1830);
        bbc2.withOpdDetails("Room 202", "Daily (Mon to Sun)", "10:30 AM - 12:00 PM", 500.0);
        saveDoctor(bbc2);

        Doctor bbc3 = new Doctor("Dr. Soumen Roy (Visiting)", "General Medicine", "Internal Medicine & Chronic Disease",
                "MBBS, MD (General Medicine)", 14, "English, Bengali, Hindi", 550.0,
                "Budge Budge Care Nursing Home", "Budge Budge", "West Bengal", 22.4810, 88.1830);
        bbc3.withOpdDetails("Room 203", "Daily (Mon to Sun)", "04:00 PM - 05:30 PM", 550.0);
        saveDoctor(bbc3);

        Doctor bbc4 = new Doctor("Dr. Kalyan Chakraborty (Visiting)", "General Surgery", "Day Care Hernia & Laparoscopic Procedures",
                "MBBS, MS (General Surgery)", 19, "English, Bengali, Hindi", 600.0,
                "Budge Budge Care Nursing Home", "Budge Budge", "West Bengal", 22.4810, 88.1830);
        bbc4.withOpdDetails("Room 204", "Daily (Mon to Sun)", "05:30 PM - 07:00 PM", 600.0);
        saveDoctor(bbc4);

        // 4. Mother & Child Care Nursing Home (Private Nursing Home)
        Doctor mcc1 = new Doctor("Dr. Priya Mukherjee (M&C)", "Pediatrics", "Neonatal Care & Pediatric Growth Clinic",
                "MBBS, MD (Pediatrics), DCH", 13, "English, Bengali, Hindi", 500.0,
                "Mother & Child Care Nursing Home", "Budge Budge", "West Bengal", 22.4910, 88.2450);
        mcc1.withOpdDetails("Room 101", "Daily (Mon to Sun)", "09:00 AM - 10:30 AM", 500.0);
        saveDoctor(mcc1);

        Doctor mcc2 = new Doctor("Dr. Anita Banerjee (M&C)", "Gynecology", "Maternity & Normal Delivery Specialist",
                "MBBS, DGO, MS (Obstetrics & Gynecology)", 14, "English, Bengali, Hindi", 500.0,
                "Mother & Child Care Nursing Home", "Budge Budge", "West Bengal", 22.4910, 88.2450);
        mcc2.withOpdDetails("Room 102", "Daily (Mon to Sun)", "10:30 AM - 12:00 PM", 500.0);
        saveDoctor(mcc2);

        // 5. Diamond City Nursing Home & Polyclinic (Nursing Home)
        Doctor dc1 = new Doctor("Dr. Kalyan Chakraborty (DC)", "General Surgery", "Laparoscopy & Day Care Surgery",
                "MBBS, MS (General Surgery)", 19, "English, Bengali, Hindi", 600.0,
                "Diamond City Nursing Home & Polyclinic", "Budge Budge", "West Bengal", 22.4720, 88.2780);
        dc1.withOpdDetails("Room 101", "Daily (Mon to Sun)", "09:00 AM - 10:30 AM", 600.0);
        saveDoctor(dc1);

        Doctor dc2 = new Doctor("Dr. Prasanta Ganguly (DC)", "Orthopedics", "Bone, Joint Trauma & Arthritis",
                "MBBS, MS (Orthopedics)", 16, "English, Bengali, Hindi", 600.0,
                "Diamond City Nursing Home & Polyclinic", "Budge Budge", "West Bengal", 22.4720, 88.2780);
        dc2.withOpdDetails("Room 102", "Daily (Mon to Sun)", "10:30 AM - 12:00 PM", 600.0);
        saveDoctor(dc2);

        // 6. Pujali Municipal Health Centre & Nursing Unit (Government)
        Doctor pmh1 = new Doctor("Dr. Bikash Samanta (Pujali)", "General Medicine", "Community Health & Outdoor Clinic",
                "MBBS, DNB (Family Medicine)", 11, "English, Bengali, Hindi", 20.0,
                "Pujali Municipal Health Centre & Nursing Unit", "Budge Budge", "West Bengal", 22.4690, 88.1480);
        pmh1.withOpdDetails("Room 101", "Daily (Mon to Sun)", "09:00 AM - 10:30 AM", 20.0);
        saveDoctor(pmh1);

        Doctor pmh2 = new Doctor("Dr. Sunita Roy (Pujali)", "Gynecology", "Maternal Health & Antenatal Checkup",
                "MBBS, DGO", 12, "English, Bengali, Hindi", 20.0,
                "Pujali Municipal Health Centre & Nursing Unit", "Budge Budge", "West Bengal", 22.4690, 88.1480);
        pmh2.withOpdDetails("Room 102", "Daily (Mon to Sun)", "10:30 AM - 12:00 PM", 20.0);
        saveDoctor(pmh2);

        // 7. Kasturi Nursing Home & Diagnostic Centre (Nursing Home)
        Doctor kas1 = new Doctor("Dr. Arpita Das (Kasturi)", "Pediatrics", "Child Health, Immunization & Neonatology",
                "MBBS, DCH, DNB (Pediatrics)", 12, "English, Bengali, Hindi", 500.0,
                "Kasturi Nursing Home & Diagnostic Centre", "Budge Budge", "West Bengal", 22.4640, 88.3050);
        kas1.withOpdDetails("Room 101", "Daily (Mon to Sun)", "09:00 AM - 10:30 AM", 500.0);
        saveDoctor(kas1);

        Doctor kas2 = new Doctor("Dr. Prasanta Ganguly (Kasturi)", "Orthopedics", "Bone, Joint Trauma & Arthritis",
                "MBBS, MS (Orthopedics)", 16, "English, Bengali, Hindi", 600.0,
                "Kasturi Nursing Home & Diagnostic Centre", "Budge Budge", "West Bengal", 22.4640, 88.3050);
        kas2.withOpdDetails("Room 102", "Daily (Mon to Sun)", "10:30 AM - 12:00 PM", 600.0);
        saveDoctor(kas2);

        Doctor kas3 = new Doctor("Dr. Sharmistha Sen (Kasturi)", "Gynecology", "High-Risk Pregnancy & Women Health",
                "MBBS, DGO, MD", 17, "English, Bengali, Hindi", 550.0,
                "Kasturi Nursing Home & Diagnostic Centre", "Budge Budge", "West Bengal", 22.4640, 88.3050);
        kas3.withOpdDetails("Room 103", "Daily (Mon to Sun)", "04:00 PM - 05:30 PM", 550.0);
        saveDoctor(kas3);

        // 6. Dr. Kunal Sarkar (Cardiology - Apollo Kolkata)
        saveDoctor(new Doctor(
                "Dr. Kunal Sarkar", "Cardiology", "Cardiothoracic & Vascular Surgery, Bypass Surgery",
                "MBBS, FRCS (Edin), FRCS (Glasg), MNAMS", 26, "English, Bengali, Hindi",
                1200.0, "Apollo Multispeciality Hospitals Kolkata", "Kolkata", "West Bengal", 22.5744, 88.3995
        ));

        // 7. Dr. Saroj Mondal (Cardiology - SSKM Kolkata)
        saveDoctor(new Doctor(
                "Dr. Saroj Mondal", "Cardiology", "Interventional Cardiology & Coronary Angioplasty",
                "MBBS, MD (Medicine), DM (Cardiology - Gold Medalist)", 19, "English, Bengali, Hindi",
                900.0, "SSKM Hospital & IPGMER Kolkata", "Kolkata", "West Bengal", 22.5385, 88.3429
        ));

        // 8. Dr. Subhashish Ghosh (Endocrinology - Apollo Salt Lake)
        saveDoctor(new Doctor(
                "Dr. Subhashish Ghosh", "Endocrinology", "Diabetology & Metabolic Thyroid Disorders",
                "MBBS, MD (Medicine), DM (Endocrinology)", 17, "English, Bengali, Hindi",
                1000.0, "Apollo Multispeciality Hospitals Kolkata", "Salt Lake", "West Bengal", 22.5744, 88.3995
        ));

        // 9. Dr. Arindam Biswas (General Medicine - Medical College & AMRI)
        saveDoctor(new Doctor(
                "Dr. Arindam Biswas", "General Medicine", "Internal Medicine & Infectious Diseases",
                "MBBS, MD (Internal Medicine), FRCP (London)", 24, "English, Bengali, Hindi",
                850.0, "Medical College & Hospital, Kolkata", "Kolkata", "West Bengal", 22.5134, 88.3667
        ));

        // 10. Dr. S. K. Mukherjee (Neurology - Medica Superspecialty)
        saveDoctor(new Doctor(
                "Dr. S. K. Mukherjee", "Neurology", "Stroke, Epilepsy & Neuro-Critical Care",
                "MBBS, MD, DM (Neurology - NIMHANS)", 20, "English, Bengali, Hindi",
                1100.0, "Medica Superspecialty Hospital", "Kolkata", "West Bengal", 22.4988, 88.3991
        ));

        // 11. Dr. Anirban Chatterjee (Orthopedics - Fortis Anandapur)
        saveDoctor(new Doctor(
                "Dr. Anirban Chatterjee", "Orthopedics", "Total Knee & Hip Joint Replacement, Sports Injury",
                "MBBS, MS (Ortho), MCh (Ortho - UK)", 16, "English, Bengali, Hindi",
                950.0, "Fortis Hospital Anandapur", "Kolkata", "West Bengal", 22.5186, 88.4011
        ));

        // 12. Dr. Sharmila Bose (Pediatrics - AMRI Salt Lake)
        saveDoctor(new Doctor(
                "Dr. Sharmila Bose", "Pediatrics", "Neonatology & Pediatric Critical Care",
                "MBBS, MD (Pediatrics), DNB (Pediatrics), DCH", 15, "English, Bengali, Hindi",
                800.0, "AMRI Hospital Salt Lake", "Salt Lake", "West Bengal", 22.5702, 88.4140
        ));

        // 13. Dr. Jayanta Chakrabarti (Oncology - Tata Medical Center New Town)
        saveDoctor(new Doctor(
                "Dr. Jayanta Chakrabarti", "Oncology", "Surgical Oncology & Breast, GI Cancer Surgery",
                "MBBS, MS (Surgery), MCh (Surgical Oncology)", 21, "English, Bengali, Hindi",
                1200.0, "Tata Medical Center", "New Town", "West Bengal", 22.5958, 88.4795
        ));

        // 14. Dr. Debabrata Roy (Gastroenterology - Peerless Hospital)
        saveDoctor(new Doctor(
                "Dr. Debabrata Roy", "Gastroenterology", "Hepatology, Therapeutic Endoscopy & IBD",
                "MBBS, MD (Medicine), DM (Gastroenterology)", 18, "English, Bengali, Hindi",
                900.0, "Peerless Hospitex Hospital", "Kolkata", "West Bengal", 22.4831, 88.3970
        ));

        // 15. Dr. Indranil Sen (Pulmonology - Ruby General Hospital)
        saveDoctor(new Doctor(
                "Dr. Indranil Sen", "Pulmonology", "Asthma, Interstitial Lung Disease, Bronchoscopy",
                "MBBS, MD (Pulmonary Med), FCCP (USA)", 14, "English, Bengali, Hindi",
                800.0, "Ruby General Hospital", "Kolkata", "West Bengal", 22.5130, 88.4008
        ));

        // 16. Dr. Tanmoy Banerjee (General Surgery - ILS Hospitals Howrah)
        saveDoctor(new Doctor(
                "Dr. Tanmoy Banerjee", "General Surgery", "Advanced Laparoscopic & Gastrointestinal Surgery",
                "MBBS, MS (General Surgery), FMAS, FIAGES", 15, "English, Bengali, Hindi",
                750.0, "ILS Hospitals Howrah", "Howrah", "West Bengal", 22.5958, 88.3436
        ));

        // 17. Dr. Prabal Neogi (Nephrology - Woodlands Hospital)
        saveDoctor(new Doctor(
                "Dr. Prabal Neogi", "Nephrology", "Kidney Dialysis, Renal Transplant & Hypertension",
                "MBBS, MD (Medicine), DM (Nephrology)", 22, "English, Bengali, Hindi",
                1000.0, "Woodlands Multispeciality Hospital", "Kolkata", "West Bengal", 22.5332, 88.3310
        ));

        // 18. Dr. Jai Ranjan Ram (Psychiatry - Apollo Kolkata)
        saveDoctor(new Doctor(
                "Dr. Jai Ranjan Ram", "Psychiatry", "Adult & Child Psychiatry, Depression, Anxiety & Stress",
                "MBBS, MD (Psychiatry), MRCPsych (UK)", 23, "English, Bengali, Hindi",
                1200.0, "Apollo Multispeciality Hospitals Kolkata", "Kolkata", "West Bengal", 22.5744, 88.3995
        ));

        // 19. Dr. Shantanu Panja (ENT - Apollo Kolkata)
        saveDoctor(new Doctor(
                "Dr. Shantanu Panja", "ENT", "Micro-Ear Surgery, Sinus Endoscopy & Head Neck Oncology",
                "MBBS, MS (ENT), DNB, FRCS", 19, "English, Bengali, Hindi",
                950.0, "Apollo Multispeciality Hospitals Kolkata", "Kolkata", "West Bengal", 22.5744, 88.3995
        ));

        // 20. Dr. Himadri Datta (Ophthalmology - Regional Institute of Ophthalmology Kolkata)
        saveDoctor(new Doctor(
                "Dr. Himadri Datta", "Ophthalmology", "Cataract Micro-Incision, Glaucoma & Cornea Transplant",
                "MBBS, MS (Ophthalmology), DO, FICO", 27, "English, Bengali, Hindi",
                800.0, "Medical College & Hospital, Kolkata", "Kolkata", "West Bengal", 22.5735, 88.3630
        ));

        // 21. Dr. Amitabha Ghosh (Neurology - Medica Hospital)
        saveDoctor(new Doctor(
                "Dr. Amitabha Ghosh", "Neurology", "Cognitive Disorders, Dementia & Movement Disorders",
                "MBBS, MD, DM (Neurology), FRCP (Glasg)", 25, "English, Bengali, Hindi",
                1300.0, "Medica Superspecialty Hospital", "Kolkata", "West Bengal", 22.4988, 88.3991
        ));

        // 22. Dr. Manotosh Das (Orthopedics - The Mission Hospital Durgapur)
        saveDoctor(new Doctor(
                "Dr. Manotosh Das", "Orthopedics", "Spine Surgery & Complex Trauma Reconstruction",
                "MBBS, MS (Orthopedics), DNB", 15, "English, Bengali, Hindi",
                700.0, "The Mission Hospital Durgapur", "Durgapur", "West Bengal", 23.5204, 87.3119
        ));

        // 23. Dr. R. N. Sharma (Pediatrics - Neotia Getwel Siliguri)
        saveDoctor(new Doctor(
                "Dr. R. N. Sharma", "Pediatrics", "Pediatric Medicine & Child Immunization",
                "MBBS, MD (Pediatrics)", 16, "English, Hindi, Bengali, Nepali",
                700.0, "Neotia Getwel Multispecialty Hospital", "Siliguri", "West Bengal", 26.7271, 88.3953
        ));

        // --- DELHI / NCR SPECIALIST DOCTORS ---
        // 24. Dr. Naresh Trehan (Cardiothoracic Surgery - Medanta Delhi NCR)
        saveDoctor(new Doctor(
                "Dr. Naresh Trehan", "Cardiology", "Cardiovascular & Thoracic Surgery, Bypass & Valve Replacement",
                "MBBS, FACS, FICS", 42, "English, Hindi, Punjabi",
                1500.0, "Medanta - The Medicity", "Delhi", "Delhi", 28.4394, 77.0425
        ));

        // 25. Dr. Ashok Seth (Cardiology - Fortis Escorts Heart Institute Delhi)
        saveDoctor(new Doctor(
                "Dr. Ashok Seth", "Cardiology", "Interventional Cardiology, Angioplasty & TAVR",
                "MBBS, MD, FRCP (Lond, Edin), FACC", 38, "English, Hindi",
                1500.0, "Fortis Escorts Heart Institute", "Delhi", "Delhi", 28.5603, 77.2789
        ));

        // 26. Dr. Randeep Guleria (Pulmonology & Medicine - Medanta Delhi NCR / Ex-AIIMS)
        saveDoctor(new Doctor(
                "Dr. Randeep Guleria", "Pulmonology", "Chest Medicine, Asthma, COPD, Sleep Disorders & Post-COVID",
                "MBBS, MD, DM (Pulmonology)", 34, "English, Hindi",
                1400.0, "Medanta - The Medicity", "Delhi", "Delhi", 28.4394, 77.0425
        ));

        // 27. Dr. Pradeep Chowbey (Bariatric & GI Surgery - Max Saket Delhi)
        saveDoctor(new Doctor(
                "Dr. Pradeep Chowbey", "General Surgery", "Minimal Access, Laparoscopic & Bariatric Surgery",
                "MBBS, MS, MNAMS, FICS, FACS", 40, "English, Hindi",
                1300.0, "Max Super Speciality Hospital Saket", "Delhi", "Delhi", 28.5273, 77.2125
        ));

        // 28. Dr. Arvinder Singh Soin (Liver Transplant - Medanta Delhi NCR)
        saveDoctor(new Doctor(
                "Dr. Arvinder Singh Soin", "Gastroenterology", "Liver Transplantation & Hepatobiliary Surgery",
                "MBBS, MS, FRCS (Edin, Glasg)", 33, "English, Hindi, Punjabi",
                1500.0, "Medanta - The Medicity", "Delhi", "Delhi", 28.4394, 77.0425
        ));

        // 29. Dr. S. K. S. Marya (Orthopedics - Max Healthcare Delhi)
        saveDoctor(new Doctor(
                "Dr. S. K. S. Marya", "Orthopedics", "Joint Replacement, Robotic Knee & Hip Surgery",
                "MBBS, MS (Ortho), DNB, M.Ch (UK), FRCS", 37, "English, Hindi",
                1300.0, "Max Super Speciality Hospital Saket", "Delhi", "Delhi", 28.5273, 77.2125
        ));

        // 30. Dr. Alka Kriplani (Gynecology - Paras / Ex-AIIMS Delhi)
        saveDoctor(new Doctor(
                "Dr. Alka Kriplani", "Gynecology", "High-Risk Obstetrics, Laparoscopic Gynae Surgery & IVF",
                "MBBS, MD, FICOG, FAMS, FICMCH", 38, "English, Hindi",
                1200.0, "Paras Healthcare / Ex-AIIMS Delhi", "Delhi", "Delhi", 28.5672, 77.2100
        ));

        // --- MUMBAI SPECIALIST DOCTORS ---
        // 31. Dr. Ramakanta Panda (Cardiac Surgery - Asian Heart Institute Mumbai)
        saveDoctor(new Doctor(
                "Dr. Ramakanta Panda", "Cardiology", "Off-Pump Bypass Surgery, Redo Bypass & Aneurysm Repair",
                "MBBS, M.Ch, FRCS", 36, "English, Hindi, Marathi, Odia",
                1500.0, "Asian Heart Institute Mumbai", "Mumbai", "Maharashtra", 19.0657, 72.8682
        ));

        // 32. Dr. Suresh Advani (Medical Oncology - Jaslok & Sushrut Mumbai)
        saveDoctor(new Doctor(
                "Dr. Suresh Advani", "Oncology", "Medical Oncology, Bone Marrow Transplant & Chemotherapy",
                "MBBS, MD, FICP, FNAMS", 48, "English, Hindi, Marathi, Sindhi",
                1600.0, "Jaslok Hospital Mumbai", "Mumbai", "Maharashtra", 18.9716, 72.8052
        ));

        // 33. Dr. Zarir Udwadia (Pulmonology - Hinduja & Breach Candy Mumbai)
        saveDoctor(new Doctor(
                "Dr. Zarir Udwadia", "Pulmonology", "MDR-TB, Interstitial Lung Disease, Bronchial Asthma",
                "MBBS, MD, DNB, FRCP (Lond)", 32, "English, Hindi, Gujarati, Marathi",
                1400.0, "P. D. Hinduja National Hospital", "Mumbai", "Maharashtra", 19.0330, 72.8396
        ));

        // 34. Dr. Gustad Daver (General & Transplant Surgery - HN Reliance Mumbai)
        saveDoctor(new Doctor(
                "Dr. Gustad Daver", "General Surgery", "Liver & Kidney Transplant, Vascular & Complex General Surgery",
                "MBBS, MS, FICS, FAIS", 39, "English, Hindi, Marathi, Gujarati",
                1300.0, "Sir H. N. Reliance Foundation Hospital", "Mumbai", "Maharashtra", 18.9567, 72.8198
        ));

        // 35. Dr. Muffazal Lakdawala (Bariatric Surgery - Saifee Mumbai)
        saveDoctor(new Doctor(
                "Dr. Muffazal Lakdawala", "General Surgery", "Bariatric & Metabolic Surgery, Advanced GI Laparoscopy",
                "MBBS, MS, FACS", 26, "English, Hindi, Marathi, Gujarati",
                1400.0, "Saifee Hospital Mumbai", "Mumbai", "Maharashtra", 18.9535, 72.8185
        ));

        // 36. Dr. Rishma Pai (Gynecology & IVF - Lilavati Hospital Mumbai)
        saveDoctor(new Doctor(
                "Dr. Rishma Pai", "Gynecology", "Infertility, IVF, Reproductive Medicine & Endoscopy",
                "MBBS, MD, DGO, FCPS, FICOG", 30, "English, Hindi, Marathi, Gujarati",
                1200.0, "Lilavati Hospital and Research Centre", "Mumbai", "Maharashtra", 19.0514, 72.8290
        ));

        // --- BENGALURU & BHUBANESWAR DOCTORS ---
        // 37. Dr. Devi Prasad Shetty (Cardiac Surgery - Narayana Health Bengaluru)
        saveDoctor(new Doctor(
                "Dr. Devi Prasad Shetty", "Cardiology", "Adult & Pediatric Cardiac Surgery, Valve Repair",
                "MBBS, MS, FRCS (Edin)", 40, "English, Hindi, Kannada, Bengali",
                1200.0, "Narayana Health City", "Bengaluru", "Karnataka", 12.8222, 77.6895
        ));

        // 38. Dr. H. Sudarshan Ballal (Nephrology - Manipal Hospitals Bengaluru)
        saveDoctor(new Doctor(
                "Dr. H. Sudarshan Ballal", "Nephrology", "Kidney Transplantation, Dialysis & Chronic Kidney Disease",
                "MBBS, MD, FRCP (Lond)", 35, "English, Hindi, Kannada",
                1200.0, "Manipal Hospital Old Airport Road", "Bengaluru", "Karnataka", 12.9592, 77.6534
        ));

        // 39. Dr. Satyabrata Tripathy (Cardiology - Apollo Bhubaneswar)
        saveDoctor(new Doctor(
                "Dr. Satyabrata Tripathy", "Cardiology", "Interventional Cardiology & Coronary Angioplasty",
                "MBBS, MD, DM (Cardiology)", 20, "English, Hindi, Odia",
                800.0, "Apollo Hospitals Bhubaneswar", "Bhubaneswar", "Odisha", 20.3087, 85.8331
        ));

        // 40. Dr. Ashok Kumar Mohapatra (Neurosurgery - AIIMS Bhubaneswar)
        saveDoctor(new Doctor(
                "Dr. Ashok Kumar Mohapatra", "Neurology", "Complex Brain & Spine Surgery, Neuro-Trauma",
                "MBBS, MS, M.Ch (Neurosurgery - AIIMS)", 40, "English, Hindi, Odia",
                1200.0, "AIIMS Bhubaneswar", "Bhubaneswar", "Odisha", 20.2312, 85.7745
        ));

        // 41. Dr. Bidhan Chandra Das (General Medicine - KIMS Bhubaneswar)
        saveDoctor(new Doctor(
                "Dr. Bidhan Chandra Das", "General Medicine", "Internal Medicine, Diabetes & Infectious Diseases",
                "MBBS, MD (Medicine)", 22, "English, Hindi, Odia",
                700.0, "KIMS Super Speciality Hospital", "Bhubaneswar", "Odisha", 20.3533, 85.8197
        ));

        // 42. Dr. Sujata Mohanty (Gynecology - SUM Ultimate Medicare)
        saveDoctor(new Doctor(
                "Dr. Sujata Mohanty", "Gynecology", "High-Risk Pregnancy, Laparoscopic Gynae & Infertility",
                "MBBS, MD, DGO", 25, "English, Hindi, Odia",
                800.0, "SUM Ultimate Medicare", "Bhubaneswar", "Odisha", 20.2818, 85.7674
        ));

        // 43. Dr. Rabindra Nath Sahu (Pediatrics - Apollo Bhubaneswar)
        saveDoctor(new Doctor(
                "Dr. Rabindra Nath Sahu", "Pediatrics", "Neonatal Intensive Care, Child Health & Vaccination",
                "MBBS, MD (Pediatrics)", 18, "English, Hindi, Odia",
                750.0, "Apollo Hospitals Bhubaneswar", "Bhubaneswar", "Odisha", 20.3087, 85.8331
        ));

        // 44. Dr. Basanta Kumar Behera (Orthopedics - AIIMS Bhubaneswar)
        saveDoctor(new Doctor(
                "Dr. Basanta Kumar Behera", "Orthopedics", "Joint Replacement, Complex Trauma & Arthroscopy",
                "MBBS, MS (Orthopedics)", 19, "English, Hindi, Odia",
                800.0, "AIIMS Bhubaneswar", "Bhubaneswar", "Odisha", 20.2312, 85.7745
        ));

        // --- BENGALURU SPECIALISTS ---
        // 45. Dr. Vivek Jawali (Cardiothoracic Surgery - Fortis Bengaluru)
        saveDoctor(new Doctor(
                "Dr. Vivek Jawali", "Cardiology", "Minimally Invasive Cardiac Surgery, Beating Heart Surgery",
                "MBBS, MS, M.Ch (CTVS)", 38, "English, Hindi, Kannada",
                1400.0, "Fortis Hospital Bannerghatta Road", "Bengaluru", "Karnataka", 12.8938, 77.5978
        ));

        // 46. Dr. Nandita Palshetkar (Gynecology & IVF - Fortis Bengaluru)
        saveDoctor(new Doctor(
                "Dr. Nandita Palshetkar", "Gynecology", "IVF Specialist, Infertility & Advanced Reproductive Medicine",
                "MBBS, MD, FCPS, FICOG", 30, "English, Hindi, Kannada, Marathi",
                1200.0, "Fortis Hospital Bannerghatta Road", "Bengaluru", "Karnataka", 12.8938, 77.5978
        ));

        // 47. Dr. Sanjay Pai (Orthopedics - Apollo Bengaluru)
        saveDoctor(new Doctor(
                "Dr. Sanjay Pai", "Orthopedics", "Robotic Knee Replacement, Hip Resurfacing & Sports Medicine",
                "MBBS, MS (Ortho), M.Ch (UK)", 28, "English, Hindi, Kannada",
                1100.0, "Apollo Hospital Bannerghatta", "Bengaluru", "Karnataka", 12.8938, 77.5978
        ));

        // 48. Dr. Annapurna S (Pediatrics - Aster CMI Bengaluru)
        saveDoctor(new Doctor(
                "Dr. Annapurna S", "Pediatrics", "General Pediatrics, Pediatric Infectious Diseases & Child Growth",
                "MBBS, DCH, DNB (Pediatrics)", 17, "English, Hindi, Kannada, Tamil",
                900.0, "Aster CMI Hospital Hebbal", "Bengaluru", "Karnataka", 13.0587, 77.5925
        ));

        // 49. Dr. K. S. Shekhar (General Surgery - Manipal Bengaluru)
        saveDoctor(new Doctor(
                "Dr. K. S. Shekhar", "General Surgery", "Advanced GI Laparoscopy, Hernia & Gallbladder Surgery",
                "MBBS, MS (General Surgery), FRCS", 32, "English, Hindi, Kannada",
                1000.0, "Manipal Hospital Old Airport Road", "Bengaluru", "Karnataka", 12.9592, 77.6534
        ));

        // --- DELHI ADDITIONAL SPECIALISTS ---
        // 50. Dr. Anoop Misra (Endocrinology & Diabetology - Fortis C-DOC Delhi)
        saveDoctor(new Doctor(
                "Dr. Anoop Misra", "Endocrinology", "Diabetology, Obesity, Lipid Disorders & Insulin Therapy",
                "MBBS, MD (Medicine), Padma Shri", 38, "English, Hindi",
                1500.0, "Fortis C-DOC Hospital", "Delhi", "Delhi", 28.5447, 77.2066
        ));

        // 51. Dr. Shamsher Dwivedee (Neurology - VIMHANS Delhi)
        saveDoctor(new Doctor(
                "Dr. Shamsher Dwivedee", "Neurology", "Stroke Management, Epilepsy, Parkinsonism & Headache Clinic",
                "MBBS, MD, DM (Neurology)", 30, "English, Hindi",
                1300.0, "VIMHANS Hospital", "Delhi", "Delhi", 28.5672, 77.2500
        ));

        // 52. Dr. Vinod Raina (Oncology - Fortis Memorial Delhi NCR)
        saveDoctor(new Doctor(
                "Dr. Vinod Raina", "Oncology", "Medical Oncology, Chemotherapy & Stem Cell Transplant",
                "MBBS, MD, DM (Medical Oncology - AIIMS)", 35, "English, Hindi",
                1400.0, "Fortis Memorial Research Institute", "Delhi", "Delhi", 28.4595, 77.0725
        ));

        // --- MUMBAI ADDITIONAL SPECIALISTS ---
        // 53. Dr. Arun Mullaji (Orthopedics - Breach Candy Mumbai)
        saveDoctor(new Doctor(
                "Dr. Arun Mullaji", "Orthopedics", "Robotic Joint Replacement & Computer-Assisted Knee Surgery",
                "MBBS, MS, M.Ch (UK), FRCS", 34, "English, Hindi, Marathi",
                1500.0, "Breach Candy Hospital", "Mumbai", "Maharashtra", 18.9716, 72.8052
        ));

        // 54. Dr. Fazal Nabi (Pediatrics - Wockhardt Mumbai)
        saveDoctor(new Doctor(
                "Dr. Fazal Nabi", "Pediatrics", "Pediatric Critical Care, Asthma & Child Nutrition",
                "MBBS, DCH, DNB (Pediatrics)", 24, "English, Hindi, Marathi, Urdu",
                1000.0, "Wockhardt Hospitals Mumbai Central", "Mumbai", "Maharashtra", 18.9712, 72.8222
        ));

        // --- CHENNAI & HYDERABAD & PUNE SPECIALISTS ---
        // 55. Dr. K. R. Balakrishnan (Cardiothoracic - MGM / Apollo Chennai)
        saveDoctor(new Doctor(
                "Dr. K. R. Balakrishnan", "Cardiology", "Heart Transplant, LVAD & Complex Heart Surgery",
                "MBBS, MS, M.Ch (CTVS)", 36, "English, Hindi, Tamil",
                1500.0, "Apollo Hospitals Greams Road", "Chennai", "Tamil Nadu", 13.0604, 80.2508
        ));

        // 56. Dr. D. Nageshwar Reddy (Gastroenterology - AIG / Apollo Hyderabad)
        saveDoctor(new Doctor(
                "Dr. D. Nageshwar Reddy", "Gastroenterology", "Advanced Therapeutic Endoscopy & Liver Diseases",
                "MBBS, MD, DM, D.Sc, Padma Bhushan", 42, "English, Hindi, Telugu",
                1600.0, "Apollo Health City Jubilee Hills", "Hyderabad", "Telangana", 17.4168, 78.4116
        ));

        // 57. Dr. Purvez Grant (Cardiology - Ruby Hall Pune)
        saveDoctor(new Doctor(
                "Dr. Purvez Grant", "Cardiology", "Interventional Cardiology & Coronary Angioplasty",
                "MBBS, MD, FACC", 35, "English, Hindi, Marathi",
                1200.0, "Ruby Hall Clinic", "Pune", "Maharashtra", 18.5314, 73.8776
        ));

        // --- TELANGANA / HYDERABAD SPECIALISTS (GOVERNMENT & PREMIER PRIVATE) ---

        // 58. Dr. B. Bhaskar Rao (Cardiology - KIMS Begumpet)
        saveDoctor(new Doctor(
                "Dr. B. Bhaskar Rao", "Cardiology", "Cardiothoracic Surgery, Heart & Lung Transplant",
                "MBBS, MS, M.Ch (CTVS)", 35, "English, Telugu, Hindi",
                1500.0, "KIMS Hospitals Begumpet", "Hyderabad", "Telangana", 17.4363, 78.4878
        ));

        // 59. Dr. P. Raghu Ram (Oncology - KIMS-Ushalakshmi Breast Centre)
        saveDoctor(new Doctor(
                "Dr. P. Raghu Ram", "Oncology", "Oncoplastic Breast Surgery & Surgical Oncology",
                "MBBS, MS, FRCS (Eng), FRCS (Edin), Padma Shri", 28, "English, Telugu, Hindi",
                1400.0, "KIMS Hospitals Begumpet", "Hyderabad", "Telangana", 17.4363, 78.4878
        ));

        // 60. Dr. Manjula Anagani (Gynecology - Yashoda Hitec City)
        saveDoctor(new Doctor(
                "Dr. Manjula Anagani", "Gynecology", "Minimally Invasive & Robotic Gynecological Surgery",
                "MBBS, MD (OBG), FICOG, Padma Shri", 27, "English, Telugu, Hindi",
                1200.0, "Yashoda Hospitals Somajiguda", "Hyderabad", "Telangana", 17.4258, 78.4568
        ));

        // 61. Dr. A. Sreenivas Kumar (Cardiology - Apollo Health City)
        saveDoctor(new Doctor(
                "Dr. A. Sreenivas Kumar", "Cardiology", "Interventional Cardiology, TAVR & Complex Angioplasty",
                "MBBS, MD, DM (Cardiology), FACC", 26, "English, Telugu, Hindi",
                1200.0, "Apollo Health City Jubilee Hills", "Hyderabad", "Telangana", 17.4168, 78.4116
        ));

        // 62. Dr. Guru N. Reddy (Gastroenterology - Continental Gachibowli)
        saveDoctor(new Doctor(
                "Dr. Guru N. Reddy", "Gastroenterology", "Therapeutic Endoscopy, Hepatology & Liver Care",
                "MD, FACP, FACG, FASGE (USA)", 34, "English, Telugu, Hindi",
                1500.0, "Continental Hospitals", "Hyderabad", "Telangana", 17.4182, 78.3475
        ));

        // 63. Dr. Sudhir Prasad (Pulmonology - Yashoda Somajiguda)
        saveDoctor(new Doctor(
                "Dr. Sudhir Prasad", "Pulmonology", "Interventional Pulmonology, Sleep Disorders & Critical Care",
                "MBBS, MD (Pulmonary Medicine), FCCP", 22, "English, Telugu, Hindi",
                1000.0, "Yashoda Hospitals Somajiguda", "Hyderabad", "Telangana", 17.4258, 78.4568
        ));

        // 64. Dr. Subba Rao (Neurology - Care Hospitals Banjara Hills)
        saveDoctor(new Doctor(
                "Dr. Subba Rao", "Neurology", "Stroke Management, Epilepsy & Neuromuscular Disorders",
                "MBBS, MD, DM (Neurology)", 25, "English, Telugu, Hindi",
                1100.0, "Care Hospitals Banjara Hills", "Hyderabad", "Telangana", 17.4156, 78.4485
        ));

        // 65. Dr. S. V. S. S. Prasad (Oncology - MNJ Institute of Oncology Govt)
        saveDoctor(new Doctor(
                "Dr. S. V. S. S. Prasad", "Oncology", "Medical Oncology, Targeted Immunotherapy & Chemotherapy",
                "MBBS, MD, DM (Medical Oncology)", 30, "English, Telugu, Hindi",
                800.0, "MNJ Institute of Oncology & Regional Cancer Centre (Govt)", "Hyderabad", "Telangana", 17.3995, 78.4612
        ));

        // 66. Dr. R. V. Kumar (Orthopedics - NIMS Govt)
        saveDoctor(new Doctor(
                "Dr. R. V. Kumar", "Orthopedics", "Joint Replacement, Pelvic Trauma & Complex Reconstruction",
                "MBBS, MS (Ortho), DNB (Ortho), M.Ch", 29, "English, Telugu, Hindi",
                600.0, "Nizam's Institute of Medical Sciences (NIMS Govt)", "Hyderabad", "Telangana", 17.4239, 78.4529
        ));

        // 67. Dr. M. Veera Prasad (General Surgery - Osmania General Hospital Govt)
        saveDoctor(new Doctor(
                "Dr. M. Veera Prasad", "General Surgery", "Emergency Trauma Surgery & Advanced Laparoscopy",
                "MBBS, MS (General Surgery), FMAS", 24, "English, Telugu, Hindi",
                500.0, "Osmania General Hospital (Govt)", "Hyderabad", "Telangana", 17.3685, 78.4735
        ));

        // 68. Dr. K. S. Lakshmi (Pediatrics - Rainbow Children's Hospital)
        saveDoctor(new Doctor(
                "Dr. K. S. Lakshmi", "Pediatrics", "Neonatal Intensive Care, Child Nutrition & Development",
                "MBBS, DCH, DNB (Pediatrics)", 20, "English, Telugu, Hindi",
                900.0, "Rainbow Children's Hospital & BirthRight", "Hyderabad", "Telangana", 17.4205, 78.4348
        ));

        // 69. Dr. P. Shravan Kumar (General Medicine - Gandhi Hospital Govt)
        saveDoctor(new Doctor(
                "Dr. P. Shravan Kumar", "General Medicine", "Infectious Diseases, Diabetes & Acute Medical Care",
                "MBBS, MD (General Medicine)", 26, "English, Telugu, Hindi",
                500.0, "Gandhi Hospital & Medical College (Govt)", "Hyderabad", "Telangana", 17.4246, 78.5034
        ));

        // --- TAMIL NADU / CHENNAI SPECIALISTS (GOVERNMENT & PREMIER PRIVATE) ---

        // 70. Prof. Mohamed Rela (Gastroenterology & Liver Transplant - Dr. Rela Institute)
        saveDoctor(new Doctor(
                "Prof. Mohamed Rela", "Gastroenterology", "Living Donor Liver Transplantation & Hepatobiliary Surgery",
                "MBBS, MS, FRCS (Edin), D.Sc", 38, "English, Tamil, Hindi",
                1800.0, "Dr. Rela Institute & Medical Centre", "Chennai", "Tamil Nadu", 12.9555, 80.1415
        ));

        // 71. Dr. Prithvi Mohandas (Orthopedics - MIOT International)
        saveDoctor(new Doctor(
                "Dr. Prithvi Mohandas", "Orthopedics", "Robotic Hip & Knee Arthroplasty, Complex Trauma & Revision Surgery",
                "MBBS, MS (Orth), MCh, FRCS", 25, "English, Tamil, Hindi",
                1500.0, "MIOT International", "Chennai", "Tamil Nadu", 13.0232, 80.1788
        ));

        // 72. Dr. V. V. Bashi (Cardiology / CTVS - SIMS Hospital Vadapalani)
        saveDoctor(new Doctor(
                "Dr. V. V. Bashi", "Cardiology", "Complex Aortic Aneurysm Surgery, CABG & Valve Repair",
                "MBBS, MS, M.Ch (CTVS), FIACS", 32, "English, Tamil, Malayalam",
                1400.0, "SIMS Hospital Vadapalani", "Chennai", "Tamil Nadu", 13.0505, 80.2105
        ));

        // 73. Dr. Suresh Rao (Cardiology / Critical Care - MGM Healthcare)
        saveDoctor(new Doctor(
                "Dr. Suresh Rao", "Cardiology", "Heart & Lung Transplant Critical Care, ECMO & Mechanical Circulatory Support",
                "MBBS, MD, PDCC (Cardiac Anesthesia)", 28, "English, Tamil, Kannada, Hindi",
                1500.0, "MGM Healthcare", "Chennai", "Tamil Nadu", 13.0725, 80.2185
        ));

        // 74. Dr. S. M. Chandramohan (General Surgery - Rajiv Gandhi Govt General Hospital)
        saveDoctor(new Doctor(
                "Dr. S. M. Chandramohan", "General Surgery", "Surgical Gastroenterology, Upper GI & Advanced Laparoscopy",
                "MBBS, MS, M.Ch (Surg Gastro), FACS", 30, "English, Tamil, Hindi",
                500.0, "Rajiv Gandhi Government General Hospital (Govt)", "Chennai", "Tamil Nadu", 13.0805, 80.2785
        ));

        // 75. Dr. P. Balaji (General Surgery / Vascular - Madras Medical College Govt Hospital)
        saveDoctor(new Doctor(
                "Dr. P. Balaji", "General Surgery", "Peripheral Vascular Surgery, Diabetic Foot & Trauma Reconstruction",
                "MBBS, MS, M.Ch (Vascular)", 24, "English, Tamil, Hindi",
                500.0, "Madras Medical College & Rajiv Gandhi Govt Hospital (Govt)", "Chennai", "Tamil Nadu", 13.0805, 80.2785
        ));

        // 76. Dr. K. Narayanasamy (General Medicine - Stanley Govt Medical College)
        saveDoctor(new Doctor(
                "Dr. K. Narayanasamy", "General Medicine", "Hepatology, Acute Viral Hepatitis & Internal Medicine",
                "MBBS, MD (Gen Med), DM (Hepatology)", 27, "English, Tamil, Hindi",
                500.0, "Government Stanley Medical College Hospital (Govt)", "Chennai", "Tamil Nadu", 13.1075, 80.2878
        ));

        // 77. Dr. Deepa Hariharan (Pediatrics - Apollo Children's Hospital)
        saveDoctor(new Doctor(
                "Dr. Deepa Hariharan", "Pediatrics", "Neonatology, Pediatric Critical Care & Child Development",
                "MBBS, MD (Pediatrics), MRCPCH (UK)", 22, "English, Tamil, Hindi",
                1100.0, "Apollo Hospitals Greams Road", "Chennai", "Tamil Nadu", 13.0604, 80.2508
        ));

        // 78. Dr. Aruna Muralidhar (Gynecology - Fortis Malar Hospital)
        saveDoctor(new Doctor(
                "Dr. Aruna Muralidhar", "Gynecology", "High-Risk Pregnancy, Infertility & Minimally Invasive Gynecological Surgery",
                "MBBS, MD, MRCOG (UK), FRCOG", 20, "English, Tamil, Kannada, Hindi",
                1200.0, "Fortis Malar Hospital", "Chennai", "Tamil Nadu", 13.0062, 80.2575
        ));

        // 79. Dr. E. Ravindra Mohan (Ophthalmology - Apollo Hospitals Greams Road)
        saveDoctor(new Doctor(
                "Dr. E. Ravindra Mohan", "Ophthalmology", "Oculoplasty, Cataract, Glaucoma & Advanced Orbital Surgery",
                "MBBS, MD (AIIMS), FRCS (Glasgow)", 26, "English, Tamil, Telugu, Hindi",
                1000.0, "Apollo Hospitals Greams Road", "Chennai", "Tamil Nadu", 13.0604, 80.2508
        ));

        // 80. Dr. R. Ravi Kumar (Pulmonology - SIMS Hospital)
        saveDoctor(new Doctor(
                "Dr. R. Ravi Kumar", "Pulmonology", "Asthma, COPD, Interventional Bronchoscopy & Sleep Apnea",
                "MBBS, MD (Pulmonary Med), FCCP", 19, "English, Tamil, Hindi",
                1100.0, "SIMS Hospital Vadapalani", "Chennai", "Tamil Nadu", 13.0505, 80.2105
        ));
    }

    private void seedBloodInventory() {
        bloodRepository.deleteAll();

        // --- WEST BENGAL (KOLKATA, HOWRAH, SILIGURI) ---
        bloodRepository.save(new BloodInventory("Central Blood Bank Kolkata (Maniktala)", "Govt. of West Bengal", "105, Vivekananda Road, Maniktala, Kolkata", "Kolkata", "West Bengal", 22.5855, 88.3750, "+91 33 2355 1200", "O+", "WHOLE_BLOOD", 38));
        bloodRepository.save(new BloodInventory("Central Blood Bank Kolkata (Maniktala)", "Govt. of West Bengal", "105, Vivekananda Road, Maniktala, Kolkata", "Kolkata", "West Bengal", 22.5855, 88.3750, "+91 33 2355 1200", "O-", "WHOLE_BLOOD", 8));
        bloodRepository.save(new BloodInventory("Central Blood Bank Kolkata (Maniktala)", "Govt. of West Bengal", "105, Vivekananda Road, Maniktala, Kolkata", "Kolkata", "West Bengal", 22.5855, 88.3750, "+91 33 2355 1200", "A+", "WHOLE_BLOOD", 26));
        bloodRepository.save(new BloodInventory("Central Blood Bank Kolkata (Maniktala)", "Govt. of West Bengal", "105, Vivekananda Road, Maniktala, Kolkata", "Kolkata", "West Bengal", 22.5855, 88.3750, "+91 33 2355 1200", "B+", "PRBC", 42));
        bloodRepository.save(new BloodInventory("Medical College Kolkata Blood Bank", "Medical College & Hospital", "88 College Street, Bowbazar, Kolkata", "Kolkata", "West Bengal", 22.5735, 88.3630, "+91 33 2255 1621", "AB+", "WHOLE_BLOOD", 18));
        bloodRepository.save(new BloodInventory("Medical College Kolkata Blood Bank", "Medical College & Hospital", "88 College Street, Bowbazar, Kolkata", "Kolkata", "West Bengal", 22.5735, 88.3630, "+91 33 2255 1621", "O+", "PLATELETS", 22));
        bloodRepository.save(new BloodInventory("Apollo Multispeciality Kolkata Blood Centre", "Apollo Hospitals Kolkata", "Canal Circular Road, Phool Bagan, Kolkata", "Kolkata", "West Bengal", 22.5744, 88.3995, "+91 33 2320 3040", "A+", "PLATELETS", 24));
        bloodRepository.save(new BloodInventory("SSKM Hospital Blood Bank", "IPGMER & SSKM Kolkata", "AJC Bose Road, Bhowanipore, Kolkata", "Kolkata", "West Bengal", 22.5385, 88.3429, "+91 33 2223 1589", "O+", "WHOLE_BLOOD", 40));
        bloodRepository.save(new BloodInventory("Howrah District Blood Bank", "District Hospital Howrah", "Station Road, Golabari, Howrah", "Howrah", "West Bengal", 22.5958, 88.3436, "+91 33 2660 0110", "O+", "WHOLE_BLOOD", 20));
        bloodRepository.save(new BloodInventory("North Bengal Medical College Blood Bank", "NBMC Siliguri", "Sushrutanagar, Siliguri", "Siliguri", "West Bengal", 26.7050, 88.3650, "+91 353 258 5480", "O+", "WHOLE_BLOOD", 25));

        // --- TELANGANA / HYDERABAD (GOVERNMENT & PREMIER PRIVATE) ---
        bloodRepository.save(new BloodInventory("Osmania General Hospital Blood Bank", "Osmania Govt Hospital", "Afzal Gunj, High Court Road, Hyderabad", "Hyderabad", "Telangana", 17.3685, 78.4735, "+91 40 2460 0121", "O+", "WHOLE_BLOOD", 45));
        bloodRepository.save(new BloodInventory("Osmania General Hospital Blood Bank", "Osmania Govt Hospital", "Afzal Gunj, High Court Road, Hyderabad", "Hyderabad", "Telangana", 17.3685, 78.4735, "+91 40 2460 0121", "A+", "WHOLE_BLOOD", 30));
        bloodRepository.save(new BloodInventory("Osmania General Hospital Blood Bank", "Osmania Govt Hospital", "Afzal Gunj, High Court Road, Hyderabad", "Hyderabad", "Telangana", 17.3685, 78.4735, "+91 40 2460 0121", "B+", "PRBC", 35));
        bloodRepository.save(new BloodInventory("NIMS Blood Centre", "Nizam's Institute of Medical Sciences (Govt)", "Punjagutta Main Rd, Somajiguda, Hyderabad", "Hyderabad", "Telangana", 17.4239, 78.4529, "+91 40 2348 9000", "O+", "PLATELETS", 28));
        bloodRepository.save(new BloodInventory("NIMS Blood Centre", "Nizam's Institute of Medical Sciences (Govt)", "Punjagutta Main Rd, Somajiguda, Hyderabad", "Hyderabad", "Telangana", 17.4239, 78.4529, "+91 40 2348 9000", "O-", "WHOLE_BLOOD", 12));
        bloodRepository.save(new BloodInventory("NIMS Blood Centre", "Nizam's Institute of Medical Sciences (Govt)", "Punjagutta Main Rd, Somajiguda, Hyderabad", "Hyderabad", "Telangana", 17.4239, 78.4529, "+91 40 2348 9000", "AB+", "WHOLE_BLOOD", 16));
        bloodRepository.save(new BloodInventory("Gandhi Hospital Blood Bank", "Gandhi Hospital & Medical College (Govt)", "Musheerabad, Secunderabad, Hyderabad", "Hyderabad", "Telangana", 17.4246, 78.5034, "+91 40 2750 5566", "B+", "WHOLE_BLOOD", 38));
        bloodRepository.save(new BloodInventory("Gandhi Hospital Blood Bank", "Gandhi Hospital & Medical College (Govt)", "Musheerabad, Secunderabad, Hyderabad", "Hyderabad", "Telangana", 17.4246, 78.5034, "+91 40 2750 5566", "A-", "WHOLE_BLOOD", 9));
        bloodRepository.save(new BloodInventory("Indian Red Cross Society Blood Centre", "Red Cross Telangana", "Vidyanagar, Adikmet, Hyderabad", "Hyderabad", "Telangana", 17.4042, 78.5085, "+91 40 2763 3087", "O+", "WHOLE_BLOOD", 55));
        bloodRepository.save(new BloodInventory("Indian Red Cross Society Blood Centre", "Red Cross Telangana", "Vidyanagar, Adikmet, Hyderabad", "Hyderabad", "Telangana", 17.4042, 78.5085, "+91 40 2763 3087", "AB-", "WHOLE_BLOOD", 7));
        bloodRepository.save(new BloodInventory("Apollo Health City Blood Centre", "Apollo Health City", "Road No. 72, Jubilee Hills, Hyderabad", "Hyderabad", "Telangana", 17.4168, 78.4116, "+91 40 2360 7777", "A+", "PLATELETS", 32));
        bloodRepository.save(new BloodInventory("Apollo Health City Blood Centre", "Apollo Health City", "Road No. 72, Jubilee Hills, Hyderabad", "Hyderabad", "Telangana", 17.4168, 78.4116, "+91 40 2360 7777", "B+", "PRBC", 40));
        bloodRepository.save(new BloodInventory("KIMS Hospitals Blood Bank", "KIMS Hospitals Begumpet", "Minister Road, Secunderabad, Hyderabad", "Hyderabad", "Telangana", 17.4363, 78.4878, "+91 40 4488 5000", "O+", "WHOLE_BLOOD", 42));

        // --- TAMIL NADU / CHENNAI (GOVERNMENT & PREMIER PRIVATE) ---
        bloodRepository.save(new BloodInventory("Rajiv Gandhi Govt Hospital Blood Bank", "Madras Medical College (Govt)", "EVR Periyar Salai, Park Town, Chennai", "Chennai", "Tamil Nadu", 13.0805, 80.2785, "+91 44 2530 5000", "O+", "WHOLE_BLOOD", 50));
        bloodRepository.save(new BloodInventory("Rajiv Gandhi Govt Hospital Blood Bank", "Madras Medical College (Govt)", "EVR Periyar Salai, Park Town, Chennai", "Chennai", "Tamil Nadu", 13.0805, 80.2785, "+91 44 2530 5000", "A+", "WHOLE_BLOOD", 35));
        bloodRepository.save(new BloodInventory("Rajiv Gandhi Govt Hospital Blood Bank", "Madras Medical College (Govt)", "EVR Periyar Salai, Park Town, Chennai", "Chennai", "Tamil Nadu", 13.0805, 80.2785, "+91 44 2530 5000", "B+", "PRBC", 40));
        bloodRepository.save(new BloodInventory("Tamil Nadu Govt Multi Super Speciality Blood Centre", "Omandurar Govt Hospital", "Anna Salai, Omandurar Estate, Chennai", "Chennai", "Tamil Nadu", 13.0683, 80.2741, "+91 44 2566 6000", "O+", "PLATELETS", 25));
        bloodRepository.save(new BloodInventory("Tamil Nadu Govt Multi Super Speciality Blood Centre", "Omandurar Govt Hospital", "Anna Salai, Omandurar Estate, Chennai", "Chennai", "Tamil Nadu", 13.0683, 80.2741, "+91 44 2566 6000", "O-", "WHOLE_BLOOD", 10));
        bloodRepository.save(new BloodInventory("Government Stanley Medical College Blood Bank", "Stanley Govt Hospital", "Old Jail Road, Royapuram, Chennai", "Chennai", "Tamil Nadu", 13.1075, 80.2878, "+91 44 2528 0900", "B+", "WHOLE_BLOOD", 32));
        bloodRepository.save(new BloodInventory("Government Stanley Medical College Blood Bank", "Stanley Govt Hospital", "Old Jail Road, Royapuram, Chennai", "Chennai", "Tamil Nadu", 13.1075, 80.2878, "+91 44 2528 0900", "AB+", "WHOLE_BLOOD", 15));
        bloodRepository.save(new BloodInventory("Indian Red Cross Society Blood Centre", "Red Cross Tamil Nadu", "50, Montieth Road, Egmore, Chennai", "Chennai", "Tamil Nadu", 13.0674, 80.2592, "+91 44 2855 4548", "O+", "WHOLE_BLOOD", 60));
        bloodRepository.save(new BloodInventory("Indian Red Cross Society Blood Centre", "Red Cross Tamil Nadu", "50, Montieth Road, Egmore, Chennai", "Chennai", "Tamil Nadu", 13.0674, 80.2592, "+91 44 2855 4548", "AB-", "WHOLE_BLOOD", 6));
        bloodRepository.save(new BloodInventory("Apollo Hospitals Blood Centre Greams Road", "Apollo Hospitals Chennai", "21 Greams Lane, Thousand Lights, Chennai", "Chennai", "Tamil Nadu", 13.0604, 80.2508, "+91 44 2829 0200", "A+", "PLATELETS", 30));
        bloodRepository.save(new BloodInventory("Apollo Hospitals Blood Centre Greams Road", "Apollo Hospitals Chennai", "21 Greams Lane, Thousand Lights, Chennai", "Chennai", "Tamil Nadu", 13.0604, 80.2508, "+91 44 2829 0200", "B+", "PRBC", 38));
        bloodRepository.save(new BloodInventory("SIMS Hospital Blood Centre", "SIMS Hospital Vadapalani", "No. 1 Jawaharlal Nehru Salai, Vadapalani, Chennai", "Chennai", "Tamil Nadu", 13.0505, 80.2105, "+91 44 4567 4567", "O+", "WHOLE_BLOOD", 28));
        bloodRepository.save(new BloodInventory("MIOT International Blood Bank", "MIOT International", "Mount Poonamallee Road, Manapakkam, Chennai", "Chennai", "Tamil Nadu", 13.0232, 80.1788, "+91 44 4200 2288", "B+", "WHOLE_BLOOD", 35));

        // --- MAHARASHTRA / MUMBAI ---
        bloodRepository.save(new BloodInventory("KEM Hospital Blood Centre", "KEM Hospital & Seth GS Medical College (Govt)", "Acharya Donde Marg, Parel, Mumbai", "Mumbai", "Maharashtra", 19.0028, 72.8424, "+91 22 2410 7000", "O+", "WHOLE_BLOOD", 48));
        bloodRepository.save(new BloodInventory("KEM Hospital Blood Centre", "KEM Hospital & Seth GS Medical College (Govt)", "Acharya Donde Marg, Parel, Mumbai", "Mumbai", "Maharashtra", 19.0028, 72.8424, "+91 22 2410 7000", "A+", "WHOLE_BLOOD", 32));
        bloodRepository.save(new BloodInventory("Tata Memorial Hospital Blood Bank", "Tata Memorial Centre (Govt)", "Dr. E Borges Road, Parel, Mumbai", "Mumbai", "Maharashtra", 19.0062, 72.8436, "+91 22 2417 7000", "B+", "PLATELETS", 40));
        bloodRepository.save(new BloodInventory("Tata Memorial Hospital Blood Bank", "Tata Memorial Centre (Govt)", "Dr. E Borges Road, Parel, Mumbai", "Mumbai", "Maharashtra", 19.0062, 72.8436, "+91 22 2417 7000", "O-", "WHOLE_BLOOD", 10));
        bloodRepository.save(new BloodInventory("Sir H.N. Reliance Foundation Blood Centre", "Reliance Foundation Hospital", "Prarthana Samaj, Girgaon, Mumbai", "Mumbai", "Maharashtra", 18.9567, 72.8198, "+91 22 6130 5000", "O+", "WHOLE_BLOOD", 36));

        // --- DELHI & NCR ---
        bloodRepository.save(new BloodInventory("AIIMS New Delhi Main Blood Bank", "AIIMS New Delhi (Govt)", "Sri Aurobindo Marg, Ansari Nagar, New Delhi", "Delhi", "Delhi", 28.5672, 77.2100, "+91 11 2658 8500", "O+", "WHOLE_BLOOD", 60));
        bloodRepository.save(new BloodInventory("AIIMS New Delhi Main Blood Bank", "AIIMS New Delhi (Govt)", "Sri Aurobindo Marg, Ansari Nagar, New Delhi", "Delhi", "Delhi", 28.5672, 77.2100, "+91 11 2658 8500", "O-", "WHOLE_BLOOD", 15));
        bloodRepository.save(new BloodInventory("AIIMS New Delhi Main Blood Bank", "AIIMS New Delhi (Govt)", "Sri Aurobindo Marg, Ansari Nagar, New Delhi", "Delhi", "Delhi", 28.5672, 77.2100, "+91 11 2658 8500", "B+", "PRBC", 50));
        bloodRepository.save(new BloodInventory("Safdarjung Hospital Blood Bank", "Safdarjung Hospital (Govt)", "Ring Road, Opposite AIIMS, New Delhi", "Delhi", "Delhi", 28.5701, 77.2078, "+91 11 2616 5060", "A+", "WHOLE_BLOOD", 40));
        bloodRepository.save(new BloodInventory("Indraprastha Apollo Blood Bank", "Apollo Hospitals Delhi", "Delhi-Mathura Road, Sarita Vihar, New Delhi", "Delhi", "Delhi", 28.5355, 77.2875, "+91 11 2692 5858", "AB+", "PLATELETS", 28));

        // --- KARNATAKA / BENGALURU ---
        bloodRepository.save(new BloodInventory("Victoria Hospital Govt Blood Bank", "Bangalore Medical College (Govt)", "Fort Road, KR Market, Bengaluru", "Bengaluru", "Karnataka", 12.9642, 77.5756, "+91 80 2670 1150", "O+", "WHOLE_BLOOD", 44));
        bloodRepository.save(new BloodInventory("Victoria Hospital Govt Blood Bank", "Bangalore Medical College (Govt)", "Fort Road, KR Market, Bengaluru", "Bengaluru", "Karnataka", 12.9642, 77.5756, "+91 80 2670 1150", "A+", "WHOLE_BLOOD", 30));
        bloodRepository.save(new BloodInventory("Narayana Health City Blood Bank", "Narayana Health City", "Bommasandra Industrial Area, Bengaluru", "Bengaluru", "Karnataka", 12.8222, 77.6895, "+91 80 7122 2222", "B+", "PRBC", 48));
        bloodRepository.save(new BloodInventory("Manipal Hospital Blood Centre", "Manipal Hospital", "HAL Old Airport Road, Bengaluru", "Bengaluru", "Karnataka", 12.9592, 77.6534, "+91 80 2502 4444", "O+", "PLATELETS", 32));

        // --- ODISHA / BHUBANESWAR ---
        bloodRepository.save(new BloodInventory("AIIMS Bhubaneswar Blood Bank", "AIIMS Bhubaneswar (Govt)", "Sijua, Patrapada, Bhubaneswar", "Bhubaneswar", "Odisha", 20.2312, 85.7745, "+91 674 247 6789", "O+", "WHOLE_BLOOD", 40));
        bloodRepository.save(new BloodInventory("AIIMS Bhubaneswar Blood Bank", "AIIMS Bhubaneswar (Govt)", "Sijua, Patrapada, Bhubaneswar", "Bhubaneswar", "Odisha", 20.2312, 85.7745, "+91 674 247 6789", "B+", "WHOLE_BLOOD", 30));
        bloodRepository.save(new BloodInventory("Apollo Hospitals Bhubaneswar Blood Centre", "Apollo Hospitals BBSR", "Sainik School Road, Bhubaneswar", "Bhubaneswar", "Odisha", 20.3087, 85.8331, "+91 674 666 1066", "A+", "PRBC", 26));
    }

    private void seedLabTests() {
        labRepository.deleteAll();
        labTestMongoRepository.deleteAll();

        // --- WEST BENGAL / KOLKATA ---
        saveLabTest(new LabTest("Complete Blood Count (CBC) with ESR", "Blood", "Suraksha Diagnostics (Salt Lake)", "Kolkata", "West Bengal", 22.5867, 88.4178, 320.0, true, false, 6, "No special fasting required. Sample collected via routine venipuncture."));
        saveLabTest(new LabTest("HbA1c Glycated Hemoglobin (3-Month Sugar)", "Blood", "Apollo Diagnostics (Kolkata)", "Kolkata", "West Bengal", 22.5744, 88.3995, 480.0, true, false, 6, "Evaluates 3-month average blood glucose. Fasting not strictly required."));
        saveLabTest(new LabTest("Comprehensive Lipid Profile (Cholesterol & HDL)", "Blood", "Pulse Diagnostics (Sarat Bose Road)", "Kolkata", "West Bengal", 22.5320, 88.3530, 600.0, true, true, 10, "Strict 10-12 hours overnight fasting mandatory. Water permitted."));
        saveLabTest(new LabTest("Thyroid Function Profile (T3, T4, TSH)", "Blood", "Dr. Lal PathLabs (Park Street)", "Kolkata", "West Bengal", 22.5510, 88.3520, 450.0, true, true, 8, "Morning fasting sample recommended before taking thyroid medications."));
        saveLabTest(new LabTest("Renal / Kidney Function Test (KFT)", "Blood", "SRL Diagnostics (Gariahat)", "Kolkata", "West Bengal", 22.5180, 88.3660, 650.0, true, false, 8, "Measures Creatinine, Urea, and Electrolytes. Stay normally hydrated."));
        saveLabTest(new LabTest("High-Resolution Chest CT Scan (HRCT)", "Imaging", "Quadra Medical Services (Hazra Road)", "Kolkata", "West Bengal", 22.5260, 88.3510, 2900.0, false, false, 12, "Wear comfortable cotton clothing without metallic buttons or jewelry."));
        saveLabTest(new LabTest("Liver Function Test (LFT) 11 Parameters", "Blood", "Suraksha Diagnostics (Salt Lake)", "Kolkata", "West Bengal", 22.5867, 88.4178, 550.0, true, true, 8, "Overnight 8-hour fasting recommended for accurate enzyme levels."));

        // --- TELANGANA / HYDERABAD ---
        saveLabTest(new LabTest("Complete Blood Count (CBC) with Platelets", "Blood", "Vijaya Diagnostic Centre (Himayatnagar)", "Hyderabad", "Telangana", 17.4015, 78.4890, 320.0, true, false, 6, "Screen for anemia, infection, platelet count. Home collection available."));
        saveLabTest(new LabTest("Fasting Blood Sugar & HbA1c Diabetes Screen", "Blood", "Apollo Diagnostics (Jubilee Hills)", "Hyderabad", "Telangana", 17.4285, 78.4110, 490.0, true, true, 6, "10-12 hours overnight fasting required. Water permitted."));
        saveLabTest(new LabTest("Lipid Profile & Cardiac Risk Panel", "Blood", "Lucid Medical Diagnostics (Banjara Hills)", "Hyderabad", "Telangana", 17.4165, 78.4480, 580.0, true, true, 8, "Evaluates Total Cholesterol, HDL, LDL, Triglycerides. Fasting required."));
        saveLabTest(new LabTest("NIMS Comprehensive Pathology & Renal Profile (Govt)", "Pathology", "NIMS Diagnostic Central Pathology (Govt)", "Hyderabad", "Telangana", 17.4239, 78.4529, 300.0, false, true, 12, "Government apex hospital clinical pathology laboratory service."));
        saveLabTest(new LabTest("Osmania General Hospital Central Lab Panel (Govt)", "Blood", "Osmania Hospital Diagnostic Services (Govt)", "Hyderabad", "Telangana", 17.3685, 78.4735, 200.0, false, false, 8, "Subsidized government teaching hospital diagnostic testing."));
        saveLabTest(new LabTest("Liver Function Test (LFT) & Viral Hepatitis Screen", "Blood", "AIG Diagnostics (Gachibowli)", "Hyderabad", "Telangana", 17.4422, 78.3625, 650.0, true, true, 8, "Advanced hepatobiliary enzyme and protein quantification."));
        saveLabTest(new LabTest("High-Resolution MRI Brain & Spine Screening", "Imaging", "Vijaya Diagnostic Centre (Banjara Hills)", "Hyderabad", "Telangana", 17.4165, 78.4480, 4200.0, false, false, 12, "3 Tesla Silent Scan MRI. Remove all metallic implants and accessories."));

        // --- TAMIL NADU / CHENNAI ---
        saveLabTest(new LabTest("Complete Hemogram (CBC + ESR + Peripheral Smear)", "Blood", "Lister Metropolis Diagnostics (Nungambakkam)", "Chennai", "Tamil Nadu", 13.0600, 80.2400, 350.0, true, false, 6, "Routine blood count with automated hematology analyzer."));
        saveLabTest(new LabTest("Rajiv Gandhi Govt Hospital Master Health Lab (Govt)", "Blood", "Rajiv Gandhi Govt General Hospital Diagnostics (Govt)", "Chennai", "Tamil Nadu", 13.0805, 80.2785, 180.0, false, true, 8, "Apex government teaching hospital diagnostic facility."));
        saveLabTest(new LabTest("HbA1c & Fasting Insulin Resistance Panel", "Blood", "Apollo Diagnostics (Greams Road)", "Chennai", "Tamil Nadu", 13.0604, 80.2508, 520.0, true, true, 6, "Gold-standard HPLC glycated hemoglobin & insulin evaluation."));
        saveLabTest(new LabTest("Stanley Govt Medical College Pathology Screen (Govt)", "Pathology", "Stanley Govt Hospital Central Lab (Govt)", "Chennai", "Tamil Nadu", 13.1075, 80.2878, 150.0, false, false, 12, "Government tertiary clinical pathology and histopathology testing."));
        saveLabTest(new LabTest("Comprehensive Thyroid & Hormone Panel (T3, T4, TSH)", "Blood", "Neuberg Ehrlich Diagnostics (Royapettah)", "Chennai", "Tamil Nadu", 13.0520, 80.2600, 480.0, true, true, 8, "Chemiluminescence immunoassay testing for thyroid dysfunctions."));
        saveLabTest(new LabTest("Ultrasound Whole Abdomen & Pelvis", "Imaging", "Anderson Diagnostics (Purasawalkam)", "Chennai", "Tamil Nadu", 13.0880, 80.2550, 1200.0, false, true, 4, "Overnight fasting and full urinary bladder required prior to scan."));
        saveLabTest(new LabTest("Renal & Electrolyte Profile (KFT + Na/K/Cl)", "Blood", "Lister Metropolis Diagnostics (Adyar)", "Chennai", "Tamil Nadu", 13.0060, 80.2550, 620.0, true, false, 6, "Evaluates serum creatinine, BUN, uric acid, and key electrolytes."));

        // --- MAHARASHTRA / MUMBAI ---
        saveLabTest(new LabTest("Complete Blood Count (CBC) with Absolute Counts", "Blood", "Suburban Diagnostics (Andheri West)", "Mumbai", "Maharashtra", 19.1190, 72.8470, 350.0, true, false, 6, "NABL accredited automated cell count with differential smear."));
        saveLabTest(new LabTest("KEM Hospital Central Diagnostic Lab Panel (Govt)", "Blood", "KEM Hospital Pathology Services (Govt)", "Mumbai", "Maharashtra", 19.0028, 72.8424, 200.0, false, false, 8, "Subsidized government municipal hospital clinical testing."));
        saveLabTest(new LabTest("HbA1c & Average Estimated Glucose", "Blood", "Metropolis Healthcare (Worli)", "Mumbai", "Maharashtra", 19.0150, 72.8180, 480.0, true, false, 6, "High-performance liquid chromatography diabetes monitoring."));
        saveLabTest(new LabTest("Comprehensive Lipid Profile & Apolipoproteins", "Blood", "Dr. Lal PathLabs (Dadar West)", "Mumbai", "Maharashtra", 19.0180, 72.8420, 620.0, true, true, 10, "10-12 hour fasting mandatory for accurate lipid quantification."));
        saveLabTest(new LabTest("Whole Body PET-CT & High-Resolution Imaging", "Imaging", "Sir H.N. Reliance Foundation Diagnostic Centre", "Mumbai", "Maharashtra", 18.9567, 72.8198, 9500.0, false, true, 24, "State-of-the-art molecular imaging with digital PET-CT detector."));

        // --- DELHI & NCR ---
        saveLabTest(new LabTest("AIIMS Central Clinical Diagnostic Panel (Govt)", "Blood", "AIIMS New Delhi Diagnostic Laboratory (Govt)", "Delhi", "Delhi", 28.5672, 77.2100, 180.0, false, false, 8, "Apex national institute clinical biochemistry and hematology."));
        saveLabTest(new LabTest("Complete Blood Count (CBC) with ESR", "Blood", "Dr. Lal PathLabs National Reference Lab (Rohini)", "Delhi", "Delhi", 28.7150, 77.1180, 350.0, true, false, 6, "CAP & NABL accredited national reference laboratory testing."));
        saveLabTest(new LabTest("HbA1c & Fasting Blood Glucose", "Blood", "SRL / Agilus Diagnostics (Saket)", "Delhi", "Delhi", 28.5240, 77.2170, 490.0, true, true, 6, "Accredited diabetes management profile with automated home pickup."));
        saveLabTest(new LabTest("Comprehensive Kidney & Liver Health Profile", "Blood", "Apollo Diagnostics (Sarita Vihar)", "Delhi", "Delhi", 28.5355, 77.2875, 850.0, true, true, 8, "Complete KFT + LFT panel testing 22 vital organ parameters."));

        // --- KARNATAKA / BENGALURU ---
        saveLabTest(new LabTest("Complete Blood Count (CBC) & Peripheral Smear", "Blood", "Anand Diagnostic Laboratory (Shivajinagar)", "Bengaluru", "Karnataka", 12.9830, 77.6030, 340.0, true, false, 6, "Premier 50-year established NABL accredited clinical pathology lab."));
        saveLabTest(new LabTest("Victoria Hospital Govt Diagnostic Pathology (Govt)", "Pathology", "Victoria Hospital Central Lab (Govt)", "Bengaluru", "Karnataka", 12.9642, 77.5756, 180.0, false, false, 8, "Subsidized government teaching hospital diagnostic testing."));
        saveLabTest(new LabTest("HbA1c & Fasting Lipid Profile", "Blood", "Manipal Hospital Diagnostic Services", "Bengaluru", "Karnataka", 12.9592, 77.6534, 650.0, true, true, 8, "Cardiac and metabolic panel with digital reports in locker."));
        saveLabTest(new LabTest("Cardiac Enzyme Profile (Troponin-I, CK-MB, hs-CRP)", "Cardiac", "Narayana Health City Diagnostics", "Bengaluru", "Karnataka", 12.8222, 77.6895, 1100.0, true, false, 4, "Critical cardiac biomarker assessment for ischemic heart conditions."));

        // --- ODISHA / BHUBANESWAR ---
        saveLabTest(new LabTest("AIIMS Bhubaneswar Central Laboratory Panel (Govt)", "Blood", "AIIMS Bhubaneswar Diagnostic Lab (Govt)", "Bhubaneswar", "Odisha", 20.2312, 85.7745, 180.0, false, false, 8, "Autonomous national government teaching institute testing."));
        saveLabTest(new LabTest("Complete Hemogram & Diabetic Screen (CBC + HbA1c)", "Blood", "Apollo Diagnostics (Unit 15)", "Bhubaneswar", "Odisha", 20.3087, 85.8331, 550.0, true, true, 6, "Advanced diagnostic profile with home phlebotomist sample collection."));
        saveLabTest(new LabTest("Renal & Liver Function Comprehensive Profile", "Blood", "KIMS Central Pathology Lab (Patia)", "Bhubaneswar", "Odisha", 20.3533, 85.8197, 680.0, true, true, 8, "Complete organ function assessment with accredited reference standards."));
    }

    private void seedVaccinations() {
        vaccinationMongoRepository.deleteAll();
        vaccinationRecordRepository.deleteAll();
        // Authentic behavior: No hardcoded dummy vaccination records are seeded.
        // Records are registered by authenticated users or healthcare facility staff.
    }

    private void seedMedicalRecords() {
        mongoRecordRepository.deleteAll();

        mongoRecordRepository.save(new MedicalRecordDocument(
                "google_arindam", "Arindam Sahoo", "BLOOD_TEST", "Complete Blood Count (CBC) with ESR",
                "Apollo Diagnostics (Kolkata)", "Dr. Kunal Sarkar", LocalDate.now().minusDays(5),
                "Hemoglobin: 14.8 g/dL (Normal). Platelets: 240,000 /mcL (Normal). WBC: 7,200 /mcL. ESR: 9 mm/hr.",
                "Normal hematological profile. All cell lines within reference ranges. No acute infection indicated.",
                "cbc_report_arindam.pdf", "PDF", "1.4 MB"
        ));

        mongoRecordRepository.save(new MedicalRecordDocument(
                "google_arindam", "Arindam Sahoo", "PRESCRIPTION", "Comprehensive Cardiac Consultation & Rx",
                "Apollo Multispeciality Hospitals Kolkata", "Dr. Kunal Sarkar", LocalDate.now().minusDays(14),
                "Metformin 500mg (1-0-1 after food), Telmisartan 40mg (1-0-0 morning), Atorvastatin 10mg (0-0-1 night).",
                "Prescription generated following routine cardiac review. BP 120/80 mmHg. Exercise 30 mins daily advised.",
                "rx_cardiac_consult.pdf", "PDF", "850 KB"
        ));

        mongoRecordRepository.save(new MedicalRecordDocument(
                "google_arindam", "Arindam Sahoo", "LAB_REPORT", "HbA1c & Fasting Metabolic Panel",
                "Suraksha Diagnostics Salt Lake", "Dr. Subhashish Ghosh", LocalDate.now().minusDays(28),
                "HbA1c: 5.6% (Optimal glycemic control). Fasting Blood Sugar: 98 mg/dL. Serum Creatinine: 0.9 mg/dL.",
                "Excellent glycemic control maintained. Continue dietary compliance and current exercise routine.",
                "hba1c_metabolic_panel.pdf", "PDF", "1.1 MB"
        ));
    }

    private void seedMedicines() {
        if (medicineRepository.count() < 10) {
            // 1. Diabetes & Endocrine
            Medicine m1 = new Medicine("Metformin (Glycomet 500 SR)", "Metformin Hydrochloride", "500 mg", "Tablet", "AFTER_FOOD", 24, 60, "Take with or right after meals to avoid stomach upset.");
            m1.setPrescribingDoctor("Dr. Subhashish Ghosh");
            medicineRepository.save(m1);
            scheduleRepository.save(new MedicineSchedule(m1, "MORNING", LocalTime.of(8, 30), "1 Tablet", "Take after breakfast"));
            scheduleRepository.save(new MedicineSchedule(m1, "NIGHT", LocalTime.of(20, 30), "1 Tablet", "Take after dinner"));

            Medicine m1b = new Medicine("Glimepiride + Metformin (Amaryl M1)", "Glimepiride 1mg + Metformin 500mg SR", "500 mg", "Tablet", "AFTER_FOOD", 30, 60, "Dual action blood sugar regulation. Take with breakfast.");
            medicineRepository.save(m1b);

            Medicine m1c = new Medicine("Dapagliflozin 10mg (Forxiga 10)", "Dapagliflozin Propanediol", "10 mg", "Tablet", "NO_RELATION", 28, 30, "SGLT-2 inhibitor for cardio-renal & glycemic protection.");
            medicineRepository.save(m1c);

            Medicine m1d = new Medicine("Human Insulin (Mixtard 30/70 Penfill)", "Biphasic Isophane Insulin 100 IU/ml", "3 ml Cartridge", "Injection", "BEFORE_FOOD", 5, 5, "Subcutaneous administration 15-30 mins before major meals. Keep refrigerated.");
            medicineRepository.save(m1d);

            Medicine m1e = new Medicine("Thyroxine Sodium 50mcg (Eltroxin 50)", "Levothyroxine Sodium", "50 mcg", "Tablet", "BEFORE_FOOD", 50, 100, "Take first thing in the morning empty stomach 30 mins before tea/coffee.");
            medicineRepository.save(m1e);

            Medicine m1f = new Medicine("Thyroxine Sodium 100mcg (Thyronorm 100)", "Levothyroxine Sodium", "100 mcg", "Tablet", "BEFORE_FOOD", 60, 100, "Morning empty stomach for hypothyroidism management.");
            medicineRepository.save(m1f);

            // 2. Cardiology & Hypertension
            Medicine m2 = new Medicine("Telmisartan (Telma 40)", "Telmisartan", "40 mg", "Tablet", "AFTER_FOOD", 18, 30, "Blood pressure control. Take consistently in morning.");
            m2.setPrescribingDoctor("Dr. Kunal Sarkar");
            medicineRepository.save(m2);
            scheduleRepository.save(new MedicineSchedule(m2, "MORNING", LocalTime.of(9, 0), "1 Tablet", "Take after breakfast"));

            Medicine m2b = new Medicine("Telmisartan + Amlodipine (Telma-AM)", "Telmisartan 40mg + Amlodipine 5mg", "45 mg", "Tablet", "AFTER_FOOD", 30, 30, "Dual combination antihypertensive for stage-2 BP control.");
            medicineRepository.save(m2b);

            Medicine m2c = new Medicine("Amlodipine 5mg (Amlong 5)", "Amlodipine Besylate", "5 mg", "Tablet", "NO_RELATION", 30, 30, "Calcium channel blocker for peripheral vasodilation & hypertension.");
            medicineRepository.save(m2c);

            Medicine m2d = new Medicine("Atorvastatin (Atorva 10)", "Atorvastatin Calcium", "10 mg", "Tablet", "AFTER_FOOD", 26, 30, "Cholesterol control. Best taken in the evening or bedtime.");
            m2d.setPrescribingDoctor("Dr. Kunal Sarkar");
            medicineRepository.save(m2d);
            scheduleRepository.save(new MedicineSchedule(m2d, "NIGHT", LocalTime.of(21, 30), "1 Tablet", "Take before sleep"));

            Medicine m2e = new Medicine("Atorvastatin 20mg (Storvas 20)", "Atorvastatin Calcium", "20 mg", "Tablet", "AFTER_FOOD", 30, 30, "High-intensity lipid lowering for cardiovascular risk reduction.");
            medicineRepository.save(m2e);

            Medicine m2f = new Medicine("Rosuvastatin 10mg (Rozavel 10)", "Rosuvastatin Calcium", "10 mg", "Tablet", "AFTER_FOOD", 30, 30, "Potent HMG-CoA reductase inhibitor for dyslipidemia.");
            medicineRepository.save(m2f);

            Medicine m2g = new Medicine("Aspirin 75mg (Ecosprin 75)", "Acetylsalicylic Acid Gastro-resistant", "75 mg", "Tablet", "AFTER_FOOD", 60, 60, "Antiplatelet blood thinner for cardiovascular & stroke prevention.");
            medicineRepository.save(m2g);

            Medicine m2h = new Medicine("Clopidogrel 75mg (Clopilet 75)", "Clopidogrel Bisulphate", "75 mg", "Tablet", "AFTER_FOOD", 30, 30, "Platelet aggregation inhibitor post-stent and cardiac care.");
            medicineRepository.save(m2h);

            Medicine m2i = new Medicine("Metoprolol Succinate 25mg (Metolar XR 25)", "Metoprolol Succinate Extended Release", "25 mg", "Tablet", "WITH_FOOD", 30, 30, "Beta-blocker for tachycardia, angina, and blood pressure.");
            medicineRepository.save(m2i);

            // 3. Gastrointestinal & Acid Reflux
            Medicine m3 = new Medicine("Pantoprazole (Pan 40)", "Pantoprazole Gastro-resistant", "40 mg", "Tablet", "BEFORE_FOOD", 4, 30, "Acid reflux prevention. Take 30 mins before morning breakfast.");
            m3.setPrescribingDoctor("Dr. Debabrata Roy");
            medicineRepository.save(m3);
            scheduleRepository.save(new MedicineSchedule(m3, "MORNING", LocalTime.of(7, 30), "1 Tablet", "Take empty stomach with warm water"));

            Medicine m3b = new Medicine("Rabeprazole + Domperidone (Razo-D)", "Rabeprazole 20mg + Domperidone 30mg SR", "50 mg", "Capsule", "BEFORE_FOOD", 20, 30, "Proton pump inhibitor + prokinetic for severe GERD & nausea.");
            medicineRepository.save(m3b);

            Medicine m3c = new Medicine("Omeprazole 20mg (Omez 20)", "Omeprazole Magnesium", "20 mg", "Capsule", "BEFORE_FOOD", 30, 30, "Gastric acid suppression for heartburn and peptic ulcer relief.");
            medicineRepository.save(m3c);

            Medicine m3d = new Medicine("Esomeprazole 40mg (Nexpro 40)", "Esomeprazole Magnesium", "40 mg", "Tablet", "BEFORE_FOOD", 30, 30, "Potent S-isomer PPI for erosive esophagitis & acid hypersecretion.");
            medicineRepository.save(m3d);

            Medicine m3e = new Medicine("Digene Antacid Gel 200ml", "Aluminium & Magnesium Hydroxide + Simethicone", "200 ml", "Syrup", "AFTER_FOOD", 1, 1, "Quick relief cooling antacid for acidity, gas, and indigestion.");
            medicineRepository.save(m3e);

            Medicine m3f = new Medicine("Ondansetron 4mg (Emeset 4)", "Ondansetron Hydrochloride", "4 mg", "Tablet", "BEFORE_FOOD", 10, 10, "Fast-acting antiemetic for nausea and vomiting prevention.");
            medicineRepository.save(m3f);

            // 4. Antibiotics & Anti-Infectives
            Medicine m4a = new Medicine("Amoxicillin + Clavulanate 625mg (Augmentin 625)", "Amoxicillin 500mg + Clavulanic Acid 125mg", "625 mg", "Tablet", "WITH_FOOD", 14, 14, "Broad-spectrum penicillin antibiotic for chest, ENT, dental & skin infections.");
            medicineRepository.save(m4a);

            Medicine m4b = new Medicine("Azithromycin 500mg (Azithral 500)", "Azithromycin Dihydrate", "500 mg", "Tablet", "BEFORE_FOOD", 6, 6, "Macrolide antibiotic for respiratory tract infections and sinusitis.");
            medicineRepository.save(m4b);

            Medicine m4c = new Medicine("Ciprofloxacin 500mg (Ciplox 500)", "Ciprofloxacin Hydrochloride", "500 mg", "Tablet", "AFTER_FOOD", 10, 10, "Fluoroquinolone antibiotic for urinary tract, typhoid, and GI infections.");
            medicineRepository.save(m4c);

            Medicine m4d = new Medicine("Ofloxacin + Ornidazole (O2 Tablet)", "Ofloxacin 200mg + Ornidazole 500mg", "700 mg", "Tablet", "AFTER_FOOD", 10, 10, "Dual antimicrobial for bacterial diarrhea, amoebiasis, and dysentery.");
            medicineRepository.save(m4d);

            Medicine m4e = new Medicine("Cefixime 200mg (Zifi 200)", "Cefixime Trihydrate", "200 mg", "Tablet", "AFTER_FOOD", 10, 10, "3rd generation cephalosporin for typhoid fever, bronchitis & UTI.");
            medicineRepository.save(m4e);

            Medicine m4f = new Medicine("Doxycycline 100mg (Doxicip 100)", "Doxycycline Hyclate", "100 mg", "Capsule", "AFTER_FOOD", 10, 10, "Broad-spectrum tetracycline for acne, respiratory, and vector-borne fevers.");
            medicineRepository.save(m4f);

            Medicine m4g = new Medicine("Fluconazole 150mg (Forcan 150)", "Fluconazole", "150 mg", "Tablet", "AFTER_FOOD", 3, 3, "Single dose antifungal for systemic and cutaneous fungal infections.");
            medicineRepository.save(m4g);

            // 5. Pain Relief & Anti-Inflammatory
            Medicine m5a = new Medicine("Paracetamol 650mg (Dolo 650)", "Paracetamol IP", "650 mg", "Tablet", "AFTER_FOOD", 30, 30, "First-line analgesic and antipyretic for high fever, headache & body aches.");
            medicineRepository.save(m5a);

            Medicine m5b = new Medicine("Paracetamol 500mg (Calpol 500)", "Paracetamol IP", "500 mg", "Tablet", "AFTER_FOOD", 30, 30, "Mild to moderate fever and headache relief. Safe for gastric tolerance.");
            medicineRepository.save(m5b);

            Medicine m5c = new Medicine("Aceclofenac + Paracetamol (Zerodol-P)", "Aceclofenac 100mg + Paracetamol 325mg", "425 mg", "Tablet", "AFTER_FOOD", 20, 20, "NSAID pain reliever for joint pain, toothache, arthritis, and muscular sprain.");
            medicineRepository.save(m5c);

            Medicine m5d = new Medicine("Ibuprofen 400mg (Brufen 400)", "Ibuprofen IP", "400 mg", "Tablet", "AFTER_FOOD", 20, 20, "Anti-inflammatory pain relief for dental pain, menstrual cramps & fever.");
            medicineRepository.save(m5d);

            Medicine m5e = new Medicine("Mefenamic Acid + Dicyclomine (Meftal-Spas)", "Mefenamic Acid 250mg + Dicyclomine 10mg", "260 mg", "Tablet", "AFTER_FOOD", 10, 10, "Antispasmodic for severe abdominal colicky pain and menstrual cramps.");
            medicineRepository.save(m5e);

            // 6. Respiratory, Cough & Allergy
            Medicine m6a = new Medicine("Montelukast + Levocetirizine (Montair-LC)", "Montelukast Sodium 10mg + Levocetirizine 5mg", "15 mg", "Tablet", "NO_RELATION", 30, 30, "Antihistamine + leukotriene receptor antagonist for allergic rhinitis & asthma.");
            medicineRepository.save(m6a);

            Medicine m6b = new Medicine("Cetirizine 10mg (Cetzine 10)", "Cetirizine Hydrochloride", "10 mg", "Tablet", "NO_RELATION", 20, 20, "Fast relief from seasonal allergies, sneezing, runny nose, and hives.");
            medicineRepository.save(m6b);

            Medicine m6c = new Medicine("Budesonide 200mcg Inhaler (Budecort 200)", "Budesonide Metered Dose Inhaler", "200 Doses", "Inhaler", "NO_RELATION", 2, 2, "Corticosteroid preventer inhaler for chronic asthma and COPD control.");
            medicineRepository.save(m6c);

            Medicine m6d = new Medicine("Salbutamol 100mcg Inhaler (Asthalin)", "Salbutamol Sulphate", "200 Doses", "Inhaler", "NO_RELATION", 2, 2, "Fast-acting bronchodilator rescue inhaler for acute shortness of breath & wheezing.");
            medicineRepository.save(m6d);

            Medicine m6e = new Medicine("Benadryl DR Cough Syrup 100ml", "Dextromethorphan HBr + Chlorpheniramine", "100 ml", "Syrup", "AFTER_FOOD", 1, 1, "Soothes dry hacking cough, throat tickle, and bronchial irritation.");
            medicineRepository.save(m6e);

            Medicine m6f = new Medicine("Ascoril D+ Sugar-Free Cough Syrup", "Dextromethorphan + Phenylephrine + CPM", "100 ml", "Syrup", "AFTER_FOOD", 1, 1, "Triple action cough formula safe for diabetics.");
            medicineRepository.save(m6f);

            // 7. Vitamins, Minerals & Rehydration
            Medicine m7a = new Medicine("Vitamin D3 60,000 IU (Calcirol / Uprise-D3)", "Cholecalciferol IP", "60,000 IU", "Capsule", "AFTER_FOOD", 8, 8, "Weekly high-dose Vitamin D3 for bone density, immunity, and fatigue recovery.");
            medicineRepository.save(m7a);

            Medicine m7b = new Medicine("Vitamin B-Complex with B12 & Zinc (Becosules Z)", "B-Complex + Vitamin C + Zinc Sulphate", "Standard", "Capsule", "AFTER_FOOD", 30, 30, "Essential neurotropic vitamins for mouth ulcers, stamina, and nervous health.");
            medicineRepository.save(m7b);

            Medicine m7c = new Medicine("Calcium 500mg + Vitamin D3 (Shelcal 500)", "Calcium Carbonate 1250mg + Vit D3 250 IU", "500 mg", "Tablet", "AFTER_FOOD", 30, 30, "Bone strengthening and osteoporosis prevention for all age groups.");
            medicineRepository.save(m7c);

            Medicine m7d = new Medicine("Ferrous Ascorbate + Folic Acid (Orofer-XT)", "Elemental Iron 100mg + Folic Acid 1.5mg", "100 mg", "Tablet", "BEFORE_FOOD", 30, 30, "Hemoglobin replenisher for iron deficiency anemia & prenatal vitality.");
            medicineRepository.save(m7d);

            Medicine m7e = new Medicine("ORS Electral Powder 21.8g", "Oral Rehydration Salts (WHO Recommended Formula)", "21.8 g", "Powder", "NO_RELATION", 10, 10, "Restores vital body fluids, sodium, potassium & dextrose during dehydration.");
            medicineRepository.save(m7e);

            // 8. Dermatology & Topical First Aid
            Medicine m8a = new Medicine("Betadine 10% Antiseptic Ointment 20g", "Povidone-Iodine IP 10% w/w", "20 g", "Ointment", "NO_RELATION", 2, 2, "Broad-spectrum topical microbicide for minor cuts, burns, wounds & abrasions.");
            medicineRepository.save(m8a);

            Medicine m8b = new Medicine("Volini Pain Relief Gel 30g", "Diclofenac Diethylamine + Methyl Salicylate", "30 g", "Gel", "NO_RELATION", 2, 2, "Deep penetrating rapid muscle and joint pain relief gel.");
            medicineRepository.save(m8b);

            Medicine m8c = new Medicine("T-Bact 2% Ointment 7.5g", "Mupirocin IP 2% w/w", "7.5 g", "Ointment", "NO_RELATION", 2, 2, "Potent topical antibiotic for impetigo, folliculitis & bacterial skin infections.");
            medicineRepository.save(m8c);

            // 9. Vaccines & Immunization
            Medicine m9a = new Medicine("Influenza Quadrivalent Vaccine (Fluarix Tetra)", "Split Virion Inactivated Influenza Vaccine IP", "0.5 ml PFS", "Injection", "NO_RELATION", 5, 5, "Annual seasonal flu protection against Influenza A (H1N1, H3N2) and B strains.");
            medicineRepository.save(m9a);

            Medicine m9b = new Medicine("Hepatitis B Recombinant Vaccine (Engerix-B 20mcg)", "Hepatitis B Surface Antigen rDNA", "1 ml Vial", "Injection", "NO_RELATION", 5, 5, "Prevents acute and chronic Hepatitis B viral infection & liver cirrhosis.");
            medicineRepository.save(m9b);

            Medicine m9c = new Medicine("Tetanus & Adult Diphtheria (Td Vaccine)", "Purified Tetanus Toxoid 0.5ml", "0.5 ml Ampoule", "Injection", "NO_RELATION", 10, 10, "Emergency wound booster and routine prophylaxis against Tetanus (lockjaw).");
            medicineRepository.save(m9c);

            Medicine m9d = new Medicine("Pneumococcal Conjugate Vaccine (Prevenar 13)", "13-Valent Pneumococcal Polysaccharide Vaccine", "0.5 ml PFS", "Injection", "NO_RELATION", 3, 3, "Protects infants, elderly & immunocompromised from severe pneumonia and meningitis.");
            medicineRepository.save(m9d);
        }
    }

    private void seedPharmacyOrders() {
        if (pharmacyOrderRepository.count() == 0) {
            PharmacyOrder po1 = new PharmacyOrder(
                    "Arindam Sahoo", "+91 98300 12345",
                    "MG Road, Budge Budge, South 24 Parganas, Kolkata 700137",
                    "Apollo Pharmacy Budge Budge",
                    "Metformin 500mg (60 tabs), Telma 40mg (30 tabs)",
                    340.0
            );
            po1.setStatus("OUT_FOR_DELIVERY");
            po1.setEtaMinutes(18);
            po1.setDeliveryExecutiveName("Subrata Roy");
            po1.setDeliveryExecutivePhone("+91 98302 44556");
            pharmacyOrderRepository.save(po1);
        }
    }

    private void seedUsers() {
        if (userMongoRepository.findAllByEmail("arindam.sahoo@gmail.com").isEmpty()) {
            UserDocument user = new UserDocument("google_arindam", "arindam.sahoo", "Arindam Sahoo", "arindam.sahoo@gmail.com", "+91 98300 12345");
            user.setState("West Bengal");
            user.setCity("Kolkata");
            user.setLocality("Budge Budge / Salt Lake");
            user.setAddress("Budge Budge, South 24 Parganas, Kolkata, West Bengal 700137");
            user.setBloodGroup("O+");
            user.setEmergencyContactName("Debashis Sahoo");
            user.setEmergencyContactPhone("+91 98301 54321");
            user.setAllergies("None");
            userMongoRepository.save(user);
        }
    }
}
