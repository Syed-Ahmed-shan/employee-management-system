package com.ems.employee_management_system.service;

import com.ems.employee_management_system.dto.EmployeeReportDTO;
import java.util.List;

/**
 * Contract for report generation operations.
 */
public interface ReportService {

    // Full detailed report for ONE employee
    EmployeeReportDTO getEmployeeReport(Long employeeId);

    // Summary report for ALL employees
    List<EmployeeReportDTO> getAllEmployeesReport();
}
