package com.healthcare.platform.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "pharmacy_orders")
public class PharmacyOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String patientName;
    private String contactPhone;
    private String deliveryAddress;
    private String pharmacyName = "Apollo Pharmacy Patia";

    private String orderedItems; // e.g. "Pantoprazole 40mg (15 tabs), Metformin 500mg (60 tabs)"
    private Double totalAmount;
    
    // Workflow: PLACED -> PHARMACIST_VERIFIED -> OUT_FOR_DELIVERY -> DELIVERED
    private String status = "PLACED";
    private Integer etaMinutes = 35;
    private String deliveryExecutiveName = "Ramesh Courier";
    private String deliveryExecutivePhone = "+91 98765 11223";

    private String prescriptionRef;
    private LocalDateTime orderedAt = LocalDateTime.now();

    public PharmacyOrder() {}

    public PharmacyOrder(String patientName, String contactPhone, String deliveryAddress, String pharmacyName, String orderedItems, Double totalAmount) {
        this.patientName = patientName;
        this.contactPhone = contactPhone;
        this.deliveryAddress = deliveryAddress;
        this.pharmacyName = pharmacyName;
        this.orderedItems = orderedItems;
        this.totalAmount = totalAmount;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }

    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }

    public String getPharmacyName() { return pharmacyName; }
    public void setPharmacyName(String pharmacyName) { this.pharmacyName = pharmacyName; }

    public String getOrderedItems() { return orderedItems; }
    public void setOrderedItems(String orderedItems) { this.orderedItems = orderedItems; }

    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getEtaMinutes() { return etaMinutes; }
    public void setEtaMinutes(Integer etaMinutes) { this.etaMinutes = etaMinutes; }

    public String getDeliveryExecutiveName() { return deliveryExecutiveName; }
    public void setDeliveryExecutiveName(String deliveryExecutiveName) { this.deliveryExecutiveName = deliveryExecutiveName; }

    public String getDeliveryExecutivePhone() { return deliveryExecutivePhone; }
    public void setDeliveryExecutivePhone(String deliveryExecutivePhone) { this.deliveryExecutivePhone = deliveryExecutivePhone; }

    public String getPrescriptionRef() { return prescriptionRef; }
    public void setPrescriptionRef(String prescriptionRef) { this.prescriptionRef = prescriptionRef; }

    public LocalDateTime getOrderedAt() { return orderedAt; }
    public void setOrderedAt(LocalDateTime orderedAt) { this.orderedAt = orderedAt; }
}
