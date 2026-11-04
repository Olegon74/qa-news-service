package com.example.qanewsservice.autotests.api.tests;

import static com.example.qanewsservice.autotests.api.assertions.Conditions.hasStatusCode;

import com.example.qanewsservice.autotests.api.clients.BaseApiTest;
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
@Story("Удаление новостей")
@Tag("API")
class DeletingNewsTests extends BaseApiTest {

    @Test
    @Tag("Positive")
    @AllureId("API-NEWS-016")
    @DisplayName("Удаление новости в статусе DRAFT возвращает 204 и удаляет новость")
    void deleteDraftNewsReturnsNoContentAndRemovesNews() {
        NewsResponse draft = createDraftNews();

        newsControllerClient.deleteNews(draft.getId(), TestUser.ADMIN)
                .should(hasStatusCode(204));

        newsControllerClient.getNewsById(draft.getId(), TestUser.ADMIN)
                .should(hasStatusCode(404));
        NewsSteps.assertListDoesNotContain(getNewsList(), draft.getId());
    }

    @Test
    @Tag("Positive")
    @AllureId("API-NEWS-017")
    @DisplayName("Удаление новости в статусе ARCHIVED возвращает 204 и удаляет новость")
    void deleteArchivedNewsReturnsNoContentAndRemovesNews() {
        NewsResponse archived = createArchivedNews();

        newsControllerClient.deleteNews(archived.getId(), TestUser.ADMIN)
                .should(hasStatusCode(204));

        newsControllerClient.getNewsById(archived.getId(), TestUser.ADMIN)
                .should(hasStatusCode(404));
        NewsSteps.assertListDoesNotContain(getNewsList(), archived.getId());
    }

    @Test
    @Tag("Negative")
    @AllureId("API-NEWS-018")
    @DisplayName("Удаление новости в статусе PUBLISHED возвращает 409")
    void deletePublishedNewsReturnsConflict() {
        NewsResponse published = createPublishedNews();

        ErrorResponse response = newsControllerClient.deleteNews(published.getId(), TestUser.ADMIN)
                .should(hasStatusCode(409))
                .as(ErrorResponse.class);

        ErrorSteps.assertError(response, 409, "/api/news/" + published.getId());
    }

    @Test
    @Tag("Negative")
    @AllureId("API-AUTHZ-005")
    @DisplayName("DELETE /api/news/{id} под наблюдателем возвращает 403")
    void deleteNewsAsViewerReturnsForbidden() {
        NewsResponse news = createDraftNews();

        ErrorResponse response = newsControllerClient.deleteNews(news.getId(), TestUser.VIEWER)
                .should(hasStatusCode(403))
                .as(ErrorResponse.class);

        ErrorSteps.assertError(response, 403, "/api/news/" + news.getId());
    }
}
