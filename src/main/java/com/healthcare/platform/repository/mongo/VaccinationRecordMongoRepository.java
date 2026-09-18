package com.healthcare.platform.repository.mongo;

import com.healthcare.platform.model.mongo.VaccinationRecordDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VaccinationRecordMongoRepository extends MongoRepository<VaccinationRecordDocument, String> {
    List<VaccinationRecordDocument> findByPatientNameIgnoreCase(String patientName);
    List<VaccinationRecordDocument> findByCategoryIgnoreCase(String category);
}
