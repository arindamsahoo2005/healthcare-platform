package com.healthcare.platform.repository;

import com.healthcare.platform.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    List<Doctor> findBySpecialtyIgnoreCase(String specialty);
    List<Doctor> findByCityIgnoreCase(String city);
    List<Doctor> findByStateIgnoreCase(String state);
    List<Doctor> findByCityIgnoreCaseAndSpecialtyIgnoreCase(String city, String specialty);
    List<Doctor> findByStateIgnoreCaseAndCityIgnoreCase(String state, String city);
    List<Doctor> findByStateIgnoreCaseAndSpecialtyIgnoreCase(String state, String specialty);
    List<Doctor> findByNameContainingIgnoreCaseOrSpecialtyContainingIgnoreCase(String name, String specialty);
    List<Doctor> findByHospitalAffiliationContainingIgnoreCase(String hospitalAffiliation);
}
