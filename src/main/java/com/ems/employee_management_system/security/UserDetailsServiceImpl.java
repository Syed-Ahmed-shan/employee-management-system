package com.ems.employee_management_system.security;

import com.ems.employee_management_system.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Tells Spring Security HOW to load a user from our database.
 *
 * Spring Security calls loadUserByUsername() during:
 *   1. Login — to find the user and verify their password
 *   2. Every request — to verify the JWT token belongs to a real user
 *
 * Our User entity already implements UserDetails, so we just return it directly.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Find user in DB, throw exception if not found
        // Spring Security catches this and returns 401 Unauthorized
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found with username: " + username));
    }
}
