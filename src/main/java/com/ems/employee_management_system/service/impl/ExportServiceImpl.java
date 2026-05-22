package com.ems.employee_management_system.service.impl;

import com.ems.employee_management_system.entity.WorkLog;
import com.ems.employee_management_system.exception.ResourceNotFoundException;
import com.ems.employee_management_system.repository.EmployeeRepository;
import com.ems.employee_management_system.repository.WorkLogRepository;
import com.ems.employee_management_system.service.ExportService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Builds CSV content from work log data.
 *
 * What is CSV?
 * CSV = Comma-Separated Values. It's a simple text format:
 *
 *   ID,Employee Name,Task Name,Hours,Date,Status
 *   1,Rahul Sharma,Fix login bug,3.5,2024-01-15,COMPLETED
 *   2,Rahul Sharma,Dashboard UI,6.0,2024-01-16,IN_PROGRESS
 *
 * Excel and Google Sheets can open CSV files directly.
 * We build this using StringBuilder — no library needed.
 *
 * Special handling:
 * → If a field contains a comma (e.g. description), we wrap it in quotes: "text, with comma"
 *   This prevents CSV from treating the comma as a column separator.
 */
@Service
@Transactional(readOnly = true)
public class ExportServiceImpl implements ExportService {

    private final WorkLogRepository workLogRepository;
    private final EmployeeRepository employeeRepository;

    public ExportServiceImpl(WorkLogRepository workLogRepository,
                              EmployeeRepository employeeRepository) {
        this.workLogRepository = workLogRepository;
        this.employeeRepository = employeeRepository;
    }

    // =========================================================
    // EXPORT ALL — All work logs to CSV
    // =========================================================
    @Override
    public String exportAllWorkLogsToCsv() {
        List<WorkLog> workLogs = workLogRepository.findAll();
        return buildCsv(workLogs);
    }

    // =========================================================
    // EXPORT BY EMPLOYEE — One employee's work logs to CSV
    // =========================================================
    @Override
    public String exportEmployeeWorkLogsToCsv(Long employeeId) {

        // Validate the employee exists first
        if (!employeeRepository.existsById(employeeId)) {
            throw new ResourceNotFoundException("Employee", "id", employeeId);
        }

        List<WorkLog> workLogs = workLogRepository.findByEmployeeIdOrderByWorkDateDesc(employeeId);
        return buildCsv(workLogs);
    }

    // =========================================================
    // Private Helper — Builds the CSV string from a list of WorkLogs
    // =========================================================
    private String buildCsv(List<WorkLog> workLogs) {

        StringBuilder csv = new StringBuilder();

        // Row 1: Header row — column names
        csv.append("ID,Employee Name,Task Name,Description,Hours Worked,Work Date,Status,Created At\n");

        // One row per work log entry
        for (WorkLog w : workLogs) {

            String employeeName = w.getEmployee().getFirstName()
                                  + " " + w.getEmployee().getLastName();

            csv.append(w.getId()).append(",");
            csv.append(escapeCsv(employeeName)).append(",");
            csv.append(escapeCsv(w.getTaskName())).append(",");
            csv.append(escapeCsv(w.getDescription())).append(",");
            csv.append(w.getHoursWorked()).append(",");
            csv.append(w.getWorkDate()).append(",");
            csv.append(w.getStatus()).append(",");
            csv.append(w.getCreatedAt());
            csv.append("\n");  // newline = next row
        }

        return csv.toString();
    }

    /**
     * Escapes a CSV field value.
     *
     * Problem: if a field contains a comma (like "Fixed bug, deployed to prod"),
     * the CSV parser would treat that comma as a column separator and break the row.
     *
     * Solution: wrap the field in double-quotes → "Fixed bug, deployed to prod"
     * Now the CSV parser knows it's one field, not two columns.
     *
     * Also handles null values by replacing them with empty string.
     */
    private String escapeCsv(String value) {
        if (value == null) return "";
        // If value contains comma, newline, or double-quote → wrap in quotes
        if (value.contains(",") || value.contains("\n") || value.contains("\"")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
