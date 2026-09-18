package com.healthcare.platform.service;

import com.healthcare.platform.model.MedicalRecord;
import com.healthcare.platform.repository.MedicalRecordRepository;
import com.healthcare.platform.repository.MedicineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AiHealthService {

    @Autowired
    private MedicalRecordRepository recordRepository;

    @Autowired
    private com.healthcare.platform.repository.mongo.MedicalRecordMongoRepository mongoRecordRepository;

    @Autowired
    private MedicineRepository medicineRepository;

    public static class AiResponse {
        private String answer;
        private boolean emergencyRedFlag;
        private String suggestedSpecialty;
        private String recommendedAction;
        private List<String> sourceDocuments = new ArrayList<>();
        private List<String> questionsForDoctor = new ArrayList<>();

        public String getAnswer() { return answer; }
        public void setAnswer(String answer) { this.answer = answer; }
        public boolean isEmergencyRedFlag() { return emergencyRedFlag; }
        public void setEmergencyRedFlag(boolean emergencyRedFlag) { this.emergencyRedFlag = emergencyRedFlag; }
        public String getSuggestedSpecialty() { return suggestedSpecialty; }
        public void setSuggestedSpecialty(String suggestedSpecialty) { this.suggestedSpecialty = suggestedSpecialty; }
        public String getRecommendedAction() { return recommendedAction; }
        public void setRecommendedAction(String recommendedAction) { this.recommendedAction = recommendedAction; }
        public List<String> getSourceDocuments() { return sourceDocuments; }
        public void setSourceDocuments(List<String> sourceDocuments) { this.sourceDocuments = sourceDocuments; }
        public List<String> getQuestionsForDoctor() { return questionsForDoctor; }
        public void setQuestionsForDoctor(List<String> questionsForDoctor) { this.questionsForDoctor = questionsForDoctor; }
    }

    /**
     * Intelligent health triage, symptom guidance, and emergency red-flag screening
     */
    public AiResponse processHealthQuery(String userQuery, String language) {
        AiResponse response = new AiResponse();
        String q = userQuery.toLowerCase().trim();

        // 1. Emergency Red-Flag Screening
        if (q.contains("chest pain") || q.contains("heart attack") || q.contains("cant breathe") ||
            q.contains("cannot breathe") || q.contains("shortness of breath") || q.contains("stroke") ||
            q.contains("paralysis") || q.contains("unconscious") || q.contains("severe bleeding")) {

            response.setEmergencyRedFlag(true);
            response.setSuggestedSpecialty("Emergency Medicine / Cardiology");
            response.setRecommendedAction("CALL 108 OR GO TO NEAREST EMERGENCY CENTER IMMEDIATELY");

            if ("hi".equalsIgnoreCase(language)) {
                response.setAnswer("⚠️ **आपातकालीन चेतावनी (Red Flag Alert):** आपके द्वारा बताए गए लक्षण (जैसे सीने में दर्द या सांस लेने में गंभीर कठिनाई) एक चिकित्सा आपातकाल का संकेत हो सकते हैं। कृपया तुरंत 108 पर कॉल करें या नजदीकी आपातकालीन अस्पताल जाएं।");
            } else if ("bn".equalsIgnoreCase(language)) {
                response.setAnswer("⚠️ **জরুরি সতর্কতা (Red Flag Alert):** আপনার উল্লিখিত লক্ষণগুলি জরুরি চিকিৎসার ইঙ্গিত দিতে পারে। অবিলম্বে ১০৮ নম্বরে কল করুন অথবা নিকটতম জরুরি বিভাগে যান।");
            } else if ("or".equalsIgnoreCase(language)) {
                response.setAnswer("⚠️ **ଜରୁରୀକାଳୀନ ଚେତାବନୀ:** ଆପଣଙ୍କ ଲକ୍ଷଣ ଗୁରୁତର ଜରୁରୀକାଳୀନ ଅବସ୍ଥା ଦର୍ଶାଉଛି। ତୁରନ୍ତ ୧୦୮ କୁ ଫୋନ କରନ୍ତୁ କିମ୍ବା ନିକଟସ୍ଥ ହସ୍ପିଟାଲକୁ ଯାଆନ୍ତୁ।");
            } else {
                response.setAnswer("🚨 **CRITICAL RED-FLAG ALERT:** The symptoms described (e.g., acute chest discomfort or severe breathing difficulty) require immediate in-person medical evaluation. Please tap the Emergency SOS button, call 108, or proceed to the nearest emergency department immediately.");
            }
            return response;
        }

        // 2. "Ask My Records" Query Handlers
        if (q.contains("record") || q.contains("test") || q.contains("report") || q.contains("last blood") ||
            q.contains("cholesterol") || q.contains("sugar") || q.contains("prescription") || q.contains("history")) {
            return answerFromMedicalRecords(q, language);
        }

        // 3. Symptom Guidance & Specialty Mapping
        if (q.contains("fever") || q.contains("cold") || q.contains("cough") || q.contains("flu")) {
            response.setSuggestedSpecialty("General Physician / Internal Medicine");
            response.setRecommendedAction("Consult a General Physician if fever exceeds 101°F or lasts more than 3 days.");
            response.setAnswer("For acute fever and cold symptoms: Stay well hydrated, monitor your temperature with a thermometer every 6 hours, and take adequate rest. If you develop persistent high fever, chest pain, or breathing difficulty, seek clinical consultation.");
            response.getQuestionsForDoctor().add("What is the underlying source of the fever (viral vs bacterial)?");
            response.getQuestionsForDoctor().add("Should any diagnostic blood counts (CBC / Dengue / Typhoid) be ordered?");
            return response;
        }

        if (q.contains("headache") || q.contains("migraine") || q.contains("dizziness")) {
            response.setSuggestedSpecialty("Neurologist");
            response.setRecommendedAction("Schedule a consultation if accompanied by visual changes or nausea.");
            response.setAnswer("Frequent or throbbing headaches may stem from migraine, tension, dehydration, or eye strain. Maintain a headache diary noting onset, duration, and triggers. Avoid skipping meals.");
            response.getQuestionsForDoctor().add("Are these headaches consistent with migraines or tension headaches?");
            response.getQuestionsForDoctor().add("Is brain neuro-imaging (MRI or CT) indicated?");
            return response;
        }

        if (q.contains("joint") || q.contains("knee") || q.contains("back pain") || q.contains("bone")) {
            response.setSuggestedSpecialty("Orthopedic Specialist / Physiotherapist");
            response.setRecommendedAction("Avoid heavy lifting and consult an Orthopedic doctor for an X-Ray evaluation.");
            response.setAnswer("Musculoskeletal pain in joints or the lumbar spine often responds to short-term rest, cold/warm compress, and gentle ergonomic correction. Persistent pain with swelling warrants clinical examination.");
            response.getQuestionsForDoctor().add("Is there joint cartilage degeneration or ligament strain?");
            response.getQuestionsForDoctor().add("Can targeted physiotherapy exercises prevent surgery?");
            return response;
        }

        // Default Comprehensive Health Guidance
        response.setSuggestedSpecialty("General Physician");
        response.setRecommendedAction("Review with your primary care provider for personalized clinical evaluation.");
        response.setAnswer("I have analyzed your query. To protect your safety, clinical symptoms should always be evaluated alongside physical vitals and diagnostic tests. You can book an appointment with our verified specialists or ask me questions about your uploaded health reports.");
        response.getQuestionsForDoctor().add("What lifestyle modifications are recommended for my current profile?");
        response.getQuestionsForDoctor().add("Are my current medication timings and dosages optimal?");
        return response;
    }

    /**
     * Answers queries directly from authorized patient health records
     */
    public AiResponse answerFromMedicalRecords(String query, String language) {
        AiResponse response = new AiResponse();

        // Check MongoDB records first
        List<com.healthcare.platform.model.mongo.MedicalRecordDocument> mongoDocs = mongoRecordRepository.findAllByOrderByRecordDateDesc();
        if (mongoDocs != null && !mongoDocs.isEmpty()) {
            String q = query.toLowerCase();

            // Blood test query
            if (q.contains("blood") || q.contains("cholesterol") || q.contains("sugar") || q.contains("glucose") || q.contains("cbc")) {
                for (var r : mongoDocs) {
                    if ("BLOOD_TEST".equalsIgnoreCase(r.getRecordType()) || r.getTitle().toLowerCase().contains("blood")) {
                        response.getSourceDocuments().add(r.getTitle() + " (" + r.getRecordDate() + ") - " + r.getFacilityName());
                        response.setAnswer("📊 **Based on your authorized health record:**\n\nYour most recent blood investigation is **" +
                                r.getTitle() + "** conducted on **" + r.getRecordDate() + "** at **" + r.getFacilityName() + "** under **" + r.getDoctorName() + "**.\n\n" +
                                "**Key Findings:** " + r.getKeyFindings() + "\n\n" +
                                "**Clinical Summary:** " + r.getSummaryText());
                        response.getQuestionsForDoctor().add("Are my cholesterol / glucose markers improving compared to the last checkup?");
                        return response;
                    }
                }
            }

            // Prescription query
            if (q.contains("prescription") || q.contains("medicine") || q.contains("dose") || q.contains("metformin")) {
                for (var r : mongoDocs) {
                    if ("PRESCRIPTION".equalsIgnoreCase(r.getRecordType()) || r.getTitle().toLowerCase().contains("prescription")) {
                        response.getSourceDocuments().add(r.getTitle() + " (" + r.getRecordDate() + ") - " + r.getDoctorName());
                        response.setAnswer("💊 **Based on your active prescription records:**\n\nYour latest prescription was issued on **" +
                                r.getRecordDate() + "** by **" + r.getDoctorName() + "** at **" + r.getFacilityName() + "**.\n\n" +
                                "**Recorded Regimen:** " + r.getSummaryText() + "\n\n" +
                                "**Reminder:** Always take your medicines at scheduled times. Do not skip or double doses.");
                        return response;
                    }
                }
            }

            // Fallback to top MongoDB record
            var latest = mongoDocs.get(0);
            response.getSourceDocuments().add(latest.getTitle() + " (" + latest.getRecordDate() + ")");
            response.setAnswer("📄 **Your Latest Health Record:**\n\n**" + latest.getTitle() + "** dated **" + latest.getRecordDate() +
                    "** (" + latest.getFacilityName() + ").\n\n**Summary:** " + latest.getSummaryText());
            return response;
        }

        // Check JPA records if any
        List<MedicalRecord> records = recordRepository.findAllByOrderByRecordDateDesc();
        if (records.isEmpty()) {
            response.setAnswer("You currently do not have any uploaded medical records in your digital health locker. You can upload laboratory reports, prescriptions, or imaging scans to enable instant AI Q&A.");
            return response;
        }

        String q = query.toLowerCase();

        // Blood test query
        if (q.contains("blood") || q.contains("cholesterol") || q.contains("sugar") || q.contains("glucose") || q.contains("cbc")) {
            for (MedicalRecord r : records) {
                if ("BLOOD_TEST".equalsIgnoreCase(r.getRecordType()) || r.getTitle().toLowerCase().contains("blood")) {
                    response.getSourceDocuments().add(r.getTitle() + " (" + r.getRecordDate() + ") - " + r.getFacilityName());
                    response.setAnswer("📊 **Based on your authorized health record:**\n\nYour most recent blood investigation is **" +
                            r.getTitle() + "** conducted on **" + r.getRecordDate() + "** at **" + r.getFacilityName() + "** under **" + r.getDoctorName() + "**.\n\n" +
                            "**Key Findings:** " + r.getKeyFindings() + "\n\n" +
                            "**Clinical Summary:** " + r.getSummaryText());
                    response.getQuestionsForDoctor().add("Are my cholesterol / glucose markers improving compared to the last checkup?");
                    return response;
                }
            }
        }

        // Prescription query
        if (q.contains("prescription") || q.contains("medicine") || q.contains("dose") || q.contains("metformin")) {
            for (MedicalRecord r : records) {
                if ("PRESCRIPTION".equalsIgnoreCase(r.getRecordType()) || r.getTitle().toLowerCase().contains("prescription")) {
                    response.getSourceDocuments().add(r.getTitle() + " (" + r.getRecordDate() + ") - " + r.getDoctorName());
                    response.setAnswer("💊 **Based on your active prescription records:**\n\nYour latest prescription was issued on **" +
                            r.getRecordDate() + "** by **" + r.getDoctorName() + "** at **" + r.getFacilityName() + "**.\n\n" +
                            "**Recorded Regimen:** " + r.getSummaryText() + "\n\n" +
                            "**Reminder:** Always take your medicines at scheduled times. Do not skip or double doses.");
                    return response;
                }
            }
        }

        // Generic fallback to most recent record
        MedicalRecord latest = records.get(0);
        response.getSourceDocuments().add(latest.getTitle() + " (" + latest.getRecordDate() + ")");
        response.setAnswer("📄 **Your Latest Health Record:**\n\n**" + latest.getTitle() + "** dated **" + latest.getRecordDate() +
                "** (" + latest.getFacilityName() + ").\n\n**Summary:** " + latest.getSummaryText());
        return response;
    }
}
