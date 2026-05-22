package com.ems.employee_management_system.service;

import com.ems.employee_management_system.dto.WorkLogFilterDTO;
import com.ems.employee_management_system.dto.WorkLogRequestDTO;
import com.ems.employee_management_system.dto.WorkLogResponseDTO;
import java.util.List;

/**
 * Contract for all work log operations.
 */
public interface WorkLogService {

    WorkLogResponseDTO createWorkLog(WorkLogRequestDTO requestDTO);

    List<WorkLogResponseDTO> getAllWorkLogs();

    WorkLogResponseDTO getWorkLogById(Long id);

    List<WorkLogResponseDTO> getWorkLogsByEmployee(Long employeeId);

    WorkLogResponseDTO updateWorkLog(Long id, WorkLogRequestDTO requestDTO);

    void deleteWorkLog(Long id);

    // Feature 4: Filter with optional criteria
    List<WorkLogResponseDTO> filterWorkLogs(WorkLogFilterDTO filterDTO);
}
