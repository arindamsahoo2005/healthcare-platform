package com.healthcare.platform.repository;

import com.healthcare.platform.model.AdmissionRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AdmissionRequestRepository extends JpaRepository<AdmissionRequest, Long> {
    List<AdmissionRequest> findAllByOrderByRequestedAtDesc();
    List<AdmissionRequest> findByHospitalId(Long hospitalId);
}
