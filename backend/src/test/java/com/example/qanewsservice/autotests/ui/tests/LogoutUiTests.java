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
@Story("Выход из аккаунта")
@Tag("UI")
class LogoutUiTests extends BaseUiTest {

    @Test
    @Tag("SMOKE")
    @AllureId("UI-LOGOUT-001")
    @DisplayName("Выход возвращает пользователя на страницу авторизации")
    void logoutReturnsUserToLoginPage() {
        authPage.openPage()
                .loginAs(TestUser.ADMIN)
                .shouldBeOpened()
                .logout()
                .shouldBeOpened();
    }
}
