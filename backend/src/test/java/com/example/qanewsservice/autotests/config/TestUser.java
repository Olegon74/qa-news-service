package com.example.qanewsservice.autotests.config;

public enum TestUser {
    ADMIN("admin.username", "admin.password", "admin.token", "ADMIN"),
    VIEWER("viewer.username", "viewer.password", "viewer.token", "VIEWER");

    private final String usernameProperty;
    private final String passwordProperty;
    private final String tokenProperty;
    private final String role;

    TestUser(String usernameProperty, String passwordProperty, String tokenProperty, String role) {
        this.usernameProperty = usernameProperty;
        this.passwordProperty = passwordProperty;
        this.tokenProperty = tokenProperty;
        this.role = role;
    }

    public String username() {
        return ConfigService.getRequired(usernameProperty);
    }

    public String password() {
        return ConfigService.getRequired(passwordProperty);
    }

    public String token() {
        return ConfigService.get(tokenProperty);
    }

    public String role() {
        return role;
    }
}
