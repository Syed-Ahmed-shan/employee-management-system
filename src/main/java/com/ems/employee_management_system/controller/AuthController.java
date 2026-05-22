package com.ems.employee_management_system.controller;

import com.ems.employee_management_system.dto.ChangePasswordRequestDTO;
import com.ems.employee_management_system.dto.JwtResponseDTO;
import com.ems.employee_management_system.dto.LoginRequestDTO;
import com.ems.employee_management_system.dto.RegisterRequestDTO;
import com.ems.employee_management_system.entity.User;
import com.ems.employee_management_system.repository.UserRepository;
import com.ems.employee_management_system.response.ApiResponse;
import com.ems.employee_management_system.security.JwtUtils;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * Handles user registration and login.
 *
 * These endpoints are PUBLIC — no token needed (defined in SecurityConfig).
 *
 * POST /api/auth/register → Create a new user account
 * POST /api/auth/login    → Login and receive a JWT token
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public AuthController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          AuthenticationManager authenticationManager,
                          JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    /**
     * POST /api/auth/register
     *
     * Creates a new user account.
     * Password is hashed with BCrypt before saving — NEVER stored as plain text.
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(
            @Valid @RequestBody RegisterRequestDTO requestDTO) {

        // Check username is not already taken
        if (userRepository.existsByUsername(requestDTO.getUsername())) {
            ApiResponse<String> response = ApiResponse.error(
                    "Username '" + requestDTO.getUsername() + "' is already taken");
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }

        // Build and save the user with hashed password
        User user = new User(
                requestDTO.getUsername(),
                passwordEncoder.encode(requestDTO.getPassword()), // BCrypt hash
                requestDTO.getRole()
        );
        userRepository.save(user);

        ApiResponse<String> response = ApiResponse.success(
                "User registered successfully", "Account created for: " + requestDTO.getUsername());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * POST /api/auth/login
     *
     * Authenticates the user and returns a JWT token.
     *
     * How it works:
     * 1. AuthenticationManager calls UserDetailsService.loadUserByUsername()
     * 2. Compares the provided password with the stored BCrypt hash
     * 3. If valid → generate JWT token and return it
     * 4. If invalid → throws exception → GlobalExceptionHandler returns 401
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JwtResponseDTO>> login(
            @Valid @RequestBody LoginRequestDTO requestDTO) {

        // This line does all the work:
        // → Loads user from DB, checks password, throws exception if wrong
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        requestDTO.getUsername(),
                        requestDTO.getPassword()
                )
        );

        // Get the authenticated user object
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // Generate JWT token
        String token = jwtUtils.generateToken(userDetails);

        // Get the role from the first authority
        String role = userDetails.getAuthorities().iterator().next().getAuthority();
        
        // Find user to get employeeId and firstLogin flag
        User user = userRepository.findByUsername(userDetails.getUsername()).orElse(null);
        Long employeeId = user != null ? user.getEmployeeId() : null;
        boolean firstLogin = user != null && user.isFirstLogin();

        JwtResponseDTO jwtResponse = new JwtResponseDTO(token, userDetails.getUsername(), role, employeeId, firstLogin);
        ApiResponse<JwtResponseDTO> response = ApiResponse.success("Login successful", jwtResponse);
        return ResponseEntity.ok(response);
    }

    /**
     * PUT /api/auth/change-password
     *
     * Allows a logged-in user to change their password.
     * Also sets firstLogin to false.
     */
    @PutMapping("/change-password")
    public ResponseEntity<ApiResponse<String>> changePassword(
            @Valid @RequestBody ChangePasswordRequestDTO requestDTO,
            Authentication authentication) {
        
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Verify old password
        if (!passwordEncoder.matches(requestDTO.getOldPassword(), user.getPassword())) {
            return new ResponseEntity<>(
                    ApiResponse.error("Incorrect old password"), 
                    HttpStatus.BAD_REQUEST);
        }

        // Update password and flag
        user.setPassword(passwordEncoder.encode(requestDTO.getNewPassword()));
        user.setFirstLogin(false);
        userRepository.save(user);

        return ResponseEntity.ok(ApiResponse.success("Password changed successfully", null));
    }
}
