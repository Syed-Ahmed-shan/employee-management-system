package com.ems.employee_management_system.dto;

import com.ems.employee_management_system.entity.Employee;
import com.ems.employee_management_system.entity.EmployeeStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * This is what WE send BACK to the user after an operation.
 *
 * It contains all the fields the user should see — including
 * the id, status, createdAt, and updatedAt that the system manages.
 *
 * Why have a separate response DTO?
 * → Keeps control over what data we expose
 * → If the entity changes internally, the API response stays stable
 * → Clean separation of concerns
 *
 * The static fromEntity() method is a "factory method" — it converts
 * an Employee entity into this DTO in a clean, reusable way.
 */
public class EmployeeResponseDTO {

    private Long id;
    private String empCode;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String department;
    private String jobTitle;
    private Double salary;
    private LocalDate joiningDate;
    private EmployeeStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // =============================================
    // Factory Method: converts an Employee entity → DTO
    // Usage: EmployeeResponseDTO dto = EmployeeResponseDTO.fromEntity(employee);
    // =============================================

    public static EmployeeResponseDTO fromEntity(Employee employee) {
        EmployeeResponseDTO dto = new EmployeeResponseDTO();
        dto.id = employee.getId();
        dto.empCode = employee.getEmpCode();
        dto.firstName = employee.getFirstName();
        dto.lastName = employee.getLastName();
        dto.email = employee.getEmail();
        dto.phone = employee.getPhone();
        dto.department = employee.getDepartment();
        dto.jobTitle = employee.getJobTitle();
        dto.salary = employee.getSalary();
        dto.joiningDate = employee.getJoiningDate();
        dto.status = employee.getStatus();
        dto.createdAt = employee.getCreatedAt();
        dto.updatedAt = employee.getUpdatedAt();
        return dto;
    }

    // =============================================
    // Getters (no setters needed — this is read-only output)
    // Getters only — once created, DTOs shouldn't be modified
    public Long getId() { return id; }
    public String getEmpCode() { return empCode; }
    public String getFirstName() { return firstName; }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getDepartment() {
        return department;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public Double getSalary() {
        return salary;
    }

    public LocalDate getJoiningDate() {
        return joiningDate;
    }

    public EmployeeStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
