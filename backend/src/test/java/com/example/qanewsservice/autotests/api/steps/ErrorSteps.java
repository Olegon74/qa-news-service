package com.example.qanewsservice.autotests.api.steps;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.example.qanewsservice.autotests.api.response.ErrorResponse;
import io.qameta.allure.Step;

public final class ErrorSteps {

    private ErrorSteps() {
    }

    @Step("Проверить ответ с ошибкой: статус={expectedStatus}, path={expectedPath}")
    public static void assertError(ErrorResponse response, int expectedStatus, String expectedPath) {
        assertSoftly(softly -> {
            softly.assertThat(response.getStatus()).isEqualTo(expectedStatus);
            softly.assertThat(response.getPath()).isEqualTo(expectedPath);
            softly.assertThat(response.getMessage()).isNotBlank();
            softly.assertThat(response.getError()).isNotBlank();
        });
    }

    @Step("Проверить поле ошибки валидации: {field}")
    public static void assertValidationField(ErrorResponse response, String field) {
        assertThat(response.getStatus()).isEqualTo(400);
        assertThat(response.getValidationErrors()).containsKey(field);
    }
}
