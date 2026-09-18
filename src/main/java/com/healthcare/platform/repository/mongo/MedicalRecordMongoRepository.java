package com.healthcare.platform.repository.mongo;

import com.healthcare.platform.model.mongo.MedicalRecordDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicalRecordMongoRepository extends MongoRepository<MedicalRecordDocument, String> {

    List<MedicalRecordDocument> findAllByOrderByRecordDateDesc();

    List<MedicalRecordDocument> findByPatientUidOrderByRecordDateDesc(String patientUid);

    List<MedicalRecordDocument> findByTitleContainingIgnoreCaseOrSummaryTextContainingIgnoreCase(String title, String summaryText);

    List<MedicalRecordDocument> findByRecordTypeIgnoreCase(String recordType);
}
