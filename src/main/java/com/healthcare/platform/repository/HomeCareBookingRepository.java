package com.healthcare.platform.repository;

import com.healthcare.platform.model.HomeCareBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HomeCareBookingRepository extends JpaRepository<HomeCareBooking, Long> {
    List<HomeCareBooking> findAllByOrderByBookedAtDesc();
    List<HomeCareBooking> findByCityIgnoreCase(String city);
}
