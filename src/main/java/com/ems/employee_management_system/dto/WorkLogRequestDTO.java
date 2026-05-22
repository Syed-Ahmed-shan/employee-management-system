package com.ems.employee_management_system.dto;

import com.ems.employee_management_system.entity.WorkLogStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * What the user sends us when creating or updating a work log.
 *
 * Notice: the user sends "employeeId" (just a number) — NOT the full
 * Employee object. We look up the Employee in the service layer.
 *
 * This is the clean way — you never expose entity relationships directly.
 */
public class WorkLogRequestDTO {

    // Which employee is this work log for?
    @NotNull(message = "Employee ID is required")
    private Long employeeId;

    @NotBlank(message = "Task name is required")
    private String taskName;

    private String description; // optional

    // Removed @NotNull and @Positive from hoursWorked to allow 0 hours when a manager assigns a task
    private Double hoursWorked;

    @NotNull(message = "Work date is required")
    private LocalDate workDate;

    // Status is optional in request — defaults to PENDING if not provided
    private WorkLogStatus status;

    private Long assignedById;
    private String assignedByName;
    private String assignedByRole;

    private String priority;
    private LocalDate deadline;

    // =============================================
    // Getters and Setters
    // =============================================

    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }

    public String getTaskName() { return taskName; }
    public void setTaskName(String taskName) { this.taskName = taskName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getHoursWorked() { return hoursWorked; }
    public void setHoursWorked(Double hoursWorked) { this.hoursWorked = hoursWorked; }

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
}
