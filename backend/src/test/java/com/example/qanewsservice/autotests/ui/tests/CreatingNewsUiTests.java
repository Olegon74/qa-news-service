package com.example.qanewsservice.autotests.ui.tests;

import com.example.qanewsservice.autotests.api.models.NewsStatus;
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
@Story("Создание новостей")
@Tag("UI")
class CreatingNewsUiTests extends BaseUiTest {

    @Test
    @Tag("SMOKE")
    @AllureId("UI-ADMIN-002")
    @DisplayName("Создание новости через UI")
    void createNewsThroughUi() {
        authPage.openPage()
                .loginAs(TestUser.ADMIN)
                .shouldBeOpened()
                .createNews(newsModel)
                .shouldHaveSuccessMessage("Success: News created")
                .shouldHaveNews(newsModel.getTitle())
                .newsCard(newsModel.getTitle())
                .shouldHaveStatus(NewsStatus.DRAFT);

        trackNewsCreatedThroughUi(newsModel.getTitle());
    }

    @Test
    @AllureId("UI-VALIDATION-001")
    @DisplayName("Пустой заголовок показывает ошибку валидации")
    void emptyTitleShowsValidationError() {
        newsModel.setTitle("");

        authPage.openPage()
                .loginAs(TestUser.ADMIN)
                .shouldBeOpened()
                .createNews(newsModel)
                .shouldHaveValidationError("Error: Title is required");
    }
}
