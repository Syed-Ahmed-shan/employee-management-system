package com.ems.employee_management_system.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT Authentication Filter — runs ONCE for every HTTP request.
 *
 * What it does step by step:
 *
 * 1. Look at the "Authorization" header of the incoming request
 *    Example header: "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
 *
 * 2. Extract the token (remove the "Bearer " prefix)
 *
 * 3. Extract the username from the token using JwtUtils
 *
 * 4. Load the full user from the database
 *
 * 5. Validate the token (not expired, signature correct, username matches)
 *
 * 6. If valid → tell Spring Security "this user is authenticated"
 *    Spring Security will then check if they have permission for the endpoint.
 *
 * 7. If invalid → do nothing → Spring Security will block the request with 401
 *
 * OncePerRequestFilter → guarantees this filter runs exactly once per request
 * (some filters run twice in Spring, this prevents that)
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);

    private final JwtUtils jwtUtils;
    private final UserDetailsServiceImpl userDetailsService;

    public JwtAuthFilter(JwtUtils jwtUtils, UserDetailsServiceImpl userDetailsService) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        try {
            // Step 1 & 2: Get the token from the Authorization header
            String token = extractTokenFromRequest(request);

            // Step 3 & 4 & 5 & 6: If token exists and is valid, authenticate
            if (token != null) {
                String username = jwtUtils.extractUsername(token);

                // Only authenticate if not already authenticated
                if (username != null &&
                    SecurityContextHolder.getContext().getAuthentication() == null) {

                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    if (jwtUtils.validateToken(token, userDetails)) {
                        // Create authentication object — tells Spring Security "user is verified"
                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails,
                                        null,                          // credentials (not needed after login)
                                        userDetails.getAuthorities()); // roles/permissions

                        // Attach request details (IP address, session info)
                        authToken.setDetails(new WebAuthenticationDetailsSource()
                                .buildDetails(request));

                        // Store authentication in Spring Security context
                        // After this line, Spring Security knows who the user is
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Could not authenticate user: {}", e.getMessage());
        }

        // Always continue to the next filter/controller — even if auth failed
        // Spring Security will reject the request if auth is required but missing
        filterChain.doFilter(request, response);
    }

    /**
     * Extracts the JWT token from the Authorization header.
     *
     * Expected header format: "Bearer <token>"
     * We strip "Bearer " and return just the token string.
     *
     * Returns null if header is missing or in wrong format.
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        // Check header exists and starts with "Bearer "
        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7); // skip "Bearer " (7 characters)
        }
        return null;
    }
}
