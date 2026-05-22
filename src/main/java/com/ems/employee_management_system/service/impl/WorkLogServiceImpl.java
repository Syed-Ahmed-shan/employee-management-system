package com.ems.employee_management_system.service.impl;

import com.ems.employee_management_system.dto.WorkLogFilterDTO;
import com.ems.employee_management_system.dto.WorkLogRequestDTO;
import com.ems.employee_management_system.dto.WorkLogResponseDTO;
import com.ems.employee_management_system.entity.Employee;
import com.ems.employee_management_system.entity.WorkLog;
import com.ems.employee_management_system.entity.WorkLogStatus;
import com.ems.employee_management_system.exception.ResourceNotFoundException;
import com.ems.employee_management_system.repository.EmployeeRepository;
import com.ems.employee_management_system.repository.WorkLogRepository;
import com.ems.employee_management_system.service.NotificationService;
import com.ems.employee_management_system.service.WorkLogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * All business logic for Work Log operations.
 *
 * Key business rules:
 *  1. You cannot create a work log for an employee that doesn't exist
 *  2. Hours worked must be positive (validated in DTO)
 *  3. When no status is provided, default to PENDING
 */
@Service
@Transactional
public class WorkLogServiceImpl implements WorkLogService {

    private final WorkLogRepository workLogRepository;
    private final EmployeeRepository employeeRepository;
    private final NotificationService notificationService;

    public WorkLogServiceImpl(WorkLogRepository workLogRepository,
                               EmployeeRepository employeeRepository,
                               NotificationService notificationService) {
        this.workLogRepository = workLogRepository;
        this.employeeRepository = employeeRepository;
        this.notificationService = notificationService;
    }

