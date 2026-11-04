package com.example.qanewsservice.autotests.ui.page;

import static com.codeborne.selenide.Selenide.page;

public final class PageFactory {

    private PageFactory() {
    }

    public static <T> T get(Class<T> pageClass) {
        return page(pageClass);
    }
}
