package com.healthcare.platform.repository;

import com.healthcare.platform.model.VaccinationRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface VaccinationRecordRepository extends JpaRepository<VaccinationRecord, Long> {
    List<VaccinationRecord> findAllByOrderByAdministeredDateDesc();
    List<VaccinationRecord> findByTargetGroupIgnoreCase(String targetGroup);
}
