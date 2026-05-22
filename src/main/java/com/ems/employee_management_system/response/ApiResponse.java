package com.ems.employee_management_system.response;

/**
 * A generic wrapper for ALL API responses in this system.
 *
 * Instead of returning raw data directly, every API endpoint
 * will return this wrapper so the client always sees the same format:
 *
 * {
 *   "success": true,
 *   "message": "Employee created successfully",
 *   "data": { ... the actual result ... }
 * }
 *
 * The <T> means this class can hold ANY type of data (Employee, List, etc.)
 * This is called a Generic class.
 */
public class ApiResponse<T> {

    private boolean success;   // true = everything went fine, false = something went wrong
    private String message;    // a human-readable message
    private T data;            // the actual data we are returning (can be any type)

    // Constructor for successful responses with data
    public ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    // Constructor for responses without data (like a delete response)
    public ApiResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
        this.data = null;
    }

    // --- Static helper methods so we don't repeat ourselves ---

    // Call this when everything is OK and you have data to return
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    // Call this when everything is OK but no data to return (e.g., delete)
    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(true, message);
    }

    // Call this when something goes wrong
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message);
    }

    // --- Getters (Spring needs these to convert to JSON) ---

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }
}
