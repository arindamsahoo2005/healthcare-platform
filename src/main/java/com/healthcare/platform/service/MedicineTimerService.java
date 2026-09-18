package com.healthcare.platform.service;

import com.healthcare.platform.model.*;
import com.healthcare.platform.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class MedicineTimerService {

    @Autowired
    private MedicineRepository medicineRepository;

    @Autowired
    private MedicineScheduleRepository scheduleRepository;

    @Autowired
    private MedicineLogRepository logRepository;

    @Autowired
    private NotificationItemRepository notificationRepository;

    /**
     * DTO containing all computed information about the next dose
     */
    public static class NextDoseInfo {
        private Long scheduleId;
        private Long medicineId;
        private String medicineName;
        private String genericName;
        private String dosage;
        private String form;
        private String foodRelation;
        private String foodAdvice;
        private String timeOfDay;
        private LocalTime scheduledTime;
        private String formattedTime;
        private long secondsRemaining;
        private String countdownDisplay; // "00:24:18"
        private boolean isDueNow;
        private boolean isOverdue;
        private int remainingPills;
        private String customInstructions;

        public Long getScheduleId() { return scheduleId; }
        public void setScheduleId(Long scheduleId) { this.scheduleId = scheduleId; }
        public Long getMedicineId() { return medicineId; }
        public void setMedicineId(Long medicineId) { this.medicineId = medicineId; }
        public String getMedicineName() { return medicineName; }
        public void setMedicineName(String medicineName) { this.medicineName = medicineName; }
        public String getGenericName() { return genericName; }
        public void setGenericName(String genericName) { this.genericName = genericName; }
        public String getDosage() { return dosage; }
        public void setDosage(String dosage) { this.dosage = dosage; }
        public String getForm() { return form; }
        public void setForm(String form) { this.form = form; }
        public String getFoodRelation() { return foodRelation; }
        public void setFoodRelation(String foodRelation) { this.foodRelation = foodRelation; }
        public String getFoodAdvice() { return foodAdvice; }
        public void setFoodAdvice(String foodAdvice) { this.foodAdvice = foodAdvice; }
        public String getTimeOfDay() { return timeOfDay; }
        public void setTimeOfDay(String timeOfDay) { this.timeOfDay = timeOfDay; }
        public LocalTime getScheduledTime() { return scheduledTime; }
        public void setScheduledTime(LocalTime scheduledTime) { this.scheduledTime = scheduledTime; }
        public String getFormattedTime() { return formattedTime; }
        public void setFormattedTime(String formattedTime) { this.formattedTime = formattedTime; }
        public long getSecondsRemaining() { return secondsRemaining; }
        public void setSecondsRemaining(long secondsRemaining) { this.secondsRemaining = secondsRemaining; }
        public String getCountdownDisplay() { return countdownDisplay; }
        public void setCountdownDisplay(String countdownDisplay) { this.countdownDisplay = countdownDisplay; }
        public boolean isDueNow() { return isDueNow; }
        public void setDueNow(boolean dueNow) { isDueNow = dueNow; }
        public boolean isOverdue() { return isOverdue; }
        public void setOverdue(boolean overdue) { isOverdue = overdue; }
        public int getRemainingPills() { return remainingPills; }
        public void setRemainingPills(int remainingPills) { this.remainingPills = remainingPills; }
        public String getCustomInstructions() { return customInstructions; }
        public void setCustomInstructions(String customInstructions) { this.customInstructions = customInstructions; }
    }

    /**
     * Finds the next scheduled dose across all active medicines
     */
    public NextDoseInfo getNextDose() {
        List<MedicineSchedule> schedules = scheduleRepository.findByActiveTrueOrderByDoseTimeAsc();
        if (schedules.isEmpty()) {
            return null;
        }

        LocalTime now = LocalTime.now();
        MedicineSchedule nextSchedule = null;
        long minDiffSeconds = Long.MAX_VALUE;

        // Check if there is an upcoming dose today
        for (MedicineSchedule s : schedules) {
            long diff = Duration.between(now, s.getDoseTime()).getSeconds();
            if (diff >= 0 && diff < minDiffSeconds) {
                minDiffSeconds = diff;
                nextSchedule = s;
            }
        }

        // If all doses for today have passed, pick the first dose for tomorrow morning
        if (nextSchedule == null) {
            nextSchedule = schedules.get(0);
            minDiffSeconds = Duration.between(now, LocalTime.MAX).getSeconds() +
                    Duration.between(LocalTime.MIN, nextSchedule.getDoseTime()).getSeconds();
        }

        return buildDoseInfo(nextSchedule, minDiffSeconds);
    }

    private NextDoseInfo buildDoseInfo(MedicineSchedule s, long diffSeconds) {
        NextDoseInfo info = new NextDoseInfo();
        Medicine med = s.getMedicine();

        info.setScheduleId(s.getId());
        info.setMedicineId(med.getId());
        info.setMedicineName(med.getName());
        info.setGenericName(med.getGenericName());
        info.setDosage(med.getDosage() != null ? med.getDosage() : s.getDosageAmount());
        info.setForm(med.getForm());
        info.setFoodRelation(med.getFoodRelation());
        info.setTimeOfDay(s.getTimeOfDay());
        info.setScheduledTime(s.getDoseTime());
        info.setRemainingPills(med.getRemainingPills() != null ? med.getRemainingPills() : 0);
        info.setCustomInstructions(med.getInstructions());

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
        info.setFormattedTime(s.getDoseTime().format(timeFormatter));

        info.setSecondsRemaining(Math.max(0, diffSeconds));

        long hours = diffSeconds / 3600;
        long minutes = (diffSeconds % 3600) / 60;
        long seconds = diffSeconds % 60;
        info.setCountdownDisplay(String.format("%02d:%02d:%02d", hours, minutes, seconds));

        info.setDueNow(diffSeconds <= 300); // within 5 minutes
        info.setOverdue(diffSeconds < 0);

        // Friendly food guidance string
        String food = med.getFoodRelation();
        if ("BEFORE_FOOD".equalsIgnoreCase(food)) {
            info.setFoodAdvice("Take on an empty stomach (30 mins before food)");
        } else if ("AFTER_FOOD".equalsIgnoreCase(food)) {
            info.setFoodAdvice("Take 15-30 minutes after your meal");
        } else if ("WITH_FOOD".equalsIgnoreCase(food)) {
            info.setFoodAdvice("Take directly with or in between food");
        } else {
            info.setFoodAdvice("Can be taken with or without food");
        }

        return info;
    }

    /**
     * Action: Mark scheduled dose as TAKEN
     */
    public MedicineLog markTaken(Long scheduleId) {
        MedicineSchedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found: " + scheduleId));

        MedicineLog log = new MedicineLog();
        log.setSchedule(schedule);
        log.setScheduledDateTime(LocalDateTime.of(LocalDate.now(), schedule.getDoseTime()));
        log.setActionDateTime(LocalDateTime.now());
        log.setStatus("TAKEN");
        logRepository.save(log);

        // Decrement pill count
        Medicine med = schedule.getMedicine();
        if (med.getRemainingPills() != null && med.getRemainingPills() > 0) {
            med.setRemainingPills(med.getRemainingPills() - 1);
            medicineRepository.save(med);

            // Refill alert if pill stock is low
            if (med.getRemainingPills() <= med.getPillThresholdAlert()) {
                NotificationItem refillNotif = new NotificationItem(
                        "REFILL",
                        "Refill Alert: " + med.getName(),
                        "Only " + med.getRemainingPills() + " doses remaining for " + med.getName() + ". Reorder from pharmacy soon.",
                        "/pharmacy",
                        "HIGH"
                );
                notificationRepository.save(refillNotif);
            }
        }

        return log;
    }

    /**
     * Action: SNOOZE dose by X minutes
     */
    public MedicineLog snooze(Long scheduleId, int minutes) {
        MedicineSchedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found: " + scheduleId));

        MedicineLog log = new MedicineLog();
        log.setSchedule(schedule);
        log.setScheduledDateTime(LocalDateTime.of(LocalDate.now(), schedule.getDoseTime()));
        log.setActionDateTime(LocalDateTime.now());
        log.setStatus("SNOOZED");
        log.setSnoozeMinutes(minutes);
        log.setSnoozeUntil(LocalDateTime.now().plusMinutes(minutes));
        return logRepository.save(log);
    }

    /**
     * Action: SKIP dose with recorded reason
     * Enforces clinical safety: Never advise an extra dose!
     */
    public MedicineLog skip(Long scheduleId, String reason) {
        MedicineSchedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found: " + scheduleId));

        MedicineLog log = new MedicineLog();
        log.setSchedule(schedule);
        log.setScheduledDateTime(LocalDateTime.of(LocalDate.now(), schedule.getDoseTime()));
        log.setActionDateTime(LocalDateTime.now());
        log.setStatus("SKIPPED");
        log.setSkipReason(reason != null && !reason.isBlank() ? reason : "Skipped by patient");
        log.setSafetyWarningText("SAFETY DIRECTIVE: Do not double your dose at the next scheduled time. Skip this dose and resume your regular schedule. Contact your doctor or pharmacist if you experience symptoms.");

        return logRepository.save(log);
    }

    /**
     * Escalates an unacknowledged dose to caregiver
     */
    public boolean notifyCaregiver(Long scheduleId, String patientName) {
        MedicineSchedule schedule = scheduleRepository.findById(scheduleId).orElse(null);
        if (schedule == null) return false;

        NotificationItem caregiverAlert = new NotificationItem(
                "EMERGENCY",
                "Caregiver Alert: Missed Dose",
                patientName + " has not confirmed their scheduled dose of " + schedule.getMedicine().getName() + " (" + schedule.getDoseTime() + "). Please check on them.",
                "/family-care",
                "URGENT"
        );
        notificationRepository.save(caregiverAlert);
        return true;
    }

    /**
     * Calculates medication adherence score percentage
     */
    public double calculateAdherencePercentage() {
        long takenCount = logRepository.countByStatus("TAKEN");
        long skippedCount = logRepository.countByStatus("SKIPPED");
        long missedCount = logRepository.countByStatus("MISSED");
        long total = takenCount + skippedCount + missedCount;

        if (total == 0) return 96.5; // High default baseline for fresh profile
        double rate = ((double) takenCount / total) * 100.0;
        return Math.round(rate * 10.0) / 10.0;
    }
}
