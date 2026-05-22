package com.ems.employee_management_system.config;

import com.ems.employee_management_system.entity.User;
import com.ems.employee_management_system.entity.Role;
import com.ems.employee_management_system.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // If the users table is completely empty, create the master admin account
        if (userRepository.count() == 0) {
            System.out.println("⚠️ Empty database detected. Creating default Master Admin account...");
            
            User admin = new User();
            admin.setUsername("Ahmed");
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setRole(Role.ROLE_ADMIN);
            admin.setFirstLogin(false);
            
            userRepository.save(admin);
            System.out.println("✅ Master Admin created -> Username: Ahmed | Password: admin");
        } else {
            System.out.println("✅ Database already contains data. Skipping automatic seeding.");
        }
    }
}
