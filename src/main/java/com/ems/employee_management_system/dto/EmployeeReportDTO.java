package com.ems.employee_management_system.dto;

/**
 * Holds the summary report data for one employee.
 *
 * This is NOT stored in the database — it is calculated on-the-fly
 * by querying and aggregating the work_logs table.
 *
 * Think of this as a "read-only calculated view" of an employee's work.
 */
public class EmployeeReportDTO {

    // Employee identity info
    private Long employeeId;
    private String employeeName;
    private String department;
    private String jobTitle;

    // Calculated work statistics
    private long totalTasks;         // total number of work log entries
    private double totalHoursWorked; // SUM of all hoursWorked
    private double averageHoursPerTask; // totalHours / totalTasks

    // Breakdown by status
    private long completedTasks;
    private long inProgressTasks;
    private long pendingTasks;

    // =============================================
    // Constructor — we build this manually in the service
    // =============================================

    public EmployeeReportDTO(Long employeeId, String employeeName, String department,
                              String jobTitle, long totalTasks, double totalHoursWorked,
                              long completedTasks, long inProgressTasks, long pendingTasks) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.department = department;
        this.jobTitle = jobTitle;
        this.totalTasks = totalTasks;
        this.totalHoursWorked = totalHoursWorked;
        // Avoid division by zero — if no tasks, average is 0
        this.averageHoursPerTask = totalTasks > 0
                ? Math.round((totalHoursWorked / totalTasks) * 100.0) / 100.0
                : 0.0;
        this.completedTasks = completedTasks;
        this.inProgressTasks = inProgressTasks;
        this.pendingTasks = pendingTasks;
    }

    // =============================================
    // Getters
    // =============================================

    public Long getEmployeeId() { return employeeId; }
    public String getEmployeeName() { return employeeName; }
    public String getDepartment() { return department; }
    public String getJobTitle() { return jobTitle; }
    public long getTotalTasks() { return totalTasks; }
    public double getTotalHoursWorked() { return totalHoursWorked; }
    public double getAverageHoursPerTask() { return averageHoursPerTask; }
    public long getCompletedTasks() { return completedTasks; }
    public long getInProgressTasks() { return inProgressTasks; }
    public long getPendingTasks() { return pendingTasks; }
}
