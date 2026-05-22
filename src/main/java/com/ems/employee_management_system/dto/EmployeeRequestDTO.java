package com.ems.employee_management_system.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import com.ems.employee_management_system.entity.Role;

/**
 * DTO = Data Transfer Object.
 *
 * This is what the USER sends us in the request body (JSON).
 * It only contains the fields the user is ALLOWED to set.
 *
 * Notice: no "id", no "createdAt", no "updatedAt" — the user
 * cannot set those. The system sets them automatically.
 *
 * Validation annotations:
 * 
 * @NotBlank → field must not be null, empty, or just spaces
 * @NotNull → field must not be null
 * @Email → must be a valid email format
 * @Positive → number must be greater than 0
 */
public class EmployeeRequestDTO {

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String email;

    private String phone; // optional — no validation needed

    private String department; // optional

    private String jobTitle; // optional

    @Positive(message = "Salary must be a positive number")
    private Double salary;

    @NotNull(message = "Joining date is required")
    private LocalDate joiningDate;

    // Optional: Defaults to ROLE_EMPLOYEE in service if not provided
    private Role role;

    // =============================================
    // Constructors
    // =============================================

    public EmployeeRequestDTO() {
    }

    // =============================================
    // Getters and Setters
    // =============================================

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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
