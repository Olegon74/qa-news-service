package com.example.qanewsservice.autotests.ui.tests;

import com.example.qanewsservice.autotests.config.TestUser;
import com.example.qanewsservice.autotests.ui.helper.BaseUiTest;
import io.qameta.allure.AllureId;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Epic("QA News Service")
@Feature("UI")
@Story("Авторизация")
@Tag("UI")
class AuthorizationUiTests extends BaseUiTest {

    @Test
    @Tag("SMOKE")
    @AllureId("UI-ADMIN-001")
    @DisplayName("Авторизация под администратором")
    void loginAsAdmin() {
        authPage.openPage()
                .shouldBeOpened()
                .loginAs(TestUser.ADMIN)
                .shouldBeOpened()
                .shouldHaveCurrentUser(TestUser.ADMIN.username(), TestUser.ADMIN.role())
                .shouldHaveCreateForm();
    }

    @Test
    @Tag("SMOKE")
    @AllureId("UI-VIEWER-001")
    @DisplayName("Авторизация под наблюдателем")
    void loginAsViewer() {
        authPage.openPage()
                .shouldBeOpened()
                .loginAs(TestUser.VIEWER)
                .shouldBeOpened()
                .shouldHaveCurrentUser(TestUser.VIEWER.username(), TestUser.VIEWER.role())
                .shouldHaveViewerReadonlyMessage()
                .shouldNotHaveCreateForm();
    }

    @Test
    @AllureId("UI-VALIDATION-002")
    @DisplayName("Неверные логин и пароль показывают ошибку авторизации")
    void wrongLoginAndPasswordShowAuthError() {
        authPage.openPage()
                .shouldBeOpened()
                .fillUsername(TestUser.ADMIN.username())
                .fillPassword("wrong-password")
                .submitWithError()
                .shouldHaveAuthError("Error: Unauthorized");
    }
}
