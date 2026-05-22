package com.ems.employee_management_system.service;

import com.ems.employee_management_system.dto.EmployeeRequestDTO;
import com.ems.employee_management_system.dto.EmployeeResponseDTO;
import java.util.List;

/**
 * Service Interface — defines WHAT operations are available.
 *
 * This is a contract. Any class that "implements" this interface
 * MUST provide all these methods.
 *
 * Why use an interface?
 * → Separation of concerns: the controller only needs to know WHAT
 * methods exist, not HOW they work.
 * → Makes it easy to write unit tests (you can mock this interface).
 * → Follows the Dependency Inversion Principle (a SOLID principle).
 */
public interface EmployeeService {

    // Creates a new employee and returns the created employee's data
    EmployeeResponseDTO createEmployee(EmployeeRequestDTO requestDTO);

    // Returns a list of all employees
    List<EmployeeResponseDTO> getAllEmployees();

    // Returns one employee by their ID — throws exception if not found
    EmployeeResponseDTO getEmployeeById(Long id);

    // Updates an existing employee's data — throws exception if not found
    EmployeeResponseDTO updateEmployee(Long id, EmployeeRequestDTO requestDTO);

    // Deletes an employee by ID — throws exception if not found
    void deleteEmployee(Long id);
}
