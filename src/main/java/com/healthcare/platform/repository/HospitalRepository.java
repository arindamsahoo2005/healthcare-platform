package com.healthcare.platform.repository;

import com.healthcare.platform.model.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HospitalRepository extends JpaRepository<Hospital, Long> {
    List<Hospital> findByCityIgnoreCase(String city);
    List<Hospital> findByStateIgnoreCase(String state);
    List<Hospital> findByStateIgnoreCaseAndCityIgnoreCase(String state, String city);
    List<Hospital> findByEmergencyDeptTrue();
    List<Hospital> findByNameContainingIgnoreCaseOrDepartmentsContainingIgnoreCase(String name, String department);
}
