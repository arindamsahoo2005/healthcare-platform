package com.healthcare.platform.repository;

import com.healthcare.platform.model.AmbulanceRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AmbulanceRequestRepository extends JpaRepository<AmbulanceRequest, Long> {
    List<AmbulanceRequest> findAllByOrderByRequestedAtDesc();
    List<AmbulanceRequest> findByStatusNot(String status);
}
