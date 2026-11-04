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
@Story("Удаление новостей")
@Tag("UI")
class DeletingNewsUiTests extends BaseUiTest {

    @Test
    @AllureId("UI-ADMIN-006")
    @DisplayName("Удаление архивной новости через UI")
    void deleteArchivedNewsThroughUi() {
        createArchivedNewsThroughApi(newsModel);

        authPage.openPage()
                .loginAs(TestUser.ADMIN)
                .shouldBeOpened()
                .shouldHaveNews(newsModel.getTitle())
                .newsCard(newsModel.getTitle())
                .clickDelete()
                .shouldHaveSuccessMessage("Success: News deleted")
                .shouldNotHaveNews(newsModel.getTitle());
    }
}
