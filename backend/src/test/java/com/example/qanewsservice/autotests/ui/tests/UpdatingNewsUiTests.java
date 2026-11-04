package com.example.qanewsservice.autotests.ui.tests;

import com.example.qanewsservice.autotests.api.models.NewsStatus;
import com.example.qanewsservice.autotests.config.TestUser;
import com.example.qanewsservice.autotests.ui.helper.BaseUiTest;
import com.example.qanewsservice.autotests.ui.model.NewsUiModel;
import io.qameta.allure.AllureId;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Epic("QA News Service")
@Feature("UI")
@Story("Обновление новостей")
@Tag("UI")
class UpdatingNewsUiTests extends BaseUiTest {

    @Test
    @AllureId("UI-ADMIN-004")
    @DisplayName("Редактирование опубликованной новости через UI")
    void editPublishedNewsThroughUi() {
        createPublishedNewsThroughApi(newsModel);
        NewsUiModel updatedModel = generateNewsUiModel();

        authPage.openPage()
                .loginAs(TestUser.ADMIN)
                .shouldBeOpened()
                .newsCard(newsModel.getTitle())
                .clickEdit()
                .updateNews(updatedModel)
                .shouldHaveSuccessMessage("Success: News updated")
                .shouldHaveNews(updatedModel.getTitle())
                .newsCard(updatedModel.getTitle())
                .shouldHaveStatus(NewsStatus.PUBLISHED);
    }
}
