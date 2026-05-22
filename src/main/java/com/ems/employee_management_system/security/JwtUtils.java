package com.ems.employee_management_system.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * Utility class for all JWT operations:
 *   1. Generate a token when user logs in
 *   2. Extract username from a token
 *   3. Validate a token (not expired, not tampered with)
 *
 * What is a JWT?
 * A JWT has 3 parts separated by dots:
 *   Header.Payload.Signature
 *
 * Header  → algorithm used (HS256)
 * Payload → data inside (username, role, expiry) — visible but NOT encrypted
 * Signature → cryptographic proof the token wasn't tampered with
 *
 * Example token:
 *   eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiJ9.xyz123
 */
@Component
public class JwtUtils {

    private static final Logger log = LoggerFactory.getLogger(JwtUtils.class);

    // Read values from application.properties
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    /**
     * Creates the SecretKey object from our plain-text secret string.
     * HMAC-SHA keys must be at least 256 bits → our secret string is long enough.
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Generate a JWT token for a successfully logged-in user.
     *
     * What goes inside the token (Payload):
     *   subject   → username (who this token belongs to)
     *   issuedAt  → when token was created
     *   expiration → when token expires (issuedAt + jwtExpirationMs)
     *
     * Then signed with our secret key → nobody can fake or modify it.
     */
    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(getSigningKey())
                .compact(); // builds the final token string
    }

    /**
     * Extract the username (subject) from a JWT token.
     * Called by our filter to find out WHO is making the request.
     */
    public String extractUsername(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    /**
     * Validate a JWT token:
     *   1. Check the signature (proves it wasn't tampered with)
     *   2. Check it's not expired
     *   3. Check the username matches the logged-in user
     *
     * Returns true if valid, false if anything is wrong.
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            String username = extractUsername(token);
            boolean isExpired = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getExpiration()
                    .before(new Date());

            return username.equals(userDetails.getUsername()) && !isExpired;
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }
}
