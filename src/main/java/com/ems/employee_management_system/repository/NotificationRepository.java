package com.ems.employee_management_system.repository;

import com.ems.employee_management_system.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    // Find unread notifications for a specific user
    List<Notification> findByRecipientUserIdAndIsReadFalseOrderByCreatedAtDesc(Long recipientUserId);
    
    // Find unread global notifications (where recipient is null, for admins)
    List<Notification> findByRecipientUserIdIsNullAndIsReadFalseOrderByCreatedAtDesc();
}
