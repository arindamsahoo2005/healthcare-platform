package com.healthcare.platform.repository;

import com.healthcare.platform.model.PharmacyOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PharmacyOrderRepository extends JpaRepository<PharmacyOrder, Long> {
    List<PharmacyOrder> findAllByOrderByOrderedAtDesc();
}
