package com.example.qanewsservice.auth.config;

import com.example.qanewsservice.auth.model.User;
import com.example.qanewsservice.auth.model.UserRole;
import com.example.qanewsservice.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner createInitialUsers() {
        return args -> {
            createUserIfMissing("admin", "admin123", UserRole.ADMIN);
            createUserIfMissing("viewer", "viewer123", UserRole.VIEWER);
        };
    }

    private void createUserIfMissing(String username, String password, UserRole role) {
        if (userRepository.existsByUsername(username)) {
            return;
        }

        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .role(role)
                .enabled(true)
                .build();
        userRepository.save(user);
    }
}
