package com.ems.employee_management_system.dto;

import com.ems.employee_management_system.entity.WorkLog;
import com.ems.employee_management_system.entity.WorkLogStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * What we send back to the user after a work log operation.
 *
 * Notice: instead of returning the full Employee object, we return
 * just the essential employee info (employeeId, employeeName).
 * This keeps the response clean and avoids circular references.
 */
public class WorkLogResponseDTO {

    private Long id;

    // We return the employee's ID and full name — not the whole Employee object
    private Long employeeId;
    private String employeeName; // e.g. "Rahul Sharma"

    private String taskName;
    private String description;
    private Double hoursWorked;
    private LocalDate workDate;
    private WorkLogStatus status;
    private Long assignedById;
    private String assignedByName;
    private String assignedByRole;
    private String priority;
    private LocalDate deadline;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // =============================================
    // Factory Method: WorkLog entity → Response DTO
    // =============================================

    public static WorkLogResponseDTO fromEntity(WorkLog workLog) {
        WorkLogResponseDTO dto = new WorkLogResponseDTO();
        dto.id = workLog.getId();

        // Pull just what we need from the Employee — not the whole object
        dto.employeeId = workLog.getEmployee().getId();
        dto.employeeName = workLog.getEmployee().getFirstName()
                           + " " + workLog.getEmployee().getLastName();

        dto.taskName = workLog.getTaskName();
        dto.description = workLog.getDescription();
        dto.hoursWorked = workLog.getHoursWorked();
        dto.setWorkDate(workLog.getWorkDate());
        dto.setStatus(workLog.getStatus());
        dto.setAssignedById(workLog.getAssignedById());
        dto.setAssignedByName(workLog.getAssignedByName());
        dto.setAssignedByRole(workLog.getAssignedByRole());
        dto.setPriority(workLog.getPriority());
        dto.setDeadline(workLog.getDeadline());
        dto.setCreatedAt(workLog.getCreatedAt());
        dto.setUpdatedAt(workLog.getUpdatedAt());
        return dto;
    }

    // =============================================
    // Getters
    // =============================================

    public Long getId() { return id; }
    public Long getEmployeeId() { return employeeId; }
    public String getEmployeeName() { return employeeName; }
    public String getTaskName() { return taskName; }
    public String getDescription() { return description; }
    public Double getHoursWorked() { return hoursWorked; }
    public LocalDate getWorkDate() { return workDate; }
    public void setWorkDate(LocalDate workDate) { this.workDate = workDate; }
    public WorkLogStatus getStatus() { return status; }
    public void setStatus(WorkLogStatus status) { this.status = status; }

    public Long getAssignedById() { return assignedById; }
    public void setAssignedById(Long assignedById) { this.assignedById = assignedById; }

    public String getAssignedByName() { return assignedByName; }
    public void setAssignedByName(String assignedByName) { this.assignedByName = assignedByName; }

    public String getAssignedByRole() { return assignedByRole; }
    public void setAssignedByRole(String assignedByRole) { this.assignedByRole = assignedByRole; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public LocalDate getDeadline() { return deadline; }
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
