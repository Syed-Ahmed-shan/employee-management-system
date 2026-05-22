package com.ems.employee_management_system.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * This class represents the Employee table in our MySQL database.
 *
 * When you start the app, Spring/Hibernate will automatically create
 * a table called "employees" with all the columns defined here.
 *
 * @Entity → marks this as a JPA entity (a DB table)
 * @Table → specifies the actual table name in the database
 */
@Entity
@Table(name = "employees")
public class Employee {

    /**
     * @Id → this field is the Primary Key
     * @GeneratedValue → the database auto-increments this (1, 2, 3, ...)
     *                 You never set the ID yourself — the DB does it.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "emp_code", unique = true, nullable = false, updatable = false)
    private String empCode;

    /**
     * @Column → maps to a column in the DB.
     *         nullable = false → this field is REQUIRED (cannot be NULL in DB)
     *         length = 100 → max 100 characters
     */
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    /**
     * unique = true → no two employees can have the same email.
     * The database enforces this constraint automatically.
     */
    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "department", length = 100)
    private String department;

    @Column(name = "job_title", length = 100)
    private String jobTitle;

    @Column(name = "salary")
    private Double salary;

    @Column(name = "joining_date")
    private LocalDate joiningDate;

    /**
     * @Enumerated(EnumType.STRING) → store the enum value as a String in the DB.
     * So the DB stores "ACTIVE" or "INACTIVE" — not 0 or 1.
     * STRING is safer because if you reorder enum values, the DB data stays
     * correct.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private EmployeeStatus status = EmployeeStatus.ACTIVE; // default is ACTIVE

    /**
     * @Column(updatable = false) → once created, this column is NEVER updated.
     *                   This is set automatically before the record is first saved.
     */
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * @PrePersist → this method runs AUTOMATICALLY right before
     *             the entity is saved to the database for the FIRST time.
     *             We use it to set both timestamps.
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * @PreUpdate → this method runs AUTOMATICALLY right before
     *            the entity is updated (saved again after changes).
     *            We use it to refresh the updatedAt timestamp.
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // =============================================
    // Constructors
    // =============================================

    // No-args constructor is REQUIRED by JPA — don't remove it!
    public Employee() {
    }

    public Employee(String empCode, String firstName, String lastName, String email, String phone,
            String department, String jobTitle, Double salary, LocalDate joiningDate) {
        this.empCode = empCode;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.department = department;
        this.jobTitle = jobTitle;
        this.salary = salary;
        this.joiningDate = joiningDate;
    }

    // =============================================
    // Getters and Setters
    // Spring/Jackson need these to read and write the fields
    // =============================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmpCode() {
        return empCode;
    }

    public void setEmpCode(String empCode) {
        this.empCode = empCode;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public Double getSalary() {
        return salary;
    }

    public void setSalary(Double salary) {
        this.salary = salary;
    }

    public LocalDate getJoiningDate() {
        return joiningDate;
    }

    public void setJoiningDate(LocalDate joiningDate) {
        this.joiningDate = joiningDate;
    }

    public EmployeeStatus getStatus() {
        return status;
    }

    public void setStatus(EmployeeStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
