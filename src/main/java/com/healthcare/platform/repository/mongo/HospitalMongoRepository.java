package com.healthcare.platform.repository.mongo;

import com.healthcare.platform.model.mongo.HospitalDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HospitalMongoRepository extends MongoRepository<HospitalDocument, String> {
    List<HospitalDocument> findByStateIgnoreCase(String state);
    List<HospitalDocument> findByCityIgnoreCase(String city);
    List<HospitalDocument> findByStateIgnoreCaseAndCityIgnoreCase(String state, String city);
}
