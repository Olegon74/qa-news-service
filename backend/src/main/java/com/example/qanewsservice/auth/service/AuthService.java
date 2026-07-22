package com.example.qanewsservice.auth.service;

import com.example.qanewsservice.auth.dto.LoginRequest;
import com.example.qanewsservice.auth.dto.LoginResponse;
import com.example.qanewsservice.auth.model.User;
import com.example.qanewsservice.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

        if (!user.isEnabled() || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        return LoginResponse.builder()
                .token(jwtService.generateToken(user))
                .tokenType("Bearer")
                .username(user.getUsername())
                .role(user.getRole())
                .build();
    }
}
