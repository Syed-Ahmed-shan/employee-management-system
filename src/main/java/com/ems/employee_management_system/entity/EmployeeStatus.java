package com.ems.employee_management_system.entity;

/**
 * An enum to represent the current state of an employee.
 *
 * Why use an Enum instead of a plain String?
 * → With a String, someone could accidentally set status = "actve" (typo).
 * → With an Enum, only ACTIVE or INACTIVE are valid — Java enforces it.
 *
 * This gets stored in the DB as the String "ACTIVE" or "INACTIVE"
 * because of @Enumerated(EnumType.STRING) on the Employee entity.
 */
public enum EmployeeStatus {
    ACTIVE, // The employee is currently working
    INACTIVE // The employee has left or is on leave
}
