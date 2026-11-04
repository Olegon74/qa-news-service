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
@Story("Архивирование новостей")
@Tag("API")
class ArchivingNewsTests extends BaseApiTest {

    @Test
    @Tag("SMOKE")
    @Tag("Positive")
    @AllureId("API-NEWS-013")
    @DisplayName("Архивирование новости в статусе PUBLISHED возвращает ARCHIVED и сохраняет статус")
    void archivePublishedNewsReturnsArchivedAndCanBeReceivedById() {
        NewsResponse published = createPublishedNews();

        NewsResponse archived = newsControllerClient.archiveNews(published.getId(), TestUser.ADMIN)
                .should(hasStatusCode(200))
                .as(NewsResponse.class);

        NewsSteps.validateRequiredFields(archived);
        NewsSteps.assertId(archived, published.getId());
        NewsSteps.assertStatus(archived, NewsStatus.ARCHIVED);

        NewsResponse receivedById = newsControllerClient.getNewsById(published.getId(), TestUser.ADMIN)
                .should(hasStatusCode(200))
                .as(NewsResponse.class);

        NewsSteps.validateRequiredFields(receivedById);
        NewsSteps.assertResponseMatchesSavedResponse(receivedById, archived);
    }

    @Test
    @Tag("Negative")
    @AllureId("API-NEWS-014")
    @DisplayName("Архивирование новости в статусе DRAFT возвращает 409")
    void archiveDraftNewsReturnsConflict() {
        NewsResponse draft = createDraftNews();

        ErrorResponse response = newsControllerClient.archiveNews(draft.getId(), TestUser.ADMIN)
                .should(hasStatusCode(409))
                .as(ErrorResponse.class);

        ErrorSteps.assertError(response, 409, "/api/news/" + draft.getId() + "/archive");
    }

    @Test
    @Tag("Negative")
    @AllureId("API-NEWS-015")
    @DisplayName("Архивирование новости в статусе ARCHIVED возвращает 409")
    void archiveArchivedNewsReturnsConflict() {
        NewsResponse archived = createArchivedNews();

        ErrorResponse response = newsControllerClient.archiveNews(archived.getId(), TestUser.ADMIN)
                .should(hasStatusCode(409))
                .as(ErrorResponse.class);

        ErrorSteps.assertError(response, 409, "/api/news/" + archived.getId() + "/archive");
    }

    @Test
    @Tag("Negative")
    @AllureId("API-AUTHZ-008")
    @DisplayName("Архивирование новости под наблюдателем возвращает 403")
    void archiveNewsAsViewerReturnsForbidden() {
        NewsResponse news = createPublishedNews();

        ErrorResponse response = newsControllerClient.archiveNews(news.getId(), TestUser.VIEWER)
                .should(hasStatusCode(403))
                .as(ErrorResponse.class);

        ErrorSteps.assertError(response, 403, "/api/news/" + news.getId() + "/archive");
    }
}
