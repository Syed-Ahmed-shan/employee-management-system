package com.ems.employee_management_system.service.impl;

import com.ems.employee_management_system.dto.EmployeeReportDTO;
import com.ems.employee_management_system.entity.Employee;
import com.ems.employee_management_system.entity.WorkLogStatus;
import com.ems.employee_management_system.exception.ResourceNotFoundException;
import com.ems.employee_management_system.repository.EmployeeRepository;
import com.ems.employee_management_system.repository.WorkLogRepository;
import com.ems.employee_management_system.service.ReportService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of report generation logic.
 *
 * How it works:
 * 1. Fetch the employee(s) from the DB
 * 2. Run aggregate queries (COUNT, SUM) on work_logs for that employee
 * 3. Build an EmployeeReportDTO with all the calculated values
 * 4. Return it — no saving, just reading and calculating
 *
 * All methods are readOnly = true because we're only reading data,
 * never writing. This is a performance optimization hint for the DB.
 */
@Service
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService {

    private final EmployeeRepository employeeRepository;
    private final WorkLogRepository workLogRepository;

    public ReportServiceImpl(EmployeeRepository employeeRepository,
                              WorkLogRepository workLogRepository) {
        this.employeeRepository = employeeRepository;
        this.workLogRepository = workLogRepository;
    }

    // =========================================================
    // Report for ONE Employee
    // =========================================================
    @Override
    public EmployeeReportDTO getEmployeeReport(Long employeeId) {

        // Step 1: Make sure employee exists
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", employeeId));

        // Step 2: Run all aggregate queries against the work_logs table
        return buildReport(employee);
    }

    // =========================================================
    // Report for ALL Employees
    // =========================================================
    @Override
    public List<EmployeeReportDTO> getAllEmployeesReport() {

        // Fetch all employees, then build a report for each one
        return employeeRepository.findAll()
                .stream()
                .map(this::buildReport)  // same as: employee -> buildReport(employee)
                .collect(Collectors.toList());
    }

    // =========================================================
    // Private Helper — builds one report for one employee
    // Reused by both methods above to avoid code duplication
    // =========================================================
    private EmployeeReportDTO buildReport(Employee employee) {

        Long employeeId = employee.getId();

        // Run each aggregate query — Spring Data JPA executes the @Query methods
        long totalTasks          = workLogRepository.countByEmployeeId(employeeId);
        double totalHours        = workLogRepository.sumHoursByEmployeeId(employeeId);
        long completedTasks      = workLogRepository.countByEmployeeIdAndStatus(
                                        employeeId, WorkLogStatus.COMPLETED);
        long inProgressTasks     = workLogRepository.countByEmployeeIdAndStatus(
                                        employeeId, WorkLogStatus.IN_PROGRESS);
        long pendingTasks        = workLogRepository.countByEmployeeIdAndStatus(
                                        employeeId, WorkLogStatus.PENDING);

        // Build and return the report DTO with all calculated values
        return new EmployeeReportDTO(
                employeeId,
                employee.getFirstName() + " " + employee.getLastName(),
                employee.getDepartment(),
                employee.getJobTitle(),
                totalTasks,
                totalHours,
                completedTasks,
                inProgressTasks,
                pendingTasks
        );
    }
}
