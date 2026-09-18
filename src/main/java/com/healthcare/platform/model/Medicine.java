package com.healthcare.platform.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "medicines")
public class Medicine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String genericName;
    private String dosage; // e.g. "500 mg", "1 tablet", "5 ml"
    private String form; // Tablet, Capsule, Syrup, Injection, Inhaler

    // Food timing relation
    @Column(nullable = false)
    private String foodRelation; // BEFORE_FOOD, AFTER_FOOD, WITH_FOOD, NO_RELATION

    private Integer remainingPills;
    private Integer totalPills;
    private Integer pillThresholdAlert = 5; // Alert if remaining <= 5

    private String prescriptionRef;
    private String prescribingDoctor;
    private LocalDate startDate;
    private LocalDate endDate; // Null if chronic / ongoing
    private boolean temporary = false;

    private String instructions;
    private boolean active = true;

    public Medicine() {}

    public Medicine(String name, String genericName, String dosage, String form, String foodRelation, Integer remainingPills, Integer totalPills, String instructions) {
        this.name = name;
        this.genericName = genericName;
        this.dosage = dosage;
        this.form = form;
        this.foodRelation = foodRelation;
        this.remainingPills = remainingPills;
        this.totalPills = totalPills;
        this.instructions = instructions;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getGenericName() { return genericName; }
    public void setGenericName(String genericName) { this.genericName = genericName; }

    public String getDosage() { return dosage; }
    public void setDosage(String dosage) { this.dosage = dosage; }

    public String getForm() { return form; }
    public void setForm(String form) { this.form = form; }

    public String getFoodRelation() { return foodRelation; }
    public void setFoodRelation(String foodRelation) { this.foodRelation = foodRelation; }

    public Integer getRemainingPills() { return remainingPills; }
    public void setRemainingPills(Integer remainingPills) { this.remainingPills = remainingPills; }

    public Integer getTotalPills() { return totalPills; }
    public void setTotalPills(Integer totalPills) { this.totalPills = totalPills; }

    public Integer getPillThresholdAlert() { return pillThresholdAlert; }
    public void setPillThresholdAlert(Integer pillThresholdAlert) { this.pillThresholdAlert = pillThresholdAlert; }

    public String getPrescriptionRef() { return prescriptionRef; }
    public void setPrescriptionRef(String prescriptionRef) { this.prescriptionRef = prescriptionRef; }

    public String getPrescribingDoctor() { return prescribingDoctor; }
    public void setPrescribingDoctor(String prescribingDoctor) { this.prescribingDoctor = prescribingDoctor; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public boolean isTemporary() { return temporary; }
    public void setTemporary(boolean temporary) { this.temporary = temporary; }

    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    @Transient
    public String getCategory() {
        String combined = ((name != null ? name : "") + " " + (genericName != null ? genericName : "") + " " + (instructions != null ? instructions : "")).toLowerCase();
        if (combined.contains("vaccine") || combined.contains("covaxin") || combined.contains("covishield") || combined.contains("rabivax") || combined.contains("hepatitis") || combined.contains("influenza") || combined.contains("typhoid") || combined.contains("tetanus") || combined.contains("bcg") || combined.contains("mmr")) {
            return "VACCINE";
        } else if (combined.contains("augmentin") || combined.contains("azithral") || combined.contains("cifran") || combined.contains("taxim") || combined.contains("clavam") || combined.contains("moxikind") || combined.contains("amoxicillin") || combined.contains("azithromycin") || combined.contains("cefixime") || combined.contains("antibiotic")) {
            return "ANTIBIOTIC";
        } else if (combined.contains("glycomet") || combined.contains("januvia") || combined.contains("rybelsus") || combined.contains("galvus") || combined.contains("lantus") || combined.contains("humalog") || combined.contains("metformin") || combined.contains("insulin") || combined.contains("diabetes")) {
            return "DIABETES";
        } else if (combined.contains("telma") || combined.contains("atorva") || combined.contains("ecosprin") || combined.contains("amlong") || combined.contains("metosartan") || combined.contains("concor") || combined.contains("rosuvas") || combined.contains("telmisartan") || combined.contains("atorvastatin") || combined.contains("cardiac") || combined.contains("hypertension")) {
            return "CARDIAC";
        } else if (combined.contains("dolo") || combined.contains("combiflam") || combined.contains("zerodol") || combined.contains("ultracet") || combined.contains("meftal") || combined.contains("paracetamol") || combined.contains("pain") || combined.contains("fever")) {
            return "PAIN";
        } else if (combined.contains("pan") || combined.contains("omez") || combined.contains("rantac") || combined.contains("digene") || combined.contains("duphalac") || combined.contains("cremaffin") || combined.contains("sporlac") || combined.contains("pantoprazole") || combined.contains("omeprazole") || combined.contains("antacid")) {
            return "GASTRO";
        } else if (combined.contains("shelcal") || combined.contains("becosules") || combined.contains("neurobion") || combined.contains("limcee") || combined.contains("supradyn") || combined.contains("evion") || combined.contains("calcium") || combined.contains("vitamin")) {
            return "VITAMIN";
        } else if (combined.contains("asthalin") || combined.contains("budecort") || combined.contains("foracort") || combined.contains("montek") || combined.contains("allegra") || combined.contains("alex") || combined.contains("montelukast") || combined.contains("levocetirizine") || combined.contains("salbutamol") || combined.contains("cough") || combined.contains("asthma")) {
            return "RESPIRATORY";
        }
        return "GENERAL";
    }

    @Transient
    public Double getEstimatedPrice() {
        String cat = getCategory();
        if ("VACCINE".equals(cat)) return 280.0;
        if ("ANTIBIOTIC".equals(cat)) return 195.0;
        if ("DIABETES".equals(cat)) return 165.0;
        if ("CARDIAC".equals(cat)) return 145.0;
        if ("PAIN".equals(cat)) return 45.0;
        if ("GASTRO".equals(cat)) return 85.0;
        if ("VITAMIN".equals(cat)) return 75.0;
        if ("RESPIRATORY".equals(cat)) return 120.0;
        return 85.0;
    }
}
