package com.ems.employee_management_system.service.impl;

import com.ems.employee_management_system.entity.Notification;
import com.ems.employee_management_system.repository.NotificationRepository;
import com.ems.employee_management_system.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceImpl implements NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);

    private final NotificationRepository notificationRepository;
    private final JavaMailSender mailSender;

    public NotificationServiceImpl(NotificationRepository notificationRepository, JavaMailSender mailSender) {
        this.notificationRepository = notificationRepository;
        this.mailSender = mailSender;
    }

    @Override
    public void notifyEmployeeCreated(String employeeName, String department, String email, String empCode) {
        // Save to DB (Global notification for all admins)
        notificationRepository.save(new Notification(null, 
                "New Employee Onboarded", 
                "Employee " + employeeName + " joined " + department));
                
        // Send real email with credentials
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            // In a real app you might use a generic "from" address
            message.setFrom("ems-portal@company.com");
            message.setTo(email);
            message.setSubject("Welcome to EMS! Your Account Credentials");
            message.setText("Hello " + employeeName + ",\n\n" +
                    "Your EMS Portal account has been created successfully!\n\n" +
                    "Login Employee ID: " + empCode + "\n" +
                    "Password: Welcome@123\n\n" +
                    "Please log in and change your password.\n\n" +
                    "Best regards,\nHR Team");
            
            mailSender.send(message);
            log.info("📧 REAL EMAIL SENT TO: {}", email);
        } catch (Exception e) {
            log.error("Failed to send email to {}", email, e);
        }
    }

    @Override
    public void notifyWorkLogCreated(String employeeName, String taskName, Double hoursWorked, Long assignedById) {
        // Save to DB (assignedById receives it, or if null, global)
        notificationRepository.save(new Notification(assignedById, 
                "New Task Assigned/Created", 
                employeeName + " has a new task: " + taskName));
    }

    @Override
    public void notifyTaskCompleted(String employeeName, String taskName, Double hoursWorked, Long assignedById) {
        // Notify the manager who assigned it
        notificationRepository.save(new Notification(assignedById, 
                "Task Completed", 
                employeeName + " completed task: " + taskName + " (" + hoursWorked + " hours)"));
    }
}
