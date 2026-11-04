package com.example.qanewsservice.autotests.ui.page;

import static com.codeborne.selenide.Selenide.$;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import java.time.Duration;

public class BasePage {

    protected static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(15);

    protected SelenideElement byTestId(String testId) {
        return $("[data-testid='" + testId + "']");
    }

    protected void clickWhenEnabled(SelenideElement element) {
        element.shouldBe(Condition.exist, DEFAULT_TIMEOUT)
                .shouldBe(Condition.visible, DEFAULT_TIMEOUT)
                .shouldBe(Condition.enabled, DEFAULT_TIMEOUT)
                .scrollIntoView(true)
                .click();
    }

    protected void shouldBeVisible(SelenideElement element) {
        element.shouldBe(Condition.exist, DEFAULT_TIMEOUT)
                .shouldBe(Condition.visible, DEFAULT_TIMEOUT);
    }

    protected String xpathLiteral(String value) {
        if (!value.contains("'")) {
            return "'" + value + "'";
        }
        if (!value.contains("\"")) {
            return "\"" + value + "\"";
        }

        StringBuilder concat = new StringBuilder("concat(");
        String[] parts = value.split("'");
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) {
                concat.append(", \"'\", ");
            }
            concat.append("'").append(parts[i]).append("'");
        }
        return concat.append(")").toString();
    }
}
