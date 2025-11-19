package com.saarthix.config;

import com.saarthix.model.User;
import com.saarthix.repository.UserRepository;
import com.saarthix.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {
    
    private final UserRepository userRepository;
    private final AuthService authService;
    
    @Value("${admin.username:ADMIN}")
    private String adminUsername;
    
    @Value("${admin.password}")
    private String adminPassword;
    
    @Override
    public void run(String... args) throws Exception {
        if (adminPassword == null || adminPassword.trim().isEmpty()) {
            System.err.println("WARNING: Admin password not configured. Admin user will not be created.");
            return;
        }
        
        // Check if admin user exists
        if (!userRepository.existsByUsername(adminUsername)) {
            // Create admin user
            User adminUser = new User();
            adminUser.setUsername(adminUsername);
            adminUser.setPassword(authService.hashPassword(adminPassword));
            adminUser.setEmail("admin@saarthix.com");
            adminUser.setName("Admin User");
            adminUser.setRoles(Set.of("ADMIN", "USER"));
            adminUser.setUserType("ADMIN");
            adminUser.setActive(true);
            adminUser.setCreatedAt(LocalDateTime.now());
            adminUser.setLastLoginAt(LocalDateTime.now());
            
            userRepository.save(adminUser);
            System.out.println("Admin user created successfully with username: " + adminUsername);
        } else {
            // Update admin password if it exists (in case password needs to be reset)
            userRepository.findByUsername(adminUsername).ifPresent(adminUser -> {
                // Only update if password is not set or needs to be reset
                if (adminUser.getPassword() == null || adminUser.getPassword().isEmpty()) {
                    adminUser.setPassword(authService.hashPassword(adminPassword));
                    adminUser.setRoles(Set.of("ADMIN", "USER"));
                    adminUser.setUserType("ADMIN");
                    adminUser.setActive(true);
                    userRepository.save(adminUser);
                    System.out.println("Admin user password updated successfully");
                }
            });
        }
    }
}

