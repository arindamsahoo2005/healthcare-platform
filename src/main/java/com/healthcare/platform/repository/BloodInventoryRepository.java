package com.healthcare.platform.repository;

import com.healthcare.platform.model.BloodInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BloodInventoryRepository extends JpaRepository<BloodInventory, Long> {
    List<BloodInventory> findByBloodGroupIgnoreCase(String bloodGroup);
    List<BloodInventory> findByCityIgnoreCase(String city);
}
