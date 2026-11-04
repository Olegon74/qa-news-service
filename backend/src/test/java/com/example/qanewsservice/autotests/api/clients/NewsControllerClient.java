package com.example.qanewsservice.autotests.api.clients;

import com.example.qanewsservice.autotests.api.assertions.AssertableResponse;
import com.example.qanewsservice.autotests.api.models.NewsRequest;
import com.example.qanewsservice.autotests.api.models.NewsStatus;
import com.example.qanewsservice.autotests.config.TestUser;
import io.qameta.allure.Step;

public class NewsControllerClient extends BaseClient {

    @Step("Создать новость пользователем {user}")
    public AssertableResponse createNews(NewsRequest request, TestUser user) {
        return new AssertableResponse(requestSpec(user)
                .body(request)
                .post("/api/news")
                .then());
    }

    @Step("Создать новость без токена")
    public AssertableResponse createNewsWithoutToken(NewsRequest request) {
        return new AssertableResponse(requestSpec()
                .body(request)
                .post("/api/news")
                .then());
    }

    @Step("Получить новость по id={id} пользователем {user}")
    public AssertableResponse getNewsById(Long id, TestUser user) {
        return new AssertableResponse(requestSpec(user)
                .get("/api/news/{id}", id)
                .then());
    }

    @Step("Получить новость по id={id} без токена")
    public AssertableResponse getNewsByIdWithoutToken(Long id) {
        return new AssertableResponse(requestSpec()
                .get("/api/news/{id}", id)
                .then());
    }

    @Step("Получить все новости пользователем {user}")
    public AssertableResponse getAllNews(TestUser user) {
        return new AssertableResponse(requestSpec(user)
                .get("/api/news")
                .then());
    }

    @Step("Получить все новости без токена")
    public AssertableResponse getAllNewsWithoutToken() {
        return new AssertableResponse(requestSpec()
                .get("/api/news")
                .then());
    }

    @Step("Получить новости со статусом {status} пользователем {user}")
    public AssertableResponse getNewsByStatus(NewsStatus status, TestUser user) {
        return getNewsByStatus(status.name(), user);
    }

    @Step("Получить новости по строковому статусу {status} пользователем {user}")
    public AssertableResponse getNewsByStatus(String status, TestUser user) {
        return new AssertableResponse(requestSpec(user)
                .queryParam("status", status)
                .get("/api/news")
                .then());
    }

    @Step("Обновить новость id={id} пользователем {user}")
    public AssertableResponse updateNews(Long id, NewsRequest request, TestUser user) {
        return new AssertableResponse(requestSpec(user)
                .body(request)
                .put("/api/news/{id}", id)
                .then());
    }

    @Step("Опубликовать новость id={id} пользователем {user}")
    public AssertableResponse publishNews(Long id, TestUser user) {
        return new AssertableResponse(requestSpec(user)
                .post("/api/news/{id}/publish", id)
                .then());
    }

    @Step("Архивировать новость id={id} пользователем {user}")
    public AssertableResponse archiveNews(Long id, TestUser user) {
        return new AssertableResponse(requestSpec(user)
                .post("/api/news/{id}/archive", id)
                .then());
    }

    @Step("Удалить новость id={id} пользователем {user}")
    public AssertableResponse deleteNews(Long id, TestUser user) {
        return new AssertableResponse(requestSpec(user)
                .delete("/api/news/{id}", id)
                .then());
    }
}
