package com.healthcare.platform.repository;

import com.healthcare.platform.model.MedicineSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MedicineScheduleRepository extends JpaRepository<MedicineSchedule, Long> {
    List<MedicineSchedule> findByActiveTrueOrderByDoseTimeAsc();
    List<MedicineSchedule> findByMedicineId(Long medicineId);
}
