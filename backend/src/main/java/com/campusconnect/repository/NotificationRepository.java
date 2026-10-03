package com.campusconnect.repository;

import com.campusconnect.entity.Notification;
import com.campusconnect.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification,Long>{
    List<Notification> findByUserOrderByCreatedAtDesc(User user);
    long countByUserAndReadAtIsNull(User user);
    boolean existsByDedupeKey(String dedupeKey);
}
