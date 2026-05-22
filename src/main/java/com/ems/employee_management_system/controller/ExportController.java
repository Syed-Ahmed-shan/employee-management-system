package com.ems.employee_management_system.controller;

import com.ems.employee_management_system.service.ExportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for CSV export downloads.
 *
 * This controller is different from others — instead of returning JSON,
 * it returns a FILE (CSV) as a download.
 *
 * How it works:
 * 1. We set Content-Type: text/csv  → browser knows it's a CSV
 * 2. We set Content-Disposition: attachment; filename="worklogs.csv"
 *    → "attachment" tells the browser to DOWNLOAD it, not display it
 *    → "filename" sets the default save name
 * 3. The body is the raw CSV text (not JSON)
 *
 * Endpoints:
 *   GET /api/export/worklogs                       → Download all work logs
 *   GET /api/export/worklogs/employee/{employeeId} → Download one employee's logs
 */
@RestController
@RequestMapping("/api/export")
public class ExportController {

    private final ExportService exportService;

    public ExportController(ExportService exportService) {
        this.exportService = exportService;
    }

    /**
     * GET /api/export/worklogs
     * Downloads all work logs as a CSV file.
     */
    @GetMapping("/worklogs")
    public ResponseEntity<byte[]> exportAllWorkLogs() {

        String csvContent = exportService.exportAllWorkLogsToCsv();

        // Build HTTP headers to trigger a file download
        HttpHeaders headers = new HttpHeaders();

        // Content-Type: tells the client what kind of file this is
        headers.setContentType(MediaType.parseMediaType("text/csv"));

        // Content-Disposition: "attachment" = download, not display
        // filename = the default name shown in Save dialog
        headers.set(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"all-worklogs.csv\"");

        // Return the CSV as bytes with 200 OK
        return ResponseEntity.ok()
                .headers(headers)
                .body(csvContent.getBytes());
    }

    /**
     * GET /api/export/worklogs/employee/{employeeId}
     * Downloads work logs for ONE specific employee as a CSV file.
     *
     * Example: GET /api/export/worklogs/employee/2
     * → Downloads "employee-2-worklogs.csv"
     */
    @GetMapping("/worklogs/employee/{employeeId}")
    public ResponseEntity<byte[]> exportEmployeeWorkLogs(
            @PathVariable Long employeeId) {

        String csvContent = exportService.exportEmployeeWorkLogsToCsv(employeeId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.set(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"employee-" + employeeId + "-worklogs.csv\"");

        return ResponseEntity.ok()
                .headers(headers)
                .body(csvContent.getBytes());
    }
}
