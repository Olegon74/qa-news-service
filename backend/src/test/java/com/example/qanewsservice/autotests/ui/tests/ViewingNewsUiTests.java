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
@Story("Просмотр новостей")
@Tag("UI")
class ViewingNewsUiTests extends BaseUiTest {

    @Test
    @Tag("SMOKE")
    @AllureId("UI-VIEWER-002")
    @DisplayName("Наблюдатель видит список новостей")
    void viewerSeesNewsList() {
        createDraftNewsThroughApi(newsModel);

        authPage.openPage()
                .loginAs(TestUser.VIEWER)
                .shouldBeOpened()
                .shouldHaveNews(newsModel.getTitle())
                .newsCard(newsModel.getTitle())
                .shouldHaveStatus(NewsStatus.DRAFT);
    }

    @Test
    @AllureId("UI-VIEWER-003")
    @DisplayName("Наблюдатель может открыть детали новости")
    void viewerCanOpenNewsDetails() {
        createDraftNewsThroughApi(newsModel);

        authPage.openPage()
                .loginAs(TestUser.VIEWER)
                .shouldBeOpened()
                .newsCard(newsModel.getTitle())
                .clickView()
                .shouldHaveSelectedNewsDetails(newsModel, NewsStatus.DRAFT);
    }

    @Test
    @AllureId("UI-VIEWER-004")
    @DisplayName("Наблюдатель не видит кнопки действий администратора")
    void viewerDoesNotSeeAdminActionButtons() {
        createPublishedNewsThroughApi(newsModel);

        authPage.openPage()
                .loginAs(TestUser.VIEWER)
                .shouldBeOpened()
                .shouldHaveViewerReadonlyMessage()
                .shouldNotHaveCreateForm()
                .shouldHaveNews(newsModel.getTitle())
                .newsCard(newsModel.getTitle())
                .shouldNotHaveAdminActionButtons();
    }
}
