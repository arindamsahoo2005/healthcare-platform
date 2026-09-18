package com.healthcare.platform.repository.mongo;

import com.healthcare.platform.model.mongo.LabTestDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LabTestMongoRepository extends MongoRepository<LabTestDocument, String> {
    List<LabTestDocument> findByCategoryIgnoreCase(String category);
    List<LabTestDocument> findByTestNameContainingIgnoreCase(String keyword);
}
