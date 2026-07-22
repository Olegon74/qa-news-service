package com.example.qanewsservice.auth.dto;

import com.example.qanewsservice.auth.model.UserRole;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponse {

    private String token;
    private String tokenType;
    private String username;
    private UserRole role;
}
