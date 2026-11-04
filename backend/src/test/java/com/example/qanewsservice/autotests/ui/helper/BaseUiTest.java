package com.example.qanewsservice.autotests.ui.helper;

import static com.example.qanewsservice.autotests.api.assertions.Conditions.hasStatusCode;

import com.example.qanewsservice.autotests.api.clients.NewsControllerClient;
import com.example.qanewsservice.autotests.api.models.NewsRequest;
import com.example.qanewsservice.autotests.api.response.NewsResponse;
import com.example.qanewsservice.autotests.config.TestUser;
import com.example.qanewsservice.autotests.db.DbExtension;
import com.example.qanewsservice.autotests.db.DbManager;
import com.example.qanewsservice.autotests.db.NewsCleanupHelper;
import com.example.qanewsservice.autotests.generation.NewsDataGenerator;
import com.example.qanewsservice.autotests.ui.config.BaseConfiguration;
import com.example.qanewsservice.autotests.ui.model.NewsUiModel;
import com.example.qanewsservice.autotests.ui.page.AuthPage;
import com.example.qanewsservice.autotests.ui.page.PageFactory;
import io.qameta.allure.Step;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.opentest4j.TestAbortedException;

@ExtendWith({DbExtension.class})
public class BaseUiTest extends BaseConfiguration {

    private static final String GENERATED_TEST_TITLE_PREFIX = "TEST News ";

    protected AuthPage authPage;
    protected NewsControllerClient newsControllerClient;
    protected NewsUiModel newsModel;

    private final List<Long> createdNewsIds = new ArrayList<>();

    @BeforeEach
    @Step("Подготовить UI-тест")
    protected void prepareUiTest() {
        authPage = PageFactory.get(AuthPage.class);
        newsControllerClient = new NewsControllerClient();
        newsModel = generateNewsUiModel();
    }

    @AfterEach
    @Step("Очистить новости, созданные UI-тестом")
    protected void cleanupCreatedNews() {
        if (!DbManager.isInitialized()) {
            throw new TestAbortedException("UI-тест прерван: БД недоступна для очистки новостей");
        }

        List<Long> ids = new ArrayList<>(createdNewsIds);
        Collections.reverse(ids);
        try {
            NewsCleanupHelper.cleanupNewsByIds(ids);
            cleanupNewsModelByTitle();
        } catch (SQLException exception) {
            System.err.println("Не удалось выполнить очистку новостей после UI-теста: "
                    + exception.getMessage());
            throw new TestAbortedException("UI-тест прерван: не удалось выполнить очистку новостей", exception);
        } finally {
            createdNewsIds.clear();
        }
    }

    protected NewsUiModel generateNewsUiModel() {
        return NewsUiModel.builder()
                .title(NewsDataGenerator.generateTitle())
                .previewText(NewsDataGenerator.generatePreviewText())
                .fullText(NewsDataGenerator.generateFullText())
                .author(NewsDataGenerator.generateAuthor())
                .build();
    }

    private void cleanupNewsModelByTitle() throws SQLException {
        if (newsModel != null && isGeneratedTestTitle(newsModel.getTitle())) {
            NewsCleanupHelper.cleanupNewsByTitle(newsModel.getTitle());
        }
    }

    private boolean isGeneratedTestTitle(String title) {
        return title != null && title.startsWith(GENERATED_TEST_TITLE_PREFIX);
    }

    protected NewsRequest toNewsRequest(NewsUiModel model) {
        return NewsRequest.builder()
                .title(model.getTitle())
                .previewText(model.getPreviewText())
                .fullText(model.getFullText())
                .author(model.getAuthor())
                .build();
    }

    @Step("Создать черновик новости через API для UI-теста")
    protected NewsResponse createDraftNewsThroughApi(NewsUiModel model) {
        NewsResponse response = newsControllerClient.createNews(toNewsRequest(model), TestUser.ADMIN)
                .should(hasStatusCode(201))
                .as(NewsResponse.class);
        trackNews(response);
        return response;
    }

    @Step("Создать опубликованную новость через API для UI-теста")
    protected NewsResponse createPublishedNewsThroughApi(NewsUiModel model) {
        NewsResponse draft = createDraftNewsThroughApi(model);
        return newsControllerClient.publishNews(draft.getId(), TestUser.ADMIN)
                .should(hasStatusCode(200))
                .as(NewsResponse.class);
    }

    @Step("Создать архивную новость через API для UI-теста")
    protected NewsResponse createArchivedNewsThroughApi(NewsUiModel model) {
        NewsResponse published = createPublishedNewsThroughApi(model);
        return newsControllerClient.archiveNews(published.getId(), TestUser.ADMIN)
                .should(hasStatusCode(200))
                .as(NewsResponse.class);
    }

    @Step("Запомнить новость, созданную через UI: {title}")
    protected void trackNewsCreatedThroughUi(String title) {
        newsControllerClient.getAllNews(TestUser.ADMIN)
                .should(hasStatusCode(200))
                .asList(NewsResponse.class)
                .stream()
                .filter(news -> title.equals(news.getTitle()))
                .findFirst()
                .ifPresent(this::trackNews);
    }

    protected void trackNews(NewsResponse response) {
        if (response != null && response.getId() != null && !createdNewsIds.contains(response.getId())) {
            createdNewsIds.add(response.getId());
        }
    }

}
