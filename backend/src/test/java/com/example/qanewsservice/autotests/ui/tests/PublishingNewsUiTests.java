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
@Story("Публикация новостей")
@Tag("UI")
class PublishingNewsUiTests extends BaseUiTest {

    @Test
    @Tag("SMOKE")
    @AllureId("UI-ADMIN-003")
    @DisplayName("Публикация новости через UI")
    void publishNewsThroughUi() {
        createDraftNewsThroughApi(newsModel);

        authPage.openPage()
                .loginAs(TestUser.ADMIN)
                .shouldBeOpened()
                .shouldHaveNews(newsModel.getTitle())
                .newsCard(newsModel.getTitle())
                .clickPublish()
                .shouldHaveSuccessMessage("Success: News published")
                .shouldHaveNews(newsModel.getTitle())
                .newsCard(newsModel.getTitle())
                .shouldHaveStatus(NewsStatus.PUBLISHED);
    }
}
