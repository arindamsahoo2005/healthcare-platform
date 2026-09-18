package com.healthcare.platform.service;

import com.healthcare.platform.repository.MedicalRecordRepository;
import com.healthcare.platform.repository.MedicineRepository;
import com.healthcare.platform.repository.mongo.MedicalRecordMongoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AiHealthServiceTest {

    @Mock
    private MedicalRecordRepository recordRepository;

    @Mock
    private MedicalRecordMongoRepository mongoRecordRepository;

    @Mock
    private MedicineRepository medicineRepository;

    @InjectMocks
    private AiHealthService aiHealthService;

    @Test
    void flagsEmergencyQueriesAndUsesRequestedLanguage() {
        AiHealthService.AiResponse response = aiHealthService.processHealthQuery("I have severe chest pain", "en");

        assertTrue(response.isEmergencyRedFlag());
        assertEquals("Emergency Medicine / Cardiology", response.getSuggestedSpecialty());
        assertTrue(response.getRecommendedAction().contains("108"));
        assertTrue(response.getAnswer().contains("CRITICAL RED-FLAG ALERT"));
    }

    @Test
    void mapsCommonSymptomsToSpecialtiesAndQuestions() {
        AiHealthService.AiResponse fever = aiHealthService.processHealthQuery("I have a persistent cough", "en");
        AiHealthService.AiResponse headache = aiHealthService.processHealthQuery("This migraine causes dizziness", "en");
        AiHealthService.AiResponse jointPain = aiHealthService.processHealthQuery("My knee and joint hurt", "en");

        assertEquals("General Physician / Internal Medicine", fever.getSuggestedSpecialty());
        assertEquals(2, fever.getQuestionsForDoctor().size());
        assertEquals("Neurologist", headache.getSuggestedSpecialty());
        assertEquals("Orthopedic Specialist / Physiotherapist", jointPain.getSuggestedSpecialty());
    }

    @Test
    void usesDefaultGuidanceForUnrecognizedQueries() {
        AiHealthService.AiResponse response = aiHealthService.processHealthQuery("How can I improve my routine?", "en");

        assertFalse(response.isEmergencyRedFlag());
        assertEquals("General Physician", response.getSuggestedSpecialty());
        assertFalse(response.getQuestionsForDoctor().isEmpty());
    }

    @Test
    void returnsEmptyRecordGuidanceWhenNoRecordsExist() {
        when(mongoRecordRepository.findAllByOrderByRecordDateDesc()).thenReturn(Collections.emptyList());
        when(recordRepository.findAllByOrderByRecordDateDesc()).thenReturn(Collections.emptyList());

        AiHealthService.AiResponse response = aiHealthService.answerFromMedicalRecords("latest blood test", "en");

        assertTrue(response.getAnswer().contains("do not have any uploaded medical records"));
        assertTrue(response.getSourceDocuments().isEmpty());
        verify(recordRepository).findAllByOrderByRecordDateDesc();
    }
}
