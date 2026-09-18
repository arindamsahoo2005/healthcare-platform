package com.healthcare.platform.repository;

import com.healthcare.platform.model.VitalLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface VitalLogRepository extends JpaRepository<VitalLog, Long> {
    List<VitalLog> findAllByOrderByTimestampDesc();
}
