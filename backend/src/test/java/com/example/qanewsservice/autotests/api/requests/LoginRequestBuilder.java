package com.example.qanewsservice.autotests.api.requests;

import com.example.qanewsservice.autotests.api.models.LoginRequest;
import com.example.qanewsservice.autotests.config.TestUser;

public final class LoginRequestBuilder {

    private LoginRequestBuilder() {
    }

    public static LoginRequest buildLoginRequest(TestUser user) {
        return LoginRequest.builder()
                .username(user.username())
                .password(user.password())
                .build();
    }

    public static LoginRequest buildLoginRequest(String username, String password) {
        return LoginRequest.builder()
                .username(username)
                .password(password)
                .build();
    }
}
