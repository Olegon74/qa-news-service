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
@Story("Архивирование новостей")
@Tag("UI")
class ArchivingNewsUiTests extends BaseUiTest {

    @Test
    @Tag("SMOKE")
    @AllureId("UI-ADMIN-005")
    @DisplayName("Архивирование новости через UI")
    void archiveNewsThroughUi() {
        createPublishedNewsThroughApi(newsModel);

        authPage.openPage()
                .loginAs(TestUser.ADMIN)
                .shouldBeOpened()
                .newsCard(newsModel.getTitle())
                .clickArchive()
                .shouldHaveSuccessMessage("Success: News archived")
                .shouldHaveNews(newsModel.getTitle())
                .newsCard(newsModel.getTitle())
                .shouldHaveStatus(NewsStatus.ARCHIVED);
    }
}
