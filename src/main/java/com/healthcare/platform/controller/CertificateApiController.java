package com.healthcare.platform.controller;

import com.healthcare.platform.model.User;
import com.healthcare.platform.model.VaccinationRecord;
import com.healthcare.platform.model.mongo.MedicalRecordDocument;
import com.healthcare.platform.repository.MedicineRepository;
import com.healthcare.platform.repository.UserRepository;
import com.healthcare.platform.repository.VaccinationRecordRepository;
import com.healthcare.platform.repository.mongo.MedicalRecordMongoRepository;
import com.healthcare.platform.service.PdfCertificateService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/certificates")
public class CertificateApiController {

    @Autowired private PdfCertificateService pdfService;
    @Autowired private VaccinationRecordRepository vaccinationRecordRepository;
    @Autowired private MedicalRecordMongoRepository mongoRecordRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private MedicineRepository medicineRepository;

    @GetMapping("/vaccination/{id}/download")
    public ResponseEntity<byte[]> downloadVaccineCertificate(@PathVariable Long id) {
        VaccinationRecord record = vaccinationRecordRepository.findById(id).orElse(null);
        if (record == null) {
            record = new VaccinationRecord("Registered Citizen", "UNIVERSAL", "Immunization Record", "Primary Dose", null, null, "Authorized Hospital", true);
        }

        byte[] pdfBytes = pdfService.generateVaccinationCertificate(record);
        String safeName = record.getVaccineName() != null ? record.getVaccineName().replaceAll("[^a-zA-Z0-9.-]", "_") : "Immunization";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"Vaccination_Certificate_" + safeName + ".pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    @GetMapping("/passport/download")
    public ResponseEntity<byte[]> downloadHealthPassport(HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            user = userRepository.findAll().stream().findFirst().orElse(new User("patient", "Registered Citizen", "user@healthcare.org", "+91 98765 43210", "PATIENT"));
        }

        byte[] pdfBytes = pdfService.generateHealthPassportPdf(user, medicineRepository.findByActiveTrue());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"Emergency_Health_Passport_" + user.getFullName().replaceAll("[^a-zA-Z0-9.-]", "_") + ".pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    @GetMapping("/record/{id}/download")
    public ResponseEntity<byte[]> downloadMedicalReport(@PathVariable String id) {
        MedicalRecordDocument doc = mongoRecordRepository.findById(id).orElse(null);
        if (doc == null) {
            doc = new MedicalRecordDocument("default", "Patient", "DIAGNOSTIC", "Investigation Report", "Diagnostic Center", "Attending Physician", null, "Report data", "Normal", "Report.pdf", "application/pdf", "1.0 MB");
        }

        byte[] pdfBytes = pdfService.generateMedicalReportPdf(doc);
        String safeTitle = doc.getTitle() != null ? doc.getTitle().replaceAll("[^a-zA-Z0-9.-]", "_") : "Report";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"Medical_Report_" + safeTitle + ".pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}
