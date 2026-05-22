package com.ems.employee_management_system.repository;

import com.ems.employee_management_system.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repository for User entity.
 *
 * findByUsername → Spring auto-generates:
 *   SELECT * FROM users WHERE username = ?
 *
 * Returns Optional<User> because the user might not exist.
 * Used by Spring Security to load user during login.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
}
