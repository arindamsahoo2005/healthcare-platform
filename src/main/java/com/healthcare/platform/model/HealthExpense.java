package com.healthcare.platform.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "health_expenses")
public class HealthExpense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String patientName;

    @Column(nullable = false)
    private String category; // DOCTOR_FEE, MEDICINE, LAB_TEST, HOSPITAL_BILL, INSURANCE_PREMIUM

    @Column(nullable = false)
    private Double amount;

    private LocalDate expenseDate = LocalDate.now();
    private String description;
    private String facilityOrVendor;
    private String paymentStatus = "PAID"; // PAID, REIMBURSEMENT_PENDING, REIMBURSED
    private String receiptFileName;

    public HealthExpense() {}

    public HealthExpense(String patientName, String category, Double amount, LocalDate expenseDate, String description, String facilityOrVendor, String paymentStatus) {
        this.patientName = patientName;
        this.category = category;
        this.amount = amount;
        this.expenseDate = expenseDate;
        this.description = description;
        this.facilityOrVendor = facilityOrVendor;
        this.paymentStatus = paymentStatus;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public LocalDate getExpenseDate() { return expenseDate; }
    public void setExpenseDate(LocalDate expenseDate) { this.expenseDate = expenseDate; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getFacilityOrVendor() { return facilityOrVendor; }
    public void setFacilityOrVendor(String facilityOrVendor) { this.facilityOrVendor = facilityOrVendor; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getReceiptFileName() { return receiptFileName; }
    public void setReceiptFileName(String receiptFileName) { this.receiptFileName = receiptFileName; }
}
