package com.example.qanewsservice.autotests.ui.page;

import static com.codeborne.selenide.Selenide.$x;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.example.qanewsservice.autotests.api.models.NewsStatus;
import com.example.qanewsservice.autotests.ui.model.NewsUiModel;
import com.example.qanewsservice.autotests.ui.page.component.NewsCardComponent;
import io.qameta.allure.Step;

public class NewsManagementPage extends BasePage {

    private final SelenideElement currentUser = byTestId("current-user");
    private final SelenideElement currentRole = byTestId("current-role");
    private final SelenideElement logoutButton = byTestId("logout-button");
    private final SelenideElement newsForm = byTestId("news-form");
    private final SelenideElement titleInput = byTestId("title-input");
    private final SelenideElement previewTextInput = byTestId("preview-text-input");
    private final SelenideElement fullTextInput = byTestId("full-text-input");
    private final SelenideElement authorInput = byTestId("author-input");
    private final SelenideElement createNewsButton = byTestId("create-news-button");
    private final SelenideElement updateNewsButton = byTestId("update-news-button");
    private final SelenideElement statusFilter = byTestId("status-filter");
    private final SelenideElement newsList = byTestId("news-list");
    private final SelenideElement successMessage = byTestId("success-message");
    private final SelenideElement errorMessage = byTestId("error-message");
    private final SelenideElement viewerReadonlyMessage = byTestId("viewer-readonly-message");
    private final SelenideElement selectedNewsDetails = byTestId("selected-news-details");

    @Step("Проверить, что страница управления новостями открыта")
    public NewsManagementPage shouldBeOpened() {
        shouldBeVisible(currentUser);
        shouldBeVisible(newsList);
        return this;
    }

    @Step("Проверить текущего пользователя: {username}, роль: {role}")
    public NewsManagementPage shouldHaveCurrentUser(String username, String role) {
        currentUser.shouldHave(Condition.exactText(username), DEFAULT_TIMEOUT);
        currentRole.shouldHave(Condition.exactText(role), DEFAULT_TIMEOUT);
        return this;
    }

    @Step("Проверить, что форма создания новости отображается")
    public NewsManagementPage shouldHaveCreateForm() {
        shouldBeVisible(newsForm);
        return this;
    }

    @Step("Проверить, что форма создания новости отсутствует")
    public NewsManagementPage shouldNotHaveCreateForm() {
        newsForm.shouldNot(Condition.exist, DEFAULT_TIMEOUT);
        return this;
    }

    @Step("Проверить, что сообщение для наблюдателя отображается")
    public NewsManagementPage shouldHaveViewerReadonlyMessage() {
        viewerReadonlyMessage.shouldBe(Condition.visible, DEFAULT_TIMEOUT);
        return this;
    }

    @Step("Заполнить форму новости: {model.title}")
    public NewsManagementPage fillNewsForm(NewsUiModel model) {
        setValue(titleInput, model.getTitle());
        setValue(previewTextInput, model.getPreviewText());
        setValue(fullTextInput, model.getFullText());
        setValue(authorInput, model.getAuthor());
        return this;
    }

    @Step("Создать новость через UI")
    public NewsManagementPage createNews(NewsUiModel model) {
        fillNewsForm(model);
        clickWhenEnabled(createNewsButton);
        return this;
    }

    @Step("Обновить новость через UI")
    public NewsManagementPage updateNews(NewsUiModel model) {
        fillNewsForm(model);
        clickWhenEnabled(updateNewsButton);
        return this;
    }

    @Step("Отправить пустую форму создания новости")
    public NewsManagementPage submitCreateForm() {
        clickWhenEnabled(createNewsButton);
        return this;
    }

    @Step("Выбрать фильтр по статусу: {status}")
    public NewsManagementPage selectStatus(NewsStatus status) {
        statusFilter.shouldBe(Condition.visible, DEFAULT_TIMEOUT)
                .shouldBe(Condition.enabled, DEFAULT_TIMEOUT)
                .selectOptionByValue(status.name());
        return this;
    }

    @Step("Проверить, что новость отображается: {title}")
    public NewsManagementPage shouldHaveNews(String title) {
        newsCard(title).shouldBeVisible();
        return this;
    }

    @Step("Проверить, что новость отсутствует: {title}")
    public NewsManagementPage shouldNotHaveNews(String title) {
        newsCardElement(title).shouldNot(Condition.exist, DEFAULT_TIMEOUT);
        return this;
    }

    @Step("Получить компонент карточки новости: {title}")
    public NewsCardComponent newsCard(String title) {
        return new NewsCardComponent(title);
    }

    @Step("Проверить детали выбранной новости: {model.title}")
    public NewsManagementPage shouldHaveSelectedNewsDetails(NewsUiModel model, NewsStatus status) {
        selectedNewsDetails.shouldBe(Condition.visible, DEFAULT_TIMEOUT);
        selectedNewsDetails.shouldHave(Condition.text(model.getTitle()), DEFAULT_TIMEOUT);
        selectedNewsDetails.shouldHave(Condition.text(model.getPreviewText()), DEFAULT_TIMEOUT);
        selectedNewsDetails.shouldHave(Condition.text(model.getFullText()), DEFAULT_TIMEOUT);
        selectedNewsDetails.shouldHave(Condition.text(model.getAuthor()), DEFAULT_TIMEOUT);
        selectedNewsDetails.shouldHave(Condition.text(status.name()), DEFAULT_TIMEOUT);
        return this;
    }

    @Step("Проверить, что сообщение об успехе содержит текст: {text}")
    public NewsManagementPage shouldHaveSuccessMessage(String text) {
        successMessage.shouldBe(Condition.visible, DEFAULT_TIMEOUT)
                .shouldHave(Condition.text(text));
        return this;
    }

    @Step("Проверить, что сообщение валидации содержит текст: {text}")
    public NewsManagementPage shouldHaveValidationError(String text) {
        errorMessage.shouldBe(Condition.visible, DEFAULT_TIMEOUT)
                .shouldHave(Condition.text(text));
        return this;
    }

    @Step("Выйти из аккаунта")
    public AuthPage logout() {
        clickWhenEnabled(logoutButton);
        return PageFactory.get(AuthPage.class);
    }

    private void setValue(SelenideElement element, String value) {
        element.shouldBe(Condition.visible, DEFAULT_TIMEOUT)
                .shouldBe(Condition.enabled, DEFAULT_TIMEOUT)
                .clear();
        if (value != null && !value.isEmpty()) {
            element.setValue(value);
        }
    }

    private SelenideElement newsCardElement(String title) {
        return $x("//article[contains(@class,'news-card') and .//h3[normalize-space()="
                + xpathLiteral(title) + "]]");
    }
}
