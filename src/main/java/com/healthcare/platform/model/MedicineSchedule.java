package com.healthcare.platform.model;

import jakarta.persistence.*;
import java.time.LocalTime;

@Entity
@Table(name = "medicine_schedules")
public class MedicineSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "medicine_id", nullable = false)
    private Medicine medicine;

    private String timeOfDay; // MORNING, AFTERNOON, EVENING, NIGHT
    private LocalTime doseTime; // e.g. 22:00 (10:00 PM)
    private String daysOfWeek = "ALL"; // ALL or comma-separated days
    private String dosageAmount = "1 dose";
    private String customNotes;
    private boolean active = true;

    public MedicineSchedule() {}

    public MedicineSchedule(Medicine medicine, String timeOfDay, LocalTime doseTime, String dosageAmount, String customNotes) {
        this.medicine = medicine;
        this.timeOfDay = timeOfDay;
        this.doseTime = doseTime;
        this.dosageAmount = dosageAmount;
        this.customNotes = customNotes;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Medicine getMedicine() { return medicine; }
    public void setMedicine(Medicine medicine) { this.medicine = medicine; }

    public String getTimeOfDay() { return timeOfDay; }
    public void setTimeOfDay(String timeOfDay) { this.timeOfDay = timeOfDay; }

    public LocalTime getDoseTime() { return doseTime; }
    public void setDoseTime(LocalTime doseTime) { this.doseTime = doseTime; }

    public String getDaysOfWeek() { return daysOfWeek; }
    public void setDaysOfWeek(String daysOfWeek) { this.daysOfWeek = daysOfWeek; }

    public String getDosageAmount() { return dosageAmount; }
    public void setDosageAmount(String dosageAmount) { this.dosageAmount = dosageAmount; }

    public String getCustomNotes() { return customNotes; }
    public void setCustomNotes(String customNotes) { this.customNotes = customNotes; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
