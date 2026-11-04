package com.example.qanewsservice.autotests.api.clients;

import static com.example.qanewsservice.autotests.api.assertions.Conditions.hasStatusCode;

import com.example.qanewsservice.autotests.api.models.NewsRequest;
import com.example.qanewsservice.autotests.api.requests.NewsRequestBuilder;
import com.example.qanewsservice.autotests.api.response.NewsResponse;
import com.example.qanewsservice.autotests.config.TestUser;
import com.example.qanewsservice.autotests.db.DbExtension;
import com.example.qanewsservice.autotests.db.DbManager;
import com.example.qanewsservice.autotests.db.NewsCleanupHelper;
import io.qameta.allure.Step;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.opentest4j.TestAbortedException;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith({DbExtension.class})
public class BaseApiTest {

    protected AuthControllerClient authControllerClient;
    protected NewsControllerClient newsControllerClient;

    private final List<Long> createdNewsIds = new ArrayList<>();

    @BeforeAll
    protected void setUpApiClients() {
        authControllerClient = new AuthControllerClient();
        newsControllerClient = new NewsControllerClient();
    }

    @AfterEach
    @Step("Очистить новости, созданные API-тестом")
    protected void cleanupCreatedNews() {
        if (!DbManager.isInitialized()) {
            throw new TestAbortedException("API-тест прерван: БД недоступна для очистки новостей");
        }

        List<Long> ids = new ArrayList<>(createdNewsIds);
        Collections.reverse(ids);
        try {
            NewsCleanupHelper.cleanupNewsByIds(ids);
        } catch (SQLException exception) {
            System.err.println("Не удалось выполнить очистку новостей после API-теста: "
                    + exception.getMessage());
            throw new TestAbortedException("API-тест прерван: не удалось выполнить очистку новостей", exception);
        } finally {
            createdNewsIds.clear();
        }
    }

    @Step("Создать черновик новости для теста")
    protected NewsResponse createDraftNews() {
        return createDraftNews(NewsRequestBuilder.buildRequiredNewsRequest());
    }

    @Step("Создать черновик новости с указанными данными")
    protected NewsResponse createDraftNews(NewsRequest request) {
        NewsResponse response = newsControllerClient.createNews(request, TestUser.ADMIN)
                .should(hasStatusCode(201))
                .as(NewsResponse.class);
        trackNews(response);
        return response;
    }

    @Step("Создать опубликованную новость для теста")
    protected NewsResponse createPublishedNews() {
        NewsResponse draft = createDraftNews();
        return newsControllerClient.publishNews(draft.getId(), TestUser.ADMIN)
                .should(hasStatusCode(200))
                .as(NewsResponse.class);
    }

    @Step("Создать архивную новость для теста")
    protected NewsResponse createArchivedNews() {
        NewsResponse published = createPublishedNews();
        return newsControllerClient.archiveNews(published.getId(), TestUser.ADMIN)
                .should(hasStatusCode(200))
                .as(NewsResponse.class);
    }

    @Step("Получить список новостей через API")
    protected List<NewsResponse> getNewsList() {
        return newsControllerClient.getAllNews(TestUser.ADMIN)
                .should(hasStatusCode(200))
                .asList(NewsResponse.class);
    }

    @Step("Найти новость id={id} в списке новостей")
    protected NewsResponse getNewsFromList(Long id) {
        return getNewsList().stream()
                .filter(news -> id.equals(news.getId()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Новость с id не найдена в списке: " + id));
    }

    protected void trackNews(NewsResponse response) {
        if (response != null && response.getId() != null && !createdNewsIds.contains(response.getId())) {
            createdNewsIds.add(response.getId());
        }
    }

}