    // =========================================================
    // CREATE — Log a new work entry for an employee
    // =========================================================
    @Override
    public WorkLogResponseDTO createWorkLog(WorkLogRequestDTO requestDTO) {

        // Step 1: Validate that the employee exists
        // If not found, throws ResourceNotFoundException → handled by GlobalExceptionHandler
        Employee employee = employeeRepository.findById(requestDTO.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employee", "id", requestDTO.getEmployeeId()));

        // Step 2: Build the WorkLog entity
        WorkLog workLog = new WorkLog();
        workLog.setEmployee(employee);         // set the relationship
        workLog.setTaskName(requestDTO.getTaskName());
        workLog.setDescription(requestDTO.getDescription());
        workLog.setHoursWorked(requestDTO.getHoursWorked());
        workLog.setWorkDate(requestDTO.getWorkDate());
        workLog.setAssignedById(requestDTO.getAssignedById());
        workLog.setAssignedByName(requestDTO.getAssignedByName());
        workLog.setAssignedByRole(requestDTO.getAssignedByRole());
        workLog.setPriority(requestDTO.getPriority() != null ? requestDTO.getPriority() : "MEDIUM");
        workLog.setDeadline(requestDTO.getDeadline());

        // Step 3: Set status — use provided value, or default to PENDING
        if (requestDTO.getStatus() != null) {
            workLog.setStatus(requestDTO.getStatus());
        } else {
            workLog.setStatus(WorkLogStatus.PENDING);
        }

        // Step 4: Save and return
        WorkLog savedWorkLog = workLogRepository.save(workLog);

        // Step 5: Send notification for new work log
        String employeeName = employee.getFirstName() + " " + employee.getLastName();
        notificationService.notifyWorkLogCreated(
                employeeName,
                savedWorkLog.getTaskName(),
                savedWorkLog.getHoursWorked(),
                savedWorkLog.getAssignedById());

        // Step 6: If status is COMPLETED, also send a completion notification
        if (savedWorkLog.getStatus() == WorkLogStatus.COMPLETED) {
            notificationService.notifyTaskCompleted(
                    employeeName,
                    savedWorkLog.getTaskName(),
                    savedWorkLog.getHoursWorked(),
                    savedWorkLog.getAssignedById());
        }

        return WorkLogResponseDTO.fromEntity(savedWorkLog);
    }

    // =========================================================
    // READ ALL — Get every work log
    // =========================================================
    @Override
    @Transactional(readOnly = true)
    public List<WorkLogResponseDTO> getAllWorkLogs() {
        return workLogRepository.findAll()
                .stream()
                .map(WorkLogResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // =========================================================
    // READ ONE — Get one work log by ID
    // =========================================================
    @Override
    @Transactional(readOnly = true)
    public WorkLogResponseDTO getWorkLogById(Long id) {
        WorkLog workLog = workLogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("WorkLog", "id", id));
        return WorkLogResponseDTO.fromEntity(workLog);
    }

    // =========================================================
    // READ BY EMPLOYEE — Get all work logs for one employee
    // =========================================================
    @Override
    @Transactional(readOnly = true)
    public List<WorkLogResponseDTO> getWorkLogsByEmployee(Long employeeId) {

        // First check the employee actually exists
        if (!employeeRepository.existsById(employeeId)) {
            throw new ResourceNotFoundException("Employee", "id", employeeId);
        }

        // Returns sorted by workDate descending (newest first)
        return workLogRepository.findByEmployeeIdOrderByWorkDateDesc(employeeId)
                .stream()
                .map(WorkLogResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // =========================================================
    // UPDATE — Modify an existing work log
    // =========================================================
    @Override
    public WorkLogResponseDTO updateWorkLog(Long id, WorkLogRequestDTO requestDTO) {

        // Step 1: Find the existing work log
        WorkLog existingWorkLog = workLogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("WorkLog", "id", id));

        // Step 2: Validate the employee (in case they changed the employeeId)
        Employee employee = employeeRepository.findById(requestDTO.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employee", "id", requestDTO.getEmployeeId()));

        // Step 3: Update all fields
        existingWorkLog.setEmployee(employee);
        existingWorkLog.setTaskName(requestDTO.getTaskName());
        existingWorkLog.setDescription(requestDTO.getDescription());
        existingWorkLog.setHoursWorked(requestDTO.getHoursWorked());
        existingWorkLog.setWorkDate(requestDTO.getWorkDate());
        
        // Update priority and deadline if provided
        if (requestDTO.getPriority() != null) {
            existingWorkLog.setPriority(requestDTO.getPriority());
        }
        if (requestDTO.getDeadline() != null) {
            existingWorkLog.setDeadline(requestDTO.getDeadline());
        }
        
        // If updating status to COMPLETED, send notification
        boolean justCompleted = (requestDTO.getStatus() == WorkLogStatus.COMPLETED && existingWorkLog.getStatus() != WorkLogStatus.COMPLETED);

        if (requestDTO.getStatus() != null) {
            existingWorkLog.setStatus(requestDTO.getStatus());
        }

        // Step 4: saveAndFlush so @PreUpdate fires and updatedAt is refreshed
        WorkLog updatedWorkLog = workLogRepository.saveAndFlush(existingWorkLog);
        
        if (justCompleted) {
            String employeeName = employee.getFirstName() + " " + employee.getLastName();
            notificationService.notifyTaskCompleted(
                    employeeName,
                    updatedWorkLog.getTaskName(),
                    updatedWorkLog.getHoursWorked(),
                    updatedWorkLog.getAssignedById());
        }

        return WorkLogResponseDTO.fromEntity(updatedWorkLog);
    }

    // =========================================================
    // DELETE — Remove a work log
    // =========================================================
    @Override
    public void deleteWorkLog(Long id) {
        WorkLog workLog = workLogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("WorkLog", "id", id));
        workLogRepository.delete(workLog);
    }

    // =========================================================
    // FILTER — Search work logs with optional criteria (Feature 4)
    // =========================================================
    @Override
    @Transactional(readOnly = true)
    public List<WorkLogResponseDTO> filterWorkLogs(WorkLogFilterDTO filterDTO) {

        // Pass all filter values to the repository query.
        // If any value is null, that filter is ignored automatically
        // (handled by the "IS NULL OR" logic in the JPQL query).
        return workLogRepository.filterWorkLogs(
                        filterDTO.getEmployeeId(),
                        filterDTO.getStatus(),
                        filterDTO.getStartDate(),
                        filterDTO.getEndDate())
                .stream()
                .map(WorkLogResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
}
