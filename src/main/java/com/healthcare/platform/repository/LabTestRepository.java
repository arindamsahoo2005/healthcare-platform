package com.healthcare.platform.repository;

import com.healthcare.platform.model.LabTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LabTestRepository extends JpaRepository<LabTest, Long> {
    List<LabTest> findByCategoryIgnoreCase(String category);
    List<LabTest> findByTestNameContainingIgnoreCase(String testName);
    List<LabTest> findByCityIgnoreCase(String city);
}
