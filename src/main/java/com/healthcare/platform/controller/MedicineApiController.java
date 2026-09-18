package com.healthcare.platform.controller;

import com.healthcare.platform.model.Medicine;
import com.healthcare.platform.model.MedicineLog;
import com.healthcare.platform.model.MedicineSchedule;
import com.healthcare.platform.repository.MedicineRepository;
import com.healthcare.platform.repository.MedicineScheduleRepository;
import com.healthcare.platform.service.MedicineTimerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.*;

@RestController
@RequestMapping("/api/medicines")
public class MedicineApiController {

    @Autowired
    private MedicineTimerService timerService;

    @Autowired
    private MedicineRepository medicineRepository;

    @Autowired
    private MedicineScheduleRepository scheduleRepository;

    @GetMapping("/next-dose")
    public ResponseEntity<?> getNextDose() {
        MedicineTimerService.NextDoseInfo next = timerService.getNextDose();
        if (next == null) {
            Map<String, Object> fallback = new HashMap<>();
            fallback.put("message", "No active medicines scheduled.");
            return ResponseEntity.ok(fallback);
        }
        return ResponseEntity.ok(next);
    }

    @PostMapping("/take/{scheduleId}")
    public ResponseEntity<?> markTaken(@PathVariable Long scheduleId) {
        MedicineLog log = timerService.markTaken(scheduleId);
        Map<String, Object> resp = new HashMap<>();
        resp.put("status", "SUCCESS");
        resp.put("message", "Dose recorded as TAKEN at " + log.getActionDateTime().toLocalTime());
        resp.put("logId", log.getId());
        resp.put("adherencePercentage", timerService.calculateAdherencePercentage());
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/snooze/{scheduleId}")
    public ResponseEntity<?> snoozeDose(@PathVariable Long scheduleId, @RequestParam(defaultValue = "10") int minutes) {
        MedicineLog log = timerService.snooze(scheduleId, minutes);
        Map<String, Object> resp = new HashMap<>();
        resp.put("status", "SUCCESS");
        resp.put("message", "Dose snoozed for " + minutes + " minutes. Reminder will alert at " + log.getSnoozeUntil().toLocalTime());
        resp.put("snoozeUntil", log.getSnoozeUntil());
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/skip/{scheduleId}")
    public ResponseEntity<?> skipDose(@PathVariable Long scheduleId, @RequestParam(defaultValue = "Patient skipped dose") String reason) {
        MedicineLog log = timerService.skip(scheduleId, reason);
        Map<String, Object> resp = new HashMap<>();
        resp.put("status", "SUCCESS");
        resp.put("message", "Dose marked as SKIPPED. Reason recorded: " + reason);
        resp.put("safetyDirective", log.getSafetyWarningText());
        resp.put("adherencePercentage", timerService.calculateAdherencePercentage());
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/adherence")
    public ResponseEntity<?> getAdherence() {
        Map<String, Object> resp = new HashMap<>();
        resp.put("adherencePercentage", timerService.calculateAdherencePercentage());
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllMedicines() {
        return ResponseEntity.ok(medicineRepository.findByActiveTrue());
    }

    @PostMapping("/add")
    public ResponseEntity<?> addMedicine(
            @RequestParam String name,
            @RequestParam String genericName,
            @RequestParam String dosage,
            @RequestParam String form,
            @RequestParam String foodRelation,
            @RequestParam Integer remainingPills,
            @RequestParam String timeOfDay,
            @RequestParam String doseTimeStr,
            @RequestParam String instructions) {

        Medicine med = new Medicine(name, genericName, dosage, form, foodRelation, remainingPills, remainingPills, instructions);
        med = medicineRepository.save(med);

        LocalTime doseTime = LocalTime.parse(doseTimeStr);
        MedicineSchedule schedule = new MedicineSchedule(med, timeOfDay, doseTime, dosage, instructions);
        scheduleRepository.save(schedule);

        Map<String, Object> resp = new HashMap<>();
        resp.put("status", "SUCCESS");
        resp.put("message", "Medicine " + name + " and schedule saved successfully!");
        resp.put("medicineId", med.getId());
        return ResponseEntity.ok(resp);
    }
}
