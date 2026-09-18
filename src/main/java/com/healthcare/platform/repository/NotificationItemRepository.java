package com.healthcare.platform.repository;

import com.healthcare.platform.model.NotificationItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificationItemRepository extends JpaRepository<NotificationItem, Long> {
    List<NotificationItem> findAllByOrderByCreatedAtDesc();
    List<NotificationItem> findByCategory(String category);
    long countByIsReadFalse();
}
