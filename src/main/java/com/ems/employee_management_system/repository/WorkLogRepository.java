package com.ems.employee_management_system.repository;

import com.ems.employee_management_system.entity.WorkLog;
import com.ems.employee_management_system.entity.WorkLogStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface WorkLogRepository extends JpaRepository<WorkLog, Long> {

    // Get all work logs for a specific employee (newest first)
    List<WorkLog> findByEmployeeIdOrderByWorkDateDesc(Long employeeId);

    // Check if an employee has any work logs
    boolean existsByEmployeeId(Long employeeId);

    // =========================================================
    // Aggregate Queries for Reports (Feature 3)
    // =========================================================

    @Query("SELECT COUNT(w) FROM WorkLog w WHERE w.employee.id = :employeeId")
    long countByEmployeeId(@Param("employeeId") Long employeeId);

    @Query("SELECT COALESCE(SUM(w.hoursWorked), 0.0) FROM WorkLog w WHERE w.employee.id = :employeeId")
    double sumHoursByEmployeeId(@Param("employeeId") Long employeeId);

    @Query("SELECT COUNT(w) FROM WorkLog w WHERE w.employee.id = :employeeId AND w.status = :status")
    long countByEmployeeIdAndStatus(@Param("employeeId") Long employeeId,
                                    @Param("status") WorkLogStatus status);

    // =========================================================
    // Dynamic Filter Query (Feature 4)
    // =========================================================

    /**
     * One query that handles ALL filter combinations.
     *
     * How the optional filtering works:
     *   (:employeeId IS NULL OR w.employee.id = :employeeId)
     *   → If employeeId is null (not provided) → condition is TRUE → no filter applied
     *   → If employeeId = 2 → filters only that employee's logs
     *
     * Same logic for status, startDate, endDate.
     * This means ONE query handles every possible combination of filters.
     */
    @Query("SELECT w FROM WorkLog w WHERE " +
           "(:employeeId IS NULL OR w.employee.id = :employeeId) AND " +
           "(:status IS NULL OR w.status = :status) AND " +
           "(:startDate IS NULL OR w.workDate >= :startDate) AND " +
           "(:endDate IS NULL OR w.workDate <= :endDate) " +
           "ORDER BY w.workDate DESC")
    List<WorkLog> filterWorkLogs(@Param("employeeId") Long employeeId,
                                  @Param("status") WorkLogStatus status,
                                  @Param("startDate") LocalDate startDate,
                                  @Param("endDate") LocalDate endDate);
}
