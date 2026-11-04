package com.example.qanewsservice.autotests.api.tests;

import static com.example.qanewsservice.autotests.api.assertions.Conditions.hasStatusCode;

import com.example.qanewsservice.autotests.api.clients.BaseApiTest;
import com.example.qanewsservice.autotests.api.models.NewsRequest;
import com.example.qanewsservice.autotests.api.models.NewsStatus;
import com.example.qanewsservice.autotests.api.requests.NewsRequestBuilder;
import com.example.qanewsservice.autotests.api.response.ErrorResponse;
import com.example.qanewsservice.autotests.api.response.NewsResponse;
import com.example.qanewsservice.autotests.api.steps.ErrorSteps;
import com.example.qanewsservice.autotests.api.steps.NewsSteps;
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
@Story("Обновление новостей")
@Tag("API")
class UpdatingNewsTests extends BaseApiTest {

    @Test
    @Tag("Positive")
    @AllureId("API-NEWS-007")
    @DisplayName("Обновление новости в статусе DRAFT возвращает 200 и сохраняет изменения")
    void updateDraftNewsReturnsOkAndCanBeReceivedById() {
        NewsResponse draft = createDraftNews();
        NewsRequest updateRequest = NewsRequestBuilder.buildUpdatedNewsRequest();

        NewsResponse updated = newsControllerClient.updateNews(draft.getId(), updateRequest, TestUser.ADMIN)
                .should(hasStatusCode(200))
                .as(NewsResponse.class);

        NewsSteps.validateRequiredFields(updated);
        NewsSteps.assertId(updated, draft.getId());
        NewsSteps.assertResponseMatchesRequest(updated, updateRequest);
        NewsSteps.assertStatus(updated, NewsStatus.DRAFT);

        NewsResponse receivedById = newsControllerClient.getNewsById(draft.getId(), TestUser.ADMIN)
                .should(hasStatusCode(200))
                .as(NewsResponse.class);

        NewsSteps.validateRequiredFields(receivedById);
        NewsSteps.assertResponseMatchesSavedResponse(receivedById, updated);
    }

    @Test
    @Tag("Positive")
    @AllureId("API-NEWS-008")
    @DisplayName("Обновление новости в статусе PUBLISHED возвращает 200 и сохраняет изменения")
    void updatePublishedNewsReturnsOkAndCanBeReceivedById() {
        NewsResponse published = createPublishedNews();
        NewsRequest updateRequest = NewsRequestBuilder.buildUpdatedNewsRequest();

        NewsResponse updated = newsControllerClient.updateNews(published.getId(), updateRequest, TestUser.ADMIN)
                .should(hasStatusCode(200))
                .as(NewsResponse.class);

        NewsSteps.validateRequiredFields(updated);
        NewsSteps.assertId(updated, published.getId());
        NewsSteps.assertResponseMatchesRequest(updated, updateRequest);
        NewsSteps.assertStatus(updated, NewsStatus.PUBLISHED);

        NewsResponse receivedById = newsControllerClient.getNewsById(published.getId(), TestUser.ADMIN)
                .should(hasStatusCode(200))
                .as(NewsResponse.class);

        NewsSteps.validateRequiredFields(receivedById);
        NewsSteps.assertResponseMatchesSavedResponse(receivedById, updated);
    }

    @Test
    @Tag("Negative")
    @AllureId("API-NEWS-009")
    @DisplayName("Обновление новости в статусе ARCHIVED возвращает 409")
    void updateArchivedNewsReturnsConflict() {
        NewsResponse archived = createArchivedNews();

        ErrorResponse response = newsControllerClient
                .updateNews(archived.getId(), NewsRequestBuilder.buildUpdatedNewsRequest(), TestUser.ADMIN)
                .should(hasStatusCode(409))
                .as(ErrorResponse.class);

        ErrorSteps.assertError(response, 409, "/api/news/" + archived.getId());
    }

    @Test
    @Tag("Negative")
    @AllureId("API-AUTHZ-006")
    @DisplayName("PUT /api/news/{id} под наблюдателем возвращает 403")
    void updateNewsAsViewerReturnsForbidden() {
        NewsResponse news = createDraftNews();

        ErrorResponse response = newsControllerClient
                .updateNews(news.getId(), NewsRequestBuilder.buildUpdatedNewsRequest(), TestUser.VIEWER)
                .should(hasStatusCode(403))
                .as(ErrorResponse.class);

        ErrorSteps.assertError(response, 403, "/api/news/" + news.getId());
    }
}
