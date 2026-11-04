package com.example.qanewsservice.autotests.api.tests;

import static com.example.qanewsservice.autotests.api.assertions.Conditions.hasStatusCode;

import com.example.qanewsservice.autotests.api.clients.BaseApiTest;
import com.example.qanewsservice.autotests.api.models.NewsRequest;
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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@Epic("QA News Service")
@Feature("API")
@Story("Создание новостей")
@Tag("API")
class CreatingNewsTests extends BaseApiTest {

    @Test
    @Tag("SMOKE")
    @Tag("Positive")
    @AllureId("API-NEWS-001")
    @DisplayName("Создание новости возвращает DRAFT и новость доступна по id")
    void createNewsReturnsDraftAndCanBeReceivedById() {
        NewsRequest request = NewsRequestBuilder.buildRequiredNewsRequest();

        NewsResponse created = newsControllerClient.createNews(request, TestUser.ADMIN)
                .should(hasStatusCode(201))
                .as(NewsResponse.class);
        trackNews(created);

        NewsSteps.assertCreatedDraft(created, request);

        NewsResponse receivedById = newsControllerClient.getNewsById(created.getId(), TestUser.ADMIN)
                .should(hasStatusCode(200))
                .as(NewsResponse.class);

        NewsSteps.validateRequiredFields(receivedById);
        NewsSteps.assertResponseMatchesSavedResponse(receivedById, created);
    }

    @Test
    @Tag("SMOKE")
    @Tag("Positive")
    @AllureId("API-AUTHZ-004")
    @DisplayName("Создание новости под администратором возвращает 201 и новость есть в списке")
    void createNewsAsAdminReturnsCreatedAndCanBeReceivedFromList() {
        NewsRequest request = NewsRequestBuilder.buildRequiredNewsRequest();

        NewsResponse created = newsControllerClient.createNews(request, TestUser.ADMIN)
                .should(hasStatusCode(201))
                .as(NewsResponse.class);
        trackNews(created);

        NewsSteps.assertCreatedDraft(created, request);

        NewsResponse receivedFromList = getNewsFromList(created.getId());

        NewsSteps.validateRequiredFields(receivedFromList);
        NewsSteps.assertResponseMatchesSavedResponse(receivedFromList, created);
    }

    @Test
    @Tag("Negative")
    @AllureId("API-AUTHZ-003")
    @DisplayName("Создание новости под наблюдателем возвращает 403")
    void createNewsAsViewerReturnsForbidden() {
        ErrorResponse response = newsControllerClient
                .createNews(NewsRequestBuilder.buildRequiredNewsRequest(), TestUser.VIEWER)
                .should(hasStatusCode(403))
                .as(ErrorResponse.class);

        ErrorSteps.assertError(response, 403, "/api/news");
    }

    @ParameterizedTest(name = "{0}")
    @Tag("Negative")
    @AllureId("API-VALIDATION-001")
    @MethodSource("com.example.qanewsservice.autotests.api.provider.NewsValidationProvider#invalidNewsRequests")
    @DisplayName("Создание новости с невалидным телом возвращает 400")
    void createNewsWithInvalidBodyReturnsBadRequest(String caseName, NewsRequest request, String expectedField) {
        ErrorResponse response = newsControllerClient.createNews(request, TestUser.ADMIN)
                .should(hasStatusCode(400))
                .as(ErrorResponse.class);

        ErrorSteps.assertValidationField(response, expectedField);
    }
}
