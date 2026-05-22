package com.ems.employee_management_system.service;

public interface NotificationService {

    // We use empCode as the login username now
    void notifyEmployeeCreated(String employeeName, String department, String email, String empCode);
    
    // We update this to accept assignedById
    void notifyWorkLogCreated(String employeeName, String taskName, Double hoursWorked, Long assignedById);
    
    // We update this to accept assignedById
    void notifyTaskCompleted(String employeeName, String taskName, Double hoursWorked, Long assignedById);

}
