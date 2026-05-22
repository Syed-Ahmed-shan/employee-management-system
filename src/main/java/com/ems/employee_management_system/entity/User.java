package com.ems.employee_management_system.entity;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Represents a system user (login account) — separate from the Employee entity.
 *
 * Why separate from Employee?
 * → Not every system user is an employee (e.g. an admin might not be in payroll)
 * → Separation of concerns: authentication data vs. HR data
 *
 * Implements UserDetails — this is Spring Security's interface.
 * Spring Security calls these methods to check credentials and permissions.
 *
 * Fields:
 *   username → used for login (can be email or a chosen username)
 *   password → stored as BCrypt hash (NEVER plain text)
 *   role     → determines what this user can access
 */
@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;  // login username

    @Column(nullable = false)
    private String password;  // BCrypt hashed password

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(name = "employee_id")
    private Long employeeId;

    @Column(name = "first_login", nullable = false)
    private boolean firstLogin = true;

    // =============================================
    // UserDetails interface methods — Spring Security uses these
    // =============================================

    /**
     * Returns the roles/permissions this user has.
     * Spring Security uses this to check if the user can access an endpoint.
     *
     * We wrap our Role enum in SimpleGrantedAuthority.
     * Example: Role.ROLE_ADMIN → authority = "ROLE_ADMIN"
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    // These return true to keep accounts enabled/non-expired/non-locked
    // In production you'd add fields for these and check them
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }

    // =============================================
    // Constructors
    // =============================================

    public User() {}

    public User(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // =============================================
    // Getters and Setters
    // =============================================

    public Long getId() { return id; }

    @Override
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    @Override
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }

    public boolean isFirstLogin() { return firstLogin; }
    public void setFirstLogin(boolean firstLogin) { this.firstLogin = firstLogin; }
}
