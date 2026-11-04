package com.example.qanewsservice.autotests.api.steps;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.example.qanewsservice.autotests.api.response.ErrorResponse;
import com.example.qanewsservice.autotests.api.response.LoginResponse;
import io.qameta.allure.Step;

public final class AuthSteps {

    private AuthSteps() {
    }

    @Step("Проверить успешный ответ авторизации")
    public static void assertSuccessfulLogin(LoginResponse response, String expectedUsername, String expectedRole) {
        assertSoftly(softly -> {
            softly.assertThat(response.getToken()).isNotBlank();
            softly.assertThat(response.getTokenType()).isEqualTo("Bearer");
            softly.assertThat(response.getUsername()).isEqualTo(expectedUsername);
            softly.assertThat(response.getRole()).isEqualTo(expectedRole);
        });
    }

    @Step("Проверить ответ с ошибкой авторизации")
    public static void assertUnauthorized(ErrorResponse response) {
        assertSoftly(softly -> {
            softly.assertThat(response.getStatus()).isEqualTo(401);
            softly.assertThat(response.getError()).isEqualTo("Unauthorized");
            softly.assertThat(response.getMessage()).isEqualTo("Invalid username or password");
            softly.assertThat(response.getPath()).isEqualTo("/api/auth/login");
        });
    }

    @Step("Проверить поле ошибки валидации авторизации: {field}")
    public static void assertLoginValidationError(ErrorResponse response, String field) {
        assertThat(response.getStatus()).isEqualTo(400);
        assertThat(response.getValidationErrors()).containsKey(field);
    }
}
