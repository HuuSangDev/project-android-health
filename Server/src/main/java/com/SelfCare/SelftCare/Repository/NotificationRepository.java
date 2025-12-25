package com.SelfCare.SelftCare.Repository;

import com.SelfCare.SelftCare.Entity.Notification;
import com.SelfCare.SelftCare.Enum.Goal;
import com.SelfCare.SelftCare.Enum.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    List<Notification> findByIsReadFalseOrderByCreatedAtDesc();
    
    List<Notification> findByTypeOrderByCreatedAtDesc(NotificationType type);
    
    List<Notification> findAllByOrderByCreatedAtDesc();

    /**
     * Lấy notifications theo goal hoặc broadcast (goal = null)
     */
    List<Notification> findByGoalOrGoalIsNullOrderByCreatedAtDesc(Goal goal);

    /**
     * Lấy notifications theo goal cụ thể
     */
    List<Notification> findByGoalOrderByCreatedAtDesc(Goal goal);
}
