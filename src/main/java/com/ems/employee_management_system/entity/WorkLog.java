package com.ems.employee_management_system.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Represents a single work log entry — one task done by one employee.
 *
 * Relationship:
 *   One Employee → Many WorkLogs  (One employee can have many work log entries)
 *   Many WorkLogs → One Employee  (Each work log belongs to exactly one employee)
 *
 * This is a @ManyToOne relationship from WorkLog's perspective.
 * In the database, a column "employee_id" is created in the "work_logs" table
 * that holds the ID of the employee who did this work.
 */
@Entity
@Table(name = "work_logs")
public class WorkLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * @ManyToOne → Many WorkLogs can belong to One Employee
     *
     * @JoinColumn → specifies the foreign key column name in THIS table.
     *   name = "employee_id" → column in work_logs table
     *   nullable = false     → every work log MUST belong to an employee
     *
     * FetchType.LAZY → Don't load the full Employee object unless we actually
     * need it. This is better for performance.
     * (EAGER would load the employee every time you load a work log — wasteful)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "task_name", nullable = false, length = 200)
    private String taskName;

    // Description can be longer text — no length limit needed (TEXT in DB)
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * Hours worked — must be positive (e.g., 2.5 hours)
     * Validated in the DTO, stored as Double here
     */
    @Column(name = "hours_worked", nullable = false)
    private Double hoursWorked;

    @Column(name = "work_date", nullable = false)
    private LocalDate workDate;

    @Column(name = "assigned_by_id")
    private Long assignedById;

    @Column(name = "assigned_by_name")
    private String assignedByName;

    @Column(name = "assigned_by_role")
    private String assignedByRole;

    @Column(name = "priority")
    private String priority = "MEDIUM";

    @Column(name = "deadline")
    private LocalDate deadline;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private WorkLogStatus status = WorkLogStatus.PENDING; // default is PENDING

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Auto-set timestamps just like Employee entity
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // =============================================
    // Constructors
    // =============================================

    public WorkLog() {}

    // =============================================
    // Getters and Setters
    // =============================================

    public Long getId() { return id; }

    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }

    public String getTaskName() { return taskName; }
    public void setTaskName(String taskName) { this.taskName = taskName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getHoursWorked() { return hoursWorked; }
    public void setHoursWorked(Double hoursWorked) { this.hoursWorked = hoursWorked; }

    public LocalDate getWorkDate() { return workDate; }
    public void setWorkDate(LocalDate workDate) { this.workDate = workDate; }

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

    public WorkLogStatus getStatus() { return status; }
    public void setStatus(WorkLogStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
