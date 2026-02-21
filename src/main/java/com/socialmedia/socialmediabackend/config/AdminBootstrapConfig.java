package com.socialmedia.socialmediabackend.config;

import com.socialmedia.socialmediabackend.User;
import com.socialmedia.socialmediabackend.UserRole;
import com.socialmedia.socialmediabackend.VerificationStatus;
import com.socialmedia.socialmediabackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminBootstrapConfig {

    @Bean
    public CommandLineRunner seedAdminUser(UserRepository userRepository,
                                           PasswordEncoder passwordEncoder,
                                           @Value("${app.bootstrap.admin.enabled:true}") boolean enabled,
                                           @Value("${app.bootstrap.admin.email:admin@socialmedia.local}") String adminEmail,
                                           @Value("${app.bootstrap.admin.password:Admin@123}") String adminPassword) {
        return args -> {
            if (!enabled) {
                return;
            }

            User admin = userRepository.findByEmail(adminEmail).orElseGet(User::new);
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setRole(UserRole.ROLE_ADMIN);
            admin.setVerificationStatus(VerificationStatus.VERIFIED);
            admin.setVerificationToken(null);
            admin.setTokenExpiry(null);
            userRepository.save(admin);
        };
    }
}
