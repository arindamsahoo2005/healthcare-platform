package com.healthcare.platform.repository.mongo;

import com.healthcare.platform.model.mongo.UserDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserMongoRepository extends MongoRepository<UserDocument, String> {
    Optional<UserDocument> findFirstByFirebaseUid(String firebaseUid);
    Optional<UserDocument> findFirstByEmail(String email);
    Optional<UserDocument> findFirstByUsername(String username);
    List<UserDocument> findAllByEmail(String email);
    List<UserDocument> findAllByUsername(String username);
    List<UserDocument> findAllByFirebaseUid(String firebaseUid);

    default Optional<UserDocument> findByFirebaseUid(String firebaseUid) {
        return findFirstByFirebaseUid(firebaseUid);
    }
    default Optional<UserDocument> findByEmail(String email) {
        return findFirstByEmail(email);
    }
    default Optional<UserDocument> findByUsername(String username) {
        return findFirstByUsername(username);
    }
}
