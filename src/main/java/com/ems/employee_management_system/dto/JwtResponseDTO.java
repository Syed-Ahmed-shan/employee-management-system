package com.ems.employee_management_system.dto;

/** What we send back after successful login — the JWT token + basic user info */
public class JwtResponseDTO {

    private String token;
    private String tokenType = "Bearer"; // standard prefix for JWT in Authorization header
    private String username;
    private String role;
    private Long employeeId;
    private boolean firstLogin;

    public JwtResponseDTO(String token, String username, String role, Long employeeId, boolean firstLogin) {
        this.token = token;
        this.username = username;
        this.role = role;
        this.employeeId = employeeId;
        this.firstLogin = firstLogin;
    }

    public String getToken() { return token; }
    public String getTokenType() { return tokenType; }
    public String getUsername() { return username; }
    public String getRole() { return role; }
    public Long getEmployeeId() { return employeeId; }
    public boolean isFirstLogin() { return firstLogin; }
}
