package com.healthcare.platform.repository;

import com.healthcare.platform.model.HealthExpense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HealthExpenseRepository extends JpaRepository<HealthExpense, Long> {
    List<HealthExpense> findAllByOrderByExpenseDateDesc();
    List<HealthExpense> findByCategory(String category);
}
