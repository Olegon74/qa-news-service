package com.example.qanewsservice.autotests.api.steps;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.example.qanewsservice.autotests.api.models.NewsRequest;
import com.example.qanewsservice.autotests.api.models.NewsStatus;
import com.example.qanewsservice.autotests.api.response.NewsResponse;
import io.qameta.allure.Step;
import java.util.List;

public final class NewsSteps {

    private NewsSteps() {
    }

    @Step("Проверить обязательные поля ответа новости")
    public static void validateRequiredFields(NewsResponse response) {
        assertSoftly(softly -> {
            softly.assertThat(response.getId()).isNotNull();
            softly.assertThat(response.getTitle()).isNotBlank();
            softly.assertThat(response.getPreviewText()).isNotBlank();
            softly.assertThat(response.getFullText()).isNotBlank();
            softly.assertThat(response.getAuthor()).isNotBlank();
            softly.assertThat(response.getStatus()).isNotNull();
            softly.assertThat(response.getCreatedAt()).isNotBlank();
            softly.assertThat(response.getUpdatedAt()).isNotBlank();
        });
    }

    @Step("Проверить соответствие новости запросу")
    public static void assertResponseMatchesRequest(NewsResponse response, NewsRequest request) {
        assertSoftly(softly -> {
            softly.assertThat(response.getTitle()).isEqualTo(request.getTitle());
            softly.assertThat(response.getPreviewText()).isEqualTo(request.getPreviewText());
            softly.assertThat(response.getFullText()).isEqualTo(request.getFullText());
            softly.assertThat(response.getAuthor()).isEqualTo(request.getAuthor());
        });
    }

    @Step("Проверить соответствие полученной новости сохраненной новости id={expected.id}")
    public static void assertResponseMatchesSavedResponse(NewsResponse actual, NewsResponse expected) {
        assertSoftly(softly -> {
            softly.assertThat(actual.getId()).isEqualTo(expected.getId());
            softly.assertThat(actual.getTitle()).isEqualTo(expected.getTitle());
            softly.assertThat(actual.getPreviewText()).isEqualTo(expected.getPreviewText());
            softly.assertThat(actual.getFullText()).isEqualTo(expected.getFullText());
            softly.assertThat(actual.getAuthor()).isEqualTo(expected.getAuthor());
            softly.assertThat(actual.getStatus()).isEqualTo(expected.getStatus());
            assertOptionalTimestampState(softly, actual.getPublishedAt(), expected.getPublishedAt(), "publishedAt");
            assertOptionalTimestampState(softly, actual.getArchivedAt(), expected.getArchivedAt(), "archivedAt");
        });
    }

    @Step("Проверить статус новости: {expectedStatus}")
    public static void assertStatus(NewsResponse response, NewsStatus expectedStatus) {
        assertThat(response.getStatus()).isEqualTo(expectedStatus);
    }

    @Step("Проверить id новости: {expectedId}")
    public static void assertId(NewsResponse response, Long expectedId) {
        assertThat(response.getId()).isEqualTo(expectedId);
    }

    @Step("Проверить, что созданная новость в статусе DRAFT")
    public static void assertCreatedDraft(NewsResponse response, NewsRequest request) {
        validateRequiredFields(response);
        assertResponseMatchesRequest(response, request);
        assertStatus(response, NewsStatus.DRAFT);
        assertThat(response.getPublishedAt()).isNull();
        assertThat(response.getArchivedAt()).isNull();
    }

    @Step("Проверить, что список содержит новость id={expected.id}")
    public static void assertListContains(List<NewsResponse> responses, NewsResponse expected) {
        NewsResponse actual = responses.stream()
                .filter(item -> expected.getId().equals(item.getId()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Новость отсутствует в списке: " + expected.getId()));

        assertResponseMatchesSavedResponse(actual, expected);
    }

    @Step("Проверить, что список содержит новость id={expected.id} со статусом {expectedStatus}")
    public static void assertListContainsWithStatus(
            List<NewsResponse> responses,
            NewsResponse expected,
            NewsStatus expectedStatus) {
        assertListContains(responses, expected);
        NewsResponse actual = responses.stream()
                .filter(item -> expected.getId().equals(item.getId()))
                .findFirst()
                .orElseThrow();
        assertStatus(actual, expectedStatus);
    }

    @Step("Проверить, что список не содержит новость id={id}")
    public static void assertListDoesNotContain(List<NewsResponse> responses, Long id) {
        assertThat(responses)
                .extracting(NewsResponse::getId)
                .doesNotContain(id);
    }

    private static void assertOptionalTimestampState(
            org.assertj.core.api.SoftAssertions softly,
            String actual,
            String expected,
            String fieldName) {
        if (expected == null) {
            softly.assertThat(actual).as(fieldName).isNull();
        } else {
            softly.assertThat(actual).as(fieldName).isNotBlank();
        }
    }
}
