package com.healthcare.platform.repository.mongo;

import com.healthcare.platform.model.mongo.DoctorDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorMongoRepository extends MongoRepository<DoctorDocument, String> {
    List<DoctorDocument> findByStateIgnoreCase(String state);
    List<DoctorDocument> findByCityIgnoreCase(String city);
    List<DoctorDocument> findBySpecialtyIgnoreCase(String specialty);
    List<DoctorDocument> findByStateIgnoreCaseAndSpecialtyIgnoreCase(String state, String specialty);
}
