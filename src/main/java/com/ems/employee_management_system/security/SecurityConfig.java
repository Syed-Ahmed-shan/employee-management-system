package com.ems.employee_management_system.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

/**
 * The main Spring Security configuration class.
 *
 * This is where we define:
 *   1. Which endpoints are public (no token needed)
 *   2. Which endpoints require specific roles
 *   3. How passwords are hashed (BCrypt)
 *   4. That we use stateless JWT (no sessions)
 *   5. Where our JWT filter fits in the chain
 *
 * Role Access Rules:
 *   ADMIN    → full access to everything
 *   MANAGER  → read employees, manage work logs, reports, export
 *   EMPLOYEE → read-only on work logs and reports only
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private final JwtAuthFilter jwtAuthFilter;
    private final CorsConfigurationSource corsConfigurationSource;

    public SecurityConfig(UserDetailsServiceImpl userDetailsService,
                          JwtAuthFilter jwtAuthFilter,
                          CorsConfigurationSource corsConfigurationSource) {
        this.userDetailsService = userDetailsService;
        this.jwtAuthFilter = jwtAuthFilter;
        this.corsConfigurationSource = corsConfigurationSource;
    }

    /**
     * BCryptPasswordEncoder — industry standard for hashing passwords.
     *
     * BCrypt is a one-way hash:
     *   "password123" → "$2a$10$xyz..." (60-char hash)
     *
     * You CANNOT reverse it — that's the point.
     * On login, we hash the input and compare hashes (never compare plain text).
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * AuthenticationManager — Spring Security's core login component.
     * It uses our UserDetailsService + PasswordEncoder to verify credentials.
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * DaoAuthenticationProvider — connects UserDetailsService and PasswordEncoder.
     * Spring Security 7 requires passing UserDetailsService via constructor.
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    /**
     * The main security filter chain — defines ALL security rules.
     *
     * Order matters in Spring Security:
     *   More specific rules must come BEFORE more general rules.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Enable CORS using our CorsConfig bean
            .cors(cors -> cors.configurationSource(corsConfigurationSource))
            // Disable CSRF — not needed for REST APIs
            .csrf(AbstractHttpConfigurer::disable)

            // Stateless session — no server-side sessions, we use JWT instead
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // =============================================
            // AUTHORIZATION RULES
            // =============================================
            .authorizeHttpRequests(auth -> auth

                // PUBLIC — no token needed
                .requestMatchers("/api/auth/**").permitAll()

                // EMPLOYEE routes — CREATE employees: ADMIN only
                .requestMatchers(HttpMethod.POST, "/api/employees/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/employees/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/employees/**").hasAuthority("ROLE_ADMIN")
                // Reading employees: ADMIN + MANAGER
                .requestMatchers(HttpMethod.GET, "/api/employees/**")
                    .hasAnyAuthority("ROLE_ADMIN", "ROLE_MANAGER")

                // WORKLOGS — all authenticated users can read
                .requestMatchers(HttpMethod.GET, "/api/worklogs/**")
                    .hasAnyAuthority("ROLE_ADMIN", "ROLE_MANAGER", "ROLE_EMPLOYEE")
                // Creating/updating work logs: ADMIN + MANAGER + EMPLOYEE
                .requestMatchers(HttpMethod.POST, "/api/worklogs/**")
                    .hasAnyAuthority("ROLE_ADMIN", "ROLE_MANAGER", "ROLE_EMPLOYEE")
                // Updating work logs: ADMIN + MANAGER + EMPLOYEE (so employees can update status)
                .requestMatchers(HttpMethod.PUT, "/api/worklogs/**")
                    .hasAnyAuthority("ROLE_ADMIN", "ROLE_MANAGER", "ROLE_EMPLOYEE")
                // Deleting work logs: ADMIN + MANAGER only
                .requestMatchers(HttpMethod.DELETE, "/api/worklogs/**")
                    .hasAnyAuthority("ROLE_ADMIN", "ROLE_MANAGER")

                // REPORTS — ADMIN + MANAGER only
                .requestMatchers("/api/reports/**")
                    .hasAnyAuthority("ROLE_ADMIN", "ROLE_MANAGER")

                // EXPORT — ADMIN + MANAGER only
                .requestMatchers("/api/export/**")
                    .hasAnyAuthority("ROLE_ADMIN", "ROLE_MANAGER")

                // Everything else requires authentication
                .anyRequest().authenticated()
            )

            // Wire in our custom authentication provider
            .authenticationProvider(authenticationProvider())

            // Add JWT filter BEFORE Spring's default login filter
            // This ensures JWT is checked before any other authentication attempt
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
