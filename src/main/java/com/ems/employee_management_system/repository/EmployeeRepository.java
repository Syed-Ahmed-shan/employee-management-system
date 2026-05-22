package com.ems.employee_management_system.repository;

import com.ems.employee_management_system.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * The Repository layer — this is where we interact with the database.
 *
 * By extending JpaRepository<Employee, Long>, Spring automatically gives us:
 * save(employee) → INSERT or UPDATE a record
 * findById(id) → SELECT by primary key
 * findAll() → SELECT all records
 * deleteById(id) → DELETE by primary key
 * existsById(id) → check if a record exists
 * count() → count all records
 * ...and many more!
 *
 * <Employee, Long> means:
 * Employee → the entity this repo manages
 * Long → the type of the primary key (the "id" field)
 *
 * @Repository → tells Spring this is a repository bean (optional here
 *             because JpaRepository is already detected, but good for clarity)
 */
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    /**
     * Custom query method — we define it by naming convention.
     * Spring automatically generates the SQL: SELECT * FROM employees WHERE email =
     * ?
     *
     * Returns Optional<Employee> because the email might not exist.
     * Optional prevents NullPointerException — you check if value is present first.
     */
    Optional<Employee> findByEmail(String email);

    /**
     * Check if an employee with this email already exists.
     * Returns true or false.
     * SQL generated: SELECT COUNT(*) > 0 FROM employees WHERE email = ?
     * Useful for checking duplicates before saving.
     */
    boolean existsByEmail(String email);
}
