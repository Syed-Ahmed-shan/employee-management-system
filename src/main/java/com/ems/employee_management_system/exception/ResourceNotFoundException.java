package com.ems.employee_management_system.exception;

/**
 * This is a custom exception we throw whenever someone tries
 * to find an employee (or any resource) that doesn't exist.
 *
 * Example: GET /api/employees/999 → employee 999 doesn't exist
 * → we throw this exception → Spring returns a proper 404 error
 *
 * RuntimeException means Java does NOT force you to handle it
 * with try-catch every time — it "bubbles up" automatically.
 */
public class ResourceNotFoundException extends RuntimeException {

    // The name of the resource (e.g., "Employee")
    private String resourceName;

    // The field we searched by (e.g., "id")
    private String fieldName;

    // The value we searched for (e.g., 99)
    private Object fieldValue;

    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        // This message goes to the RuntimeException parent class
        super(String.format("%s not found with %s: %s", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    public String getResourceName() {
        return resourceName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Object getFieldValue() {
        return fieldValue;
    }
}
