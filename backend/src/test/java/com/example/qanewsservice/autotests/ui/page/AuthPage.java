package com.example.qanewsservice.autotests.ui.page;

import static com.codeborne.selenide.Selenide.open;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.example.qanewsservice.autotests.config.TestUser;
import io.qameta.allure.Step;

public class AuthPage extends BasePage {

    private final SelenideElement loginForm = byTestId("login-form");
    private final SelenideElement usernameInput = byTestId("username-input");
    private final SelenideElement passwordInput = byTestId("password-input");
    private final SelenideElement loginButton = byTestId("login-button");
    private final SelenideElement authErrorMessage = byTestId("auth-error-message");

    @Step("Открыть страницу авторизации")
    public AuthPage openPage() {
        open("/");
        return this;
    }

    @Step("Проверить, что страница авторизации открыта")
    public AuthPage shouldBeOpened() {
        shouldBeVisible(loginForm);
        return this;
    }

    @Step("Заполнить логин: {username}")
    public AuthPage fillUsername(String username) {
        usernameInput.shouldBe(Condition.visible, DEFAULT_TIMEOUT)
                .shouldBe(Condition.enabled, DEFAULT_TIMEOUT)
                .clear();
        usernameInput.setValue(username);
        return this;
    }

    @Step("Заполнить пароль")
    public AuthPage fillPassword(String password) {
        passwordInput.shouldBe(Condition.visible, DEFAULT_TIMEOUT)
                .shouldBe(Condition.enabled, DEFAULT_TIMEOUT)
                .clear();
        passwordInput.setValue(password);
        return this;
    }

    @Step("Отправить форму авторизации")
    public NewsManagementPage submitSuccessful() {
        clickWhenEnabled(loginButton);
        return PageFactory.get(NewsManagementPage.class);
    }

    @Step("Отправить форму авторизации и остаться на странице авторизации")
    public AuthPage submitWithError() {
        clickWhenEnabled(loginButton);
        return this;
    }

    @Step("Авторизоваться пользователем {user}")
    public NewsManagementPage loginAs(TestUser user) {
        return fillUsername(user.username())
                .fillPassword(user.password())
                .submitSuccessful();
    }

    @Step("Проверить, что ошибка авторизации содержит текст: {text}")
    public AuthPage shouldHaveAuthError(String text) {
        authErrorMessage.shouldBe(Condition.visible, DEFAULT_TIMEOUT)
                .shouldHave(Condition.text(text));
        return this;
    }
}
