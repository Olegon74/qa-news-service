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
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Epic("QA News Service")
@Feature("API")
@Story("Получение новостей")
@Tag("API")
class GettingNewsTests extends BaseApiTest {

    @Test
    @Tag("SMOKE")
    @Tag("Positive")
    @AllureId("API-NEWS-002")
    @DisplayName("Получение новости по id возвращает созданную новость")
    void getNewsByIdReturnsCreatedNews() {
        NewsRequest request = NewsRequestBuilder.buildRequiredNewsRequest();
        NewsResponse created = createDraftNews(request);

        NewsResponse response = newsControllerClient.getNewsById(created.getId(), TestUser.ADMIN)
                .should(hasStatusCode(200))
                .as(NewsResponse.class);

        NewsSteps.validateRequiredFields(response);
        NewsSteps.assertResponseMatchesRequest(response, request);
        NewsSteps.assertResponseMatchesSavedResponse(response, created);
    }

    @Test
    @Tag("Positive")
    @AllureId("API-NEWS-003")
    @DisplayName("Список всех новостей содержит созданную новость")
    void getAllNewsContainsCreatedNews() {
        NewsResponse created = createDraftNews();

        List<NewsResponse> response = getNewsList();

        NewsSteps.assertListContains(response, created);
    }

    @Test
    @Tag("Positive")
    @AllureId("API-NEWS-004")
    @DisplayName("Фильтрация новостей по статусу DRAFT")
    void filterNewsByDraftStatus() {
        NewsResponse draft = createDraftNews();

        List<NewsResponse> response = newsControllerClient.getNewsByStatus(NewsStatus.DRAFT, TestUser.ADMIN)
                .should(hasStatusCode(200))
                .asList(NewsResponse.class);

        NewsSteps.assertListContainsWithStatus(response, draft, NewsStatus.DRAFT);
    }

    @Test
    @Tag("Positive")
    @AllureId("API-NEWS-005")
    @DisplayName("Фильтрация новостей по статусу PUBLISHED")
    void filterNewsByPublishedStatus() {
        NewsResponse published = createPublishedNews();

        List<NewsResponse> response = newsControllerClient.getNewsByStatus(NewsStatus.PUBLISHED, TestUser.ADMIN)
                .should(hasStatusCode(200))
                .asList(NewsResponse.class);

        NewsSteps.assertListContainsWithStatus(response, published, NewsStatus.PUBLISHED);
    }

    @Test
    @Tag("Positive")
    @AllureId("API-NEWS-006")
    @DisplayName("Фильтрация новостей по статусу ARCHIVED")
    void filterNewsByArchivedStatus() {
        NewsResponse archived = createArchivedNews();

        List<NewsResponse> response = newsControllerClient.getNewsByStatus(NewsStatus.ARCHIVED, TestUser.ADMIN)
                .should(hasStatusCode(200))
                .asList(NewsResponse.class);

        NewsSteps.assertListContainsWithStatus(response, archived, NewsStatus.ARCHIVED);
    }

    @Test
    @Tag("Negative")
    @AllureId("API-NEWS-019")
    @DisplayName("Получение несуществующей новости по id возвращает 404")
    void getUnknownNewsByIdReturnsNotFound() {
        long unknownId = 999999999L;

        ErrorResponse response = newsControllerClient.getNewsById(unknownId, TestUser.ADMIN)
                .should(hasStatusCode(404))
                .as(ErrorResponse.class);

        ErrorSteps.assertError(response, 404, "/api/news/" + unknownId);
    }

    @Test
    @Tag("Negative")
    @AllureId("API-NEWS-020")
    @DisplayName("Фильтрация новостей по невалидному статусу возвращает 400")
    void filterNewsByInvalidStatusReturnsBadRequest() {
        ErrorResponse response = newsControllerClient.getNewsByStatus("UNKNOWN", TestUser.ADMIN)
                .should(hasStatusCode(400))
                .as(ErrorResponse.class);

        ErrorSteps.assertError(response, 400, "/api/news");
    }

    @Test
    @Tag("Negative")
    @AllureId("API-AUTHZ-001")
    @DisplayName("GET /api/news без токена возвращает 401")
    void getAllNewsWithoutTokenReturnsUnauthorized() {
        ErrorResponse response = newsControllerClient.getAllNewsWithoutToken()
                .should(hasStatusCode(401))
                .as(ErrorResponse.class);

        ErrorSteps.assertError(response, 401, "/api/news");
    }

    @Test
    @Tag("Negative")
    @AllureId("API-AUTHZ-002")
    @DisplayName("GET /api/news/{id} без токена возвращает 401")
    void getNewsByIdWithoutTokenReturnsUnauthorized() {
        ErrorResponse response = newsControllerClient.getNewsByIdWithoutToken(1L)
                .should(hasStatusCode(401))
                .as(ErrorResponse.class);

        ErrorSteps.assertError(response, 401, "/api/news/1");
    }
}
