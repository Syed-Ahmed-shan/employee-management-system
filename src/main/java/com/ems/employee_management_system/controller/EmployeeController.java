package com.ems.employee_management_system.controller;

import com.ems.employee_management_system.dto.EmployeeRequestDTO;
import com.ems.employee_management_system.dto.EmployeeResponseDTO;
import com.ems.employee_management_system.response.ApiResponse;
import com.ems.employee_management_system.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Employee operations.
 *
 * @RestController → combines @Controller + @ResponseBody.
 *                 Means: every method returns JSON, not a web page.
 *
 * @RequestMapping → all endpoints in this class start with "/api/employees"
 *
 *                 Endpoint summary:
 *                 POST /api/employees → Create a new employee
 *                 GET /api/employees → Get all employees
 *                 GET /api/employees/{id} → Get one employee by ID
 *                 PUT /api/employees/{id} → Update an employee by ID
 *                 DELETE /api/employees/{id} → Delete an employee by ID
 */
@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    /**
     * We inject the SERVICE (not the implementation directly).
     * The controller only knows about the EmployeeService interface.
     * Spring automatically injects EmployeeServiceImpl here.
     * This is called "programming to an interface" — a best practice.
     */
    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    // =========================================================
    // POST /api/employees
    // Create a new employee
    // =========================================================
    /**
     * @PostMapping → handles HTTP POST requests
     * @RequestBody → reads the JSON from the request body into EmployeeRequestDTO
     * @Valid → triggers the validation annotations on EmployeeRequestDTO
     *        (e.g., @NotBlank, @Email). If validation fails, Spring
     *        automatically sends a 400 error via our GlobalExceptionHandler.
     *
     *        ResponseEntity lets us control the HTTP status code we return.
     *        201 CREATED is the correct code for a successful creation.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<EmployeeResponseDTO>> createEmployee(
            @Valid @RequestBody EmployeeRequestDTO requestDTO) {

        EmployeeResponseDTO createdEmployee = employeeService.createEmployee(requestDTO);
        ApiResponse<EmployeeResponseDTO> response = ApiResponse.success(
                "Employee created successfully", createdEmployee);
        return new ResponseEntity<>(response, HttpStatus.CREATED); // HTTP 201
    }

    // =========================================================
    // GET /api/employees
    // Get all employees
    // =========================================================
    /**
     * @GetMapping → handles HTTP GET requests
     *             HTTP 200 OK is the default for successful GET requests.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<EmployeeResponseDTO>>> getAllEmployees() {
        List<EmployeeResponseDTO> employees = employeeService.getAllEmployees();
        ApiResponse<List<EmployeeResponseDTO>> response = ApiResponse.success(
                "Employees fetched successfully", employees);
        return ResponseEntity.ok(response); // HTTP 200
    }

    // =========================================================
    // GET /api/employees/{id}
    // Get one employee by ID
    // =========================================================
    /**
     * @PathVariable → extracts the {id} value from the URL.
     *               Example: GET /api/employees/5 → id = 5
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EmployeeResponseDTO>> getEmployeeById(@PathVariable Long id) {
        EmployeeResponseDTO employee = employeeService.getEmployeeById(id);
        ApiResponse<EmployeeResponseDTO> response = ApiResponse.success(
                "Employee fetched successfully", employee);
        return ResponseEntity.ok(response); // HTTP 200
    }

    // =========================================================
    // PUT /api/employees/{id}
    // Update an employee
    // =========================================================
    /**
     * @PutMapping → handles HTTP PUT requests (full update — replace all fields)
     *             We combine @PathVariable (to find who to update) and
     * @RequestBody (the new data to update with).
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EmployeeResponseDTO>> updateEmployee(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeRequestDTO requestDTO) {

        EmployeeResponseDTO updatedEmployee = employeeService.updateEmployee(id, requestDTO);
        ApiResponse<EmployeeResponseDTO> response = ApiResponse.success(
                "Employee updated successfully", updatedEmployee);
        return ResponseEntity.ok(response); // HTTP 200
    }

    // =========================================================
    // DELETE /api/employees/{id}
    // Delete an employee
    // =========================================================
    /**
     * @DeleteMapping → handles HTTP DELETE requests
     *                We return HTTP 200 OK with a success message (no data in the
     *                body).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        ApiResponse<Object> response = ApiResponse.success("Employee deleted successfully");
        return ResponseEntity.ok(response); // HTTP 200
    }
}
