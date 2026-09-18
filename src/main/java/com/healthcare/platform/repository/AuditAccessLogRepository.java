package com.healthcare.platform.repository;

import com.healthcare.platform.model.AuditAccessLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AuditAccessLogRepository extends JpaRepository<AuditAccessLog, Long> {
    List<AuditAccessLog> findAllByOrderByTimestampDesc();
}
