package com.ems.employee_management_system.service;

/**
 * Defines export operations.
 *
 * Returns a String — the full CSV content as text.
 * The Controller then wraps it in an HTTP response with the right headers
 * so the browser/Postman treats it as a downloadable file.
 */
public interface ExportService {

    // Export ALL work logs as CSV content
    String exportAllWorkLogsToCsv();

    // Export work logs for ONE employee as CSV content
    String exportEmployeeWorkLogsToCsv(Long employeeId);
}
