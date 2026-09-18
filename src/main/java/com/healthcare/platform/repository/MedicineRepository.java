package com.healthcare.platform.repository;

import com.healthcare.platform.model.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MedicineRepository extends JpaRepository<Medicine, Long> {
    List<Medicine> findByActiveTrue();
    List<Medicine> findByRemainingPillsLessThanEqual(Integer threshold);
}
