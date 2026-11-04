package com.example.qanewsservice.autotests.ui.page.component;

import static com.codeborne.selenide.Selenide.$x;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.example.qanewsservice.autotests.api.models.NewsStatus;
import com.example.qanewsservice.autotests.ui.page.BasePage;
import com.example.qanewsservice.autotests.ui.page.NewsManagementPage;
import com.example.qanewsservice.autotests.ui.page.PageFactory;
import io.qameta.allure.Step;

public class NewsCardComponent extends BasePage {

    private final String title;

    public NewsCardComponent(String title) {
        this.title = title;
    }

    @Step("Проверить, что карточка новости отображается: {this.title}")
    public NewsCardComponent shouldBeVisible() {
        shouldBeVisible(card());
        return this;
    }

    @Step("Проверить статус карточки новости: {status}")
    public NewsCardComponent shouldHaveStatus(NewsStatus status) {
        statusLabel().shouldBe(Condition.visible, DEFAULT_TIMEOUT)
                .shouldHave(Condition.exactText(status.name()));
        return this;
    }

    @Step("Открыть детали новости: {this.title}")
    public NewsManagementPage clickView() {
        clickWhenEnabled(button("View"));
        return PageFactory.get(NewsManagementPage.class);
    }

    @Step("Нажать редактирование новости: {this.title}")
    public NewsManagementPage clickEdit() {
        clickWhenEnabled(button("Edit"));
        return PageFactory.get(NewsManagementPage.class);
    }

    @Step("Нажать публикацию новости: {this.title}")
    public NewsManagementPage clickPublish() {
        clickWhenEnabled(button("Publish"));
        return PageFactory.get(NewsManagementPage.class);
    }

    @Step("Нажать архивирование новости: {this.title}")
    public NewsManagementPage clickArchive() {
        clickWhenEnabled(button("Archive"));
        return PageFactory.get(NewsManagementPage.class);
    }

    @Step("Нажать удаление новости: {this.title}")
    public NewsManagementPage clickDelete() {
        clickWhenEnabled(button("Delete"));
        return PageFactory.get(NewsManagementPage.class);
    }

    @Step("Проверить, что кнопки действий администратора отсутствуют: {this.title}")
    public NewsCardComponent shouldNotHaveAdminActionButtons() {
        button("Edit").shouldNot(Condition.exist);
        button("Publish").shouldNot(Condition.exist);
        button("Archive").shouldNot(Condition.exist);
        button("Delete").shouldNot(Condition.exist);
        return this;
    }

    private SelenideElement card() {
        return $x("//article[contains(@class,'news-card') and .//h3[normalize-space()="
                + xpathLiteral(title) + "]]");
    }

    private SelenideElement statusLabel() {
        return card().$x(".//span[contains(@class,'status')]");
    }

    private SelenideElement button(String text) {
        return card().$x(".//button[normalize-space()=" + xpathLiteral(text) + "]");
    }
}
