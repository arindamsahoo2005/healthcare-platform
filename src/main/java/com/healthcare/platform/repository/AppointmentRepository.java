package com.healthcare.platform.repository;

import com.healthcare.platform.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findAllByOrderByAppointmentDateTimeAsc();
    List<Appointment> findByDoctorId(Long doctorId);
    List<Appointment> findByStatus(String status);
}
