package com.ems.employee_management_system.dto;

import com.ems.employee_management_system.entity.WorkLogStatus;
import java.time.LocalDate;

/**
 * Holds the filter criteria sent by the user as query parameters.
 *
 * All fields are OPTIONAL — the user can use any combination:
 *   ?employeeId=2
 *   ?status=COMPLETED
 *   ?startDate=2024-01-01&endDate=2024-01-31
 *   ?employeeId=2&status=COMPLETED&startDate=2024-01-01&endDate=2024-01-31
 *
 * If a field is null, we ignore it in the query (no filter on that field).
 */
public class WorkLogFilterDTO {

    private Long employeeId;       // filter by employee (optional)
    private WorkLogStatus status;  // filter by status: PENDING, IN_PROGRESS, COMPLETED (optional)
    private LocalDate startDate;   // filter from this date (optional)
    private LocalDate endDate;     // filter up to this date (optional)

    // Getters and Setters
    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }

    public WorkLogStatus getStatus() { return status; }
    public void setStatus(WorkLogStatus status) { this.status = status; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
}
