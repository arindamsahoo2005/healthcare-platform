package com.healthcare.platform.repository;

import com.healthcare.platform.model.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {
    List<MedicalRecord> findAllByOrderByRecordDateDesc();
    List<MedicalRecord> findByRecordTypeIgnoreCase(String recordType);
    List<MedicalRecord> findByTitleContainingIgnoreCaseOrSummaryTextContainingIgnoreCase(String title, String summary);
}
