package com.example.qanewsservice.autotests.api.tests;

import static com.example.qanewsservice.autotests.api.assertions.Conditions.hasStatusCode;

import com.example.qanewsservice.autotests.api.clients.BaseApiTest;
import com.example.qanewsservice.autotests.api.models.NewsStatus;
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
@Story("Публикация новостей")
@Tag("API")
class PublishingNewsTests extends BaseApiTest {

    @Test
    @Tag("SMOKE")
    @Tag("Positive")
    @AllureId("API-NEWS-010")
    @DisplayName("Публикация новости в статусе DRAFT возвращает PUBLISHED и сохраняет статус")
    void publishDraftNewsReturnsPublishedAndCanBeReceivedById() {
        NewsResponse draft = createDraftNews();

        NewsResponse published = newsControllerClient.publishNews(draft.getId(), TestUser.ADMIN)
                .should(hasStatusCode(200))
                .as(NewsResponse.class);

        NewsSteps.validateRequiredFields(published);
        NewsSteps.assertId(published, draft.getId());
        NewsSteps.assertStatus(published, NewsStatus.PUBLISHED);

        NewsResponse receivedById = newsControllerClient.getNewsById(draft.getId(), TestUser.ADMIN)
                .should(hasStatusCode(200))
                .as(NewsResponse.class);

        NewsSteps.validateRequiredFields(receivedById);
        NewsSteps.assertResponseMatchesSavedResponse(receivedById, published);
    }

    @Test
    @Tag("Negative")
    @AllureId("API-NEWS-011")
    @DisplayName("Публикация новости в статусе PUBLISHED возвращает 409")
    void publishPublishedNewsReturnsConflict() {
        NewsResponse published = createPublishedNews();

        ErrorResponse response = newsControllerClient.publishNews(published.getId(), TestUser.ADMIN)
                .should(hasStatusCode(409))
                .as(ErrorResponse.class);

        ErrorSteps.assertError(response, 409, "/api/news/" + published.getId() + "/publish");
    }

    @Test
    @Tag("Negative")
    @AllureId("API-NEWS-012")
    @DisplayName("Публикация новости в статусе ARCHIVED возвращает 409")
    void publishArchivedNewsReturnsConflict() {
        NewsResponse archived = createArchivedNews();

        ErrorResponse response = newsControllerClient.publishNews(archived.getId(), TestUser.ADMIN)
                .should(hasStatusCode(409))
                .as(ErrorResponse.class);

        ErrorSteps.assertError(response, 409, "/api/news/" + archived.getId() + "/publish");
    }

    @Test
    @Tag("Negative")
    @AllureId("API-AUTHZ-007")
    @DisplayName("Публикация новости под наблюдателем возвращает 403")
    void publishNewsAsViewerReturnsForbidden() {
        NewsResponse news = createDraftNews();

        ErrorResponse response = newsControllerClient.publishNews(news.getId(), TestUser.VIEWER)
                .should(hasStatusCode(403))
                .as(ErrorResponse.class);

        ErrorSteps.assertError(response, 403, "/api/news/" + news.getId() + "/publish");
    }
}
