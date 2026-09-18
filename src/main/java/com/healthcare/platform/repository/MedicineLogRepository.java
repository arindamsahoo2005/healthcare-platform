package com.healthcare.platform.repository;

import com.healthcare.platform.model.MedicineLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MedicineLogRepository extends JpaRepository<MedicineLog, Long> {
    List<MedicineLog> findAllByOrderByScheduledDateTimeDesc();
    List<MedicineLog> findByScheduledDateTimeBetween(LocalDateTime start, LocalDateTime end);
    long countByStatus(String status);
}
