package com.ems.employee_management_system.controller;

import com.ems.employee_management_system.dto.EmployeeReportDTO;
import com.ems.employee_management_system.response.ApiResponse;
import com.ems.employee_management_system.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Report generation.
 *
 * Endpoints:
 *   GET /api/reports/employee/{employeeId}  → Report for ONE employee
 *   GET /api/reports/employees              → Report for ALL employees
 *
 * Notice: these are all GET requests — we never write data in reports.
 */
@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    /**
     * GET /api/reports/employee/{employeeId}
     *
     * Returns a full work summary for one specific employee.
     * Example: GET /api/reports/employee/2
     */
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<ApiResponse<EmployeeReportDTO>> getEmployeeReport(
            @PathVariable Long employeeId) {

        EmployeeReportDTO report = reportService.getEmployeeReport(employeeId);
        ApiResponse<EmployeeReportDTO> response = ApiResponse.success(
                "Employee report generated successfully", report);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/reports/employees
     *
     * Returns a work summary for ALL employees in the system.
     * Useful for a manager-level overview.
     */
    @GetMapping("/employees")
    public ResponseEntity<ApiResponse<List<EmployeeReportDTO>>> getAllEmployeesReport() {

        List<EmployeeReportDTO> reports = reportService.getAllEmployeesReport();
        ApiResponse<List<EmployeeReportDTO>> response = ApiResponse.success(
                "All employees report generated successfully", reports);
        return ResponseEntity.ok(response);
    }
}
