package com.example.qanewsservice.autotests.api.tests;

import static com.example.qanewsservice.autotests.api.assertions.Conditions.hasStatusCode;

import com.example.qanewsservice.autotests.api.clients.BaseApiTest;
import com.example.qanewsservice.autotests.api.requests.LoginRequestBuilder;
import com.example.qanewsservice.autotests.api.response.ErrorResponse;
import com.example.qanewsservice.autotests.api.response.LoginResponse;
import com.example.qanewsservice.autotests.api.steps.AuthSteps;
import com.example.qanewsservice.autotests.config.TestUser;
import io.qameta.allure.AllureId;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Epic("QA News Service")
@Feature("API")
@Story("AuthApiTests")
@Tag("API")
class AuthApiTests extends BaseApiTest {

    @Test
    @Tag("SMOKE")
    @Tag("Positive")
    @AllureId("API-AUTH-001")
    @DisplayName("Авторизация под администратором возвращает токен")
    void loginAsAdminReturnsToken() {
        LoginResponse response = authControllerClient.login(LoginRequestBuilder.buildLoginRequest(TestUser.ADMIN))
                .should(hasStatusCode(200))
                .as(LoginResponse.class);

        AuthSteps.assertSuccessfulLogin(response, TestUser.ADMIN.username(), TestUser.ADMIN.role());
    }

    @Test
    @Tag("SMOKE")
    @Tag("Positive")
    @AllureId("API-AUTH-002")
    @DisplayName("Авторизация под наблюдателем возвращает токен")
    void loginAsViewerReturnsToken() {
        LoginResponse response = authControllerClient.login(LoginRequestBuilder.buildLoginRequest(TestUser.VIEWER))
                .should(hasStatusCode(200))
                .as(LoginResponse.class);

        AuthSteps.assertSuccessfulLogin(response, TestUser.VIEWER.username(), TestUser.VIEWER.role());
    }

    @Test
    @Tag("Negative")
    @AllureId("API-AUTH-003")
    @DisplayName("Авторизация с неверным паролем возвращает 401")
    void loginWithWrongPasswordReturnsUnauthorized() {
        ErrorResponse response = authControllerClient
                .login(LoginRequestBuilder.buildLoginRequest(TestUser.ADMIN.username(), "wrong-password"))
                .should(hasStatusCode(401))
                .as(ErrorResponse.class);

        AuthSteps.assertUnauthorized(response);
    }

    @Test
    @Tag("Negative")
    @AllureId("API-AUTH-004")
    @DisplayName("Авторизация неизвестного пользователя возвращает 401")
    void loginWithUnknownUserReturnsUnauthorized() {
        ErrorResponse response = authControllerClient
                .login(LoginRequestBuilder.buildLoginRequest("unknown-user", "admin123"))
                .should(hasStatusCode(401))
                .as(ErrorResponse.class);

        AuthSteps.assertUnauthorized(response);
    }

    @Test
    @Tag("Negative")
    @AllureId("API-AUTH-005")
    @DisplayName("Авторизация с пустым логином возвращает 400")
    void loginWithEmptyUsernameReturnsBadRequest() {
        ErrorResponse response = authControllerClient
                .login(LoginRequestBuilder.buildLoginRequest("", TestUser.ADMIN.password()))
                .should(hasStatusCode(400))
                .as(ErrorResponse.class);

        AuthSteps.assertLoginValidationError(response, "username");
    }

    @Test
    @Tag("Negative")
    @AllureId("API-AUTH-006")
    @DisplayName("Авторизация с пустым паролем возвращает 400")
    void loginWithEmptyPasswordReturnsBadRequest() {
        ErrorResponse response = authControllerClient
                .login(LoginRequestBuilder.buildLoginRequest(TestUser.ADMIN.username(), ""))
                .should(hasStatusCode(400))
                .as(ErrorResponse.class);

        AuthSteps.assertLoginValidationError(response, "password");
    }
}
