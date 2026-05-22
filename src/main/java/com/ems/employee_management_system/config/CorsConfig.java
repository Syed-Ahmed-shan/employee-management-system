package com.ems.employee_management_system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * CORS Configuration — allows the React frontend to call our API.
 *
 * Without this, the browser blocks cross-origin requests:
 * Frontend on localhost:5173 → Backend on localhost:8080 = BLOCKED by browser
 * Frontend on vercel.app    → Backend on railway.app    = BLOCKED by browser
 *
 * With this config, we explicitly allow these cross-origin requests.
 *
 * allowedOriginPatterns("*") → allow all origins (for development + production)
 * In production you could restrict to just your Vercel URL.
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Allow requests from any frontend origin (localhost dev + Vercel production)
        config.setAllowedOriginPatterns(List.of("*"));

        // Allow standard HTTP methods
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Allow all headers including Authorization (needed for JWT Bearer token)
        config.setAllowedHeaders(List.of("*"));

        // Allow credentials (cookies, authorization headers)
        config.setAllowCredentials(true);

        // Apply this CORS config to all endpoints
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
