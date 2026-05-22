package com.ems.employee_management_system.entity;

/**
 * The 3 roles in this system:
 *
 * ADMIN    → Full access to everything
 * MANAGER  → Can read employees, manage work logs, view reports & exports
 * EMPLOYEE → Read-only access to work logs and reports
 *
 * Stored as "ROLE_ADMIN", "ROLE_MANAGER", "ROLE_EMPLOYEE" in the DB
 * because Spring Security expects roles to have the "ROLE_" prefix.
 */
public enum Role {
    ROLE_ADMIN,
    ROLE_MANAGER,
    ROLE_EMPLOYEE
}
