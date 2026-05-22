package com.ems.employee_management_system.entity;

/**
 * Represents the current state of a work log entry (task).
 *
 * PENDING     → Task is assigned but not yet started
 * IN_PROGRESS → Employee is currently working on it
 * COMPLETED   → Task is finished
 *
 * Stored as a String in the DB (e.g. "COMPLETED") because of
 * @Enumerated(EnumType.STRING) on the WorkLog entity.
 */
public enum WorkLogStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED
}
