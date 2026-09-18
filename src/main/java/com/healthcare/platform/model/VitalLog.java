package com.healthcare.platform.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "vital_logs")
public class VitalLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String patientName;
    private LocalDateTime timestamp = LocalDateTime.now();

    private Integer heartRate; // bpm
    private Integer systolicBp; // mmHg
    private Integer diastolicBp; // mmHg
    private Integer spo2Percent; // %
    private Double temperatureF; // °F
    private Double bloodGlucoseMgDl; // mg/dL
    private Double weightKg;

    // Wearable sync data
    private Integer stepsToday = 7450;
    private Double sleepHours = 7.5;
    private Integer activeMinutes = 45;
    private String dataSource = "GOOGLE_HEALTH_CONNECT"; // MANUAL, GOOGLE_HEALTH_CONNECT, SAMSUNG_HEALTH, SMARTWATCH

    private String note;

    public VitalLog() {}

    public VitalLog(String patientName, Integer heartRate, Integer systolicBp, Integer diastolicBp, Integer spo2Percent, Double bloodGlucoseMgDl, Double weightKg, String dataSource) {
        this.patientName = patientName;
        this.heartRate = heartRate;
        this.systolicBp = systolicBp;
        this.diastolicBp = diastolicBp;
        this.spo2Percent = spo2Percent;
        this.bloodGlucoseMgDl = bloodGlucoseMgDl;
        this.weightKg = weightKg;
        this.dataSource = dataSource;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public Integer getHeartRate() { return heartRate; }
    public void setHeartRate(Integer heartRate) { this.heartRate = heartRate; }

    public Integer getSystolicBp() { return systolicBp; }
    public void setSystolicBp(Integer systolicBp) { this.systolicBp = systolicBp; }

    public Integer getDiastolicBp() { return diastolicBp; }
    public void setDiastolicBp(Integer diastolicBp) { this.diastolicBp = diastolicBp; }

    public Integer getSpo2Percent() { return spo2Percent; }
    public void setSpo2Percent(Integer spo2Percent) { this.spo2Percent = spo2Percent; }

    public Double getTemperatureF() { return temperatureF; }
    public void setTemperatureF(Double temperatureF) { this.temperatureF = temperatureF; }

    public Double getBloodGlucoseMgDl() { return bloodGlucoseMgDl; }
    public void setBloodGlucoseMgDl(Double bloodGlucoseMgDl) { this.bloodGlucoseMgDl = bloodGlucoseMgDl; }

    public Double getWeightKg() { return weightKg; }
    public void setWeightKg(Double weightKg) { this.weightKg = weightKg; }

    public Integer getStepsToday() { return stepsToday; }
    public void setStepsToday(Integer stepsToday) { this.stepsToday = stepsToday; }

    public Double getSleepHours() { return sleepHours; }
    public void setSleepHours(Double sleepHours) { this.sleepHours = sleepHours; }

    public Integer getActiveMinutes() { return activeMinutes; }
    public void setActiveMinutes(Integer activeMinutes) { this.activeMinutes = activeMinutes; }

    public String getDataSource() { return dataSource; }
    public void setDataSource(String dataSource) { this.dataSource = dataSource; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
