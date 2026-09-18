package com.healthcare.platform.controller;

import com.healthcare.platform.model.Medicine;
import com.healthcare.platform.repository.MedicineRepository;
import com.healthcare.platform.service.AiHealthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/ai")
public class AiApiController {

    @Autowired
    private AiHealthService aiHealthService;

    @Autowired
    private MedicineRepository medicineRepository;

    @PostMapping("/chat")
    public ResponseEntity<AiHealthService.AiResponse> chat(
            @RequestParam String message,
            @RequestParam(defaultValue = "en") String language) {
        AiHealthService.AiResponse response = aiHealthService.processHealthQuery(message, language);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/ask-records")
    public ResponseEntity<AiHealthService.AiResponse> askRecords(
            @RequestParam String query,
            @RequestParam(defaultValue = "en") String language) {
        AiHealthService.AiResponse response = aiHealthService.answerFromMedicalRecords(query, language);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/explain-medicine/{id}")
    public ResponseEntity<?> explainMedicine(@PathVariable Long id) {
        Optional<Medicine> medOpt = medicineRepository.findById(id);
        if (medOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Medicine not found"));
        }
        Medicine med = medOpt.get();
        Map<String, Object> resp = new HashMap<>();
        resp.put("name", med.getName());
        resp.put("genericName", med.getGenericName());
        resp.put("dosage", med.getDosage());
        resp.put("foodRelation", med.getFoodRelation());
        resp.put("explanation", "This medication is prescribed as part of your therapy. " +
                "Food timing directive: " + med.getFoodRelation() + ". " +
                "General safety guideline: Always swallow with water. Never take an extra dose if you missed a previous dose; wait until the next scheduled time.");
        return ResponseEntity.ok(resp);
    }
}
