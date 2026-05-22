package com.ems.employee_management_system.controller;

import com.ems.employee_management_system.entity.Notification;
import com.ems.employee_management_system.entity.User;
import com.ems.employee_management_system.repository.NotificationRepository;
import com.ems.employee_management_system.repository.UserRepository;
import com.ems.employee_management_system.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationController(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Notification>>> getUnreadNotifications() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            return ResponseEntity.ok(ApiResponse.error("User not found"));
        }

        List<Notification> notifications;
        if (user.getRole().name().equals("ROLE_ADMIN")) {
            // Admins see global unread notifications and their own
            notifications = notificationRepository.findByRecipientUserIdIsNullAndIsReadFalseOrderByCreatedAtDesc();
            notifications.addAll(notificationRepository.findByRecipientUserIdAndIsReadFalseOrderByCreatedAtDesc(user.getId()));
        } else {
            // Managers and Employees see their specific notifications
            notifications = notificationRepository.findByRecipientUserIdAndIsReadFalseOrderByCreatedAtDesc(user.getId());
        }

        return ResponseEntity.ok(ApiResponse.success("Notifications fetched", notifications));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<ApiResponse<String>> markAsRead(@PathVariable Long id) {
        Notification notification = notificationRepository.findById(id).orElse(null);
        if (notification != null) {
            notification.setRead(true);
            notificationRepository.save(notification);
        }
        return ResponseEntity.ok(ApiResponse.success("Marked as read"));
    }
}
