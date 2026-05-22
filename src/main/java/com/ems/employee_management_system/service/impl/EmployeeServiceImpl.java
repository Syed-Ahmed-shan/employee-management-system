package com.ems.employee_management_system.service.impl;

import com.ems.employee_management_system.dto.EmployeeRequestDTO;
import com.ems.employee_management_system.dto.EmployeeResponseDTO;
import com.ems.employee_management_system.entity.Employee;
import com.ems.employee_management_system.exception.ResourceNotFoundException;
import com.ems.employee_management_system.repository.EmployeeRepository;
import com.ems.employee_management_system.service.EmployeeService;
import com.ems.employee_management_system.service.NotificationService;
import com.ems.employee_management_system.entity.Role;
import com.ems.employee_management_system.entity.User;
import com.ems.employee_management_system.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service Implementation — HOW each operation actually works.
 *
 * @Service → tells Spring "this is a service bean, manage it for me"
 *
 *          This class implements EmployeeService, meaning it MUST provide
 *          all the methods defined in the interface.
 *
 *          ALL business logic lives here — never in the controller or
 *          repository.
 *
 * @Transactional → Spring wraps operations in a DB transaction.
 *                If something fails halfway, the whole operation is rolled
 *                back.
 *                We put it on write operations (create, update, delete).
 */
@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    /**
     * We need the repository to talk to the database.
     *
     * Constructor Injection is the recommended way to inject dependencies.
     * It's better than @Autowired on the field because:
     * → It makes dependencies explicit and visible
     * → Easier to write unit tests
     * → Spring recommends it too
     */
    private final EmployeeRepository employeeRepository;
    private final NotificationService notificationService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository,
                                NotificationService notificationService,
                                UserRepository userRepository,
                                PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.notificationService = notificationService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /** Helper to generate EMP codes like DEV-A7K29 */
    private String generateEmpCode(String department) {
        String prefix = (department != null && department.length() >= 3) 
                        ? department.substring(0, 3).toUpperCase() 
                        : "EMP";
        String randomStr = UUID.randomUUID().toString().substring(0, 5).toUpperCase();
        return prefix + "-" + randomStr;
    }

    // =========================================================
    // CREATE — Add a new employee
    // =========================================================
    @Override
    public EmployeeResponseDTO createEmployee(EmployeeRequestDTO requestDTO) {

        // Business Rule: No two employees can have the same email
        if (employeeRepository.existsByEmail(requestDTO.getEmail())) {
            throw new IllegalArgumentException(
                    "An employee with email '" + requestDTO.getEmail() + "' already exists.");
        }

        // Step 1: Build the Employee entity from the DTO data
        Employee employee = new Employee();
        employee.setEmpCode(generateEmpCode(requestDTO.getDepartment()));
        employee.setFirstName(requestDTO.getFirstName());
        employee.setLastName(requestDTO.getLastName());
        employee.setEmail(requestDTO.getEmail());
        employee.setPhone(requestDTO.getPhone());
        employee.setDepartment(requestDTO.getDepartment());
        employee.setJobTitle(requestDTO.getJobTitle());
        employee.setSalary(requestDTO.getSalary());
        employee.setJoiningDate(requestDTO.getJoiningDate());
        // status defaults to ACTIVE (set in the entity)

        // Step 2: Save to the database
        Employee savedEmployee = employeeRepository.save(employee);

        // Step 3: Create User Account for the Employee
        User newUser = new User();
        newUser.setUsername(savedEmployee.getEmpCode()); // Login with EmpCode now!
        newUser.setPassword(passwordEncoder.encode("Welcome@123"));
        newUser.setRole(requestDTO.getRole() != null ? requestDTO.getRole() : Role.ROLE_EMPLOYEE);
        newUser.setEmployeeId(savedEmployee.getId());
        userRepository.save(newUser);

        // Step 4: Send welcome notification with EmpCode
        notificationService.notifyEmployeeCreated(
                savedEmployee.getFirstName() + " " + savedEmployee.getLastName(),
                savedEmployee.getDepartment(),
                savedEmployee.getEmail(),
                savedEmployee.getEmpCode());

        // Step 4: Convert the saved entity to a ResponseDTO and return it
        return EmployeeResponseDTO.fromEntity(savedEmployee);
    }

    // =========================================================
    // READ ALL — Get every employee
    // =========================================================
    @Override
    @Transactional(readOnly = true) // readOnly = true is a performance hint for DB
    public List<EmployeeResponseDTO> getAllEmployees() {
        // findAll() returns a List<Employee>
        // We convert each Employee → EmployeeResponseDTO using stream + map
        return employeeRepository.findAll()
                .stream()
                .map(EmployeeResponseDTO::fromEntity) // same as: employee -> EmployeeResponseDTO.fromEntity(employee)
                .collect(Collectors.toList());
    }

    // =========================================================
    // READ ONE — Get one employee by ID
    // =========================================================
    @Override
    @Transactional(readOnly = true)
    public EmployeeResponseDTO getEmployeeById(Long id) {
        // findById returns Optional<Employee>
        // .orElseThrow() → if empty, throw our custom exception with a clear message
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));

        return EmployeeResponseDTO.fromEntity(employee);
    }

    // =========================================================
    // UPDATE — Modify an existing employee
    // =========================================================
    @Override
    public EmployeeResponseDTO updateEmployee(Long id, EmployeeRequestDTO requestDTO) {

        // Step 1: Make sure the employee exists, throw exception if not
        Employee existingEmployee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));

        // Step 2: If they are changing the email, check that the new email
        // is not already used by a DIFFERENT employee
        if (!existingEmployee.getEmail().equalsIgnoreCase(requestDTO.getEmail())
                && employeeRepository.existsByEmail(requestDTO.getEmail())) {
            throw new IllegalArgumentException(
                    "Another employee with email '" + requestDTO.getEmail() + "' already exists.");
        }

        // Step 3: Update the fields
        existingEmployee.setFirstName(requestDTO.getFirstName());
        existingEmployee.setLastName(requestDTO.getLastName());
        existingEmployee.setEmail(requestDTO.getEmail());
        existingEmployee.setPhone(requestDTO.getPhone());
        existingEmployee.setDepartment(requestDTO.getDepartment());
        existingEmployee.setJobTitle(requestDTO.getJobTitle());
        existingEmployee.setSalary(requestDTO.getSalary());
        existingEmployee.setJoiningDate(requestDTO.getJoiningDate());

        // Step 4: saveAndFlush() — forces Hibernate to immediately run the SQL UPDATE.
        // This ensures @PreUpdate fires right now (updating the updatedAt timestamp)
        // before we convert the entity to a DTO and return it.
        // Regular save() delays the SQL to end-of-transaction, so @PreUpdate fires too late.
        Employee updatedEmployee = employeeRepository.saveAndFlush(existingEmployee);

        return EmployeeResponseDTO.fromEntity(updatedEmployee);
    }

    // =========================================================
    // DELETE — Remove an employee
    // =========================================================
    @Override
    public void deleteEmployee(Long id) {
        // Step 1: Make sure they exist before trying to delete
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));

        // Step 2: Delete the record from the database
        employeeRepository.delete(employee);
    }
}
