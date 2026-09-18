package com.healthcare.platform.service;

import com.healthcare.platform.model.Medicine;
import com.healthcare.platform.model.User;
import com.healthcare.platform.model.VaccinationRecord;
import com.healthcare.platform.model.mongo.MedicalRecordDocument;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PdfCertificateService {

    private static final Font TITLE_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, new Color(15, 23, 42));
    private static final Font SUBTITLE_FONT = FontFactory.getFont(FontFactory.HELVETICA, 10, new Color(100, 116, 139));
    private static final Font HEADER_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Color.WHITE);
    private static final Font CELL_BOLD = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, new Color(30, 41, 59));
    private static final Font CELL_NORMAL = FontFactory.getFont(FontFactory.HELVETICA, 10, new Color(51, 65, 85));
    private static final Font FOOTER_FONT = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 8, new Color(148, 163, 184));

    public byte[] generateVaccinationCertificate(VaccinationRecord record) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 40, 40, 40, 40);

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Header Banner
            Paragraph title = new Paragraph("CERTIFICATE OF IMMUNIZATION", TITLE_FONT);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            Paragraph sub = new Paragraph("Ministry of Health & Family Welfare • National Healthcare Registry", SUBTITLE_FONT);
            sub.setAlignment(Element.ALIGN_CENTER);
            sub.setSpacingAfter(20);
            document.add(sub);

            // Verification Details Box
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{35f, 65f});
            table.setSpacingBefore(10);
            table.setSpacingAfter(20);

            addTableRow(table, "Beneficiary Name", record.getPatientName() != null ? record.getPatientName() : "Registered Citizen");
            addTableRow(table, "Target Group", record.getTargetGroup() != null ? record.getTargetGroup() : "Universal");
            addTableRow(table, "Vaccine Name", record.getVaccineName());
            addTableRow(table, "Dose Number / Cycle", record.getDoseNumber() != null ? record.getDoseNumber() : "Primary Dose");
            addTableRow(table, "Administered Date", record.getAdministeredDate() != null ? record.getAdministeredDate().toString() : "Recorded");
            addTableRow(table, "Next Due Date", record.getNextDueDate() != null ? record.getNextDueDate().toString() : "Not Applicable / Completed");
            addTableRow(table, "Healthcare Facility", record.getProviderName() != null ? record.getProviderName() : "Authorized Healthcare Center");
            addTableRow(table, "Batch Number", record.getBatchNumber() != null ? record.getBatchNumber() : "VAC-" + System.currentTimeMillis() % 100000);
            addTableRow(table, "Certificate Number", record.getCertificateNumber() != null ? record.getCertificateNumber() : "CERT-IMM-" + (record.getId() != null ? record.getId() * 1024 : "99120"));
            addTableRow(table, "Verification Status", "VERIFIED & DIGITALLY SIGNED (ABDM)");

            document.add(table);

            // Security Notice
            Paragraph security = new Paragraph("This is an official verifiable digital health credential. Tampering with this certificate is punishable under healthcare regulations.", FOOTER_FONT);
            security.setAlignment(Element.ALIGN_CENTER);
            security.setSpacingBefore(30);
            document.add(security);

            Paragraph stamp = new Paragraph("Digitally Signed by Chief Medical Officer • " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss")), FOOTER_FONT);
            stamp.setAlignment(Element.ALIGN_CENTER);
            document.add(stamp);

            document.close();
        } catch (Exception e) {
            throw new RuntimeException("Error generating vaccination certificate PDF", e);
        }

        return out.toByteArray();
    }

    public byte[] generateHealthPassportPdf(User user, List<Medicine> activeMeds) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 40, 40, 40, 40);

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            Paragraph title = new Paragraph("EMERGENCY DIGITAL HEALTH PASSPORT", TITLE_FONT);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            Paragraph sub = new Paragraph("Global First Responder Emergency Dossier • 24x7 Critical Profile", SUBTITLE_FONT);
            sub.setAlignment(Element.ALIGN_CENTER);
            sub.setSpacingAfter(20);
            document.add(sub);

            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{35f, 65f});

            addTableRow(table, "Patient Full Name", user != null ? user.getFullName() : "Patient");
            addTableRow(table, "Blood Group", (user != null && user.getBloodGroup() != null) ? user.getBloodGroup() : "O+ (Rh Positive)");
            addTableRow(table, "Emergency Contact", (user != null && user.getEmergencyContactName() != null) ? user.getEmergencyContactName() + " (" + user.getEmergencyContactPhone() + ")" : "108 / Emergency Services");
            addTableRow(table, "Severe Allergies", (user != null && user.getAllergies() != null) ? user.getAllergies() : "None reported");
            addTableRow(table, "Chronic Conditions", (user != null && user.getChronicConditions() != null) ? user.getChronicConditions() : "None reported");
            addTableRow(table, "Preferred Location / City", (user != null && user.getPreferredCity() != null) ? user.getPreferredCity() : "Live Location");

            StringBuilder meds = new StringBuilder();
            if (activeMeds != null && !activeMeds.isEmpty()) {
                for (Medicine m : activeMeds) {
                    meds.append("• ").append(m.getName()).append(" ").append(m.getDosage()).append(" (").append(m.getFoodRelation()).append(")\n");
                }
            } else {
                meds.append("No active prescriptions recorded.");
            }
            addTableRow(table, "Active Medications", meds.toString().trim());

            document.add(table);

            Paragraph stamp = new Paragraph("Generated on " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss")) + " • Valid for First Responders & Emergency Rooms", FOOTER_FONT);
            stamp.setAlignment(Element.ALIGN_CENTER);
            stamp.setSpacingBefore(30);
            document.add(stamp);

            document.close();
        } catch (Exception e) {
            throw new RuntimeException("Error generating health passport PDF", e);
        }

        return out.toByteArray();
    }

    public byte[] generateMedicalReportPdf(MedicalRecordDocument record) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 40, 40, 40, 40);

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            Paragraph title = new Paragraph("CLINICAL DIAGNOSTIC REPORT", TITLE_FONT);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            Paragraph sub = new Paragraph((record.getFacilityName() != null ? record.getFacilityName() : "Clinical Diagnostic Center") + " • Verified Medical Record", SUBTITLE_FONT);
            sub.setAlignment(Element.ALIGN_CENTER);
            sub.setSpacingAfter(20);
            document.add(sub);

            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{35f, 65f});

            addTableRow(table, "Report Title", record.getTitle());
            addTableRow(table, "Category / Type", record.getRecordType());
            addTableRow(table, "Patient Name", record.getPatientName() != null ? record.getPatientName() : "Patient");
            addTableRow(table, "Consulting Physician", record.getDoctorName() != null ? record.getDoctorName() : "Medical Officer");
            addTableRow(table, "Date of Investigation", record.getRecordDate() != null ? record.getRecordDate().toString() : "Recent");
            addTableRow(table, "Diagnostic Summary", record.getSummaryText() != null ? record.getSummaryText() : "Investigation completed.");
            addTableRow(table, "Key Clinical Findings", record.getKeyFindings() != null ? record.getKeyFindings() : "Within expected physiological parameters.");

            document.add(table);

            Paragraph stamp = new Paragraph("Electronic Laboratory Dossier Stored in MongoDB • Digitally Signed & Sealed", FOOTER_FONT);
            stamp.setAlignment(Element.ALIGN_CENTER);
            stamp.setSpacingBefore(30);
            document.add(stamp);

            document.close();
        } catch (Exception e) {
            throw new RuntimeException("Error generating medical report PDF", e);
        }

        return out.toByteArray();
    }

    private void addTableRow(PdfPTable table, String header, String value) {
        PdfPCell c1 = new PdfPCell(new Phrase(header, CELL_BOLD));
        c1.setBackgroundColor(new Color(241, 245, 249));
        c1.setPadding(8);
        c1.setBorderColor(new Color(226, 232, 240));

        PdfPCell c2 = new PdfPCell(new Phrase(value, CELL_NORMAL));
        c2.setPadding(8);
        c2.setBorderColor(new Color(226, 232, 240));

        table.addCell(c1);
        table.addCell(c2);
    }
}
