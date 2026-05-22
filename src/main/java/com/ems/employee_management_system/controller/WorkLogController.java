package com.ems.employee_management_system.controller;

import com.ems.employee_management_system.dto.WorkLogFilterDTO;
import com.ems.employee_management_system.dto.WorkLogRequestDTO;
import com.ems.employee_management_system.dto.WorkLogResponseDTO;
import com.ems.employee_management_system.entity.WorkLogStatus;
import com.ems.employee_management_system.response.ApiResponse;
import com.ems.employee_management_system.service.WorkLogService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
import java.util.List;

/**
 * REST Controller for Work Log operations.
 *
 * Endpoints:
 *   POST   /api/worklogs                          → Create a work log
 *   GET    /api/worklogs                          → Get all work logs
 *   GET    /api/worklogs/{id}                     → Get one work log by ID
 *   GET    /api/worklogs/employee/{employeeId}    → Get all logs for an employee
 *   PUT    /api/worklogs/{id}                     → Update a work log
 *   DELETE /api/worklogs/{id}                     → Delete a work log
 */
@RestController
@RequestMapping("/api/worklogs")
public class WorkLogController {

    private final WorkLogService workLogService;

    public WorkLogController(WorkLogService workLogService) {
        this.workLogService = workLogService;
    }

    // POST /api/worklogs
    @PostMapping
    public ResponseEntity<ApiResponse<WorkLogResponseDTO>> createWorkLog(
            @Valid @RequestBody WorkLogRequestDTO requestDTO) {

        WorkLogResponseDTO created = workLogService.createWorkLog(requestDTO);
        ApiResponse<WorkLogResponseDTO> response = ApiResponse.success(
                "Work log created successfully", created);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // GET /api/worklogs
    @GetMapping
    public ResponseEntity<ApiResponse<List<WorkLogResponseDTO>>> getAllWorkLogs() {
        List<WorkLogResponseDTO> workLogs = workLogService.getAllWorkLogs();
        ApiResponse<List<WorkLogResponseDTO>> response = ApiResponse.success(
                "Work logs fetched successfully", workLogs);
        return ResponseEntity.ok(response);
    }

    // GET /api/worklogs/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<WorkLogResponseDTO>> getWorkLogById(
            @PathVariable Long id) {

        WorkLogResponseDTO workLog = workLogService.getWorkLogById(id);
        ApiResponse<WorkLogResponseDTO> response = ApiResponse.success(
                "Work log fetched successfully", workLog);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/worklogs/employee/{employeeId}
     * Returns ALL work logs for a specific employee.
     *
     * Example: GET /api/worklogs/employee/1
     * → returns all tasks logged by employee with ID 1
     */
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<ApiResponse<List<WorkLogResponseDTO>>> getWorkLogsByEmployee(
            @PathVariable Long employeeId) {

        List<WorkLogResponseDTO> workLogs = workLogService.getWorkLogsByEmployee(employeeId);
        ApiResponse<List<WorkLogResponseDTO>> response = ApiResponse.success(
                "Work logs for employee fetched successfully", workLogs);
        return ResponseEntity.ok(response);
    }

    // PUT /api/worklogs/{id}
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<WorkLogResponseDTO>> updateWorkLog(
            @PathVariable Long id,
            @Valid @RequestBody WorkLogRequestDTO requestDTO) {

        WorkLogResponseDTO updated = workLogService.updateWorkLog(id, requestDTO);
        ApiResponse<WorkLogResponseDTO> response = ApiResponse.success(
                "Work log updated successfully", updated);
        return ResponseEntity.ok(response);
    }

    // DELETE /api/worklogs/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteWorkLog(@PathVariable Long id) {
        workLogService.deleteWorkLog(id);
        ApiResponse<Object> response = ApiResponse.success("Work log deleted successfully");
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/worklogs/filter
     *
     * Filter work logs using optional query parameters.
     * All parameters are optional — use any combination:
     *
     * Examples:
     *   /api/worklogs/filter?employeeId=2
     *   /api/worklogs/filter?status=COMPLETED
     *   /api/worklogs/filter?startDate=2024-01-01&endDate=2024-01-31
     *   /api/worklogs/filter?employeeId=2&status=IN_PROGRESS
     *   /api/worklogs/filter?employeeId=2&startDate=2024-01-01&endDate=2024-01-31&status=COMPLETED
     *
     * @RequestParam(required = false) means the parameter is optional.
     * If not provided, it will be null — and our query ignores null values.
     *
     * @DateTimeFormat tells Spring how to parse the date string from the URL
     * into a LocalDate Java object.
     */
    @GetMapping("/filter")
    public ResponseEntity<ApiResponse<List<WorkLogResponseDTO>>> filterWorkLogs(
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) WorkLogStatus status,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        // Build the filter DTO from the query params
        WorkLogFilterDTO filterDTO = new WorkLogFilterDTO();
        filterDTO.setEmployeeId(employeeId);
        filterDTO.setStatus(status);
        filterDTO.setStartDate(startDate);
        filterDTO.setEndDate(endDate);

        List<WorkLogResponseDTO> results = workLogService.filterWorkLogs(filterDTO);
        ApiResponse<List<WorkLogResponseDTO>> response = ApiResponse.success(
                "Filtered work logs fetched successfully", results);
        return ResponseEntity.ok(response);
    }
}
